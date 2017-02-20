package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Cameras;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ChooseCameraCommand extends Command
{

    private final Cameras m_cameras;
    private int m_whichCamera;

    public ChooseCameraCommand(Cameras cameras, int whichCamera)
    {
        m_cameras = cameras;
        m_whichCamera = whichCamera;
        requires(m_cameras);
    }

    @Override
    public void initialize()
    {
        m_cameras.changeCamera(m_whichCamera);

        // Log the selection for debugging?
        /*
         * if (m_camera == Cameras.CameraType.CAM_FWD) {
         * }
         * else if (m_camera == Cameras.CameraType.CAM_REV) {
         * }
         */
    }

    @Override
    public void execute()
    {
    }

    @Override
    public boolean isFinished()
    {
        return true; // Once we're done, we're done...
    }

    @Override
    public void interrupted()
    {
    }

    @Override
    public void end()
    {
    }
}
