package oms.cj.tube.tutor.basic.step9;


import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.tutor.basic.step1.TubeTutorBaseActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class TubeTutor9Activity3 extends TubeTutorBaseActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.solutiondetails8, AdGlobals.getInstance().getAdInterface());
		setContentView(view);
        
        prepareBrowseButtonOnClickListener(R.id.next, R.id.previous, R.id.toc, this);
        
        setOnClickListener(R.id.step8_a, this);
        setOnClickListener(R.id.step8_b, this);
        setOnClickListener(R.id.step8_c, this);
        setOnClickListener(R.id.step8_d, this);
    }
    
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.next:
			nextSection("oms.cj.tube.tutor.basic.step9.TubeTutor9Activity4");
			break;
		case R.id.step8_a:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step8_1, "9.1");
			break;
		case R.id.step8_b:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step8_2, "9.2");
			break;
		case R.id.step8_c:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step8_3, "9.3");
			break;
		case R.id.step8_d:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step8_4, "9.4");
			break;
		default:
			super.onClick(v);
		}
	}
}
