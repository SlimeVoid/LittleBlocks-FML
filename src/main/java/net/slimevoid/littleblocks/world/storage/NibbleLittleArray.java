package net.slimevoid.littleblocks.world.storage;


/**
 * Created by Greg on 30/01/15.
 */
public class NibbleLittleArray {

    private final byte[] data;

    public NibbleLittleArray()
{
    this.data = new byte[2048];
}

    public NibbleLittleArray(byte[] storageArray)
    {
        this.data = storageArray;

        if (storageArray.length != 2048)
        {
            throw new IllegalArgumentException("ChunkLittleNibbleArrays should be 1024 bytes not: " + storageArray.length);
        }
    }

    /**
     * Returns the nibble of data corresponding to the passed in x, y, z. y is at most 6 bits, z is at most 4.
     */
    public int get(int x, int y, int z)
    {
        return this.getFromIndex(this.getCoordinateIndex(x, y, z));
    }

    /**
     * Arguments are x, y, z, val. Sets the nibble of data at x << 11 | z << 7 | y to val.
     */
    public void set(int x, int y, int z, int value)
    {
        this.setIndex(this.getCoordinateIndex(x, y, z), value);
    }

    private int getCoordinateIndex(int x, int y, int z)
    {
        return y << 8 | z << 4 | x;
    }

    public int getFromIndex(int index)
    {
        int j = this.func_177478_c(index);
        return this.func_177479_b(index) ? this.data[j] & 7 : this.data[j] >> 4 & 15;
    }

    public void setIndex(int index, int value)
    {
        int k = this.func_177478_c(index);

        if (this.func_177479_b(index))
        {
            this.data[k] = (byte)(this.data[k] & 240 | value & 15);
        }
        else
        {
            this.data[k] = (byte)(this.data[k] & 15 | (value & 15) << 4);
        }
    }

    private boolean func_177479_b(int p_177479_1_)
    {
        return (p_177479_1_ & 1) == 0;
    }

    private int func_177478_c(int p_177478_1_)
    {
        return p_177478_1_ >> 1;
    }

    public byte[] getData()
    {
        return this.data;
    }
}
