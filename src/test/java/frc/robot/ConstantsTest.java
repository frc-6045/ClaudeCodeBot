package frc.robot;

import static org.junit.Assert.*;
import org.junit.Test;
import frc.robot.Constants.*;

/**
 * Unit tests for Constants validation
 * Ensures robot constants are within safe ranges
 */
public class ConstantsTest {

  @Test
  public void testMotorCANIdsAreUnique() {
    // Collect all CAN IDs
    int[] canIds = {
      DriveConstants.kFrontLeftDriveMotorId,
      DriveConstants.kFrontLeftTurningMotorId,
      DriveConstants.kFrontRightDriveMotorId,
      DriveConstants.kFrontRightTurningMotorId,
      DriveConstants.kBackLeftDriveMotorId,
      DriveConstants.kBackLeftTurningMotorId,
      DriveConstants.kBackRightDriveMotorId,
      DriveConstants.kBackRightTurningMotorId,
      IntakeConstants.kIntakeMotorId,
      IntakeConstants.kIntakeRollerMotorId,
      ArmConstants.kArmMotorId,
      ArmConstants.kExtensionMotorId,
      ClimberConstants.kLeftClimberMotorId,
      ClimberConstants.kRightClimberMotorId
    };

    // Check for duplicates
    for (int i = 0; i < canIds.length; i++) {
      for (int j = i + 1; j < canIds.length; j++) {
        assertNotEquals("Duplicate CAN ID found: " + canIds[i], canIds[i], canIds[j]);
      }
    }
  }

  @Test
  public void testCurrentLimitsAreReasonable() {
    // Current limits should be between 10A and 80A
    assertTrue("Drive current limit too low", DriveConstants.kDriveCurrentLimit >= 10);
    assertTrue("Drive current limit too high", DriveConstants.kDriveCurrentLimit <= 80);

    assertTrue("Turning current limit too low", DriveConstants.kTurningCurrentLimit >= 10);
    assertTrue("Turning current limit too high", DriveConstants.kTurningCurrentLimit <= 80);

    assertTrue("Intake current limit too low", IntakeConstants.kCurrentLimit >= 10);
    assertTrue("Intake current limit too high", IntakeConstants.kCurrentLimit <= 80);

    assertTrue("Arm current limit too low", ArmConstants.kCurrentLimit >= 10);
    assertTrue("Arm current limit too high", ArmConstants.kCurrentLimit <= 80);

    assertTrue("Climber current limit too low", ClimberConstants.kCurrentLimit >= 10);
    assertTrue("Climber current limit too high", ClimberConstants.kCurrentLimit <= 80);
  }

  @Test
  public void testIntakeSpeedsAreValid() {
    // Speeds should be between -1.0 and 1.0
    assertTrue("Intake speed out of range",
        IntakeConstants.kIntakeSpeed >= -1.0 && IntakeConstants.kIntakeSpeed <= 1.0);
    assertTrue("Outtake speed out of range",
        IntakeConstants.kOuttakeSpeed >= -1.0 && IntakeConstants.kOuttakeSpeed <= 1.0);
    assertTrue("Hold speed out of range",
        IntakeConstants.kHoldSpeed >= -1.0 && IntakeConstants.kHoldSpeed <= 1.0);
  }

  @Test
  public void testArmPositionLimitsAreConsistent() {
    // Min should be less than max
    assertTrue("Arm position limits inconsistent",
        ArmConstants.kMinArmPosition < ArmConstants.kMaxArmPosition);

    // Extended limits should be within normal limits
    assertTrue("Extended min position outside normal range",
        ArmConstants.kMinArmPositionExtended >= ArmConstants.kMinArmPosition);
    assertTrue("Extended max position outside normal range",
        ArmConstants.kMaxArmPositionExtended <= ArmConstants.kMaxArmPosition);
  }

