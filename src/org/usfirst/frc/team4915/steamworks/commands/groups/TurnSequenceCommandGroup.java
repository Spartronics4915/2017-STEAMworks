package org.usfirst.frc.team4915.steamworks.commands.groups;

import org.usfirst.frc.team4915.steamworks.commands.DriveDistancePIDCmd;
import org.usfirst.frc.team4915.steamworks.commands.TurnDegreesIMU;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Command group for testing
 */
public class TurnSequenceCommandGroup extends CommandGroup
{

    private final Drivetrain m_drivetrain;

    public TurnSequenceCommandGroup(Drivetrain drivetrain)
    {
        m_drivetrain = drivetrain;
        addSequential(new DriveDistancePIDCmd(m_drivetrain, 25)); // Calculated by circumscribing a circle around the robot and getting it's radius and adding 4 for some leeway
        addSequential(new TurnDegreesIMU(m_drivetrain, -90));
        addSequential(new DriveDistancePIDCmd(m_drivetrain, 103));
        addSequential(new TurnDegreesIMU(m_drivetrain, -123));
        addSequential(new DriveDistancePIDCmd(m_drivetrain, 3));
        // Additional delays and shooting commands will go here
    }
}
