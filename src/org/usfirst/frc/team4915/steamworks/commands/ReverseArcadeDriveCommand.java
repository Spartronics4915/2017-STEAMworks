package org.usfirst.frc.team4915.steamworks.commands;

import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team4915.steamworks.subsystems.Cameras;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;


/**
 *
 */
public class ReverseArcadeDriveCommand extends Command
{

    private final Drivetrain m_drivetrain;
    private final Cameras m_cameras;
    
    public ReverseArcadeDriveCommand(Drivetrain drivetrain, Cameras cameras)
    {
        m_drivetrain = drivetrain;
        m_cameras = cameras;

        requires(m_drivetrain);
        requires(m_cameras);
    }

    @Override
    protected void end()
    {
        m_drivetrain.m_logger.info("ReverseArcadeDriveCommand end");
        m_drivetrain.resetReverse(m_cameras);
    }

    
    protected void execute()
    {
        m_drivetrain.driveArcade();
    }

    
    protected void initialize()
    {
        m_drivetrain.m_logger.info("ReverseArcadeDriveCommand initialize");;
        m_drivetrain.setControlMode(TalonControlMode.PercentVbus, 12.0, -12.0, 
                                    0, 0, 0, 0 /* zero PIDF  */);
        m_drivetrain.setReverse(m_cameras);

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
