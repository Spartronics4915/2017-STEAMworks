package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;
import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.PIDOutput;

public class DriveStraightCommand extends Command implements PIDSource, PIDOutput
{

    private final Drivetrain m_drivetrain;
    private double m_inches;
    private double m_revs;
    private PIDController m_pidController;
    private Logger m_logger;

    private double m_heading;
    private static final double IMU_CORRECTION = 0.1875;
    private static final double MAX_DEGREES_ERROR = .125;

    private static final double k_P = 1.43, k_D = 0, k_I = 0, k_F = 0; // Working P 1.43

    public DriveStraightCommand(Drivetrain drivetrain, double inches)
    {
        m_drivetrain = drivetrain;
        m_logger = new Logger("DriveStraightCommand", Logger.Level.DEBUG);
        // Adjust for negative direction logic of ArcadeDrive
        m_inches = -inches;
        m_revs = m_drivetrain.getInchesToRevolutions(m_inches);
        m_pidController = new PIDController(k_P, k_D, k_I, k_F, this, this);
        m_pidController.setOutputRange(-1, 1); // Set the output range so that this works with our PercentVbus turning method
        m_pidController.setInputRange(-5 * Math.abs(m_revs), 5 * Math.abs(m_revs)); // We limit our input range to revolutions, either direction
        m_pidController.setAbsoluteTolerance(2 * (1 / (3 * (2 * Math.PI)))); // This is the tolerance for error for reaching our target, targeting one inch

        requires(m_drivetrain);
        m_logger.info("constructed");
    }

    @Override
    public void initialize()
    {
       m_logger.info("initialize");
        m_drivetrain.setControlMode(TalonControlMode.PercentVbus, 3.0, -3.0, // This was 3 and -3
                0.0, 0, 0, 0 /* zero PIDF */);
        m_drivetrain.resetPosition();
        m_pidController.reset(); // Reset all of the things that have been passed to the IMU in any previous turns
        m_pidController.setSetpoint(m_revs); // Set the point we want to turn to
        m_heading = m_drivetrain.getIMUHeading();
       m_logger.debug("Initial heading: " + m_heading);
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
           m_logger.debug("Actual ticks driven " + m_drivetrain.getEncPosition());
           m_logger.debug("Desired ticks " + m_revs * 1000 + " ticks.");
           m_logger.debug("Difference ticks " + ((m_revs * 1000) - m_drivetrain.getEncPosition()) + " ticks.");
        }
        return m_pidController.isEnabled() ? m_pidController.onTarget() : true;
    }

    @Override
    public void interrupted()
    {
       m_logger.info("interrupted");
        end();
    }

    @Override
    public void end()
    {
        if (m_pidController.isEnabled())
        {
            m_pidController.reset();
            assert (!m_pidController.isEnabled()); // docs say we're disabled now
        }
       m_logger.info("end");
        m_drivetrain.stop();
    }

    // PIDSource -----------------------------------------------------------------------
    @Override
    public void setPIDSourceType(PIDSourceType pidSource)
    {
        if (pidSource != PIDSourceType.kDisplacement)
        {
           m_logger.error("only supports kDisplacement");
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

    // Computes a turn correction factor based on the difference from the initial to the current IMU heading 
    public double getIMUCorrection()
    {
        double currentHeading;
        double error;

        currentHeading = m_drivetrain.getIMUHeading();

        error = currentHeading - m_heading; //difference to the original direction

        // recalculate the error in case you turn more than 180 degrees
        if (error > 180)
        {
            error -= 360;
        }
        else if (error < -180)
        {
            error += 360;
        }

        double correction = 0.0;

        if (error >= MAX_DEGREES_ERROR || error <= -MAX_DEGREES_ERROR)
        {
            correction = Math.copySign(IMU_CORRECTION, error * m_revs); // Adjust for direction
        }

       m_logger.debug("IMU Heading: " + currentHeading + " Correction: " + correction + " Error: " + error);

        return correction;
    }

}
