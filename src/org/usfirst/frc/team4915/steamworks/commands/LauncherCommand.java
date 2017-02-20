package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher.LauncherState;

import com.ctre.CANTalon.FeedbackDevice;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class LauncherCommand extends Command
{

    private final Launcher m_launcher;
    private Logger m_logger;
    private final Launcher.LauncherState m_state;

    public LauncherCommand(Launcher launcher, Launcher.LauncherState state)
    {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);

        m_launcher = launcher;
        m_logger = new Logger("Launcher", Logger.Level.DEBUG);
        m_state = state;
        requires(m_launcher);
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
        m_logger.debug("LauncherCommand Initialized to " + m_state);
        m_launcher.setAgitatorTarget();
        m_launcher.setLauncher(m_state);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
        m_launcher.setLauncher(m_state);
    }
    
    

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        switch (m_state)
        {
            case ON:
                return false;
            case OFF:
                return true;
            case SINGLE:
                if(m_launcher.isSingleShotDone()) 
                {
                    return true;
                }
                return false;
        }
        return false;
    }

    // Called once after isFinished returns true
    protected void end()
    {
        m_logger.notice("LauncherCommand End");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
        m_logger.info("LauncherCommand.interrupted");
        end();
    }
}
