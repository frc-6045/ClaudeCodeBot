package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;

/**
 * Arm subsystem for scoring coral on the reef
 * Controls arm angle and extension for different scoring levels (L1-L4)
 */
public class ArmSubsystem extends SubsystemBase {
  private final CANSparkMax m_armMotor = new CANSparkMax(
      ArmConstants.kArmMotorId, MotorType.kBrushless);
  private final CANSparkMax m_extensionMotor = new CANSparkMax(
      ArmConstants.kExtensionMotorId, MotorType.kBrushless);

  private final RelativeEncoder m_armEncoder;
  private final RelativeEncoder m_extensionEncoder;

  private final SparkPIDController m_armPIDController;
  private final SparkPIDController m_extensionPIDController;

  private ArmPosition m_targetPosition = ArmPosition.STOWED;
  private boolean m_isHomed = false;

  public enum ArmPosition {
    STOWED(ArmConstants.kStowedPosition, ArmConstants.kRetractedPosition),
    INTAKE(ArmConstants.kIntakePosition, ArmConstants.kRetractedPosition),
    LEVEL_1(ArmConstants.kLevel1Position, ArmConstants.kRetractedPosition),
    LEVEL_2(ArmConstants.kLevel2Position, ArmConstants.kRetractedPosition),
    LEVEL_3(ArmConstants.kLevel3Position, ArmConstants.kExtendedPosition),
    LEVEL_4(ArmConstants.kLevel4Position, ArmConstants.kExtendedPosition);

    public final double armAngle;
    public final double extension;

    ArmPosition(double armAngle, double extension) {
      this.armAngle = armAngle;
      this.extension = extension;
    }
  }

  /** Creates a new ArmSubsystem. */
  public ArmSubsystem() {
    // Restore factory defaults
    m_armMotor.restoreFactoryDefaults();
    m_extensionMotor.restoreFactoryDefaults();

    // Get encoders
    m_armEncoder = m_armMotor.getEncoder();
    m_extensionEncoder = m_extensionMotor.getEncoder();

    // Get PID controllers
    m_armPIDController = m_armMotor.getPIDController();
    m_extensionPIDController = m_extensionMotor.getPIDController();

    // Configure PID for arm
    m_armPIDController.setP(ArmConstants.kArmP);
    m_armPIDController.setI(ArmConstants.kArmI);
    m_armPIDController.setD(ArmConstants.kArmD);
    m_armPIDController.setFF(ArmConstants.kArmFF);

    // Configure PID for extension
    m_extensionPIDController.setP(ArmConstants.kExtensionP);
    m_extensionPIDController.setI(ArmConstants.kExtensionI);
    m_extensionPIDController.setD(ArmConstants.kExtensionD);
    m_extensionPIDController.setFF(ArmConstants.kExtensionFF);

    // Set software limits for safety (min/max positions)
    m_armPIDController.setSmartMotionMaxVelocity(ArmConstants.kMaxArmVelocity, 0);
    m_armPIDController.setSmartMotionMaxAccel(ArmConstants.kMaxArmAcceleration, 0);
    m_extensionPIDController.setSmartMotionMaxVelocity(ArmConstants.kMaxExtensionVelocity, 0);
    m_extensionPIDController.setSmartMotionMaxAccel(ArmConstants.kMaxExtensionAcceleration, 0);

    // Set current limits
    m_armMotor.setSmartCurrentLimit(ArmConstants.kCurrentLimit);
    m_extensionMotor.setSmartCurrentLimit(ArmConstants.kCurrentLimit);

    // Enable voltage compensation for consistent behavior
    m_armMotor.enableVoltageCompensation(12.0);
    m_extensionMotor.enableVoltageCompensation(12.0);

    // Set idle mode to brake for safety
    m_armMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
    m_extensionMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);

    // Set CAN timeout for safety (motors will stop if no signal for 100ms)
    m_armMotor.setCANTimeout(100);
    m_extensionMotor.setCANTimeout(100);

    // Burn flash to save configuration
    m_armMotor.burnFlash();
    m_extensionMotor.burnFlash();

