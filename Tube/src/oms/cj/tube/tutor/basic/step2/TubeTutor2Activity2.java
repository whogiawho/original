package oms.cj.tube.tutor.basic.step2;

import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.tutor.basic.step1.TubeTutorBaseActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class TubeTutor2Activity2 extends TubeTutorBaseActivity{

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.object1, AdGlobals.getInstance().getAdInterface());
		
		setContentView(view);
        
        prepareBrowseButtonOnClickListener(R.id.next, R.id.previous, R.id.toc, this);
        setButtonText(R.id.previous, getResources().getString(R.string.End));
    }
     
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.next:
			nextSection("oms.cj.tube.tutor.basic.step2.TubeTutor2Activity3");
			break;
		default:
			super.onClick(v);
		}
	}
}
