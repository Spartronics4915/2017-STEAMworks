package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TurnDegreesIMU extends Command
{

    private final Drivetrain m_drivetrain;
    private Logger m_logger;

    public TurnDegreesIMU(Drivetrain drivetrain)
    {
        m_drivetrain = drivetrain;
        m_logger = new Logger("TurnDegreesIMU", Logger.Level.DEBUG);
        requires(m_drivetrain);
    }

    @Override
    protected void initialize()
    {
        // Will the IMU be initialized by the time we get here?
        m_drivetrain.endIMUTurn();
        m_drivetrain.setControlMode(TalonControlMode.PercentVbus);
        m_drivetrain.startIMUTurnAbsolute(360); // We will parameterize this value in the constructor for command groups probably
        m_logger.debug("initalized");
    }

    @Override
    protected void execute()
    {
        // We shouldn't have to do anything here
    }

    @Override
    protected boolean isFinished()
    {
        return m_drivetrain.isIMUTurnFinished(); // We're done when the IMU turn is done
    }

    @Override
    protected void end()
    {
        // We don't need anything here
        m_logger.debug("ended");
    }

    @Override
    protected void interrupted()
    {
        m_drivetrain.endIMUTurn(); // Make sure that we stop turning
        m_logger.debug("interrupted");
        // Should we put a log message here?
    }
}
