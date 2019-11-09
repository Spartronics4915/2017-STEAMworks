package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Drivetrain;

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
        m_drivetrain.m_logger.debug("RecordingSetCommand end");
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
        m_drivetrain.m_logger.debug("RecordingSetCommand initialize");
    }

    @Override
    public void interrupted()
    {
        m_drivetrain.m_logger.debug("RecordingSetCommand interrupted");
    }

    @Override
    public boolean isFinished()
    {
        m_drivetrain.m_logger.debug("RecordingSetCommand finished");
        return true;
    }
}
