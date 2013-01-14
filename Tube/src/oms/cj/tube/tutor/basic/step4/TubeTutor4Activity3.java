package oms.cj.tube.tutor.basic.step4;


import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.tutor.basic.step1.TubeTutorBaseActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class TubeTutor4Activity3 extends TubeTutorBaseActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.solutiondetails3, AdGlobals.getInstance().getAdInterface());
		setContentView(view);
        
        prepareBrowseButtonOnClickListener(R.id.next, R.id.previous, R.id.toc, this);
        
        setOnClickListener(R.id.step3_a, this);
        setOnClickListener(R.id.step3_b, this);
        setOnClickListener(R.id.step3_c, this);
        setOnClickListener(R.id.step3_d, this);
        setOnClickListener(R.id.step3_e, this);
        setOnClickListener(R.id.step3_f, this);
    }
    
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.next:
			nextSection("oms.cj.tube.tutor.basic.step4.TubeTutor4Activity4");
			break;
		case R.id.step3_a:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step3_1, "4.1");
			break;
		case R.id.step3_b:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step3_2, "4.2");
			break;
		case R.id.step3_c:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step3_3, "4.3");
			break;
		case R.id.step3_d:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step3_4, "4.4");
			break;
		case R.id.step3_e:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step3_5, "4.5");
			break;
		case R.id.step3_f:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step3_6, "4.6");
			break;
		default:
			super.onClick(v);
		}
	}
}
