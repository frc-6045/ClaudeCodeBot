package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.VisionConstants;

/**
 * Subsystem for Limelight vision processing
 * Provides target detection and distance calculation
 */
public class VisionSubsystem extends SubsystemBase {
  private final NetworkTable m_limelight;
  private final NetworkTableEntry m_tx; // Horizontal offset
  private final NetworkTableEntry m_ty; // Vertical offset
  private final NetworkTableEntry m_ta; // Target area
  private final NetworkTableEntry m_tv; // Valid target (0 or 1)
  private final NetworkTableEntry m_ledMode; // LED mode control
  private final NetworkTableEntry m_camMode; // Camera mode (vision/driver)

  // LED modes
  public enum LEDMode {
    PIPELINE(0),    // Use LED mode from pipeline
    OFF(1),         // Force LEDs off
    BLINK(2),       // Force LEDs to blink
    ON(3);          // Force LEDs on

    public final int value;
    LEDMode(int value) {
      this.value = value;
    }
  }

  // Camera modes
  public enum CameraMode {
    VISION(0),      // Vision processing mode
    DRIVER(1);      // Driver camera mode (increases exposure, disables vision)

    public final int value;
    CameraMode(int value) {
      this.value = value;
    }
  }

  public VisionSubsystem() {
    m_limelight = NetworkTableInstance.getDefault().getTable(VisionConstants.kLimelightName);
    m_tx = m_limelight.getEntry("tx");
    m_ty = m_limelight.getEntry("ty");
    m_ta = m_limelight.getEntry("ta");
    m_tv = m_limelight.getEntry("tv");
    m_ledMode = m_limelight.getEntry("ledMode");
    m_camMode = m_limelight.getEntry("camMode");

    // Start with LEDs off to save battery
    setLEDMode(LEDMode.OFF);
    setCameraMode(CameraMode.VISION);
  }

  @Override
  public void periodic() {
    // Publish vision data to SmartDashboard
    SmartDashboard.putBoolean("Vision/Has Target", hasTarget());
    SmartDashboard.putNumber("Vision/X Offset", getHorizontalOffset());
    SmartDashboard.putNumber("Vision/Y Offset", getVerticalOffset());
    SmartDashboard.putNumber("Vision/Target Area", getTargetArea());
    SmartDashboard.putNumber("Vision/Distance (m)", getDistanceToTarget());
  }

  /**
   * Check if Limelight sees a valid target
   *
   * @return true if target is detected
   */
  public boolean hasTarget() {
    return m_tv.getDouble(0.0) == 1.0;
  }

  /**
   * Get horizontal offset to target in degrees
   * Negative = target is left, Positive = target is right
   *
   * @return Horizontal offset in degrees (-29.8 to 29.8)
   */
  public double getHorizontalOffset() {
    return m_tx.getDouble(0.0);
  }

  /**
   * Get vertical offset to target in degrees
   * Negative = target is below crosshair, Positive = target is above
   *
   * @return Vertical offset in degrees (-24.85 to 24.85)
   */
  public double getVerticalOffset() {
    return m_ty.getDouble(0.0);
  }

  /**
   * Get target area percentage (0-100)
   * Indicates how much of the image the target fills
   *
   * @return Target area as percentage
   */
  public double getTargetArea() {
    return m_ta.getDouble(0.0);
  }

  /**
   * Calculate distance to target using vertical angle
   * Uses the formula: distance = (targetHeight - cameraHeight) / tan(mountAngle + verticalOffset)
   *
   * @return Distance to target in meters (0 if no target)
   */
  public double getDistanceToTarget() {
    if (!hasTarget()) {
      return 0.0;
    }

    double verticalAngleRad = Math.toRadians(
        VisionConstants.kLimelightMountAngleDegrees + getVerticalOffset()
    );

    double heightDifference = VisionConstants.kTargetHeightMeters -
                              VisionConstants.kLimelightHeightMeters;

    return heightDifference / Math.tan(verticalAngleRad);
  }

  /**
   * Check if robot is aligned with target horizontally
   *
   * @param toleranceDegrees How close to center is "aligned" (typically 1-3 degrees)
   * @return true if aligned within tolerance
   */
  public boolean isAligned(double toleranceDegrees) {
    if (!hasTarget()) {
      return false;
    }
    return Math.abs(getHorizontalOffset()) <= toleranceDegrees;
  }

  /**
   * Check if robot is at correct distance from target
   *
   * @param targetDistanceMeters Desired distance
   * @param toleranceMeters Acceptable distance error
   * @return true if within range
   */
  public boolean isAtDistance(double targetDistanceMeters, double toleranceMeters) {
    if (!hasTarget()) {
      return false;
    }
    double currentDistance = getDistanceToTarget();
    return Math.abs(currentDistance - targetDistanceMeters) <= toleranceMeters;
  }

  /**
   * Set Limelight LED mode
   *
   * @param mode LED mode to set
   */
  public void setLEDMode(LEDMode mode) {
    m_ledMode.setNumber(mode.value);
  }

  /**
   * Set Limelight camera mode
   *
   * @param mode Camera mode to set
   */
  public void setCameraMode(CameraMode mode) {
    m_camMode.setNumber(mode.value);
  }

  /**
   * Get steering adjustment value for auto-aim
   * Returns a value proportional to horizontal offset
   *
   * @param kP Proportional constant for steering (typically 0.01-0.05)
   * @param minCommand Minimum command to overcome friction (typically 0.05)
   * @return Steering adjustment value (-1.0 to 1.0)
   */
  public double getSteeringAdjustment(double kP, double minCommand) {
    if (!hasTarget()) {
      return 0.0;
    }

    double offset = getHorizontalOffset();
    double steer = offset * kP;

    // Add minimum command to overcome friction (only when steering needed)
    if (Math.abs(offset) > 1.0) { // Only if not already aligned
      if (steer > 0) {
        steer += minCommand;
      } else if (steer < 0) {
        steer -= minCommand;
      }
    }

    // Clamp to [-1.0, 1.0]
    return Math.max(-1.0, Math.min(1.0, steer));
  }

  /**
   * Enable vision processing (turns on LEDs and vision mode)
   */
  public void enableVision() {
    setLEDMode(LEDMode.ON);
    setCameraMode(CameraMode.VISION);
  }

  /**
   * Disable vision processing (turns off LEDs, saves battery)
   */
  public void disableVision() {
    setLEDMode(LEDMode.OFF);
  }

  /**
   * Switch to driver camera mode (for driving, not targeting)
   */
  public void enableDriverMode() {
    setLEDMode(LEDMode.OFF);
    setCameraMode(CameraMode.DRIVER);
  }
}
