package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Command;

public class DriveTicksCommand extends Command
{
    private final Drivetrain m_drivetrain;
    private final Logger m_logger;
    private int m_startTicks, m_endTicks;
    
    public DriveTicksCommand(Drivetrain drivetrain)
    {
        m_drivetrain = drivetrain;
        m_logger = new Logger("DriveTicksCommand", Logger.Level.DEBUG);
        requires(m_drivetrain);
    }

    @Override
    public void initialize()
    {
        m_drivetrain.setControlMode(TalonControlMode.PercentVbus);
        // m_drivetrain.resetEncPosition();  (since this method doesn't take effect immediately, we try an alternate approach)
        m_startTicks = m_drivetrain.getEncPosition();
        m_endTicks = m_startTicks + m_drivetrain.getTicksPerRev() * 4;
        m_logger.info("initalized, start:" + m_startTicks + " end:" + m_endTicks);
    }

    @Override
    public void execute()
    {
        // m_drivetrain.driveTicksTest(250);
        m_drivetrain.driveStraight(.25);
    }

    @Override
    public boolean isFinished()
    {
        int pos = m_drivetrain.getEncPosition();
        if(pos >= m_endTicks)
        {
            m_logger.debug("finished, final position: " + pos);
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void end()
    {
        m_drivetrain.stop();
        m_logger.debug("end");
    }
    
    @Override
    public void interrupted()
    {
        m_logger.debug("interrupted");
        end();
    }

}
