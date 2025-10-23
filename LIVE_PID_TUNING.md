# Live PID Tuning Guide

This robot code supports **live PID tuning** via SmartDashboard, allowing you to adjust PID values without redeploying code!

---

## How It Works

The robot publishes PID constants to SmartDashboard and reads them back every cycle (~20ms). When you change a value in SmartDashboard, the robot immediately updates the motor controller.

**Benefits**:
- ✅ No need to redeploy code for each PID change
- ✅ See results instantly
- ✅ Much faster tuning process
- ✅ Can save values to Constants.java when done

---

## Setup

### 1. Deploy Code
```bash
./gradlew deploy
```

### 2. Open SmartDashboard
- Launch **SmartDashboard** or **Shuffleboard** from WPILib
- Connect to your robot (10.TE.AM.2 or USB)
- Enable robot in **Teleop** mode

### 3. Find PID Values
Look for these keys in SmartDashboard:

**Arm PID:**
- `Arm/PID/kP`
- `Arm/PID/kI`
- `Arm/PID/kD`
- `Arm/PID/kFF`

**Extension PID:**
- `Extension/PID/kP`
- `Extension/PID/kI`
- `Extension/PID/kD`
- `Extension/PID/kFF`

---

## Tuning Process

### Step 1: Start with Defaults
The robot starts with these values from Constants.java:
- P = 0.1
- I = 0.0
- D = 0.0
- FF = 0.0

### Step 2: Tune P (Proportional)

**Goal**: Get the motor to reach the target without oscillation

1. **Set a target position** (use operator controller buttons)
2. **Observe behavior**:
   - **No movement**: Increase `kP` (try doubling: 0.1 → 0.2 → 0.4)
   - **Reaches target**: Good! Move to next step
   - **Oscillates/vibrates**: Decrease `kP` (try 50%: 0.4 → 0.2)
   - **Slow approach**: Increase `kP` slightly

3. **Find the sweet spot**:
   - Start low and increase gradually
   - When oscillation starts, back off 20-30%
   - Example: If oscillates at P=1.0, use P=0.7

**⚠️ SAFETY**: If violent oscillation occurs, DISABLE IMMEDIATELY and reduce P by 50%

### Step 3: Tune I (Integral) - Optional

**When to add I**: If the arm settles close but not exactly at target (steady-state error)

1. **Start with I = 0.001** (very small!)
2. **Observe**:
   - **Slowly drifts to target**: Good! I is working
   - **Oscillates**: I is too high, reduce it
   - **No change**: Increase I slightly

**Warning**: Too much I causes **integral windup** and instability

**Typical values**: 0.0001 to 0.01

### Step 4: Tune D (Derivative) - Optional

**When to add D**: If overshooting target or settling time is too long

1. **Start with D = P / 10**
   - Example: If P = 0.5, try D = 0.05
2. **Observe**:
   - **Smooth approach, no overshoot**: Good!
   - **Still overshoots**: Increase D
   - **Sluggish/slow**: Decrease D

**Typical values**: 0.01 to 0.5

### Step 5: Tune FF (Feedforward) - Advanced

**When to add FF**: For gravity compensation on arms, or velocity control

**For Arms (fighting gravity)**:
1. **Find holding power**: What voltage keeps arm from falling?
2. **Calculate FF**: Holding voltage / max voltage
   - Example: Needs 1.5V to hold, max is 12V → FF = 1.5/12 = 0.125
3. **Fine-tune**: Adjust until arm doesn't drift

**Typical values**: 0.0 to 0.3 for arms

---

## Example Tuning Session

**Scenario**: Tuning arm angle PID

1. **Initial test** (P=0.1, I=0, D=0, FF=0):
   - Press "Level 2" button
   - Result: Motor barely moves
   - Action: Increase P to 0.3

2. **Second test** (P=0.3):
   - Result: Moves slowly but reaches target in 5 seconds
   - Action: Increase P to 0.6 for faster response

3. **Third test** (P=0.6):
   - Result: Reaches target quickly, slight overshoot
   - Action: Add D = 0.06 to dampen overshoot

4. **Fourth test** (P=0.6, D=0.06):
   - Result: Smooth, fast, no overshoot!
   - Action: Test all positions, looks good

5. **Fifth test** (checking steady-state):
   - Result: Settles 2 degrees below target
   - Action: Add tiny I = 0.001 to eliminate error

6. **Final values**: P=0.6, I=0.001, D=0.06, FF=0

**Time to tune**: ~15-20 minutes

---

## Saving Your Values

Once you've found good PID values:

