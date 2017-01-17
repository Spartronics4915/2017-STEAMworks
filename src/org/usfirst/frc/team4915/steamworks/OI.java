package org.usfirst.frc.team4915.steamworks;

import org.usfirst.frc.team4915.steamworks.commands.IntakeCommand;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class OI
{

    // Ports for joysticks
    public static final int DRIVE_STICK_PORT = 0;
    public static final int LAUNCHER_STICK_PORT = 1;

    public final Joystick m_auxStick = new Joystick(DRIVE_STICK_PORT);
    public final Joystick m_driveStick = new Joystick(LAUNCHER_STICK_PORT);
    public final JoystickButton m_intakeOn = new JoystickButton(m_auxStick, 2);

    public OI(Robot robot)
    {
        if (robot.getIntake().wasSuccessful())
        {
            m_intakeOn.whileHeld(new IntakeCommand(robot.getIntake()));
        }
    }
}
