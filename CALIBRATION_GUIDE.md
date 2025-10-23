# Robot Calibration Guide

This guide walks you through all calibration steps needed before your robot is competition-ready.

**⚠️ CRITICAL**: The robot will NOT work properly until these calibrations are complete!

---

## Table of Contents

1. [Swerve Module Calibration](#1-swerve-module-calibration) ⭐ **CRITICAL**
2. [Arm Position Calibration](#2-arm-position-calibration) ⭐ **CRITICAL**
3. [PID Tuning](#3-pid-tuning) ⭐ **CRITICAL**
4. [Autonomous Distance Tuning](#4-autonomous-distance-tuning)
5. [Current Threshold Tuning](#5-current-threshold-tuning)
6. [Final Verification](#6-final-verification)

---

## 1. Swerve Module Calibration ⭐ CRITICAL

**Why**: Swerve wheels won't point in the correct direction without calibration.

**See**: [SWERVE_CALIBRATION.md](SWERVE_CALIBRATION.md) for detailed steps.

**Quick Summary**:
1. Manually align all wheels to point straight forward
2. Read encoder positions from SmartDashboard
3. Update `Constants.DriveConstants`:
   ```java
   public static final double kFrontLeftEncoderOffset = 0.0;  // Replace with actual value
   public static final double kFrontRightEncoderOffset = 0.0; // Replace with actual value
   public static final double kBackLeftEncoderOffset = 0.0;   // Replace with actual value
   public static final double kBackRightEncoderOffset = 0.0;  // Replace with actual value
   ```
4. Re-deploy code and test

**Time Required**: 15-20 minutes

---

## 2. Arm Position Calibration ⭐ CRITICAL

**Why**: Arm will move to wrong positions if encoder values are incorrect.

### Step 1: Home the Arm
1. Deploy code to robot
2. In teleop, use D-Pad to manually move arm to stowed position
3. Press **D-Pad Right** to home the arm
4. Verify "ARM HOMED" message in console

### Step 2: Record Actual Positions

**🎉 NEW: Built-in Calibration Helper!**

The robot now has a **RecordArmPosition** command that makes this easy:

1. Manually move arm to desired position
2. Press **Operator D-Pad Left**
3. Check console output - it prints the Constants.java code for you!
4. Copy and paste into Constants.java

**Example output**:
```
==================================================
📍 POSITION RECORDED: CURRENT
==================================================
Arm Angle:  152.34 encoder ticks
Extension:  0.00 encoder ticks

Add to Constants.java:
public static final double kCURRENTPosition = 152.34;
==================================================
```

For each scoring position, follow these steps:

**STOWED Position:**
1. Arm should already be at home (stowed) position
2. SmartDashboard should show:
   - `Arm/Angle Position: 0.0`
   - `Arm/Extension Position: 0.0`
3. This is your reference point

**INTAKE Position:**
1. Manually move arm to intake position (arm down, ready to pick up coral)
2. Record values from SmartDashboard:
   - `Arm/Angle Position: _____`
   - `Arm/Extension Position: _____`

**LEVEL_1 Position:**
1. Manually move arm to Level 1 scoring position
2. Measure from game piece to ground (should match L1 height)
3. Record encoder values from SmartDashboard

**LEVEL_2 Position:**
1. Manually move arm to Level 2 scoring position
2. Measure and verify height
3. Record encoder values

**LEVEL_3 Position:**
1. Extend arm fully first (important for collision detection)
2. Move arm angle to Level 3 height
3. Verify arm doesn't hit robot frame
4. Record encoder values

**LEVEL_4 Position:**
1. Extend arm fully
2. Move arm angle to Level 4 height
3. Verify clearance
4. Record encoder values

### Step 3: Update Constants.java

Open `src/main/java/frc/robot/Constants.java` and update:

```java
public static class ArmConstants {
  // Positions - UPDATE THESE WITH RECORDED VALUES!
  public static final double kStowedPosition = 0;      // Always 0 (home position)
  public static final double kIntakePosition = 50;     // ← Replace with recorded value
  public static final double kLevel1Position = 100;    // ← Replace with recorded value
  public static final double kLevel2Position = 200;    // ← Replace with recorded value
  public static final double kLevel3Position = 300;    // ← Replace with recorded value
  public static final double kLevel4Position = 400;    // ← Replace with recorded value

  // Extension positions
  public static final double kRetractedPosition = 0;   // Always 0 (retracted)
  public static final double kExtendedPosition = 500;  // ← Replace with recorded value

  // Safety limits - set based on your observations
  public static final double kMinArmPosition = -10;    // Slightly below home
  public static final double kMaxArmPosition = 450;    // Slightly above max position
  public static final double kMaxExtensionPosition = 550; // Slightly above max extension

  // Collision detection limits (when extended)
  public static final double kMinArmPositionExtended = 50;  // Don't let arm go too low when extended
  public static final double kMaxArmPositionExtended = 450; // Don't let arm go too high when extended
}
```

### Step 4: Verify
1. Re-deploy code
2. Home arm again
3. Test each position using operator controller buttons
4. Verify arm goes to correct heights

**Time Required**: 30-40 minutes

---

## 3. PID Tuning ⭐ CRITICAL

**Why**: Without tuned PID, motors will oscillate wildly or not reach targets.

**See**: [TUNING_GUIDE.md](TUNING_GUIDE.md) for detailed tuning procedure.

### What Needs Tuning:

1. **Arm Angle PID** (`kArmP`, `kArmI`, `kArmD`, `kArmFF`)
2. **Arm Extension PID** (`kExtensionP`, `kExtensionI`, `kExtensionD`, `kExtensionFF`)
3. **Climber PID** (`kClimberP`, `kClimberI`, `kClimberD`)
4. **Swerve Turning PID** (`kTurningP`, `kTurningI`, `kTurningD`)

### Quick Tuning Steps:

For each subsystem:

1. **Start with P = 0.1, I = 0, D = 0**
2. Command motor to move to target position
3. Observe behavior:
   - **No movement**: Increase P
   - **Oscillates**: Decrease P
   - **Steady-state error**: Add small I (0.001)
   - **Overshoots**: Add D (start with P/10)
4. Repeat until smooth motion with no oscillation
5. Update Constants.java with final values

**⚠️ SAFETY**:
- Start with LOW P values
- Have disable button ready
- If violent oscillation occurs, **DISABLE IMMEDIATELY** and reduce P by 50%

**Time Required**: 1-2 hours (most time-consuming step)

---

## 4. Autonomous Distance Tuning

**Why**: Autonomous driving distances are estimates and need field-specific adjustment.

### Current Values (in DriveDistance commands):
- `getLeaveAuto()`: 3.0 meters
- `getScoreAndLeaveAuto()`: 3.0 meters backward
- `getTwoPieceAuto()`: 2.0 meters forward, 2.0 meters backward

### How to Tune:

1. **Setup**:
   - Place robot at starting position
   - Mark target distance with tape
   - Enable autonomous mode

2. **Test Drive Distance**:
   - Run "Leave Community" autonomous
   - Measure actual distance traveled
   - Calculate error: `actual - target`

3. **Adjust**:
   - If robot travels too far: decrease distance value
   - If robot doesn't travel far enough: increase distance value
   - Example: If you want 3.0m but robot only goes 2.7m, change to 3.3m

4. **Update RobotContainer.java**:
   ```java
   new DriveDistance(m_drive, 3.3, 0.5), // Adjusted from 3.0
   ```

5. **Re-test** until consistent

### Factors Affecting Distance:
- Carpet vs. hard floor
- Battery voltage
- Wheel wear
- Encoder calibration quality

**Time Required**: 20-30 minutes per autonomous routine

---

## 5. Current Threshold Tuning

**Why**: Current thresholds vary based on mechanism weight and friction.

### AutoHomeArm Current Threshold

**Default**: 15A

**How to tune**:
1. Watch SmartDashboard during homing:
   - `Arm/Arm Current` shows live current draw
2. Observe current when arm hits hard stop
3. Adjust threshold in AutoHomeArm.java:
   ```java
   private static final double CURRENT_THRESHOLD = 15.0; // Adjust this
   ```
4. Threshold should be:
   - **Higher than normal operation** (avoid false triggers)
   - **Lower than stall current** (detect stop quickly)
   - Good starting point: 1.5x normal current

### Jam Detection Thresholds

**Arm motors**: 35A (in ArmSubsystem.java)
**Intake motors**: 25A (in IntakeSubsystem.java)

**How to tune**:
1. Observe normal operation currents on SmartDashboard
2. Manually jam mechanism (carefully!)
3. Note current spike value
4. Set threshold between normal and jam currents
5. Update in respective subsystem files

**Time Required**: 15-20 minutes

---

## 6. Final Verification

Before considering calibration complete, verify:

### Drive System
- [ ] Robot drives straight when commanded forward
- [ ] Robot strafes correctly (perpendicular to forward)
- [ ] Robot rotates smoothly
- [ ] Field-oriented mode works (robot moves relative to field)
- [ ] No unusual noises or jerking

### Arm System
- [ ] Arm homes successfully (manual or auto)
- [ ] All position buttons move arm to correct heights
- [ ] No oscillation at target positions
- [ ] Arm stops smoothly (no overshoot)
- [ ] Collision detection prevents frame crashes
- [ ] Manual control respects limits

### Intake System
- [ ] Beam break sensor detects game pieces
- [ ] Auto-transition to holding works
- [ ] Outtake ejects game pieces

### Climber System
- [ ] Extends and retracts smoothly
- [ ] Both sides stay synchronized
- [ ] No violent motion

### Autonomous
- [ ] Auto homing works reliably
- [ ] Drive distances are accurate
- [ ] Scoring positions are correct
- [ ] All autonomous routines complete successfully

---

## Calibration Checklist

Print this and check off as you complete each step:

### Pre-Calibration
- [ ] Code deployed successfully
- [ ] All motor controllers show green lights (CAN connected)
- [ ] Controllers connected (check SmartDashboard)
- [ ] Battery fully charged (>12.5V)
- [ ] Safety glasses on

### Swerve Calibration
- [ ] All wheels aligned forward
- [ ] Encoder offsets recorded
- [ ] Constants.java updated
- [ ] Code re-deployed
- [ ] Drive test passed

### Arm Calibration
- [ ] Arm homed successfully
- [ ] STOWED position verified (0, 0)
- [ ] INTAKE position recorded
- [ ] LEVEL_1 position recorded
- [ ] LEVEL_2 position recorded
- [ ] LEVEL_3 position recorded
- [ ] LEVEL_4 position recorded
- [ ] Extension positions recorded
- [ ] Safety limits verified
- [ ] Constants.java updated
- [ ] Code re-deployed
- [ ] Position test passed

### PID Tuning
- [ ] Arm angle PID tuned
- [ ] Arm extension PID tuned
- [ ] Climber PID tuned
- [ ] Swerve turning PID tuned
- [ ] Constants.java updated
- [ ] Code re-deployed
- [ ] All movements smooth

### Autonomous Tuning
- [ ] Leave Auto distance tuned
- [ ] Score and Leave distance tuned
- [ ] Two Piece Auto distances tuned
- [ ] Auto homing threshold tuned
- [ ] Code re-deployed
- [ ] All autos tested

### Final Verification
- [ ] All checklist items above passed
- [ ] Robot operates smoothly
- [ ] No error messages in console
- [ ] Ready for competition!

---

## Troubleshooting

**Swerve won't drive straight**:
- Re-check encoder offsets
- Verify all modules point same direction when aligned
- Check for mechanical issues

**Arm oscillates**:
- Reduce P value by 50%
- Add D term (try P/10)
- Check mechanical friction

**Positions are close but not exact**:
- Fine-tune encoder values in Constants.java
- Consider mechanical slop/backlash
- Verify arm is fully homed before testing

**Auto homing times out**:
- Check mechanical hard stop exists
- Lower current threshold
- Verify motor isn't stalling early

**Encoders reset randomly**:
- Check CAN wiring
- Verify SPARK MAX firmware is up to date
- Check for loose connections

---

## Time Estimate

**Total calibration time**: 3-4 hours for first-time calibration

**Breakdown**:
- Swerve: 20 min
- Arm positions: 40 min
- PID tuning: 1-2 hours
- Autonomous: 30 min
- Current thresholds: 20 min
- Testing/verification: 30 min

**TIP**: Have multiple team members work on different subsystems simultaneously to save time!

---

## After Calibration

Once calibration is complete:

1. **Document your values**:
   - Take screenshots of working Constants.java
   - Write down all tuned values
   - Note any special procedures

2. **Back up your code**:
   - Commit to git: `git add . && git commit -m "Final calibrated values"`
   - Push to GitHub (auto-push is enabled)
   - Save a copy elsewhere

3. **Create a quick reference card**:
   - PID values
   - Position setpoints
   - Any special startup procedures

4. **Practice!**:
   - Run through all operations
   - Train all drivers
   - Practice autonomous routines

---

**Good luck at competition!** 🏆
