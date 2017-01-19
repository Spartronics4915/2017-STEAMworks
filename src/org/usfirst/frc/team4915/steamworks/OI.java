package org.usfirst.frc.team4915.steamworks;

import org.usfirst.frc.team4915.steamworks.commands.IntakeCommand;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class OI
{

    // Ports for joysticks
    public static final int DRIVE_STICK_PORT = 0;
    public static final int LAUNCHER_STICK_PORT = 1;


    public final Joystick m_driveStick = new Joystick(LAUNCHER_STICK_PORT);
    public final Joystick m_auxStick = new Joystick(DRIVE_STICK_PORT);

    public final JoystickButton m_intakeOn = new JoystickButton(m_auxStick, 2);

    private Robot m_robot;
    private SendableChooser<Command> m_chooser;

    public OI(Robot robot)
    {
        m_robot = robot;
        initAutoOI();
        initDrivetrainOI();
        initIntakeOI();
        initLauncherOI();
        initClimberOI();
    }
    
    public Command getAutoCommand()
    {
        return m_chooser.getSelected();
    }
    
    private void initAutoOI()
    {
        m_chooser = new SendableChooser<>();
        m_chooser.addDefault("Default Auto", 
                             new IntakeCommand(m_robot.getIntake()));
        // chooser.addObject("My Auto", new MyAutoCommand());
        SmartDashboard.putData("Auto mode", m_chooser);
    }
    
    private void initDrivetrainOI()
    {
        m_robot.getDrivetrain().setDriveStick(m_driveStick);
    }
    
    private void initIntakeOI()
    {
        if (m_robot.getIntake().initialized())
        {
            m_intakeOn.whileHeld(new IntakeCommand(m_robot.getIntake()));
        }
    }
    
    private void initLauncherOI()
    {
        // includes carousel
    }
    
    private void initClimberOI()
    {
    }
}
