package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class IntakeSetCommand extends Command
{

    private final Intake m_intake;
    private final Intake.State m_state;

    public IntakeSetCommand(Intake intake, Intake.State state)
    {
        m_intake = intake;
        m_state = state;
        requires(m_intake);
    }

    @Override
    public void end()
    {
        m_intake.setIntake(Intake.State.OFF);
    }

    @Override
    public void execute()
    {
        m_intake.setIntake(m_state);
    }

    @Override
    public void initialize()
    {
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
