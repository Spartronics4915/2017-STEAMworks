
package org.usfirst.frc.team4915.steamworks;

import org.usfirst.frc.team4915.steamworks.subsystems.Climber;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;
import org.usfirst.frc.team4915.steamworks.subsystems.Intake;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher;
import org.usfirst.frc.team4915.steamworks.subsystems.Cameras;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;


public class Robot extends IterativeRobot
{

    public Logger m_logger;

    private Drivetrain m_drivetrain;
    private Intake m_intake;
    private OI m_oi;
    private Climber m_climber;
    private Cameras m_cameras;

    private Launcher m_launcher;

    @Override
    public void robotInit()
    {
        m_logger = new Logger("Robot", Logger.Level.DEBUG);
        m_intake = new Intake();
        m_drivetrain = new Drivetrain();
        m_climber = new Climber();
        m_cameras = new Cameras();
        m_launcher = new Launcher();
        m_oi = new OI(this); // make sure OI is last
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

    public Cameras getCameras() {
        return m_cameras;
    }

    public Launcher getLauncher() {
        return m_launcher;
    }

    @Override
    public void autonomousInit()
    {
        Command acmd = m_oi.getAutoCommand();
        m_logger.notice("autonomous initalized.");
        if (acmd != null)
        {
            acmd.start();
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

    }

    @Override
    public void disabledPeriodic()
    {
        // we don't want to run the scheduler in disabled mode!
        // Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit()
    {
        Command acmd = m_oi.getAutoCommand();
        m_logger.notice("teleop initalized.");
        if (acmd != null)
        {
            acmd.cancel();
        }
        //new LauncherCommand(m_launcher, LauncherState.OFF).start();
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

    public Climber getClimber()
    {
        return m_climber;
    }

}
