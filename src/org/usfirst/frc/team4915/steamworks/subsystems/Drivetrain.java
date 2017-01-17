package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;
import org.usfirst.frc.team4915.steamworks.commands.ManualDriveCommand;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;

public class Drivetrain extends SpartronicsSubsystem
{

    public static final int QUAD_ENCODER_TICKS_PER_REVOLUTION = 9000;
    private Joystick m_driveStick;

    private CANTalon m_leftFollowerMotor;
    private CANTalon m_leftMasterMotor;

    private CANTalon m_rightFollowerMotor;
    private CANTalon m_rightMasterMotor;

    private RobotDrive m_robotDrive;
    private Logger m_logger;

    public Drivetrain()
    {
        m_logger = new Logger("Drivetrain", Logger.Level.DEBUG);
        m_driveStick = null; // we'll get a value for this after OI is inited

        try
        {
            m_leftFollowerMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_LEFT_FOLLOWER);
            m_leftMasterMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_LEFT_MASTER);
            m_rightFollowerMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_RIGHT_FOLLOWER);
            m_rightMasterMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_RIGHT_MASTER);

            m_leftMasterMotor.changeControlMode(TalonControlMode.Speed);
            m_leftFollowerMotor.changeControlMode(TalonControlMode.Follower);
            m_leftFollowerMotor.set(m_leftMasterMotor.getDeviceID());

            m_rightMasterMotor.changeControlMode(TalonControlMode.Speed);
            m_rightFollowerMotor.changeControlMode(TalonControlMode.Follower);
            m_rightFollowerMotor.set(m_rightMasterMotor.getDeviceID());

            m_leftMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            m_rightMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            m_leftMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);
            m_rightMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);

            m_leftMasterMotor.setVoltageRampRate(48);
            m_rightMasterMotor.setVoltageRampRate(48);

            m_robotDrive = new RobotDrive(m_leftFollowerMotor, m_leftMasterMotor, m_rightFollowerMotor, m_rightMasterMotor);
            Logger.getInstance().info("Drivetrain initialized");
        }
        catch (Exception e)
        {
            m_logger.exception(e, false);
            m_initialized = false;
            return;
        }
    }
    
    public void setDriveStick(Joystick s)
    {
        m_driveStick = s;
    }

    public void drive(double forward, double rotation)
    {
        if (initialized())
        {
            m_robotDrive.arcadeDrive(forward, rotation);
        }
    }

    @Override
    protected void initDefaultCommand()
    {
        if (initialized())
        {
            setDefaultCommand(new ManualDriveCommand(this, m_driveStick));
        }
    }

}
