package com.spartronics4915.frc2017.commands;

import java.util.List;

import com.spartronics4915.frc2017.subsystems.Drivetrain;
import com.spartronics4915.frc2017.subsystems.Launcher;
import com.spartronics4915.frc2017.subsystems.Launcher.LauncherState;

import edu.wpi.first.wpilibj.command.Command;

public class ReplayCommand extends Command
{

    private final Drivetrain m_drivetrain;
    private final Launcher m_launcher;
    private List<Double> m_replayForward;
    private List<Double> m_replayRotation;
    private int m_currentStep;
    private int m_launchAt = 0;
    private boolean m_finished = false;

    public ReplayCommand(Drivetrain drivetrain, Launcher launcher)
    {
        m_drivetrain = drivetrain;
        m_launcher = launcher;
        requires(m_drivetrain);
    }

    @Override
    public void end()
    {
        m_drivetrain.m_logger.debug("ReplayCommand end");
        m_drivetrain.stop();
    }

    @Override
    public void execute()
    {
        if (m_launchAt != 0 && m_currentStep == m_launchAt)
        {
            m_drivetrain.m_logger.notice("Launching");
            LauncherCommand launch = new LauncherCommand(m_launcher, LauncherState.ON, true);
            launch.initialize();
            m_drivetrain.m_logger.debug("Started the command...");
            while (!launch.isFinished())
            {
                if (launch.timeSinceInitialized() > 5)
                {
                    m_drivetrain.m_logger.debug("Stopping anyway...");
                    launch.interrupted();
                    break;
                }
                launch.execute();
                m_drivetrain.driveArcadeDirect(0, 0);
            }
            launch.end();
        }
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
        m_drivetrain.loadReplay();
        m_replayForward = m_drivetrain.getReplayForward();
        m_replayRotation = m_drivetrain.getReplayRotation();
        m_currentStep = 0;
        m_launchAt = m_drivetrain.getReplayLaunch();
        m_finished = false;
    }

    @Override
    public void interrupted()
    {
        m_drivetrain.m_logger.debug("ReplayCommand interrupted");
        end();
    }

    @Override
    public boolean isFinished()
    {
        return m_finished;
    }
}
