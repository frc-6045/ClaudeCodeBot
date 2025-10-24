package frc.robot;

import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;
import frc.robot.Constants.DriveConstants;

/**
 * Configuration for PathPlanner path following
 * Sets up holonomic drive controller and robot constraints
 */
public class PathPlannerConfig {

  /**
   * Create robot configuration for PathPlanner
   * Defines mass, MOI, gearing, wheel radius, etc.
   *
   * @return RobotConfig for PathPlanner
   */
  public static RobotConfig createRobotConfig() {
    try {
      // Robot physical properties
      double massKg = 50.0; // Robot mass (estimate - MEASURE YOUR ROBOT!)
      double moi = 6.0; // Moment of inertia kg*m^2 (estimate)

      // Module configuration
      double wheelRadiusMeters = DriveConstants.kWheelDiameterMeters / 2.0;
      double gearing = DriveConstants.kDriveGearRatio;
      double maxDriveVelocityMPS = DriveConstants.kMaxSpeedMetersPerSecond;

      // Current limits
      double wheelCOF = 1.2; // Coefficient of friction (carpet)
      int driveCurrentLimit = DriveConstants.kDriveCurrentLimit;

      // Module positions (relative to robot center)
      double halfWheelBase = DriveConstants.kWheelBaseMeters / 2.0;
      double halfTrackWidth = DriveConstants.kTrackWidthMeters / 2.0;

      Translation2d[] moduleTranslations = new Translation2d[] {
        new Translation2d(halfWheelBase, halfTrackWidth),    // Front Left
        new Translation2d(halfWheelBase, -halfTrackWidth),   // Front Right
        new Translation2d(-halfWheelBase, halfTrackWidth),   // Back Left
        new Translation2d(-halfWheelBase, -halfTrackWidth)   // Back Right
      };

      // Create robot config
      return new RobotConfig(
        massKg,
        moi,
        new com.pathplanner.lib.config.ModuleConfig(
          wheelRadiusMeters,
          maxDriveVelocityMPS,
          wheelCOF,
          DCMotor.getNeoVortex(1).withReduction(gearing), // NEO Vortex with L3 gearing
          driveCurrentLimit,
          1 // Number of motors per module
        ),
        moduleTranslations
      );
    } catch (Exception e) {
      System.err.println("Failed to create PathPlanner RobotConfig: " + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Create holonomic drive controller for path following
   *
   * @return PPHolonomicDriveController configured for swerve drive
   */
  public static PPHolonomicDriveController createHolonomicController() {
    // Translation PID constants
    PIDConstants translationPID = new PIDConstants(
      5.0,  // kP - TUNE THIS for your robot
      0.0,  // kI
      0.0   // kD
    );

    // Rotation PID constants
    PIDConstants rotationPID = new PIDConstants(
      3.0,  // kP - TUNE THIS for your robot
      0.0,  // kI
      0.0   // kD
    );

    return new PPHolonomicDriveController(
      translationPID,
      rotationPID
    );
  }

  /**
   * Get maximum velocity for path planning
   *
   * @return Max velocity in m/s
   */
  public static double getMaxVelocity() {
    return DriveConstants.kMaxSpeedMetersPerSecond;
  }

  /**
   * Get maximum acceleration for path planning
   *
   * @return Max acceleration in m/s^2
   */
  public static double getMaxAcceleration() {
    return 3.0; // m/s^2 - Conservative to prevent tipping
  }

  /**
   * Get maximum angular velocity for path planning
   *
   * @return Max angular velocity in rad/s
   */
  public static double getMaxAngularVelocity() {
    return DriveConstants.kMaxAngularSpeed;
  }

  /**
   * Get maximum angular acceleration for path planning
   *
   * @return Max angular acceleration in rad/s^2
   */
  public static double getMaxAngularAcceleration() {
    return Math.PI * 2; // One full rotation per second^2
  }
}
