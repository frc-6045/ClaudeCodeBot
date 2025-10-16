package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.IntakeSubsystem;

/**
 * Commands for the intake subsystem
 */
public class IntakeCommands {

  /**
   * Command to continuously intake until a game piece is detected
   *
   * @param intake The intake subsystem
   * @return Command that intakes until beam break is triggered
   */
  public static Command intakeUntilDetected(IntakeSubsystem intake) {
    return Commands.sequence(
        intake.runOnce(() -> intake.intake()),
        Commands.waitUntil(() -> intake.hasGamePiece()),
        intake.runOnce(() -> intake.hold())
    );
  }

  /**
   * Command to intake for a specified duration
   *
   * @param intake The intake subsystem
   * @param seconds Duration to run intake
   * @return Timed intake command
   */
  public static Command intakeTimed(IntakeSubsystem intake, double seconds) {
    return Commands.sequence(
        intake.runOnce(() -> intake.intake()),
        Commands.waitSeconds(seconds),
        intake.runOnce(() -> intake.stop())
    );
  }

  /**
   * Command to outtake/eject game piece
   *
   * @param intake The intake subsystem
   * @param seconds Duration to outtake
   * @return Outtake command
   */
  public static Command outtake(IntakeSubsystem intake, double seconds) {
    return Commands.sequence(
        intake.runOnce(() -> intake.outtake()),
        Commands.waitSeconds(seconds),
        intake.runOnce(() -> intake.stop())
    );
  }

  /**
   * Command to continuously intake (requires manual stop)
   *
   * @param intake The intake subsystem
   * @return Continuous intake command
   */
  public static Command intakeContinuous(IntakeSubsystem intake) {
    return intake.run(() -> intake.intake());
  }

  /**
   * Command to hold game piece
   *
   * @param intake The intake subsystem
   * @return Hold command
   */
  public static Command hold(IntakeSubsystem intake) {
    return intake.run(() -> intake.hold());
  }

  /**
   * Command to stop intake
   *
   * @param intake The intake subsystem
   * @return Stop command
   */
  public static Command stop(IntakeSubsystem intake) {
    return intake.runOnce(() -> intake.stop());
  }
}
