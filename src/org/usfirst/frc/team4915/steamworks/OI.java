package org.usfirst.frc.team4915.steamworks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import org.usfirst.frc.team4915.steamworks.Logger.Level;
import org.usfirst.frc.team4915.steamworks.commandgroups.TurnSequenceCommandGroup;
import org.usfirst.frc.team4915.steamworks.commands.ClimberSetCommand;
import org.usfirst.frc.team4915.steamworks.commands.DriveDistanceCmd;
import org.usfirst.frc.team4915.steamworks.commands.DriveDistancePIDCmd;
import org.usfirst.frc.team4915.steamworks.commands.IntakeSetCommand;
import org.usfirst.frc.team4915.steamworks.commands.LauncherCommand;
import org.usfirst.frc.team4915.steamworks.commands.RecordingSetCommand;
import org.usfirst.frc.team4915.steamworks.commands.ReplayCommand;
import org.usfirst.frc.team4915.steamworks.commands.ReverseArcadeDriveCommand;
import org.usfirst.frc.team4915.steamworks.subsystems.Climber;
import org.usfirst.frc.team4915.steamworks.subsystems.Intake.State;

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

    
    //Aux Stick Buttons
    public final JoystickButton m_climberOn = new JoystickButton(m_auxStick, 11);
    public final JoystickButton m_climberOff = new JoystickButton(m_auxStick, 10);
    public final JoystickButton m_climberSlow = new JoystickButton(m_auxStick, 9);
    
    public final JoystickButton m_launcherOn = new JoystickButton(m_auxStick, 3);
    public final JoystickButton m_launcherOff = new JoystickButton(m_auxStick, 2);
    
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

    private Set<Command> m_autoPresetOptions = new HashSet<>();
    private Set<String> m_autoReplayOptions = new HashSet<>();

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
            String command = strategy.replaceFirst("Preset: ", "");
            Set<Command> matches = m_autoPresetOptions.stream()
                    .filter(preset -> preset.getName().equals(command))
                    .collect(Collectors.toSet());
            if (matches.isEmpty())
            {
                m_logger.error("No autonomous preset matches " + command);
                return null;
            }
            if (matches.size() > 1)
            {
                m_logger.error("More than one preset matches autonomous choice \"" + command + "\": " + Arrays.toString(matches.toArray()));
                return null;
            }
            m_logger.info("Found " + command);
            return matches.iterator().next();
        }

        if (strategy.startsWith("Replay: "))
        {
            String replay = strategy.replaceFirst("Replay: ", "");
            m_logger.debug("Searching for " + replay);
            if (m_autoReplayOptions.contains(strategy))
            {
                m_logger.notice("Found a replay named " + replay);
                return new ReplayCommand(m_robot.getDrivetrain());
            }
            m_logger.error("Didn't find " + replay);
        }
        return null;
    }

    private void initAutoOI()
    {
        m_autoPresetOptions.add(new TurnSequenceCommandGroup(m_robot.getDrivetrain()));

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
            Files.list(root)
                    .filter(Files::isReadable)
                    .map(Path::getFileName)
                    .map(Path::toString)
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
        m_autoPresetOptions.stream().map(Command::getName).map(name -> "Preset: " + name).forEach(display::add);

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
        m_turnIMUStart.whenPressed(new TurnSequenceCommandGroup(m_robot.getDrivetrain()));
        m_driveDistance.whenPressed(new DriveDistanceCmd(m_robot.getDrivetrain(), 36));
        ; // needs tweaking!
        m_driveDistancePID.whenPressed(new DriveDistancePIDCmd(m_robot.getDrivetrain(), 57.3));
        ; // needs tweaking!
        m_replayRecord.whenPressed(new RecordingSetCommand(m_robot.getDrivetrain(), true));
        m_replayStop.whenPressed(new RecordingSetCommand(m_robot.getDrivetrain(), false));
        m_replayReplay.whenPressed(new ReplayCommand(m_robot.getDrivetrain()));
        
        m_reverseDrive.toggleWhenPressed(new ReverseArcadeDriveCommand(m_robot.getDrivetrain()));
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

    	m_launcherOn.whenPressed(new LauncherCommand(m_robot.getLauncher(), true));
    	m_launcherOff.whenPressed(new LauncherCommand(m_robot.getLauncher(), false));
    	
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

    private String allianceToString(AllianceStationID a)
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

}
