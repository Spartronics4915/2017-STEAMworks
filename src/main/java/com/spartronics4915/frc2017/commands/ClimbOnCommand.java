package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Climber;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ClimbOnCommand extends CommandBase
{
    private Climber mClimber;

    public ClimbOnCommand()
    {
        mClimber = Climber.getInstance();
        addRequirements(mClimber);
    }

    @Override
    public void initialize()
    {
        // Intentionally left blank
    }

    @Override
    public void execute()
    {
        mClimber.climb();
    }

    @Override
    public void end(boolean interrupted)
    {
        mClimber.stop();
    }

    @Override
    public boolean isFinished()
    {
        return false; // apparently, gotta look into that:::: did they cancel with the stopcommand thing they had going on???
    }
}
