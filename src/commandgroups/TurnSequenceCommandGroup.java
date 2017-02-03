package commandgroups;

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
        // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.
        m_drivetrain = drivetrain;
        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
        addSequential(new TurnDegreesIMU(m_drivetrain, 45));
        addSequential(new TurnDegreesIMU(m_drivetrain, -45));
        addSequential(new TurnDegreesIMU(m_drivetrain, 180));

    }
}
