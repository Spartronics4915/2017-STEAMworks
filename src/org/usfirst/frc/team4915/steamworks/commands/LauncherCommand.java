package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher.LauncherState;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class LauncherCommand extends Command
{

    private final Launcher m_launcher;
    private Logger m_logger;
    private Launcher.LauncherState m_state;
    private boolean m_terminateWhenEmpty;

    public LauncherCommand(Launcher launcher, Launcher.LauncherState state, boolean terminateWhenEmpty)
    {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);

        m_launcher = launcher;
        m_logger = new Logger("LauncherCommand", Logger.Level.DEBUG);
        m_state = state;
        m_terminateWhenEmpty = terminateWhenEmpty; // true if and only if in autonomous
        requires(m_launcher);
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
        m_logger.debug("Initialized (" + m_state + ")");
        m_launcher.setAgitatorTarget();
        m_launcher.setLauncher(m_state);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
        m_launcher.setLauncher(m_state);
    }

    // Make this return true when this Command no longer needs to run execute()
    public boolean isFinished()
    {
        switch (m_state)
        {
            case ON:
                if (m_terminateWhenEmpty && m_launcher.isEmpty()) 
                    return true;
                else
                    return false;
            case OFF:
                return true;
            case SINGLE:
                if (m_launcher.isSingleShotDone())
                    return true;
                else
                    return false;
            case UNJAM:
                return false;
        }
        return false;
    }

    // Called once after isFinished returns true
    // Allows autonomous to end without interfering with teleop
    protected void end()
    {
        m_logger.notice("End (" + m_state + ")");
        m_launcher.setLauncher(LauncherState.OFF);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
        m_logger.info("Interrupted");
        end();
    }
}
