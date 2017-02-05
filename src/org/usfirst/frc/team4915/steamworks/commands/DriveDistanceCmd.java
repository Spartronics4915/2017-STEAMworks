package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Command;

public class DriveDistanceCmd extends Command
{

    private final Drivetrain m_drivetrain;
    private double m_inches, m_revs;

    public DriveDistanceCmd(Drivetrain drivetrain, double inches)
    {
        m_drivetrain = drivetrain;
        m_inches = inches;
        m_revs = m_drivetrain.getInchesToRevolutions(m_inches);
        requires(m_drivetrain);
    }

    @Override
    public void initialize()
    {
        m_drivetrain.m_logger.info("DriveDistanceCmd initialize");;
        m_drivetrain.setControlMode(TalonControlMode.Position, 4.0, -4.0, 
                                    0.56, 0, 0, 0 /* zero IDF  */);
        m_drivetrain.resetPosition();
    }

    @Override
    public void execute()
    {
        m_drivetrain.setClosedLoopTargetRevolutions(m_revs);
    }

    @Override
    public boolean isFinished()
    {
        return m_drivetrain.closedLoopTargetIsReached(.1);
    }
    
    @Override
    public void interrupted()
    {
        m_drivetrain.m_logger.info("DriveDistanceCmd interrupted");
        end();
    }

    @Override
    public void end()
    {
        m_drivetrain.m_logger.info("DriveDistanceCmd end"); 
        m_drivetrain.stop();
    }
    
}
