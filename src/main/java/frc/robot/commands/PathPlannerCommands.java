package frc.robot.commands;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathPlannerPath;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveDriveSubsystem;

/**
 * Factory class for PathPlanner autonomous commands
 * Provides easy access to path following and auto routines
 */
public class PathPlannerCommands {

  /**
   * Load and follow a PathPlanner path from deploy directory
   *
   * @param pathName Name of the path file (without .path extension)
   * @return Command that follows the path
   */
  public static Command followPath(String pathName) {
    try {
      PathPlannerPath path = PathPlannerPath.fromPathFile(pathName);
      return AutoBuilder.followPath(path);
    } catch (Exception e) {
      System.err.println("Failed to load path: " + pathName);
      e.printStackTrace();
      return edu.wpi.first.wpilibj2.command.Commands.none();
    }
  }

  /**
   * Load and run a named PathPlanner auto from deploy directory
   *
   * @param autoName Name of the auto file (without .auto extension)
   * @return Command that runs the full auto routine
   */
  public static Command runAuto(String autoName) {
    try {
      return AutoBuilder.buildAuto(autoName);
    } catch (Exception e) {
      System.err.println("Failed to load auto: " + autoName);
      e.printStackTrace();
      return edu.wpi.first.wpilibj2.command.Commands.none();
    }
  }

  /**
   * Create example path following command
   * This is a placeholder - create actual paths using PathPlanner GUI
   *
   * @return Example path following command
   */
  public static Command examplePath() {
    return followPath("Example Path");
  }

  // Prevent instantiation
  private PathPlannerCommands() {
    throw new UnsupportedOperationException("This is a utility class!");
  }
}
