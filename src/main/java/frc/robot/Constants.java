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
    // MAXSwerve Module CAN IDs
    // Front Left Module
    public static final int kFrontLeftDriveMotorId = 1;
    public static final int kFrontLeftTurningMotorId = 2;

    // Front Right Module
    public static final int kFrontRightDriveMotorId = 3;
    public static final int kFrontRightTurningMotorId = 4;

    // Back Left Module
    public static final int kBackLeftDriveMotorId = 5;
    public static final int kBackLeftTurningMotorId = 6;

    // Back Right Module
    public static final int kBackRightDriveMotorId = 7;
    public static final int kBackRightTurningMotorId = 8;

    // MAXSwerve physical characteristics
    public static final double kWheelDiameterMeters = 0.0762; // 3 inches = 0.0762 meters
    public static final double kDriveGearRatio = 4.71; // L3 gear ratio (fast MAXSwerve)
    public static final double kTurnGearRatio = 9424.0 / 203.0; // MAXSwerve turning ratio

    // Drivetrain dimensions (MEASURE YOUR ROBOT!)
    // Distance from robot center to module (front-back)
    public static final double kWheelBaseMeters = 0.5715; // 22.5 inches - ADJUST THIS!
    // Distance from robot center to module (left-right)
    public static final double kTrackWidthMeters = 0.5715; // 22.5 inches - ADJUST THIS!

    // Drive characteristics
    // Theoretical free speed: (6784 RPM * wheel circumference) / (60 * gear ratio) = 5.6 m/s
    public static final double kMaxSpeedMetersPerSecond = 5.6; // NEO Vortex theoretical max with L3
    public static final double kMaxAngularSpeed = 2 * Math.PI; // radians per second

    // Motor inversions - TUNE THESE for your robot
    public static final boolean kFrontLeftDriveInverted = false;
    public static final boolean kFrontRightDriveInverted = false;
    public static final boolean kBackLeftDriveInverted = false;
    public static final boolean kBackRightDriveInverted = false;

    public static final boolean kFrontLeftTurningInverted = true;
    public static final boolean kFrontRightTurningInverted = true;
    public static final boolean kBackLeftTurningInverted = true;
    public static final boolean kBackRightTurningInverted = true;

    // Absolute encoder offsets (radians) - MUST CALIBRATE!
    // Point all wheels forward, read encoder values, put negative of those values here
    public static final double kFrontLeftEncoderOffset = 0.0; // CALIBRATE ME!
    public static final double kFrontRightEncoderOffset = 0.0; // CALIBRATE ME!
    public static final double kBackLeftEncoderOffset = 0.0; // CALIBRATE ME!
    public static final double kBackRightEncoderOffset = 0.0; // CALIBRATE ME!

    // PID constants for turning motors
    public static final double kTurningP = 1.0;
    public static final double kTurningI = 0.0;
    public static final double kTurningD = 0.0;

    // Current limits
    public static final int kDriveCurrentLimit = 50; // NEO Vortex can handle more
    public static final int kTurningCurrentLimit = 30; // NEO 550

    // Slew rate limiters (prevent tipping)
    public static final double kDirectionSlewRate = 1.2; // radians per second
    public static final double kMagnitudeSlewRate = 1.8; // percent per second (1.8 = 0 to 100% in 0.56s)
    public static final double kRotationalSlewRate = 2.0; // percent per second
  }

  public static class IntakeConstants {
    // Motor CAN IDs (adjusted for swerve drive using IDs 1-8)
    public static final int kIntakeMotorId = 9;
    public static final int kIntakeRollerMotorId = 10;

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
    // Motor CAN IDs (adjusted for swerve drive using IDs 1-8)
    public static final int kArmMotorId = 11;
    public static final int kExtensionMotorId = 12;

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
    // Motor CAN IDs (adjusted for swerve drive using IDs 1-8)
    public static final int kLeftClimberMotorId = 13;
    public static final int kRightClimberMotorId = 14;

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
