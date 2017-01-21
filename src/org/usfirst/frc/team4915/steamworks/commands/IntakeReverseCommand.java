package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Intake;
import org.usfirst.frc.team4915.steamworks.subsystems.Intake.State;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class IntakeReverseCommand extends Command
{

    private final Intake m_intake;

    public IntakeReverseCommand(Intake intake)
    {
        m_intake = intake;
        requires(m_intake);
    }

    @Override
    public void end()
    {
        m_intake.setIntake(State.OFF);
    }

    @Override
    public void execute()
    {
        m_intake.setIntake(State.REVERSE);
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
