package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;
import org.usfirst.frc.team4915.steamworks.commands.ArcadeDriveCommand;
import org.usfirst.frc.team4915.steamworks.sensors.BNO055;
import org.usfirst.frc.team4915.steamworks.sensors.IMUPIDSource;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.RobotDrive;

public class Drivetrain extends SpartronicsSubsystem
{

    private static final int QUAD_ENCODER_CYCLES_PER_REVOLUTION = 250; // Encoder-specific value, for E4P-250-250-N-S-D-D
    private static final int QUAD_ENCODER_TICKS_PER_REVOLUTION = QUAD_ENCODER_CYCLES_PER_REVOLUTION * 4; // This should be one full rotation
    private static final double TURN_MULTIPLIER = -0.55; // Used to make turning smoother
    private static final double MAX_OUTPUT_ROBOT_DRIVE = 0.3;

    private Joystick m_driveStick; // Joystick for ArcadeDrive

    // Port motors
    private CANTalon m_portFollowerMotor;
    private CANTalon m_portMasterMotor;

    // Starboard motors
    private CANTalon m_starboardFollowerMotor;
    private CANTalon m_starboardMasterMotor;

    // Robot drive for ArcadeDrive
    private RobotDrive m_robotDrive;

    // Logger
    public Logger m_logger;

    // IMU
    private BNO055 m_imu;
    // PID Turning with IMU
    private PIDController m_turningPIDController;
    private IMUPIDSource m_imuPIDSource;
    private static final double turnKp = 0.2;
    private static final double turnKi = 0;
    private static final double turnKd = 0.30;
    private static final double turnKf = 0.001;

    public Drivetrain()
    {
        m_logger = new Logger("Drivetrain", Logger.Level.DEBUG);
        m_driveStick = null; // We'll get a value for this after OI is inited

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

            // Reset
            m_portMasterMotor.set(0);
            m_starboardMasterMotor.set(0);

            // Setup the motor so it has an encoder
            m_portMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            m_starboardMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);

