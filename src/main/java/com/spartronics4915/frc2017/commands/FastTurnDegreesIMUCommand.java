package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Drivetrain;

/**
 * This extends TurnDegreesIMU and just sets the Drivetrain tolerance higher
 */
public class FastTurnDegreesIMUCommand extends TurnDegreesIMUCommand
{

    private final Drivetrain m_drivetrain;

    public FastTurnDegreesIMUCommand(Drivetrain drivetrain, double degrees)
    {
        super(drivetrain, degrees); // Calls parent constructor
        m_drivetrain = drivetrain;
        m_drivetrain.setIMUPIDAbsoluteTolerance(10); // Set the tolerance to higher
    }

    @Override
    protected boolean isFinished()
    {
        return m_drivetrain.isIMUTurnFinished();
    }
}
