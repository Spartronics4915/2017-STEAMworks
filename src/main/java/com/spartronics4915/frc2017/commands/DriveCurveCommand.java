package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Drivetrain;
import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.PIDOutput;

public class DriveCurveCommand extends Command implements PIDSource, PIDOutput
{

    private final Drivetrain m_drivetrain;
    private double m_inches;
    private double m_revs;
    private double m_curveFactor;
    private PIDController m_pidController;

    private static final double k_P = 1.43, k_D = 0, k_I = 0, k_F = 0; // Working P 1.43

    public DriveCurveCommand(Drivetrain drivetrain, double inches, double curveFactor)
    {
        m_drivetrain = drivetrain;
        // Adjust for negative direction logic of ArcadeDrive
        m_inches = -inches;
        m_revs = m_drivetrain.getInchesToRevolutions(m_inches);
        m_curveFactor = curveFactor;
        m_drivetrain.m_logger.info("desired inches: " + m_inches + " curveFactor: " + m_curveFactor);
        m_pidController = new PIDController(k_P, k_D, k_I, k_F, this, this);
        m_pidController.setOutputRange(-1, 1); // Set the output range so that this works with our PercentVbus turning method
        m_pidController.setInputRange(-5 * Math.abs(m_revs), 5 * Math.abs(m_revs)); // We limit our input range to revolutions, either direction
        m_pidController.setAbsoluteTolerance(2*(1 / (3 * (2 * Math.PI)))); // This is the tolerance for error for reaching our target, targeting one inch

        requires(m_drivetrain);
    }

    @Override
    public void initialize()
    {
        m_drivetrain.m_logger.info("driveCurveCommand initialize\r\n");
        m_drivetrain.setControlMode(TalonControlMode.PercentVbus, 6.0, -6.0, // This was 3 and -3 //is this even doing anything?
                0.0, 0, 0, 0 /* zero PIDF */, 0.4 /* also set max output */);
        m_pidController.setSetpoint(m_revs); // Set the point we want to turn to
        m_drivetrain.resetPosition();
        m_pidController.reset(); // Reset all of the things that have been passed to the IMU in any previous turns
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
            m_drivetrain.m_logger.debug("driveCurveCommand Actual ticks driven "+m_drivetrain.getEncPosition());
            m_drivetrain.m_logger.debug("driveCurveCommand Desired ticks " + m_revs*1000 + " ticks.");
            m_drivetrain.m_logger.debug("driveCurveCommand Difference ticks " + ((m_revs*1000)-m_drivetrain.getEncPosition()) + " ticks.");
        }
        return m_pidController.isEnabled() ? m_pidController.onTarget() : true;
    }

    @Override
    public void interrupted()
    {
        m_drivetrain.m_logger.info("driveCurveCommand interrupted");
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
        m_drivetrain.m_logger.info("driveCurveCommand end");
        m_drivetrain.stop();
    }

    // PIDSource -----------------------------------------------------------------------
    @Override
    public void setPIDSourceType(PIDSourceType pidSource)
    {
        if (pidSource != PIDSourceType.kDisplacement)
        {
            m_drivetrain.m_logger.error("driveCurveCommand only supports kDisplacement");
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
        m_drivetrain.driveArcadeDirect(output, m_curveFactor);
    }
}
