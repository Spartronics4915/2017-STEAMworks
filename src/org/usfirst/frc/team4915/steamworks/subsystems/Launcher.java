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
	public static final double DEFAULT_SPEED = 60;
	public static final int QUAD_ENCODER_TICKS_PER_REVOLUTION = 7; //7 cpr
	private CANTalon m_launcherMotor;
	private Logger m_logger;

	public Launcher() {
		m_logger = new Logger("Launcher", Logger.Level.DEBUG);
		try {
			m_logger.info("Launcher initialized 1");
			m_launcherMotor = new CANTalon(RobotMap.LAUNCHER_MOTOR);
			m_launcherMotor.changeControlMode(TalonControlMode.Speed);
			
			
			m_launcherMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
			m_launcherMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);
			m_launcherMotor.setVoltageRampRate(48);
			m_launcherMotor.reverseSensor(false);
			m_launcherMotor.configNominalOutputVoltage(0.0f, -0.0f);
			m_launcherMotor.configPeakOutputVoltage(12.0f, 0.0f);
			
			m_logger.info("Launcher initialized");
		} catch (Exception e) {
			m_logger.exception(e, false);
			m_initialized = false;
		}
	}

	public void setLauncher(boolean isOn) {
		if (initialized()) {
			if (isOn) {
				setLauncherSpeed(DEFAULT_SPEED);
				m_logger.info("Launcher.setLauncher:ON");
			} else {
				setLauncherSpeed(0);
				m_logger.info("Launcher.setLauncher:OFF");
			}
		}
	}

	// Sets the launcher to a given speed
	public void setLauncherSpeed(double speed) {
		m_launcherMotor.set(speed);
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());

	}
}
