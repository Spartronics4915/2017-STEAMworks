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
    }

    @Override
    public void execute()
    {
        m_climber.setClimber(m_state);
    }

    @Override
    public boolean isFinished()
    {
        return false;
    }

    @Override
    public void interrupted()
    {
        end();
    }

    @Override
    public void end()
    {
        m_climber.setClimber(Climber.State.OFF);
    }

}
