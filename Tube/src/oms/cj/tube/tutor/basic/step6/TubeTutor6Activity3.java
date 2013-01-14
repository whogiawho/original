package oms.cj.tube.tutor.basic.step6;


import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.tutor.basic.step1.TubeTutorBaseActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class TubeTutor6Activity3 extends TubeTutorBaseActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.solutiondetails5, AdGlobals.getInstance().getAdInterface());
		setContentView(view);
        
        prepareBrowseButtonOnClickListener(R.id.next, R.id.previous, R.id.toc, this);
        
        setOnClickListener(R.id.step5_a, this);
        setOnClickListener(R.id.step5_b, this);
        setOnClickListener(R.id.step5_c, this);
    }
    
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.next:
			nextSection("oms.cj.tube.tutor.basic.step6.TubeTutor6Activity4");
			break;
		case R.id.step5_a:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step5_1, "6.1");
			break;
		case R.id.step5_b:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step5_2, "6.2");
			break;
		case R.id.step5_c:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step5_3, "6.3");
			break;
		default:
			super.onClick(v);
		}
	}
}
