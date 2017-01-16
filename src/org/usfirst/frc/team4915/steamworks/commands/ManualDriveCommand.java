package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.command.Command;

public class ManualDriveCommand extends Command
{

    private static final double TURN_MULTIPLIER = -0.55;
    private final Joystick driveStick;

    private final Drivetrain drivetrain;

    public ManualDriveCommand(Drivetrain drivetrain, Joystick driveStick)
    {
        this.drivetrain = drivetrain;
        this.driveStick = driveStick;
    }

    @Override
    public void execute()
    {
        double forwardAmount = -driveStick.getAxis(AxisType.kX);
        double rotationAmount = driveStick.getAxis(AxisType.kY) * TURN_MULTIPLIER;
        drivetrain.drive(forwardAmount, rotationAmount);
    }

    @Override
    public boolean isFinished()
    {
        return false;
    }

}
