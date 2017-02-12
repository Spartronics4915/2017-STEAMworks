package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Launcher extends SpartronicsSubsystem
{

    //the "perfect" static speed that always makes goal
    public static final double DEFAULT_LAUNCHER_SPEED = 3000; //3000 rpm (CtreMagEncoder) Since it is CTRE, it is able to program its RPM itself
    public static final double DEFAULT_AGITATOR_SPEED = 60; //60 rpm (CtreMagEncoder) 
    private CANTalon m_launcherMotor;
    private CANTalon m_agitatorMotor;
    private Logger m_logger;

    public Launcher()
    {
        m_logger = new Logger("Launcher", Logger.Level.DEBUG);
        try
        {
            m_logger.info("Launcher initialized 1");
            m_launcherMotor = new CANTalon(RobotMap.LAUNCHER_MOTOR);
            m_launcherMotor.changeControlMode(TalonControlMode.Speed);

            m_launcherMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
            m_launcherMotor.reverseSensor(false);
            //m_launcherMotor.setInverted(true); // the true one isnt inverted

            m_launcherMotor.configNominalOutputVoltage(0.0f, -0.0f);
            m_launcherMotor.configPeakOutputVoltage(12.0f, -12.0f);
            int allowableError = 4096 * 5 / (60 * 10); // 4096 nu/rev * 5 rpm and then convert to NU/100ms
            m_launcherMotor.setAllowableClosedLoopErr(allowableError); //4096 Native Units per rev * 5 revs per min
            
            /* changeable fpid values in smartdashboard
            m_launcherMotor.setF(.03527); // (1023)/Native Units Per 100ms. See Talon Reference Manual pg 77
            m_launcherMotor.setP(.03188); //(.09 currently) (Proportion off target speed * 1023) / Worst Error //.03188 BASE
            m_launcherMotor.setI(0); // (.0009 currently) start at 1 / 100th of P gain
            m_launcherMotor.setD(0);
            */
            
            
            m_agitatorMotor = new CANTalon(RobotMap.AGITATOR_MOTOR);
            m_agitatorMotor.changeControlMode(TalonControlMode.Speed);

            m_agitatorMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
            m_agitatorMotor.reverseSensor(false);
            m_agitatorMotor.configNominalOutputVoltage(0.0f, -0.0f);
            m_agitatorMotor.configPeakOutputVoltage(12.0f, -12.0f);
            
            int pulseWidthPos = m_agitatorMotor.getPulseWidthPosition(); //get the decoded pulse width encoder position
            int pulseWidthUs = m_agitatorMotor.getPulseWidthRiseToFallUs(); //get the pulse width in us (microseconds), rise-to-fall
            int periodUs = m_agitatorMotor.getPulseWidthRiseToRiseUs(); // get the periodin us (microseconds), rise-to-rise
            int pusleWidthVel = m_agitatorMotor.getPulseWidthVelocity(); //get the measured velocity
            
            
            m_agitatorMotor.setF(0);
            m_agitatorMotor.setP(0);
            m_agitatorMotor.setI(0);
            m_agitatorMotor.setD(0);

            m_logger.info("Launcher initialized");
        }
        catch (Exception e)
        {
            m_logger.exception(e, false);
            m_initialized = false;
        }
    }

    private void logMotor(CANTalon motor)
    {
        double speed = motor.get();
        double tgt = SmartDashboard.getNumber("Launcher_TGT", DEFAULT_LAUNCHER_SPEED);
        double motorOutput = motor.getOutputVoltage() / motor.getBusVoltage();
        if (motor.equals(m_launcherMotor))
        {
            m_logger.debug("Launcher Target Speed: " + tgt + " Actual Speed:  " + speed);
            m_logger.debug("Launcher Error: " + motor.getClosedLoopError() + " Launcher Motor Output: " + motorOutput);
            SmartDashboard.putString("Launcher Status: ", "Initialized");
        }
        else
        {
            m_logger.debug("Agitator Target Speed: " + DEFAULT_AGITATOR_SPEED + " Actual Speed:  " + speed);
            m_logger.debug("Agitator Error: " + motor.getClosedLoopError() + " Agitator Motor Output: " + motorOutput);
            SmartDashboard.putString("Agitator Status: ", "Initialized");
        }

    }

    public void setLauncher(boolean isOn)
    {
        if (initialized())
        {
            if (isOn)
            {
                //SmartDashboard.putNumber("Launcher_TGT", DEFAULT_LAUNCHER_SPEED);
                updateLauncherSpeed();
                setAgitatorSpeed(DEFAULT_AGITATOR_SPEED);
                logMotor(m_launcherMotor);

            }
            else
            {
                m_launcherMotor.set(0);
                setAgitatorSpeed(0);
            }
        }
    }

    // Sets the launcher to a given speed
    public void updateLauncherSpeed()
    {   
        double speedTarget = SmartDashboard.getNumber("Launcher_TGT", Launcher.DEFAULT_LAUNCHER_SPEED);
        m_launcherMotor.set(speedTarget);
        double speedActual = m_launcherMotor.getSpeed();
        SmartDashboard.putNumber("Launcher_ACT", speedActual);
        String msg = String.format("%.0f / %.0f", speedActual, speedTarget );
        SmartDashboard.putString("Launcher_MSG", msg);
        m_logger.debug("Raw error" + m_launcherMotor.getClosedLoopError());
        // logMotor(m_launcherMotor);
    }

    // Sets the agitator to a given speed
    public void setAgitatorSpeed(double speed)
    {
        SmartDashboard.putString("Agitator Status: ", "Initialized");
        m_agitatorMotor.set(speed);
    }

    public void initDefaultCommand()
    {
        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());

    }
}
