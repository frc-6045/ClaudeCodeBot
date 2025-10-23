# Known Issues & Limitations

This document lists known issues, limitations, and things that could break with the robot code.

**Note**: All critical safety and configuration issues have been fixed. See [CODE_REVIEW_FINDINGS.md](CODE_REVIEW_FINDINGS.md) for details on what was fixed.

---

## ⚠️ HIGH Priority - Must Fix Before Competition

### 1. PID Values Are Placeholders
**Impact**: HIGH - Robot will not perform well

**Problem**:
- All PID constants are set to safe default values (P=0.1)
- Arm will likely be sluggish or not reach targets properly
- Climber PID also needs tuning
- These MUST be tuned for your specific robot

**Solution** - NOW EASIER WITH LIVE TUNING:
- Use **Live PID Tuning** via SmartDashboard - no code redeployment needed!
- See [LIVE_PID_TUNING.md](LIVE_PID_TUNING.md) for complete walkthrough
- Adjust values in SmartDashboard, see results instantly
- Save final values to Constants.java when done
- Also see [TUNING_GUIDE.md](TUNING_GUIDE.md) for theory

**Estimated time**: 30-60 minutes with live tuning (was 1-2 hours)

**Files to modify**: [Constants.java](src/main/java/frc/robot/Constants.java) (after finding values)

---

### 2. Position Setpoints Are Arbitrary
**Impact**: HIGH - Arm movements won't work correctly

**Problem**:
- Encoder values for scoring positions are made up
- Your robot's geometry will be different
- Arm could move to wrong positions or crash into itself
- Swerve module offsets need calibration

**Solution**:
- Manually move arm to each position
- Record actual encoder values from dashboard
- Update Constants.java with real values
- Follow [SWERVE_CALIBRATION.md](SWERVE_CALIBRATION.md) for swerve

**Files to modify**: [Constants.java](src/main/java/frc/robot/Constants.java)

---

### 3. Swerve Encoder Offsets Must Be Calibrated
**Impact**: HIGH - Drive will not work correctly

**Problem**:
- Swerve module encoder offsets are set to 0.0
- Wheels will not point in correct directions
- Robot will not drive straight

**Solution**:
- Follow [SWERVE_CALIBRATION.md](SWERVE_CALIBRATION.md) step-by-step
- Physically align wheels, measure offsets
- Update Constants.DriveConstants encoder offset values

**Files to modify**: [Constants.java](src/main/java/frc/robot/Constants.java)

---

## MEDIUM Priority - Optional Improvements

### 4. ✅ FIXED - Time-Based Autonomous Replaced with Encoder Feedback
**Status**: FIXED

**What was improved**:
- Created `DriveDistance` command that uses swerve encoders
- All autonomous routines now use encoder-based driving
- Much more reliable than time-based commands
- Auto routines use arm position feedback

**Note**: Distances (3m, 2m) are estimates and may need adjustment based on field testing

---

### 5. ✅ FIXED - Autonomous Arm Homing Now Uses Current Detection
**Status**: FIXED

**What was improved**:
- AutoHomeArm now monitors motor current to detect hard stop
- 5-cycle confirmation (100ms) prevents false positives
- Still has 5-second timeout as safety fallback
- More reliable than pure timeout method

**Note**: Current threshold (15A) may need tuning based on your arm's mechanical resistance

---

## LOW Priority - Nice to Have

### 6. No Unit Tests
**Problem**: No automated testing of robot code

**Impact**: Harder to catch bugs before deployment

**Solution**: Add JUnit tests for command logic

---

### 7. No Vision Targeting
**Problem**: No Limelight integration for auto-aiming

**Impact**: Manual alignment required for scoring

**Note**: Limelight constants exist in Constants.java but not implemented

---

### 8. No Path Following
**Problem**: Can't follow complex autonomous paths

**Impact**: Limited to simple autonomous routines

**Note**: Would require PathPlanner or Trajectory integration

---

### 9. Manual Homing Required Every Boot
**Problem**: Arm must be homed by operator every time robot boots

**Impact**: Extra step before each match

**Note**: Would require absolute encoders (e.g., CTRE CANcoder) to fix properly

---

## Limitations by Design

These are not bugs, but architectural limitations:

- **Relative encoders only**: Arm position lost on reboot (requires manual homing)
- **No vision processing**: Manual aiming required
- **Simple autonomous**: Time-based commands only
- **No auto-balance**: Would require gyro pitch/roll sensing
- **Single-piece capacity**: Intake can only hold one coral at a time

