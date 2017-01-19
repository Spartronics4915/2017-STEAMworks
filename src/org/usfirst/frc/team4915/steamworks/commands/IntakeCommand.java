package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class IntakeCommand extends Command
{

    private final Intake m_intake;

    public IntakeCommand(Intake intake)
    {
        m_intake = intake;

        requires(m_intake);
    }

    @Override
    public void end()
    {
        m_intake.setIntake(false);
    }

    @Override
    public void execute()
    {
        m_intake.setIntake(true);
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
