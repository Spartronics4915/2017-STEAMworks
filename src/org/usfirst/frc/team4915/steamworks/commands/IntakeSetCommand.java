package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class IntakeSetCommand extends Command
{

    private final Intake m_intake;
    private final Intake.State m_state;

    public IntakeSetCommand(Intake intake, Intake.State state)
    {
        m_intake = intake;
        m_state = state;
        requires(m_intake);
    }

    @Override
    public void initialize()
    {
        String nm = m_intake.getStateString(m_state);
        m_intake.m_logger.notice("initialize IntakeSetCommand " + nm);
        SmartDashboard.putString("Intake State", nm);
        SmartDashboard.putNumber("Intake Speed", m_intake.getIntakeSpeed(m_state));
    }

    @Override
    public void execute()
    {
        m_intake.setIntake(m_state);
        SmartDashboard.putNumber("Intake Current", m_intake.getIntakeCurrent());
    }

    @Override
    public boolean isFinished()
    {
        return false; // we rely on buttons to control our lifetime
    }

    @Override
    public void interrupted()
    {
        m_intake.m_logger.notice("interrupted IntakeSetCommand " +
                m_intake.getStateString(m_state));
        end();
    }

    @Override
    public void end()
    {
        m_intake.m_logger.notice("end IntakeSetCommand " + 
                                m_intake.getStateString(m_state));
        m_intake.setIntake(Intake.State.OFF);
    }
}
