package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Launcher;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class LaunchMultipleCommand extends CommandBase
{
    private Launcher mLauncher;
    private boolean mTerminateWhenEmpty;
    private double mInitialPosition;

    public LaunchMultipleCommand(boolean terminateWhenEmpty)
    {
        mLauncher = Launcher.getInstance();
        mTerminateWhenEmpty = terminateWhenEmpty;
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
        if (mTerminateWhenEmpty && (mCurrentPosition >= mInitialPosition + 4096 * 3.5)) // Calculate 3 full rotations
            return true;
        return false;
    }
}
