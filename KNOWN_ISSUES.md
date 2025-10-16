# Known Issues & Limitations

This document lists known issues, limitations, and things that could break with the robot code.

## Critical Issues

### 1. ⚠️ Autonomous Requires Manual Arm Homing
**Impact**: HIGH - Autonomous will fail if not addressed

**Problem**:
- If the robot boots directly into autonomous mode, the arm will NOT be homed
- Autonomous routines that move the arm will fail silently
- The arm will print error messages but won't move

**Workaround**:
1. **Before competition**: Always run in Teleop mode first
2. Home the arm (Operator D-Pad Right)
3. Then disable and select autonomous

**Proper Solution** (for future):
- Add absolute encoders (like CANCoder or Through Bore Encoder)
- Or add limit switches for automatic homing
- Or force arm to home position on boot using current limiting

### 2. ⚠️ No Gradle Wrapper JAR
**Impact**: MEDIUM - Build system won't work

**Problem**:
- The gradle-wrapper.jar file is missing
- Running `./gradlew` will fail

**Solution**:
1. Install Gradle on your system
2. Run: `gradle wrapper --gradle-version 8.5`
3. Or download the wrapper JAR manually from the Gradle website
4. Or use WPILib VSCode which includes Gradle

### 3. ⚠️ No CAN Timeout Protection
**Impact**: MEDIUM - Motors could run with stale data

**Problem**:
- If CAN bus communication fails, motors might continue with last command
- No watchdog to detect stale motor controller data
- Could cause dangerous runaway conditions

**Mitigation**:
- WPILib has built-in motor safety for most cases
- SPARK MAX controllers have their own timeout protection
- But it's not explicitly configured in this code

**Solution** (for future):
- Add CAN status frame checks in periodic()
- Monitor `isAlive()` status on motor controllers
- Add explicit motor safety timeouts

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

### 7. No Battery Voltage Compensation in Drive
**Problem**: Drive speed varies with battery voltage (12V vs 10V)

**Impact**: Inconsistent autonomous, harder to control

**Mitigation**: Voltage compensation is enabled on arm/intake, but not drive

### 8. No Ramp Rate Limiting on Drive
**Problem**: Full throttle can cause wheel slip or tip the robot

**Impact**: Harder to control, potential for tipping

**Solution**: Add ramp rate limiting in DriveSubsystem if needed

### 9. No Current Spike Detection
**Problem**: Can't detect if mechanism is jammed

**Impact**: Motors could burn out trying to move jammed mechanism

**Solution**: Monitor motor current in periodic(), stop if exceeds threshold

### 10. Simple Time-Based Autonomous
**Problem**: Autonomous routines use only timers, no sensor feedback

**Impact**: Unreliable scoring, can't adapt to field conditions

**Solution**: Add sensor feedback (encoders, vision, etc.) for robust auto

## Minor Issues

### 11. No Telemetry/Logging
**Problem**: Limited data on dashboard for debugging

**Impact**: Harder to diagnose issues during matches

**Solution**: Add SmartDashboard.putNumber() calls in periodic()

### 12. No Unit Tests
**Problem**: No automated testing of robot code

**Impact**: Harder to catch bugs before deployment

**Solution**: Add JUnit tests for command logic

### 13. Hard-Coded Controller Ports
**Problem**: If controllers swap ports, controls break

**Impact**: Confusing during competition

**Solution**: Add port checking or dynamic assignment

### 14. No Brownout Protection
**Problem**: No code to reduce load when voltage drops

**Impact**: Robot could brownout during heavy use

**Solution**: Monitor battery voltage, reduce current limits when low

## Limitations by Design

### 15. No Swerve Drive
**Problem**: Tank drive is less maneuverable than swerve

**Impact**: Harder to position precisely

**Note**: This is by design - swerve is much more complex

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
