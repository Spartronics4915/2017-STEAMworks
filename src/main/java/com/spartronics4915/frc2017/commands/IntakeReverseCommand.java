package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeReverseCommand extends CommandBase
{
    private Intake mIntake;

    public IntakeReverseCommand()
    {
        mIntake = Intake.getInstance();
        addRequirements(mIntake);
    }

    @Override
    public void initialize()
    {
        // Intentionally left blank
    }

    @Override
    public void execute()
    {
        mIntake.reverse();
    }

    @Override
    public void end(boolean interrupted)
    {
        mIntake.stop();
    }

    @Override
    public boolean isFinished()
    {
        return false; // they relied on buttons to control their lifetime (:D :D :D)
    }
}
