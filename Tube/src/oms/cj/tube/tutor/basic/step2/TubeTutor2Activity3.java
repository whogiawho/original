package oms.cj.tube.tutor.basic.step2;

import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.tutor.basic.step1.TubeTutorBaseActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class TubeTutor2Activity3 extends TubeTutorBaseActivity{

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.step1, AdGlobals.getInstance().getAdInterface());
		setContentView(view);
        
        prepareBrowseButtonOnClickListener(R.id.next, R.id.previous, R.id.toc, this);
        
        setOnClickListener(R.id.step1_1, this);
        setOnClickListener(R.id.step1_2, this);
        setOnClickListener(R.id.step1_3, this);
        setOnClickListener(R.id.step1_4, this);
    }
    
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.next:
			nextSection("oms.cj.tube.tutor.basic.step2.TubeTutor2Activity4");
			break;
		case R.id.step1_1:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step1_1, "2.1");
			break;
		case R.id.step1_2:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step1_2, "2.2");
			break;
		case R.id.step1_3:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step1_3, "2.3");
			break;
		case R.id.step1_4:
			nextSection(TUBETUTORPLAYACTIVITY, R.layout.step1_4, "2.4");
			break;
		default:
			super.onClick(v);
		}
	}
}
