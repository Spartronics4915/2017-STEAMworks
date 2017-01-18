package org.usfirst.frc.team4915.steamworks;

// Logger:
//  a simple class to route all logging through. 
//  future enhancement:
//      * support logging to file
//      * runtime inference of logging level (practice vs competition)
//  usage:
//      singleton: Logger.getInstance().debug("here's my message");
//      via Robot:  robot.m_logger.debug("here's a debugging msg");
//      via Subsystem: this.m_logger.debug("here's a message");
//  loglevel conventions:
//      debug: used for debugging software... not available during
//          competition.
//      info:  used for less interesting but non-debugging message.
//      notice: used for msgs you always want to see in log
//          - subsystem initializations
//          - important state transitions (ie entering auto, teleop)
//      warning: used to convey non-fatal but abnormal state
//      error: used to convey strong abnormal conditions
//      exception: used in a catch block to report exceptions.
//
public class Logger 
{
    public enum Level
    {
        DEBUG,
        INFO,
        NOTICE,
        WARNING,
        ERROR
    };
    private static int s_minloglevel = Level.DEBUG.ordinal();
    
    private static Logger s_logger;
    public static Logger getInstance()
    {
        if(s_logger == null)
        {
            s_logger = new Logger("<shared>", Level.DEBUG);
        }
        return s_logger; 
    }
    
    
    private int m_loglevel; // per-instance
    private String m_namespace;
    
    public Logger(String nm, Level lev) 
    {
        m_namespace = nm;
        m_loglevel = lev.ordinal();
    } 

    public void debug(String msg)
    {
        if(reportLevel(Level.DEBUG))
        {
            logMsg("DEBUG  ", msg);
        }
    }
    
    public void info(String msg)
    {
        if(reportLevel(Level.INFO))
        {
            logMsg("INFO   ", msg);
        }
    }
    
    public void notice(String msg)
    {
        if(reportLevel(Level.NOTICE))
        {
            logMsg("NOTICE ", msg);
        }
    }
    
    public void warning(String msg)
    {
        if(reportLevel(Level.WARNING))
        {
            logMsg("WARNING", msg);
        }
    }
    
    public void error(String msg)
    {
        logMsg("ERROR  ", msg);
    }
    
    public void exception(Exception e, boolean skipStackTrace)
    {
        logMsg("EXCEPT ", e.getMessage());
        if(!skipStackTrace)
        {
            e.printStackTrace();
        }
    }
    
    private boolean reportLevel(Level lev)
    {
        int ilev = lev.ordinal();
        if(ilev >= m_loglevel && ilev >= s_minloglevel)
            return true;
        else
            return false;
    }

    private void logMsg(String lvl, String msg)
    {
        System.out.println(m_namespace + " " + lvl + ": " + msg);
    }
 
}
