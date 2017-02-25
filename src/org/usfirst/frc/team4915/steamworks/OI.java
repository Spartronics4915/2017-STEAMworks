package org.usfirst.frc.team4915.steamworks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.usfirst.frc.team4915.steamworks.Logger.Level;
import org.usfirst.frc.team4915.steamworks.commands.ClimberSetCommand;
import org.usfirst.frc.team4915.steamworks.commands.IntakeSetCommand;
import org.usfirst.frc.team4915.steamworks.commands.LauncherCommand;
import org.usfirst.frc.team4915.steamworks.commands.ReplayCommand;
import org.usfirst.frc.team4915.steamworks.commands.ReverseArcadeDriveCommand;
import org.usfirst.frc.team4915.steamworks.commands.groups.ParameterizedCommandGroup;
import org.usfirst.frc.team4915.steamworks.subsystems.Climber;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher;
import org.usfirst.frc.team4915.steamworks.subsystems.Intake.State;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher.LauncherState;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class OI
{

    // Ports for joysticks
    public static final int DRIVE_CONTROLLER_PORT = 0;
    public static final int AUX_STICK_PORT = 1;
    //public static final int ALT_DRIVE_STICK_PORT = 2;

    public final XboxController m_driveStick = new XboxController(DRIVE_CONTROLLER_PORT);
    public final Joystick m_auxStick = new Joystick(AUX_STICK_PORT);
    //public final Joystick m_altDriveStick = new Joystick(ALT_DRIVE_STICK_PORT);

    //Drive Controller buttons
    public final JoystickButton m_intakeOn = new JoystickButton(m_driveStick, 1);
    public final JoystickButton m_intakeOff = new JoystickButton(m_driveStick, 2);
    public final JoystickButton m_intakeReverse = new JoystickButton(m_driveStick, 4);

    public final JoystickButton m_reverseDrive = new JoystickButton(m_driveStick, 3);

    //public final JoystickButton m_cameraFwd = new JoystickButton(m_driveStick, 7);
    //public final JoystickButton m_cameraRev = new JoystickButton(m_driveStick, 8);

    //Aux Stick Buttons
    public final JoystickButton m_climberOn = new JoystickButton(m_auxStick, 11);
    public final JoystickButton m_climberOff = new JoystickButton(m_auxStick, 10);
    public final JoystickButton m_climberSlow = new JoystickButton(m_auxStick, 9);

    public final JoystickButton m_launcherOn = new JoystickButton(m_auxStick, 3);
    public final JoystickButton m_launcherOff = new JoystickButton(m_auxStick, 2);
    public final JoystickButton m_launcherSingle = new JoystickButton(m_auxStick, 4);
    public final JoystickButton m_launcherUnjam = new JoystickButton(m_auxStick, 5);

    public final JoystickButton m_auxIntakeOn = new JoystickButton(m_auxStick, 6);
    public final JoystickButton m_auxIntakeOff = new JoystickButton(m_auxStick, 7);
    public final JoystickButton m_auxIntakeReverse = new JoystickButton(m_auxStick, 8);

    /*
     * //Alt Drive Stick Buttons
     * public final JoystickButton m_altIntakeOn = new
     * JoystickButton(m_altDriveStick, 3);
     * public final JoystickButton m_altIntakeOff = new
     * JoystickButton(m_altDriveStick, 4);
     * public final JoystickButton m_altIntakeReverse = new
     * JoystickButton(m_altDriveStick, 6);
     * public final JoystickButton m_replayRecord = new
     * JoystickButton(m_altDriveStick, 7);
     * public final JoystickButton m_replayStop = new
     * JoystickButton(m_altDriveStick, 8);
     * public final JoystickButton m_replayReplay = new
     * JoystickButton(m_altDriveStick, 10);
     * //Auto test button
     * public final JoystickButton m_turnIMUStart = new
     * JoystickButton(m_altDriveStick, 9);
     * public final JoystickButton m_driveDistance = new
     * JoystickButton(m_altDriveStick, 11);
     * public final JoystickButton m_driveDistancePID = new
     * JoystickButton(m_altDriveStick, 12);
     */

    private Logger m_logger;
    private Robot m_robot;
    private String m_alliance;  // this isn't known accurately until autonomousInit
    
    public enum WallPosition // For command groups
    {
        ONE,
        TWO,
        THREE
    }

    private Set<String> m_autoReplayOptions = new HashSet<>();

    public OI(Robot robot)
    {
        m_robot = robot;
        m_logger = new Logger("OI", Logger.Level.DEBUG);
        initAutoOI(); // This is called twice, THIS IS INTENTIONAL. It is to resolve a timing issue, so we rebuild the list.
        /*
         * The first time we run this we build the list so it can be selected,
         * and we don't get a NullPointer because we set
         * m_alliance to blue as a dummy value. To actually get what the driver
         * selected we run it again, after getting
         * our actualy m_alliance from the SmartDashboard in initAlliance().
         */
        initDrivetrainOI();
        initIntakeOI();
        initLauncherOI();
        initChooseCameraOI();
        initClimberOI();

        // Init loggers last, as this uses special values generated when other loggers are created.
        initLoggers();

        // Version string and related information
        try (InputStream manifest = getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"))
        {
            // build a version string
            Attributes attributes = new Manifest(manifest).getMainAttributes();
            String buildStr = "by: " + attributes.getValue("Built-By") +
                    "  on: " + attributes.getValue("Built-At") +
                    "  (" + attributes.getValue("Code-Version") + ")";
            SmartDashboard.putString("Build", buildStr);

            m_logger.notice("=================================================");
            m_logger.notice("Initialized in station " + SmartDashboard.getString("AllianceStation", "Blue"));
            m_logger.notice(Instant.now().toString());
            m_logger.notice("Built " + buildStr);
            m_logger.notice("=================================================");

        }
        catch (IOException e)
        {
            SmartDashboard.putString("Build", "version not found!");
            m_logger.error("Build version not found!");
            m_logger.exception(e, true /* no stack trace needed */);
        }
    }

    // The job of initAutoOI is to populate user interface elements for the driver
    // station.   Since this is called via robotInit it is *too early* to know accurately
    // which alliance position and driver station we occupy.  This is known only at the
    // moment of Robot.autonomousInit()/getAutoCommand, so we must defer station-specific parameters
    // until that point.
    private void initAutoOI()
    {
        // here we enumerate all recordings
        Path root = Paths.get(System.getProperty("user.home"), "Recordings");
        if (!Files.isDirectory(root))
        {
            try
            {
                Files.createDirectories(root);
            }
            catch (IOException e)
            {
                m_logger.exception(e, true);
            }
        }
        if (!Files.isWritable(root))
        {
            m_logger.error("Recordings folder isn't writable!");
            return;
        }
        try
        {
            // since we don't know the alliance here, we have a problem.
            // for now, the workaround is to populate all presets.
            // String alliance = getAlliance();
            // String other = alliance.equals("Blue") ? "Red" : "Blue";
            Files.list(root)
                    .filter(Files::isReadable)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    // Hide replays meant for the other alliance
                    // .filter(filename -> !filename.contains(other))
                    .forEach(file ->
                    {
                        m_autoReplayOptions.add("Replay: " + file);
                        m_logger.debug("Autonomous option found: " + file);
                    });
        }
        catch (IOException e)
        {
            m_logger.exception(e, true);
        }

        Set<String> display = new HashSet<>();
        // You can't put commas into the names of these because that's how they're delineated, 
        // all of them are backwards to keep the drivers not confused
        display.add("None");
        display.add("Preset: Cross Baseline Positons 1+3");
        display.add("Preset: Place Gear Position 2");
        display.add("Preset: Drive and Shoot Position 3");
        display.add("Preset: Drive Shoot and Cross Baseline Position 3 with Curve");

        display.addAll(m_autoReplayOptions);

        SmartDashboard.putString("AutoStrategyOptions", String.join(",", display));
    }

    // getAutoCommand is presumed to occur during autonomousInit (ie: long after robotInit)
    public Command getAutoCommand()
    {
        m_logger.info("Finding an autonomous command...");
        Command result = null;
        String strategy = SmartDashboard.getString("AutoStrategy", "");
        if (strategy.equals("None") || strategy.equals(""))
        {
            m_logger.warning("No autonomous strategy selected.");
            return result;
        }
        m_alliance = SmartDashboard.getString("AllianceStation", "Blue");
        if(m_alliance != "Blue" && m_alliance != "Red")
        {
            m_logger.error("Unknown alliance station, switching to baseline strategy");
            strategy = "Preset: Cross Baseline Positons 1+3";
        }

        if (strategy.startsWith("Preset: "))
        {
            Drivetrain drivetrain = m_robot.getDrivetrain();
            Launcher launcher = m_robot.getLauncher();
            String name = strategy.replaceFirst("Preset: ", "");
            switch (name)
            {
                case "Cross Baseline Positons 1+3":
                    // This is the length from the diamond plate to the baseline
                    result = new ParameterizedCommandGroup(drivetrain, launcher, this,
                            "Drive", "-93.3");
                    break;
                case "Place Gear Position 2":
                    // This is the length from the diamond plate with the robot length subtracted and the 8 
                    //  subtracted to account for the spring and the inset of the gear on the robot
                    result = new ParameterizedCommandGroup(drivetrain, launcher, this,
                            "Drive", "" + (-114.3 + (RobotMap.ROBOT_LENGTH - 3)));
                    break;
                case "Drive and Shoot Position 3":
                    // We drive forward, turn to be parallel with the boiler, and drive into the boiler
                    result = new ParameterizedCommandGroup(drivetrain, launcher, this,
                            "Drive", "" + (-42 + returnForSide(m_alliance, 0, 10)),
                            "Turn", "-45",
                            "Drive Timeout", "" + (37 + returnForSide(m_alliance, 0, -3)), "2.5",
                            "Shoot");
                    break;
                case "Drive Shoot and Cross Baseline Position 3 with Curve":
                    // Do our regular shooting routine, then almost the exact opposite, and then drive over the baseline
                    result = new ParameterizedCommandGroup(drivetrain, launcher, this,
                            "Drive", "" + (-42 + returnForSide(m_alliance, 0, 10)),
                            "Turn", "-45",
                            "Drive Timeout", "" + (37 + returnForSide(m_alliance, 0, -3)), "2.5",
                            "Shoot",
                            "Curve", "-97", "0.5");
                    break;
                default:
                    break;
            }
            if (result == null)
                m_logger.error("No autonomous preset matches " + name);
            else

                m_logger.info("Found " + name);
            return result;
        }
        else if (strategy.startsWith("Replay: "))
        {
            String replay = strategy.replaceFirst("Replay: ", "");
            m_logger.debug("Searching for " + replay);
            if (m_autoReplayOptions.contains(strategy))
            {
                m_logger.notice("Found a replay named " + replay);
                return new ReplayCommand(m_robot.getDrivetrain(), m_robot.getLauncher());
            }
            m_logger.error("Didn't find " + replay);
            return null;
        }

        m_logger.error("Nothing matches " + strategy);
        return null;
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
        ; // needs tweaking!
          //m_driveDistancePID.whenPressed(new DriveStraightCommand(m_robot.getDrivetrain(), 57.3));
        ; // needs tweaking!
          //m_replayRecord.whenPressed(new RecordingSetCommand(m_robot.getDrivetrain(), true));
          //m_replayStop.whenPressed(new RecordingSetCommand(m_robot.getDrivetrain(), false));
          //m_replayReplay.whenPressed(new ReplayCommand(m_robot.getDrivetrain(), m_robot.getLauncher()));

        m_reverseDrive.toggleWhenPressed(new ReverseArcadeDriveCommand(m_robot.getDrivetrain(), m_robot.getCameras()));
    }

    private void initIntakeOI()
    {
        m_intakeOn.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.ON));
        m_intakeOff.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.OFF));
        m_intakeReverse.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.REVERSE));

        //Alternate drivestick buttons
        //m_altIntakeOn.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.ON));
        //m_altIntakeOff.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.OFF));
        //m_altIntakeReverse.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.REVERSE));

        //Aux Stick Buttons
        m_auxIntakeOn.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.ON));
        m_auxIntakeOff.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.OFF));
        m_auxIntakeReverse.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.REVERSE));
    }

    private double returnForSide(String alliance, double blue, double red)
    {
        switch (alliance)
        {
            case "Blue":
                return blue;
            case "Red":
                return red;
            default:
                m_logger.warning("returnForSide did't receive Red or Blue from WPILib DriverStation."); 
                // This shouldn't ever happen, but we're going to be defensive about it
                return 0;
        }
    }

    public int getAllianceScale() // called from ParameterizedCommandGroup
    {
        switch (m_alliance)
        {
            case "Blue":
                return -1;
            case "Red":
                return 1;
            default:
                m_logger.error("getAllianceScale called in invalid state."); 
                // This shouldn't ever happen, but we're going to be defensive about it
                return 1;
        }
    }

    private void initLauncherOI()
    {
        m_launcherOn.whenPressed(new LauncherCommand(m_robot.getLauncher(), LauncherState.ON, false));
        m_launcherOff.whenPressed(new LauncherCommand(m_robot.getLauncher(), LauncherState.OFF, false));
        m_launcherSingle.whenPressed(new LauncherCommand(m_robot.getLauncher(), LauncherState.SINGLE, false));
        m_launcherUnjam.whenPressed(new LauncherCommand(m_robot.getLauncher(), LauncherState.UNJAM, false));
    }

    private void initChooseCameraOI()
    {
        //m_cameraFwd.whenPressed(new ChooseCameraCommand(m_robot.getCameras(), Cameras.CAM_FWD));
        //m_cameraRev.whenPressed(new ChooseCameraCommand(m_robot.getCameras(), Cameras.CAM_REV));
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
