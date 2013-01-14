package oms.cj.tube.YUV;

public abstract class Decoder {
    public abstract void decode2RGB(int[] bitmapData, byte[] rgbBuf, byte[] yuv420sp, int width, int height);
}
