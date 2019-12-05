package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Cameras;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ChooseCameraCommand extends CommandBase
{
    private Cameras mCameras;
    private int mWhichCamera;

    public ChooseCameraCommand(int whichCamera)
    {
        mCameras = Cameras.getInstance();
        mWhichCamera = whichCamera;
        addRequirements(mCameras);
    }

    @Override
    public void initialize()
    {
        mCameras.changeCamera(mWhichCamera);
    }

    @Override
    public void execute()
    {

    }

    @Override
    public void end(boolean interrupted)
    {

    }

    @Override
    public boolean isFinished()
    {
        return true; // Once we're done, we're done...
    }
}
