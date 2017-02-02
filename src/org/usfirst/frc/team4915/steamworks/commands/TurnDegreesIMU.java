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

    public TurnDegreesIMU(Drivetrain drivetrain, double degrees)
    {
        m_drivetrain = drivetrain;
        m_degrees = degrees;
        requires(m_drivetrain);
    }

    @Override
    protected void initialize()
    {
        // Will the IMU be initialized by the time we get here?
        m_drivetrain.endIMUTurn();
        m_drivetrain.setControlMode(TalonControlMode.PercentVbus);
        m_drivetrain.startIMUTurnAbsolute(m_degrees); // We will parameterize this value in the constructor for command groups probably
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
        return m_drivetrain.isIMUTurnFinished(); // We're done when the IMU turn is done
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
        // Should we put a log message here?
    }
}
