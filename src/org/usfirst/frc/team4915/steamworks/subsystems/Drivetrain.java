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
    private final Joystick driveStick;

    private final CANTalon leftFollowerMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_LEFT_FOLLOWER);
    private final CANTalon leftMasterMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_LEFT_MASTER);

    private final CANTalon rightFollowerMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_RIGHT_FOLLOWER);
    private final CANTalon rightMasterMotor = new CANTalon(RobotMap.DRIVE_TRAIN_MOTOR_RIGHT_MASTER);

    private final RobotDrive robotDrive;

    public Drivetrain(Joystick driveStick)
    {
        this.driveStick = driveStick;

        leftMasterMotor.changeControlMode(TalonControlMode.Speed);
        leftFollowerMotor.changeControlMode(TalonControlMode.Follower);
        leftFollowerMotor.set(leftMasterMotor.getDeviceID());

        rightMasterMotor.changeControlMode(TalonControlMode.Speed);
        rightFollowerMotor.changeControlMode(TalonControlMode.Follower);
        rightFollowerMotor.set(rightMasterMotor.getDeviceID());

        leftMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        rightMasterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        leftMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);
        rightMasterMotor.configEncoderCodesPerRev(QUAD_ENCODER_TICKS_PER_REVOLUTION);

        leftMasterMotor.setVoltageRampRate(48);
        rightMasterMotor.setVoltageRampRate(48);

        robotDrive = new RobotDrive(leftFollowerMotor, leftMasterMotor, rightFollowerMotor, rightMasterMotor);
    }

    public void drive(double forward, double rotation)
    {
        robotDrive.arcadeDrive(forward, rotation);
    }

    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new ManualDriveCommand(this, driveStick));
    }

}
