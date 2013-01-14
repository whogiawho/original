package oms.cj.tube.tutor.basic.step7;


import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.tutor.basic.step1.TubeTutorBaseActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class TubeTutor7Activity3 extends TubeTutorBaseActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.solutiondetails6, AdGlobals.getInstance().getAdInterface());
		setContentView(view);
        
        prepareBrowseButtonOnClickListener(R.id.next, R.id.previous, R.id.toc, this);
        
        setOnClickListener(R.id.step6_a, this);
        setOnClickListener(R.id.step6_b, this);
        setOnClickListener(R.id.step6_c, this);
        setOnClickListener(R.id.step6_d, this);
        setOnClickListener(R.id.step6_e, this);
        setOnClickListener(R.id.step6_f, this);
        setOnClickListener(R.id.step6_g, this);
    }
    
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.next:
			nextSection("oms.cj.tube.tutor.basic.step7.TubeTutor7Activity4");
			break;
		case R.id.step6_a:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step6_1, "7.1");
			break;
		case R.id.step6_b:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step6_2, "7.2");
			break;
		case R.id.step6_c:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step6_3, "7.3");
			break;
		case R.id.step6_d:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step6_4, "7.4");
			break;
		case R.id.step6_e:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step6_5, "7.5");
			break;
		case R.id.step6_f:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step6_6, "7.6");
			break;
		case R.id.step6_g:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step6_7, "7.7");
			break;
		default:
			super.onClick(v);
		}
	}
}
