
package org.usfirst.frc.team4915.steamworks;

import org.usfirst.frc.team4915.steamworks.subsystems.Climber;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;
import org.usfirst.frc.team4915.steamworks.subsystems.Intake;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher.LauncherState;

import java.util.ArrayList;

import org.usfirst.frc.team4915.steamworks.commands.ArcadeDriveCommand;
import org.usfirst.frc.team4915.steamworks.commands.LauncherCommand;
import org.usfirst.frc.team4915.steamworks.subsystems.Cameras;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot
{

    public Logger m_logger;

    private Drivetrain m_drivetrain;
    private Intake m_intake;
    private OI m_oi;
    private Climber m_climber;
    private Cameras m_cameras;
    private Command m_autoCmd;

    private Launcher m_launcher;

    @Override
    public void robotInit()
    {
        m_logger = new Logger("Robot", Logger.Level.DEBUG);
        CANProbe cp = new CANProbe();
        ArrayList<String> canDevices = cp.Find();
        m_logger.notice("robotInit CANDevicesFound:\n" + canDevices);
        SmartDashboard.putString("CANBusStatus", 
                            canDevices.size() == RobotMap.NUM_CAN_DEVICES ? "OK" : 
                            (""+canDevices.size()+"/"+RobotMap.NUM_CAN_DEVICES));
        m_drivetrain = new Drivetrain(); // We put drivetrain first so that our IMU is ready for everything else faster
        m_intake = new Intake();
        m_climber = new Climber();
        m_cameras = new Cameras();
        m_launcher = new Launcher();
        m_oi = new OI(this); // make sure OI is last
        m_autoCmd = null;
    }

    @Override
    public void robotPeriodic()
    {
        // This is invoked in all periodic cases in addition to the other
        // active periodic mode.  We implement this method in order to
        // quell the "unimplemented" message.  To get code running through
        // this method create a method in your subsystem to be called here 
        // as a hook.
        m_drivetrain.updatePeriodicHook(); // Drivetrain hook
    }

    public Intake getIntake()
    {
        return m_intake;
    }

    public Drivetrain getDrivetrain()
    {
        return m_drivetrain;
    }

    public Cameras getCameras()
    {
        return m_cameras;
    }

    public Launcher getLauncher()
    {
        return m_launcher;
    }

    public Climber getClimber()
    {
        return m_climber;
    }

    @Override
    public void autonomousInit()
    {
        m_logger.notice("autonomous initalized.");
        m_drivetrain.initAutonomous();
        m_autoCmd = m_oi.getAutoCommand();
        if (m_autoCmd != null)
        {
            m_autoCmd.start();
        }
        else
        {
            m_logger.error("can't start autonomous command because it is null.");
        }
    }

    @Override
    public void autonomousPeriodic()
    {
        Scheduler.getInstance().run();
    }

    @Override
    public void disabledInit()
    {
        m_logger.notice("autonomous initalized.");
    }

    @Override
    public void disabledPeriodic()
    {
        // running the scheduler during disabled mode has the effect of canceling
        // *most* active tasks, and so it is okay.
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit()
    {
        m_logger.notice("teleop initalized.");
        if (m_autoCmd != null)
        {
            m_autoCmd.cancel();
            m_autoCmd = null;
        }
        else
        {
            m_logger.error("can't cancel a null autonomous command.");
        }
        new LauncherCommand(m_launcher, LauncherState.OFF, false).start(); // make sure launcher is off when teleop starts.
        m_drivetrain.initTeleop();
    }

    @Override
    public void teleopPeriodic()
    {
        Scheduler.getInstance().run();
    }

    @Override
    public void testPeriodic()
    {
        LiveWindow.run();
    }

}
