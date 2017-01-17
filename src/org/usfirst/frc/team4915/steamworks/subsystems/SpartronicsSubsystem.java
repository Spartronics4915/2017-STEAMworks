package org.usfirst.frc.team4915.steamworks.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class SpartronicsSubsystem extends Subsystem
{

    protected boolean m_successful = true;

    public boolean wasSuccessful()
    {
        return m_successful;
    }

}
