package org.usfirst.frc.team4915.steamworks.commands;

import java.util.List;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher.LauncherState;

import edu.wpi.first.wpilibj.command.Command;

public class ReplayCommand extends Command
{

    private final Drivetrain m_drivetrain;
    private final Launcher m_launcher;
    private final Command m_launcherOn;
    private final Command m_launcherOff;
    private List<Double> m_replayForward;
    private List<Double> m_replayRotation;
    private int m_currentStep;
    private int m_launchStart = 0;
    private int m_launchStop = 0;
    private boolean m_finished = false;

    public ReplayCommand(Drivetrain drivetrain, Launcher launcher)
    {
        m_drivetrain = drivetrain;
        m_launcher = launcher;
        m_launcherOn = new LauncherCommand(m_launcher, LauncherState.ON, true);
        m_launcherOff = new LauncherCommand(m_launcher, LauncherState.OFF, true);
        requires(m_drivetrain);
    }

    @Override
    public void end()
    {
        m_drivetrain.m_logger.debug("ReplayCommand end");
    }

    @Override
    public void execute()
    {
        if (m_launchStart != 0)
        {
            if (m_currentStep == m_launchStart)
            {
                m_drivetrain.m_logger.notice("Launching in replay");
                m_launcherOn.start();
            }
            else if (m_currentStep == m_launchStop)
            {
                m_drivetrain.m_logger.notice("Stopped launching in replay");
                m_launcherOn.cancel();
                m_launcherOff.start();
            }
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
        m_launchStart = m_drivetrain.getReplayLaunchStart();
        m_launchStop = m_drivetrain.getReplayLaunchStop();
        m_finished = false;
    }

    @Override
    public void interrupted()
    {
        m_drivetrain.m_logger.debug("ReplayCommand interrupted");
    }

    @Override
    public boolean isFinished()
    {
        return m_finished;
    }
}
