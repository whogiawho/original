package oms.cj.tube.tutor.basic.step1;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import oms.cj.tube.R;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import oms.cj.tube.tutor.TutorPlayer;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class TubeTutor1Activity1 extends TubeTutorBaseActivity {
	private final static String TAG = "TubeTutorActivity1";
	public final static String fileDemo = "Demo";
	public final static int RESULT_COMPLETE = Activity.RESULT_FIRST_USER;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG+".onCreate", "being called!");

		Tube tube = new Tube(Tube.DefaultHalfLength);
		tube.setAnimationLength(Tube.AnimationDefaultLength);
		tube.setRandomRotateN(20);
		tube.setSize(Tube.DefaultSize);
		
		Stack<RotateAction> actions1 = new Stack<RotateAction>();
		tube.randomize(actions1);
		Stack<RotateAction> actions2 = new Stack<RotateAction>();
		for(;actions1.size()>0;){
			RotateAction r = actions1.pop();
			actions2.push(new RotateAction(r.getLayer(), Tube.reverseDir(r.getDir())));
		}
        initDemoFile(actions2);
        
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutormain);

        prepareBrowseButtonOnClickListener(R.id.next, R.id.previous, R.id.toc, this);
        setButtonText(R.id.previous, getResources().getString(R.string.End));
		
        TutorPlayer tutorPlayer = (TutorPlayer) findViewById(R.id.demo);
        tutorPlayer.setColor(tube);
        setTubePlayer(tutorPlayer);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    @Override
    public void onDestroy(){
    	Log.i(TAG+".onDestory", "being called!");
    	removeDemoFile();
    	super.onDestroy();
    }
    
    private void removeDemoFile(){
		String[] cubesArray = this.fileList();
		List<String> currentFilesList = Arrays.asList(cubesArray);
		
		if(currentFilesList.contains(fileDemo)){
			this.deleteFile(fileDemo);
		}
    }
    
    private void initDemoFile(Stack<RotateAction> actions){
		String[] cubesArray = this.fileList();
		List<String> currentFilesList = Arrays.asList(cubesArray);
	
		if(!currentFilesList.contains(fileDemo)){
			try {
				//create the file
				FileOutputStream fos;
				fos = this.openFileOutput(fileDemo, Context.MODE_PRIVATE);
				fos.close();
				//save origin cube to the file
				saveToFile(fileDemo, this, actions);
			} catch (FileNotFoundException e) {
				Log.e(TAG+".initDemoFile", "FileNotFoundException");
			} catch (IOException e) {
				Log.e(TAG+".initDemoFile", "IOException");				}
		}
    }
    
    private void saveToFile(String strFileName, Activity act, Stack<RotateAction> actions) throws FileNotFoundException, IOException{
        FileOutputStream fos = act.openFileOutput(strFileName, Context.MODE_PRIVATE);
        ObjectOutputStream objectOut = new ObjectOutputStream(new BufferedOutputStream(fos));

        objectOut.writeObject(actions);
        objectOut.close();
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch(id){
		case R.id.next:
			nextSection("oms.cj.tube.tutor.basic.step1.TubeTutor1Activity2");
			break;
		default:
			super.onClick(v);
			break;
		}
	}
}