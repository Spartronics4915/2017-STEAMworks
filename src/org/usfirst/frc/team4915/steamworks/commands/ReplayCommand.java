package org.usfirst.frc.team4915.steamworks.commands;

import java.util.List;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

public class ReplayCommand extends Command
{

    private final Drivetrain m_drivetrain;
    private List<Double> m_replayForward;
    private List<Double> m_replayRotation;
    private int m_currentStep;
    private boolean m_finished = false;

    public ReplayCommand(Drivetrain drivetrain)
    {
        m_drivetrain = drivetrain;
        requires(m_drivetrain);
    }

    @Override
    public void end()
    {
    }

    @Override
    public void execute()
    {
        if (m_currentStep++ >= m_replayForward.size())
        {
            m_finished = true;
            m_drivetrain.m_logger.notice("Done playing back.");
            return;
        }
        double forwardAmount = m_replayForward.get(m_currentStep - 1);
        double rotationAmount = m_replayRotation.get(m_currentStep - 1);

        m_drivetrain.driveArcadeDirect(forwardAmount, rotationAmount);
        m_drivetrain.m_logger
                .debug(String.format("Replayed %d of %d <%f, %f>", m_currentStep, m_replayForward.size(), forwardAmount, rotationAmount));
    }

    @Override
    public void initialize()
    {
        m_drivetrain.m_logger.info("Playing back recording");
        // Can't do this in the constructor or the lists will be empty. This
        // method is called when we run the command, but the constructor is
        // called very early, when OI is constructed.
        m_replayForward = m_drivetrain.getReplayForward();
        m_replayRotation = m_drivetrain.getReplayRotation();
        m_currentStep = 0;
        m_finished = false;
    }

    @Override
    public void interrupted()
    {
    }

    @Override
    public boolean isFinished()
    {
        return m_finished;
    }
}
