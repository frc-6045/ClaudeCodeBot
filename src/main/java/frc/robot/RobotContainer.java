package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.*;
import frc.robot.subsystems.*;
import frc.robot.subsystems.ArmSubsystem.ArmPosition;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // Subsystems
  private final DriveSubsystem m_drive = new DriveSubsystem();
  private final IntakeSubsystem m_intake = new IntakeSubsystem();
  private final ArmSubsystem m_arm = new ArmSubsystem();
  private final ClimberSubsystem m_climber = new ClimberSubsystem();

  // Controllers
  private final XboxController m_driverController = new XboxController(
      OperatorConstants.kDriverControllerPort);
  private final XboxController m_operatorController = new XboxController(
      OperatorConstants.kOperatorControllerPort);

  // Autonomous chooser
  private final SendableChooser<Command> m_autoChooser = new SendableChooser<>();

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();

    // Configure default commands
    configureDefaultCommands();

    // Configure autonomous chooser
    configureAutoChooser();
  }

  /**
   * Set up default commands for subsystems
   */
  private void configureDefaultCommands() {
    // Default drive command - arcade drive with left stick Y and right stick X
    m_drive.setDefaultCommand(
        DriveCommands.arcadeDrive(
            m_drive,
            () -> -applyDeadband(m_driverController.getLeftY()),
            () -> -applyDeadband(m_driverController.getRightX())
        )
    );
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // ===== DRIVER CONTROLS =====

    // A button - Intake coral
    new JoystickButton(m_driverController, XboxController.Button.kA.value)
        .whileTrue(IntakeCommands.intakeUntilDetected(m_intake));

    // B button - Outtake coral
    new JoystickButton(m_driverController, XboxController.Button.kB.value)
        .whileTrue(IntakeCommands.outtake(m_intake, 1.0));

    // X button - Stop intake
    new JoystickButton(m_driverController, XboxController.Button.kX.value)
        .onTrue(IntakeCommands.stop(m_intake));

    // Left bumper - Slow mode (reduce drive speed)
    new JoystickButton(m_driverController, XboxController.Button.kLeftBumper.value)
        .onTrue(Commands.runOnce(() -> m_drive.setMaxOutput(0.5)))
        .onFalse(Commands.runOnce(() -> m_drive.setMaxOutput(1.0)));

    // Right bumper - Quick turn (curvature drive)
    new JoystickButton(m_driverController, XboxController.Button.kRightBumper.value)
        .whileTrue(DriveCommands.curvatureDrive(
            m_drive,
            () -> -applyDeadband(m_driverController.getLeftY()),
            () -> -applyDeadband(m_driverController.getRightX()),
            true
        ));

    // ===== OPERATOR CONTROLS =====

    // A button - Arm to intake position
    new JoystickButton(m_operatorController, XboxController.Button.kA.value)
        .onTrue(ArmCommands.intakePosition(m_arm));

    // B button - Arm to Level 1
    new JoystickButton(m_operatorController, XboxController.Button.kB.value)
        .onTrue(ArmCommands.scoreLevel1(m_arm));

    // X button - Arm to Level 2
    new JoystickButton(m_operatorController, XboxController.Button.kX.value)
        .onTrue(ArmCommands.scoreLevel2(m_arm));

    // Y button - Arm to Level 3
    new JoystickButton(m_operatorController, XboxController.Button.kY.value)
        .onTrue(ArmCommands.scoreLevel3(m_arm));

    // Right bumper - Arm to Level 4
    new JoystickButton(m_operatorController, XboxController.Button.kRightBumper.value)
        .onTrue(ArmCommands.scoreLevel4(m_arm));

    // Left bumper - Stow arm
    new JoystickButton(m_operatorController, XboxController.Button.kLeftBumper.value)
        .onTrue(ArmCommands.stow(m_arm));

    // Back button - Extend climber
    new JoystickButton(m_operatorController, XboxController.Button.kBack.value)
        .onTrue(ClimberCommands.extend(m_climber, 50));

    // Start button - Retract climber
    new JoystickButton(m_operatorController, XboxController.Button.kStart.value)
        .onTrue(ClimberCommands.retract(m_climber, 50));

    // Right trigger - Climb
    new Trigger(() -> m_operatorController.getRightTriggerAxis() > 0.5)
        .whileTrue(ClimberCommands.climb(m_climber))
        .onFalse(ClimberCommands.stop(m_climber));

    // Left trigger - Descend climber
    new Trigger(() -> m_operatorController.getLeftTriggerAxis() > 0.5)
        .whileTrue(ClimberCommands.descend(m_climber))
        .onFalse(ClimberCommands.stop(m_climber));

    // D-pad for manual arm control and homing
    new Trigger(() -> m_operatorController.getPOV() == 0) // Up
        .whileTrue(Commands.run(() -> m_arm.manualArmControl(0.3), m_arm))
        .onFalse(Commands.runOnce(() -> m_arm.stop(), m_arm));

    new Trigger(() -> m_operatorController.getPOV() == 180) // Down
        .whileTrue(Commands.run(() -> m_arm.manualArmControl(-0.3), m_arm))
        .onFalse(Commands.runOnce(() -> m_arm.stop(), m_arm));

    new Trigger(() -> m_operatorController.getPOV() == 90) // Right - Home the arm
        .onTrue(Commands.runOnce(() -> {
          m_arm.home();
          System.out.println("ARM HOMED - Encoders reset, arm ready for use");
        }, m_arm));
  }

  /**
   * Configure the autonomous command chooser
   */
  private void configureAutoChooser() {
    // Add autonomous options
    m_autoChooser.setDefaultOption("Do Nothing", Commands.none());
    m_autoChooser.addOption("Leave Community", getLeaveAuto());
    m_autoChooser.addOption("Score and Leave", getScoreAndLeaveAuto());
    m_autoChooser.addOption("Two Piece Auto", getTwoPieceAuto());

    // Put the chooser on the dashboard
    SmartDashboard.putData("Auto Chooser", m_autoChooser);
  }

  /**
   * Simple autonomous - just drive forward to leave community
   *
   * @return the command to run in autonomous
   */
  public Command getLeaveAuto() {
    return Commands.sequence(
        Commands.runOnce(() -> m_drive.resetEncoders()),
        Commands.run(() -> m_drive.arcadeDrive(0.5, 0), m_drive)
            .withTimeout(3.0),
        Commands.runOnce(() -> m_drive.stop(), m_drive)
    );
  }

  /**
   * Score preloaded coral and leave community
   * WARNING: Arm must be manually homed before running autonomous!
   *
   * @return the command to run in autonomous
   */
  public Command getScoreAndLeaveAuto() {
    return Commands.sequence(
        // Check if arm is homed, print warning if not
        Commands.runOnce(() -> {
          if (!m_arm.isHomed()) {
            System.err.println("AUTONOMOUS ERROR: Arm not homed! Place arm in stowed position and press D-Pad Right in teleop first!");
          }
        }),

        // Score preloaded coral at Level 2
        ArmCommands.moveToPosition(m_arm, ArmPosition.LEVEL_2, 20),
        Commands.waitSeconds(0.5),
        IntakeCommands.outtake(m_intake, 0.5),
        Commands.waitSeconds(0.5),

        // Stow arm
        ArmCommands.stow(m_arm),

        // Drive backwards to leave community
        Commands.runOnce(() -> m_drive.resetEncoders()),
        Commands.run(() -> m_drive.arcadeDrive(-0.5, 0), m_drive)
            .withTimeout(3.0),
        Commands.runOnce(() -> m_drive.stop(), m_drive)
    );
  }

  /**
   * Score preloaded coral, pick up another, and score again
   * WARNING: Arm must be manually homed before running autonomous!
   *
   * @return the command to run in autonomous
   */
  public Command getTwoPieceAuto() {
    return Commands.sequence(
        // Check if arm is homed
        Commands.runOnce(() -> {
          if (!m_arm.isHomed()) {
            System.err.println("AUTONOMOUS ERROR: Arm not homed! Place arm in stowed position and press D-Pad Right in teleop first!");
          }
        }),

        // Score preloaded coral
        ArmCommands.moveToPosition(m_arm, ArmPosition.LEVEL_2, 20),
        IntakeCommands.outtake(m_intake, 0.5),

        // Move arm to intake position
        ArmCommands.intakePosition(m_arm),

        // Drive to game piece
        Commands.run(() -> m_drive.arcadeDrive(0.5, 0), m_drive)
            .withTimeout(2.0),

        // Intake game piece
        IntakeCommands.intakeUntilDetected(m_intake),

        // Drive back
        Commands.run(() -> m_drive.arcadeDrive(-0.5, 0), m_drive)
            .withTimeout(2.0),

        // Score second piece
        ArmCommands.moveToPosition(m_arm, ArmPosition.LEVEL_2, 20),
        IntakeCommands.outtake(m_intake, 0.5),

        // Stow
        ArmCommands.stow(m_arm),
        Commands.runOnce(() -> m_drive.stop(), m_drive)
    );
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return m_autoChooser.getSelected();
  }

  /**
   * Apply deadband to controller input
   *
   * @param value The input value
   * @return The value with deadband applied
   */
  private double applyDeadband(double value) {
    if (Math.abs(value) < OperatorConstants.kDriveDeadband) {
      return 0.0;
    }
    return value;
  }
}
