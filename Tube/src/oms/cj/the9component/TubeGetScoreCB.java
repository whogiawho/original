package oms.cj.the9component;

import java.util.List;
import oms.cj.tube.R;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.openfeint.api.resource.Leaderboard;
import com.openfeint.api.resource.Score;

public class TubeGetScoreCB extends Leaderboard.GetScoresCB{
	private final static String TAG = "TubeGetScoreCB";
	
	private Activity mAct;
	private int mScore;
	private String mStrScore;
	private String mLeaderBoardID;
	private IThe9 mIThe9;
	
	public TubeGetScoreCB(Activity act, IThe9 i, String id, int score, String s){
		mAct = act;
		mScore = score;
		mStrScore = s;
		mLeaderBoardID = id;
		mIThe9 = i;
	}
	
	@Override
	public void onSuccess(List<Score> scores) {
		Log.i(TAG+"onSuccess", "being called!");
		
		if(scores==null){	//the first time to submit
			Log.i(TAG+".onSuccess", "scores==null!");
			mIThe9.submitScoreToThe9(mLeaderBoardID, mScore, mStrScore);
		} else if(scores!=null && scores.size()!=0){
			Score s1 = scores.get(0);
			Log.i(TAG+".onSuccess", "s1="+s1.score);
			if(s1.score>mScore)
				mIThe9.submitScoreToThe9(mLeaderBoardID, mScore, mStrScore);
		}
	}
	
	@Override
	public void onFailure(String exceptionMessage) {
		String out = String.format(mAct.getString(R.string.fmtReadingScore), exceptionMessage);
		Toast t = Toast.makeText(mAct, out, Toast.LENGTH_SHORT);
		t.show();
	}
}
