# FRC Team 6045 - 2025 Reefscape Robot Code

Robot code for Team 6045's 2025 Reefscape competition robot with **REV MAXSwerve drive**.

## Project Structure

```
src/main/java/frc/robot/
├── Robot.java                    # Main robot class
├── RobotContainer.java           # Controller bindings and subsystem initialization
├── Constants.java                # Robot-wide constants and configuration
├── Main.java                    # Entry point
├── subsystems/                  # Robot subsystems
│   ├── SwerveDriveSubsystem.java # Swerve drive with field-oriented control
│   ├── SwerveModule.java         # Individual MAXSwerve module
│   ├── IntakeSubsystem.java
│   ├── ArmSubsystem.java
│   └── ClimberSubsystem.java
└── commands/                    # Command factories
    ├── IntakeCommands.java
    ├── ArmCommands.java
    └── ClimberCommands.java
```

## Subsystems

### SwerveDriveSubsystem
- **Drive**: 4x REV MAXSwerve modules with NEO Vortex motors (CAN IDs 1, 3, 5, 7)
- **Turning**: 4x NEO 550 motors (CAN IDs 2, 4, 6, 8)
- **Gyro**: NavX-MXP for field-oriented control
- **Gear Ratio**: L3 (4.71:1) - Fast configuration
- **Features**: Field-oriented drive, robot-oriented mode, odometry, X-pattern wheel lock
- **Max Speed**: 5.6 m/s theoretical

### IntakeSubsystem
- **Motors**: 2x NEO motors (CAN IDs 9-10)
- **Sensors**: Beam break sensor for game piece detection
- **Features**: Auto-hold when game piece detected, intake/outtake modes

### ArmSubsystem
- **Motors**: 2x NEO motors (CAN IDs 11-12) for arm angle and extension
- **Control**: PID position control with Smart Motion
- **Positions**: Stowed, Intake, Level 1-4 scoring positions

### ClimberSubsystem
- **Motors**: 2x NEO motors (CAN IDs 13-14)
- **Features**: Synchronized or independent control, position feedback

## Controller Mappings

### Driver Controller (Port 0 - Swerve Drive)
- **Left Stick Y**: Forward/backward (field-oriented)
- **Left Stick X**: Strafe left/right (field-oriented)
- **Right Stick X**: Rotate robot
- **A Button**: Run intake
- **B Button**: Outtake/eject
- **X Button**: Stop intake
- **Left Bumper**: Robot-oriented mode (while held)
- **Right Bumper**: Reset gyro (zero heading)
- **Start Button**: X-pattern wheel lock

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
- **4x REV MAXSwerve Modules** (L3 gearing)
- **4x NEO Vortex Motors** (drive)
- **4x NEO 550 Motors** (turning)
- **6x Additional NEO Motors** (intake, arm, climber)
- **14x SPARK MAX Motor Controllers**
- **1x NavX-MXP Gyroscope** (on MXP port)
- **1x Beam Break Sensor** (for intake)
- **2x Xbox Controllers**

## Motor Controller CAN IDs

| Subsystem | Motor | CAN ID |
|-----------|-------|--------|
| **Swerve - Front Left** | Drive (NEO Vortex) | 1 |
| | Turn (NEO 550) | 2 |
| **Swerve - Front Right** | Drive (NEO Vortex) | 3 |
| | Turn (NEO 550) | 4 |
| **Swerve - Back Left** | Drive (NEO Vortex) | 5 |
| | Turn (NEO 550) | 6 |
| **Swerve - Back Right** | Drive (NEO Vortex) | 7 |
| | Turn (NEO 550) | 8 |
| **Intake** | Intake Motor | 9 |
| | Roller Motor | 10 |
| **Arm** | Arm Motor | 11 |
| | Extension Motor | 12 |
| **Climber** | Left Climber | 13 |
| | Right Climber | 14 |

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
- **[SWERVE_CALIBRATION.md](SWERVE_CALIBRATION.md)** - ⚙️ **CRITICAL**: Swerve module calibration (do this first!)
- **[TUNING_GUIDE.md](TUNING_GUIDE.md)** - PID tuning and calibration instructions
- **[KNOWN_ISSUES.md](KNOWN_ISSUES.md)** - ⚠️ Important limitations and potential problems
- **[FIXES_APPLIED.md](FIXES_APPLIED.md)** - List of safety fixes and improvements

## License

This code is provided as-is for educational purposes.

## Support

For questions or issues, contact the team programming lead or open an issue in this repository.

---







The whole point is to trailblaze "vibe coding" into FRC
