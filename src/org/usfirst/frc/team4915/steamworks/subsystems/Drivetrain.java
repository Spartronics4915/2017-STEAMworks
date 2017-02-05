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

    /**
     * Encoder is connected directly into the wheel shaft -- 1:1 scaling encoder
     * values
     * Encoder is 250 cycles per revolution. Multiply by 4 to get 1000 encoder
     * ticks per wheel revolution at Talon.
     * 
     * @TODO: Verify experimentally by rotating a wheel 360 degrees and
     *        comparing before and after tick counts.
     *        Target and sampled position is passed into the equation in native
     *        units (i.e. ticks), unless QuadratureEncoder correctly
     *        configured with configEncoderCodesPerRev() [TalonSRX Programming
     *        17.2.1]
     */
    // Encoder-specific value, for E4P-250-250-N-S-D-D
    private static final int QUAD_ENCODER_CYCLES_PER_REVOLUTION = 250;
    // Full rotation in ticks -- this is the native units
    private static final int QUAD_ENCODER_TICKS_PER_REVOLUTION = QUAD_ENCODER_CYCLES_PER_REVOLUTION * 4;
    // Number of ticks per inch -- we have 6" wheels
    private static final int QUAD_ENCODER_TICKS_PER_INCH = (int) (QUAD_ENCODER_TICKS_PER_REVOLUTION / (Math.PI * 6));
    // Used to make turning smoother
    private static final double TURN_MULTIPLIER = -0.55;

    private static final double MAX_OUTPUT_ROBOT_DRIVE = 0.3;
    // Circumference of our wheel
    private static final double WHEEL_CIRCUMFERENCE = (Math.PI * 6);

    private static final int ALLOWED_CLOSED_LOOP_ERROR = 50;

    private static final int MAX_ENC_ERROR = 200;
    private static final double WHEEL_DIAMETER = 6;
    private static final int MAX_OUTPUT_VOLTAGE = 12;

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
    private static final double turnKp = 0.09;
    private static final double turnKi = 0;
    private static final double turnKd = 0.30;
    private static final double turnKf = 0.001;

    public Drivetrain()
    {
        m_logger = new Logger("Drivetrain", Logger.Level.DEBUG);
        m_driveStick = null; // We'll get a value for this after OI is inited

        try
        {
            m_logger.info("initializing Drivetrain");
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

            /*
             * Once a "Feedback Device" is selected, the "Sensor Position" and
             * "Sensor Velocity" signals will update the output of the selected feedback
             * device. It will also be multiplied by (-1) if "Reverse Feedback Sensor"
             * is asserted programmatically. 
             * [TalonSRX programming 7]
             */
            m_portMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            m_starboardMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);

            // Set the number of encoder ticks per wheel revolution -- used for unit scaling (rotations & RPM)
            m_portMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_CYCLES_PER_REVOLUTION);
            m_starboardMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_CYCLES_PER_REVOLUTION);

            // Configure peak output voltages
            //            m_portMasterMotor.configPeakOutputVoltage(12.0, -12.0);
            //            m_starboardMasterMotor.configPeakOutputVoltage(12.0, -12.0);

            /*
             * Typically reverseSensor() is enough to keep sensor in-phase w/
             * motor
             * reverseOutput() reverses the output of the closed-loop math as an
             * alternative to flip motor direction
             * TalonSRX programming section 7.4
             */
            m_starboardMasterMotor.reverseOutput(true);

            m_portMasterMotor.reverseSensor(false); // @TODO verify that sensor is in-phase w/ the motor
            m_starboardMasterMotor.reverseSensor(true); // @TODO verify that sensor is in-phase w/ the motor

            // Enable break mode
            m_portMasterMotor.enableBrakeMode(true);
            m_starboardMasterMotor.enableBrakeMode(true);

            // Reset the encoder position
            m_portMasterMotor.setEncPosition(0);
            m_starboardMasterMotor.setEncPosition(0);

            // Setup motor control profile parameters
            /*
             * Set PID values
             * If you want your mechanism to drive 50% throttle (0-1023) when
             * the error is 1000 (one rotation)
             * Proportional Gain = (0.50 x 1023)/1000 = ~0.511 [TalonSRX
             * programming 10.1]
             * IMPORTANT: you can use the roboRIO utility to tune the PID
             * instead of coding it here
             */
            setPID(1.0, 0, 0);

            //for percent v-bus
            m_portMasterMotor.setInverted(false); // Set direction so that the port motor is *not* inverted
            m_starboardMasterMotor.setInverted(false); // Set direction so that the starboard motor is *not* inverted

            /*
             * Set closed loop error
             * dictates where motor output is neutral, regardless of calculated
             * results
             * when closed-loop error is within allowable closed-loop error,
             * P,I,D terms are zeroed
             * if allowable closed loop error is 28 then ((28/(250*4))*360) is
             * ~10 degree error
             */
            m_portMasterMotor.setAllowableClosedLoopErr(ALLOWED_CLOSED_LOOP_ERROR); // default is 0
            m_starboardMasterMotor.setAllowableClosedLoopErr(ALLOWED_CLOSED_LOOP_ERROR);

            // Stop
            m_portMasterMotor.set(0);
            m_starboardMasterMotor.set(0);

            // Reset the encoder position
            m_portMasterMotor.setEncPosition(0);
            m_starboardMasterMotor.setEncPosition(0);

            /*
             * Bounds the output of the closed-loop modes [TalonSRX 10.5]
             * peak output: maximum/strongest motor output allowed during
             * closed-loop
             * - APIs available to configure forward / reverse peak output
             * nominal output: minimal/weakest output allowed during closed-loop
             * nominal output ensures that if the closed-loop values are too too
             * weak, motor output is
             * large enough to drive the robot.
             * - APIs available to configure forward / reverse nominal output
             * Note: in native units, these represent -1023 (full reverse; -12V)
             * to +1023 (full forward; 12V)
             */
            m_portMasterMotor.configNominalOutputVoltage(+1.0f, -1.0f);
            m_starboardMasterMotor.configNominalOutputVoltage(+1.0f, -1.0f);

            m_portMasterMotor.configPeakOutputVoltage(+3.0f, -3.0f);
            m_starboardMasterMotor.configPeakOutputVoltage(+3.0f, -3.0f);

            // max allowable voltage change /sec: reach to 12V after .25sec
            m_portMasterMotor.setVoltageRampRate(48.0);
            m_starboardMasterMotor.setVoltageRampRate(48.0);

            // closed-loop ramp rate
            m_portMasterMotor.setCloseLoopRampRate(48.0);
            m_starboardMasterMotor.setCloseLoopRampRate(48.0);

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

    @Override
    protected void initDefaultCommand()
    {
        if (initialized())
        {
            setDefaultCommand(new ArcadeDriveCommand(this));
        }
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

    public void driveInDistance(double distanceInInches)
    {
        double rotations = m_inchesToRotations(distanceInInches);
        double portGet = m_portMasterMotor.get();
        double starGet = m_starboardMasterMotor.get();
        double portPosition = m_portMasterMotor.getPosition();
        double starPosition = m_starboardMasterMotor.getPosition();
        int portEncPosition = m_portMasterMotor.getEncPosition();
        int starEncPosition = m_starboardMasterMotor.getEncPosition();

        m_portMasterMotor.set(rotations);
        m_starboardMasterMotor.set(rotations);

        //m_logger.info("Get:\t" + portGet + ", " + starGet);
        m_logger.info("GetPosition:\t" + portPosition + ", " + starPosition);
        m_logger.info("GetEncPosition:\t" + portEncPosition + ", " + starEncPosition);
    }

    public void driveInDistance(double portDistance, double starboardDistance)
    {
        m_portMasterMotor.set(m_inchesToRotations(portDistance));
        m_starboardMasterMotor.set(m_inchesToRotations(starboardDistance));
    }

    public double m_inchesToRotations(double inches)
    {
        return inches / (Math.PI * WHEEL_DIAMETER);
    }

    public void setPosition(int port, int starboard) throws InterruptedException
    {
        m_portMasterMotor.setPosition(port);
        m_starboardMasterMotor.setPosition(starboard);

        Thread.sleep(100);
    }

    public boolean withinAllowableClosedLoopError()
    {
        int portError = m_portMasterMotor.getClosedLoopError();
        int starError = m_starboardMasterMotor.getClosedLoopError();
        m_logger.info("EncError: " + portError + ", " + starError);

        if ((portError <= ALLOWED_CLOSED_LOOP_ERROR) && (starError <= ALLOWED_CLOSED_LOOP_ERROR))
        {
            return true;
        }
        return false;
    }

    public boolean isSetToZero()
    {
        return (m_portMasterMotor.getPosition() == 0 && m_starboardMasterMotor.getPosition() == 0);
    }

    public double ticksToRotations(int ticks)
    {
        return (double) ticks / QUAD_ENCODER_TICKS_PER_REVOLUTION;
    }

    public void setPID(double p, double i, double d)
    {
        m_portMasterMotor.setPID(p, i, d);
        m_starboardMasterMotor.setPID(p, i, d);
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
    public void setControlMode(TalonControlMode m, double forwardPeakVoltage, double reversePeakVoltage)
    {
        if (initialized())
        {
            // Change the control mode
            m_portMasterMotor.changeControlMode(m);
            m_starboardMasterMotor.changeControlMode(m);
            // Set so we're at a state we understand
            m_portMasterMotor.set(0);
            m_starboardMasterMotor.set(0);

            //TODO Explicitly set maximum output
            m_portMasterMotor.configPeakOutputVoltage(forwardPeakVoltage, reversePeakVoltage);
            m_starboardMasterMotor.configPeakOutputVoltage(forwardPeakVoltage, reversePeakVoltage);
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
                double rotation = m_driveStick.getX();
                if (Math.abs(forward) > 0.02 | Math.abs(rotation) > 0.02)
                {
                    m_robotDrive.arcadeDrive(forward, rotation);
                }
                else
                {
                    m_logger.debug("joystick is within deadzone for both axies so driving is disabled" + forward + " " + rotation);
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
        }
    }

    public void resetEncPosition()
    {
        m_portMasterMotor.setEncPosition(0);
        m_starboardMasterMotor.setEncPosition(0);

        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } /* wait a bit to make sure the setPosition() above takes effect */
    }

    public void resetPosition() throws InterruptedException
    {
        m_portMasterMotor.setPosition(0);
        m_starboardMasterMotor.setPosition(0);

        Thread.sleep(100); /*
                            * wait a bit to make sure the setPosition() above
                            * takes effect
                            */
    }

    public boolean isPositionReset()
    {
        if (m_portMasterMotor.getPosition() == 0 &&
                m_starboardMasterMotor.getPosition() == 0)
        {
            return true;
        }
        return false;
    }

    // returns how much the encoders are off from where they should be
    public double getAvgEncError()
    {
        if (initialized())
        {
            //            int portError = getError(m_portMasterMotor.getEncPosition(), m_destinationPositionPort);
            //            int starboardError = getError(m_starboardMasterMotor.getEncPosition(), m_destinationPositionStarboard);
            //            m_logger.info("portError: " + portError);
            //            m_logger.info("starboardError: " + starboardError);
            //return (Math.abs(portError)+Math.abs(starboardError)/2); // Get both encoder positions and average them
            return m_starboardMasterMotor.getClosedLoopError();
        }
        else
        {
            return 0;
        }
    }

    public int getError(int currentPos, int destinationPos)
    {
        int difference;
        difference = destinationPos - (int) Math.abs((double) currentPos);
        return difference;
    }

    public int getPortEncError()
    {
        return m_portMasterMotor.getClosedLoopError();
    }

    public int getStarboardEncError()
    {
        return m_starboardMasterMotor.getClosedLoopError();
    }

    public int inchesToTicks(double inches)
    {
        return (int) (inches * QUAD_ENCODER_TICKS_PER_INCH);
    }

    // Hook called by Robot periodic used to update the SmartDashboard
    public void updatePeriodicHook()
    {
        if (m_imu.isInitialized()) // Make sure that the IMU is initialized
        {
            SmartDashboard.putNumber("Drivetrain_IMU_Heading", this.getIMUNormalizedHeading()); // Send data to the SmartDashboard with the normalized IMU heading
        }
    }

    //checks if encoders are at 0
    public boolean isEncPositionZero()
    {
        return ((m_portMasterMotor.getEncPosition() == 0)
                && (m_starboardMasterMotor.getEncPosition() == 0));
    }

    //checks if both encoders reached a certain number of ticks
    public boolean isLocationReached()
    {
        m_logger.info("Encoder position: \t" + m_portMasterMotor.getEncPosition() + "\t" + m_starboardMasterMotor.getEncPosition());

        double avgEncError = getAvgEncError();

        return (avgEncError <= MAX_ENC_ERROR);
    }

    public void setMaxOutput(double maxOutput)
    {
        m_portMasterMotor.configMaxOutputVoltage(maxOutput);
        m_starboardMasterMotor.configMaxOutputVoltage(maxOutput);
    }

    public int getPortPosition()
    {
        return (int) m_portMasterMotor.getPosition();
    }

    public int getStarboardPosition()
    {
        return (int) m_starboardMasterMotor.getPosition();
    }
}
