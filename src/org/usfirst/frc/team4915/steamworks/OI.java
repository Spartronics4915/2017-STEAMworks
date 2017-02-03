package org.usfirst.frc.team4915.steamworks;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.Logger.Level;
import org.usfirst.frc.team4915.steamworks.commands.IntakeSetCommand;
import org.usfirst.frc.team4915.steamworks.commands.LauncherOffCommand;
import org.usfirst.frc.team4915.steamworks.commands.LauncherOnCommand;
import org.usfirst.frc.team4915.steamworks.commands.TurnDegreesIMU;
import org.usfirst.frc.team4915.steamworks.commands.ClimberSetCommand;
import org.usfirst.frc.team4915.steamworks.subsystems.Climber;
import org.usfirst.frc.team4915.steamworks.subsystems.Intake.State;

import commandgroups.TurnSequenceCommandGroup;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
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

    public final JoystickButton m_turnIMUStart = new JoystickButton(m_auxStick, 3);
    public final JoystickButton m_intakeOn = new JoystickButton(m_driveStick, 7);
    public final JoystickButton m_intakeOff = new JoystickButton(m_driveStick, 9);
    public final JoystickButton m_intakeReverse = new JoystickButton(m_driveStick, 11);

    public final JoystickButton m_launcherOn = new JoystickButton(m_driveStick, 8);
    public final JoystickButton m_launcherOff = new JoystickButton(m_driveStick, 10);
    public final JoystickButton m_climberOn = new JoystickButton(m_driveStick, 8);
    public final JoystickButton m_climberOff = new JoystickButton(m_driveStick, 12);
    public final JoystickButton m_climberSlow = new JoystickButton(m_driveStick, 10);

    private Logger m_logger;
    private Robot m_robot;

    public OI(Robot robot)
    {
        m_robot = robot;
        m_logger = new Logger("OI", Logger.Level.DEBUG);
        initAutoOI();
        initDrivetrainOI();
        initIntakeOI();
        initLauncherOI();
        initClimberOI();

        // Init loggers last, as this uses special values generated when other loggers are created.
        initLoggers();

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
    }

    public Command getAutoCommand()
    {
        return null;
    }

    private void initAutoOI()
    {
    }

    private void initClimberOI()
    {
        m_climberOn.whenPressed(new ClimberSetCommand(m_robot.getClimber(), Climber.State.ON));
        m_climberOff.whenPressed(new ClimberSetCommand(m_robot.getClimber(), Climber.State.OFF));
        m_climberSlow.whenPressed(new ClimberSetCommand(m_robot.getClimber(), Climber.State.SLOW));
    }

    private void initDrivetrainOI()
    {
        m_robot.getDrivetrain().setDriveStick(m_driveStick);
        m_turnIMUStart.whenPressed(new TurnSequenceCommandGroup(m_robot.getDrivetrain()));
    }

    private void initIntakeOI()
    {
        m_intakeOn.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.ON));
        m_intakeOff.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.OFF));
        m_intakeReverse.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.REVERSE));
    }

    private void initLauncherOI()
    {
    	m_launcherOn.whenPressed(new LauncherOnCommand(m_robot.getLauncher()));
    	m_launcherOff.whenPressed(new LauncherOffCommand(m_robot.getLauncher()));
    	
        // includes carousel
    }

    private void initLoggers()
    {

        /*
         * Get the shared instance, then throw away the result.
         * This ensures that the shared logger is created, even if never used
         * elsewhere.
         */
        Logger.getSharedInstance();

        for (Logger logger : Logger.getAllLoggers())
        {
            String key = "Loggers/" + logger.getNamespace();
            Level desired;

            if (!SmartDashboard.containsKey(key))
            {
                // First time this logger has been sent to SmartDashboard
                SmartDashboard.putString(key, logger.getLogLevel().name());
                desired = Level.DEBUG;
            }
            else
            {
                String choice = SmartDashboard.getString(key, "DEBUG");
                Level parsed = Level.valueOf(choice);
                if (parsed == null)
                {
                    m_logger.error("The choice '" + choice + "' for logger " + logger.getNamespace() + " isn't a valid value.");
                    desired = Level.DEBUG;
                }
                else
                {
                    desired = parsed;
                }
            }
            logger.setLogLevel(desired);
        }
    }
}