---

## Things That Will Definitely Break If...

❌ **You forget to home the arm** → Arm won't move, autonomous will fail

❌ **PID values aren't tuned** → Arm oscillates violently or doesn't move

❌ **Position values aren't calibrated** → Arm goes to wrong places

❌ **Swerve offsets aren't calibrated** → Robot won't drive straight

❌ **Motor inversions are wrong** → Robot drives backward, intake ejects instead

❌ **CAN IDs don't match** → Motors don't respond, or wrong motors move

❌ **Battery voltage is too low (<10V)** → Brownouts, erratic behavior

❌ **Current limits are too high** → Brownouts during heavy use

❌ **You run autonomous without testing** → Unpredictable behavior

❌ **CAN bus wiring is loose** → Intermittent failures, encoder resets

❌ **You deploy without building first** → Old code runs, confusing behavior

---

## Pre-Competition Checklist

Use this to avoid most of the above issues:

### Critical (Must Do)
- [ ] **Swerve encoder offsets calibrated** (SWERVE_CALIBRATION.md)
- [ ] **All PID values tuned** (TUNING_GUIDE.md)
- [ ] **All position setpoints calibrated** (record from dashboard)
- [ ] **Arm homed in Teleop before running Auto**
- [ ] **Battery fully charged** (>12.5V)

### Important (Should Do)
- [ ] Motor inversions verified (drives forward when commanded forward)
- [ ] All CAN IDs verified (each motor responds correctly)
- [ ] Current limits tested (no brownouts during full operation)
- [ ] All autonomous routines tested in practice
- [ ] Controllers paired and ports verified
- [ ] Beam break sensor working (test with object)

### Safety (Always Do)
- [ ] CAN bus wiring checked (all connections tight)
- [ ] Latest code deployed (check timestamp in Driver Station)
- [ ] All mechanical hard stops in place
- [ ] Safety glasses on for testing! 👓

---

## When Things Go Wrong

### Robot won't enable
- Check battery voltage (must be >7V)
- Check for code errors in Driver Station console
- Verify RoboRIO has power light
- Check controller connections (both driver and operator)

### Motors don't work
- Check CAN bus (green lights on SPARK MAX?)
- Verify motor controllers are powered
- Check for CAN errors in Driver Station
- Verify CAN IDs match Constants.java

### Arm oscillates wildly
- **DISABLE IMMEDIATELY**
- P value is too high
- Decrease kArmP by 50% and retry
- Follow tuning guide carefully

### Robot brownouts
- Lower current limits in Constants.java
- Charge battery fully
- Don't run too many motors at once
- Check battery connections

### Encoder values reset randomly
- Check CAN wiring (loose connections)
- Home the arm again
- May need better wire management
- Verify SPARK MAX firmware is up to date

### Swerve drives weird / won't go straight
- Calibrate encoder offsets (SWERVE_CALIBRATION.md)
- Check wheel inversions in Constants.java
- Verify gyro is working (check dashboard)
- Zero gyro heading with right bumper

### Controllers don't work
- Check dashboard for connection warnings
- Verify correct USB ports (0 = driver, 1 = operator)
- Re-pair controllers with Driver Station
- Check for controller swap message in console

---

## Getting Help

If you encounter an issue not listed here:
1. Check Driver Station console for error messages
2. Enable message logging in DriverStation
3. Check SmartDashboard for telemetry data
4. Review WPILib documentation
5. Post on Chief Delphi (cd.chiefdelphi.com)
6. Contact FIRST technical support

---

## What's Already Fixed ✅

All critical safety and configuration issues have been resolved:
- ✅ All 14 motor controllers properly configured (CAN timeout, voltage comp, brake mode)
- ✅ Current spike detection (prevents motor burnout)
- ✅ Collision detection for arm (prevents frame crashes)
- ✅ Brownout protection (battery monitoring)
- ✅ Controller validation (warns if disconnected/swapped)
- ✅ Exception handling (detailed error messages)
- ✅ Manual arm control safety limits
- ✅ Null pointer safety in autonomous
- ✅ Comprehensive telemetry

See [CODE_REVIEW_FINDINGS.md](CODE_REVIEW_FINDINGS.md) for complete list of fixes.

---

## Contributing

If you fix any of these issues, please:
1. Document the fix
2. Update this file
3. Update CODE_REVIEW_FINDINGS.md if applicable
4. Share with the community!
