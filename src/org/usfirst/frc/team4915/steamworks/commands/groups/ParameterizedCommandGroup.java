package org.usfirst.frc.team4915.steamworks.commands.groups;

import org.usfirst.frc.team4915.steamworks.OI;
import org.usfirst.frc.team4915.steamworks.commands.DriveCurveCommand;
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
        requires(drivetrain);
        
        drivetrain.m_logger.debug("ParamterizedCommandGroup: " + String.join(",", params));

        for (int i = 0; i < params.length; )
        {
            String command = params[i++];

            switch (command)
            {
                case "Drive":
                	double distance = Double.parseDouble(params[i++]);
                    addSequential(new DriveStraightCommand(drivetrain, distance));
                    break;
                case "Curve":
                	distance = Double.parseDouble(params[i++]);
                	double curve = Double.parseDouble(params[i++]);
                    addSequential(new DriveCurveCommand(drivetrain, distance, curve * oi.getSideMultiplier()));
                    break;
                case "Turn":
                	double angle = Double.parseDouble(params[i++]);
                    addSequential(new TurnDegreesIMUCommand(drivetrain, angle * oi.getSideMultiplier()));
                    break;
                case "Shoot":
                    addSequential(new LauncherCommand(launcher, LauncherState.ON, true)); // Takes no parameters
                    break;
                case "Stop":
                    addSequential(new StopCommand(drivetrain)); // Takes no parameters
                    break;
                default:
                    drivetrain.m_logger.warning("ParameterizedCommandGroup Unrececognized parameter "+command);
                    break;
            }
        }
        addSequential(new StopCommand(drivetrain)); // This is so we stop and don't get yelled at by motor saftey when we're done
    }
}
