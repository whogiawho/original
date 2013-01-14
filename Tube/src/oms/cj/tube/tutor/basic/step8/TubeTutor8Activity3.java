package oms.cj.tube.tutor.basic.step8;


import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.tutor.basic.step1.TubeTutorBaseActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class TubeTutor8Activity3 extends TubeTutorBaseActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.solutiondetails7, AdGlobals.getInstance().getAdInterface());
		setContentView(view);
        
        prepareBrowseButtonOnClickListener(R.id.next, R.id.previous, R.id.toc, this);
        
        setOnClickListener(R.id.step7_a, this);
        setOnClickListener(R.id.step7_b, this);
    }
    
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.next:
			nextSection("oms.cj.tube.tutor.basic.step8.TubeTutor8Activity4");
			break;
		case R.id.step7_a:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step7_1, "8.1");
			break;
		case R.id.step7_b:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step7_2, "8.2");
			break;
		default:
			super.onClick(v);
		}
	}
}
