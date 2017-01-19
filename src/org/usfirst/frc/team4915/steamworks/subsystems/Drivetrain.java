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
    // Public finals
    public static final double SLOW_MULTIPLIER = 0.3;
    public static final double MEDIUM_MULTIPLIER = 1;
    public static final double FAST_MULTIPLIER = 1.7;
    public static final int QUAD_ENCODER_TICKS_PER_REVOLUTION = 9000;
    
    // Private member variables
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
        m_driveStick = null; // We'll get a value for this after OI is initialized

        try
        {
            m_portFollowerMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_PORT_FOLLOWER); // Set motors
            m_portMasterMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_PORT_MASTER);
            m_starboardFollowerMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_STARBOARD_FOLLOWER);
            m_starboardMasterMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_STARBOARD_MASTER);

            m_portMasterMotor.changeControlMode(TalonControlMode.PercentVbus);
            m_portFollowerMotor.changeControlMode(TalonControlMode.Follower); // Set to follower
            m_portFollowerMotor.set(m_portMasterMotor.getDeviceID());

            m_starboardMasterMotor.changeControlMode(TalonControlMode.PercentVbus);
            m_starboardFollowerMotor.changeControlMode(TalonControlMode.Follower); // Set to follower
            m_starboardFollowerMotor.set(m_starboardMasterMotor.getDeviceID());

            m_portMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            m_starboardMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            m_portMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);
            m_starboardMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);
            
            m_portMasterMotor.setInverted(false); // Set direction so that the port motor is inverted *not* inverted
            m_starboardMasterMotor.setInverted(false); // Set direction so that the starboard motor is *not* inverted

            m_portMasterMotor.setVoltageRampRate(48);
            m_starboardMasterMotor.setVoltageRampRate(48);

            m_robotDrive = new RobotDrive(m_portMasterMotor, m_starboardMasterMotor); // Can't be follower motors
            
            m_logger.info("Drivetrain initialized");
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

    public void driveArcade()
    {
        if (initialized())
        {
            double forward = m_driveStick.getX();
            double rotation = m_driveStick.getY();
            m_robotDrive.arcadeDrive(forward, rotation);
        }
    }
    
    public void setControlMode(int mode) {
        m_starboardMasterMotor.setControlMode(mode);
        m_portMasterMotor.setControlMode(mode);
    }
    
    public TalonControlMode getControlMode(CANTalon motor) {
        return motor.getControlMode();
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
