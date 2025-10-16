package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This class should not be used for any other purpose.
 * All constants should be declared globally (i.e. public static). Do not put anything
 * functional in this class.
 */
public final class Constants {

  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final int kOperatorControllerPort = 1;

    // Controller deadband
    public static final double kDriveDeadband = 0.1;
  }

  public static class DriveConstants {
    // Motor CAN IDs
    public static final int kLeftFrontMotorId = 1;
    public static final int kLeftRearMotorId = 2;
    public static final int kRightFrontMotorId = 3;
    public static final int kRightRearMotorId = 4;

    // Drive characteristics
    public static final double kMaxSpeedMetersPerSecond = 4.0;
    public static final double kMaxAngularSpeed = 2 * Math.PI; // radians per second

    // Distance between left and right wheels
    public static final double kTrackWidthMeters = 0.6;

    // Motor inversion
    public static final boolean kLeftMotorsInverted = false;
    public static final boolean kRightMotorsInverted = true;

    // Current limits
    public static final int kCurrentLimit = 40;
  }

  public static class IntakeConstants {
    // Motor CAN IDs
    public static final int kIntakeMotorId = 5;
    public static final int kIntakeRollerMotorId = 6;

    // Intake speeds
    public static final double kIntakeSpeed = 0.75;
    public static final double kOuttakeSpeed = -0.5;
    public static final double kHoldSpeed = 0.1;

    // Current limits
    public static final int kCurrentLimit = 30;

    // Beam break sensor
    public static final int kBeamBreakChannel = 0;
  }

  public static class ArmConstants {
    // Motor CAN IDs
    public static final int kArmMotorId = 7;
    public static final int kExtensionMotorId = 8;

    // Arm positions (encoder ticks) - THESE NEED TO BE TUNED FOR YOUR ROBOT!
    public static final double kStowedPosition = 0;
    public static final double kIntakePosition = 5;
    public static final double kLevel1Position = 100;
    public static final double kLevel2Position = 200;
    public static final double kLevel3Position = 300;
    public static final double kLevel4Position = 400;

    // Extension positions (encoder ticks) - THESE NEED TO BE TUNED FOR YOUR ROBOT!
    public static final double kRetractedPosition = 0;
    public static final double kExtendedPosition = 1000;
    public static final double kMaxExtensionPosition = 1200; // Hard limit

    // Arm PID constants - MUST BE TUNED! Start with small values and increase
    public static final double kArmP = 0.1;
    public static final double kArmI = 0.0;
    public static final double kArmD = 0.0;
    public static final double kArmFF = 0.0;

    // Extension PID constants - MUST BE TUNED! Start with small values and increase
    public static final double kExtensionP = 0.1;
    public static final double kExtensionI = 0.0;
    public static final double kExtensionD = 0.0;
    public static final double kExtensionFF = 0.0;

    // Arm position limits (encoder ticks) - TUNE THESE TO MATCH YOUR PHYSICAL ROBOT!
    public static final double kMinArmPosition = -10; // Minimum safe angle
    public static final double kMaxArmPosition = 500; // Maximum safe angle

    // Smart Motion constraints (encoder units per second)
    public static final double kMaxArmVelocity = 2000;
    public static final double kMaxArmAcceleration = 1500;
    public static final double kMaxExtensionVelocity = 2000;
    public static final double kMaxExtensionAcceleration = 1500;

    // Current limits
    public static final int kCurrentLimit = 40;
  }

  public static class ClimberConstants {
    // Motor CAN IDs
    public static final int kLeftClimberMotorId = 9;
    public static final int kRightClimberMotorId = 10;

    // Climber positions
    public static final double kRetractedPosition = 0;
    public static final double kExtendedPosition = 5000;

    // Climb speeds
    public static final double kClimbSpeed = 0.8;
    public static final double kDescendSpeed = -0.5;

    // Current limits
    public static final int kCurrentLimit = 60;
  }

  public static class AutoConstants {
    public static final double kMaxSpeedMetersPerSecond = 2.0;
    public static final double kMaxAccelerationMetersPerSecondSquared = 2.0;

    // PID constants for auto
    public static final double kPDriveVel = 2.0;
    public static final double kPDriveTurn = 1.0;
  }

  public static class VisionConstants {
    // Limelight configuration
    public static final String kLimelightName = "limelight";
    public static final double kLimelightMountAngleDegrees = 25.0;
    public static final double kLimelightHeightMeters = 0.5;
    public static final double kTargetHeightMeters = 0.9;
  }
}
