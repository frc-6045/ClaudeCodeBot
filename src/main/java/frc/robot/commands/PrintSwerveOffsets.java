package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.SwerveDriveSubsystem;

/**
 * Command to print current swerve module angles for calibration
 * Use this to find encoder offsets after physically aligning wheels
 *
 * USAGE:
 * 1. Manually rotate all wheels to point straight forward
 * 2. Run this command
 * 3. Copy the printed offset values to Constants.java
 */
public class PrintSwerveOffsets extends InstantCommand {
  private final SwerveDriveSubsystem m_drive;

  public PrintSwerveOffsets(SwerveDriveSubsystem drive) {
    m_drive = drive;
    addRequirements(drive);
  }

  @Override
  public void initialize() {
    // Get current module states (this includes turning encoder positions)
    var moduleStates = m_drive.getModuleStates();

    System.out.println("==================================================");
    System.out.println("🔧 SWERVE MODULE CALIBRATION");
    System.out.println("==================================================");
    System.out.println("Make sure all wheels are pointing STRAIGHT FORWARD!");
    System.out.println("");
    System.out.println("Current turning encoder positions (radians):");
    System.out.println("  Front Left:  " + String.format("%.4f", moduleStates[0].angle.getRadians()));
    System.out.println("  Front Right: " + String.format("%.4f", moduleStates[1].angle.getRadians()));
    System.out.println("  Back Left:   " + String.format("%.4f", moduleStates[2].angle.getRadians()));
    System.out.println("  Back Right:  " + String.format("%.4f", moduleStates[3].angle.getRadians()));
    System.out.println("");
    System.out.println("Add these to Constants.DriveConstants:");
    System.out.println("  public static final double kFrontLeftEncoderOffset = " +
                      String.format("%.4f", moduleStates[0].angle.getRadians()) + ";");
    System.out.println("  public static final double kFrontRightEncoderOffset = " +
                      String.format("%.4f", moduleStates[1].angle.getRadians()) + ";");
    System.out.println("  public static final double kBackLeftEncoderOffset = " +
                      String.format("%.4f", moduleStates[2].angle.getRadians()) + ";");
    System.out.println("  public static final double kBackRightEncoderOffset = " +
                      String.format("%.4f", moduleStates[3].angle.getRadians()) + ";");
    System.out.println("==================================================");

    // Publish to SmartDashboard
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber("Swerve Cal/FL Offset",
        moduleStates[0].angle.getRadians());
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber("Swerve Cal/FR Offset",
        moduleStates[1].angle.getRadians());
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber("Swerve Cal/BL Offset",
        moduleStates[2].angle.getRadians());
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber("Swerve Cal/BR Offset",
        moduleStates[3].angle.getRadians());
  }
}
