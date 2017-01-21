package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

public class RunTicksCommand extends Command
{
    private final Drivetrain m_drivetrain;
    private final Logger m_logger;
    
    public RunTicksCommand(Drivetrain drivetrain)
    {
        m_drivetrain = drivetrain;
        m_logger = new Logger("Ticks", Logger.Level.DEBUG);
        requires(m_drivetrain);
    }

    @Override
    public void end()
    {
        
    }

    @Override
    public void execute()
    {
        m_drivetrain.driveTicksTest(250);
    }

    @Override
    public void initialize()
    {
        m_drivetrain.resetEncPosition();
        m_logger.info("initalized");
    }

    @Override
    public void interrupted()
    {
        end();
    }

    @Override
    public boolean isFinished()
    {
        return false;
    }
}
