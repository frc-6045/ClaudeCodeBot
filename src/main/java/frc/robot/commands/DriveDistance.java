package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveDriveSubsystem;

/**
 * Command to drive a specific distance using encoder feedback
 * More reliable than time-based driving for autonomous
 */
public class DriveDistance extends Command {
  private final SwerveDriveSubsystem m_drive;
  private final double m_distanceMeters;
  private final double m_speed;
  private final double m_tolerance;

  private double m_startDistance;
  private static final double TIMEOUT_SECONDS = 10.0; // Safety timeout
  private double m_startTime;

  /**
   * Creates a new DriveDistance command
   *
   * @param drive The drive subsystem
   * @param distanceMeters Distance to drive (positive = forward, negative = backward)
   * @param speed Speed to drive at (0.0 to 1.0)
   */
  public DriveDistance(SwerveDriveSubsystem drive, double distanceMeters, double speed) {
    this(drive, distanceMeters, speed, 0.1); // Default 10cm tolerance
  }

  /**
   * Creates a new DriveDistance command with custom tolerance
   *
   * @param drive The drive subsystem
   * @param distanceMeters Distance to drive (positive = forward, negative = backward)
   * @param speed Speed to drive at (0.0 to 1.0)
   * @param toleranceMeters How close to get to target (meters)
   */
  public DriveDistance(SwerveDriveSubsystem drive, double distanceMeters, double speed, double toleranceMeters) {
    m_drive = drive;
    m_distanceMeters = distanceMeters;
    m_speed = Math.abs(speed); // Ensure positive
    m_tolerance = Math.abs(toleranceMeters);
    addRequirements(drive);
  }

  @Override
  public void initialize() {
    m_startDistance = getAverageDistance();
    m_startTime = System.currentTimeMillis() / 1000.0;
    System.out.println("DriveDistance: Starting - target: " +
                      String.format("%.2f", m_distanceMeters) + "m");
  }

  @Override
  public void execute() {
    // Determine direction based on target distance
    double direction = m_distanceMeters >= 0 ? 1.0 : -1.0;

    // Drive in the appropriate direction at specified speed
    // Robot-relative mode (field-relative = false)
    m_drive.drive(m_speed * direction, 0, 0, false);
  }

  @Override
  public void end(boolean interrupted) {
    m_drive.stop();

    if (interrupted) {
      System.err.println("DriveDistance: Interrupted!");
    } else {
      double actualDistance = getAverageDistance() - m_startDistance;
      System.out.println("DriveDistance: Complete - traveled: " +
                        String.format("%.2f", actualDistance) + "m");
    }
  }

  @Override
  public boolean isFinished() {
    double distanceTraveled = Math.abs(getAverageDistance() - m_startDistance);
    double targetDistance = Math.abs(m_distanceMeters);

    // Check if we've reached the target distance
    if (distanceTraveled >= targetDistance - m_tolerance) {
      return true;
    }

    // Safety timeout
    double elapsed = (System.currentTimeMillis() / 1000.0) - m_startTime;
    if (elapsed > TIMEOUT_SECONDS) {
      System.err.println("DriveDistance: ⚠️ Timeout reached! Check robot mobility.");
      return true;
    }

    return false;
  }

  /**
   * Get average distance traveled by all swerve modules
   * This provides a more accurate reading than a single module
   *
   * @return Average distance in meters
   */
  private double getAverageDistance() {
    return m_drive.getAverageDistance();
  }
}
