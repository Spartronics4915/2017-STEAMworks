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
    public static final double DEFAULT_LAUNCHER_SPEED = 1000; //1000 rpm (CtreMagEncoder) Since it is CTRE, it is able to program its RPM itself
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
            m_launcherMotor.setInverted(true);

            m_launcherMotor.configNominalOutputVoltage(0.0f, -0.0f);
            m_launcherMotor.configPeakOutputVoltage(12.0f, -12.0f);
            m_launcherMotor.setF(.03188); // (1023)/Native Units Per 100ms. See Talon Reference Manual pg 77
            m_launcherMotor.setP(.09); //(Proportion off target speed * 1023) / Worst Error //.03188 BASE
            m_launcherMotor.setI(.0009); // start at 1 / 100th of P gain
            m_launcherMotor.setD(0);
            
            
            m_agitatorMotor = new CANTalon(RobotMap.AGITATOR_MOTOR);
            m_agitatorMotor.changeControlMode(TalonControlMode.Speed);

            m_agitatorMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
            m_agitatorMotor.reverseSensor(false);
            m_agitatorMotor.configNominalOutputVoltage(0.0f, -0.0f);
            m_agitatorMotor.configPeakOutputVoltage(12.0f, -12.0f);
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
        double motorOutput = motor.getOutputVoltage() / motor.getBusVoltage();
        if (motor.equals(m_launcherMotor))
        {
            m_logger.debug("Launcher Target Speed: " + DEFAULT_LAUNCHER_SPEED + " Actual Speed:  " + speed);
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
                setLauncherSpeed(DEFAULT_LAUNCHER_SPEED);
                setAgitatorSpeed(DEFAULT_AGITATOR_SPEED);
                m_logger.info("Launcher.setLauncher:ON");
                m_logger.info("Launcher.setAgitator:ON");
                logMotor(m_launcherMotor);

            }
            else
            {
                setLauncherSpeed(0);
                setAgitatorSpeed(0);
                m_logger.info("Launcher.setLauncher:OFF");
            }
        }
    }

    // Sets the launcher to a given speed
    public void setLauncherSpeed(double speed)
    {   
        String msg = String.format("%f / %f", speed, m_launcherMotor.getSpeed());
        SmartDashboard.putString("Launcher_LMotor_Status", msg);
        m_launcherMotor.set(speed);
        logMotor(m_launcherMotor);
    }

    // Sets the agitator to a given speed
    public void setAgitatorSpeed(double speed)
    {
        m_agitatorMotor.set(speed);
    }

    public void initDefaultCommand()
    {
        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());

    }
}
