package oms.cj.tube.camera.self;

public interface IRecognition {
	public boolean match(byte[] rawData, Thread worker);
	public int[] getMatchColor();
}
