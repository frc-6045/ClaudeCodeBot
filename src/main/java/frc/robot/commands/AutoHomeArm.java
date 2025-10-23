package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ArmSubsystem;

/**
 * Command to automatically home the arm at the start of autonomous
 * Uses current spike detection to know when arm hits mechanical stop
 */
public class AutoHomeArm extends Command {
  private final ArmSubsystem m_arm;
  private static final double HOMING_SPEED = -0.15; // Slow retract speed
  private static final double CURRENT_THRESHOLD = 15.0; // Amps - indicates hitting hard stop
  private static final int CURRENT_SPIKE_CYCLES = 5; // Confirm for 5 cycles (~100ms)
  private static final double TIMEOUT_SECONDS = 5.0; // Safety timeout

  private double m_startTime;
  private int m_currentSpikeCounter;

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
    m_currentSpikeCounter = 0;
    System.out.println("AUTO-HOMING: Starting arm homing sequence...");
    System.out.println("AUTO-HOMING: Retracting slowly until hard stop detected");
  }

  @Override
  public void execute() {
    // Slowly retract arm until it hits the hard stop
    m_arm.manualArmControl(HOMING_SPEED);

    // Monitor current draw - increment counter if above threshold
    double current = m_arm.getArmCurrent();
    if (current > CURRENT_THRESHOLD) {
      m_currentSpikeCounter++;
      if (m_currentSpikeCounter >= CURRENT_SPIKE_CYCLES) {
        System.out.println("AUTO-HOMING: Hard stop detected (current: " +
                          String.format("%.1f", current) + "A)");
      }
    } else {
      m_currentSpikeCounter = 0; // Reset if current drops
    }
  }

  @Override
  public void end(boolean interrupted) {
    m_arm.stop();

    if (!interrupted) {
      // We've reached the hard stop - reset encoders and mark as homed
      m_arm.home();
      System.out.println("AUTO-HOMING: ✓ Arm homed successfully!");
    } else {
      System.err.println("AUTO-HOMING: ✗ Homing interrupted!");
    }
  }

  @Override
  public boolean isFinished() {
    double elapsed = (System.currentTimeMillis() / 1000.0) - m_startTime;

    // Finish if current spike sustained for required cycles (hit hard stop)
    if (m_currentSpikeCounter >= CURRENT_SPIKE_CYCLES) {
      System.out.println("AUTO-HOMING: Hard stop confirmed via current detection");
      return true;
    }

    // Safety timeout - finish if taking too long
    if (elapsed > TIMEOUT_SECONDS) {
      System.err.println("AUTO-HOMING: ⚠️ Timeout reached without detecting hard stop!");
      System.err.println("AUTO-HOMING: Check mechanical hard stop and current threshold");
      return true;
    }

    return false;
  }
}
