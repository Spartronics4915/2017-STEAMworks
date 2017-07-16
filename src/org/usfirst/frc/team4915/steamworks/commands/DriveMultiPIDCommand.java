package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.command.Command;

/**
 * DriveMultiPIDCommand defines a PID for the left and right motor
 * which are driven by multiple set points.
 */
public class DriveMultiPIDCommand extends Command {

    private final Drivetrain m_drivetrain;
    private PIDController m_leftPIDController;
    private PIDController m_rightPIDController;
	
    public DriveMultiPIDCommand(Drivetrain drivetrain) {
    	m_drivetrain = drivetrain;
    	
    	requires(m_drivetrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        m_drivetrain.m_logger.info("DriveMultiPIDCommand initalize");
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
        m_drivetrain.m_logger.info("DriveStraightCommand end");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        m_drivetrain.m_logger.info("DriveStraightCommand interrupted");
    }
}
