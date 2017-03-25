package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Launcher extends SpartronicsSubsystem
{

    public static enum LauncherState
    {
        OFF,
        ON,
        SINGLE,
        UNJAM
    }

    //the "perfect" static speed that always makes goal
    public static final double DEFAULT_LAUNCHER_SPEED = 2880;
    public static final double DEFAULT_AGITATOR_SPEED = .7;
    private CANTalon m_launcherMotor;
    private CANTalon m_agitatorMotor;
    private Logger m_logger;
    private LauncherState m_state;
    private double m_initialPos;
    private int m_startupCount;
    private int m_jamCount;

    public Launcher()
    {
        m_logger = new Logger("Launcher", Logger.Level.DEBUG);
        try
        {
            m_state = LauncherState.OFF;
            m_startupCount = 0;
            m_launcherMotor = new CANTalon(RobotMap.LAUNCHER_MOTOR);
            m_launcherMotor.setAllowableClosedLoopErr(0); //4096 Native Units per rev * 5 revs per min
            m_launcherMotor.changeControlMode(TalonControlMode.Speed);
            m_launcherMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
            m_launcherMotor.reverseSensor(false);
            m_launcherMotor.configNominalOutputVoltage(0.0f, -0.0f);
            m_launcherMotor.configPeakOutputVoltage(12.0f, -12.0f);
            m_launcherMotor.setVoltageRampRate(0.0); 
            m_launcherMotor.setCloseLoopRampRate(0.0); 
            m_launcherMotor.enableBrakeMode(false);
            
            m_launcherMotor.setF(0.0315);
            m_launcherMotor.setP(0.0475);
            m_launcherMotor.setI(0.000001); 
            m_launcherMotor.setD(0.65);

            m_agitatorMotor = new CANTalon(RobotMap.AGITATOR_MOTOR);
            m_agitatorMotor.changeControlMode(TalonControlMode.PercentVbus);
            m_agitatorMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
            m_agitatorMotor.configNominalOutputVoltage(0.0f, -0.0f);
            m_agitatorMotor.configPeakOutputVoltage(12.0f, -12.0f);
            m_agitatorMotor.reverseSensor(false);
            
            SmartDashboard.putNumber("Launcher_TGT", Launcher.DEFAULT_LAUNCHER_SPEED);
            SmartDashboard.putNumber("Agitator_TGT", Launcher.DEFAULT_AGITATOR_SPEED);
            

            m_logger.info("Launcher initialized");
        }
        catch (Exception e)
        {
            m_logger.exception(e, false);
            m_initialized = false;
        }
    }

    //Sets Launcher and Agitator to set speeds regarding state given from buttons
    public void setLauncher(LauncherState state)
    {
        if (initialized())
        {
            if (m_state != state && state == LauncherState.ON) // Occurs when state switches to on from any other state
            {
                m_startupCount = 0;
                m_logger.debug("startupCount = 0");
            }
            m_state = state;
            double speedTarget = SmartDashboard.getNumber("Launcher_TGT", Launcher.DEFAULT_LAUNCHER_SPEED);
            double agitatorSpeedTarget = SmartDashboard.getNumber("Agitator_TGT", Launcher.DEFAULT_AGITATOR_SPEED);
            double speedActual = m_launcherMotor.getSpeed();
            SmartDashboard.putNumber("Launcher_ACT", speedActual);
            String msg = String.format("%.0f / %.0f", speedActual, speedTarget);
            SmartDashboard.putString("Launcher_MSG", msg);
            m_launcherMotor.set(getLauncherSpeed(speedTarget)); // speedTarget is set in getLauncherSpeed based on state
            m_agitatorMotor.set(getAgitatorSpeed(agitatorSpeedTarget)); // agitatorSpeedTarget is set in getAgitatorSpeed based on the state
        }
    }

    public double getLauncherSpeed(double speedTarget)
    {
        switch (m_state)
        {
            case ON:
                return speedTarget;
            case OFF:
                return 0;
            case SINGLE:
                return speedTarget;
            case UNJAM:
                return speedTarget;
        }
        return 0;
    }

    public double getAgitatorSpeed(double speedTarget)
    {
        switch (m_state)
        {
            case ON:
                if (isLauncherAtSpeed())
                {
                    if (isJammed())
                    {
                        return -speedTarget;
                    }
                    else
                    {
                        return speedTarget;
                    }
                }
                else
                {
                    return 0;
                }
            case OFF:
                return 0;
            case SINGLE:
                if (isSingleShotDone() || !isLauncherAtSpeed())
                {
                    return 0;
                }
                else
                {
                    return speedTarget; 
                }
            case UNJAM:
                if (!isJammed())
                {
                    m_state = LauncherState.ON;
                    return speedTarget;
                }
                else
                {
                    return -speedTarget;
                }
        }
        return 0;
    }

    private boolean isLauncherAtSpeed()
    {
        final double epsilon = 500; // allow 500 RPM of error.
        double speedTarget = SmartDashboard.getNumber("Launcher_TGT", Launcher.DEFAULT_LAUNCHER_SPEED);
        double speedActual = m_launcherMotor.getSpeed();
        if ((speedActual >= (speedTarget - epsilon)) && (speedActual <= (speedTarget + epsilon)))
        {
            m_startupCount++;
            if (m_startupCount < 15) // if launcher at speed for less than 300ms (code runs once every 20ms) Only tested on first run through
            {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean isSingleShotDone()
    {
        double currentPosition = m_agitatorMotor.getPulseWidthPosition();
        m_logger.debug("isSingleShotDone: Current Position: " + currentPosition + " Initial Position: " + m_initialPos);
        if (currentPosition >= (m_initialPos + 1024)) /// 1024 native units, 1/4 rotation
        {
            return true;
        }
        return false;
    }

    private boolean isJammed() 
    {
        if (this.getRpm() < 10) // target is 70
        {
            m_jamCount++; // times in a row that the agitator hasn't moved
            if (m_jamCount < 15)  // We are waiting for the reversed velocity to have an effect
            {
                return false;
            }
            else if (m_jamCount > 30) // If it doesn't work at this time, set speed to normal direction
            {
                m_jamCount = 0;
                return false;
            }
            else
            {
                return true;
            }
        }
        m_jamCount = 0;
        return false;
    }

    private double getRpm()  
    {
        double currentAgitatorSpeed = m_agitatorMotor.getEncVelocity(); 
        return currentAgitatorSpeed * 600.0 / 4096.0; //conversion from native units / 100ms to rpm
    }

    public void setAgitatorTarget()
    {
        m_initialPos = m_agitatorMotor.getPulseWidthPosition();
        m_logger.debug("setAgitatorTarget: Initial position is " + m_initialPos);
    }

    public double getInitialPos()
    {
        return m_initialPos;
    }

    public CANTalon getAgitator()
    {
        return m_agitatorMotor;
    }

    public boolean isEmpty()
    {
        double currentPosition = m_agitatorMotor.getPulseWidthPosition();
        if (currentPosition >= (m_initialPos + 4096 * 3))  // 3 full rotations
        {
            return true;
        }
        return false;

    }

    public void initDefaultCommand()
    {
        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());

    }
}
