package com.spartronics4915.frc2017.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class SpartronicsSubsystem extends SubsystemBase
{
    private boolean mInitialized = false;

    public boolean isInitialized()
    {
        return mInitialized;
    }

}
