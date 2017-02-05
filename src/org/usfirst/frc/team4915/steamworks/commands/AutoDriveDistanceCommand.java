package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

public class AutoDriveDistanceCommand extends Command
{
    private int m_withinAllowableClosedLoopErrorCount;
    private static final int FINISH_COUNT_THRESHOLD = 10;
    
    private Logger m_logger = new Logger("DriveForwardCommand", Logger.Level.DEBUG);
    
    private Drivetrain m_drivetrain;
    private double m_desiredInches; //distance the robot is going to drive in inches, set in initialize()
    
    public AutoDriveDistanceCommand(Drivetrain drivetrain, double distanceInInches) {
        // Use requires() here to declare subsystem dependencies
        m_drivetrain = drivetrain;
        requires(m_drivetrain);
        
        this.m_desiredInches = distanceInInches;
        m_drivetrain.m_logger.info("constructed");
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        m_drivetrain.m_logger.info("Initializing");
        
        m_withinAllowableClosedLoopErrorCount = 0;
        // initialize encoders
        try
        {
            m_drivetrain.resetPosition();
            //m_drivetrain.resetEncPosition();
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // give it one rotation to run 
        // m_drivetrain.driveInDistance(Math.PI*6);
        
        m_logger.info("isPositionReset: " + m_drivetrain.isPositionReset());
        
        //is that necessary?
//        m_drivetrain.setPID(1.0, 0, 0);
//        m_drivetrain.setControlMode(CANTalon.TalonControlMode.Position, 12, -12);   
        
        m_logger.info("Initialized");
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
        // for motor safety -- need to update 'set' with target vs. current position
        // @TODO validate IF our positions are absolute, meaning we can repeatedly call 'set()'
        m_drivetrain.driveInDistance(m_desiredInches);
        
        m_drivetrain.m_logger.info("Position: " + m_drivetrain.getPortPosition() 
            + ", " + m_drivetrain.getStarboardPosition());
        m_drivetrain.m_logger.info("Error: " + m_drivetrain.getPortEncError() 
            + ", " + m_drivetrain.getStarboardEncError());
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        if (m_drivetrain.withinAllowableClosedLoopError())
        {
            m_withinAllowableClosedLoopErrorCount++;
            m_logger.info("withinClosedLoopErrorCount: " + m_withinAllowableClosedLoopErrorCount);
            return true; // Trying halting the first time
        }
        else
        {
            m_withinAllowableClosedLoopErrorCount = 0;
        }
        // isFinished runs every 20ms, and we look for more than 10 occurrences
        // within the allowable closed loop error range. It takes at least
        // 200ms to stabilize and return true
        if (m_withinAllowableClosedLoopErrorCount > FINISH_COUNT_THRESHOLD)
        {
            m_logger.info("isFinished = true");
            return true;
        }
        return false;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        m_drivetrain.stop();      
        m_drivetrain.m_logger.info("done executing");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
        end();
    }
}