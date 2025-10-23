# MAXSwerve Calibration Guide - Team 6045

This guide will help you calibrate your MAXSwerve modules for optimal performance.

## Critical Setup Steps

### 1. Measure Your Robot

**BEFORE deploying code**, measure your robot dimensions:

1. Measure the distance from robot center to the center of each swerve module
2. Front-to-back distance (wheelbase)
3. Left-to-right distance (track width)

Update in [Constants.java](src/main/java/frc/robot/Constants.java):
```java
public static final double kWheelBaseMeters = 0.5715; // CHANGE THIS!
public static final double kTrackWidthMeters = 0.5715; // CHANGE THIS!
```

### 2. Verify Gear Ratio

Your robot uses **L3 (fast)** gearing with ratio 4.71:1.

If you're using different gearing:
- **L2**: Change `kDriveGearRatio = 5.50`
- **L1**: Change `kDriveGearRatio = 6.75`

### 3. Set CAN IDs

Your swerve modules should have CAN IDs assigned as:

| Module | Drive Motor | Turn Motor |
|--------|-------------|------------|
| Front Left | 1 | 2 |
| Front Right | 3 | 4 |
| Back Left | 5 | 6 |
| Back Right | 7 | 8 |

Use REV Hardware Client to configure these IDs.

## Calibrating Absolute Encoder Offsets

This is the **most important** calibration step!

### Why This Matters

MAXSwerve modules use relative encoders, which means they don't know their absolute position when the robot boots. If the wheels aren't pointed forward (0 degrees), the robot will drive incorrectly.

### Calibration Procedure

#### Step 1: Point All Wheels Forward

1. **Manually rotate** each swerve module so all wheels point straight forward
2. All wheels should be parallel, pointing in the "forward" direction of the robot
3. Use a straight edge or measuring tool to ensure they're truly aligned

#### Step 2: Read Encoder Values

1. Deploy the code to the robot
2. Enable robot in **Teleop** mode
3. Open **SmartDashboard** or **Shuffleboard**
4. Look for encoder readings for each module's turning motor
   - You can add these to `SwerveDriveSubsystem.periodic()`:
   ```java
   SmartDashboard.putNumber("FL Turn Pos", m_frontLeft.getTurningPosition());
   SmartDashboard.putNumber("FR Turn Pos", m_frontRight.getTurningPosition());
   SmartDashboard.putNumber("BL Turn Pos", m_backLeft.getTurningPosition());
   SmartDashboard.putNumber("BR Turn Pos", m_backRight.getTurningPosition());
   ```

