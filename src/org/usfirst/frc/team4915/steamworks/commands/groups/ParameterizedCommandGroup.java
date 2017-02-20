package org.usfirst.frc.team4915.steamworks.commands.groups;

import org.usfirst.frc.team4915.steamworks.OI;
import org.usfirst.frc.team4915.steamworks.commands.DriveStraightCommand;
import org.usfirst.frc.team4915.steamworks.commands.LauncherCommand;
import org.usfirst.frc.team4915.steamworks.commands.StopCommand;
import org.usfirst.frc.team4915.steamworks.commands.TurnDegreesIMUCommand;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher.LauncherState;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * This is a generic command group that should work for all autonomous programs
 * we want, you just specify a number of parameters as follows:
 * "Drive", "inches", "Turn", "degrees", "Shoot" "seconds"
 * Example: DriveCommandGroup(drivetrain, oi, "Drive", "96", "Turn", "135",
 * "Shoot", "7")
 */
public class ParameterizedCommandGroup extends CommandGroup
{

    public ParameterizedCommandGroup(Drivetrain drivetrain, Launcher launcher, OI oi, String... params)
    {
        int safeMaxIndex = 0;
        requires(drivetrain);
        if (params.length % 2 != 0)
        {
            // We subtract 2 here because arrays start at 0 in Java, and we want one less then the greatest array index so we subtract one more
            safeMaxIndex = params.length - 2; // If we have an odd number of indices, then the safe length is one less so we don't get a null array value
        }
        else
        {
            safeMaxIndex = params.length - 1;
        }

        for (int i = 0; i < safeMaxIndex; i += 2)
        {
            String command = params[i];
            double value = Double.parseDouble(params[i + 1]);
            switch (command)
            {
                case "Drive":
                    addSequential(new DriveStraightCommand(drivetrain, value));
                    break;
                case "Turn":
                    addSequential(new TurnDegreesIMUCommand(drivetrain, value * oi.getSideMultiplier()));
                    break;
                case "Shoot":
                    addSequential(new LauncherCommand(launcher, LauncherState.ON, true));
                    break;
                case "Stop":
                    addSequential(new StopCommand(drivetrain)); // Takes no parameters
                    break;
                default:
                    drivetrain.m_logger.warning("ParameterizedCommandGroup Unrececognized parameter " + command);
                    break;
            }
        }
        addSequential(new StopCommand(drivetrain)); // This is so we stop and don't get yelled at by motor saftey when we're done
    }
}
