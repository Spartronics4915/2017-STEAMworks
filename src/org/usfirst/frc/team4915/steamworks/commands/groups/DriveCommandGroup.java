package org.usfirst.frc.team4915.steamworks.commands.groups;

import org.usfirst.frc.team4915.steamworks.OI;
import org.usfirst.frc.team4915.steamworks.commands.DriveStraightCommand;
import org.usfirst.frc.team4915.steamworks.commands.TurnDegreesIMU;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * This is a generic command group that should work for all autonomous programs
 * we want, you just specify a number of parameters as follows:
 *   "Drive", "inches", "Turn", "degrees", "Shoot" "seconds"
 *   Example: DriveCommandGroup(drivetrain, oi, "Drive", "96", "Turn", "135", "Shoot", "7") 
 */
public class DriveCommandGroup extends CommandGroup
{
    public static String DriveCommand = "Drive";
    public static String TurnCommand = "Turn";
    public static String ShootCommand = "Shoot";
    
    public DriveCommandGroup(Drivetrain drivetrain, OI oi, String... params) {
    	
    	for (int i = 0; i < params.length; i+=2) 
    	{
    		String command = params[i];
    		double value = Double.parseDouble(params[i+1]);
    		
    		if (command.equals(DriveCommand)) 
    		{
    			addSequential(new DriveStraightCommand(drivetrain, value));
    		} 
    		else if (command.equals(TurnCommand))
    		{
    			addSequential(new TurnDegreesIMU(drivetrain, value * oi.getSideMultiplier()));
    		} 
    		else if (command.equals(ShootCommand))
    		{
    			// TODO
    		}
    	}
    }
}
