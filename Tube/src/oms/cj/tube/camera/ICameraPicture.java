package oms.cj.tube.camera;

public interface ICameraPicture {
	public final static int INVALID = -1;
	public final static int TYPE0 = 0;   	//ex|way1(6 times)
	public final static int TYPE1 = 1;		//ex|way2(2 times)
	public final static int TYPE2 = 2;		//self|way1(6 times)
	public final static int TYPE3 = 3;		//self|way2(2 times)
	public void onPictureTaken(byte[] yuvData, byte[] rgbData, int width, int height);
	public int getWayType();
}
