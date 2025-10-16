package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ClimberConstants;

/**
 * Climber subsystem for end-game climbing
 * Two-sided climber with synchronized or independent control
 */
public class ClimberSubsystem extends SubsystemBase {
  private final CANSparkMax m_leftClimber = new CANSparkMax(
      ClimberConstants.kLeftClimberMotorId, MotorType.kBrushless);
  private final CANSparkMax m_rightClimber = new CANSparkMax(
      ClimberConstants.kRightClimberMotorId, MotorType.kBrushless);

  private final RelativeEncoder m_leftEncoder;
  private final RelativeEncoder m_rightEncoder;

  private final SparkPIDController m_leftPIDController;
  private final SparkPIDController m_rightPIDController;

  private ClimberState m_currentState = ClimberState.RETRACTED;

  public enum ClimberState {
    RETRACTED,
    EXTENDED,
    CLIMBING,
    STOPPED
  }

  /** Creates a new ClimberSubsystem. */
  public ClimberSubsystem() {
    // Restore factory defaults
    m_leftClimber.restoreFactoryDefaults();
    m_rightClimber.restoreFactoryDefaults();

    // Get encoders
    m_leftEncoder = m_leftClimber.getEncoder();
    m_rightEncoder = m_rightClimber.getEncoder();

    // Get PID controllers
    m_leftPIDController = m_leftClimber.getPIDController();
    m_rightPIDController = m_rightClimber.getPIDController();

    // Set current limits (higher for climbing)
    m_leftClimber.setSmartCurrentLimit(ClimberConstants.kCurrentLimit);
    m_rightClimber.setSmartCurrentLimit(ClimberConstants.kCurrentLimit);

    // Burn flash to save configuration
    m_leftClimber.burnFlash();
    m_rightClimber.burnFlash();

    // Reset encoders
    resetEncoders();
  }

  /**
   * Extend both climbers to full height
   */
  public void extend() {
    m_leftPIDController.setReference(ClimberConstants.kExtendedPosition, ControlType.kPosition);
    m_rightPIDController.setReference(ClimberConstants.kExtendedPosition, ControlType.kPosition);
    m_currentState = ClimberState.EXTENDED;
  }

  /**
   * Retract both climbers to stowed position
   */
  public void retract() {
    m_leftPIDController.setReference(ClimberConstants.kRetractedPosition, ControlType.kPosition);
    m_rightPIDController.setReference(ClimberConstants.kRetractedPosition, ControlType.kPosition);
    m_currentState = ClimberState.RETRACTED;
  }

  /**
   * Climb using both climbers at set speed
   */
  public void climb() {
    m_leftClimber.set(ClimberConstants.kClimbSpeed);
    m_rightClimber.set(ClimberConstants.kClimbSpeed);
    m_currentState = ClimberState.CLIMBING;
  }

  /**
   * Descend using both climbers
   */
  public void descend() {
    m_leftClimber.set(ClimberConstants.kDescendSpeed);
    m_rightClimber.set(ClimberConstants.kDescendSpeed);
  }

  /**
   * Control left climber independently
   *
   * @param speed Speed from -1.0 to 1.0
   */
  public void setLeftSpeed(double speed) {
    m_leftClimber.set(speed);
  }

  /**
   * Control right climber independently
   *
   * @param speed Speed from -1.0 to 1.0
   */
  public void setRightSpeed(double speed) {
    m_rightClimber.set(speed);
  }

  /**
   * Set both climbers to same speed
   *
   * @param speed Speed from -1.0 to 1.0
   */
  public void setBothSpeed(double speed) {
    m_leftClimber.set(speed);
    m_rightClimber.set(speed);
  }

  /**
   * Stop both climbers
   */
  public void stop() {
    m_leftClimber.set(0);
    m_rightClimber.set(0);
    m_currentState = ClimberState.STOPPED;
  }

  /**
   * Get left climber encoder position
   *
   * @return Position in encoder ticks
   */
  public double getLeftPosition() {
    return m_leftEncoder.getPosition();
  }

  /**
   * Get right climber encoder position
   *
   * @return Position in encoder ticks
   */
  public double getRightPosition() {
    return m_rightEncoder.getPosition();
  }

  /**
   * Check if climbers are extended
   *
   * @param tolerance Acceptable error in encoder ticks
   * @return true if both climbers are at extended position
   */
  public boolean isExtended(double tolerance) {
    boolean leftExtended = Math.abs(getLeftPosition() - ClimberConstants.kExtendedPosition) < tolerance;
    boolean rightExtended = Math.abs(getRightPosition() - ClimberConstants.kExtendedPosition) < tolerance;
    return leftExtended && rightExtended;
  }

  /**
   * Check if climbers are retracted
   *
   * @param tolerance Acceptable error in encoder ticks
   * @return true if both climbers are at retracted position
   */
  public boolean isRetracted(double tolerance) {
    boolean leftRetracted = Math.abs(getLeftPosition() - ClimberConstants.kRetractedPosition) < tolerance;
    boolean rightRetracted = Math.abs(getRightPosition() - ClimberConstants.kRetractedPosition) < tolerance;
    return leftRetracted && rightRetracted;
  }

  /**
   * Reset climber encoders to zero
   */
  public void resetEncoders() {
    m_leftEncoder.setPosition(0);
    m_rightEncoder.setPosition(0);
  }

  /**
   * Get current climber state
   *
   * @return Current state
   */
  public ClimberState getState() {
    return m_currentState;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    // Could add safety limits or telemetry here
  }
}
