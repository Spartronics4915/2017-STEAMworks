package org.usfirst.frc.team4915.steamworks.commands.groups;

import org.usfirst.frc.team4915.steamworks.OI;
import org.usfirst.frc.team4915.steamworks.commands.DriveStraightCommand;
import org.usfirst.frc.team4915.steamworks.commands.StopCommand;
import org.usfirst.frc.team4915.steamworks.commands.TurnDegreesIMUCommand;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * This is a generic command group that should work for all autonomous programs
 * we want, you just specify a number of parameters as follows:
 * "Drive", "inches", "Turn", "degrees", "Shoot" "seconds"
 * Example: DriveCommandGroup(drivetrain, oi, "Drive", "96", "Turn", "135",
 * "Shoot", "7")
 */
public class DriveCommandGroup extends CommandGroup
{

    public DriveCommandGroup(Drivetrain drivetrain, OI oi, String... params)
    {

        for (int i = 0; i < params.length; i += 2)
        {
            String command = params[i];
            double value = Double.parseDouble(params[i + 1]);
            switch (command)
            {
                case "Drive":
                    addSequential(new DriveStraightCommand(drivetrain, value));
                case "Turn":
                    addSequential(new TurnDegreesIMUCommand(drivetrain, value * oi.getSideMultiplier()));
                case "Shoot":
                    // TODO: Add a command here
                case "Stop":
                    addSequential(new StopCommand(drivetrain));
                default:
                    addSequential(new StopCommand(drivetrain));
            }
        }
        addSequential(new StopCommand(drivetrain)); // This is so we stop and don't get yelled at by motor saftey when we're done
    }
}
