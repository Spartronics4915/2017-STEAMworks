package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber extends SpartronicsSubsystem
{

    //The 'SHLOW' is cutting the the speed of 'ON' in half
    // The speed of ON is currently .75

    public static enum State
    {
        OFF,
        ON,
        SLOW
    }

    private CANTalon m_climberMotor;

    public Logger m_logger;

    public Climber()
    {
        m_logger = new Logger("Climber", Logger.Level.DEBUG);
        try

        {
            m_climberMotor = new CANTalon(RobotMap.CLIMBER_MOTOR);
            m_climberMotor.changeControlMode(TalonControlMode.PercentVbus);
            m_logger.info("Climber initialized");
            SmartDashboard.putString("Climber Status", "Initialized");
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

    public double getClimberCurrent()
    {
        return m_climberMotor.getOutputCurrent();
    }

    public String getStateString(State s)
    {
        switch (s)
        {
            case OFF:
                return "OFF";
            case ON:
                return "ON";
            case SLOW:
                return "SLOW";
        }
        return null;
    }

    public double getClimberSpeed(State s)
    {
        double speed = SmartDashboard.getNumber("Climber Speed", .75);
        switch (s)
        {
            case ON:
                return speed;
            case SLOW:
                return speed / 2;
            case OFF:
            default:
                return 0;
        }
    }

    public void setClimber(State s) // NB: this is called during execute! (limit log / dashboard traffic)
    {
        if (initialized())
        {
            m_climberMotor.set(getClimberSpeed(s));
        }

    }
}
