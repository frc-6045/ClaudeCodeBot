# Known Issues & Limitations

This document lists known issues, limitations, and things that could break with the robot code.

## ✅ FIXED Issues

### 1. ✅ FIXED - Autonomous Arm Homing
**Status**: PARTIALLY FIXED

**Solution Applied**:
- Added `AutoHomeArm` command that can run at start of autonomous
- Added warnings in autonomous routines if arm not homed
- Telemetry added to show homing status on dashboard

**Remaining Limitation**:
- Auto-home uses timeout method (needs testing)
- For best results: Add absolute encoders or limit switches
- Or manually home in teleop before autonomous

### 2. ✅ FIXED - Gradle Wrapper JAR
**Status**: FIXED

**Solution**:
- gradle-wrapper.jar now included in gradle/wrapper/
- `./gradlew` commands will now work
- Fixed settings.gradle compatibility issue

### 3. ✅ FIXED - CAN Timeout Protection
**Status**: FIXED

**Solution Applied**:
- All motor controllers now have `setCANTimeout(100)` configured
- Motors will stop if no CAN signal for 100ms
- Built-in SPARK MAX safety features activated

### 4. ⚠️ PID Values Are Placeholders
**Impact**: HIGH - Robot will not perform well

**Problem**:
- All PID constants are set to default/placeholder values
- Arm will likely oscillate or not reach targets properly
- These MUST be tuned for your specific robot

**Solution**:
- Follow [TUNING_GUIDE.md](TUNING_GUIDE.md) carefully
- Start with small P values and increase gradually
- Test in safe environment before competition

### 5. ⚠️ Position Setpoints Are Arbitrary
**Impact**: HIGH - Arm movements won't work correctly

**Problem**:
- Encoder values for scoring positions are made up
- Your robot's geometry will be different
- Arm could move to wrong positions or crash into itself

**Solution**:
- Manually move arm to each position
- Record actual encoder values from dashboard
- Update Constants.java with real values

### 6. ⚠️ No Collision Detection
**Impact**: MEDIUM - Arm could hit frame

**Problem**:
- Software limits are single-axis (arm angle, extension)
- No protection against arm hitting the robot frame when extended
- No "keep-out zones" for dangerous configurations

**Solution** (for future):
- Add compound limit checks (e.g., "if extended, limit arm angle")
- Calculate arm tip position and check against frame geometry
- Use 2D kinematics to enforce safe workspace

## Medium Issues

### 7. ✅ FIXED - Battery Voltage Compensation in Drive
**Status**: FIXED

**Solution Applied**:
- Voltage compensation enabled on all swerve drive and turning motors
- Set to 12.0V nominal voltage
- Ensures consistent performance regardless of battery charge level

**Impact**: Drive speed now consistent throughout match

### 8. ✅ FIXED - Ramp Rate Limiting on Drive
**Status**: FIXED

**Solution Applied**:
- SlewRateLimiters implemented in SwerveDriveSubsystem
- Magnitude slew rate: 1.8 (0 to 100% in 0.56s)
- Rotational slew rate: 2.0
- Prevents wheel slip and tipping during aggressive inputs

**Impact**: Smooth, controlled acceleration

### 9. No Current Spike Detection
**Problem**: Can't detect if mechanism is jammed

**Impact**: Motors could burn out trying to move jammed mechanism

**Solution**: Monitor motor current in periodic(), stop if exceeds threshold

### 10. Simple Time-Based Autonomous
**Problem**: Autonomous routines use only timers, no sensor feedback

**Impact**: Unreliable scoring, can't adapt to field conditions

**Solution**: Add sensor feedback (encoders, vision, etc.) for robust auto

## Minor Issues

### 11. ✅ FIXED - Telemetry/Logging
**Status**: FIXED

**Solution Applied**:
- Swerve: Gyro angle, robot position, gyro connection status
- Arm: Position, homing status, limits, target position
- Intake: Game piece detection, state
- Robot: Battery voltage, total current draw, brownout warnings

All telemetry published to SmartDashboard for debugging

### 12. No Unit Tests
**Problem**: No automated testing of robot code

