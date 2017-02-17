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
import org.usfirst.frc.team4915.steamworks.commands.groups.GenericCommandGroup;
import org.usfirst.frc.team4915.steamworks.commands.ClimberSetCommand;
import org.usfirst.frc.team4915.steamworks.commands.DriveDistanceCmd;
import org.usfirst.frc.team4915.steamworks.commands.DriveStraightCommand;
import org.usfirst.frc.team4915.steamworks.commands.IntakeSetCommand;
import org.usfirst.frc.team4915.steamworks.commands.LauncherCommand;
import org.usfirst.frc.team4915.steamworks.commands.RecordingSetCommand;
import org.usfirst.frc.team4915.steamworks.commands.ReplayCommand;
import org.usfirst.frc.team4915.steamworks.commands.ReverseArcadeDriveCommand;
import org.usfirst.frc.team4915.steamworks.subsystems.Climber;
import org.usfirst.frc.team4915.steamworks.subsystems.Intake.State;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher.LauncherState;
import org.usfirst.frc.team4915.steamworks.commands.ChooseCameraCommand;
import org.usfirst.frc.team4915.steamworks.subsystems.Cameras;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.hal.AllianceStationID;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class OI
{

    // Ports for joysticks
    public static final int DRIVE_CONTROLLER_PORT = 0;
    public static final int AUX_STICK_PORT = 1;
    public static final int ALT_DRIVE_STICK_PORT = 2;

    public final XboxController m_driveStick = new XboxController(DRIVE_CONTROLLER_PORT);
    public final Joystick m_auxStick = new Joystick(AUX_STICK_PORT);
    public final Joystick m_altDriveStick = new Joystick(ALT_DRIVE_STICK_PORT);

    //Drive Controller buttons
    public final JoystickButton m_intakeOn = new JoystickButton(m_driveStick, 1);
    public final JoystickButton m_intakeOff = new JoystickButton(m_driveStick, 2);
    public final JoystickButton m_intakeReverse = new JoystickButton(m_driveStick, 4);
    
    public final JoystickButton m_reverseDrive = new JoystickButton(m_driveStick, 3);

    public final JoystickButton m_cameraFwd = new JoystickButton(m_driveStick, 7);
    public final JoystickButton m_cameraRev = new JoystickButton(m_driveStick, 8);


    //Aux Stick Buttons
    public final JoystickButton m_climberOn = new JoystickButton(m_auxStick, 11);
    public final JoystickButton m_climberOff = new JoystickButton(m_auxStick, 10);
    public final JoystickButton m_climberSlow = new JoystickButton(m_auxStick, 9);

    public final JoystickButton m_launcherOn = new JoystickButton(m_auxStick, 3);
    public final JoystickButton m_launcherOff = new JoystickButton(m_auxStick, 2);
    public final JoystickButton m_launcherSingle = new JoystickButton(m_auxStick, 4);

    public final JoystickButton m_auxIntakeOn = new JoystickButton(m_auxStick, 6);
    public final JoystickButton m_auxIntakeOff = new JoystickButton(m_auxStick, 7);
    public final JoystickButton m_auxIntakeReverse = new JoystickButton(m_auxStick, 8);

    //Alt Drive Stick Buttons
    public final JoystickButton m_altIntakeOn = new JoystickButton(m_altDriveStick, 3);
    public final JoystickButton m_altIntakeOff = new JoystickButton(m_altDriveStick, 4);
    public final JoystickButton m_altIntakeReverse = new JoystickButton(m_altDriveStick, 6);

    public final JoystickButton m_replayRecord = new JoystickButton(m_altDriveStick, 7);
    public final JoystickButton m_replayStop = new JoystickButton(m_altDriveStick, 8);
    public final JoystickButton m_replayReplay = new JoystickButton(m_altDriveStick, 10);

    //Auto test button
    public final JoystickButton m_turnIMUStart = new JoystickButton(m_altDriveStick, 9);
    public final JoystickButton m_driveDistance = new JoystickButton(m_altDriveStick, 11);
    public final JoystickButton m_driveDistancePID = new JoystickButton(m_altDriveStick, 12);

    private Logger m_logger;
    private Robot m_robot;

    public enum WallPosition // For command groups
    {
        ONE,
        TWO,
        THREE
    }

    private Map<String, Command> m_autoPresetOptions = new HashMap<>();
    private Set<String> m_autoReplayOptions = new HashSet<>();

    public OI(Robot robot)
    {
        m_robot = robot;
        m_logger = new Logger("OI", Logger.Level.DEBUG);
        initAutoOI();
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
            m_logger.notice("Initialized in station " + HAL.getAllianceStation());
            SmartDashboard.putString("AllianceStation", allianceToString(HAL.getAllianceStation()));
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

    public Command getAutoCommand()
    {
        m_logger.info("Finding an autonomous command...");
        String strategy = SmartDashboard.getString("AutoStrategy", "");
        if (strategy.equals("None") || strategy.equals(""))
        {
            m_logger.warning("No autonomous strategy selected.");
            return null;
        }

        if (strategy.startsWith("Preset: "))
        {
            String name = strategy.replaceFirst("Preset: ", "");
            Command command = m_autoPresetOptions.get(name);
            if (command == null)
            {
                m_logger.error("No autonomous preset matches " + name);
                return null;
            }
            m_logger.info("Found " + name);
            return command;
        }

        if (strategy.startsWith("Replay: "))
        {
            String replay = strategy.replaceFirst("Replay: ", "");
            m_logger.debug("Searching for " + replay);
            if (m_autoReplayOptions.contains(strategy))
            {
                m_logger.notice("Found a replay named " + replay);
                return new ReplayCommand(m_robot.getDrivetrain(), m_robot.getLauncher());
            }
            m_logger.error("Didn't find " + replay);
        }
        return null;
    }

    private void initAutoOI()
    {
        // You can't put commas into the names of these because that's how they're deliniated
        m_autoPresetOptions.put("Cross Baseline Positons 1+3", new GenericCommandGroup(m_robot.getDrivetrain(), this,
                -95,0,0,0,0)); // This is the length from the diamond plate to the baseline
        m_autoPresetOptions.put("Place Gear Position 2", new GenericCommandGroup(m_robot.getDrivetrain(), this, 
                -(93.3-(RobotMap.ROBOT_LENGTH+1)),0,0,0,0)); // This is the length from the diamond plate with the robot length and an inch (just to be safe) subtracted
        m_autoPresetOptions.put("Drive and Shoot Position 1", new GenericCommandGroup(m_robot.getDrivetrain(), this, 
                35,90,248-RobotMap.ROBOT_WIDTH,135,17)); // Drive out for the turning radius + 10 inches to be aligned with the middle of the boiler, drive the distance from the baseline minus the robot's width and then turn to be parallel with the boiler, and then drive into the boiler
        m_autoPresetOptions.put("Drive and Shoot Position 2", new GenericCommandGroup(m_robot.getDrivetrain(), this, 
                35,90,124-(RobotMap.ROBOT_WIDTH/2),135,17)); // Drive out for the turning radius + 10 inches to be aligned with the middle of the boiler, drive the distance from the baseline minus half of the robot's width (we're centered on the baseline) and then turn so we're parallel with the boiler and drive into the boiler
        m_autoPresetOptions.put("Drive and Shoot Position 3", new GenericCommandGroup(m_robot.getDrivetrain(), this, 
                35,135,24,Double.NaN,Double.NaN)); // This is the length from the diamond plate with the robot length and an inch (just to be safe) subtracted
        m_autoPresetOptions.put("Drive, Shoot, and Cross Baseline Position 3", new GenericCommandGroup(m_robot.getDrivetrain(), this, 
                35,135,24,-90,Double.NaN)); // This is the length from the diamond plate with the robot length and an inch (just to be safe) subtracted
        
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
            Alliance alliance = DriverStation.getInstance().getAlliance();
            if (alliance == null)
            {
                m_logger.error("We're not on an alliance?");
                return;
            }
            String other = alliance.toString().equals("Blue") ? "Red" : "Blue";
            Files.list(root)
                    .filter(Files::isReadable)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    // Hide replays meant for the other alliance
                    .filter(filename -> !filename.contains(other))
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
        // Special value.
        display.add("None");

        display.addAll(m_autoReplayOptions);
        m_autoPresetOptions.keySet()
                .stream()
                .map(name -> "Preset: " + name)
                .forEach(display::add);

        SmartDashboard.putString("AutoStrategyOptions", String.join(",", display));
    }

    private void initClimberOI()
    {
        m_climberOn.whenPressed(new ClimberSetCommand(m_robot.getClimber(), Climber.State.ON));
        m_climberOff.whenPressed(new ClimberSetCommand(m_robot.getClimber(), Climber.State.OFF));
        m_climberSlow.whenPressed(new ClimberSetCommand(m_robot.getClimber(), Climber.State.SLOW));
    }

    private void initDrivetrainOI()
    {
        m_robot.getDrivetrain().setDriveStick(m_driveStick, m_altDriveStick);
        m_driveDistance.whenPressed(new DriveDistanceCmd(m_robot.getDrivetrain(), 36));
        ; // needs tweaking!
        m_driveDistancePID.whenPressed(new DriveStraightCommand(m_robot.getDrivetrain(), 57.3));
        ; // needs tweaking!
        m_replayRecord.whenPressed(new RecordingSetCommand(m_robot.getDrivetrain(), true));
        m_replayStop.whenPressed(new RecordingSetCommand(m_robot.getDrivetrain(), false));
        m_replayReplay.whenPressed(new ReplayCommand(m_robot.getDrivetrain(), m_robot.getLauncher()));
        
        m_reverseDrive.toggleWhenPressed(new ReverseArcadeDriveCommand(m_robot.getDrivetrain(), m_robot.getCameras()));
    }

    private void initIntakeOI()
    {
        m_intakeOn.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.ON));
        m_intakeOff.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.OFF));
        m_intakeReverse.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.REVERSE));

        //Alternate drivestick buttons
        m_altIntakeOn.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.ON));
        m_altIntakeOff.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.OFF));
        m_altIntakeReverse.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.REVERSE));

        //Aux Stick Buttons
        m_auxIntakeOn.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.ON));
        m_auxIntakeOff.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.OFF));
        m_auxIntakeReverse.whenPressed(new IntakeSetCommand(m_robot.getIntake(), State.REVERSE));
    }

    private void initLauncherOI()
    {
    	m_launcherOn.whenPressed(new LauncherCommand(m_robot.getLauncher(), LauncherState.ON));
    	m_launcherOff.whenPressed(new LauncherCommand(m_robot.getLauncher(), LauncherState.OFF));
    	m_launcherSingle.whenPressed(new LauncherCommand(m_robot.getLauncher(), LauncherState.SINGLE));
        // includes carousel
    }

    private void initChooseCameraOI()
    {
        m_cameraFwd.whenPressed(new ChooseCameraCommand(m_robot.getCameras(), Cameras.CAM_FWD));
        m_cameraRev.whenPressed(new ChooseCameraCommand(m_robot.getCameras(), Cameras.CAM_REV));
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

    private String allianceToString(AllianceStationID a) // This is used with a network table value
    {
        switch (a)
        {
            case Blue1:
                return "Blue1";
            case Blue2:
                return "Blue2";
            case Blue3:
                return "Blue3";
            case Red1:
                return "Red1";
            case Red2:
                return "Red2";
            case Red3:
                return "Red3";
        }
        return "unknown";
    }

    public int getSideMultiplier()
    {
        switch (DriverStation.getInstance().getAlliance())
        {
            case Blue:
                return -1;
            case Red:
                return 1;
            default:
                m_logger.warning("getSideMultiplier did't recive Red or Blue from WPILib DriverStation."); // This shouldn't ever happen, but we're going to be defensive about it
                return 1;
        }
    }

}