  @Test
  public void testArmPositionsAreWithinLimits() {
    // All arm positions should be within safe limits
    assertTrue("Stowed position out of range",
        ArmConstants.kStowedPosition >= ArmConstants.kMinArmPosition &&
        ArmConstants.kStowedPosition <= ArmConstants.kMaxArmPosition);

    assertTrue("Intake position out of range",
        ArmConstants.kIntakePosition >= ArmConstants.kMinArmPosition &&
        ArmConstants.kIntakePosition <= ArmConstants.kMaxArmPosition);

    assertTrue("Level 1 position out of range",
        ArmConstants.kLevel1Position >= ArmConstants.kMinArmPosition &&
        ArmConstants.kLevel1Position <= ArmConstants.kMaxArmPosition);

    assertTrue("Level 2 position out of range",
        ArmConstants.kLevel2Position >= ArmConstants.kMinArmPosition &&
        ArmConstants.kLevel2Position <= ArmConstants.kMaxArmPosition);

    assertTrue("Level 3 position out of range",
        ArmConstants.kLevel3Position >= ArmConstants.kMinArmPosition &&
        ArmConstants.kLevel3Position <= ArmConstants.kMaxArmPosition);

    assertTrue("Level 4 position out of range",
        ArmConstants.kLevel4Position >= ArmConstants.kMinArmPosition &&
        ArmConstants.kLevel4Position <= ArmConstants.kMaxArmPosition);
  }

  @Test
  public void testPIDConstantsAreNonNegative() {
    // PID constants should not be negative
    assertTrue("Turning P is negative", DriveConstants.kTurningP >= 0);
    assertTrue("Turning I is negative", DriveConstants.kTurningI >= 0);
    assertTrue("Turning D is negative", DriveConstants.kTurningD >= 0);

    assertTrue("Arm P is negative", ArmConstants.kArmP >= 0);
    assertTrue("Arm I is negative", ArmConstants.kArmI >= 0);
    assertTrue("Arm D is negative", ArmConstants.kArmD >= 0);

    assertTrue("Extension P is negative", ArmConstants.kExtensionP >= 0);
    assertTrue("Extension I is negative", ArmConstants.kExtensionI >= 0);
    assertTrue("Extension D is negative", ArmConstants.kExtensionD >= 0);

    assertTrue("Climber P is negative", ClimberConstants.kClimberP >= 0);
    assertTrue("Climber I is negative", ClimberConstants.kClimberI >= 0);
    assertTrue("Climber D is negative", ClimberConstants.kClimberD >= 0);
  }

  @Test
  public void testControllerPortsAreValid() {
    // Controller ports should be 0-5
    assertTrue("Driver controller port invalid",
        OperatorConstants.kDriverControllerPort >= 0 &&
        OperatorConstants.kDriverControllerPort <= 5);

    assertTrue("Operator controller port invalid",
        OperatorConstants.kOperatorControllerPort >= 0 &&
        OperatorConstants.kOperatorControllerPort <= 5);

    // Should be different ports
    assertNotEquals("Controllers on same port",
        OperatorConstants.kDriverControllerPort,
        OperatorConstants.kOperatorControllerPort);
  }

  @Test
  public void testWheelDiameterIsReasonable() {
    // MAXSwerve uses 3-inch wheels (0.0762m)
    // Allow small variation for measurement error
    assertTrue("Wheel diameter too small", DriveConstants.kWheelDiameterMeters > 0.05);
    assertTrue("Wheel diameter too large", DriveConstants.kWheelDiameterMeters < 0.15);
  }

  @Test
  public void testMaxSpeedIsReasonable() {
    // Max speed should be positive and realistic for FRC
    assertTrue("Max speed too slow", DriveConstants.kMaxSpeedMetersPerSecond > 0.5);
    assertTrue("Max speed too fast", DriveConstants.kMaxSpeedMetersPerSecond < 10.0);
  }

  @Test
  public void testDeadbandIsReasonable() {
    // Deadband should be small positive value
    assertTrue("Deadband is negative", OperatorConstants.kDriveDeadband >= 0);
    assertTrue("Deadband too large", OperatorConstants.kDriveDeadband < 0.5);
  }
}
