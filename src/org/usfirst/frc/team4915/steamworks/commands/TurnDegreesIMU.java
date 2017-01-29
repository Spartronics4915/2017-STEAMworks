package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TurnDegreesIMU extends Command
{

    private final Drivetrain m_drivetrain;

    public TurnDegreesIMU(Drivetrain drivetrain)
    {
        m_drivetrain = drivetrain;
        requires(m_drivetrain);
    }

    @Override
    protected void initialize()
    {
        // Will the IMU be initialized by the time we get here?
        m_drivetrain.startIMUTurnAbsolute(360); // We will parameterize this value for command groups probably
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
    }

    @Override
    protected void interrupted()
    {
        m_drivetrain.endIMUTurn(); // Make sure that we stop turning
        // Should we put a log message here?
    }
}
