package oms.cj.tube.camera.self.way1;

import oms.cj.tube.camera.self.IRecognition;

public class SideRecognition implements IRecognition{

	@Override
	public boolean match(byte[] rawData, Thread worker) {
		
		return false;
	}

	@Override
	public int[] getMatchColor() {
		return null;
	}
}
