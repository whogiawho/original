package oms.cj.tube.tutor.basic.step5;


import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.tutor.basic.step1.TubeTutorBaseActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class TubeTutor5Activity3 extends TubeTutorBaseActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.solutiondetails4, AdGlobals.getInstance().getAdInterface());
		setContentView(view);
        
        prepareBrowseButtonOnClickListener(R.id.next, R.id.previous, R.id.toc, this);
        
        setOnClickListener(R.id.step4_a, this);
        setOnClickListener(R.id.step4_b, this);
        setOnClickListener(R.id.step4_c, this);
    }
    
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.next:
			nextSection("oms.cj.tube.tutor.basic.step5.TubeTutor5Activity4");
			break;
		case R.id.step4_a:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step4_1, "5.1");
			break;
		case R.id.step4_b:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step4_2, "5.2");
			break;
		case R.id.step4_c:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step4_3, "5.3");
			break;
		default:
			super.onClick(v);
		}
	}
}
