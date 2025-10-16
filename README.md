# FRC Team 6045 - 2025 Reefscape Robot Code

Robot code for Team 6045's 2025 Reefscape competition robot.

## Project Structure

```
src/main/java/frc/robot/
├── Robot.java              # Main robot class
├── RobotContainer.java     # Controller bindings and subsystem initialization
├── Constants.java          # Robot-wide constants and configuration
├── Main.java              # Entry point
├── subsystems/            # Robot subsystems
│   ├── DriveSubsystem.java
│   ├── IntakeSubsystem.java
│   ├── ArmSubsystem.java
│   └── ClimberSubsystem.java
└── commands/              # Command factories
    ├── DriveCommands.java
    ├── IntakeCommands.java
    ├── ArmCommands.java
    └── ClimberCommands.java
```

## Subsystems

### DriveSubsystem
- **Motors**: 4x NEO motors (CAN IDs 1-4)
- **Control**: Arcade, tank, and curvature drive modes
- **Features**: Speed limiting, encoder feedback

### IntakeSubsystem
- **Motors**: 2x NEO motors (CAN IDs 5-6)
- **Sensors**: Beam break sensor for game piece detection
- **Features**: Auto-hold when game piece detected, intake/outtake modes

### ArmSubsystem
- **Motors**: 2x NEO motors (CAN IDs 7-8) for arm angle and extension
- **Control**: PID position control
- **Positions**: Stowed, Intake, Level 1-4 scoring positions

### ClimberSubsystem
- **Motors**: 2x NEO motors (CAN IDs 9-10)
- **Features**: Synchronized or independent control, position feedback

## Controller Mappings

### Driver Controller (Port 0)
- **Left Stick Y**: Forward/backward drive
- **Right Stick X**: Turn/rotation
- **A Button**: Run intake
- **B Button**: Outtake/eject
- **X Button**: Stop intake
- **Left Bumper**: Slow mode (50% speed)
- **Right Bumper**: Quick turn mode

### Operator Controller (Port 1)
- **A Button**: Arm to intake position
- **B Button**: Arm to Level 1
- **X Button**: Arm to Level 2
- **Y Button**: Arm to Level 3
- **Right Bumper**: Arm to Level 4
- **Left Bumper**: Stow arm
- **Back Button**: Extend climber
- **Start Button**: Retract climber
- **Right Trigger**: Climb up
- **Left Trigger**: Climb down
- **D-Pad Up/Down**: Manual arm control
- **D-Pad Right**: **HOME ARM** (reset encoders - do this first!)

## Important: Homing the Arm

**BEFORE USING THE ROBOT**, you must home the arm:
1. Manually position the arm in the stowed position (fully retracted, safe)
2. Enable the robot in Teleop mode
3. Press **Operator D-Pad Right** to home the arm
4. You should see "ARM HOMED" in the console
5. The arm is now safe to use with position commands

**The arm will NOT move to positions until it has been homed!**

## Autonomous Modes

1. **Do Nothing**: Default - robot stays stationary
2. **Leave Community**: Drive forward for 3 seconds
3. **Score and Leave**: Score preloaded coral at Level 2, then leave community
4. **Two Piece Auto**: Score preload, pick up second coral, and score again

## Configuration & Tuning

All robot constants are defined in [Constants.java](src/main/java/frc/robot/Constants.java):
- Motor CAN IDs
- PID constants (MUST BE TUNED - see TUNING_GUIDE.md)
- Position setpoints (MUST BE CALIBRATED for your robot)
- Current limits
- Controller deadbands

**See [TUNING_GUIDE.md](TUNING_GUIDE.md) for detailed setup and tuning instructions!**

## Building and Deploying

### Prerequisites
- WPILib 2025 installed
- Java 17 or higher
- Gradle

### Build Commands
```bash
# Build the code
./gradlew build

# Deploy to robot
./gradlew deploy

# Run simulation
./gradlew simulateJava
```

## Hardware Requirements

- **RoboRIO 2.0**
- **10x REV NEO Brushless Motors**
- **10x SPARK MAX Motor Controllers**
- **1x Beam Break Sensor** (for intake)
- **2x Xbox Controllers**

## Motor Controller CAN IDs

| Motor | CAN ID |
|-------|--------|
| Left Front Drive | 1 |
| Left Rear Drive | 2 |
| Right Front Drive | 3 |
| Right Rear Drive | 4 |
| Intake Motor | 5 |
| Intake Roller | 6 |
| Arm Motor | 7 |
| Extension Motor | 8 |
| Left Climber | 9 |
| Right Climber | 10 |

## Safety Features

- **Software position limits** on arm to prevent crashes
- **Homing requirement** - arm won't move until properly zeroed
- **Current limiting** on all motors to prevent brownouts
- **Encoder feedback** for accurate position control
- **Automatic game piece holding** when detected by beam break
- **Voltage compensation** for consistent performance
- **Brake mode** on arm/climber to prevent uncontrolled movement
- **Deadband** on controller inputs to prevent drift
- **Max output limiting** for precision maneuvers

## Team Information

- **Team Number**: 6045
- **Competition**: 2025 Reefscape
- **Framework**: WPILib Command-Based

## Documentation

- **[QUICK_START.md](QUICK_START.md)** - First-time setup guide (start here!)
- **[TUNING_GUIDE.md](TUNING_GUIDE.md)** - PID tuning and calibration instructions
- **[KNOWN_ISSUES.md](KNOWN_ISSUES.md)** - ⚠️ Important limitations and potential problems
- **[FIXES_APPLIED.md](FIXES_APPLIED.md)** - List of safety fixes and improvements

## License

This code is provided as-is for educational purposes.

## Support

For questions or issues, contact the team programming lead or open an issue in this repository.

---







The whole point is to trailblaze "vibe coding" into FRC
