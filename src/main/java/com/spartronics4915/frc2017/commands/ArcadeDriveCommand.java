package com.spartronics4915.frc2017.commands;

import com.spartronics4915.frc2017.subsystems.Drivetrain;


import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.command.Command;

public class ArcadeDriveCommand extends Command
{

    private final Drivetrain m_drivetrain;



    public ArcadeDriveCommand(Drivetrain drivetrain)
    {
        m_drivetrain = drivetrain;

        requires(m_drivetrain);
    }

    @Override
    public void initialize()
    {
        m_drivetrain.m_logger.info("ArcadeDriveCommand initialize");;
        m_drivetrain.setControlMode(TalonControlMode.PercentVbus, 12.0, -12.0,
                                    0, 0, 0, 0 /* zero PIDF  */, 5.0);
    }

    @Override
    public void execute()
    {
        m_drivetrain.driveArcade(); // Run the Drivetrain.driveArcade method with the joystick information
    }

    @Override
    public boolean isFinished()
    {
        return false;
    }

    @Override
    public void interrupted()
    {
        m_drivetrain.m_logger.info("ArcadeDriveCommand interrupted");
        end();
    }

    @Override
    public void end()
    {
        m_drivetrain.m_logger.info("ArcadeDriveCommand end");
        m_drivetrain.stop();
    }

}
