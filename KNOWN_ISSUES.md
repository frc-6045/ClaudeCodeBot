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
- PathPlanner PID values need tuning
- These MUST be tuned for your specific robot

**Solution** - NOW EASIER WITH LIVE TUNING:
- Use **Live PID Tuning** via SmartDashboard - no code redeployment needed!
- See [LIVE_PID_TUNING.md](LIVE_PID_TUNING.md) for complete walkthrough
- Adjust values in SmartDashboard, see results instantly
- Save final values to Constants.java when done
- Also see [TUNING_GUIDE.md](TUNING_GUIDE.md) for theory

**Estimated time**: 30-60 minutes with live tuning (was 1-2 hours)

**Files to modify**:
- [Constants.java](src/main/java/frc/robot/Constants.java) (after finding values)
- [PathPlannerConfig.java](src/main/java/frc/robot/PathPlannerConfig.java) (for autonomous)

---

### 2. Position Setpoints Need Calibration
**Impact**: HIGH - Arm movements won't work correctly

**Problem**:
- Encoder values for scoring positions are placeholder values
- Your robot's geometry will be different
- Arm could move to wrong positions or crash into itself
- Swerve module offsets need calibration

**Solution** - NOW EASIER WITH HELPER COMMANDS:
- Use **Operator D-Pad Left** to record arm positions (prints code to copy/paste)
- Use **Driver Back button** to print swerve offsets (prints code to copy/paste)
- Follow [SWERVE_CALIBRATION.md](SWERVE_CALIBRATION.md) for swerve
- Follow [CALIBRATION_GUIDE.md](CALIBRATION_GUIDE.md) for complete walkthrough

**Files to modify**: [Constants.java](src/main/java/frc/robot/Constants.java)

---

### 3. Vision Constants Need Calibration
**Impact**: MEDIUM - Vision targeting won't work accurately

**Problem**:
- Limelight mount height and angle are placeholder values
- Target height is an estimate
- Vision subsystem is implemented but not wired into RobotContainer
- Auto-aim won't be accurate without proper calibration

**Solution**:
1. Measure actual Limelight height from floor
2. Measure Limelight mount angle
3. Measure target height
4. Update VisionConstants in Constants.java
5. Wire VisionSubsystem into RobotContainer
6. Bind vision commands to controller buttons

**Files to modify**:
- [Constants.java](src/main/java/frc/robot/Constants.java) (VisionConstants section)
- [RobotContainer.java](src/main/java/frc/robot/RobotContainer.java) (add vision bindings)

---

## Limitations by Design

These are not bugs, but architectural limitations:

- ~~**Relative encoders only**: Arm position lost on reboot (requires manual homing)~~ ✅ FIXED with AutoHomeArm
- ~~**No vision processing**: Manual aiming required~~ ✅ FIXED with VisionSubsystem
- ~~**Simple autonomous**: Time-based commands only~~ ✅ FIXED with PathPlanner and encoder-based driving
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

### Critical Safety & Configuration
- ✅ All 14 motor controllers properly configured (CAN timeout, voltage comp, brake mode)
- ✅ Current spike detection (prevents motor burnout)
- ✅ Collision detection for arm (prevents frame crashes)
- ✅ Brownout protection (battery monitoring)
- ✅ Controller validation (warns if disconnected/swapped)
- ✅ Exception handling (detailed error messages)
- ✅ Manual arm control safety limits
- ✅ Null pointer safety in autonomous
- ✅ Comprehensive telemetry

### Advanced Features Added
- ✅ **Unit Tests** - Comprehensive test suite for Constants and commands ([src/test/](src/test/java/frc/robot/))
- ✅ **Limelight Vision** - Full vision targeting subsystem with auto-aim ([VisionSubsystem.java](src/main/java/frc/robot/subsystems/VisionSubsystem.java))
- ✅ **PathPlanner Integration** - Complex autonomous path following ([PathPlannerConfig.java](src/main/java/frc/robot/PathPlannerConfig.java))
- ✅ **Auto-Homing** - Automatic arm homing with current detection ([AutoHomeArm.java](src/main/java/frc/robot/commands/AutoHomeArm.java))
- ✅ **Encoder-Based Driving** - Autonomous uses encoder feedback, not time-based ([DriveDistance.java](src/main/java/frc/robot/commands/DriveDistance.java))
- ✅ **Live PID Tuning** - Tune PID values via SmartDashboard without redeploying
- ✅ **Calibration Helpers** - One-button commands to record positions and offsets

See [CODE_REVIEW_FINDINGS.md](CODE_REVIEW_FINDINGS.md) for complete list of fixes.

---

## Contributing

If you fix any of these issues, please:
1. Document the fix
2. Update this file
3. Update CODE_REVIEW_FINDINGS.md if applicable
4. Share with the community!
