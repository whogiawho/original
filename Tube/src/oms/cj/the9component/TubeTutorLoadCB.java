package oms.cj.the9component;

import oms.cj.tube.R;
import android.app.Activity;
import android.widget.Toast;
import com.openfeint.api.resource.Achievement;
import com.openfeint.api.resource.Achievement.LoadCB;

public class TubeTutorLoadCB extends LoadCB {

	private Achievement mA;
	private Activity mAct;
	
	public TubeTutorLoadCB(Achievement a, Activity act){
		mA = a;
		mAct = act;
	}
	
	@Override
	public void onSuccess() {
		if(!mA.isUnlocked){
			TubeTutorUnlockCB cb = new TubeTutorUnlockCB(mAct);
			mA.unlock(cb);
		} else {
			String out = String.format("%s", mAct.getString(R.string.AchievementReachedAlready));
			Toast t = Toast.makeText(mAct, out, Toast.LENGTH_SHORT);
			t.show();			
		}
	}
	
	@Override public void onFailure(String exceptionMessage) {
		String out = String.format("Error (%s).", exceptionMessage);
		Toast t = Toast.makeText(mAct, out, Toast.LENGTH_SHORT);
		t.show();
	}
}
