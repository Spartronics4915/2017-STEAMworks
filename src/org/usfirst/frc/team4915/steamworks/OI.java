package org.usfirst.frc.team4915.steamworks;

import org.usfirst.frc.team4915.steamworks.commands.AutoDriveStraightCommand;
import org.usfirst.frc.team4915.steamworks.Logger.Level;
import org.usfirst.frc.team4915.steamworks.commands.IntakeEncoderUpdateCommand;
import org.usfirst.frc.team4915.steamworks.commands.IntakeSetCommand;
import org.usfirst.frc.team4915.steamworks.subsystems.Intake.State;

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

    public final JoystickButton m_ticksOn = new JoystickButton(m_auxStick, 3);
    public final JoystickButton m_intakeOn = new JoystickButton(m_driveStick, 7);
    public final JoystickButton m_intakeOff = new JoystickButton(m_driveStick, 9);
    public final JoystickButton m_intakeReverse = new JoystickButton(m_driveStick, 11);
    public final JoystickButton m_intakeCount = new JoystickButton(m_driveStick, 5);
    public final JoystickButton m_autoButton = new JoystickButton(m_auxStick, 8);

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
    }

    private void initDrivetrainOI()
    {
        m_robot.getDrivetrain().setDriveStick(m_driveStick);
        //m_ticksOn.toggleWhenPressed(new DriveTicksCommand(m_robot.getDrivetrain()));
        m_autoButton.whenReleased(new AutoDriveStraightCommand(m_robot.getDrivetrain(), 18.849));
        m_logger.info("Drivetrain initialized");
    }

    private void initIntakeOI()
    {
        m_intakeOn.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.ON));
        m_intakeOff.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.OFF));
        m_intakeReverse.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.REVERSE));
        m_intakeCount.whenPressed(new IntakeEncoderUpdateCommand(m_robot.getIntake()));
    }

    private void initLauncherOI()
    {
        // includes carousel
    }

    private void initLoggers() {

        /*
         * Get the shared instance, then throw away the result.
         * This ensures that the shared logger is created, even if never used elsewhere.
         */
        Logger.getSharedInstance();

        for (Logger logger : Logger.getAllLoggers())
        {
            SendableChooser<Level> loggerChooser = new SendableChooser<>();
            for (Level level : Level.values())
            {
                loggerChooser.addObject(logger.getNamespace() + " " + level.name(), level);
            }

            SmartDashboard.putData("Logger for " + logger.getNamespace(), loggerChooser);

            Level desired = loggerChooser.getSelected();
            if (desired == null)
            {
                desired = Level.DEBUG;
            }
            logger.setLogLevel(desired);
        }
    }
    
}
