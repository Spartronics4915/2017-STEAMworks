package org.usfirst.frc.team4915.steamworks.commands.groups;

import org.usfirst.frc.team4915.steamworks.OI;
import org.usfirst.frc.team4915.steamworks.commands.DriveStraightCommand;
import org.usfirst.frc.team4915.steamworks.commands.TurnDegreesIMU;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * This is a generic command group that should work for all autonomous programs
 * we want, you just specify a number of parameters in an array
 */
public class GenericCommandGroup extends CommandGroup
{

    int sideMultiplier;

    public GenericCommandGroup(Drivetrain drivetrain, OI oi, double param1, double param2, double param3, double param4, double param5)
    {
//        int i = 0;
//        while (!drivetrain.isIMUInitalized() || i > 10)
//        {
//            i++;
//            try
//            {
//                Thread.sleep(100);
//            }
//            catch (InterruptedException e)
//            {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            // We're just waiting here
//        }
        // It's more elegant to use an array or list here, but arrays suck in Java and I don't have time to deal with a list
        addSequential(new DriveStraightCommand(drivetrain, param1));
        if (param2 != Double.NaN)
        {
            addSequential(new TurnDegreesIMU(drivetrain, param2 * oi.getSideMultiplier()));
        }
        addSequential(new DriveStraightCommand(drivetrain, param3));
        if (param4 != Double.NaN)
        {
            addSequential(new TurnDegreesIMU(drivetrain, param4 * oi.getSideMultiplier()));
        }
        addSequential(new DriveStraightCommand(drivetrain, param5));
    }
}
