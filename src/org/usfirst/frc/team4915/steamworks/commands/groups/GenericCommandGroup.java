package org.usfirst.frc.team4915.steamworks.commands.groups;

import org.usfirst.frc.team4915.steamworks.OI;
import org.usfirst.frc.team4915.steamworks.commands.DriveDistancePIDCmd;
import org.usfirst.frc.team4915.steamworks.commands.TurnDegreesIMU;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * This is a generic command group that should work for all autonomous programs we want, you just specify a number of parameters in an array
 */
public class GenericCommandGroup extends CommandGroup {
    int sideMultiplier;
    public GenericCommandGroup(Drivetrain drivetrain, OI oi, double param1, double param2, double param3, double param4, double param5) {
        // It's more elegant to use an array or list here, but arrays suck in Java and I don't have time to deal with a list
        addSequential(new DriveDistancePIDCmd(drivetrain, param1));
        addSequential(new TurnDegreesIMU(drivetrain, param2*oi.getSideMultiplier()));
        addSequential(new DriveDistancePIDCmd(drivetrain, param3));
        addSequential(new TurnDegreesIMU(drivetrain, param4*oi.getSideMultiplier()));
        addSequential(new DriveDistancePIDCmd(drivetrain, param5));
    }
}
