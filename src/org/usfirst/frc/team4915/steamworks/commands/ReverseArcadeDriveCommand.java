package org.usfirst.frc.team4915.steamworks.commands;

import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;


/**
 *
 */
public class ReverseArcadeDriveCommand extends Command
{

    private final Drivetrain m_drivetrain;
    
    public ReverseArcadeDriveCommand(Drivetrain drivetrain)
    {
        m_drivetrain = drivetrain;

        requires(m_drivetrain);
    }

    @Override
    protected void end()
    {
        m_drivetrain.m_logger.info("ReverseArcadeDriveCommand end");
        m_drivetrain.resetReverse();
    }

    
    protected void execute()
    {
        m_drivetrain.setReverse();
        m_drivetrain.driveArcade();
    }

    
    protected void initialize()
    {
        m_drivetrain.m_logger.info("ReverseArcadeDriveCommand initialize");;
        m_drivetrain.setControlMode(TalonControlMode.PercentVbus, 12.0, -12.0, 
                                    0, 0, 0, 0 /* zero PIDF  */);
    }

    
    protected void interrupted()
    {
        m_drivetrain.m_logger.info("ReverseArcadeDriveCommand interrupted");
        end();
    }

    @Override
    protected boolean isFinished()
    {
        return false;
    }
}
