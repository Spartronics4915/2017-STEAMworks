package com.spartronics4915.frc2017;

import com.spartronics4915.frc2017.subsystems.*;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot
{
    // private Drive mDrive;
    private Intake mIntake;
    private Launcher mLauncher;
    private Cameras mCameras;
    private Climber mClimber;
    private RobotOI mRobotOI;

    @Override
    public void robotInit()
    {
        // boolean success = false;
        try
        {
            // mDrive = Drive.getInstance(); // We put Drive first to ensure the IMU is ready for everything else
            mIntake = Intake.getInstance();
            mLauncher = Launcher.getInstance();
            mCameras = Cameras.getInstance();
            mClimber = Climber.getInstance();
            mRobotOI = new RobotOI(); // Make sure RobotOI is last
            // success = true;
        }
        catch (Exception e)
        {
            // success = false;
            // logException("Could not initialize Robot: ", e);
        }
        // logInitialized(success);
    }

    @Override
    public void robotPeriodic()
    {
        CommandScheduler.getInstance().run(); // Per WPILIB Documentation
    }

    @Override
    public void disabledInit()
    {
        CommandScheduler.getInstance().cancelAll(); // This should run end(true) on every command, i.e. subsystems will reset accordingly
        // logNotice("Robot disabled");
    }

    @Override
    public void disabledPeriodic()
    {
        // Intentionally left blank
    }

    @Override
    public void autonomousInit()
    {
        // TODO: Provide logic for what Autonomous command runs
        // Note that three possible locations are here, in RobotOI.getAutonomousCommand, and a specialized Auto command.
    }

    @Override
    public void autonomousPeriodic()
    {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void teleopInit()
    {
        CommandScheduler.getInstance().cancelAll(); // FIXME: This cancels _all_ commands - is this bad practice?
        CommandScheduler.getInstance().run();
    }

    @Override
    public void teleopPeriodic()
    {
        CommandScheduler.getInstance().run(); // Redundant, but redundancy is good here
    }

    @Override
    public void testPeriodic()
    {

    }

}
