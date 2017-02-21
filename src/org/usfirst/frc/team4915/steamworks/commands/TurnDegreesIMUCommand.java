package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TurnDegreesIMUCommand extends Command
{

    private final Drivetrain m_drivetrain;
    private double m_degrees;
    private int m_targetCounter;

    public TurnDegreesIMUCommand(Drivetrain drivetrain, double degrees)
    {
        m_drivetrain = drivetrain;
        m_degrees = degrees;
        requires(m_drivetrain);
    }

    @Override
    protected void initialize()
    {
        m_targetCounter = 0;
        // Will the IMU be initialized by the time we get here?
        m_drivetrain.endIMUTurn();
        m_drivetrain.setControlMode(TalonControlMode.PercentVbus, 3.0, -3.0, // This is the same as DriveStraight because it is probably overriden if a command is run before it
                0, 0, 0, 0 /* zeros since we're not in closed-loop */);
        m_drivetrain.startIMUTurnAbsolute(m_degrees);
        m_drivetrain.m_logger.debug("TurnDegreesIMUCommand I want to turn  "+m_degrees+" degrees.");
        m_drivetrain.m_logger.info("TurnDegreesIMUCommand initalized");
    }

    @Override
    protected void execute()
    {
        // We shouldn't have to do anything here because all of the PID control stuff runs in another thread
    }

    @Override
    protected boolean isFinished()
    {
        if (m_drivetrain.isIMUTurnFinished())
        {
            m_targetCounter++;
            if (m_targetCounter > 20) // Make sure that we're on target for a while
            {
                return m_drivetrain.isIMUTurnFinished(); // We're done when the IMU turn is done
            }
            else
            {
                return false;
            }
        }
        else
        {
            m_targetCounter = 0;
            return false;
        }
//        return m_drivetrain.isIMUTurnFinished();
    }

    @Override
    protected void end()
    {
        m_drivetrain.endIMUTurn();
        m_drivetrain.m_logger.debug("TurnDegreesIMUCommand Actual degrees driven "+m_drivetrain.getIMUNormalizedHeading());
        m_drivetrain.m_logger.debug("TurnDegreesIMUCommand Desired degrees driven " + m_degrees);
        m_drivetrain.m_logger.debug("TurnDegreesIMUCommand Difference ticks " + ((m_degrees)-m_drivetrain.getIMUNormalizedHeading()) + " ticks.");
        m_drivetrain.m_logger.info("TurnDegreesIMUCommand ended");

    }

    @Override
    protected void interrupted()
    {
        m_drivetrain.endIMUTurn(); // Make sure that we stop turning
        m_drivetrain.m_logger.info("TurnDegreesIMUCommand interrupted");
    }
}
