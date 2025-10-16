# Quick Start Guide - Team 6045 Reefscape Robot

## First Time Setup

### 1. Install Software
- Download and install **WPILib 2025** from [wpilib.org](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html)
- Install **REV Hardware Client** for SPARK MAX configuration

### 2. Deploy Code to Robot
```bash
# Open terminal in project directory
cd /path/to/ClaudeCodeBot

# Build the code
./gradlew build

# Deploy to robot (must be connected via USB or WiFi)
./gradlew deploy
```

### 3. Configure SPARK MAX Controllers
Using REV Hardware Client:
1. Connect to robot via USB
2. Set CAN IDs for each SPARK MAX (see table below)
3. Update firmware if needed

| Motor | CAN ID |
|-------|--------|
| Left Front Drive | 1 |
| Left Rear Drive | 2 |
| Right Front Drive | 3 |
| Right Rear Drive | 4 |
| Intake Motor | 5 |
| Intake Roller | 6 |
| Arm Motor | 7 |
| Extension Motor | 8 |
| Left Climber | 9 |
| Right Climber | 10 |

### 4. First Boot Procedure

#### BEFORE ENABLING THE ROBOT:

1. **Connect Controllers**
   - Driver controller on USB port 0
   - Operator controller on USB port 1

2. **Home the Arm** (CRITICAL!)
   - Manually position arm in stowed position (fully retracted)
   - Enable robot in **Teleop** mode
   - Press **Operator D-Pad Right**
   - Confirm "ARM HOMED" appears in Driver Station console

3. **Test Drive**
   - Use driver controller left/right sticks
   - Verify robot drives straight (adjust motor inversions in Constants.java if needed)

4. **Test Intake**
   - Press **Driver A button** to run intake
   - Verify coral is pulled in (not pushed out)

5. **Test Arm Movement**
   - Press **Operator B button** (Level 1)
   - Arm should move smoothly to position
   - If it oscillates or moves erratically, **DISABLE immediately** and tune PID

6. **Test All Systems**
   - Test each scoring position (Operator A/B/X/Y/RB buttons)
   - Test climber extend/retract (Operator Back/Start)
   - Run autonomous routines from SmartDashboard

## If Something Goes Wrong

### Robot Won't Move
- Check: Is robot enabled? (Driver Station should show green)
- Check: Are motors getting power? (Check battery voltage)
- Check: CAN bus working? (Check for CAN errors in Driver Station)

### Arm Won't Move
- **Most Common**: Arm not homed! Press Operator D-Pad Right
- Check console for "WARNING: Arm not homed" message

### Robot Keeps Browning Out
- Lower current limits in Constants.java
- Check battery charge (should be >12V)
- Don't run too many high-power systems simultaneously

### Code Won't Deploy
- Check: Robot is powered on and connected
- Check: Correct team number in build.gradle (should be 6045)
- Try: Restart RoboRIO and try again

### Motors Running Backward
- Edit Constants.java
- Toggle inversion flags (e.g., `kLeftMotorsInverted = !kLeftMotorsInverted`)
- Rebuild and redeploy

## Controller Reference

### Driver (Port 0)
- **Left Stick Y**: Drive forward/backward
- **Right Stick X**: Turn left/right
- **A**: Intake coral
- **B**: Eject coral
- **X**: Stop intake
- **Left Bumper**: Slow mode
- **Right Bumper**: Quick turn

### Operator (Port 1)
- **A**: Intake position
- **B**: Score Level 1
- **X**: Score Level 2
- **Y**: Score Level 3
- **RB**: Score Level 4
- **LB**: Stow arm
- **Back**: Extend climber
- **Start**: Retract climber
- **RT**: Climb
- **LT**: Descend
- **D-Pad Up/Down**: Manual arm
- **D-Pad Right**: HOME ARM

## Next Steps

1. **Read [TUNING_GUIDE.md](TUNING_GUIDE.md)** - Essential for optimal performance
2. **Calibrate positions** - Find and set actual encoder values for your robot
3. **Tune PID values** - Make arm movements smooth and accurate
4. **Practice driving** - Get comfortable with controls before competition

## Emergency Stop

**SPACE BAR** on Driver Station = Emergency Stop (disables robot immediately)

## Need Help?

- Check logs in Driver Station console
- Review [README.md](README.md) for detailed documentation
- See [TUNING_GUIDE.md](TUNING_GUIDE.md) for configuration help
- Consult WPILib docs: https://docs.wpilib.org

## Pre-Competition Checklist

- [ ] All motors tested and running correct direction
- [ ] Arm homed and all positions calibrated
- [ ] PID values tuned for smooth movement
- [ ] Current limits set to prevent brownouts
- [ ] Autonomous routines tested
- [ ] Battery fully charged
- [ ] All CAN connections secure
- [ ] Controllers paired and tested
- [ ] Backup code on USB drive

Good luck at competition! 🤖
