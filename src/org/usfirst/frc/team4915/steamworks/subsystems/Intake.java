package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake extends SpartronicsSubsystem
{

    public static enum State
    {
        OFF,
        ON,
        REVERSE,
        COUNT
    }

    //private static final int INTAKE_QUAD_ENCODER_TICKS_PER_REVOLUTION = 9000;

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
            //declares the encoder
            m_intakeMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            //m_intakeMotor.configEncoderCodesPerRev(INTAKE_QUAD_ENCODER_TICKS_PER_REVOLUTION);
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

    // gives encoder ticks to the Counter command
    public int getEncoder()
    {
        return m_intakeMotor.getEncPosition();
    }

    public void setIntake(State state)
    {
        if (initialized())
        {
            // System.out.println(m_intakeEncoder.get());
            //Records Intake status in dashboard and the logger
            SmartDashboard.putString("Intake Status", state.name());
            m_logger.info("Intake Status" + state.name());
            //Changes the current state of the Intake
            switch (state)
            {
                /*
                 * Modes Within the Intake Class:
                 * On: Currently runs in speed mode
                 * Reverse: Opposite direction of On
                 * Count: On, but counts encoder ticks
                 * Off: turns intake off
                 */
                case ON:
                    m_logger.info("Intake motor on");
                    m_intakeMotor.set(INTAKE_SPEED);
                    break;
                case REVERSE:
                    m_logger.info("Intake motor in reverse");
                    m_intakeMotor.set(-INTAKE_SPEED);
                    break;
                case COUNT:
                    m_logger.info("Intake motor in Count Mode");
                    m_intakeMotor.set(INTAKE_SPEED);
                    break;
                case OFF:
                default:
                    m_logger.info("Intake motor off");
                    m_intakeMotor.set(0);
            }
        }
    }
}
