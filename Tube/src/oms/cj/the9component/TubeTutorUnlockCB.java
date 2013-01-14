package oms.cj.the9component;

import android.app.Activity;
import android.widget.Toast;
import com.openfeint.api.resource.Achievement.UnlockCB;

public class TubeTutorUnlockCB extends UnlockCB {

	Activity mAct;
	
	TubeTutorUnlockCB(Activity act){
		mAct = act;
	}
	
	@Override
	public void onSuccess(boolean newUnlock) {	
		String out = String.format("%s.", "Unlocked");
		Toast t = Toast.makeText(mAct, out, Toast.LENGTH_SHORT);
		t.show();
	}
	
	@Override public void onFailure(String exceptionMessage) {
		String out = String.format("Error (%s) unlocking achievement.", exceptionMessage);
		Toast t = Toast.makeText(mAct, out, Toast.LENGTH_SHORT);
		t.show();
	}	

}
