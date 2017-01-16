package org.usfirst.frc.team4915.steamworks;

import org.usfirst.frc.team4915.steamworks.commands.IntakeCommand;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class OI
{

    public final Joystick m_auxStick = new Joystick(1);

    public final Joystick m_driveStick = new Joystick(0);
    public final JoystickButton m_intakeOn = new JoystickButton(m_auxStick, 2);

    public OI(Robot robot)
    {
        m_intakeOn.whileHeld(new IntakeCommand(robot));
    }
}
