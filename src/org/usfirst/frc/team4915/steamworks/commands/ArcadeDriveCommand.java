package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

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
                                    0, 0, 0, 0 /* zero PIDF  */);
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
    }
    
}
