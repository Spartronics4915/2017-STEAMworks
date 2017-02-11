package org.usfirst.frc.team4915.steamworks.sensors;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

// IMUPIDSource adapts our pre-existing BMO055 implementation
// for use in conjunction with PIDController.
public class IMUPIDSource implements PIDSource
{

    private BNO055 m_imu;
    private boolean m_normalized;

    public IMUPIDSource(BNO055 imu, boolean useNormalized)
    {
        this.m_imu = imu;
        this.m_normalized = useNormalized; // A normalized heading is -180 to 180 degrees and a non-normalized one is 0 to 360 degrees
    }

    public void setPIDSourceType(PIDSourceType pidSource)
    {
        if (pidSource != PIDSourceType.kDisplacement)
        {
            System.out.println("IMUPIDSource only supports kDisplacement");
        }
    }

    public PIDSourceType getPIDSourceType()
    {
        return PIDSourceType.kDisplacement;
    }

    public double pidGet()
    {
        if (m_normalized)
        {
            return this.m_imu.getNormalizedHeading();
        }
        else
        {
            return this.m_imu.getHeading();
        }
    }

    public double getHeading()
    {
        if (m_normalized)
        {
            return this.m_imu.getNormalizedHeading();
        }
        else
        {
            return this.m_imu.getHeading();
        }
    }
}
