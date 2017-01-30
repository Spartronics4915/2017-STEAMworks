package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
//This command turns the agitator and launcher motor off at the same time
public class LauncherOffCommand extends Command {

	private final Launcher m_launcher;
	private Logger m_logger;

	public LauncherOffCommand(Launcher launcher) {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		m_launcher = launcher;
		m_logger = new Logger("Launcher", Logger.Level.DEBUG);
		requires(m_launcher);
	}

	// Called just before this Command runs the first time
	protected void initialize() {

	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		m_launcher.setLauncher(false);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	protected void end() {
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		m_logger.info("LauncherOffCommand.interrupted");
	}
}
