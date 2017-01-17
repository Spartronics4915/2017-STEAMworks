
package org.usfirst.frc.team4915.steamworks;

import org.usfirst.frc.team4915.steamworks.commands.IntakeCommand;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;
import org.usfirst.frc.team4915.steamworks.subsystems.Intake;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot
{

    public static Logger logger;

    private Command m_autonomousCommand;
    private SendableChooser<Command> m_chooser = new SendableChooser<>();

    private Drivetrain m_drivetrain;
    private Intake m_intake;
    private OI m_oi;

    @Override
    public void autonomousInit()
    {
        m_autonomousCommand = m_chooser.getSelected();
        if (m_autonomousCommand != null)
        {
            m_autonomousCommand.start();
        }
    }

    @Override
    public void autonomousPeriodic()
    {
        Scheduler.getInstance().run();
    }

    @Override
    public void disabledInit()
    {

    }

    @Override
    public void disabledPeriodic()
    {
        Scheduler.getInstance().run();
    }

    public Intake getIntake()
    {
        return m_intake;
    }

    @Override
    public void robotInit()
    {
        m_intake = new Intake(this);
        m_oi = new OI(this);
        m_drivetrain = new Drivetrain(m_oi.m_driveStick);

        m_chooser.addDefault("Default Auto", new IntakeCommand(this.m_intake));
        // chooser.addObject("My Auto", new MyAutoCommand());
        SmartDashboard.putData("Auto mode", m_chooser);
    }

    @Override
    public void teleopInit()
    {
        if (m_autonomousCommand != null)
        {
            m_autonomousCommand.cancel();
        }
    }

    @Override
    public void teleopPeriodic()
    {
        Scheduler.getInstance().run();
    }

    @Override
    public void testPeriodic()
    {
        LiveWindow.run();
    }
}
