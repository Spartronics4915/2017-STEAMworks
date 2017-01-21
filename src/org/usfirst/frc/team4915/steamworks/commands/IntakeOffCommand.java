package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Intake;
import org.usfirst.frc.team4915.steamworks.subsystems.Intake.State;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class IntakeOffCommand extends Command
{

    private final Intake m_intake;

    public IntakeOffCommand(Intake intake)
    {
        m_intake = intake;

        requires(m_intake);
    }

    @Override
    public void end()
    {
    }

    @Override
    public void execute()
    {
        m_intake.setIntake(State.OFF);
    }

    @Override
    public void initialize()
    {
    }

    @Override
    public void interrupted()
    {
    }

    @Override
    public boolean isFinished()
    {
        return true;
    }
}
