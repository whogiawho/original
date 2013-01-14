package oms.cj.tube.tutor;

import android.app.Activity;
import oms.cj.tube.ITubeRenderCallbacks;
import oms.cj.tube.PlayRenderer;

public class TutorQuizRenderer extends PlayRenderer{

    public TutorQuizRenderer( String fileName, boolean randomized, 
    		int IDSwitch, Activity act){
    	super(fileName, randomized, IDSwitch, act);
    }

    public void setCallbacks(ITubeRenderCallbacks cb){
    	getTube().setCallbacks(cb);
    }
}
