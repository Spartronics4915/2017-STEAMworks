package org.usfirst.frc.team4915.steamworks.subsystems;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;
import org.usfirst.frc.team4915.steamworks.commands.ArcadeDriveCommand;
import org.usfirst.frc.team4915.steamworks.sensors.BNO055;
import org.usfirst.frc.team4915.steamworks.sensors.IMUPIDSource;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/* on Declan's choice for motor names: http://oceanservice.noaa.gov/facts/port-starboard.html
    Since port and starboard never change, they are unambiguous references that are independent of a mariner's orientation, and, 
    thus, mariners use these nautical terms instead of left and right to avoid confusion. When looking forward, toward the bow of a ship, 
    port and starboard refer to the left and right sides, respectively.

    In the early days of boating, before ships had rudders on their centerlines, boats were controlled using a steering oar. Most sailors 
    were right handed, so the steering oar was placed over or through the right side of the stern. Sailors began calling the right side the 
    steering side, which soon became "starboard" by combining two Old English words: steor (meaning "steer") and bord (meaning "the side of a boat").

    As the size of boats grew, so did the steering oar, making it much easier to tie a boat up to a dock on the side opposite the oar. This 
    side became known as larboard, or "the loading side." Over time, larboard - too easily confused with starboard - was replaced with port. 
    After all, this was the side that faced the port, allowing supplies to be ported aboard by porters.
*/

public class Drivetrain extends SpartronicsSubsystem
{


    private static final int QUAD_ENCODER_CODES_PER_REVOLUTION = 250; // Encoder-specific value, for E4P-250-250-N-S-D-D
    private static final int QUAD_ENCODER_TICKS_PER_REVOLUTION = QUAD_ENCODER_CODES_PER_REVOLUTION * 4; // This should be one full rotation
    private static final double MAX_OUTPUT_ROBOT_DRIVE = 0.3;
    private static final double WHEEL_DIAMETER = 6;
    private static final double WHEEL_CIRCUMFERENCE = 20.06; // This is to account for drift

    private XboxController m_driveStick;// Joystick for ArcadeDrive
    private Joystick m_altDriveStick; //Alternate Joystick for ArcadeDrive
    
    private static final int LIGHT_OUTPUT_PORT = 0;
    private DigitalOutput m_lightOutput;



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

    private static final double turnKp = 0.12;
    private static final double turnKi = 0.01;
    private static final double turnKd = 0.1;
    private static final double turnKf = 0.001;

    // Replay
    private boolean m_isRecording = false;
    private Instant m_startedRecordingAt;
    private final List<Double> m_replayForward = new ArrayList<>();
    private final List<Double> m_replayRotation = new ArrayList<>();
    private int m_replayLaunchStart = 0;
    private int m_replayLaunchStop = 0;
    
    //Reverse
    private boolean m_reverseIsOn = false;


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
            m_starboardMasterMotor.reverseOutput(true); // NB: this only applies for closed loop modes.  RobotDrive assumes that
                                                        // 'left' and 'right' motors are in opposition and automatically
                                                        // reverses one from another.   When we directly control the two motors
                                                        // in open-loop mode (PercentVbus) we must apply the output inversion explicitly.
                                                        // or we can invoke m_robotDrive.setLeftRightMotorOutputs(leftOutput, rightOutput).

