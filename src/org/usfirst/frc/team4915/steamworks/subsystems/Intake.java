package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

public class Intake extends SpartronicsSubsystem
{

    private static final double INTAKE_SPEED = 0.75;

    private CANTalon m_intakeMotor;
    private Logger m_logger;
    
    public Intake()
    {
        m_logger = new Logger("Intake", Logger.Level.DEBUG);
        try
        {
            m_intakeMotor = new CANTalon(RobotMap.INTAKE_MOTOR);
            m_intakeMotor.changeControlMode(TalonControlMode.Speed);
            m_logger.info("Intake initialized");
        }
        catch (Exception e)
        {
            m_logger.exception(e, false);
            m_initialized = false;
        }
    }

    @Override
    protected void initDefaultCommand()
    {

    }

    public void setIntake(boolean onOff)
    {
        if (initialized())
        {
            if (onOff)
            {
                m_intakeMotor.set(INTAKE_SPEED);
            }
            else
            {
                m_intakeMotor.set(0);
            }
        }

    }

}
