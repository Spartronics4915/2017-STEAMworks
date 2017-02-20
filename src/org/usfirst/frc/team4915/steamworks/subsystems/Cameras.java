package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.UsbCameraInfo;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.VideoMode;

// Notes for USB camera setup
/* Microsoft Lifecam HD-3000 resolutions (with MJPEG compression)
 * (Collected via 'lsusb' command on Linux)
 *   160x120    (4x3)
 *   176x144    (11x9)
 *   320x240    (4x3)
 *   352x288    (11x9)
 *   416x240    (16x9)
 *   640x360    (16x9)
 *   640x480    (4x3)
 *   800x448    (16x9)
 *   800x600    (4x3)
 *   960x544    (16x9)
 *   1280x720   (16x9)
 */
/* Genius 120-degree Ultra Wide Angle Full HD Conference Webcam
 * (WideCam F100) https://www.amazon.com/gp/product/B0080CE5M4
 * (Collected via 'lsusb' command on Linux)
 *   320x240    (4x3)
 *   352x288    (11x9)
 *   640x360    (16x9)
 *   800x448    (16x9)
 *   960x544    (16x9)
 *   1280x720   (16x9)
 */

public class Cameras extends SpartronicsSubsystem
{
    // Values used by changeCamera() method
    public static final int CAM_FWD = 0;
    public static final int CAM_REV = 1;
    public static final int CAM_NONE = 2;

    private static final int imageWidth = 320;
    private static final int imageHeight = 240;
    private static final int frameRate = 20;

    private static final int serverPort = 1180;

    // Save space in an array for two USB cameras
    private UsbCamera m_camera[] = new UsbCamera[2];

    private int m_numCameras;           // Track the number of cameras we have
    private int m_currentCamera;        // Store the current camera ID

    private MjpegServer m_mjpegServer;  // The camera server object

    private Logger m_logger;

    public Cameras() {
        m_logger = new Logger("Cameras", Logger.Level.DEBUG);

        m_logger.info("Constructor called");

        m_numCameras=0;         // Initialize the camera counter

        // Get a list of attached USB cameras
        UsbCameraInfo camInfo[] = UsbCamera.enumerateUsbCameras();

        // Go through the list of camera info and set up the cameras
        for (int i=0 ; i<camInfo.length ; i++)
        {
            // Only assign CAM_FWD and CAM_REV (and assume that the first
            // camera in the list is CAM_FWD -- is this a good assumption?)
            if (m_numCameras <= CAM_REV)
            {
                // Create a new camera instance
                m_camera[m_numCameras] =
                    new UsbCamera(camInfo[i].name, camInfo[i].dev);

                // Configure camera settings
                m_camera[m_numCameras].setVideoMode(
                        VideoMode.PixelFormat.kMJPEG,
                        imageWidth, imageHeight, frameRate);

                // Debugging in case logger is not working
                // System.out.println("Camera " + m_numCameras +
                //       " (" + camInfo[i].dev + ") " + ": " + camInfo[i].name);

                m_logger.info("Camera " + m_numCameras +
                        " (" + camInfo[i].dev + ") " + ": " + camInfo[i].name);

                m_numCameras++; // Increment our camera counter
            }
            else
            {
                // If there were extra cameras in the list, log them

                // Debugging in case logger is not working
                // System.out.println("Unexpected source found ("
                //      + camInfo[i].dev + ") " + ": " + camInfo[i].name);

                m_logger.info("Unexpected source found ("
                        + camInfo[i].dev + ") " + ": " + camInfo[i].name);
            }
        }

        // Now that all the cameras have been found, set their modes and
        // start up the server (if we have at least one camera...)
        if ((m_numCameras >= 1) && (m_camera[CAM_FWD] != null))
        {
            // Create camera network server, and assign the first camera
            m_mjpegServer = new MjpegServer("USB Camera Server", serverPort);
            m_mjpegServer.setSource(m_camera[CAM_FWD]);
            m_currentCamera = CAM_FWD;
        }

        m_logger.info("Constructor finished, " + m_numCameras + " found");

        // Debugging in case logger is not working
        // System.out.println("Cameras(): Constructor done, " + m_numCameras +
        //      " found");
    }

    public boolean changeCamera(int camera) {
        boolean status = false;

        // Bail out if we have no cameras configured
        if (m_numCameras == 0)
        {
            return false;
        }

        switch (camera)
        {
            case CAM_NONE:
                // TODO (if a "blank" stream is desired)
                // Create a static video source with CvSource, and
                // assign it to the mjpegServer
                // Idea: Make the source a static picture of a test pattern
                m_logger.info("Selecting CAM_NONE");
                m_currentCamera = CAM_NONE;
                break;

            case CAM_FWD:
                // First check to make sure we have this camera available
                if (m_currentCamera != CAM_FWD)
                {
                    if (m_numCameras >= CAM_FWD)
                    {
                        if ((m_camera[CAM_FWD] != null) &&
                                (m_camera[CAM_FWD].isValid()))
                        {
                            m_logger.info("Selecting CAM_FWD");
                            // Assign the camera to the streaming server
                            m_mjpegServer.setSource(m_camera[CAM_FWD]);
                            m_currentCamera = CAM_FWD;
                            status = true;      // We have been successful!
                        }
                        else
                        {
                            m_logger.info("CAM_FWD is invalid!");
                        }
                    }
                    else
                    {
                        m_logger.info("No CAM_FWD!");
                    }
                }
                break;

            case CAM_REV:
                // First check to make sure we have this camera available
                if (m_currentCamera != CAM_REV)
                {
                    if (m_numCameras >= CAM_REV)
                    {
                        if ((m_camera[CAM_REV] != null) &&
                                (m_camera[CAM_REV].isValid()))
                        {
                            m_logger.info("Selecting CAM_REV");
                            // Assign the camera to the streaming server
                            m_mjpegServer.setSource(m_camera[CAM_REV]);
                            m_currentCamera = CAM_REV;
                            status = true;      // We have been successful!
                        }
                        else
                        {
                            m_logger.info("CAM_REV is invalid!");
                        }
                    }
                    else
                    {
                        m_logger.info("No CAM_REV!");
                    }
                }
                break;

            default:
                m_logger.info("Invalid camera: " + camera);
                break;
        }
        return status;
    }

    @Override
    protected void initDefaultCommand() {
        // No default command
    }
}
