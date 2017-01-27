package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

public class AutoDriveStraightCommand extends Command
{
    public static final int QUAD_ENCODER_TICKS_PER_REVOLUTION = 250;
    public static final int QUAD_ENCODER_TICKS_PER_INCH = QUAD_ENCODER_TICKS_PER_REVOLUTION / (int) (Math.PI * 6);
    
    public double AUTOSPEED;

    private boolean isInitialized = false;
    private int initializeRetryCount = 0;
    private final static int MAX_RETRIES = 100;
    private Drivetrain m_drivetrain;
    private double m_desiredDistanceTicks;
    private double m_desiredInches;
    
    private Logger m_logger;

    public AutoDriveStraightCommand(Drivetrain drivetrain) {
        
//        this.AUTOSPEED = speed;
//        requires(m_drivetrain());     //what does requires() do?
//        desiredDistanceTicks = inchesToTicks(desiredDistanceInches);
        
        m_logger = new Logger("AutoCrossBaseline", Logger.Level.INFO);
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
        m_drivetrain.init();
        
        m_desiredInches = 10;
        m_desiredDistanceTicks = inchesToTicks(m_desiredInches);
        m_drivetrain.setDesiredDistance(m_desiredDistanceTicks);
        
        AUTOSPEED = 25; //To do: check what a good speed would be
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
        // updateSB();
        if(initializeRetryCount >= MAX_RETRIES) {
            m_logger.warning("INITIALISATION FAILED, MAXED OUT RETRIES");
            m_drivetrain.stop();
        }
        
        if (!isInitialized){
            isInitialized = m_drivetrain.isEncPositionZero();
            initializeRetryCount++;
        }
        else if(!isFinished())
        {
            m_drivetrain.driveStraight(AUTOSPEED);
        }
        else
        {
            m_logger.info("Reached final location");
        }
    }

    protected void end()
    {
        m_drivetrain.stop();
    }
    
    public boolean isFinished()
    {
        boolean isDone = false;
        if(m_drivetrain.isLocationReached(m_desiredDistanceTicks))
        {
            isDone = false;
        } 
        else
        {
            isDone = true;
        }
        return isDone;
    }
    public int inchesToTicks(double inches)
    {
        return (int) (inches * QUAD_ENCODER_TICKS_PER_INCH);
    }
}
