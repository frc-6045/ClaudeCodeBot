package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;

/**
 * Drivetrain subsystem for the robot
 * Controls tank drive with 4 NEO motors (2 per side)
 * Uses SPARK MAX follower mode for proper motor control
 */
public class DriveSubsystem extends SubsystemBase {
  // Left side motors
  private final CANSparkMax m_leftFront = new CANSparkMax(
      DriveConstants.kLeftFrontMotorId, MotorType.kBrushless);
  private final CANSparkMax m_leftRear = new CANSparkMax(
      DriveConstants.kLeftRearMotorId, MotorType.kBrushless);

  // Right side motors
  private final CANSparkMax m_rightFront = new CANSparkMax(
      DriveConstants.kRightFrontMotorId, MotorType.kBrushless);
  private final CANSparkMax m_rightRear = new CANSparkMax(
      DriveConstants.kRightRearMotorId, MotorType.kBrushless);

  // Differential drive - uses only front motors, rear motors follow
  private final DifferentialDrive m_drive = new DifferentialDrive(m_leftFront, m_rightFront);

  /** Creates a new DriveSubsystem. */
  public DriveSubsystem() {
    // Restore factory defaults to avoid unexpected behavior
    m_leftFront.restoreFactoryDefaults();
    m_leftRear.restoreFactoryDefaults();
    m_rightFront.restoreFactoryDefaults();
    m_rightRear.restoreFactoryDefaults();

    // Set current limits to prevent brownouts
    m_leftFront.setSmartCurrentLimit(DriveConstants.kCurrentLimit);
    m_leftRear.setSmartCurrentLimit(DriveConstants.kCurrentLimit);
    m_rightFront.setSmartCurrentLimit(DriveConstants.kCurrentLimit);
    m_rightRear.setSmartCurrentLimit(DriveConstants.kCurrentLimit);

    // Invert motors as needed (only need to invert leaders)
    m_leftFront.setInverted(DriveConstants.kLeftMotorsInverted);
    m_rightFront.setInverted(DriveConstants.kRightMotorsInverted);

    // Set the rear motors to follow the front motors (SPARK MAX follower mode)
    // Followers will automatically match the inversion of their leader
    m_leftRear.follow(m_leftFront);
    m_rightRear.follow(m_rightFront);

    // Set CAN timeout for safety (motors will stop if no signal for 100ms)
    m_leftFront.setCANTimeout(100);
    m_leftRear.setCANTimeout(100);
    m_rightFront.setCANTimeout(100);
    m_rightRear.setCANTimeout(100);

    // Burn flash to save configuration
    m_leftFront.burnFlash();
    m_leftRear.burnFlash();
    m_rightFront.burnFlash();
    m_rightRear.burnFlash();
  }

  /**
   * Arcade drive method for teleop control
   *
   * @param fwd The commanded forward movement
   * @param rot The commanded rotation
   */
  public void arcadeDrive(double fwd, double rot) {
    m_drive.arcadeDrive(fwd, rot);
  }

  /**
   * Tank drive method for teleop control
   *
   * @param leftSpeed  The commanded left side speed
   * @param rightSpeed The commanded right side speed
   */
  public void tankDrive(double leftSpeed, double rightSpeed) {
    m_drive.tankDrive(leftSpeed, rightSpeed);
  }

  /**
   * Curvature drive method for teleop control
   * Allows for quick turns while maintaining speed
   *
   * @param fwd           The commanded forward movement
   * @param rot           The commanded rotation
   * @param allowTurnInPlace Whether to allow turning in place
   */
  public void curvatureDrive(double fwd, double rot, boolean allowTurnInPlace) {
    m_drive.curvatureDrive(fwd, rot, allowTurnInPlace);
  }

  /**
   * Stop all drive motors
   */
  public void stop() {
    m_drive.stopMotor();
  }

  /**
   * Set the max output of the drive. Useful for limiting speed during specific maneuvers
   *
   * @param maxOutput Maximum output from 0 to 1.0
   */
  public void setMaxOutput(double maxOutput) {
    m_drive.setMaxOutput(maxOutput);
  }

  /**
   * Get the average encoder position of the left side
   *
   * @return Average encoder position in rotations
   */
  public double getLeftEncoderPosition() {
    return (m_leftFront.getEncoder().getPosition() + m_leftRear.getEncoder().getPosition()) / 2.0;
  }

  /**
   * Get the average encoder position of the right side
   *
   * @return Average encoder position in rotations
   */
  public double getRightEncoderPosition() {
    return (m_rightFront.getEncoder().getPosition() + m_rightRear.getEncoder().getPosition()) / 2.0;
  }

  /**
   * Reset the drive encoders to currently read a position of 0
   */
  public void resetEncoders() {
    m_leftFront.getEncoder().setPosition(0);
    m_leftRear.getEncoder().setPosition(0);
    m_rightFront.getEncoder().setPosition(0);
    m_rightRear.getEncoder().setPosition(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    // Monitor for CAN errors (if motors stop responding, this will show in DS)
  }
}
