package oms.cj.tube.tutor;

import oms.cj.ads.AdGlobals;
import oms.cj.ads.IHackedActions;
import oms.cj.ads.WapsNode;
import oms.cj.tube.R;
import oms.cj.widget.TutorAdapter;

import com.waps.AppConnect;
import com.wooboo.adlib_android.ImpressionAdView;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MoFangMiJiActivity extends ListActivity {
	private final static String TAG = "MoFangMiJiActivity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
		ListAdapter adapter = new TutorAdapter(this, mChaptersID, R.layout.toc, iconsID);
        setListAdapter(adapter);
        getListView().setTextFilterEnabled(true);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        AdGlobals.getInstance().displayWoobooDynamicAd(getListView(), this);
    }
    
    private int[] iconsID = {
    	R.drawable.qqcontact,
    	R.drawable.roadmap,
    	R.drawable.chapter2,
    	R.drawable.chapter3,
    	R.drawable.chapter4,
    	R.drawable.chapter5,
    	R.drawable.chapter6,
    	R.drawable.chapter7,
    	R.drawable.chapter8,
    	R.drawable.chapter9,
    };
    
    private int[] mChaptersID = {
    	R.string.feedback,
    	R.string.chapter1,
    	R.string.chapter2,
    	R.string.chapter3,
    	R.string.chapter4,
    	R.string.chapter5,
    	R.string.chapter6,
    	R.string.chapter7,
    	R.string.chapter8,
    	R.string.chapter9,
    };
    
    private String[] mActivityName = {
    		null,
    		"oms.cj.tube.tutor.basic.step1.TubeTutor1Activity1",
    		"oms.cj.tube.tutor.basic.step2.TubeTutor2Activity2",
       		"oms.cj.tube.tutor.basic.step3.TubeTutor3Activity1",
       		"oms.cj.tube.tutor.basic.step4.TubeTutor4Activity1",
       		"oms.cj.tube.tutor.basic.step5.TubeTutor5Activity1",
       		"oms.cj.tube.tutor.basic.step6.TubeTutor6Activity1",
       		"oms.cj.tube.tutor.basic.step7.TubeTutor7Activity1",
       		"oms.cj.tube.tutor.basic.step8.TubeTutor8Activity1",
       		"oms.cj.tube.tutor.basic.step9.TubeTutor9Activity1",
    };
    
    @Override
    protected void onListItemClick(ListView l, View v, final int position, long id) 
    {    
    	String chapter = (String) l.getItemAtPosition(position);
    	Log.i(TAG+".onListItemClick", "chapter=" + chapter);
    	
    	if(position>=6){
			if(AdGlobals.getInstance().wapsAdSwitch){
				//define hacked actions here
				IHackedActions action = new IHackedActions(){
					@Override
					public void playAfterPassCheck() {
						startActivity(position);
					}
				};
				//pass node to WapsNode so that it can be called by getPoints's callback function
				WapsNode node = new WapsNode(this, action);
				if(!node.checkQualificationToContinue()){
					Log.i(TAG+".onClick", "calling AppConnect.getInstance(this).getPoints(node)!");
					AppConnect.getInstance(this).getPoints(node);
					return;
				}
			}
    	}
    	
    	startActivity(position);
    }
    
    private void startActivity(int position){
    	String activityToStart = mActivityName[position];
    	if(activityToStart!=null){
    		Intent intent=new Intent();
    		intent.setClassName(this, activityToStart); 
    		startActivity(intent);
    	}
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭 渐入式 广告
		ImpressionAdView.close();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
	}
	
	@Override
	protected void onResume(){
		super.onPause();
	}
}