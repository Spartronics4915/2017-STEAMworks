package com.spartronics4915.frc2017.subsystems;

import com.spartronics4915.frc2017.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Intake extends SpartronicsSubsystem
{
    private static Intake mInstance = null;

    public static Intake getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new Intake();
        }
        return mInstance;
    }

    private TalonSRX mIntakeMotor;
    private final double mIntakeSpeed = 0.95; // With respect to PercentOutput

    private Intake()
    {
        // boolean success = false;
        try
        {
            mIntakeMotor = new TalonSRX(RobotMap.INTAKE_MOTOR);
            mIntakeMotor.configFactoryDefault();
            // success = true;
        }
        catch (Exception e)
        {
            // success = false;
            // logException("Could not instantiate Intake: ", e);
        }
        // logInitialized(success);
    }

    public void intake()
    {
        mIntakeMotor.set(ControlMode.PercentOutput, mIntakeSpeed);
    }

    public void reverse()
    {
        mIntakeMotor.set(ControlMode.PercentOutput, -mIntakeSpeed);
    }

    public void stop()
    {
        mIntakeMotor.set(ControlMode.PercentOutput, 0);
    }

    public double getIntakeCurrent()
    {
        return mIntakeMotor.getOutputCurrent();
    }
}
