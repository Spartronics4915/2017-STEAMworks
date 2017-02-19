package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;
import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.PIDOutput;

public class DriveDistancePIDCmd extends Command implements PIDSource, PIDOutput
{

    private final Drivetrain m_drivetrain;
    private double m_inches, m_revs;
    private PIDController m_pidController;
    
    private double m_initialHeading;
    private static final double KP = .5;
    private static final double MIN_DEGREES_ERROR = .5;

    private static final double k_P = 1.7, k_D = 0, k_I = 0, k_F = 0;

    public DriveDistancePIDCmd(Drivetrain drivetrain, double inches)
    {
        m_drivetrain = drivetrain;
        m_inches = inches;
        m_revs = m_drivetrain.getInchesToRevolutions(m_inches);
        m_pidController = new PIDController(k_P, k_D, k_I, k_F, this, this);
        m_pidController.setOutputRange(-1, 1); // Set the output range so that this works with our PercentVbus turning method
        m_pidController.setInputRange(-5 * m_revs, 5 * m_revs); // We limit our input range to revolutions, either direction
        m_pidController.setPercentTolerance(0.6); // This is the tolerance for error for reaching our target

        requires(m_drivetrain);
        m_drivetrain.m_logger.info("DriveDistancePIDCmd constructed");
    }

    @Override
    public void initialize()
    {
        m_drivetrain.m_logger.info("DriveDistancePIDCmd initialize");
        m_drivetrain.setControlMode(TalonControlMode.PercentVbus, 3.0, -3.0,
                0.0, 0, 0, 0 /* zero PIDF */);
        m_drivetrain.resetPosition();
        m_pidController.reset(); // Reset all of the things that have been passed to the IMU in any previous turns
        m_pidController.setSetpoint(m_revs); // Set the point we want to turn to
        
        m_initialHeading = m_drivetrain.getIMUHeading();
        m_drivetrain.m_logger.info("inintial direction: " + m_initialHeading);
    }

    @Override
    public void execute()
    {
        // We shouldn't have to do anything here because all of the PID control stuff runs in another thread
        if (!m_pidController.isEnabled())
        {
            m_pidController.enable();
        }
    }

    @Override
    public boolean isFinished()
    {
        if (m_pidController.onTarget())
        {
            m_drivetrain.m_logger.debug("DriveDistancePIDCmd PID is on target");
        }
        return m_pidController.isEnabled() ? m_pidController.onTarget() : true;
    }

    @Override
    public void interrupted()
    {
        m_drivetrain.m_logger.info("DriveDistancePIDCmd interrupted");
        end();
    }

    @Override
    public void end()
    {
        if (m_pidController.isEnabled())
        {
            m_pidController.reset();
            assert (!m_pidController.isEnabled());// docs say we're disabled now
        }
        m_drivetrain.m_logger.info("DriveDistancePIDCmd end");
        m_drivetrain.stop();
    }

    // PIDSource -----------------------------------------------------------------------
    @Override
    public void setPIDSourceType(PIDSourceType pidSource)
    {
        if (pidSource != PIDSourceType.kDisplacement)
        {
            m_drivetrain.m_logger.error("DriveDistancePIDCmd only supports kDisplacement");
        }
    }

    @Override
    public PIDSourceType getPIDSourceType()
    {
        return PIDSourceType.kDisplacement;
    }

    @Override
    public double pidGet()
    {
        return m_drivetrain.getOpenLoopValue();
    }

    // PIDOutput -----------------------------------------------------------------------
    @Override
    public void pidWrite(double output)
    {
        m_drivetrain.driveArcadeDirect(output, getIMUCorrection());
    }
    
    public double getIMUCorrection()
    {
        double currentHeading;
        double error;
        double correction = 0;
        
        currentHeading = m_drivetrain.getIMUHeading();
        m_drivetrain.m_logger.info("Current heading: " + currentHeading);
        
        error = currentHeading - m_initialHeading; //difference to the original direction
        
        //recalculate the error in case you turn more than 180 dggrees
        if(error > 180)
        {
            error -= 360;
        }
        else if(error < -180)
        {
            error += 360;
        }
        m_drivetrain.m_logger.info("headinig error: " + error);
        
        if(error >= MIN_DEGREES_ERROR || error <= -MIN_DEGREES_ERROR)
        {
            m_drivetrain.m_logger.info("correction: " + error * -KP);
            correction = error * KP;
        }
        return correction;
    }

}
