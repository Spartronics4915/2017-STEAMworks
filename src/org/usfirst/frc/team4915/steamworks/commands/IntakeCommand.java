package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class IntakeCommand extends Command
{

    private final Robot robot;

    public IntakeCommand(Robot robot)
    {
        this.robot = robot;

        requires(robot.getIntake());
    }

    @Override
    public void end()
    {
        robot.getIntake().setIntake(false);
    }

    @Override
    public void execute()
    {
    }

    @Override
    public void initialize()
    {
        robot.getIntake().setIntake(true);
    }

    @Override
    public void interrupted()
    {
        end();
    }

    @Override
    public boolean isFinished()
    {
        return false;
    }
}
