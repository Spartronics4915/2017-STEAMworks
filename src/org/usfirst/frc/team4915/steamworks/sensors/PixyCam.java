/*
 * PixCam - a java+roboRIOI+I2C interface to CMUCam
 *    see: http://cmucam.org/projects/cmucam5/wiki/Porting_Guide
 *      The I2C interface operates as an I2C slave and requires polling.
 *      There are weak 4.7K pullups to 5V on SDA and SCL signals, via R14 and R15.
 *      I2C signals are 5V tolerant. The I2C address can be configured in
 *      the "Interface" tab of the Configure Parameters dialog in PixyMon.
 *      Here's how to hook up your controller's I2C to Pixy:
 *          Pin 10 ➜ your controller's ground signal
 *          Pin 9 (I2C SDA) ➜ your controller's I2C SDA signal
 *          Pin 5 (I2C SCL) ➜ your controller's I2C SCL signal
 *      Note, when talking to more than one Pixy over I2C, you will need to
 *      configure a different I2C address for each Pixy so they don't step
 *      on each other. You can make a "multi-crimp cable", meaning you can
 *      take a 10-conductor ribbon cable and crimp N 10-pin IDC connectors i
 *      to it and plug into to your N Pixys. That is, when selecting I2C as
 *      an interface, all signals on Pixy's I/O connector go into a
 *      high-impedance state and won't interfere with each other, waste
 *      power, etc.
 */

package org.usfirst.frc.team4915.steamworks.sensors;

import edu.wpi.first.wpilibj.I2C;

import java.nio.ByteBuffer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.concurrent.*;

class PixyCam
{

    public class Point2
    {

        public double m_x, m_y;
    }

    private enum BlockType
    {
        NormalBlock,
        CCBlock
    }

    private class Block // fields should be ushort, but seem to fit
    {
        public short signature;
        public short x;
        public short y;
        public short width;
        public short height;
        public short angle; // only valid for color-coded blocks
    }
    
    private enum SetOp
    {
        LED,
        Brightness,
        Servos
    }
    
    private class SetCmd
    {
        public SetOp op;
        public short a, b, c;
        
        public SetCmd(SetOp o, short aa, short bb, short cc)
        {
            op = o;
            a = aa;
            b = bb;
            c = cc;
        }
    }
        
    private class PixyCamUpdateTask extends TimerTask
    {

        private PixyCam m_pixycam;

        private PixyCamUpdateTask(PixyCam cam)
        {
            m_pixycam = cam;
        }

        public void run()
        {
            m_pixycam.update();
        }
    }

    private static final int k_maxBlocks = 10;
    private static final int k_maxSendBufSize = 6;
    private static final int k_bufferSize = 1024;
    private static final int k_threadPeriod = 20; // ms, 50fps frame-rate of PixyCam
    private static final short k_startWord = (short) 0xaa55;
    private static final short k_startWordCC = (short) 0xaa56;
    private static final short k_startWordX = 0x55aa;
    private static final byte k_syncLED = (byte) 0xfd;
    private static final byte k_syncBrightness = (byte) 0xfe;
    private static final byte k_syncServos = (byte) 0xff;

    private I2C m_i2c;
    private java.util.Timer m_scheduler;
    private volatile double m_targetX, m_targetY;
    private volatile boolean m_initialized;
    private ConcurrentLinkedQueue<SetCmd> m_setcmdQueue;
    private BlockType m_blockType;
    private byte[] m_bytes;
    private boolean m_skipStart;
    private Block[] m_blocks;
    private ByteBuffer m_sendBuf;
   
    public PixyCam(int deviceAddress)
    {
        m_i2c = new I2C(I2C.Port.kOnboard, deviceAddress);
        m_scheduler = new java.util.Timer();
        m_scheduler.schedule(new PixyCamUpdateTask(this), 0L, k_threadPeriod);
        m_initialized = false;
        m_skipStart = false;
        m_bytes = new byte[k_bufferSize];
        m_blocks = new Block[k_maxBlocks];
        m_setcmdQueue = new ConcurrentLinkedQueue<SetCmd>();
        m_sendBuf = ByteBuffer.allocateDirect(k_maxSendBufSize);
    }

    public boolean GetTarget(Point2 center)
    {
        if (m_initialized)
        {
            center.m_x = m_targetX;
            center.m_y = m_targetY;
        }
        return m_initialized;
    } 
    public void SetCamBrightness(short b)
    {
        m_setcmdQueue.add(new SetCmd(SetOp.Brightness, b, (short) 0, (short) 0));
    }
    
