package org.usfirst.frc.team4915.steamworks.commands.groups;

import org.usfirst.frc.team4915.steamworks.RobotMap;
import org.usfirst.frc.team4915.steamworks.OI.WallPosition;
import org.usfirst.frc.team4915.steamworks.commands.DriveDistancePIDCmd;
import org.usfirst.frc.team4915.steamworks.commands.TurnDegreesIMU;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class DriveShootCommandGroup extends CommandGroup
{

    public DriveShootCommandGroup(Drivetrain drivetrain, WallPosition wallPosition, int sideMultiplier)
    { // These variables aren't used anywhere except for here, so should they use the member variable naming convention?
        switch (wallPosition)
        {
            case ONE:
                addSequential(new DriveDistancePIDCmd(drivetrain, 25));
                addSequential(new TurnDegreesIMU(drivetrain, 90*sideMultiplier));
                addSequential(new DriveDistancePIDCmd(drivetrain, 248-RobotMap.ROBOT_LENGTH));
            case TWO:
                addSequential(new DriveDistancePIDCmd(drivetrain, 25));
                addSequential(new TurnDegreesIMU(drivetrain, 90*sideMultiplier));
                addSequential(new DriveDistancePIDCmd(drivetrain, 124-RobotMap.ROBOT_LENGTH));
            case THREE:
                // TODO: To be measured
                addSequential(new DriveDistancePIDCmd(drivetrain, 25));
        }
        // These commands are always run
        addSequential(new TurnDegreesIMU(drivetrain, 133.7*sideMultiplier));
        addSequential(new DriveDistancePIDCmd(drivetrain, 15)); // TODO: This number needs to be measured
    }
}
