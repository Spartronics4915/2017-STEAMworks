package org.usfirst.frc.team4915.steamworks;

import org.usfirst.frc.team4915.steamworks.commands.IntakeCommand;
import org.usfirst.frc.team4915.steamworks.commands.RunTicksCommand;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.io.IOException;

public class OI
{

    // Ports for joysticks
    public static final int DRIVE_STICK_PORT = 0;
    public static final int AUX_STICK_PORT = 1;


    public final Joystick m_driveStick = new Joystick(DRIVE_STICK_PORT);
    public final Joystick m_auxStick = new Joystick(AUX_STICK_PORT);

    public final JoystickButton m_intakeOn = new JoystickButton(m_auxStick, 2);
    public final JoystickButton m_ticksOn = new JoystickButton(m_auxStick, 3);

    private Robot m_robot;
    private SendableChooser<Command> m_chooser;
    private Logger m_logger;

    public OI(Robot robot)
    {
        m_robot = robot;
		m_logger = new Logger("OI", Logger.Level.DEBUG);
        initAutoOI();
        initDrivetrainOI();
        initIntakeOI();
        initLauncherOI();
        initClimberOI();
        initTestOI();

        /* VERSION STRING!!  */
        try (InputStream manifest = getClass().getClassLoader().
                                        getResourceAsStream("META-INF/MANIFEST.MF"))
        {
            // build a version string
            Attributes attributes = new Manifest(manifest).getMainAttributes();
            String buildStr = "by: " + attributes.getValue("Built-By") +
                              "  on: " + attributes.getValue("Built-At") +
                              "  vers:" + attributes.getValue("Code-Version");
            SmartDashboard.putString("Build", buildStr);
            m_logger.notice("Build " + buildStr);;
        }
        catch (IOException e)
        {
            SmartDashboard.putString("Build", "version not found!");
            m_logger.error("Build version not found!");;
            m_logger.exception(e, true /*no stack trace needed*/);
        }
    }
    
    private void initTestOI() {
        m_ticksOn.toggleWhenPressed(new RunTicksCommand(m_robot.getDrivetrain()));
    }
    
    private void initAutoOI()
    {
        m_chooser = new SendableChooser<>();
        m_chooser.addDefault("Default Auto", 
                             new IntakeCommand(m_robot.getIntake()));
        // chooser.addObject("My Auto", new MyAutoCommand());
        SmartDashboard.putData("Auto mode", m_chooser);
    }
    
    public Command getAutoCommand()
    {
        return m_chooser.getSelected();
    }
   
    private void initDrivetrainOI()
    {
        m_robot.getDrivetrain().setDriveStick(m_driveStick);
    }
    
    private void initIntakeOI()
    {
        if (m_robot.getIntake().initialized())
        {
            m_intakeOn.toggleWhenPressed(new IntakeCommand(m_robot.getIntake()));
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
