package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.ClimberSubsystem;
import java.util.function.DoubleSupplier;

/**
 * Commands for the climber subsystem
 */
public class ClimberCommands {

  /**
   * Command to extend climbers and wait until fully extended
   *
   * @param climber The climber subsystem
   * @param tolerance Acceptable error in encoder ticks
   * @return Extend command
   */
  public static Command extend(ClimberSubsystem climber, double tolerance) {
    return Commands.sequence(
        climber.runOnce(() -> climber.extend()),
        Commands.waitUntil(() -> climber.isExtended(tolerance))
    );
  }

  /**
   * Command to retract climbers and wait until fully retracted
   *
   * @param climber The climber subsystem
   * @param tolerance Acceptable error in encoder ticks
   * @return Retract command
   */
  public static Command retract(ClimberSubsystem climber, double tolerance) {
    return Commands.sequence(
        climber.runOnce(() -> climber.retract()),
        Commands.waitUntil(() -> climber.isRetracted(tolerance))
    );
  }

  /**
   * Command to climb continuously
   *
   * @param climber The climber subsystem
   * @return Climb command
   */
  public static Command climb(ClimberSubsystem climber) {
    return climber.run(() -> climber.climb());
  }

  /**
   * Command to descend continuously
   *
   * @param climber The climber subsystem
   * @return Descend command
   */
  public static Command descend(ClimberSubsystem climber) {
    return climber.run(() -> climber.descend());
  }

  /**
   * Command for manual climber control with joystick input
   *
   * @param climber The climber subsystem
   * @param speedSupplier Supplier for climb speed
   * @return Manual control command
   */
  public static Command manualControl(ClimberSubsystem climber, DoubleSupplier speedSupplier) {
    return climber.run(() -> climber.setBothSpeed(speedSupplier.getAsDouble()));
  }

  /**
   * Command for independent left/right climber control
   *
   * @param climber The climber subsystem
   * @param leftSpeed Supplier for left climber speed
   * @param rightSpeed Supplier for right climber speed
   * @return Independent control command
   */
  public static Command independentControl(
      ClimberSubsystem climber,
      DoubleSupplier leftSpeed,
      DoubleSupplier rightSpeed) {
    return climber.run(() -> {
      climber.setLeftSpeed(leftSpeed.getAsDouble());
      climber.setRightSpeed(rightSpeed.getAsDouble());
    });
  }

  /**
   * Command to stop climbers
   *
   * @param climber The climber subsystem
   * @return Stop command
   */
  public static Command stop(ClimberSubsystem climber) {
    return climber.runOnce(() -> climber.stop());
  }
}
