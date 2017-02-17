package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher.LauncherState;

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
    public static enum LauncherState
    {
        OFF,
        ON,
        SINGLE
    }

    //the "perfect" static speed that always makes goal
    public static final double DEFAULT_LAUNCHER_SPEED = 3000; //3000 rpm (CtreMagEncoder) Since it is CTRE, it is able to program its RPM itself
    public static final double DEFAULT_AGITATOR_SPEED = .2; //60; //60 rpm (CtreMagEncoder) 
    private CANTalon m_launcherMotor;
    private CANTalon m_agitatorMotor;
    private Logger m_logger;
    private LauncherState m_state;
    private int m_initialPos;

    public Launcher()
    {
        m_logger = new Logger("Launcher", Logger.Level.DEBUG);
        try
        {
            int allowableError = 4096 * 5 / (60 * 10); // 4096 nu/rev * 5 rpm and then convert to NU/100ms
            m_launcherMotor.setAllowableClosedLoopErr(allowableError); //4096 Native Units per rev * 5 revs per min
            m_state = LauncherState.OFF;
            m_logger.info("Launcher initialized 1");
            m_launcherMotor = new CANTalon(RobotMap.LAUNCHER_MOTOR);
            m_launcherMotor.changeControlMode(TalonControlMode.Speed);
            m_launcherMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
            m_launcherMotor.reverseSensor(false);
            m_launcherMotor.configNominalOutputVoltage(0.0f, -0.0f);
            m_launcherMotor.configPeakOutputVoltage(12.0f, -12.0f);
            
            // changeable fpid values in smartdashboard
            /*
            m_launcherMotor.setF(.0305); // (1023)/Native Units Per 100ms. See Talon Reference Manual pg 77
            m_launcherMotor.setP(0); //(.09 currently) (Proportion off target speed * 1023) / Worst Error //.03188 BASE
            m_launcherMotor.setI(0); // (.0009 currently) start at 1 / 100th of P gain
            m_launcherMotor.setD(0);
            */
            
            m_agitatorMotor = new CANTalon(RobotMap.AGITATOR_MOTOR);
            m_agitatorMotor.changeControlMode(TalonControlMode.PercentVbus);
            m_agitatorMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
            m_agitatorMotor.reverseSensor(false);
            m_agitatorMotor.configNominalOutputVoltage(0.0f, -0.0f);
            m_agitatorMotor.configPeakOutputVoltage(12.0f, -12.0f);
            
            m_agitatorMotor.setF(.0305);
            m_agitatorMotor.setP(0.06);
            m_agitatorMotor.setI(0.0);
            m_agitatorMotor.setD(0.6);


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
        if(initialized()) 
        {
            m_state = state;
            double speedTarget = SmartDashboard.getNumber("Launcher_TGT", Launcher.DEFAULT_LAUNCHER_SPEED);
            double speedActual = m_launcherMotor.getSpeed();
            SmartDashboard.putNumber("Launcher_ACT", speedActual);
            String msg = String.format("%.0f / %.0f", speedActual, speedTarget );
            SmartDashboard.putString("Launcher_MSG", msg);
            m_launcherMotor.set(getLauncherSpeed(speedTarget));
            m_agitatorMotor.set(getAgitatorSpeed(DEFAULT_AGITATOR_SPEED));         }
    }
    
    public double getLauncherSpeed(double speedTarget)
    {
        switch (m_state) 
        {
            case ON: return speedTarget;
            case OFF: return 0;
            case SINGLE: return speedTarget;
        }
        return 0;
    }

    public double getAgitatorSpeed(double speedTarget)
    {
        switch (m_state) {
            case ON: if(isLauncherAtSpeed()) 
            {
                return speedTarget;
            } 
            else 
            {
                return 0;
            }           
            case OFF: return 0;
            case SINGLE: 
                if(isSingleShotDone() || !isLauncherAtSpeed()) 
                {
                    return 0;
                } 
                else 
                {
                    return speedTarget;
                }
        }
        return 0;
    }
    
    private boolean isLauncherAtSpeed()
    {
        final double epsilon = 20; // allow 10 RPM of slop.
        double speedTarget = SmartDashboard.getNumber("Launcher_TGT", Launcher.DEFAULT_LAUNCHER_SPEED);
        double speedActual = m_launcherMotor.getSpeed();
        if (speedActual >= speedTarget - epsilon && speedActual <= speedTarget + epsilon)
        {
            return true;
        }
        return false;
    }


    public boolean isSingleShotDone() 
    {
        int CurrentPosition = m_agitatorMotor.getPulseWidthPosition();
        if(CurrentPosition >= (m_initialPos + 1024)) /// we think this should be 1024, not 256...
        {
            m_logger.debug("isSingleShotDone returning true: Current Position: " + CurrentPosition + " Initial Position: " + m_initialPos);
            return true;
        }
        return false;
    }

    public void setAgitatorTarget()
    {
        if(m_state == LauncherState.SINGLE) {
            m_initialPos = m_agitatorMotor.getPulseWidthPosition();
        }
    }
    
    public int getInitialPos() 
    {
        return m_initialPos;
    }
    
    public void initDefaultCommand()
    
    {
        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());

    }
}
