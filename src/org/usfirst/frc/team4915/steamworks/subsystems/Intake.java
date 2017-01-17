package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.Robot;
import org.usfirst.frc.team4915.steamworks.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

public class Intake extends SpartronicsSubsystem
{

    private static final double INTAKE_SPEED = 0.75;

    private CANTalon m_intakeMotor;

    public Intake(Robot robot)
    {
        try
        {
            m_intakeMotor = new CANTalon(RobotMap.INTAKE_MOTOR);
            m_intakeMotor.changeControlMode(TalonControlMode.Speed);
            Logger.getInstance().info("Intake initialized");
        }
        catch (Exception e)
        {
            Logger.getInstance().exception(e, false);
            m_successful = false;
        }
    }

    @Override
    protected void initDefaultCommand()
    {

    }

    public void setIntake(boolean onOff)
    {
        if (wasSuccessful())
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
