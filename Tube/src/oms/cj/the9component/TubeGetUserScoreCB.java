package oms.cj.the9component;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.openfeint.api.resource.Leaderboard;
import com.openfeint.api.resource.Score;

public class TubeGetUserScoreCB extends Leaderboard.GetUserScoreCB{
	public final static String TAG = "TubeGetUserScoreCB";
	
	private Activity mAct;
	private int mScore;
	private String mStrScore;
	private String mLeaderBoardID;
	private IThe9 mIThe9;
	
	public TubeGetUserScoreCB(Activity act, IThe9 i, String id, int score, String s){
		mAct = act;
		mScore = score;
		mStrScore = s;
		mLeaderBoardID = id;
		mIThe9 = i;
	}
	
	@Override
	public void onSuccess(Score score) {
		if(score==null){
			Log.i(TAG+".TubeGetUserScoreCB.onSuccess", "scores==null!");
			mIThe9.submitScoreToThe9(mLeaderBoardID, mScore, mStrScore);
		} else {
			Log.i(TAG+".TubeGetUserScoreCB.onSuccess", "score="+score.score);
			if(score.score>mScore)
				mIThe9.submitScoreToThe9(mLeaderBoardID, mScore, mStrScore);
		}
	}
	
	@Override
	public void onFailure(String exceptionMessage) {
		String out = String.format("Error (%s) reading score.", exceptionMessage);
		Toast t = Toast.makeText(mAct, out, Toast.LENGTH_SHORT);
		t.show();
	}
}