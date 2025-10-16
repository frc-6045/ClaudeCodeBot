package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.ArmSubsystem.ArmPosition;

/**
 * Commands for the arm subsystem
 */
public class ArmCommands {

  /**
   * Command to move arm to a specific position and wait until reached
   *
   * @param arm The arm subsystem
   * @param position Target position
   * @param tolerance Acceptable error in encoder ticks
   * @return Command to move to position
   */
  public static Command moveToPosition(ArmSubsystem arm, ArmPosition position, double tolerance) {
    return Commands.sequence(
        arm.runOnce(() -> arm.setPosition(position)),
        Commands.waitUntil(() -> arm.atTargetPosition(tolerance))
    );
  }

  /**
   * Command to move arm to a specific position (doesn't wait)
   *
   * @param arm The arm subsystem
   * @param position Target position
   * @return Command to set position
   */
  public static Command setPosition(ArmSubsystem arm, ArmPosition position) {
    return arm.runOnce(() -> arm.setPosition(position));
  }

  /**
   * Command to stow the arm
   *
   * @param arm The arm subsystem
   * @return Stow command
   */
  public static Command stow(ArmSubsystem arm) {
    return setPosition(arm, ArmPosition.STOWED);
  }

  /**
   * Command to move arm to intake position
   *
   * @param arm The arm subsystem
   * @return Intake position command
   */
  public static Command intakePosition(ArmSubsystem arm) {
    return setPosition(arm, ArmPosition.INTAKE);
  }

  /**
   * Command to score at Level 1
   *
   * @param arm The arm subsystem
   * @return Level 1 scoring command
   */
  public static Command scoreLevel1(ArmSubsystem arm) {
    return setPosition(arm, ArmPosition.LEVEL_1);
  }

  /**
   * Command to score at Level 2
   *
   * @param arm The arm subsystem
   * @return Level 2 scoring command
   */
  public static Command scoreLevel2(ArmSubsystem arm) {
    return setPosition(arm, ArmPosition.LEVEL_2);
  }

  /**
   * Command to score at Level 3
   *
   * @param arm The arm subsystem
   * @return Level 3 scoring command
   */
  public static Command scoreLevel3(ArmSubsystem arm) {
    return setPosition(arm, ArmPosition.LEVEL_3);
  }

  /**
   * Command to score at Level 4
   *
   * @param arm The arm subsystem
   * @return Level 4 scoring command
   */
  public static Command scoreLevel4(ArmSubsystem arm) {
    return setPosition(arm, ArmPosition.LEVEL_4);
  }

  /**
   * Command to stop arm movement
   *
   * @param arm The arm subsystem
   * @return Stop command
   */
  public static Command stop(ArmSubsystem arm) {
    return arm.runOnce(() -> arm.stop());
  }
}
