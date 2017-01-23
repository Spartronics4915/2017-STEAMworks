package org.usfirst.frc.team4915.steamworks;

import org.usfirst.frc.team4915.steamworks.Logger.Level;

import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class LoggerChooser extends SendableChooser<Level> implements NamedSendable
{

    private String m_name;

    public LoggerChooser(String name)
    {
        m_name = name;
        addDefault(name + "_" + Level.DEBUG.name(), Level.DEBUG);
        addObject(name + "_" + Level.INFO.name(), Level.INFO);
        addObject(name + "_" + Level.NOTICE.name(), Level.NOTICE);
        addObject(name + "_" + Level.WARNING.name(), Level.WARNING);
        addObject(name + "_" + Level.ERROR.name(), Level.ERROR);
    }

    @Override
    public String getName()
    {
        return m_name;
    }

}