            // Set the number of encoder ticks per wheel revolution
            m_portMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_CYCLES_PER_REVOLUTION); // This is actual ticks, so it *shouldn't* be multiplied by 4
            m_starboardMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_CYCLES_PER_REVOLUTION);

            m_portMasterMotor.setInverted(true); // Set direction so that the port motor is *not* inverted
            m_starboardMasterMotor.setInverted(true); // Set direction so that the starboard motor is *not* inverted

            // What do these do, and do we want a variable for them?
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

            // PID Turning with the IMUPIDSource and controller
            m_imuPIDSource = new IMUPIDSource(m_imu); // Make a new IMUPIDSource that we can use with a PIDController
            m_turningPIDController = new PIDController(turnKp, turnKi, turnKd, turnKf,
                    m_imuPIDSource,
                    new PIDOutput()
                    {

                        public void pidWrite(double output)
                        {
                            turn(output); // Turn with the output we get
                        }
                    }); // A PID Controller which has an inline method for PID output
            // Should the numbers below be replace with constants?
            m_turningPIDController.setOutputRange(-1, 1); // Set the output range so that this works with our PercentVbus turning method
            m_turningPIDController.setInputRange(-180, 180); // We do this so that the PIDController takes inputs consistent with our IMU's outputs
            m_turningPIDController.setPercentTolerance(0.6); // This is the tolerance for error for reaching our target

            // Make a new RobotDrive so we can use built in WPILib functions like ArcadeDrive
            m_robotDrive = new RobotDrive(m_portMasterMotor, m_starboardMasterMotor);

            // Set a max speed for RobotDrive
            m_robotDrive.setMaxOutput(MAX_OUTPUT_ROBOT_DRIVE);

            // Debug stuff so everyone knows that we're initialized
            m_logger.info("initialized successfully"); // Tell everyone that the drivetrain is initialized successfully
        }
        catch (Exception e)
        {
            m_logger.exception(e, false);
            m_initialized = false;
        }
        SmartDashboard.putString("Drivetrain_Status", (m_initialized ? 
                        "initialized": "error"));
        //
        // Right now the naming of smart dashboard keys is going by a convention 
        // that I made up, which is 
        //      <SUBSYSTEM>_<INFORMATION SOURCE>_<NAME OF INFORMATION>
        // Comment from dbadb:  
        //   since we have to manually lay out the smartdashboard,
        //   the value of this convention is less than a simpler naming
        //   strategy... If we can make the case for more programmatic
        //   layout (as with loggers), we should revisit this.
    }

    // This is private because it is only being used by the turning PID output
    private void turn(double speed)
    {
        if (initialized())
        {
            if (m_portMasterMotor.getControlMode() == TalonControlMode.PercentVbus
                    && m_starboardMasterMotor.getControlMode() == TalonControlMode.PercentVbus) // Make sure that we're in PercentVbus mode
            {
                m_robotDrive.arcadeDrive(0, speed); // We're using 0 for the "forward" parameter and 'speed' for the "rotation" parameter, basically we are telling RobotDrive to turn with 'speed'
            }
            else
            {
                m_logger.warning("turn attempt with wrong motor control mode (should be PercentVbus)");
            }
        }
    }

    // We call this absolute because we do not turn 180 degrees, we turn *to* 180 degrees
    public void startIMUTurnAbsolute(double degrees)
    {
        if (m_imu.isInitialized())
        {
            m_turningPIDController.reset(); // Reset all of the things that have been passed to the IMU in any previous turns
            m_turningPIDController.setSetpoint(degrees); // Set the point we want to turn to
            m_turningPIDController.enable(); // Enable the PIDController (we should start turning)
        }
        else
        {
            m_logger.warning("can't start an IMU turn because the IMU isn't initalized");
        }
    }

    public void endIMUTurn()
    {
        if (m_turningPIDController.isEnabled())
        {
            m_turningPIDController.disable();
            m_turningPIDController.reset();
        }
    }

    public void debugIMU()
    {
        m_logger.debug("onTarget: " + m_turningPIDController.onTarget() + "heading: " + m_imu.getHeading() + "PID: " + m_turningPIDController.get());
        System.out.println("I should be outputting debug information.");
    }

    public boolean isIMUTurnFinished()
    {
        debugIMU();
        return m_turningPIDController.onTarget();
    }

    public double getIMUNormalizedHeading()
    {
        if (m_imu.isInitialized())
        {
            return m_imu.getNormalizedHeading();
        }
        else
        {
            m_logger.warning("can't get normalized IMU heading because the IMU isn't initalized");
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

    // Not to be confused with CANTalon's setControlMode
    public void setControlMode(TalonControlMode m)
    {
        if (initialized())
        {
            // Change the control mode
            m_portMasterMotor.changeControlMode(m);
            m_starboardMasterMotor.changeControlMode(m);
            // Set so we're at a state we understand
            m_portMasterMotor.set(0);
            m_starboardMasterMotor.set(0);
            // Enable control
            m_portMasterMotor.enableControl();
            m_starboardMasterMotor.enableControl();
            // @TODO Explicitly set maximum output
        }
    }

    public TalonControlMode getControlMode()
    {
        if (initialized())
        {
            return m_portMasterMotor.getControlMode(); // Presumes that all motors have the same control mode
        }
        else
        {
            return TalonControlMode.Disabled;
        }
    }

    // Uses arcade drive
    public void driveArcade()
    {
        if (initialized())
        {
            if (m_portMasterMotor.getControlMode() == TalonControlMode.PercentVbus
                    && m_starboardMasterMotor.getControlMode() == TalonControlMode.PercentVbus)
            {
                double forward = m_driveStick.getY();
                double rotation = -(m_driveStick.getX() * TURN_MULTIPLIER);
                if (Math.abs(forward) > 3 || Math.abs(rotation) > 3)
                {
                    m_robotDrive.arcadeDrive(forward, rotation);
                }
                else
                {
                    m_logger.debug("joystick is within deadzone for both axies so driving is disabled");
                }
            }
            else
            {
                m_logger.warning("drive arcade attempt with wrong motor control mode (should be PercentVbus)");
            }
        }
    }

    public void stop()
    {
        if (initialized())
        {
            m_portMasterMotor.set(0);
            m_starboardMasterMotor.set(0);
            m_portMasterMotor.disableControl();
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

    // Hook called by Robot periodic used to update the SmartDashboard
    public void updatePeriodicHook()
    {
        if (m_imu.isInitialized()) // Make sure that the IMU is initialized
        {
            SmartDashboard.putNumber("Drivetrain_IMU_Heading", this.getIMUNormalizedHeading()); // Send data to the SmartDashboard with the normalized IMU heading
        }
    }

    @Override
    protected void initDefaultCommand()
    {
        if (initialized())
        {
            setDefaultCommand(new ArcadeDriveCommand(this));
        }
    }

}