**Impact**: Harder to catch bugs before deployment

**Solution**: Add JUnit tests for command logic

### 13. Hard-Coded Controller Ports
**Problem**: If controllers swap ports, controls break

**Impact**: Confusing during competition

**Solution**: Add port checking or dynamic assignment

### 14. ✅ FIXED - Brownout Protection
**Status**: FIXED

**Solution Applied**:
- Battery voltage monitoring in Robot.periodic()
- Warning at <11.5V, critical warning at <10.5V
- Dashboard indicator for low battery
- Total current draw monitoring via Power Distribution
- Console warnings when brownout risk detected

## Limitations by Design

### 15. ✅ IMPLEMENTED - Swerve Drive
**Status**: IMPLEMENTED

**Current Implementation**:
- REV MAXSwerve modules with field-oriented control
- NavX gyroscope for heading
- Requires encoder calibration (see SWERVE_CALIBRATION.md)
- L3 gearing for speed

### 16. No Vision Targeting
**Problem**: No Limelight integration for auto-aiming

**Impact**: Manual alignment required

**Note**: Limelight constants exist but not implemented

### 17. No Path Following
**Problem**: Can't follow complex autonomous paths

**Impact**: Simple autonomous only

**Note**: Would require PathPlanner or Trajectory integration

### 18. Manual Homing Only
**Problem**: Arm must be homed by operator every boot

**Impact**: Extra step before each match

**Note**: Would require absolute encoders to fix properly

## Things That Will Definitely Break If...

❌ **You forget to home the arm** → Arm won't move, autonomous will fail

❌ **PID values aren't tuned** → Arm oscillates violently or doesn't move

❌ **Position values aren't calibrated** → Arm goes to wrong places

❌ **Motor inversions are wrong** → Robot drives backward, intake ejects instead

❌ **CAN IDs don't match** → Motors don't respond, or wrong motors move

❌ **Battery voltage is too low (<10V)** → Brownouts, erratic behavior

❌ **Current limits are too high** → Brownouts during heavy use

❌ **You run autonomous without testing** → Unpredictable behavior

❌ **CAN bus wiring is loose** → Intermittent failures, encoder resets

❌ **You deploy without building first** → Old code runs, confusing behavior

## Pre-Competition Checklist

Use this to avoid most of the above issues:

- [ ] Arm has been homed in Teleop before running Auto
- [ ] All PID values have been tuned
- [ ] All position setpoints have been calibrated
- [ ] Motor inversions verified (robot drives forward when commanded forward)
- [ ] All CAN IDs verified (each motor responds correctly)
- [ ] Battery fully charged (>12.5V)
- [ ] Current limits tested (no brownouts during full operation)
- [ ] All autonomous routines tested in practice
- [ ] CAN bus wiring checked (all connections tight)
- [ ] Latest code deployed (check timestamp in Driver Station)
- [ ] Controllers paired and ports verified
- [ ] Beam break sensor working (test with object)
- [ ] All mechanical hard stops in place
- [ ] Safety glasses on for testing! 👓

## When Things Go Wrong

### Robot won't enable
- Check battery voltage (must be >7V)
- Check for code errors in Driver Station console
- Verify RoboRIO has power light

### Motors don't work
- Check CAN bus (green lights on SPARK MAX?)
- Verify motor controllers are powered
- Check for CAN errors in Driver Station

### Arm oscillates wildly
- **DISABLE IMMEDIATELY**
- P value is too high
- Decrease kArmP by 50% and retry

### Robot brownouts
- Lower current limits in Constants.java
- Charge battery fully
- Don't run too many motors at once

### Encoder values reset randomly
- Check CAN wiring (loose connections)
- Home the arm again
- May need better wire management

## Getting Help

If you encounter an issue not listed here:
1. Check Driver Station console for error messages
2. Enable message logging in DriverStation
3. Review WPILib documentation
4. Post on Chief Delphi (cd.chiefdelphi.com)
5. Contact FIRST technical support

## Contributing

If you fix any of these issues, please:
1. Document the fix
2. Update this file
3. Share with the community!
