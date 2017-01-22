package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class IntakeOnCommand extends Command
{

    private final Intake m_intake;

    public IntakeOnCommand(Intake intake)
    {
        m_intake = intake;

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
        m_intake.setIntake(Intake.State.ON);
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
        return false;
    }
}
