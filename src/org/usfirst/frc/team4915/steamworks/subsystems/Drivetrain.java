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

    private static final int QUAD_ENCODER_CODES_PER_REVOLUTION = 250; // Encoder-specific value, for E4P-250-250-N-S-D-D
    private static final int QUAD_ENCODER_TICKS_PER_REVOLUTION = QUAD_ENCODER_CODES_PER_REVOLUTION * 4; // This should be one full rotation
    private static final double MAX_OUTPUT_ROBOT_DRIVE = 0.3;
    private static final double WHEEL_DIAMETER = 6;
    private static final double WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER * Math.PI;

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
    
    private double m_target;
    private int m_targetReached;

    // IMU
    private BNO055 m_imu;
    // PID Turning with IMU
    private PIDController m_turningPIDController;
    private IMUPIDSource m_imuPIDSource;
    
    private static final double turnKp = 0.09;
    private static final double turnKi = 0;
    private static final double turnKd = 0.30;
    private static final double turnKf = 0.001;

    public Drivetrain()
    {
        m_logger = new Logger("Drivetrain", Logger.Level.DEBUG);
        m_driveStick = null; // We'll get a value for this after OI is inited
        m_target = 0;
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
            m_portMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_CODES_PER_REVOLUTION); // This is actual ticks, so it *shouldn't* be multiplied by 4
            m_starboardMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_CODES_PER_REVOLUTION);

            m_portMasterMotor.setInverted(true); // Set direction so that the port motor is *not* inverted
            m_starboardMasterMotor.setInverted(true); // Set direction so that the starboard motor is *not* inverted

            // Configure peak output voltages
            m_portMasterMotor.configPeakOutputVoltage(12.0, -12.0);
            m_starboardMasterMotor.configPeakOutputVoltage(12.0, -12.0);

            // Affects maximum acceleration
            m_portMasterMotor.setVoltageRampRate(48);
            m_starboardMasterMotor.setVoltageRampRate(48);

            // Enable break mode
            m_portMasterMotor.enableBrakeMode(true);
            m_starboardMasterMotor.enableBrakeMode(true);

            // Stop
            m_portMasterMotor.set(0);
            m_starboardMasterMotor.set(0);

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

            // Set a max output in volts for RobotDrive
            m_robotDrive.setMaxOutput(MAX_OUTPUT_ROBOT_DRIVE);

            // Debug stuff so everyone knows that we're initialized
            m_logger.info("initialized successfully"); // Tell everyone that the drivetrain is initialized successfully
        }
        catch (Exception e)
        {
            m_logger.exception(e, false);
            m_initialized = false;
        }
        SmartDashboard.putString("Drivetrain_Status", (m_initialized ? "initialized" : "error"));
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

    public double getInchesToRevolutions(double inches)
    {
        return inches / WHEEL_CIRCUMFERENCE;
    }
    
    private double getTicksToRevolutions(int ticks)
    {
        return ticks/QUAD_ENCODER_TICKS_PER_REVOLUTION;
    }

    // Not to be confused with CANTalon's setControlMode
    public void setControlMode(TalonControlMode m,
                                double fwdPeakV, double revPeakV,
                                double P, double I, double D, double F)
    {
        if (initialized())
        {
            // Change the control mode
            int izone = 0; // integration zone
            double rampRate = 36; // measured in volts/sec
            int profile = 0;

            switch(m)
            {
                case PercentVbus:
                    rampRate = 36;
                    break;
                case Position:
                    rampRate = 12;
                    if(F != 0)
                        m_logger.warning("setControlMode: nonzero feedforward term unexpected");
                    break;
                case Speed:
                    rampRate = 12;
                    break;
                default:
                    m_logger.warning("setControlMode: " + m + " unexpected");
                    break;
            }

            m_portMasterMotor.changeControlMode(m);
            m_starboardMasterMotor.changeControlMode(m);

            // Set so we're at a state we understand
            m_portMasterMotor.set(0);
            m_starboardMasterMotor.set(0);

            m_portMasterMotor.setPID(P, I, D, F, izone, rampRate, profile);
            m_starboardMasterMotor.setPID(P, I, D, F, izone, rampRate, profile);
            
            m_portMasterMotor.setProfile(0); // defensive programming
            m_starboardMasterMotor.setProfile(0);

            // Explicitly set maximum output
            m_portMasterMotor.configPeakOutputVoltage(fwdPeakV, revPeakV);
            m_starboardMasterMotor.configPeakOutputVoltage(fwdPeakV, revPeakV);            
        }
    }
    
    // resetPosition: resets the sense/measurement of current position
    //  In theory this should be the same as resetEncoder
    public void resetPosition()
    {
        switch(getControlMode())
        {
            case Position:
                m_portMasterMotor.setPosition(0);
                m_starboardMasterMotor.setPosition(0);
                performDelay();
                if(m_portMasterMotor.getPosition() != 0 ||
                   m_starboardMasterMotor.getPosition() != 0)
                 {
                      m_logger.warning("resetPosition latency!");
                 }
                break;
            case Speed:
            case PercentVbus:
                resetEncoder();
            default:
                m_logger.warning("Can't resetPosition for current control mode");
                break;
        }
    }
    
    private void resetEncoder()
    {
        m_portMasterMotor.setEncPosition(0);
        m_starboardMasterMotor.setEncPosition(0);
        performDelay();
        if(m_portMasterMotor.getEncPosition() != 0 ||
           m_starboardMasterMotor.getEncPosition() != 0)
        {
             m_logger.warning("resetEncoder latency!");
        }
    }
   
    private void performDelay()
    {
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            m_logger.exception(e, false);
        } 
    }
    
    public void setClosedLoopTargetRevolutions(double tg)
    {
        switch(getControlMode())
        {
            case Speed:
            case Position:
                if(m_target != tg)
                {
                    m_target = tg;
                    m_targetReached = 0;
                    m_logger.debug("new target: " + tg);
                }
                m_portMasterMotor.set(tg);
                m_starboardMasterMotor.set(tg);
                break;
            case PercentVbus:
            default:
                m_logger.warning("Can't set closed loop target for current control mode");
                break;
        }
    }
    
    public boolean closedLoopTargetIsReached(double epsilon)
    {
        boolean result = true;
        switch(getControlMode())
        {
            case Speed:
            case Position:
                if(Math.abs(m_portMasterMotor.get() - m_target) < epsilon &&
                   Math.abs(m_starboardMasterMotor.get() - m_target) < epsilon)
                {
                    m_targetReached++;
                    if(m_targetReached < 5)
                        result = false;
                    // otherwise true and we're there!
                }
                else
                {
                    m_targetReached = 0;
                    result = false;
                }
                break;
            case PercentVbus:
            default:
                m_logger.warning("Can't check target for current control mode");
                break;
        }
        return result;
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
                double rotation = m_driveStick.getX();
                if (Math.abs(forward) < 0.02 && Math.abs(rotation) < 0.02)
                {
                    // To keep motor saftey happy
                    forward = 0.0;
                    rotation = 0.0;
                }
                m_robotDrive.arcadeDrive(forward, rotation);
            }
            else
            {
                m_logger.warning("drive arcade attempt with wrong motor control mode (should be PercentVbus)");
            }
        }
    }
    
    public void driveArcadeDirect(double fwd, double rotation)
    {
        if(initialized())
        {
            m_robotDrive.arcadeDrive(fwd, rotation);
        }
    }

    public void stop()
    {
        if (initialized())
        {
            m_portMasterMotor.set(0);
            m_starboardMasterMotor.set(0);
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
    
    // getClosedLoopValue: returns the average of the current motor states
    //  NB: use with care, especially for PID control. If the two motor
    //      encoders are way out of sync, we could get into trouble.
    public double getClosedLoopValue(boolean takeAverage) 
    {
        double result = 0;
        if(initialized())
        {
            switch(getControlMode())
            {
                case Speed:
                case Position:
                    if(takeAverage)
                    {
                        double x = m_portMasterMotor.get();
                        double y = m_starboardMasterMotor.get();
                        result = (x + y) * .5; // we assume we're driving straight
                    }
                    else
                    {
                        result = m_portMasterMotor.get();
                    }
                    break;
                default:
                    m_logger.warning("can't get closed loop value for current control mode");
                    break; // fall through, return 0
            }
        }
        return result;
    }

    public double getOpenLoopValue()
    {
        double result = 0;
        if(initialized())
        {
            switch(getControlMode())
            {
                case PercentVbus:
                    result = getTicksToRevolutions(this.getEncPosition());
                    break;
                case Speed:
                case Position:
                default:
                    m_logger.warning("can't get open loop value for current control mode");
                    break; // fall through, return 0
            }           
        }
        return result;
    }
    
    private int getEncPosition()
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