5. Write down the values (they'll be in radians, probably between 0 and 2π)

#### Step 3: Update Constants

In [Constants.java](src/main/java/frc/robot/Constants.java), update the encoder offsets:

```java
public static final double kFrontLeftEncoderOffset = 1.234; // YOUR VALUE HERE
public static final double kFrontRightEncoderOffset = 2.345; // YOUR VALUE HERE
public static final double kBackLeftEncoderOffset = 3.456; // YOUR VALUE HERE
public static final double kBackRightEncoderOffset = 4.567; // YOUR VALUE HERE
```

**IMPORTANT**: Use the EXACT values you read from the dashboard!

#### Step 4: Test

1. Redeploy the code
2. Enable robot
3. Drive forward (left stick Y)
   - Robot should drive straight forward
   - If it drifts or spins, recheck your offsets

## Tuning Motor Inversions

If your robot drives backward when commanded forward, or turns the wrong way:

### Drive Motor Inversions

Test each module individually. Update in Constants.java:

```java
public static final boolean kFrontLeftDriveInverted = false; // Try true if backward
public static final boolean kFrontRightDriveInverted = false;
public static final boolean kBackLeftDriveInverted = false;
public static final boolean kBackRightDriveInverted = false;
```

### Turn Motor Inversions

If modules turn the wrong direction (optimize goes the long way):

```java
public static final boolean kFrontLeftTurningInverted = true; // Try false
public static final boolean kFrontRightTurningInverted = true;
public static final boolean kBackLeftTurningInverted = true;
public static final boolean kBackRightTurningInverted = true;
```

## Tuning Turning PID

The turning motors use PID control to reach target angles.

### Default Values
```java
public static final double kTurningP = 1.0;
public static final double kTurningI = 0.0;
public static final double kTurningD = 0.0;
```

### If modules oscillate or don't turn smoothly:

1. **Oscillation** (wheels shake back and forth):
   - Decrease `kTurningP` (try 0.5)
   - Add some `kTurningD` (try 0.1)

2. **Slow to respond**:
   - Increase `kTurningP` (try 1.5)

3. **Overshoots target**:
   - Decrease `kTurningP`
   - Increase `kTurningD`

## Field-Oriented vs Robot-Oriented

### Field-Oriented (Default)
- **Left stick forward** always drives away from the driver
- Robot compensates for its rotation automatically
- Easier to drive from a fixed position
- Requires NavX gyro to be working

### Robot-Oriented (Hold Left Bumper)
- **Left stick forward** drives in the direction the robot is facing
- Like a car - turn the robot, then drive forward
- Useful for precision maneuvers
- Works even if gyro fails

## Resetting Gyro

Press **Right Bumper** to zero the gyro. This makes the current direction "forward" for field-oriented mode.

**When to reset:**
- At the start of each match (while robot is facing driver station)
- If the robot's "forward" drifts during a match
- After the robot has been moved or rotated while disabled

## X-Pattern (Wheel Lock)

Press **Start Button** to lock wheels in an X pattern.

**Purpose:**
- Prevents other robots from pushing you
- Useful when disabled on field
- Emergency brake for runaway robot

**To unlock:** Drive normally with the joysticks

## Common Issues

### Robot drives in wrong direction
- Check encoder offsets are correct
- Verify all wheels were pointing forward during calibration
- Try inverting drive motors

### Robot spins when trying to drive straight
- Encoder offsets are wrong - recalibrate
- One or more modules pointed wrong direction during calibration

### Modules don't turn to correct angle
- Turning PID needs tuning
- Check turn motor inversions
- Verify `kTurnGearRatio` is correct (should be 9424.0/203.0)

### Robot is slower than expected
- Check `kDriveGearRatio` matches your gearing (4.71 for L3)
- Verify wheel diameter is correct (3 inches = 0.0762m)
- Check battery voltage (low battery = slow robot)

### Field-oriented doesn't work
- NavX may not be connected or configured
- Check NavX is on MXP port
- Try robot-oriented mode (hold left bumper) to verify drive works

### Robot drifts over time in field-oriented
- Gyro drift is normal
- Reset gyro periodically (right bumper)
- Consider using vision for auto-correction

## Pre-Competition Checklist

- [ ] All swerve modules turn smoothly without oscillation
- [ ] Robot drives straight forward when commanded
- [ ] Strafing (left stick X) works correctly
- [ ] Rotation (right stick X) works correctly
- [ ] Field-oriented mode works (forward always away from driver)
- [ ] Robot-oriented mode works (left bumper held)
- [ ] Gyro reset works (right bumper)
- [ ] X-pattern lock works (start button)
- [ ] All encoder offsets are calibrated and saved
- [ ] All CAN IDs are correct
- [ ] NavX is connected and calibrated
- [ ] Battery is fully charged (>12.5V)

## Useful Dashboard Values to Add

Add these to `SwerveDriveSubsystem.periodic()` for debugging:

```java
// Module angles
SmartDashboard.putNumber("FL Angle", m_frontLeft.getState().angle.getDegrees());
SmartDashboard.putNumber("FR Angle", m_frontRight.getState().angle.getDegrees());
SmartDashboard.putNumber("BL Angle", m_backLeft.getState().angle.getDegrees());
SmartDashboard.putNumber("BR Angle", m_backRight.getState().angle.getDegrees());

// Module speeds
SmartDashboard.putNumber("FL Speed", m_frontLeft.getState().speedMetersPerSecond);
SmartDashboard.putNumber("FR Speed", m_frontRight.getState().speedMetersPerSecond);
SmartDashboard.putNumber("BL Speed", m_backLeft.getState().speedMetersPerSecond);
SmartDashboard.putNumber("BR Speed", m_backRight.getState().speedMetersPerSecond);

// Gyro
SmartDashboard.putNumber("Gyro Angle", getHeading());
SmartDashboard.putNumber("Gyro Rate", getTurnRate());

// Odometry
SmartDashboard.putNumber("Robot X", getPose().getX());
SmartDashboard.putNumber("Robot Y", getPose().getY());
SmartDashboard.putNumber("Robot Angle", getPose().getRotation().getDegrees());
```

This will help you debug issues during calibration and competition!
