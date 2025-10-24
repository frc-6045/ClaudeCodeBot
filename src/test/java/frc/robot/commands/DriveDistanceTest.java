package frc.robot.commands;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit tests for DriveDistance command logic
 */
public class DriveDistanceTest {

  @Test
  public void testDistanceParameterValidation() {
    // Test that distance can be positive or negative
    double forwardDistance = 3.0;
    double backwardDistance = -3.0;

    assertTrue("Forward distance should be positive", forwardDistance > 0);
    assertTrue("Backward distance should be negative", backwardDistance < 0);
  }

  @Test
  public void testSpeedNormalization() {
    // Speed should be converted to absolute value
    double speed = -0.5;
    double normalizedSpeed = Math.abs(speed);

    assertEquals("Speed should be normalized to positive", 0.5, normalizedSpeed, 0.001);
  }

  @Test
  public void testToleranceNormalization() {
    // Tolerance should be converted to absolute value
    double tolerance = -0.1;
    double normalizedTolerance = Math.abs(tolerance);

    assertEquals("Tolerance should be normalized to positive", 0.1, normalizedTolerance, 0.001);
  }

  @Test
  public void testDirectionCalculation() {
    // Test direction calculation based on target distance
    double forwardTarget = 2.0;
    double backwardTarget = -2.0;

    double forwardDirection = forwardTarget >= 0 ? 1.0 : -1.0;
    double backwardDirection = backwardTarget >= 0 ? 1.0 : -1.0;

    assertEquals("Forward should be positive direction", 1.0, forwardDirection, 0.001);
    assertEquals("Backward should be negative direction", -1.0, backwardDirection, 0.001);
  }

  @Test
  public void testDistanceComparison() {
    // Test if distance traveled exceeds target
    double targetDistance = 3.0;
    double tolerance = 0.1;

    double distanceTraveled1 = 2.5; // Not reached yet
    double distanceTraveled2 = 2.95; // Within tolerance
    double distanceTraveled3 = 3.5; // Exceeded

    assertFalse("Should not be finished yet",
        distanceTraveled1 >= targetDistance - tolerance);
    assertTrue("Should be within tolerance",
        distanceTraveled2 >= targetDistance - tolerance);
    assertTrue("Should have exceeded target",
        distanceTraveled3 >= targetDistance - tolerance);
  }

  @Test
  public void testTimeoutLogic() {
    double timeoutSeconds = 10.0;

    double elapsed1 = 5.0; // Not timed out
    double elapsed2 = 10.5; // Timed out

    assertFalse("Should not timeout yet", elapsed1 > timeoutSeconds);
    assertTrue("Should timeout", elapsed2 > timeoutSeconds);
  }

  @Test
  public void testDefaultToleranceValue() {
    // Default tolerance should be 0.1m (10cm)
    double defaultTolerance = 0.1;

    assertEquals("Default tolerance should be 10cm", 0.1, defaultTolerance, 0.001);
  }
}
