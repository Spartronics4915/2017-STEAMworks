// FIXME: This Class has issues!!!
// 1. relies heavily on logging
// 2. overcommented code - or maybe not? not using states should clear this up
// 3. redundant code
// all of which is a product of the state system and should be fixable, but is kind of a lot at once

package com.spartronics4915.frc2017.subsystems;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.UsbCameraInfo;
import edu.wpi.cscore.VideoMode;

public class Cameras extends SpartronicsSubsystem
{
    private static Cameras mInstance = null;

    public static Cameras getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new Cameras();
        }
        return mInstance;
    }

    // Values used by changeCamera() method
    public static final int CAM_FWD = 0;
    public static final int CAM_REV = 1;
    public static final int CAM_NONE = 2;

    private static final int mImageWidth = 320;
    private static final int mImageHeight = 240;
    private static final int mFrameRate = 20;

    private static final int mServerPort = 1180;

    private UsbCamera mCamera[]; // Save space in an array for two USB cameras
    private int mNumCameras; // Track the number of cameras we have
    private int mCurrentCamera; // Store the current camera ID
    private MjpegServer mMjpegServer; // The camera server object


    private Cameras()
    {
        mCamera = new UsbCamera[2];
        mNumCameras = 0;

        UsbCameraInfo camInfo[] = UsbCamera.enumerateUsbCameras();

        for (int i = 0; i < camInfo.length; i++)
        {
            if (mNumCameras <= CAM_REV)
            {
                mCamera[mNumCameras] = new UsbCamera(camInfo[i].name, camInfo[i].dev);
                mCamera[mNumCameras].setVideoMode(VideoMode.PixelFormat.kMJPEG, mImageWidth, mImageHeight, mFrameRate);
                mNumCameras++;
            }
        }

        if ((mNumCameras >= 1) && (mCamera[CAM_FWD] != null))
        {
            mMjpegServer = new MjpegServer("USB Camera Server", mServerPort);
            mMjpegServer.setSource(mCamera[CAM_FWD]);
            mCurrentCamera = CAM_FWD;
        }
    }

    public boolean changeCamera(int camera)
    {
        boolean status = false;

        if (mNumCameras == 0)
            return false;

        switch (camera)
        {
            case CAM_NONE:
                mCurrentCamera = CAM_NONE;
                break;

            case CAM_FWD:
                if (mCurrentCamera != CAM_FWD)
                {
                    if (mNumCameras >= CAM_FWD)
                    {
                        if ((mCamera[CAM_FWD] != null) && (mCamera[CAM_FWD].isValid()))
                        {
                            mMjpegServer.setSource(mCamera[CAM_FWD]);
                            mCurrentCamera = CAM_FWD;
                            status = true;
                        }
                    }
                }
                break;

            case CAM_REV:
                if (mCurrentCamera != CAM_REV)
                {
                    if (mNumCameras >= CAM_REV)
                    {
                        if ((mCamera[CAM_REV] != null) && (mCamera[CAM_REV].isValid()))
                        {
                            mMjpegServer.setSource(mCamera[CAM_REV]);
                            mCurrentCamera = CAM_REV;
                            status = true;
                        }
                    }
                }
                break;

            default:
                break;
        }
        return status;
    }
}
