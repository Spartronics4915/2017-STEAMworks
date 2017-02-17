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
    public GenericCommandGroup(Drivetrain drivetrain, OI oi, double[] parameters) {
        addSequential(new DriveDistancePIDCmd(drivetrain, parameters[0]));
        addSequential(new TurnDegreesIMU(drivetrain, parameters[1]*oi.getSideMultiplier()));
        addSequential(new DriveDistancePIDCmd(drivetrain, parameters[2]));
        addSequential(new TurnDegreesIMU(drivetrain, parameters[3]*oi.getSideMultiplier()));
        addSequential(new DriveDistancePIDCmd(drivetrain, parameters[3]));
    }
}
