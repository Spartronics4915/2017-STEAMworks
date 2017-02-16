package org.usfirst.frc.team4915.steamworks.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team4915.steamworks.OI.WallPosition;
import org.usfirst.frc.team4915.steamworks.RobotMap;
import org.usfirst.frc.team4915.steamworks.commands.DriveDistancePIDCmd;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

/**
 * Drive across the baseline
 */
public class DriveCrossCommandGroup extends CommandGroup {

    public DriveCrossCommandGroup(Drivetrain drivetrain, WallPosition wallPosition) { // These variables aren't used anywhere except for here, so should they use the member variable naming convention?
        switch (wallPosition) {
            case ONE:
                addSequential(new DriveDistancePIDCmd(drivetrain, 95 + RobotMap.ROBOT_LENGTH));
            case TWO:
                addSequential(new DriveDistancePIDCmd(drivetrain, 95 + RobotMap.ROBOT_LENGTH));
            case THREE:
                addSequential(new DriveDistancePIDCmd(drivetrain, 93.3 - RobotMap.ROBOT_LENGTH));
        }
    }
}
