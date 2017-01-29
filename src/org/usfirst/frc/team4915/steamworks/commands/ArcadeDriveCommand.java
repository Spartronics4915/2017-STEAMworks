package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import com.ctre.CANTalon.TalonControlMode;
import org.usfirst.frc.team4915.steamworks.Logger;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;

public class ArcadeDriveCommand extends Command
{
    private final Drivetrain m_drivetrain;
    private final XboxController m_driveStick;
    private final Logger m_logger;

    public ArcadeDriveCommand(Drivetrain drivetrain, XboxController driveStick)
    {
        m_drivetrain = drivetrain;
        m_driveStick = driveStick;
        
        m_logger = new Logger("ArcadeDriveCommand", Logger.Level.DEBUG);
        
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
