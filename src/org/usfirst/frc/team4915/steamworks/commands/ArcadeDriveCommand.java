package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.command.Command;

public class ArcadeDriveCommand extends Command
{

    private static final double TURN_MULTIPLIER = -0.55;
    private final Joystick m_driveStick;

    private final Drivetrain m_drivetrain;

    public ArcadeDriveCommand(Drivetrain drivetrain, Joystick driveStick)
    {
        m_drivetrain = drivetrain;
        m_driveStick = driveStick;

        requires(m_drivetrain);
    }

    @Override
    public void execute()
    {
        double forwardAmount = -m_driveStick.getAxis(AxisType.kX); // Get joystick x-axis values
        double rotationAmount = m_driveStick.getAxis(AxisType.kY) * TURN_MULTIPLIER; // Get joystick y-axis values and multiply with TURN_MULTIPLIER
        m_drivetrain.driveArcade(forwardAmount, rotationAmount); // Run the Drivetrain.driveArcade method with the joystick information
    }

    @Override
    public boolean isFinished()
    {
        return false;
    }

}
