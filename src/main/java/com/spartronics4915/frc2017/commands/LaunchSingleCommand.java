package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Launcher;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class LaunchSingleCommand extends CommandBase
{
    private Launcher mLauncher;
    private double mInitialPosition;

    public LaunchSingleCommand()
    {
        mLauncher = Launcher.getInstance();
        addRequirements(mLauncher);
    }

    @Override
    public void initialize()
    {
        mInitialPosition = mLauncher.getAgitatorPosition();
    }

    @Override
    public void execute()
    {
        mLauncher.launch();
    }

    @Override
    public void end(boolean interrupted)
    {
        mLauncher.stop();
    }

    @Override
    public boolean isFinished()
    {
        double mCurrentPosition = mLauncher.getAgitatorPosition();
        if (mCurrentPosition >= (mInitialPosition + 1024)) // 1024 native units, 1/4 rotation
            return true;
        return false;
    }
}
