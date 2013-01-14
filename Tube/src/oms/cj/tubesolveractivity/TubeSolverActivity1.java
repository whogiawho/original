package oms.cj.tubesolveractivity;

import com.wooboo.adlib_android.ImpressionAdView;
import java.util.Stack;
import oms.cj.ads.AdGlobals;
import oms.cj.tube.ChooseFileActivity;
import oms.cj.tube.Config;
import oms.cj.tube.R;
import oms.cj.tube.colorsetter.IComplete;
import oms.cj.tube.colorsetter.TubeColorSetter;
import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.Quaternion;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import oms.cj.tube.solver.SolverView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class TubeSolverActivity1 extends Activity implements IComplete{
	private final static String TAG = "TubeSolverActivity1";
	private final static int REQUESTOFCAMERA = 0;
	private final static int REQUESTOFSAVE = 1;
	private final static int REQUESTOFOPEN = 2;
	
	private TubeColorSetter mColorSetter; 
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
        setContentView(R.layout.tubesolveractivity1);
  
        mColorSetter = (TubeColorSetter) findViewById(R.id.tubecolorsetter);
        mColorSetter.setOnCompleteListener(this);
		
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // load Defined Colors to Colors.definedCubeColors
        SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, 0);
        for(int i=0;i<6;i++){
        	int color = settings.getInt(Config.ref[Config.colorOffsetInRef+i], Color.standardCubeColors[i].toInt());
        	Color.setDefinedCubeColors(i, color);
        }
        
        AdGlobals.getInstance().adDynamicFlow(mColorSetter, this, Gravity.BOTTOM|Gravity.LEFT);        
    }
	
	@Override
	public void onPause(){
		super.onPause();
		
		if(mColorSetter!=null)
			mColorSetter.onPause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		if(mColorSetter!=null)
			mColorSetter.onResume();
	}
	
	@Override
	public void onCompleteClicked(Tube tube) {
        Intent intent=new Intent();

        int[] colors = new int[Tube.CubesEachTube*Cube.FacesEachCube];
        tube.getColor(colors);
        for(int i=0;i<colors.length;i++){
        	Log.i(TAG+".onCompleteClicked", "i color = " + colors[i]);
        }
        Bundle bundle = new Bundle();
        bundle.putIntArray("tubecolors", colors);
        intent.putExtras(bundle);
        
        intent.setClassName(this, "oms.cj.tubesolveractivity.TubeSolverActivity2"); 
        startActivity(intent);
	}
	
	@Override
	public void onCameraClicked(Tube tube) {
        Intent intent=new Intent();
        
        // load ways to take picture
        SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, 0);
        int way = settings.getInt(Config.ref[10], R.id.cameraWay1);
        switch(way){
        case R.id.cameraWay1:
        default:
        	intent.setClassName(this, "oms.cj.tube.camera.ex.way1.Snapshot");
        	break;
        case R.id.cameraWay2:
            intent.setClassName(this, "oms.cj.tube.camera.ex.way2.Snapshot"); 
        	break;
        case R.id.cameraWay3:
        	intent.setClassName(this, "oms.cj.tube.camera.self.way1.Snapshot"); 
        	break;
        case R.id.cameraWay4:
        	intent.setClassName(this, "oms.cj.tube.camera.self.way2.Snapshot"); 
        	break;
        }
        startActivityForResult(intent, REQUESTOFCAMERA);
	}
	
	private Tube tubeForSaveAndOpen=null; //temporary variables for serialize tube to a selected file
	@Override
	public void onSaveClicked(Tube tube) {
    	Intent intent=new Intent();
    	intent.setClass(this, ChooseFileActivity.class);
    	intent.putExtra(ChooseFileActivity.REQUESTCODE, ChooseFileActivity.CHOOSE_FILE_TO_SAVE);
		startActivityForResult(intent, REQUESTOFSAVE);
		tubeForSaveAndOpen = tube;
	}
	
	protected void onActivityResult(final int requestCode, int resultCode, Intent data){
		if(requestCode==REQUESTOFCAMERA){
			Tube t = new Tube(Tube.DefaultHalfLength);
			
			if(resultCode==RESULT_OK){
				
				//set Tube's color from data
				Bundle b = data.getExtras();
				int[] colors = b.getIntArray(oms.cj.tube.camera.Snapshot.TUBECOLORS);
				for(int i=0;i<Tube.SidesEachTube;i++){
					for(int j=0;j<Tube.CubesEachSide;j++){
						int idx = i*Tube.CubesEachSide + j;
						Color c = new Color(colors[idx]);
						t.setVisibleFaceColor(i, j, c);
					}
				}
			}
			mColorSetter.setCubesColor(t);
			return;
		}

		if(requestCode==REQUESTOFSAVE){
			if(resultCode==RESULT_OK){
				final String fileName = data.getStringExtra(ChooseFileActivity.FILE2SERIALIZE);
				Log.i(TAG+".onActivityResult", tubeForSaveAndOpen.toString());
				Color[] faceColor = new Color[Tube.SidesEachTube];
				Stack<RotateAction> commands = SolverView._basicSolve(tubeForSaveAndOpen, faceColor);
				commands = reverse(commands);
				Quaternion q = mColorSetter.getCurrentQuaternion();
				mColorSetter.saveToFile(this, fileName, commands, q, faceColor);
			}
			return;
		}
		
		if(requestCode==REQUESTOFOPEN){
			if(resultCode==RESULT_OK){
				final String fileName = data.getStringExtra(ChooseFileActivity.FILE2SERIALIZE);
				Log.i(TAG+".onActivityResult", tubeForSaveAndOpen.toString());
				mColorSetter.restoreFromFile(this, fileName);
			}
			return;
		}
	}
	
	private Stack<RotateAction> reverse(Stack<RotateAction> commands){
		Stack<RotateAction> returnCommands = new Stack<RotateAction>();
		
		for(int i=0;i<commands.size();i++){
			RotateAction r = commands.get(commands.size()-1-i);
			returnCommands.push(r.reverse());
		}
		return returnCommands;
	}

	@Override
	public void onOpenClicked(Tube tube) {
    	Intent intent=new Intent();
    	intent.setClass(this, ChooseFileActivity.class);
    	intent.putExtra(ChooseFileActivity.REQUESTCODE, ChooseFileActivity.CHOOSE_FILE_TO_OPEN);
		startActivityForResult(intent, REQUESTOFOPEN);
		tubeForSaveAndOpen = tube;
	}
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭 渐入式 广告
		ImpressionAdView.close();
	}
}
