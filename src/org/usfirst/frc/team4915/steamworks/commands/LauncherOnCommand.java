package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class LauncherOnCommand extends Command
{

    private final Launcher m_launcher;
    private Logger m_logger;

    public LauncherOnCommand(Launcher launcher)
    {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);

        m_launcher = launcher;
        m_logger = new Logger("Launcher", Logger.Level.DEBUG);
        requires(m_launcher);
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
        m_logger.debug("LauncherOnCommand Initialized");
        m_launcher.setLauncher(true);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
        m_launcher.updateLauncherSpeed();
        m_launcher.setAgitatorSpeed(Launcher.DEFAULT_AGITATOR_SPEED);
        //m_logger.debug("rawZ = " + rawZ + ", speed = " + speed);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        return false;
    }

    // Called once after isFinished returns true
    protected void end()
    {

    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
        m_logger.info("LauncherOnCommand.interrupted");
    }
}
