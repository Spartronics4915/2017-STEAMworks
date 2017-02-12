package org.usfirst.frc.team4915.steamworks.commands;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team4915.steamworks.subsystems.Climber;
import org.usfirst.frc.team4915.steamworks.subsystems.Climber.State;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class ClimberSetCommand extends Command
{
    private final Climber m_climber;
    private final Climber.State m_state;
    
    private double currentJump;
    //testing needed to find the current
    private double safetyValue;
    private int counter = 0;
    
    private boolean safe = true;
    public boolean m_climberIsRecording = false;
    private double m_timeSinceRecording;
    private final List<Double> m_readCurrent = new ArrayList<>();

    public ClimberSetCommand(Climber climber, State on)
    {
        m_climber = climber;
        m_state = on;
        requires(m_climber);
    }
    
    public void climbersafetySwitch()
    {
        m_readCurrent.clear();
        m_climberIsRecording = true;
    }

    @Override
    public void initialize()
    {
        //Disabled for now to ensure no breaking of code
        //climbersafetySwitch();
    }

    @Override
    public void execute()
    {
        m_climber.setClimber(m_state);
        if(m_climberIsRecording && m_timeSinceRecording >= 1 && counter == 100)
        {
            /* TEMPORARY MAY CAUSE PROBLEMS: 
             * sets the safety value to the first of the array this may miss something
             * Checks the last value stored
             * Still doesn't write the arraylist to memory for later viewing*/
            safetyValue = m_readCurrent.get(0);
            currentJump = m_readCurrent.get(m_readCurrent.size() - 1);
            if(currentJump > safetyValue)
            {
                safe = false;   
            }
            m_readCurrent.clear();
        }
    }

    @Override
    public boolean isFinished()
    {
        counter++;
        m_timeSinceRecording = timeSinceInitialized();
        return !safe;
    }

    @Override
    public void interrupted()
    {
        m_climber.m_logger.notice("interrupted ClimberSetCommand " + m_climber.getStateString(m_state));
        end();
    }

    @Override
    public void end()
    {
        m_climber.m_logger.notice("end ClimberSetCommand " + m_climber.getStateString(m_state));
        m_climber.setClimber(Climber.State.OFF);
    }

}