            // Setup the motor so it has an encoder
            m_portMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            m_starboardMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);

            // Set the number of encoder ticks per wheel revolution
            m_portMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_CODES_PER_REVOLUTION); // This is actual ticks, so it *shouldn't* be multiplied by 4
            m_starboardMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_CODES_PER_REVOLUTION);

            m_portMasterMotor.setInverted(true); // Set direction so that the port motor is inverted
            m_starboardMasterMotor.setInverted(true); // Set direction so that the starboard motor is inverted

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
            m_turningPIDController.setAbsoluteTolerance(3); // This is the tolerance for error for reaching our target

            // Make a new RobotDrive so we can use built in WPILib functions like ArcadeDrive
            m_robotDrive = new RobotDrive(m_portMasterMotor, m_starboardMasterMotor);

            // Set a max output in volts for RobotDrive
            m_robotDrive.setMaxOutput(MAX_OUTPUT_ROBOT_DRIVE);

            loadReplay();
            
            m_lightOutput = new DigitalOutput(LIGHT_OUTPUT_PORT);
            SmartDashboard.putString("ReverseEnabled", "Disabled");


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
            printIMUErrorMessage(m_imu);
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

    public boolean isIMUTurnFinished()
    {
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
            printIMUErrorMessage(m_imu);
            return 0;
        }
    }

    public double getIMUHeading()
    {
        if (m_imu.isInitialized())
        {
            return m_imu.getHeading();
        }
        else
        {
            printIMUErrorMessage(m_imu);
            return 0;
        }
    }
    
    public boolean isIMUInitalized()
    {
        return m_imu.isInitialized();
    }
    
    private void printIMUErrorMessage(BNO055 imu)
    {
        if (!imu.isSensorPresent())
        {
            m_logger.error("can't get normalized IMU heading because the IMU is not present (it's probably not plugged in)!");
        }
        else
        {
            m_logger.warning("can't get normalized IMU heading because the IMU isn't initalized");
        }
    }
    
    public void setDriveStick(XboxController s, Joystick j) // setDriveStick is presumably called once from OI after joystick initialization
    {
        m_driveStick = s;
        m_altDriveStick = j;
    }

    public double getInchesToRevolutions(double inches)
    {
        return inches / WHEEL_CIRCUMFERENCE;
    }

    private double getTicksToRevolutions(int ticks)
    {
        return ticks / (double)QUAD_ENCODER_TICKS_PER_REVOLUTION;
    }

    // Not to be confused with CANTalon's setControlMode... The idea here is to
    // make sure that we keep all the control-mode-specific settings under close
    // management.
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

            switch (m)
            {
                case PercentVbus:
                    rampRate = 36;
                    break;
                case Position:
                    rampRate = 12;
                    if (F != 0)
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
    //  In theory this should be the same as resetEncoder but for
    //  clarity, we make the distinctions based on current control mode.
    public void resetPosition()
    {
        switch (getControlMode())
        {
            case Position:
                m_portMasterMotor.setPosition(0);
                m_starboardMasterMotor.setPosition(0);
                performDelay();
                if (m_portMasterMotor.getPosition() != 0 ||
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

    // resetEncoder differs from resetPosition only in terms of units.
    // Conceptually, resetPosition isn't meaningful unless the motor is
    // running in Position control mode.  This point is only relevant
    // if we need to reset position to a value other than 0.
    // In any event, resetEncoder is private since client of resetPosition()
    // shouldn't care about this subtlety.
    private void resetEncoder()
    {
        m_portMasterMotor.setEncPosition(0);
        m_starboardMasterMotor.setEncPosition(0);
        performDelay();
        if (m_portMasterMotor.getEncPosition() != 0 ||
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

    // setClosedLoopTargetRevolutions - can be used for both Speed and Position modes.
    // For speed mode, the units are required to be RPM.  for position mode, revolutions.
    public void setClosedLoopTargetRevolutions(double tg)
    {
        switch (getControlMode())
        {
            case Speed:
            case Position:
                if (m_target != tg)
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

    // closedLoopTargetIsReached - is be ussed for both speed and position modes.
    // We employ the get() method to obtain values since is returns its result in
    // the mode-specific units.
    public boolean closedLoopTargetIsReached(double epsilon)
    {
        boolean result = true;
        switch (getControlMode())
        {
            case Speed:
            case Position:
                if (Math.abs(m_portMasterMotor.get() - m_target) < epsilon &&
                        Math.abs(m_starboardMasterMotor.get() - m_target) < epsilon)
                {
                    m_targetReached++;
                    if (m_targetReached < 5)
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

    
    // Uses arcade drive coupled with the drivestick
    public void driveArcade()
    {
        if (initialized())
        {
            if (m_portMasterMotor.getControlMode() == TalonControlMode.PercentVbus
                    && m_starboardMasterMotor.getControlMode() == TalonControlMode.PercentVbus)
            {
                double forward = triggerAxis() + m_altDriveStick.getY();
                double rotation = m_driveStick.getX(GenericHID.Hand.kLeft) + m_altDriveStick.getX();
                if(m_reverseIsOn)
                {
                    forward = -triggerAxis() - m_altDriveStick.getY();
                    rotation = m_driveStick.getX(GenericHID.Hand.kLeft) + m_altDriveStick.getX();
                    //m_logger.debug("Reverse Engaged");
                }
                
                if (Math.abs(forward) < 0.02 && Math.abs(rotation) < 0.02)
                {
                    // To keep motor safety happy
                    forward = 0.0;
                    rotation = 0.0;
                }
                if (getRecordingEnabled() && m_isRecording)
                {
                    m_replayForward.add(forward); 
                    m_replayRotation.add(rotation);
                }
                m_robotDrive.arcadeDrive(forward, rotation);
            }
            else
            {
                m_logger.warning("drive arcade attempt with wrong motor control mode (should be PercentVbus)");
            }
        }
    }
    
    public void setReverse(Cameras m_cameras)
    {
        
        m_reverseIsOn = true;
        m_lightOutput.set(true);
        m_cameras.changeCamera(Cameras.CAM_REV);
        SmartDashboard.putString("ReverseEnabled", "Enabled");

    }
    
    public void resetReverse(Cameras m_cameras)
    {
        m_reverseIsOn = false;
        m_lightOutput.set(false);
        m_cameras.changeCamera(Cameras.CAM_FWD);
        SmartDashboard.putString("ReverseEnabled", "Disabled");
    }
    
    public double triggerAxis()
    {
        if(m_driveStick.getTriggerAxis(GenericHID.Hand.kRight) > 0)
        {
            return -(m_driveStick.getTriggerAxis(GenericHID.Hand.kRight));
        }
        else if(m_driveStick.getTriggerAxis(GenericHID.Hand.kLeft) > 0)
        {
            return (m_driveStick.getTriggerAxis(GenericHID.Hand.kLeft));
        }
        else
        {
            return 0;
        }
    }

    // driveArcadeDirect exposes minimal access to our robotdrive, can be used
    // from autonomous commands since it doesn't use the joystick. This method
    // also does not record any inputs, use the non-direct version for that.
    public void driveArcadeDirect(double fwd, double rotation)
    {
        if (initialized())
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
            // Is this the right thing to do?
            stopRecording();

            // XXX: should we disable here?
        }
    }

    // getClosedLoopValue: returns the average of the current motor states
    //  NB: use with care, especially for PID control. If the two motor
    //      encoders are way out of sync, we could get into trouble.
    public double getClosedLoopValue(boolean takeAverage)
    {
        double result = 0;
        if (initialized())
        {
            switch (getControlMode())
            {
                case Speed:
                case Position:
                    if (takeAverage)
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

    // getOpenLoopValue() returns encoder state in revolutions.  Presumably caller
    //  has previously reset the the encoder with a call to resetPosition() and is
    //  measuring a distance from that point.
    public double getOpenLoopValue()
    {
        double result = 0;
        if (initialized())
        {
            switch (getControlMode())
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

    // getEncPosition is private since we really don't want clients to worry about ticks.
    // XXX: Needs to be made private
    public int getEncPosition()
    {
        // XXX: for now we only return one enc position... Should caller need access to
        //      a specific motor, we should add a parameter
        if (initialized())
        {
            return m_starboardMasterMotor.getEncPosition(); // We're sampling the starboard motor because it's not inverted
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
            SmartDashboard.putNumber("Drivetrain_Encoder_Value", this.getEncPosition());
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

    public void startRecording()
    {
        if (!getRecordingEnabled())
        {
            return;
        }
        m_startedRecordingAt = Instant.now();
        m_logger.notice("Started recording at " + m_startedRecordingAt);

        // Get rid of the last program
        m_replayForward.clear();
        m_replayRotation.clear();

        m_isRecording = true;
    }

    public void stopRecording()
    {
        if (m_isRecording)
        {
            Instant now = Instant.now();
            m_logger.notice("Stopped recording at " + now);
            long delta = m_startedRecordingAt.until(now, ChronoUnit.SECONDS);
            m_logger.notice("(After " + delta + " seconds, " + m_replayForward.size() + " entries)");

            m_isRecording = false;
            if (saveRecording(now.toString()))
            {
                SmartDashboard.putString("AutoStrategyOptions", SmartDashboard.getString("AutoStrategyOptions", "") + ",Replay: " + now.toString());
            }
        }
    }
    
    

    public void loadReplay()
    {
        String strategy = SmartDashboard.getString("AutoStrategy", "");
        if (strategy.equals("") || strategy.equals("None"))
        {
            m_logger.notice("No strategy selected, not loading a replay.");
            m_replayForward.clear();
            m_replayRotation.clear();
            return;
        }
        if (strategy.startsWith("Replay: "))
        {
            try
            {
                strategy = strategy.replaceFirst("Replay: ", "");
                m_logger.notice("Loading " + strategy + " from disk...");
                List<String> lines = Files.readAllLines(Paths.get(System.getProperty("user.home"), "Recordings", strategy));
                String[] forwardFromFile = lines.get(0).split(",");
                String[] rotationFromFile = lines.get(1).split(",");
                if (lines.size() > 2)
                {
                    String[] launchFromFile = lines.get(2).split(",");
                    m_replayLaunchStart = Integer.valueOf(launchFromFile[0]);
                    m_replayLaunchStop = Integer.valueOf(launchFromFile[1]);
                    if (m_replayLaunchStart >= forwardFromFile.length || m_replayLaunchStart < 0 || m_replayLaunchStop < m_replayLaunchStart)
                    {
                        m_logger.debug("Supposed to launch at an invalid step (" + m_replayLaunchStart + "), max " + forwardFromFile.length);
                        m_replayLaunchStart = 0;
                    }
                    else
                    {
                        m_logger.debug("Will launch at step number " + m_replayLaunchStart);
                    }
                }
                else
                {
                    m_logger.debug("Won't try to launch.");
                }

                if (forwardFromFile.length != 0 && rotationFromFile.length != 0)
                {
                    List<Double> forwardProgram = Arrays.asList(forwardFromFile).stream()
                            .map(Double::parseDouble)
                            .collect(Collectors.toList());
                    List<Double> rotationProgram = Arrays.asList(rotationFromFile).stream()
                            .map(Double::parseDouble)
                            .collect(Collectors.toList());

                    if (forwardProgram.size() == rotationProgram.size())
                    {
                        m_replayForward.clear();
                        m_replayRotation.clear();

                        // Copying the contents of the temporary lists into the cleared m_
                        // lists because if we simply m_replayForward = forwardProgram it
                        // throws errors about assigning to final variables.
                        m_replayForward.addAll(forwardProgram);
                        m_replayRotation.addAll(rotationProgram);
                    }
                    else
                    {
                        m_logger.error("Autonomous program's forward and rotation arrays are different sizes!");
                        m_logger.debug("Forward array: " + Arrays.toString(forwardFromFile));
                        m_logger.debug("Rotation array: " + Arrays.toString(rotationFromFile));
                    }
                }
            }
            catch (NumberFormatException e)
            {
                m_logger.error("Badly formatted number in replay string");
            }
            catch (IOException e)
            {
                m_logger.exception(e, false);
            }
        }
    }

    private boolean saveRecording(String name)
    {
        m_logger.notice("Saving the recording...");
        // This takes all the Doubles in m_replayForward, converts them to a list of Strings,
        // then collects them back into one string by "joining" them together with commas.
        String forward = m_replayForward.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String rotation = m_replayRotation.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        List<String> both = Arrays.asList(new String[] {forward, rotation});
        try
        {
            Files.createDirectories(Paths.get(System.getProperty("user.home"), "Recordings"));
            Files.write(Paths.get(System.getProperty("user.home"), "Recordings", name), both, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            return true;
        }
        catch (IOException e)
        {
            m_logger.error("Couldn't save!");
            m_logger.warning("Old Forward:  " + forward);
            m_logger.warning("Old Rotation: " + rotation);
            m_logger.exception(e, false);
            return false;
        }
    }

    public boolean getRecordingEnabled()
    {
        return SmartDashboard.getBoolean("RecordingEnabled", false);
    }

    public List<Double> getReplayForward()
    {
        return m_replayForward;
    }

    public List<Double> getReplayRotation()
    {
        return m_replayRotation;
    }

    public int getReplayLaunchStart()
    {
        return m_replayLaunchStart;
    }

    public int getReplayLaunchStop()
    {
        return m_replayLaunchStop;
    }

}
