package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

public class ArcadeDriveCommand extends Command
{

    private final Drivetrain m_drivetrain;
    private final Joystick m_driveStick;

    public ArcadeDriveCommand(Drivetrain drivetrain, Joystick driveStick)
    {
        m_drivetrain = drivetrain;
        m_driveStick = driveStick;

        requires(m_drivetrain);
    }

    @Override
    public void initialize()
    {
        m_drivetrain.setControlMode(TalonControlMode.PercentVbus);
    }

    @Override
    public void execute()
    {
        m_drivetrain.setDriveStick(m_driveStick);
        m_drivetrain.driveArcade(); // Run the Drivetrain.driveArcade method with the joystick information
    }

    @Override
    public boolean isFinished()
    {
        return false;
    }
}
