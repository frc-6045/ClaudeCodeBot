package frc.robot;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * FRC Team 6045 - 2025 Reefscape Robot
 * Main robot class that manages all subsystems and commands
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;
  private PowerDistribution m_pdp;

  // Brownout protection
  private static final double BROWNOUT_VOLTAGE = 10.5; // Volts
  private static final double WARNING_VOLTAGE = 11.5; // Volts
  private boolean m_lowVoltageWarning = false;

  @Override
  public void robotInit() {
    // Instantiate our RobotContainer. This will perform all our button bindings,
    // and put our autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();

    // Initialize Power Distribution for monitoring
    try {
      m_pdp = new PowerDistribution();
    } catch (Exception e) {
      System.err.println("WARNING: Could not initialize Power Distribution - voltage monitoring disabled");
    }
  }

  @Override
  public void robotPeriodic() {
    // Runs the Scheduler. This is responsible for polling buttons, adding
    // newly-scheduled commands, running already-scheduled commands, removing
    // finished or interrupted commands, and running subsystem periodic() methods.
    CommandScheduler.getInstance().run();

    // Monitor battery voltage and warn about brownouts
    double batteryVoltage = RobotController.getBatteryVoltage();
    SmartDashboard.putNumber("Battery Voltage", batteryVoltage);

    if (batteryVoltage < BROWNOUT_VOLTAGE) {
      if (!m_lowVoltageWarning) {
        System.err.println("⚠️ CRITICAL: Battery voltage below " + BROWNOUT_VOLTAGE + "V! Risk of brownout!");
        m_lowVoltageWarning = true;
      }
      SmartDashboard.putBoolean("LOW BATTERY", true);
    } else if (batteryVoltage < WARNING_VOLTAGE) {
      SmartDashboard.putBoolean("LOW BATTERY", true);
    } else {
      SmartDashboard.putBoolean("LOW BATTERY", false);
      m_lowVoltageWarning = false;
    }

    // Publish total current draw if PDP is available
    if (m_pdp != null) {
      SmartDashboard.putNumber("Total Current", m_pdp.getTotalCurrent());
    }
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}
}
