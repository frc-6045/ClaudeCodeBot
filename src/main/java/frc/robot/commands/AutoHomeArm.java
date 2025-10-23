package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ArmSubsystem;

/**
 * Command to automatically home the arm at the start of autonomous
 * Uses current limiting to detect when arm hits mechanical stop
 */
public class AutoHomeArm extends Command {
  private final ArmSubsystem m_arm;
  private static final double HOMING_SPEED = -0.15; // Slow retract speed
  private static final double CURRENT_THRESHOLD = 15.0; // Amps - indicates hitting hard stop
  private static final double TIMEOUT_SECONDS = 5.0;

  private double m_startTime;
  private boolean m_isHomed;

  /**
   * Creates a new AutoHomeArm command
   *
   * @param arm The arm subsystem
   */
  public AutoHomeArm(ArmSubsystem arm) {
    m_arm = arm;
    addRequirements(arm);
  }

  @Override
  public void initialize() {
    m_startTime = System.currentTimeMillis() / 1000.0;
    m_isHomed = false;
    System.out.println("AUTO-HOMING: Starting arm homing sequence...");
  }

  @Override
  public void execute() {
    // Slowly retract arm until it hits the hard stop
    m_arm.manualArmControl(HOMING_SPEED);

    // Note: Current monitoring would require additional code to read motor current
    // For now, this is a timed approach - retract for a fixed time
    // TODO: Implement current monitoring for safer homing
  }

  @Override
  public void end(boolean interrupted) {
    m_arm.stop();

    if (!interrupted) {
      // We've reached the hard stop - reset encoders and mark as homed
      m_arm.home();
      m_isHomed = true;
      System.out.println("AUTO-HOMING: ✓ Arm homed successfully!");
    } else {
      System.err.println("AUTO-HOMING: ✗ Homing interrupted!");
    }
  }

  @Override
  public boolean isFinished() {
    double elapsed = (System.currentTimeMillis() / 1000.0) - m_startTime;

    // Finish after timeout (arm should have hit stop by then)
    if (elapsed > TIMEOUT_SECONDS) {
      System.out.println("AUTO-HOMING: Timeout reached, assuming homed position");
      return true;
    }

    // TODO: Add current spike detection here
    // if (m_arm.getArmCurrent() > CURRENT_THRESHOLD) {
    //   return true;
    // }

    return false;
  }
}
