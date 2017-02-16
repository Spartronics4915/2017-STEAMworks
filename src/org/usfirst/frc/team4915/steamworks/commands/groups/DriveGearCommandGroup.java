package org.usfirst.frc.team4915.steamworks.commands.groups;

import org.usfirst.frc.team4915.steamworks.RobotMap;
import org.usfirst.frc.team4915.steamworks.OI.WallPosition;
import org.usfirst.frc.team4915.steamworks.commands.DriveDistancePIDCmd;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class DriveGearCommandGroup extends CommandGroup {

    public DriveGearCommandGroup(Drivetrain drivetrain, WallPosition wallPosition) { // These variables aren't used anywhere except for here, so should they use the member variable naming convention?
        switch (wallPosition) {
            case ONE:
                // TODO: Measure and add somthing for driving from here
            case TWO:
                addSequential(new DriveDistancePIDCmd(drivetrain, -93.3 + RobotMap.ROBOT_LENGTH)); // Robot is assumed to be starting backwards
            case THREE:
                // TODO: Measure and add somthing for driving from here
        }
    }
}
