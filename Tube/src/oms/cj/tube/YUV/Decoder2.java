package oms.cj.tube.YUV;

public class Decoder2 extends Decoder {

	@Override
	public void decode2RGB(int[] bitmapData, byte[] rgbBuf, byte[] yuv420sp,
			int width, int height) {
    	final int sz = width * height;
	    if(bitmapData == null) 
	      	throw new NullPointerException("buffer 'out' is null");
    	if(bitmapData.length < sz) 
    	    throw new IllegalArgumentException("buffer 'out' size " + 
    	    		bitmapData.length + " < minimum " + sz);
    	if(yuv420sp == null) 
    	   	throw new NullPointerException("buffer 'fg' is null");
    	if(yuv420sp.length < sz) 
    	  	throw new IllegalArgumentException("buffer 'fg' size " + 
    	  			yuv420sp.length + " < minimum " + sz * 3/ 2);

    	int Y, Cr = 0, Cb = 0;
    	for(int j = 0, yp = 0; j < height; j++) {
    		int pixPtr = j * width;
    		final int jDiv2 = j >> 1;
    		for(int i = 0; i < width; i++, yp++) {
    			Y = yuv420sp[pixPtr]; if(Y < 0) Y += 255;
    		    if((i & 0x1) != 1) {
    		    	final int cOff = sz + jDiv2 * width + (i >> 1) * 2;
    		        Cb = yuv420sp[cOff];
    		        if(Cb < 0) Cb += 127; else Cb -= 128;
    		        Cr = yuv420sp[cOff + 1];
    		        if(Cr < 0) Cr += 127; else Cr -= 128;
    		    }
    		    int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
    		    if(R < 0) R = 0; else if(R > 255) R = 255;
    		    int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1) + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
    		    if(G < 0) G = 0; else if(G > 255) G = 255;
    		    int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
    		    if(B < 0) B = 0; else if(B > 255) B = 255;
    		    
    		    rgbBuf[yp*3] = (byte) B;
    		    rgbBuf[yp*3+1] = (byte) G;
    		    rgbBuf[yp*3+2] = (byte) R;
    		    bitmapData[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
    		}
    	}
	}
}
