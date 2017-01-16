package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.RobotMap;
import org.usfirst.frc.team4915.steamworks.commands.ManualDriveCommand;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Drivetrain extends Subsystem
{

    public static final int QUAD_ENCODER_TICKS_PER_REVOLUTION = 9000;
    private final Joystick m_driveStick;

    private final CANTalon m_leftFollowerMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_LEFT_FOLLOWER);
    private final CANTalon m_leftMasterMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_LEFT_MASTER);

    private final CANTalon m_rightFollowerMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_RIGHT_FOLLOWER);
    private final CANTalon m_rightMasterMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_RIGHT_MASTER);

    private final RobotDrive m_robotDrive;

    public Drivetrain(Joystick driveStick)
    {
        this.m_driveStick = driveStick;

        m_leftMasterMotor.changeControlMode(TalonControlMode.Speed);
        m_leftFollowerMotor.changeControlMode(TalonControlMode.Follower);
        m_leftFollowerMotor.set(m_leftMasterMotor.getDeviceID());

        m_rightMasterMotor.changeControlMode(TalonControlMode.Speed);
        m_rightFollowerMotor.changeControlMode(TalonControlMode.Follower);
        m_rightFollowerMotor.set(m_rightMasterMotor.getDeviceID());

        m_leftMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        m_rightMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        m_leftMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);
        m_rightMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);

        m_leftMasterMotor.setVoltageRampRate(48);
        m_rightMasterMotor.setVoltageRampRate(48);

        m_robotDrive = new RobotDrive(m_leftFollowerMotor, m_leftMasterMotor, m_rightFollowerMotor, m_rightMasterMotor);
    }

    public void drive(double forward, double rotation)
    {
        m_robotDrive.arcadeDrive(forward, rotation);
    }

    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new ManualDriveCommand(this, m_driveStick));
    }

}
