package org.usfirst.frc.team4915.steamworks;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.usfirst.frc.team4915.steamworks.Logger.Level;
import org.usfirst.frc.team4915.steamworks.commands.DriveTicksCommand;
import org.usfirst.frc.team4915.steamworks.commands.IntakeOffCommand;
import org.usfirst.frc.team4915.steamworks.commands.IntakeOnCommand;
import org.usfirst.frc.team4915.steamworks.commands.IntakeReverseCommand;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class OI
{

    public static final int AUX_STICK_PORT = 1;
    // Ports for joysticks
    public static final int DRIVE_STICK_PORT = 0;

    public final Joystick m_auxStick = new Joystick(AUX_STICK_PORT);
    public final Joystick m_driveStick = new Joystick(DRIVE_STICK_PORT);

    public final JoystickButton m_intakeOff = new JoystickButton(m_driveStick, 9);
    public final JoystickButton m_intakeOn = new JoystickButton(m_driveStick, 7);
    public final JoystickButton m_intakeReverse = new JoystickButton(m_driveStick, 11);
    private Logger m_logger;

    private Robot m_robot;
    public final JoystickButton m_ticksOn = new JoystickButton(m_auxStick, 3);

    public OI(Robot robot)
    {
        m_robot = robot;
        m_logger = new Logger("OI", Logger.Level.DEBUG);
        initAutoOI();
        initDrivetrainOI();
        initIntakeOI();
        initLauncherOI();
        initClimberOI();

        /* VERSION STRING!! */
        try (InputStream manifest = getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"))
        {
            // build a version string
            Attributes attributes = new Manifest(manifest).getMainAttributes();
            String buildStr = "by: " + attributes.getValue("Built-By") +
                    "  on: " + attributes.getValue("Built-At") +
                    "  vers:" + attributes.getValue("Code-Version");
            SmartDashboard.putString("Build", buildStr);
            m_logger.notice("Build " + buildStr);
            ;
        }
        catch (IOException e)
        {
            SmartDashboard.putString("Build", "version not found!");
            m_logger.error("Build version not found!");
            m_logger.exception(e, true /* no stack trace needed */);
        }

        for (Logger logger : Logger.getAllLoggers())
        {
            LoggerChooser loggerChooser = new LoggerChooser(logger.getNamespace());

            SmartDashboard.putData(loggerChooser);

            Level desired = loggerChooser.getSelected();
            if (desired == null)
            {
                desired = Level.DEBUG;
            }
            logger.setLogLevel(desired);
            m_logger.debug("Logger created: " + logger.getNamespace() + " (" + desired.name() + ")");
        }
    }

    public Command getAutoCommand()
    {
        // TODO this just stops the robot from dying on boot
        return new IntakeOffCommand(m_robot.getIntake());
    }

    private void initAutoOI()
    {
    }

    private void initClimberOI()
    {
    }

    private void initDrivetrainOI()
    {
        m_robot.getDrivetrain().setDriveStick(m_driveStick);
        m_ticksOn.toggleWhenPressed(new DriveTicksCommand(m_robot.getDrivetrain()));
    }

    private void initIntakeOI()
    {
        if (m_robot.getIntake().initialized())
        {
            m_intakeOn.whenPressed(new IntakeOnCommand(m_robot.getIntake()));
            m_intakeOff.whenPressed(new IntakeOffCommand(m_robot.getIntake()));
            m_intakeReverse.whenPressed(new IntakeReverseCommand(m_robot.getIntake()));
        }
    }

    private void initLauncherOI()
    {
        // includes carousel
    }
}
