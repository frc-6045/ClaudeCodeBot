package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;

/**
 * Swerve drive subsystem using REV MAXSwerve modules
 * 4 modules with NEO Vortex drive motors and NEO 550 turning motors
 * NavX gyroscope for field-oriented control
 */
public class SwerveDriveSubsystem extends SubsystemBase {
  // Create swerve modules
  private final SwerveModule m_frontLeft = new SwerveModule(
      DriveConstants.kFrontLeftDriveMotorId,
      DriveConstants.kFrontLeftTurningMotorId,
      DriveConstants.kFrontLeftDriveInverted,
      DriveConstants.kFrontLeftTurningInverted,
      DriveConstants.kFrontLeftEncoderOffset);

  private final SwerveModule m_frontRight = new SwerveModule(
      DriveConstants.kFrontRightDriveMotorId,
      DriveConstants.kFrontRightTurningMotorId,
      DriveConstants.kFrontRightDriveInverted,
      DriveConstants.kFrontRightTurningInverted,
      DriveConstants.kFrontRightEncoderOffset);

  private final SwerveModule m_backLeft = new SwerveModule(
      DriveConstants.kBackLeftDriveMotorId,
      DriveConstants.kBackLeftTurningMotorId,
      DriveConstants.kBackLeftDriveInverted,
      DriveConstants.kBackLeftTurningInverted,
      DriveConstants.kBackLeftEncoderOffset);

  private final SwerveModule m_backRight = new SwerveModule(
      DriveConstants.kBackRightDriveMotorId,
      DriveConstants.kBackRightTurningMotorId,
      DriveConstants.kBackRightDriveInverted,
      DriveConstants.kBackRightTurningInverted,
      DriveConstants.kBackRightEncoderOffset);

  // NavX gyroscope
  private final AHRS m_gyro = new AHRS(SPI.Port.kMXP);

  // Slew rate limiters to make joystick inputs smoother and prevent tipping
  private final SlewRateLimiter m_xLimiter = new SlewRateLimiter(DriveConstants.kMagnitudeSlewRate);
  private final SlewRateLimiter m_yLimiter = new SlewRateLimiter(DriveConstants.kMagnitudeSlewRate);
  private final SlewRateLimiter m_rotLimiter = new SlewRateLimiter(DriveConstants.kRotationalSlewRate);

  // Swerve drive kinematics (module positions relative to robot center)
  private final SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(
      // Front left
      new edu.wpi.first.math.geometry.Translation2d(
          DriveConstants.kWheelBaseMeters / 2,
          DriveConstants.kTrackWidthMeters / 2),
      // Front right
      new edu.wpi.first.math.geometry.Translation2d(
          DriveConstants.kWheelBaseMeters / 2,
          -DriveConstants.kTrackWidthMeters / 2),
      // Back left
      new edu.wpi.first.math.geometry.Translation2d(
          -DriveConstants.kWheelBaseMeters / 2,
          DriveConstants.kTrackWidthMeters / 2),
      // Back right
      new edu.wpi.first.math.geometry.Translation2d(
          -DriveConstants.kWheelBaseMeters / 2,
          -DriveConstants.kTrackWidthMeters / 2));

  // Odometry for tracking robot position
  private final SwerveDriveOdometry m_odometry = new SwerveDriveOdometry(
      m_kinematics,
      Rotation2d.fromDegrees(-m_gyro.getAngle()),
      getModulePositions());

  /** Creates a new SwerveDriveSubsystem. */
  public SwerveDriveSubsystem() {
    // Calibrate gyro on boot (robot must be stationary!)
    new Thread(() -> {
      try {
        Thread.sleep(1000);
        zeroHeading();
        System.out.println("✓ Gyro calibration complete");
      } catch (Exception e) {
        System.err.println("⚠️ ERROR: Gyro calibration failed: " + e.getMessage());
        System.err.println("   Field-oriented drive may not work correctly!");
        e.printStackTrace();
      }
    }).start();
  }

  @Override
  public void periodic() {
    // Update odometry
    m_odometry.update(
        Rotation2d.fromDegrees(-m_gyro.getAngle()),
        getModulePositions());

    // Telemetry - publish to SmartDashboard for debugging
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber("Swerve/Gyro Angle", getHeading());
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber("Swerve/Robot X", getPose().getX());
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber("Swerve/Robot Y", getPose().getY());
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putBoolean("Swerve/Gyro Connected", m_gyro.isConnected());
  }

  /**
   * Returns the currently-estimated pose of the robot
   *
   * @return The pose
   */
  public Pose2d getPose() {
    return m_odometry.getPoseMeters();
  }

