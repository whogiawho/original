package oms.cj.tube;

import oms.cj.tube.component.RotateAction;

public interface ITubeRenderCallbacks {
	//onRotateStart is called when a request is removed from Tube's animation queue
	public void onRotateStart(RotateAction r);
	
	//1. onRotateFinish is called when Tube completes it rotation, at which time mode = ANIMATIONON
	//2. calling mTube.enqueueRotateRequest() in onRotateFinish() does not guarantee onRotateStart()
	//   is being called rightly in onRotateFinish(); instead onRotateStart() may be called outside 
	//   onRotateFinish().
	public void onRotateFinish(RotateAction r);
}
