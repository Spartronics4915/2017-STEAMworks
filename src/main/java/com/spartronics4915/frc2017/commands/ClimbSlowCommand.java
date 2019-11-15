package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Climber;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ClimbSlowCommand extends CommandBase
{
    private Climber mClimber;

    public ClimbSlowCommand()
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
        mClimber.slow();
    }

    @Override
    public void end(boolean interrupted)
    {
        mClimber.stop();
    }

    @Override
    public boolean isFinished()
    {
        return false; // Stopping is handled by the ClimbOffCommand and mapped to a button
    }
}
