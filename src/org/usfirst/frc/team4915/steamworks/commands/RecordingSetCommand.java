package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

public class RecordingSetCommand extends Command
{

    private final Drivetrain m_drivetrain;
    private final boolean m_state;

    public RecordingSetCommand(Drivetrain drivetrain, boolean state)
    {
        m_drivetrain = drivetrain;
        m_state = state;
        requires(m_drivetrain);
    }

    @Override
    public void end()
    {
    }

    @Override
    public void execute()
    {
        if (m_state)
        {
            m_drivetrain.startRecording();
        }
        else
        {
            m_drivetrain.stopRecording();
        }
    }

    @Override
    public void initialize()
    {
    }

    @Override
    public void interrupted()
    {
    }

    @Override
    public boolean isFinished()
    {
        return true;
    }
}
