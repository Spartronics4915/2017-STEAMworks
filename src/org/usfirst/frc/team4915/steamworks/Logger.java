package org.usfirst.frc.team4915.steamworks;

// Logger:
//  a simple class to route all logging through. 
//  future enhancement:
//      * support logging to file
//      * runtime inference of logging level (practice vs competition)
//  usage:
//      singleton: Logger.getInstance().debug("here's my message");
//      via Robot:  robot.logger.debug("here's a debugging msg");
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

    private static Logger s_logger;

    public static Logger getInstance()
    {
        if (s_logger == null)
        {
            s_logger = new Logger();
        }
        return s_logger;
    }

    private enum level
    {
        DEBUG,
        INFO,
        NOTICE,
        WARNING,
        ERROR
    };

    private static int s_loglevel = level.DEBUG.ordinal();

    private Logger()
    {
    } // currently we encourage singleton usage

    public void debug(String msg)
    {
        if (s_loglevel <= level.DEBUG.ordinal())
        {
            logMsg("DEBUG  ", msg);
        }
    }

    public void info(String msg)
    {
        if (s_loglevel <= level.INFO.ordinal())
        {
            logMsg("INFO   ", msg);
        }
    }

    public void notice(String msg)
    {
        if (s_loglevel <= level.NOTICE.ordinal())
        {
            logMsg("NOTICE ", msg);
        }
    }

    public void warning(String msg)
    {
        if (s_loglevel <= level.WARNING.ordinal())
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
        if (!skipStackTrace)
        {
            e.printStackTrace();
        }
    }

    private void logMsg(String lvl, String msg)
    {
        System.out.println(lvl + ": " + msg);
    }

}