    public void SetLEDColor(short r, short g, short b)
    {
        m_setcmdQueue.add(new SetCmd(SetOp.LED, r, g, b));        
    }
    
    public void SetServos(short s0, short s1)
    {
        m_setcmdQueue.add(new SetCmd(SetOp.Servos, s0, s1, (short) 0));        
    }

    private void update() // invoked via scheduler (in another thread)
    {
        // http://www.javacodex.com/Concurrency/ConcurrentLinkedQueue-Example
        while(!m_setcmdQueue.isEmpty())
        {
            SetCmd c = m_setcmdQueue.poll();
            byte[] bytes = m_sendBuf.array();
            switch(c.op)
            {
            case LED:
                bytes[0] = 0;
                bytes[1] = k_syncLED;
                bytes[2] = (byte) c.a;
                bytes[3] = (byte) c.b;
                bytes[4] = (byte) c.c;
                m_i2c.writeBulk(m_sendBuf, 5);
                break;
            case Brightness:
                bytes[0] = 0;
                bytes[1] = k_syncBrightness;
                bytes[2] = (byte) c.a;
                m_i2c.writeBulk(m_sendBuf, 3);
                break;
            case Servos:
                bytes[0] = 0;
                bytes[1] = k_syncServos;
                bytes[2] = (byte) (c.a & 0xFF); // these are short
                bytes[3] = (byte) ((c.a>>8) & 0xFF); // XXX: endian-ness not validated yet
                bytes[4] = (byte) (c.b & 0xFF);
                bytes[5] = (byte) ((c.b>>8) & 0xFF);
                m_i2c.writeBulk(m_sendBuf, 6);
                break;
            }
        }
       
        if (1 == getBlocks(1)) // for now we only care about the first (largest) block
        {
            m_targetX = m_blocks[0].x;
            m_targetY = m_blocks[0].y;
        }
    }

    // getBlocks is the primary means to obtain target data from PixyCam
    private int getBlocks(int maxBlocks) // returns blocks received.
    {
        int blockCount = 0;
        if (!m_skipStart)
        {
            if (!getStart())
                return blockCount; // error, can't read blocks
        }

        while (blockCount < maxBlocks)
        {
            short checksum = getShort();
            if (checksum == k_startWord)
            {
                m_skipStart = true;
                m_blockType = BlockType.NormalBlock;
                break; // out of while loop
            }
            else if (checksum == k_startWordCC)
            {
                m_skipStart = true;
                m_blockType = BlockType.CCBlock;
                break;
            }
            else if (checksum == 0)
                break;

            // here we read one block
            Block blk = m_blocks[blockCount];
            short w;

            blk.signature = getShort();
            w = blk.signature;
            blk.x = getShort();
            w += blk.x;
            blk.y = getShort();
            w += blk.y;
            blk.width = getShort();
            w += blk.width;
            blk.height = getShort();
            w += blk.height;
            if (m_blockType == BlockType.CCBlock)
            {
                blk.angle = getShort();
                w += blk.angle;
            }
            else
                blk.angle = 0;

            if (w == checksum)
                blockCount++;
            else
            {
                Logger.getAnonymousLogger().warning("PixyCam: checksum error");
            }

            // proceed to next block
            w = getShort();
            if (w == k_startWord)
                m_blockType = BlockType.NormalBlock;
            else if (w == k_startWordCC)
                m_blockType = BlockType.CCBlock;
            else
                break;
        }
        return blockCount;
    }

    private boolean getStart() // returns true on success
    {
        short w, lastw = (short) 0xffff;
        while (true)
        {
            w = getShort();
            if (w == 0 && lastw == 0) // means no data
                return false;
            else if (w == k_startWord && lastw == k_startWord)
            {
                m_blockType = BlockType.NormalBlock;
                return true;
            }
            else if (w == k_startWordCC && lastw == k_startWord)
            {
                m_blockType = BlockType.CCBlock;
                return true;
            }
            else if (w == k_startWordX) // this is important, might be juxtaposed
            {
                getByte(); // we're out of sync!
            }
            lastw = w;
        }
    }

    private byte getByte()
    {
        if (m_i2c.readOnly(m_bytes, 1))
            return 0; // transfer aborted
        else
            return m_bytes[0];
    }

    private short getShort()
    {
        if (m_i2c.readOnly(m_bytes, 2))
            return 0; // transfer aborted
        else
            return (short) ((m_bytes[0] << 8) | m_bytes[1]);
    }

};
