package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Launcher extends SpartronicsSubsystem 
{
	//the "perfect" static speed that always makes goal
	public static final double DEFAULT_LAUNCHER_SPEED = 60; //60 rpm (CtreMagEncoder) (Native Units per 100 ms)
	public static final double DEFAULT_AGITATOR_SPEED = 60; //60 rpm
	private CANTalon m_launcherMotor;
	private CANTalon m_agitatorMotor;
	private Logger m_logger;

	public Launcher() {
		m_logger = new Logger("Launcher", Logger.Level.DEBUG);
		try {
			m_logger.info("Launcher initialized 1");
			m_launcherMotor = new CANTalon(RobotMap.LAUNCHER_MOTOR);
			m_launcherMotor.changeControlMode(TalonControlMode.Speed);
			
			m_launcherMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
			m_launcherMotor.reverseSensor(false);
			
			
			m_launcherMotor.configNominalOutputVoltage(0.0f, -0.0f);
			m_launcherMotor.configPeakOutputVoltage(12.0f, 0.0f);
			m_launcherMotor.setF(2.498); // (1023)/Native Units Per 100ms. See Talon Reference Manual pg 77
			m_launcherMotor.setP(.1); //
			m_launcherMotor.setI(0);
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
		} catch (Exception e) {
			m_logger.exception(e, false);
			m_initialized = false;
		}
	}
	
	private void logMotor(CANTalon motor) 
	{
		double speed = motor.getSpeed();
		double motorOutput = motor.getOutputVoltage()/motor.getBusVoltage();
		if(motor.equals(m_launcherMotor)) 
		{
			m_logger.debug("Launcher Target Speed: "+ DEFAULT_LAUNCHER_SPEED + " Actual Speed:  "+ speed);
			m_logger.debug("Launcher Error: "+motor.getClosedLoopError() + " Launcher Motor Output: " + motorOutput);
		}
		else
		{
			m_logger.debug("Agitator Target Speed: "+ DEFAULT_AGITATOR_SPEED + " Actual Speed:  "+ speed);
			m_logger.debug("Agitator Error: "+motor.getClosedLoopError() + " Agitator Motor Output: " + motorOutput);
		}
		
	}

	public void setLauncher(boolean isOn) 
	{
		if (initialized()) {
			if (isOn) {
				
				setLauncherSpeed(DEFAULT_LAUNCHER_SPEED);
				setAgitatorSpeed(DEFAULT_AGITATOR_SPEED);
				m_logger.info("Launcher.setLauncher:ON");
				m_logger.info("Launcher.setAgitator:ON");
				logMotor(m_launcherMotor);
				
			} else {
				setLauncherSpeed(0);
				setAgitatorSpeed(0);
				m_logger.info("Launcher.setLauncher:OFF");
			}
		}
	}
	
	

	// Sets the launcher to a given speed
	public void setLauncherSpeed(double speed) {
		m_launcherMotor.set(speed);
	}
	
	// Sets the agitator to a given speed
	public void setAgitatorSpeed(double speed) {
		m_agitatorMotor.set(speed);
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());

	}
}
