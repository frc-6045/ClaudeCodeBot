package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DriveSubsystem;
import java.util.function.DoubleSupplier;

/**
 * Drive commands for teleop and autonomous
 */
public class DriveCommands {

  /**
   * Creates a command to drive using arcade drive
   *
   * @param drive The drive subsystem
   * @param fwd   Supplier for forward speed
   * @param rot   Supplier for rotation speed
   * @return Command for arcade drive
   */
  public static Command arcadeDrive(
      DriveSubsystem drive,
      DoubleSupplier fwd,
      DoubleSupplier rot) {
    return drive.run(() -> drive.arcadeDrive(fwd.getAsDouble(), rot.getAsDouble()));
  }

  /**
   * Creates a command to drive using tank drive
   *
   * @param drive      The drive subsystem
   * @param leftSpeed  Supplier for left side speed
   * @param rightSpeed Supplier for right side speed
   * @return Command for tank drive
   */
  public static Command tankDrive(
      DriveSubsystem drive,
      DoubleSupplier leftSpeed,
      DoubleSupplier rightSpeed) {
    return drive.run(() -> drive.tankDrive(leftSpeed.getAsDouble(), rightSpeed.getAsDouble()));
  }

  /**
   * Creates a command to drive using curvature drive
   *
   * @param drive            The drive subsystem
   * @param fwd              Supplier for forward speed
   * @param rot              Supplier for rotation speed
   * @param allowTurnInPlace Supplier for whether to allow turning in place
   * @return Command for curvature drive
   */
  public static Command curvatureDrive(
      DriveSubsystem drive,
      DoubleSupplier fwd,
      DoubleSupplier rot,
      boolean allowTurnInPlace) {
    return drive.run(() -> drive.curvatureDrive(
        fwd.getAsDouble(),
        rot.getAsDouble(),
        allowTurnInPlace));
  }

  /**
   * Creates a command to stop the drivetrain
   *
   * @param drive The drive subsystem
   * @return Command to stop the drive
   */
  public static Command stopDrive(DriveSubsystem drive) {
    return drive.runOnce(() -> drive.stop());
  }
}
