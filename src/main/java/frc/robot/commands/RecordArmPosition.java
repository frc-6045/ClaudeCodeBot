package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.ArmSubsystem;

/**
 * Command to record current arm position for calibration
 * Prints encoder values to console and SmartDashboard
 * Use this to find the actual encoder values for each scoring position
 */
public class RecordArmPosition extends InstantCommand {
  private final ArmSubsystem m_arm;
  private final String m_positionName;

  /**
   * Creates a command to record the current arm position
   *
   * @param arm The arm subsystem
   * @param positionName Name of the position being recorded (e.g., "LEVEL_2")
   */
  public RecordArmPosition(ArmSubsystem arm, String positionName) {
    m_arm = arm;
    m_positionName = positionName;
    addRequirements(arm);
  }

  @Override
  public void initialize() {
    double armPosition = m_arm.getArmPosition();
    double extensionPosition = m_arm.getExtensionPosition();

    // Print to console with clear formatting
    System.out.println("==================================================");
    System.out.println("📍 POSITION RECORDED: " + m_positionName);
    System.out.println("==================================================");
    System.out.println("Arm Angle:  " + String.format("%.2f", armPosition) + " encoder ticks");
    System.out.println("Extension:  " + String.format("%.2f", extensionPosition) + " encoder ticks");
    System.out.println("");
    System.out.println("Add to Constants.java:");
    System.out.println("public static final double k" + m_positionName + "Position = " +
                      String.format("%.2f", armPosition) + ";");
    if (extensionPosition > 50) { // If significantly extended
      System.out.println("public static final double kExtendedPosition = " +
                        String.format("%.2f", extensionPosition) + ";");
    }
    System.out.println("==================================================");

    // Also publish to SmartDashboard
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putString("Calibration/Last Position", m_positionName);
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber("Calibration/Last Arm Angle", armPosition);
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber("Calibration/Last Extension", extensionPosition);
  }
}
