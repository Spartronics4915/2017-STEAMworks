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

    public ReplayCommand(Drivetrain drivetrain)
    {
        m_drivetrain = drivetrain;
        m_currentStep = 0;
        requires(m_drivetrain);
    }

    @Override
    public void end()
    {
    }

    @Override
    public void execute()
    {
        if (m_currentStep >= m_replayForward.size())
        {
            return;
        }
        double forwardAmount = m_replayForward.get(m_currentStep);
        double rotationAmount = m_replayRotation.get(m_currentStep);

        m_drivetrain.driveArcadeDirect(forwardAmount, rotationAmount);
        m_drivetrain.m_logger
                .debug(String.format("Replayed %d of %d <%f, %f>", m_currentStep, m_replayForward.size(), forwardAmount, rotationAmount));
        m_currentStep++;
    }

    @Override
    public void initialize()
    {
        // Can't do this in the constructor or the lists will be empty. This
        // method is called when we run the command, but the constructor is
        // called very early, when OI is constructed.
        m_replayForward = m_drivetrain.getReplayForward();
        m_replayRotation = m_drivetrain.getReplayRotation();
    }

    @Override
    public void interrupted()
    {
    }

    @Override
    public boolean isFinished()
    {
        return m_currentStep >= m_replayForward.size();
    }
}