### 1. Record Values from SmartDashboard
Write down the values that work well:
```
Arm/PID/kP = 0.6
Arm/PID/kI = 0.001
Arm/PID/kD = 0.06
Arm/PID/kFF = 0.0
```

### 2. Update Constants.java
Open `src/main/java/frc/robot/Constants.java` and update:

```java
public static class ArmConstants {
  // Arm PID constants - TUNED VALUES
  public static final double kArmP = 0.6;    // ← Update
  public static final double kArmI = 0.001;  // ← Update
  public static final double kArmD = 0.06;   // ← Update
  public static final double kArmFF = 0.0;   // ← Update

  // Extension PID constants - TUNED VALUES
  public static final double kExtensionP = 0.8;  // ← Update
  public static final double kExtensionI = 0.0;
  public static final double kExtensionD = 0.08;
  public static final double kExtensionFF = 0.0;
}
```

### 3. Redeploy
```bash
./gradlew deploy
```

### 4. Test Again
Verify values work after redeployment

### 5. Commit
```bash
git add src/main/java/frc/robot/Constants.java
git commit -m "Add tuned PID values for arm"
# Auto-push is enabled, no need to push manually
```

---

## Troubleshooting

### Values don't appear in SmartDashboard
- Check robot is connected
- Verify robot code is running (not disabled)
- Try refreshing SmartDashboard connection

### Changes don't take effect
- Make sure robot is **enabled** (disabled mode doesn't run periodic())
- Verify you're changing the correct field
- Check for typos in field names

### Robot oscillates violently
- **DISABLE IMMEDIATELY**
- Reduce P by 50%
- Set I and D to 0
- Re-enable and test with lower P

### Arm drifts down even with PID
- Need feedforward (FF) for gravity compensation
- Calculate: FF = (voltage to hold arm) / 12.0
- Start with FF = 0.1 and adjust

### Values reset after robot reboot
- This is expected! Live tuning is temporary
- Must update Constants.java and redeploy to make permanent

---

## Tips for Success

**Start Conservative**:
- Low P values are safer
- Can always increase, hard to recover from too high

**Test All Positions**:
- Don't just tune at one setpoint
- Test Level 1, 2, 3, 4, stowed, etc.
- Values should work for all positions

**Document As You Go**:
- Keep notes of what values you tried
- Record observations
- Makes it easier to backtrack if needed

**Take Your Time**:
- PID tuning is iterative
- Don't rush
- Small changes are better than big jumps

**Use the Graphs** (Shuffleboard):
- Plot position vs. target over time
- Visualize overshoot and settling time
- Makes tuning easier to see

---

## Advanced: What Each Parameter Does

### P (Proportional)
- **Effect**: How hard to push toward target
- **Higher P**: Faster response, more aggressive
- **Lower P**: Slower response, gentler
- **Too high**: Oscillation, instability
- **Too low**: Sluggish, doesn't reach target

### I (Integral)
- **Effect**: Eliminates steady-state error
- **Higher I**: Faster error correction
- **Lower I**: Slower error correction
- **Too high**: Oscillation, integral windup
- **Too low**: Small errors remain

### D (Derivative)
- **Effect**: Dampens oscillation, predicts future error
- **Higher D**: More damping, less overshoot
- **Lower D**: Less damping, more overshoot
- **Too high**: Sluggish, resistant to change
- **Too low**: Overshoots, rings

### FF (Feedforward)
- **Effect**: Applies base power to overcome known resistance
- **For arms**: Compensates for gravity
- **For velocity**: Provides power proportional to desired speed
- **Too high**: Overshoots, too much power
- **Too low**: Still needs P to do all the work

---

## Quick Reference Card

**Copy this and tape it near your driver station:**

```
=== PID TUNING QUICK REFERENCE ===

1. Start: P=0.1, I=0, D=0, FF=0

2. Tune P:
   - No movement? → Increase P
   - Oscillates? → Decrease P
   - Good? → Move to step 3

3. Add I (if needed):
   - Steady error? → Try I=0.001
   - Still error? → Increase slightly
   - Oscillates? → Decrease I

4. Add D (if needed):
   - Overshoots? → Try D = P/10
   - Still overshoots? → Increase D
   - Too slow? → Decrease D

5. Add FF (arms only):
   - Drifts down? → Try FF=0.1
   - Still drifts? → Increase FF
   - Overshoots? → Decrease FF

6. Save to Constants.java when done!

SAFETY: If violent oscillation → DISABLE + reduce P by 50%
```

---

**Happy tuning!** 🎛️
