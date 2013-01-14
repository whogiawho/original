package oms.cj.tube.tutor.basic.step6;


import com.openfeint.api.resource.Achievement;
import oms.cj.ads.AdGlobals;
import oms.cj.the9component.TubeTutorLoadCB;
import oms.cj.tube.ITubeRenderCallbacks;
import oms.cj.tube.PlayRenderer;
import oms.cj.tube.R;
import oms.cj.tube.TubeApplication;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import oms.cj.tube.tutor.TutorQuizView;
import oms.cj.tube.tutor.basic.step1.TubeTutorBaseActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class TubeTutor6Activity4 extends TubeTutorBaseActivity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.practise5, AdGlobals.getInstance().getAdInterface());
		setContentView(view);
  
        prepareBrowseButtonOnClickListener(R.id.next, R.id.previous, R.id.toc, this);
        prepareESButtonOnClickListener(R.id.grow, R.id.shrink, this);
        setButtonText(R.id.next, getResources().getString(R.string.End));
        
        TutorQuizView vQuiz = (TutorQuizView) findViewById(R.id.practiseview);
        if(AdGlobals.getInstance().the9Switch){
            ZAN_YUN_JIAN_Callback cb = new ZAN_YUN_JIAN_Callback(vQuiz); 
            vQuiz.setCallbacks(cb);      	
        }

    }
    
	public void onClick(View v) {
        TutorQuizView vQuiz = (TutorQuizView) findViewById(R.id.practiseview);
        PlayRenderer _renderer = vQuiz.getRenderer();
		int size = _renderer.getTube().getSize();

		int id = v.getId();
		switch(id){
		case R.id.grow:
			if(size<Tube.MaxSize&&size>=Tube.MinSize)
				size++;
			_renderer.getTube().setSize(size);
			break;
		case R.id.shrink:
			if(size<=Tube.MaxSize&&size>Tube.MinSize)
				size--;
			_renderer.getTube().setSize(size);
			break;
		
		case R.id.next:
			nextSection(null);
			break;
		default:
			super.onClick(v);
		}
	}
	
	@Override
	public void nextSection(String activityName) {
		setResult(TubeTutorBaseActivity.RESULT_COMPLETE);
		finish();
	}
	
	public final static String TAG = "TubeTutor3Activity3";
	class ZAN_YUN_JIAN_Callback implements ITubeRenderCallbacks {
		TutorQuizView mVQuiz;
		
		ZAN_YUN_JIAN_Callback(TutorQuizView vQuiz){
			mVQuiz = vQuiz;
		}

		private boolean isInPlace(Tube t){
			boolean bInPlace = isTutorStep6InPlace(t);
			
			return bInPlace;
		}
		@Override
		public void onRotateFinish(RotateAction r) {
			Log.i(TAG+".ZAN_YUN_JIAN_Callback.onRotateFinish", "being called!");
	        Tube t = mVQuiz.getRenderer().getTube();
	        
	        boolean bInPlace = isInPlace(t);
	        
	        if(bInPlace){
	        	Log.i(TAG+".ZAN_YUN_JIAN_Callback.onRotateFinish", "pls unlock ZAN_YUN_JIAN!");
				final Achievement a = new Achievement(TubeApplication.cjAchievements[TubeApplication.ZAN_YUN_JIAN]);
				Achievement.LoadCB cb = new TubeTutorLoadCB(a, TubeTutor6Activity4.this);
				a.load(cb);	        	
	        }
		}

		@Override
		public void onRotateStart(RotateAction r) {
		}	
	}	
}
