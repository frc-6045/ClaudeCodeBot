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

### 2. ✅ FIXED - Helper Commands for Position Calibration
**Status**: FIXED

**What was improved**:
- Created `RecordArmPosition` command to easily capture encoder values
- Bound to **Operator D-Pad Left** for quick access during testing
- Command prints formatted output directly to console AND SmartDashboard
- Shows exact Constants.java code to copy/paste
- No more guessing - just move arm to position and press button!

**How to use**:
1. Move arm manually (D-Pad Up/Down) to desired position
2. Press **Operator D-Pad Left** to record values
3. Copy the printed Constants.java code from console
4. Update [Constants.java](src/main/java/frc/robot/Constants.java)

**Note**: Position values still need to be calibrated for your specific robot, but now it's MUCH easier!

---

### 3. ✅ FIXED - Helper Command for Swerve Calibration
**Status**: FIXED

**What was improved**:
- Created `PrintSwerveOffsets` command to print encoder offsets
- Bound to **Driver Back button** for easy access
- Command prints formatted output to console AND SmartDashboard
- Shows exact Constants.java code to copy/paste
- Follow [SWERVE_CALIBRATION.md](SWERVE_CALIBRATION.md) for full walkthrough

**How to use**:
1. Manually rotate all wheels to point straight forward
2. Press **Driver Back button** to print offsets
3. Copy the printed Constants.java code from console
4. Update [Constants.java](src/main/java/frc/robot/Constants.java)

**Note**: Offsets still need to be measured, but the helper makes it trivial!

---

## MEDIUM Priority - Optional Improvements

(All items in this section have been fixed! See "What's Already Fixed ✅" section below.)

---

## LOW Priority - Nice to Have

### 6. ✅ FIXED - Unit Tests Added
**Status**: FIXED

**What was improved**:
- Created comprehensive unit tests for Constants validation
- Added tests for DriveDistance command logic
- Tests validate CAN IDs, current limits, PID constants, speed limits
- JUnit already configured in build.gradle
- Run tests with: `./gradlew test`

**Location**: [src/test/java/frc/robot/](src/test/java/frc/robot/)

---

### 7. ✅ FIXED - Limelight Vision Targeting Implemented
**Status**: FIXED

**What was improved**:
- Created `VisionSubsystem` with full Limelight integration
- Auto-aim, distance calculation, target detection
- Vision-assisted driving commands
- LED control to save battery
- Helper methods: `isAligned()`, `getDistanceToTarget()`, `getSteeringAdjustment()`

**Location**:
- [VisionSubsystem.java](src/main/java/frc/robot/subsystems/VisionSubsystem.java)
- [VisionCommands.java](src/main/java/frc/robot/commands/VisionCommands.java)

**Note**: Needs to be wired into RobotContainer and Limelight height/angle calibrated for your robot

---

### 8. ✅ FIXED - PathPlanner Integration Complete
**Status**: FIXED

**What was improved**:
- PathPlanner library added to vendordeps
- `PathPlannerConfig` class for holonomic drive configuration
- AutoBuilder fully configured in SwerveDriveSubsystem
- Automatic red alliance path mirroring
- `PathPlannerCommands` factory for easy path following
- Ready for complex autonomous routines!

**Location**:
- [PathPlannerConfig.java](src/main/java/frc/robot/PathPlannerConfig.java)
- [PathPlannerCommands.java](src/main/java/frc/robot/commands/PathPlannerCommands.java)
- [vendordeps/PathplannerLib.json](vendordeps/PathplannerLib.json)

**How to use**:
1. Download PathPlanner GUI app
2. Create paths and autos in GUI
3. Deploy paths to robot
4. Use `PathPlannerCommands.followPath("pathName")` in autonomous

**Note**: PID values in PathPlannerConfig will need tuning for your robot

---

### 9. ✅ FIXED - Auto-Homing with Current Detection
**Status**: FIXED

**What was improved**:
- `AutoHomeArm` command uses current spike detection
- Monitors motor current to detect mechanical hard stop
- 5-cycle confirmation (100ms) prevents false positives
- 5-second timeout as safety fallback
- Perfect for autonomous - no operator needed!
- Can be used in autonomous init to auto-home the arm

**Location**: [AutoHomeArm.java](src/main/java/frc/robot/commands/AutoHomeArm.java)

**How to use**:
```java
// In autonomous init or at start of auto routine
new AutoHomeArm(m_arm)
```

**Note**: Current threshold (15A) may need tuning based on your arm's mechanical resistance

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
