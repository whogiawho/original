package oms.cj.the9component;

import oms.cj.tube.R;
import android.app.Activity;
import android.widget.Toast;

import com.openfeint.api.resource.Score;

public class TubeSubmitToCB extends Score.SubmitToCB {
	private Activity mAct;
	private Score mScore;
	
	public TubeSubmitToCB(Activity act, Score score){
		mAct = act;
		mScore = score;
	}
	private final void finishUp(int resultCode) {
		// sweet, pop the thingerydingery
		mAct.setResult(resultCode);
		mAct.finish();
	}
	
	@Override public void onSuccess(boolean newHighScore) {
		Toast.makeText(mAct, mAct.getString(R.string.ScorePosted), Toast.LENGTH_SHORT).show();
		if (mScore.blob == null) finishUp(Activity.RESULT_OK);
	}

	@Override public void onFailure(String exceptionMessage) {
		String out = mAct.getString(R.string.fmtErrScorePost, exceptionMessage);
		Toast.makeText(mAct, out, Toast.LENGTH_SHORT).show();
		finishUp(Activity.RESULT_CANCELED);
	}
	
	@Override public void onBlobUploadSuccess() {
		Toast.makeText(mAct, mAct.getString(R.string.BlobUploaded), Toast.LENGTH_SHORT).show();
		finishUp(Activity.RESULT_OK);
	}
	
	@Override public void onBlobUploadFailure(String exceptionMessage) {
		String out = mAct.getString(R.string.fmtErrBlobUpload, exceptionMessage);
		Toast.makeText(mAct, out, Toast.LENGTH_SHORT).show();
		finishUp(Activity.RESULT_CANCELED);
	}
}
