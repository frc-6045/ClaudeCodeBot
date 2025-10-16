# Robot Tuning Guide - Team 6045 Reefscape

This guide will help you configure and tune your robot for optimal performance.

## Initial Setup Checklist

### 1. Hardware Installation
- [ ] All motors are properly wired to SPARK MAX controllers
- [ ] CAN IDs match the values in [Constants.java](src/main/java/frc/robot/Constants.java)
- [ ] Beam break sensor is connected to DIO port 0
- [ ] RoboRIO is powered and connected to the robot
- [ ] Controllers are paired (Driver on port 0, Operator on port 1)

### 2. Motor Direction Testing
Before running the robot, test each motor individually:

1. **Drive Motors**: Verify left/right inversion
   - In `Constants.DriveConstants`, adjust `kLeftMotorsInverted` and `kRightMotorsInverted`
   - Push the robot forward - both sides should move forward

2. **Intake Motors**: Test intake direction
   - Intake should pull game pieces in, not push them out
   - Adjust motor inversions if needed

3. **Arm Motors**: Test carefully with limited power
   - Ensure positive commands raise the arm (don't let it crash!)
   - Test extension direction

### 3. Encoder Zeroing

#### Drive Encoders
Encoders auto-zero on boot. No action needed.

#### Arm Encoders (CRITICAL!)
1. Manually position the arm in the **stowed position** (fully retracted, safe starting position)
2. Enable the robot in **Teleop** mode
3. Press **Operator D-Pad Right** to home the arm (or call `arm.home()` from code)
4. The arm is now homed and safe to use

**WARNING**: Do NOT move the arm until it has been homed!

## PID Tuning

### What is PID?
PID (Proportional-Integral-Derivative) control helps motors reach and maintain target positions smoothly.

- **P (Proportional)**: Main correction force. Higher = faster response, but can overshoot
- **I (Integral)**: Eliminates steady-state error. Usually keep at 0 for FRC
- **D (Derivative)**: Dampens oscillation. Helps prevent overshoot
- **FF (FeedForward)**: Counteracts gravity/friction. Important for arms

### Tuning the Arm PID

Edit values in [Constants.java](src/main/java/frc/robot/Constants.java):

#### Step 1: Tune P (kArmP)
1. Set `kArmP = 0.01`, `kArmI = 0`, `kArmD = 0`, `kArmFF = 0`
2. Command the arm to a position (e.g., Level 1)
3. Observe behavior:
   - **Too slow to reach target**: Increase P by 2x
   - **Oscillates around target**: Decrease P by 0.5x
   - **Just right**: Reaches target smoothly without oscillation
4. Repeat until optimal

**Typical starting values**: `kArmP = 0.05` to `0.2`

#### Step 2: Tune D (kArmD)
1. With P tuned, if there's still oscillation, add D
2. Start with `kArmD = kArmP / 10`
3. Increase D if oscillation persists
4. Too much D makes the system sluggish

**Typical values**: `kArmD = 0.005` to `0.05`

#### Step 3: Tune FF (kArmFF)
1. FF helps hold the arm against gravity
2. With arm horizontal, measure the voltage needed to hold it (use manual control)
3. FF ≈ voltage / 12.0 / position
4. Fine-tune by testing different positions

**Typical values**: `kArmFF = 0.01` to `0.1`

### Tuning Extension PID
Follow the same process for `kExtensionP`, `kExtensionD`, `kExtensionFF`.

Extension typically needs:
- **Higher P** (less mass, faster response)
- **Less FF** (usually moving horizontally, less gravity)

### Tuning Drive PID (for Autonomous)
Drive PID is in `Constants.AutoConstants`:
- `kPDriveVel`: Start with 2.0, increase if robot doesn't reach speed
- `kPDriveTurn`: Start with 1.0, tune for turning accuracy

## Position Calibration

### Finding Arm Positions
1. **Home the arm** (stowed position)
2. Enable ShuffleBoard/SmartDashboard to see encoder values
3. Manually move arm to each scoring position using D-pad controls
4. Record the encoder values for each position:
   - Intake position: _____ ticks
   - Level 1: _____ ticks
   - Level 2: _____ ticks
   - Level 3: _____ ticks
   - Level 4: _____ ticks

5. Update these values in `Constants.ArmConstants`

### Finding Extension Positions
Same process:
- Retracted: Should be 0 (homed position)
- Extended (for L3/L4): _____ ticks

## Safety Limits

### Arm Limits
In `Constants.ArmConstants`, set safe position limits:

```java
public static final double kMinArmPosition = -10;  // Don't go below this
public static final double kMaxArmPosition = 500;  // Don't go above this
```

**How to find limits:**
1. Manually test arm range of motion (CAREFULLY!)
2. Note encoder values at physical limits
3. Set software limits 10-20 ticks inside physical limits for safety margin

### Extension Limits
```java
public static final double kMaxExtensionPosition = 1200;  // Don't extend beyond this
```

## Current Limits

Current limits prevent brownouts (robot losing power):

- **Drive motors**: 40A per motor (adjust if brownouts occur)
- **Arm motors**: 40A (increase if arm is underpowered)
- **Intake**: 30A
- **Climber**: 60A (needs more power)

Edit in respective `Constants` sections.

## Smart Motion Tuning

Smart Motion provides smooth, controlled movement:

```java
public static final double kMaxArmVelocity = 2000;      // encoder units/sec
public static final double kMaxArmAcceleration = 1500;  // encoder units/sec²
```

- **Higher velocity**: Faster movement, but more aggressive
- **Higher acceleration**: Quicker startup, but jerkier motion
- **Lower values**: Smoother, safer, but slower

Start conservative and increase gradually.

## Testing Procedure

### Pre-Match Checklist
1. [ ] Home the arm before enabling
2. [ ] Test drive in both directions
3. [ ] Test intake/outtake
4. [ ] Verify all scoring positions work
5. [ ] Test climber extension/retraction
6. [ ] Run autonomous routines
7. [ ] Check for brownouts under full load

### Common Issues

#### Arm won't move
- **Check**: Is arm homed? Look for "WARNING: Arm not homed" in console
- **Solution**: Home the arm using operator controls

#### Arm oscillates
- **Check**: P is too high
- **Solution**: Decrease `kArmP` by 25-50%

#### Arm doesn't reach target
- **Check**: P is too low
- **Solution**: Increase `kArmP` by 50-100%

#### Robot brownouts during movement
- **Check**: Too many motors running at once, current limits too high
- **Solution**: Lower current limits, reduce simultaneous movements

#### Drive motors don't work correctly
- **Check**: Motors may be inverted
- **Solution**: Toggle `kLeftMotorsInverted` or `kRightMotorsInverted`

#### Encoders reset randomly
- **Check**: Loose CAN connections, power issues
- **Solution**: Check all CAN wiring, ensure good connections

## Advanced Tuning

### Drive Characterization
For accurate autonomous:
1. Use WPILib's SysId tool to characterize drivetrain
2. Update `kMaxSpeedMetersPerSecond` and PID constants based on results

### Arm Gravity Compensation
For better FF tuning:
1. Measure arm mass and center of gravity
2. Calculate theoretical FF: `FF = (mass * 9.81 * CoG) / (12V * gear_ratio)`
3. Use as starting point for `kArmFF`

### Vision Alignment
If using Limelight:
1. Tune `kLimelightMountAngleDegrees` for accurate distance
2. Add vision PID loop for auto-alignment to reef

## Resources

- [WPILib PID Introduction](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/introduction-to-pid.html)
- [REV Robotics SPARK MAX Documentation](https://docs.revrobotics.com/sparkmax/)
- [FRC Control System Documentation](https://docs.wpilib.org/en/stable/)

## Notes

Record your final tuned values here for quick reference:

```
kArmP: _______
kArmD: _______
kArmFF: _______

kExtensionP: _______
kExtensionD: _______

Arm Positions:
  Stowed: 0
  Intake: _______
  Level 1: _______
  Level 2: _______
  Level 3: _______
  Level 4: _______

Extension Positions:
  Retracted: 0
  Extended: _______
```