    // Reset encoders
    resetEncoders();
  }

  /**
   * Set the arm to a predefined position (with safety checks)
   *
   * @param position The target position
   */
  public void setPosition(ArmPosition position) {
    if (!m_isHomed) {
      System.err.println("WARNING: Arm not homed! Cannot safely move to position.");
      return;
    }

    m_targetPosition = position;

    // Clamp positions within safe limits
    double safeArmAngle = clampArmAngle(position.armAngle);
    double safeExtension = clampExtension(position.extension);

    m_armPIDController.setReference(safeArmAngle, ControlType.kSmartMotion);
    m_extensionPIDController.setReference(safeExtension, ControlType.kSmartMotion);
  }

  /**
   * Set arm angle manually (with safety limits)
   *
   * @param angle Target angle in encoder ticks
   */
  public void setArmAngle(double angle) {
    if (!m_isHomed) {
      System.err.println("WARNING: Arm not homed! Cannot safely move.");
      return;
    }
    double safeAngle = clampArmAngle(angle);
    m_armPIDController.setReference(safeAngle, ControlType.kSmartMotion);
  }

  /**
   * Set extension manually (with safety limits)
   *
   * @param extension Target extension in encoder ticks
   */
  public void setExtension(double extension) {
    if (!m_isHomed) {
      System.err.println("WARNING: Arm not homed! Cannot safely move.");
      return;
    }
    double safeExtension = clampExtension(extension);
    m_extensionPIDController.setReference(safeExtension, ControlType.kSmartMotion);
  }

  /**
   * Clamp arm angle to safe limits
   *
   * @param angle Desired angle
   * @return Clamped angle within safe range
   */
  private double clampArmAngle(double angle) {
    return Math.max(ArmConstants.kMinArmPosition,
                    Math.min(ArmConstants.kMaxArmPosition, angle));
  }

  /**
   * Clamp extension to safe limits
   *
   * @param extension Desired extension
   * @return Clamped extension within safe range
   */
  private double clampExtension(double extension) {
    return Math.max(ArmConstants.kRetractedPosition,
                    Math.min(ArmConstants.kMaxExtensionPosition, extension));
  }

  /**
   * Manual control of arm with voltage
   *
   * @param speed Speed from -1.0 to 1.0
   */
  public void manualArmControl(double speed) {
    m_armMotor.set(speed);
  }

  /**
   * Manual control of extension with voltage
   *
   * @param speed Speed from -1.0 to 1.0
   */
  public void manualExtensionControl(double speed) {
    m_extensionMotor.set(speed);
  }

  /**
   * Stop all arm movement
   */
  public void stop() {
    m_armMotor.set(0);
    m_extensionMotor.set(0);
  }

  /**
   * Get current arm encoder position
   *
   * @return Arm position in encoder ticks
   */
  public double getArmPosition() {
    return m_armEncoder.getPosition();
  }

  /**
   * Get current extension encoder position
   *
   * @return Extension position in encoder ticks
   */
  public double getExtensionPosition() {
    return m_extensionEncoder.getPosition();
  }

  /**
   * Check if arm is at target position
   *
   * @param tolerance Acceptable error in encoder ticks
   * @return true if arm is within tolerance of target
   */
  public boolean atTargetPosition(double tolerance) {
    boolean armAtTarget = Math.abs(getArmPosition() - m_targetPosition.armAngle) < tolerance;
    boolean extensionAtTarget = Math.abs(getExtensionPosition() - m_targetPosition.extension) < tolerance;
    return armAtTarget && extensionAtTarget;
  }

  /**
   * Reset encoders to zero (use when arm is in known position)
   * WARNING: Only call this when arm is physically at home position!
   */
  public void resetEncoders() {
    m_armEncoder.setPosition(0);
    m_extensionEncoder.setPosition(0);
  }

  /**
   * Mark the arm as homed (encoders are zeroed at known position)
   */
  public void setHomed(boolean homed) {
    m_isHomed = homed;
  }

  /**
   * Check if arm has been homed
   *
   * @return true if arm is homed
   */
  public boolean isHomed() {
    return m_isHomed;
  }

  /**
   * Home the arm (reset encoders and mark as homed)
   * Call this when arm is manually placed in stowed position
   */
  public void home() {
    resetEncoders();
    m_isHomed = true;
    m_targetPosition = ArmPosition.STOWED;
  }

  /**
   * Get the current target position
   *
   * @return Current target position
   */
  public ArmPosition getTargetPosition() {
    return m_targetPosition;
  }

  /**
   * Check if arm is within safe operating limits
   *
   * @return true if arm position is safe
   */
  public boolean isWithinLimits() {
    double armPos = getArmPosition();
    double extPos = getExtensionPosition();

    boolean armSafe = armPos >= ArmConstants.kMinArmPosition &&
                      armPos <= ArmConstants.kMaxArmPosition;
    boolean extSafe = extPos >= ArmConstants.kRetractedPosition &&
                      extPos <= ArmConstants.kMaxExtensionPosition;

    return armSafe && extSafe;
  }

  @Override
  public void periodic() {
    // Safety check - if arm goes out of bounds, stop it
    if (!isWithinLimits() && m_isHomed) {
      System.err.println("WARNING: Arm exceeded safe limits! Stopping motors.");
      stop();
    }

    // Telemetry
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber("Arm/Angle Position", getArmPosition());
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber("Arm/Extension Position", getExtensionPosition());
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putBoolean("Arm/Is Homed", m_isHomed);
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putBoolean("Arm/Within Limits", isWithinLimits());
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putString("Arm/Target Position", m_targetPosition.name());
  }
}
