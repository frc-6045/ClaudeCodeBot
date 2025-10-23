package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

/**
 * Intake subsystem for picking up and holding coral game pieces
 * Uses rollers with a beam break sensor to detect when a coral is secured
 */
public class IntakeSubsystem extends SubsystemBase {
  private final CANSparkMax m_intakeMotor = new CANSparkMax(
      IntakeConstants.kIntakeMotorId, MotorType.kBrushless);
  private final CANSparkMax m_rollerMotor = new CANSparkMax(
      IntakeConstants.kIntakeRollerMotorId, MotorType.kBrushless);

  private final DigitalInput m_beamBreak = new DigitalInput(IntakeConstants.kBeamBreakChannel);

  private IntakeState m_currentState = IntakeState.STOPPED;

  // Current spike detection for jammed mechanisms
  private static final double CURRENT_SPIKE_THRESHOLD = 25.0; // Amps (lower for intake)
  private static final int CURRENT_SPIKE_DURATION = 10; // Periodic cycles (~200ms at 20ms per cycle)
  private int m_intakeCurrentSpikeCounter = 0;
  private int m_rollerCurrentSpikeCounter = 0;

  public enum IntakeState {
    INTAKING,
    OUTTAKING,
    HOLDING,
    STOPPED
  }

  /** Creates a new IntakeSubsystem. */
  public IntakeSubsystem() {
    // Restore factory defaults
    m_intakeMotor.restoreFactoryDefaults();
    m_rollerMotor.restoreFactoryDefaults();

    // Set current limits
    m_intakeMotor.setSmartCurrentLimit(IntakeConstants.kCurrentLimit);
    m_rollerMotor.setSmartCurrentLimit(IntakeConstants.kCurrentLimit);

    // Enable voltage compensation for consistent behavior
    m_intakeMotor.enableVoltageCompensation(12.0);
    m_rollerMotor.enableVoltageCompensation(12.0);

    // Set idle mode to brake for safety
    m_intakeMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
    m_rollerMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);

    // Set CAN timeout for safety (motors will stop if no signal for 100ms)
    m_intakeMotor.setCANTimeout(100);
    m_rollerMotor.setCANTimeout(100);

    // Burn flash to save configuration
    m_intakeMotor.burnFlash();
    m_rollerMotor.burnFlash();
  }

  /**
   * Run intake to pick up coral
   */
  public void intake() {
    m_intakeMotor.set(IntakeConstants.kIntakeSpeed);
    m_rollerMotor.set(IntakeConstants.kIntakeSpeed);
    m_currentState = IntakeState.INTAKING;
  }

  /**
   * Run intake in reverse to eject coral
   */
  public void outtake() {
    m_intakeMotor.set(IntakeConstants.kOuttakeSpeed);
    m_rollerMotor.set(IntakeConstants.kOuttakeSpeed);
    m_currentState = IntakeState.OUTTAKING;
  }

  /**
   * Hold coral in place with low power
   */
  public void hold() {
    m_intakeMotor.set(IntakeConstants.kHoldSpeed);
    m_rollerMotor.set(IntakeConstants.kHoldSpeed);
    m_currentState = IntakeState.HOLDING;
  }

  /**
   * Stop all intake motors
   */
  public void stop() {
    m_intakeMotor.set(0);
    m_rollerMotor.set(0);
    m_currentState = IntakeState.STOPPED;
  }

  /**
   * Check if coral is detected by beam break sensor
   *
   * @return true if coral is present
   */
  public boolean hasGamePiece() {
    // Beam break returns false when beam is broken
    return !m_beamBreak.get();
  }

  /**
   * Get the current state of the intake
   *
   * @return Current intake state
   */
  public IntakeState getState() {
    return m_currentState;
  }

  /**
   * Set manual speed for intake motors
   *
   * @param speed Speed from -1.0 to 1.0
   */
  public void setSpeed(double speed) {
    m_intakeMotor.set(speed);
    m_rollerMotor.set(speed);
  }

  @Override
  public void periodic() {
    // Auto-transition to holding when game piece is detected during intake
    if (m_currentState == IntakeState.INTAKING && hasGamePiece()) {
      hold();
    }

    // Current spike detection - detect jammed mechanisms
    double intakeCurrent = m_intakeMotor.getOutputCurrent();
    double rollerCurrent = m_rollerMotor.getOutputCurrent();

    // Intake motor current spike detection
    if (intakeCurrent > CURRENT_SPIKE_THRESHOLD) {
      m_intakeCurrentSpikeCounter++;
      if (m_intakeCurrentSpikeCounter >= CURRENT_SPIKE_DURATION) {
        System.err.println("⚠️ WARNING: Intake motor current spike detected! Possible jam or overload.");
        System.err.println("Current: " + intakeCurrent + "A (threshold: " + CURRENT_SPIKE_THRESHOLD + "A)");
        // Stop the intake motor to prevent damage
        m_intakeMotor.set(0);
        m_intakeCurrentSpikeCounter = 0; // Reset counter
      }
    } else {
      m_intakeCurrentSpikeCounter = 0; // Reset if current drops
    }

    // Roller motor current spike detection
    if (rollerCurrent > CURRENT_SPIKE_THRESHOLD) {
      m_rollerCurrentSpikeCounter++;
      if (m_rollerCurrentSpikeCounter >= CURRENT_SPIKE_DURATION) {
        System.err.println("⚠️ WARNING: Roller motor current spike detected! Possible jam or overload.");
        System.err.println("Current: " + rollerCurrent + "A (threshold: " + CURRENT_SPIKE_THRESHOLD + "A)");
        // Stop the roller motor to prevent damage
        m_rollerMotor.set(0);
        m_rollerCurrentSpikeCounter = 0; // Reset counter
      }
    } else {
      m_rollerCurrentSpikeCounter = 0; // Reset if current drops
    }

    // Telemetry
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putBoolean("Intake/Has Game Piece", hasGamePiece());
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putString("Intake/State", m_currentState.name());
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber("Intake/Intake Current", intakeCurrent);
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber("Intake/Roller Current", rollerCurrent);
  }
}
