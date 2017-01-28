package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;
import org.usfirst.frc.team4915.steamworks.commands.ArcadeDriveCommand;
import org.usfirst.frc.team4915.steamworks.sensors.BNO055;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;

public class Drivetrain extends SpartronicsSubsystem
{

    private static final int QUAD_ENCODER_CYCLES_PER_REVOLUTION = 250; // Encoder-specific value, for E4P-250-250-N-S-D-D
    private static final int QUAD_ENCODER_TICKS_PER_REVOLUTION = QUAD_ENCODER_CYCLES_PER_REVOLUTION*4; // This should be one full rotation
    private static final double TURN_MULTIPLIER = -0.55; // Used to make turning smoother

    private Joystick m_driveStick;

    private CANTalon m_portFollowerMotor;
    private CANTalon m_portMasterMotor;

    private CANTalon m_starboardFollowerMotor;
    private CANTalon m_starboardMasterMotor;

    private RobotDrive m_robotDrive;
    private Logger m_logger;
    
    private BNO055 m_imu;

    public Drivetrain()
    {
        m_logger = new Logger("Drivetrain", Logger.Level.DEBUG);
        m_driveStick = null; // we'll get a value for this after OI is inited

        try
        {
            // Create new CANTalons for all our drivetrain motors
            m_portFollowerMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_PORT_FOLLOWER);
            m_portMasterMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_PORT_MASTER);
            m_starboardFollowerMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_STARBOARD_FOLLOWER);
            m_starboardMasterMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_STARBOARD_MASTER);

            // Set the Master motor to a control mode and make the follower a follower
            m_portMasterMotor.changeControlMode(TalonControlMode.PercentVbus);
            m_portFollowerMotor.changeControlMode(TalonControlMode.Follower);
            m_portFollowerMotor.set(m_portMasterMotor.getDeviceID()); // Sets the master motor for the follower

            // Sets the Master motor to a control mode and make a follower a follower
            m_starboardMasterMotor.changeControlMode(TalonControlMode.PercentVbus);
            m_starboardFollowerMotor.changeControlMode(TalonControlMode.Follower);
            m_starboardFollowerMotor.set(m_starboardMasterMotor.getDeviceID()); // Sets the master motor for the follower

            // Setup the motor so it has an encoder
            m_portMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            m_starboardMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);

            // Set the number of encoder ticks per wheel revolution
            m_portMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);
            m_starboardMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);

            m_portMasterMotor.setInverted(false); // Set direction so that the port motor is *not* inverted
            m_starboardMasterMotor.setInverted(false); // Set direction so that the starboard motor is *not* inverted

            m_portMasterMotor.setVoltageRampRate(48);
            m_starboardMasterMotor.setVoltageRampRate(48);

            // Enable break mode
            m_portMasterMotor.enableBrakeMode(true);
            m_starboardMasterMotor.enableBrakeMode(true);

            // Reset the encoder position
            m_portMasterMotor.setEncPosition(0);
            m_starboardMasterMotor.setEncPosition(0);
            
            // Get an instance of the BNO055 IMU
            m_imu = BNO055.getInstance(BNO055.opmode_t.OPERATION_MODE_IMUPLUS,
                    BNO055.vector_type_t.VECTOR_EULER);

            // Make a new RobotDrive so we can use built in WPILib functions like ArcadeDrive
            m_robotDrive = new RobotDrive(m_portMasterMotor, m_starboardMasterMotor);
            m_logger.info("initialized successfully"); // Tell everyone that the drivetrain is initialized successfully
        }
        catch (Exception e)
        {
            m_logger.exception(e, false);
            m_initialized = false;
            return;
        }
    }
    
    public double getIMUNormalizedHeading() 
    {
        if (m_imu.isInitialized()) 
        {
            return m_imu.getNormalizedHeading();
        }
        else 
        {
            m_logger.warning("can't get IMU normalized heading because the IMU isn't initalized.");
            return 0;
        }
    }

    public void setDriveStick(Joystick s)
    {
        m_driveStick = s;
    }

    public int getTicksPerRev()
    {
        return QUAD_ENCODER_TICKS_PER_REVOLUTION;
    }

    public void setControlMode(TalonControlMode m) // Not to be confused with CANTalon's setControlMode
    {
        if (initialized())
        {
            // Change the control mode
            m_portMasterMotor.changeControlMode(m);
            m_starboardMasterMotor.changeControlMode(m);
            // Set the position so we're at a state we understand
            m_portMasterMotor.set(0);
            m_starboardMasterMotor.set(0);
            // @TODO Explicitly set maximum output
        }
    }

    public TalonControlMode getControlMode()
    {
        if (initialized())
        {
            return m_portMasterMotor.getControlMode(); // presumes that all motors are the same
        }
        else
        {
            return TalonControlMode.Disabled;
        }
    }

    public void driveArcade() // Uses arcadeDrive
    {
        if (initialized())
        {
            if (m_portMasterMotor.getControlMode() == TalonControlMode.PercentVbus
                    && m_starboardMasterMotor.getControlMode() == TalonControlMode.PercentVbus)
            {
                double forward = m_driveStick.getY();
                double rotation = m_driveStick.getX() * TURN_MULTIPLIER;
                m_robotDrive.arcadeDrive(forward, rotation);
            }
            else
            {
                m_logger.warning("drive arcade attempt with wrong motor control mode (should be PercentVbus)");
            }
        }
    }

    public void driveStraight(double value)
    {
        if (initialized())
        {
            m_portMasterMotor.set(value);
            m_starboardMasterMotor.set(value);
        }
    }

    public void stop()
    {
        if (initialized())
        {
            m_portMasterMotor.set(0);
            m_starboardMasterMotor.set(0);
            ;
        }
    }

    public void driveTicksTest(int startingticks, int ticks)
    {
        if (initialized())
        {
            if (m_portMasterMotor.getEncPosition() < startingticks + ticks)
            {
                m_portMasterMotor.set(0.5);
            }
            else
            {
                m_portMasterMotor.set(0);
            }
        }
    }

    public void resetEncPosition() // WARNING: this routine doesn't take effect immediately...
    {
        if (initialized())
        {
            m_portMasterMotor.setEncPosition(0);
            m_starboardMasterMotor.setEncPosition(0);
        }
    }

    public int getEncPosition()
    {
        // XXX: for now we only return one enc position... Should caller need access to
        //      a specific motor, we should add a parameter
        if (initialized())
        {
            return m_portMasterMotor.getEncPosition();
        }
        else
        {
            return 0;
        }
    }
    
    public void updatePeriodicHook() { // Hook called by Robot periodic used to update the SmartDash board
        if (m_imu.isInitialized()) // Make sure that the IMU is initalized
        {
            SmartDashboard.putDouble("Drivetrain_IMU_Heading",this.getIMUNormalizedHeading()); // Send data to the SmartDashboard with the normalized IMU heading
        }
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
