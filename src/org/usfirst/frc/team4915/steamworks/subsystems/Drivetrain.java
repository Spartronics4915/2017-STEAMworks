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
    public static final int QUAD_ENCODER_TICKS_PER_INCH = QUAD_ENCODER_TICKS_PER_REVOLUTION / (int) (Math.PI * 6);
    private Joystick m_driveStick; //6 is the diameter of the wheels

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

            m_portMasterMotor.changeControlMode(TalonControlMode.Speed);
            m_portFollowerMotor.changeControlMode(TalonControlMode.Follower);
            m_portFollowerMotor.set(m_portMasterMotor.getDeviceID());

            m_starboardMasterMotor.changeControlMode(TalonControlMode.Speed);
            m_starboardFollowerMotor.changeControlMode(TalonControlMode.Follower);
            m_starboardFollowerMotor.set(m_starboardMasterMotor.getDeviceID());

            m_portMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            m_starboardMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            m_portMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);
            m_starboardMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);

            m_portMasterMotor.setVoltageRampRate(48);
            m_starboardMasterMotor.setVoltageRampRate(48);

            m_robotDrive = new RobotDrive(m_portFollowerMotor, m_portMasterMotor, m_starboardFollowerMotor, m_starboardMasterMotor);
            m_logger.info("Drivetrain initialized");
        }
        catch (Exception e)
        {
            m_logger.exception(e, false);
            m_initialized = false;
            return;
        }
    }
    
    public void init() {
        this.setMaxOutput(25);
        // reset encoders
        m_starboardMasterMotor.setEncPosition(0);
        m_portMasterMotor.setEncPosition(0);
        
        //change the control mode to POSITION
        m_portMasterMotor.changeControlMode(CANTalon.TalonControlMode.Position);
        m_starboardMasterMotor.changeControlMode(CANTalon.TalonControlMode.Position);
    }
    
    public void setDesiredDistance(double ticks)
    {
        m_portMasterMotor.set(ticks);
        m_starboardMasterMotor.set(ticks);
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

    public int inchesToTicks(double inches)
    {
        return (int) (inches * QUAD_ENCODER_TICKS_PER_INCH);
    }

    @Override
    protected void initDefaultCommand()
    {
        if (initialized())
        {
            setDefaultCommand(new ArcadeDriveCommand(this, m_driveStick));
        }
    }
    
    public void driveStraight(double value)
    {
        m_robotDrive.setMaxOutput(value);
    }
    
    //checks if encoders are at 0
    public boolean isEncPositionZero() {
        return ((m_portMasterMotor.getEncPosition() == 0) 
                && (m_starboardMasterMotor.getEncPosition() == 0));
    }
    
    //checks if both encoders reached a certain number of ticks
    public boolean isLocationReached(double m_desiredDistanceTicks) {
        return (Math.abs(m_portMasterMotor.getEncPosition()) >= m_desiredDistanceTicks
                || Math.abs(m_starboardMasterMotor.getEncPosition()) >= m_desiredDistanceTicks);
    }

    public void stop()
    {
        m_robotDrive.stopMotor();
    }
    
    public void setMaxOutput(int n)
    {
        
    }
}
