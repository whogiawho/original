package oms.cj.tube.tutor.basic.step1;

import com.openfeint.api.resource.Achievement;
import oms.cj.ads.AdGlobals;
import oms.cj.the9component.TubeTutorLoadCB;
import oms.cj.tube.R;
import oms.cj.tube.TubeApplication;
import oms.cj.tube.tutor.basic.step1.TubeTutorBaseActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class TubeTutor1Activity4 extends TubeTutorBaseActivity{
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.symbolrep, AdGlobals.getInstance().getAdInterface());
		setContentView(view);
        
        prepareBrowseButtonOnClickListener(R.id.next, R.id.previous, R.id.toc, this);
        
        setButtonText(R.id.next, getResources().getString(R.string.End));
        
        setOnClickListener(R.id.cw1R90, this);
        setOnClickListener(R.id.ccw1R90, this);
        setOnClickListener(R.id.cw1R180, this);
        setOnClickListener(R.id.cw2R90, this);
        setOnClickListener(R.id.cw3R90, this);
    }

    @Override
	public void onClick(View v) {
    	
		int id = v.getId();
		switch(id){
		case R.id.next:
			if(AdGlobals.getInstance().the9Switch){
				//check if cjAchievements[0] is got here
				final Achievement a = new Achievement(TubeApplication.cjAchievements[TubeApplication.DOU_MU_GONG]);
				Achievement.LoadCB cb = new TubeTutorLoadCB(a, this);
				a.load(cb);				
			}
			
			nextSection(null);
			break;
		case R.id.cw1R90:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.symbolrep1, "1.1");
			break;
		case R.id.ccw1R90:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.symbolrep2, "1.2");
			break;
		case R.id.cw1R180:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.symbolrep3, "1.3");
			break;
		case R.id.cw3R90:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.symbolrep4, "1.4");
			break;
		case R.id.cw2R90:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.symbolrep5, "1.5");
			break;
		default:
			super.onClick(v);
			break;
		}
	}

	@Override
	public void nextSection(String activityName) {
		setResult(TubeTutorBaseActivity.RESULT_COMPLETE);
		finish();
	}
}