// A nicely written command if I do say so myself
// IDEA: seperate the Launcher into Agitator and Launcher subsystems _just_ to force people to write integration

package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Launcher;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class UnjamLauncherCommand extends CommandBase
{
    private Launcher mLauncher;
    private int mJamCount; // times in a row the agitator hasn't moved

    public UnjamLauncherCommand()
    {
        mLauncher = Launcher.getInstance();
        addRequirements(mLauncher);
    }

    @Override
    public void initialize()
    {
        mJamCount = 0;
    }

    @Override
    public void execute()
    {
        // The intended action is for the launcher to launch and reverse until it becomes unjammed
        if (mJamCount < 15)   // We are waiting for the reversed velocity to have an effect
        {
            mLauncher.launch();
        }
        else if (mJamCount > 30) // If it doesn't work at this time, reverse the velocity again
        {
            mJamCount = 0;
            mLauncher.launch();
        }
        else
        {
            mLauncher.reverse();
        }
        mJamCount++;
    }

    @Override
    public void end(boolean interrupted)
    {
        mJamCount = 0;
        mLauncher.stop();
    }

    @Override
    public boolean isFinished()
    {
        if (mLauncher.getRPM() >= 10) // target is 70
            return true;
        return false;
    }
}
