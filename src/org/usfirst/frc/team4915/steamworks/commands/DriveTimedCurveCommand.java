package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import org.usfirst.frc.team4915.steamworks.commands.DriveStraightCommand;

/**
 * Extends drive straight command and adds a curve after a certain number of inches.
 */
public class DriveTimedCurveCommand extends DriveStraightCommand {
    
    private Drivetrain m_drivetrain;
    private double m_straightInches;
    private double m_straightRevs;
    private double m_curve;

    public DriveTimedCurveCommand(Drivetrain drivetrain, double totalInches, double curve, double straightInches)
    {
        super(drivetrain, totalInches);
        m_drivetrain = drivetrain;
        m_straightInches = straightInches;
        m_curve = curve;
        m_straightRevs = m_drivetrain.getInchesToRevolutions(m_straightInches);
    }
    
    // PIDOutput -----------------------------------------------------------------------
    @Override
    public void pidWrite(double output)
    {
        if (m_drivetrain.getOpenLoopValue() >= m_straightRevs) { // If we're over the number of revolutions we want to drive straight with, add the curve
            m_drivetrain.driveArcadeDirect(output, m_curve);
        } else {
            m_drivetrain.driveArcadeDirect(output, super.getIMUCorrection());
        }
    }
    
}
