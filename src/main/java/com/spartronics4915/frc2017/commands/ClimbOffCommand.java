package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Climber;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ClimbOffCommand extends CommandBase
{
    private Climber mClimber;

    public ClimbOffCommand()
    {
        mClimber = Climber.getInstance();
        addRequirements(mClimber);
    }

    @Override
    public void initialize()
    {
        mClimber.stop();
    }

    @Override
    public void execute()
    {
        mClimber.stop();
    }

    @Override
    public void end(boolean interrupted)
    {
        mClimber.stop(); // Redundancy isn't a terrible thing, especially in this context
    }

    @Override
    public boolean isFinished()
    {
        return true;
    }
}
