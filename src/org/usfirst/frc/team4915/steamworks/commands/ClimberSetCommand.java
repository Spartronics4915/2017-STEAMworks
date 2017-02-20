package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Climber;
import org.usfirst.frc.team4915.steamworks.subsystems.Climber.State;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class ClimberSetCommand extends Command
{
    private final Climber m_climber;
    private final Climber.State m_state;

    public ClimberSetCommand(Climber climber, State on)
    {
        m_climber = climber;
        m_state = on;
        requires(m_climber);
    }

    @Override
    public void initialize()
    {
        String nm = m_climber.getStateString(m_state);
        m_climber.m_logger.notice("initialize ClimberSetCommand " + nm);
        SmartDashboard.putString("Climber State", nm);
        SmartDashboard.putNumber("Climber Speed",  m_climber.getClimberSpeed(m_state));
    }

    @Override
    public void execute()
    {
        m_climber.setClimber(m_state);
        SmartDashboard.putNumber("Climber Current", m_climber.getClimberCurrent());
        //possible future code if amps exceed safe amount testing needed to find this value
        //if(m_climber.getClimberCurrent() > someSafteyValue){ end(); }
    }

    @Override
    public boolean isFinished()
    {
        return false;
    }

    @Override
    public void interrupted()
    {
        m_climber.m_logger.notice("interrupted ClimberSetCommand " + m_climber.getStateString(m_state));
        end();
    }

    @Override
    public void end()
    {
        m_climber.m_logger.notice("end ClimberSetCommand " + m_climber.getStateString(m_state));
        m_climber.setClimber(Climber.State.OFF);
    }

}
