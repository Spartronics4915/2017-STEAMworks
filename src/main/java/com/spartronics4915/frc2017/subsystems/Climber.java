// IDEA: Structure thing-to-do: eliminate Constants.java.
// Constants go in their own respective subsystem / command class.
// Easier for newcomers, easier to find, easier naming.

package com.spartronics4915.frc2017.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.spartronics4915.frc2017.RobotMap;

public class Climber extends SpartronicsSubsystem
{
    private static Climber mInstance = null;

    public static Climber getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new Climber();
        }
        return mInstance;
    }

    private TalonSRX mClimberMotor;
    private final double mClimbSpeed = 0.90; // With respect to PercentOutput

    private Climber()
    {
        // boolean success = false;
        try
        {
            mClimberMotor = new TalonSRX(RobotMap.CLIMBER_MOTOR);
            mClimberMotor.configFactoryDefault();
            // success = true;
        }
        catch (Exception e)
        {
            // success = false;
            // logException("Could not instantiate Climber subsystem: " + e);
        }
        // logInitialized(success);
    }

    public void climb()
    {
        mClimberMotor.set(ControlMode.PercentOutput, mClimbSpeed);
    }

    public void slow()
    {
        mClimberMotor.set(ControlMode.PercentOutput, mClimbSpeed / 2);
    }

    public void stop()
    {
        mClimberMotor.set(ControlMode.PercentOutput, 0);
    }

    public double getClimberCurrent()
    {
        return mClimberMotor.getOutputCurrent();
    }
}
