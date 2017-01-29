package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Command;

public class AutoDriveStraightCommand extends Command
{
    public static final int QUAD_ENCODER_TICKS_PER_REVOLUTION = 250 * 4;
    public static final int QUAD_ENCODER_TICKS_PER_INCH = (int)(QUAD_ENCODER_TICKS_PER_REVOLUTION / (Math.PI * 6));
    
    public double AUTOSPEED;

//    private boolean isInitialized = false;
//    private int initializeRetryCount = 0;
//    private final static int MAX_RETRIES = 100;
    private Drivetrain m_drivetrain;
    private double m_desiredInches; //distance the robot is going to drive in inches, set in initialize()
    
    private Logger m_logger;

    public AutoDriveStraightCommand(Drivetrain drivetrain) {
        
//        this.AUTOSPEED = speed;
//        desiredDistanceTicks = inchesToTicks(desiredDistanceInches);
        m_drivetrain = drivetrain;
        requires(m_drivetrain);
        m_logger = new Logger("AutoDriveStraightCommand", Logger.Level.INFO);
        
        m_logger.info("constructed");
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
        m_drivetrain.setMaxOutput(4); // Set the max output in volts (we think)
        //m_drivetrain.resetEncPosition(); // Reset encoder position, *doesn't take effect immediately*
        m_drivetrain.setControlMode(CANTalon.TalonControlMode.Position); // Set ourselves to position mode
        m_drivetrain.setPID(0.22,0,0); // PID tuning
        
        m_desiredInches = 36; //TODO: put that in the constructor later
        m_drivetrain.setDesiredDistance(m_desiredInches);
        
        m_logger.info("Desired inches: " + m_desiredInches);
        
//        AUTOSPEED = 25; //To do: check what a good speed would be
        
        m_logger.info("Initialized");
        //m_logger.info("Average encoder position: " + m_drivetrain.getAvgEncPosition());
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
        //do nothing, wait for command to finish
    }

    protected void end()
    {
        m_drivetrain.stop();
        m_logger.info("done executing");
    }
    
    public boolean isFinished()
    {
        if(m_drivetrain.isLocationReached())
        {
            m_logger.info("isFinished is true");
            return true;
        }
        return false;
    }
}