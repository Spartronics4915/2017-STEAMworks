package com.spartronics4915.frc2017.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.spartronics4915.frc2017.RobotMap;

public class Launcher extends SpartronicsSubsystem
{
    private static Launcher mInstance = null;

    public static Launcher getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new Launcher();
        }
        return mInstance;
    }

    private TalonSRX mLauncherMotor;
    private TalonSRX mAgitatorMotor;
    private final double mLauncherVelocity = 2880.0; // The old number was 2940 (changed at GP)
    private final double mAgitatorSpeed = 0.7; // Speed == PercentOutput, Velocity == Velocity

    private Launcher()
    {
        // boolean success = false;
        try
        {
            mLauncherMotor = new TalonSRX(RobotMap.LAUNCHER_MOTOR);
            mLauncherMotor.configFactoryDefault();
            mAgitatorMotor = new TalonSRX(RobotMap.AGITATOR_MOTOR);
            mAgitatorMotor.configFactoryDefault();
            // success = true
        }
        catch (Exception e)
        {
            // success = false;
            // logException("Could not instantiate Launcher: ", e);
        }
        // logInitialized(success);
    }

    public void launch()
    {
        mLauncherMotor.set(ControlMode.Velocity, mLauncherVelocity);
        mAgitatorMotor.set(ControlMode.PercentOutput, mAgitatorSpeed);
    }

    public void reverse()
    {
        mLauncherMotor.set(ControlMode.Velocity, -mLauncherVelocity);
        mAgitatorMotor.set(ControlMode.PercentOutput, -mAgitatorSpeed);
    }

    public void stop()
    {
        mLauncherMotor.set(ControlMode.PercentOutput, 0);
        mAgitatorMotor.set(ControlMode.PercentOutput, 0);
    }

    public double getAgitatorPosition()
    {
        return mAgitatorMotor.getSensorCollection().getPulseWidthPosition();
    }

    public double getRPM()
    {
        double mCurrentSpeed = mAgitatorMotor.getSensorCollection().getQuadratureVelocity(); // Conversion from native units / 100ms to rpm
        return mCurrentSpeed * 600.0 / 4096.0; // FIXME: This presumes that Quadratures are simply more accurate Encs (encoders), which is likely _not_ true.
    }
}
