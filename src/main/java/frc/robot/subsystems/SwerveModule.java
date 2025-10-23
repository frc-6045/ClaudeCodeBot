package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.Constants.DriveConstants;

/**
 * Individual MAXSwerve module
 * Each module has a drive motor (NEO Vortex) and turning motor (NEO 550)
 */
public class SwerveModule {
  private final CANSparkMax m_driveMotor;
  private final CANSparkMax m_turningMotor;

  private final RelativeEncoder m_driveEncoder;
  private final RelativeEncoder m_turningEncoder;

  private final SparkPIDController m_turningPIDController;

  private final double m_chassisAngularOffset;
  private SwerveModuleState m_desiredState = new SwerveModuleState(0.0, new Rotation2d());

  /**
   * Constructs a MAXSwerve Module
   *
   * @param driveMotorId CAN ID for drive motor
   * @param turningMotorId CAN ID for turning motor
   * @param driveInverted Whether drive motor is inverted
   * @param turningInverted Whether turning motor is inverted
   * @param chassisAngularOffset Absolute encoder offset in radians
   */
  public SwerveModule(
      int driveMotorId,
      int turningMotorId,
      boolean driveInverted,
      boolean turningInverted,
      double chassisAngularOffset) {

    m_driveMotor = new CANSparkMax(driveMotorId, MotorType.kBrushless);
    m_turningMotor = new CANSparkMax(turningMotorId, MotorType.kBrushless);

    // Factory reset to avoid unexpected behavior
    m_driveMotor.restoreFactoryDefaults();
    m_turningMotor.restoreFactoryDefaults();

    // Get encoders
    m_driveEncoder = m_driveMotor.getEncoder();
    m_turningEncoder = m_turningMotor.getEncoder();

    // Get PID controller for turning
    m_turningPIDController = m_turningMotor.getPIDController();

    // Set inversions
    m_driveMotor.setInverted(driveInverted);
    m_turningMotor.setInverted(turningInverted);

    // Set current limits
    m_driveMotor.setSmartCurrentLimit(DriveConstants.kDriveCurrentLimit);
    m_turningMotor.setSmartCurrentLimit(DriveConstants.kTurningCurrentLimit);

    // Enable voltage compensation
    m_driveMotor.enableVoltageCompensation(12.0);
    m_turningMotor.enableVoltageCompensation(12.0);

    // Set idle mode to brake
    m_driveMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
    m_turningMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);

    // Configure drive encoder
    // Convert rotations to meters: (rotations * wheel circumference) / gear ratio
    double wheelCircumference = Math.PI * DriveConstants.kWheelDiameterMeters;
    m_driveEncoder.setPositionConversionFactor(
        wheelCircumference / DriveConstants.kDriveGearRatio);
    m_driveEncoder.setVelocityConversionFactor(
        wheelCircumference / DriveConstants.kDriveGearRatio / 60.0); // RPM to m/s

    // Configure turning encoder
    // Convert rotations to radians
    m_turningEncoder.setPositionConversionFactor(
        2 * Math.PI / DriveConstants.kTurnGearRatio);
    m_turningEncoder.setVelocityConversionFactor(
        2 * Math.PI / DriveConstants.kTurnGearRatio / 60.0); // RPM to rad/s

    // Configure turning PID
    m_turningPIDController.setP(DriveConstants.kTurningP);
    m_turningPIDController.setI(DriveConstants.kTurningI);
    m_turningPIDController.setD(DriveConstants.kTurningD);
    m_turningPIDController.setFF(0);

    // Enable PID wrapping for continuous input (turning motor can wrap around)
    m_turningPIDController.setPositionPIDWrappingEnabled(true);
    m_turningPIDController.setPositionPIDWrappingMinInput(0);
    m_turningPIDController.setPositionPIDWrappingMaxInput(2 * Math.PI);

    // Set CAN timeouts
    m_driveMotor.setCANTimeout(100);
    m_turningMotor.setCANTimeout(100);

    // Burn flash to save configuration
    m_driveMotor.burnFlash();
    m_turningMotor.burnFlash();

    m_chassisAngularOffset = chassisAngularOffset;

    // Reset encoders
    m_driveEncoder.setPosition(0);
    resetTurningEncoder();
  }

  /**
   * Returns the current state of the module
   *
   * @return The current state of the module
   */
  public SwerveModuleState getState() {
    return new SwerveModuleState(
        m_driveEncoder.getVelocity(),
        new Rotation2d(m_turningEncoder.getPosition() - m_chassisAngularOffset));
  }

  /**
   * Returns the current position of the module
   *
   * @return The current position of the module
   */
  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(
        m_driveEncoder.getPosition(),
        new Rotation2d(m_turningEncoder.getPosition() - m_chassisAngularOffset));
  }

  /**
   * Sets the desired state for the module
   *
   * @param desiredState Desired state with speed and angle
   */
  public void setDesiredState(SwerveModuleState desiredState) {
    // Apply chassis angular offset to the desired state
    SwerveModuleState correctedDesiredState = new SwerveModuleState();
    correctedDesiredState.speedMetersPerSecond = desiredState.speedMetersPerSecond;
    correctedDesiredState.angle = desiredState.angle.plus(Rotation2d.fromRadians(m_chassisAngularOffset));

    // Optimize the reference state to avoid spinning further than 90 degrees
    SwerveModuleState optimizedDesiredState = SwerveModuleState.optimize(
        correctedDesiredState,
        new Rotation2d(m_turningEncoder.getPosition()));

    // Command drive and turning motors
    m_driveMotor.set(optimizedDesiredState.speedMetersPerSecond / DriveConstants.kMaxSpeedMetersPerSecond);
    m_turningPIDController.setReference(
        optimizedDesiredState.angle.getRadians(),
        ControlType.kPosition);

    m_desiredState = desiredState;
  }

  /**
   * Zeroes all the encoders
   */
  public void resetEncoders() {
    m_driveEncoder.setPosition(0);
  }

  /**
   * Reset turning encoder to chassis angular offset
   * Call this when wheels are pointed straight forward
   */
  public void resetTurningEncoder() {
    m_turningEncoder.setPosition(m_chassisAngularOffset);
  }

  /**
   * Get drive position in meters
   *
   * @return Drive position in meters
   */
  public double getDrivePosition() {
    return m_driveEncoder.getPosition();
  }

  /**
   * Get turning angle in radians
   *
   * @return Turning angle in radians
   */
  public double getTurningPosition() {
    return m_turningEncoder.getPosition();
  }

  /**
   * Get drive velocity in m/s
   *
   * @return Drive velocity in m/s
   */
  public double getDriveVelocity() {
    return m_driveEncoder.getVelocity();
  }

  /**
   * Stop both drive and turning motors
   */
  public void stop() {
    m_driveMotor.set(0);
    m_turningMotor.set(0);
  }
}
