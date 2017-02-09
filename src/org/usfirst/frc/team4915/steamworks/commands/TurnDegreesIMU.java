package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TurnDegreesIMU extends Command
{

    private final Drivetrain m_drivetrain;
    private double m_degrees;
    private int targetCounter;

    public TurnDegreesIMU(Drivetrain drivetrain, double degrees)
    {
        m_drivetrain = drivetrain;
        m_degrees = degrees;
        requires(m_drivetrain);
    }

    @Override
    protected void initialize()
    {
        targetCounter = 0;
        // Will the IMU be initialized by the time we get here?
        m_drivetrain.endIMUTurn();
        m_drivetrain.setControlMode(TalonControlMode.PercentVbus, 12.0, -12.0,
                0, 0, 0, 0 /* zeros since we're not in closed-loop */);
        m_drivetrain.startIMUTurnAbsolute(m_degrees);
        m_drivetrain.m_logger.debug("initalized");
    }

    @Override
    protected void execute()
    {
        // We shouldn't have to do anything here because all of the PID control stuff runs in another thread
        m_drivetrain.debugIMU();
    }

    @Override
    protected boolean isFinished()
    {
        if (m_drivetrain.isIMUTurnFinished())
        {
            m_drivetrain.m_logger.debug(targetCounter+"");
            targetCounter++;
            if (targetCounter > 20) // Make sure that we're on target for a while
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
            targetCounter = 0;
            return false;
        }
//        return m_drivetrain.isIMUTurnFinished();
    }

    @Override
    protected void end()
    {
        m_drivetrain.endIMUTurn();
        m_drivetrain.m_logger.debug("ended");
    }

    @Override
    protected void interrupted()
    {
        m_drivetrain.endIMUTurn(); // Make sure that we stop turning
        m_drivetrain.m_logger.debug("interrupted");
    }
}
