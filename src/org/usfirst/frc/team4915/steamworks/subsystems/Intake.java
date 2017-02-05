package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake extends SpartronicsSubsystem
{

    public static enum State
    {
        OFF,
        ON,
        REVERSE
    }

    private static final double INTAKE_SPEED = 0.75;

    private CANTalon m_intakeMotor;

    private Logger m_logger;

    public Intake()
    {
        m_logger = new Logger("Intake", Logger.Level.DEBUG);
        try
        {
            m_intakeMotor = new CANTalon(RobotMap.INTAKE_MOTOR);
            m_intakeMotor.changeControlMode(TalonControlMode.PercentVbus);
            m_logger.info("Intake initialized");
            SmartDashboard.putString("Intake Status: ", "Initialized");
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

    public void setIntake(State state)
    {
        if (initialized())
        {
            //Records Intake status in dashboard and the logger
            SmartDashboard.putString("Intake State: ", state.name());
            m_logger.info("Intake Status" + state.name());
            //Changes the current state of the Intake
            switch (state)
            {
                /*
                 * Modes Within the Intake Class:
                 * On: Currently runs in speed mode
                 * Reverse: Opposite direction of On
                 * Off: turns intake off
                 */
                case ON:
                    m_logger.info("Intake motor on");
                    m_intakeMotor.set(INTAKE_SPEED);
                    SmartDashboard.putNumber("Intake Speed", INTAKE_SPEED);
                    break;
                case REVERSE:
                    m_logger.info("Intake motor in reverse");
                    m_intakeMotor.set(-INTAKE_SPEED);
                    SmartDashboard.putNumber("Intake Speed", -INTAKE_SPEED);
                    break;
                case OFF:
                default:
                    m_logger.info("Intake motor off");
                    m_intakeMotor.set(0);
                    SmartDashboard.putNumber("Intake Speed", 0);
            }
        }
    }
}
