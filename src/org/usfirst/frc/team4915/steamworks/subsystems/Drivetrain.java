package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;
import org.usfirst.frc.team4915.steamworks.commands.ArcadeDriveCommand;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;

public class Drivetrain extends SpartronicsSubsystem
{

    public static final int QUAD_ENCODER_TICKS_PER_REVOLUTION = 250;
    private Joystick m_driveStick;

    private CANTalon m_portFollowerMotor;
    private CANTalon m_portMasterMotor;

    private CANTalon m_starboardFollowerMotor;
    private CANTalon m_starboardMasterMotor;

    private RobotDrive m_robotDrive;
    private Logger m_logger;

    public Drivetrain()
    {
        m_logger = new Logger("Drivetrain", Logger.Level.DEBUG);
        m_driveStick = null; // we'll get a value for this after OI is inited

        try
        {
            m_portFollowerMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_PORT_FOLLOWER);
            m_portMasterMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_PORT_MASTER);
            m_starboardFollowerMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_STARBOARD_FOLLOWER);
            m_starboardMasterMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_STARBOARD_MASTER);

            m_portMasterMotor.changeControlMode(TalonControlMode.PercentVbus);
            m_portFollowerMotor.changeControlMode(TalonControlMode.Follower);
            m_portFollowerMotor.set(m_portMasterMotor.getDeviceID());

            m_starboardMasterMotor.changeControlMode(TalonControlMode.PercentVbus);
            m_starboardFollowerMotor.changeControlMode(TalonControlMode.Follower);
            m_starboardFollowerMotor.set(m_starboardMasterMotor.getDeviceID());

            m_portMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            m_starboardMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            m_portMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);
            m_starboardMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);
            
            m_portMasterMotor.setInverted(false); // Set direction so that the port motor is inverted *not* inverted
            m_starboardMasterMotor.setInverted(false); // Set direction so that the starboard motor is *not* inverted

            m_portMasterMotor.setVoltageRampRate(48);
            m_starboardMasterMotor.setVoltageRampRate(48);
            
            m_portMasterMotor.setEncPosition(0);
            m_starboardMasterMotor.setEncPosition(0);

            m_robotDrive = new RobotDrive(m_portMasterMotor, m_starboardMasterMotor);
            m_logger.info("initialized successfully");
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

    public void driveArcade(double forward, double rotation)
    {
        if (initialized())
        {
            m_robotDrive.arcadeDrive(forward, rotation);
        }
    }
    
    public void driveTicksTest(int ticks) {
        if (initialized()) {
            m_portMasterMotor.changeControlMode(TalonControlMode.PercentVbus);
            if (m_portMasterMotor.getEncPosition() < ticks) {
                m_portMasterMotor.set(0.5);
            } else {
                m_portMasterMotor.set(0);
            }
        }
    }
    
    public void resetEncPosition() {
        m_portMasterMotor.setEncPosition(0);
        m_starboardMasterMotor.setEncPosition(0);
    }

    @Override
    protected void initDefaultCommand()
    {
        if (initialized())
        {
            setDefaultCommand(new ArcadeDriveCommand(this, m_driveStick));
        }
    }

}
