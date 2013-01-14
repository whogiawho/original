package oms.cj.tube.colorsetter;

import oms.cj.tube.component.Tube;

public interface IComplete {
	public void onCompleteClicked(Tube tube);
	public void onCameraClicked(Tube tube);
	public void onSaveClicked(Tube tube);
	public void onOpenClicked(Tube tube);
}
