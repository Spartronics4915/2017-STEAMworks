package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

/**
 * This command should be called at the end of command groups to keep motor
 * saftey happy
 */
public class StopCommand extends Command
{

    private static Drivetrain m_drivetrain;

    public StopCommand(Drivetrain drivetrain)
    {
        m_drivetrain = drivetrain;
        requires(drivetrain);
    }

    protected void initialize()
    {
        m_drivetrain.m_logger.info("StopCommand initalized");
    }

    protected void execute()
    {
        m_drivetrain.stop();
    }

    protected boolean isFinished()
    {
        return false; // This command never ends, it's for the end of command groups
    }

    protected void end()
    {
        m_drivetrain.m_logger.info("StopCommand ended");
    }

    protected void interrupted()
    {
        m_drivetrain.m_logger.info("StopCommand interrupted");
    }
}