  /**
   * Resets the odometry to the specified pose
   *
   * @param pose The pose to which to set the odometry
   */
  public void resetOdometry(Pose2d pose) {
    m_odometry.resetPosition(
        Rotation2d.fromDegrees(-m_gyro.getAngle()),
        getModulePositions(),
        pose);
  }

  /**
   * Method to drive the robot using joystick info
   * Field-relative mode: Robot moves relative to field orientation
   * Robot-relative mode: Robot moves relative to its own orientation
   *
   * @param xSpeed Speed of the robot in the x direction (forward/backward) in m/s
   * @param ySpeed Speed of the robot in the y direction (left/right) in m/s
   * @param rot Angular rate of the robot in rad/s
   * @param fieldRelative Whether the provided x and y speeds are relative to the field
   */
  public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
    // Apply slew rate limiters for smoother control
    double xSpeedFiltered = m_xLimiter.calculate(xSpeed) * DriveConstants.kMaxSpeedMetersPerSecond;
    double ySpeedFiltered = m_yLimiter.calculate(ySpeed) * DriveConstants.kMaxSpeedMetersPerSecond;
    double rotFiltered = m_rotLimiter.calculate(rot) * DriveConstants.kMaxAngularSpeed;

    // Convert chassis speeds to module states
    SwerveModuleState[] swerveModuleStates =
        m_kinematics.toSwerveModuleStates(
            fieldRelative
                ? ChassisSpeeds.fromFieldRelativeSpeeds(
                    xSpeedFiltered, ySpeedFiltered, rotFiltered, Rotation2d.fromDegrees(-m_gyro.getAngle()))
                : new ChassisSpeeds(xSpeedFiltered, ySpeedFiltered, rotFiltered));

    setModuleStates(swerveModuleStates);
  }

  /**
   * Sets the swerve ModuleStates
   *
   * @param desiredStates The desired SwerveModule states
   */
  public void setModuleStates(SwerveModuleState[] desiredStates) {
    // Normalize wheel speeds so no wheel exceeds max speed
    SwerveDriveKinematics.desaturateWheelSpeeds(
        desiredStates, DriveConstants.kMaxSpeedMetersPerSecond);

    m_frontLeft.setDesiredState(desiredStates[0]);
    m_frontRight.setDesiredState(desiredStates[1]);
    m_backLeft.setDesiredState(desiredStates[2]);
    m_backRight.setDesiredState(desiredStates[3]);
  }

  /**
   * Resets the drive encoders to currently read a position of 0
   */
  public void resetEncoders() {
    m_frontLeft.resetEncoders();
    m_frontRight.resetEncoders();
    m_backLeft.resetEncoders();
    m_backRight.resetEncoders();
  }

  /**
   * Get average distance traveled by all swerve modules
   * Useful for encoder-based autonomous driving
   *
   * @return Average distance in meters
   */
  public double getAverageDistance() {
    double fl = Math.abs(m_frontLeft.getDrivePosition());
    double fr = Math.abs(m_frontRight.getDrivePosition());
    double bl = Math.abs(m_backLeft.getDrivePosition());
    double br = Math.abs(m_backRight.getDrivePosition());

    return (fl + fr + bl + br) / 4.0;
  }

  /**
   * Zeroes the heading of the robot (makes current direction "forward")
   */
  public void zeroHeading() {
    m_gyro.reset();
  }

  /**
   * Returns the heading of the robot
   *
   * @return the robot's heading in degrees, from -180 to 180
   */
  public double getHeading() {
    return Rotation2d.fromDegrees(-m_gyro.getAngle()).getDegrees();
  }

  /**
   * Returns the turn rate of the robot
   *
   * @return The turn rate of the robot, in degrees per second
   */
  public double getTurnRate() {
    return -m_gyro.getRate();
  }

  /**
   * Get all module positions for odometry
   *
   * @return Array of module positions
   */
  private SwerveModulePosition[] getModulePositions() {
    return new SwerveModulePosition[] {
        m_frontLeft.getPosition(),
        m_frontRight.getPosition(),
        m_backLeft.getPosition(),
        m_backRight.getPosition()
    };
  }

  /**
   * Get current states of all swerve modules
   * Useful for calibration and debugging
   *
   * @return Array of module states [FL, FR, BL, BR]
   */
  public SwerveModuleState[] getModuleStates() {
    return new SwerveModuleState[] {
      m_frontLeft.getState(),
      m_frontRight.getState(),
      m_backLeft.getState(),
      m_backRight.getState()
    };
  }

  /**
   * Stop all swerve modules
   */
  public void stop() {
    m_frontLeft.stop();
    m_frontRight.stop();
    m_backLeft.stop();
    m_backRight.stop();
  }

  /**
   * Sets modules to X pattern (prevents being pushed when disabled)
   */
  public void setX() {
    m_frontLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
    m_frontRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
    m_backLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
    m_backRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
  }
}
