package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber extends SpartronicsSubsystem
{

    public static enum State
    {
        OFF,
        ON,
        SLOW
    }

    private static final double CLIMBER_SPEED = 0.75;

    private CANTalon m_climberMotor;

    private Logger m_logger;

    public Climber()
    {
        m_logger = new Logger("Climber", Logger.Level.DEBUG);
        try
        {
            m_climberMotor = new CANTalon(RobotMap.CLIMBER_MOTOR);
            m_climberMotor.changeControlMode(TalonControlMode.PercentVbus);
            m_logger.info("Climber initialized");
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

    public void setClimber(State state)
    {
        if (initialized())
        {
            SmartDashboard.putString("Climber Status", state.name());
            m_logger.info("Climber Status" + state.name());
            switch (state)
            {
                case ON:
                    m_logger.info("Climber motor on");
                    m_climberMotor.set(CLIMBER_SPEED);
                    break;
                case SLOW:
                    m_logger.info("Climber motor in slow");
                    m_climberMotor.set(CLIMBER_SPEED/2);
                    break;
                case OFF:
                default:
                    m_logger.info("Climber motor off");
                    m_climberMotor.set(0);
            }
        }

    }
}
