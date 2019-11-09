package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Drivetrain;

import com.spartronics4915.frc2017.commands.DriveStraightCommand;

/**
 * Extends drive straight command and adds a curve after a certain number of inches.
 */
public class DriveTimedCurveCommand extends DriveStraightCommand {

    private Drivetrain m_drivetrain;
    private double m_straightInches;
    private double m_straightRevs;
    private double m_curve;
    private boolean m_reverse;

    public DriveTimedCurveCommand(Drivetrain drivetrain, double totalInches, double curve, double straightInches, double speed, boolean reverse)
    {
        super(drivetrain, totalInches, speed);
        m_drivetrain = drivetrain;
        // This is to contend with another negative value, don't subclass next year, don't do this neagtive thing either
        m_straightInches = -straightInches;
        m_curve = curve;
        m_reverse = reverse;
        m_straightRevs = m_drivetrain.getInchesToRevolutions(m_straightInches);
        m_drivetrain.m_logger.notice(""+m_straightInches+", "+m_curve+", "+m_reverse+", "+m_straightRevs+", "+speed);
    }

    // PIDOutput -----------------------------------------------------------------------
    @Override
    public void pidWrite(double output)
    {
        m_drivetrain.m_logger.debug(""+m_drivetrain.getOpenLoopValue()+", "+m_straightRevs);
        if (!m_reverse) {
            // The reason we have greater than on m_straightRevs is to contend with the other negative numbers
            if (m_drivetrain.getOpenLoopValue() <= m_straightRevs) { // If we're over the number of revolutions we want to drive straight with, add the curve
                m_drivetrain.driveArcadeDirect(output, m_curve);
                m_drivetrain.m_logger.notice("Curving");
            } else {
                m_drivetrain.driveArcadeDirect(output, super.getIMUCorrection());
                m_drivetrain.m_logger.notice("Not curving");
            }
        } else if (m_reverse) {
            if (m_drivetrain.getOpenLoopValue() > m_straightRevs) { // If we're over the number of revolutions we want to drive straight with, add the curve
                m_drivetrain.driveArcadeDirect(output, m_curve);
                m_drivetrain.m_logger.notice("Curving");
            } else {
                m_drivetrain.driveArcadeDirect(output, super.getIMUCorrection());
                m_drivetrain.m_logger.notice("Not curving");
            }
        }
    }



}
