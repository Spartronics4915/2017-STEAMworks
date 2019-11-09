package com.spartronics4915.frc2017.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.Timer;

public class DelayCommand extends Command
{
    private Timer m_timer;
    private double m_duration;

    public DelayCommand(double duration)
    {
        m_duration = duration;          // Save the duration of the delay
        m_timer = new Timer();          // Create the timer
    }

    @Override
    public void initialize()
    {
        m_timer.reset();                // Reset the timer
        m_timer.start();                // Make sure it is started
    }

    @Override
    public void execute()
    {
        // Do nothing... Maybe need to feed the motors with zeros?
    }

    @Override
    public boolean isFinished()
    {
        // If we are done, return true. Otherwise false.
        return (m_timer.hasPeriodPassed(m_duration));
    }

    @Override
    public void interrupted()
    {
        end();
    }

    @Override
    public void end()
    {
        m_timer.stop();                 // Stop the timer
    }

}
