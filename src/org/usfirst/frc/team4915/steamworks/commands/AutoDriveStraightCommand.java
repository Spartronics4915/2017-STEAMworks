package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Command;

public class AutoDriveStraightCommand extends Command
{   
    public double AUTOSPEED;

//    private boolean isInitialized = false;
//    private int initializeRetryCount = 0;
//    private final static int MAX_RETRIES = 100;
    private Drivetrain m_drivetrain;
    private double m_desiredInches; //distance the robot is going to drive in inches, set in initialize()

    public AutoDriveStraightCommand(Drivetrain drivetrain, double distanceInInches) {
        this.m_desiredInches = distanceInInches;        
        
        m_drivetrain = drivetrain;
        requires(m_drivetrain);
        
        m_drivetrain.m_logger.info("constructed");
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
        m_drivetrain.m_logger.info("Initializing");
//        m_drivetrain.setMaxOutput(1); // Set the max output in volts (we think)
//        //m_drivetrain.resetEncPosition(); // Reset encoder position, *doesn't take effect immediately*
//        m_drivetrain.setControlMode(CANTalon.TalonControlMode.Position); // Set ourselves to position mode
//        m_drivetrain.setPID(0.22,0,0); // PID tuning
//        m_drivetrain.setDesiredDistance(m_desiredInches);
//        
//        m_drivetrain.m_logger.info("Desired inches: " + m_desiredInches);
//        
//        
        //m_drivetrain.m_logger.info("Average encoder position: " + m_drivetrain.getAvgEncPosition());
        
        m_drivetrain.m_logger.info("Position: " + m_drivetrain.getPortPosition() 
        + ", " + m_drivetrain.getStarboardPosition());
        
        m_drivetrain.setControlMode(CANTalon.TalonControlMode.Position);        
        m_drivetrain.setPosition(0, 0);
        m_drivetrain.setPID(1.0, 0, 0);
        
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
        //always set the destination
        //m_drivetrain.setDestinationPosition();
        m_drivetrain.driveStraight(m_desiredInches);
        
        m_drivetrain.m_logger.info("Position: " + m_drivetrain.getPortPosition() 
                                 + ", " + m_drivetrain.getStarboardPosition());
        m_drivetrain.m_logger.info("Error: " + m_drivetrain.getPortEncError() 
        + ", " + m_drivetrain.getStarboardEncError());
        
        m_drivetrain.m_logger.info("Error: " + m_drivetrain.get());
        
    }

    protected void end()
    {
        m_drivetrain.stop();
        m_drivetrain.m_logger.info("done executing");
    }
    
    public boolean isFinished()
    {
        if(m_drivetrain.isLocationReached())
        {
            m_drivetrain.m_logger.info("isFinished is true");
            return true;
        }
        return false;
    }
}