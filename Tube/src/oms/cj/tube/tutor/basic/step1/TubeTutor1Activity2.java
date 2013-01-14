package oms.cj.tube.tutor.basic.step1;

import java.util.Arrays;
import java.util.List;
import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class TubeTutor1Activity2 extends TubeTutorBaseActivity {
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.roadmap, AdGlobals.getInstance().getAdInterface());
		setContentView(view);
        
		prepareBrowseButtonOnClickListener(R.id.next, R.id.previous, R.id.toc, this);
		
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		switch(id){
		case R.id.next:
			removeDemoFile();
			nextSection("oms.cj.tube.tutor.basic.step1.TubeTutor1Activity3");
			break;
		case R.id.toc:
			removeDemoFile();
			super.onClick(v);
			break;
		default:
			super.onClick(v);
			break;
		}
	}
	
    private void removeDemoFile(){
		String[] cubesArray = this.fileList();
		List<String> currentFilesList = Arrays.asList(cubesArray);
		
		if(currentFilesList.contains(TubeTutor1Activity1.fileDemo)){
			this.deleteFile(TubeTutor1Activity1.fileDemo);
		}
    }
}
