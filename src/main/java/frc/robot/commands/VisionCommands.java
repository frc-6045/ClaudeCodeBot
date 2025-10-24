package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.SwerveDriveSubsystem;
import frc.robot.subsystems.VisionSubsystem;

/**
 * Factory class for vision-related commands
 */
public class VisionCommands {

  // Vision tuning constants
  private static final double STEERING_KP = 0.03; // Proportional gain for auto-aim
  private static final double STEERING_MIN_COMMAND = 0.05; // Minimum to overcome friction
  private static final double ALIGNMENT_TOLERANCE = 2.0; // degrees
  private static final double DISTANCE_TOLERANCE = 0.2; // meters
  private static final double AUTO_AIM_TIMEOUT = 3.0; // seconds

  /**
   * Command to auto-aim at target using vision
   * Rotates robot until aligned with target
   *
   * @param drive Drive subsystem
   * @param vision Vision subsystem
   * @return Command that auto-aims at target
   */
  public static Command autoAim(SwerveDriveSubsystem drive, VisionSubsystem vision) {
    return Commands.runEnd(
        () -> {
          // Enable vision if not already
          vision.enableVision();

          if (vision.hasTarget()) {
            // Get steering adjustment from vision
            double steer = vision.getSteeringAdjustment(STEERING_KP, STEERING_MIN_COMMAND);

            // Rotate in place (no translation)
            drive.drive(0, 0, -steer, false); // Robot-relative rotation
          } else {
            // No target - stop
            drive.stop();
          }
        },
        () -> {
          drive.stop();
          vision.disableVision(); // Turn off LEDs when done
        },
        drive,
        vision
    )
    .withTimeout(AUTO_AIM_TIMEOUT)
    .until(() -> vision.isAligned(ALIGNMENT_TOLERANCE));
  }

  /**
   * Command to aim and approach target to specific distance
   *
   * @param drive Drive subsystem
   * @param vision Vision subsystem
   * @param targetDistanceMeters Desired distance from target
   * @return Command that aims and drives to target
   */
  public static Command aimAndApproach(SwerveDriveSubsystem drive, VisionSubsystem vision,
                                       double targetDistanceMeters) {
    return Commands.run(
        () -> {
          vision.enableVision();

          if (vision.hasTarget()) {
            // Get steering adjustment
            double steer = vision.getSteeringAdjustment(STEERING_KP, STEERING_MIN_COMMAND);

            // Calculate forward/backward speed based on distance
            double currentDistance = vision.getDistanceToTarget();
            double distanceError = currentDistance - targetDistanceMeters;

            // Simple proportional control for distance
            double forwardSpeed = distanceError * 0.3; // kP for distance
            forwardSpeed = Math.max(-0.5, Math.min(0.5, forwardSpeed)); // Clamp speed

            // Drive forward/back while rotating to align
            drive.drive(forwardSpeed, 0, -steer, false);
          } else {
            drive.stop();
          }
        },
        drive,
        vision
    )
    .withTimeout(5.0)
    .until(() -> vision.isAligned(ALIGNMENT_TOLERANCE) &&
                 vision.isAtDistance(targetDistanceMeters, DISTANCE_TOLERANCE))
    .finallyDo(() -> {
      drive.stop();
      vision.disableVision();
    });
  }

  /**
   * Command to enable vision mode (turns on LEDs)
   *
   * @param vision Vision subsystem
   * @return Instant command to enable vision
   */
  public static Command enableVision(VisionSubsystem vision) {
    return Commands.runOnce(() -> vision.enableVision(), vision);
  }

  /**
   * Command to disable vision mode (turns off LEDs to save battery)
   *
   * @param vision Vision subsystem
   * @return Instant command to disable vision
   */
  public static Command disableVision(VisionSubsystem vision) {
    return Commands.runOnce(() -> vision.disableVision(), vision);
  }

  /**
   * Command to toggle to driver camera mode
   *
   * @param vision Vision subsystem
   * @return Instant command to enable driver mode
   */
  public static Command enableDriverMode(VisionSubsystem vision) {
    return Commands.runOnce(() -> vision.enableDriverMode(), vision);
  }

  /**
   * Command to align with target (rotation only, no forward movement)
   * Useful for scoring while stationary
   *
   * @param drive Drive subsystem
   * @param vision Vision subsystem
   * @return Command that rotates to align with target
   */
  public static Command alignOnly(SwerveDriveSubsystem drive, VisionSubsystem vision) {
    return autoAim(drive, vision); // Same as autoAim
  }

  /**
   * Manual vision-assisted driving command
   * Driver controls translation, vision controls rotation
   *
   * @param drive Drive subsystem
   * @param vision Vision subsystem
   * @param forwardSpeed Forward/backward speed from joystick
   * @param strafeSpeed Left/right speed from joystick
   * @return Command for vision-assisted driving
   */
  public static Command visionAssistedDrive(SwerveDriveSubsystem drive, VisionSubsystem vision,
                                           double forwardSpeed, double strafeSpeed) {
    return Commands.run(
        () -> {
          vision.enableVision();

          if (vision.hasTarget()) {
            // Driver controls translation, vision controls rotation
            double steer = vision.getSteeringAdjustment(STEERING_KP, STEERING_MIN_COMMAND);
            drive.drive(forwardSpeed, strafeSpeed, -steer, true); // Field-relative
          } else {
            // No target - give full control to driver (would need joystick input)
            drive.stop();
          }
        },
        drive,
        vision
    )
    .finallyDo(() -> vision.disableVision());
  }

  // Prevent instantiation
  private VisionCommands() {
    throw new UnsupportedOperationException("This is a utility class!");
  }
}
