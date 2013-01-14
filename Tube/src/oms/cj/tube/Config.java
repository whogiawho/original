package oms.cj.tube;

import java.util.HashMap;
import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.camera.DefineColor;
import oms.cj.tube.component.Color;
import oms.cj.tube.component.Tube;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

public class Config extends Activity implements View.OnClickListener{
	private final static String TAG = "Config";

//	LinearLayout mconfig;
	public static final String PREFS_NAME = "MagiccubePrefs";
    private Button mbsaveref, mbcancelref;
    public final static String[] ref={
    	"speed",
    	"randomRotateN",
    	"size",
    	"faceidswitch",
    	"yellow",
    	"white",
    	"red",
    	"orange",
    	"blue",
    	"green",
    	"cameraWay",
    	"solveWay",
    };
    public final static int colorOffsetInRef = 4;
    private final static int[] colorRId = {
    	R.id.defineyellow,
    	R.id.definewhite,
    	R.id.definered,
    	R.id.defineorange,
    	R.id.defineblue,
    	R.id.definegreen,
    };
    private static HashMap<Integer, Integer> mMap = new HashMap<Integer, Integer>();
    static {
    	mMap.put(R.id.defineyellow, Color.INDEXOFYELLOW);
    	mMap.put(R.id.definewhite, Color.INDEXOFWHITE);
    	mMap.put(R.id.definered, Color.INDEXOFRED);
    	mMap.put(R.id.defineorange, Color.INDEXOFORANGE);
    	mMap.put(R.id.defineblue, Color.INDEXOFBLUE);
    	mMap.put(R.id.definegreen, Color.INDEXOFGREEN);
    }
    
    private EditText mSpeed, mRandomRotateN, mSize;
    private RadioGroup mIDSwitchGrp;
    private int[] mColors = new int[mMap.size()];
    private RadioGroup mCameraWay;
    private RadioGroup mSolveWay;
    
    @Override
    public void onResume(){
    	super.onResume();
    	
    }
    
	@Override
	public void onPause(){
		super.onPause();
	
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.config, AdGlobals.getInstance().getAdInterface());
		
        setContentView(view);
/*        
        mconfig=(LinearLayout) findViewById(R.id.config);
        mconfig.setBackgroundResource(R.drawable.background);
*/        
    	mSpeed=(EditText)findViewById(R.id.speed);
    	mRandomRotateN=(EditText)findViewById(R.id.randomRotateN);
    	mSize=(EditText)findViewById(R.id.tubesize);
    	mIDSwitchGrp=(RadioGroup)findViewById(R.id.idswitchgroup);
    	mCameraWay = (RadioGroup)findViewById(R.id.cameraWay);
    	mSolveWay = (RadioGroup)findViewById(R.id.solveWay);
    	
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int speed = settings.getInt(ref[0], Tube.AnimationDefaultLength); 
        int randomRotateN = settings.getInt(ref[1], Tube.randomRotateNdefault);
        int size = settings.getInt(ref[2], Tube.DefaultSize);
        int faceidswitch = settings.getInt(ref[3], R.id.noid);
        int cameraWay = settings.getInt(ref[10], R.id.cameraWay1);
        int solveWay = settings.getInt(ref[11], R.id.advancedSolve);
        
        mSpeed.setText(Integer.valueOf(speed).toString());
        mRandomRotateN.setText(Integer.valueOf(randomRotateN).toString());
        mSize.setText(Integer.valueOf(size).toString());
        mIDSwitchGrp.check(faceidswitch);
        mCameraWay.check(cameraWay);
        mSolveWay.check(solveWay);
        
        mbsaveref=(Button)findViewById(R.id.saveref);
        mbcancelref=(Button)findViewById(R.id.cancelref);
        mbsaveref.setOnClickListener(this);
        mbcancelref.setOnClickListener(this);
        
        for(int i=0;i<mMap.size();i++){
        	int color = settings.getInt(ref[colorOffsetInRef+i], Color.standardCubeColors[i].toInt());
        	ImageView image = (ImageView)findViewById(colorRId[i]);
        	image.setOnClickListener(this); 
        	image.setBackgroundColor(color);
        	mColors[i] = color;
        	Color.setDefinedCubeColors(i, color);
        }
    }
    
    private void cancelref(){
    	finish();
    }
    
    private void showAlertDlg(int min, int max){
    	Dialog dialog=new AlertDialog.Builder(this)
        .setTitle("数字输入有错")
        .setMessage("有效值"+ min + "-" + max + "！请改正")
        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
        	public void onClick(DialogInterface dialog, int whichButton){
        		return;
        	}
        })
        .create();
    	dialog.show();    	
    }
    
    private void saveref(){
    	int speed; 
    	try {
    		speed = new Integer(mSpeed.getText().toString());
    	} catch (NumberFormatException ex) {
    		showAlertDlg(0, 999);
        	return;
    	}
    	
		int randomRotateN;
		try {
			randomRotateN= new Integer(mRandomRotateN.getText().toString());
			if(randomRotateN<25||randomRotateN>99){
				showAlertDlg(25, 99);
	        	return;				
			}
		} catch (NumberFormatException ex) {
			showAlertDlg(25, 99);
        	return;
    	}
    	
		int size;
		try {
			size= new Integer(mSize.getText().toString());
			if(!(size>=1 && size<=24)){
				showAlertDlg(1,24);
				return;
			}
		} catch (NumberFormatException ex) {
			showAlertDlg(1, 24);
        	return;
    	}
		
		int faceidswitch = mIDSwitchGrp.getCheckedRadioButtonId();
		int cameraWay = mCameraWay.getCheckedRadioButtonId();
		int solveWay = mSolveWay.getCheckedRadioButtonId();
		
		// Save user preferences. We need an Editor object to
		// make changes. All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(ref[0], speed);
		editor.putInt(ref[1], randomRotateN);
		editor.putInt(ref[2], size);
		editor.putInt(ref[3], faceidswitch);
		editor.putInt(ref[10], cameraWay);
		editor.putInt(ref[11], solveWay);
		
		// save Colors defined
		for(int i=0;i<mMap.size();i++){
			editor.putInt(ref[colorOffsetInRef+i], mColors[i]);
    		
    		// redefine oms.cj.tube.component.Color's corresponding color
    		Color.setDefinedCubeColors(i, mColors[i]);
		}
		
		// Don't forget to commit your edits!!!
		editor.commit();

		// 返回到main Activity
        finish();
    }   
    
    public void onClick(View view){
    	int viewid=view.getId();
    	
    	switch(viewid){
    	case R.id.saveref:
    		saveref();
    		break;
    	case R.id.cancelref:
    		cancelref();
    		break;
    	case R.id.defineyellow:
    	case R.id.definewhite:
    	case R.id.definered:
    	case R.id.defineorange:
    	case R.id.defineblue:
    	case R.id.definegreen:
    		Intent intent = new Intent();
    		intent.setClass(this, DefineColor.class);
    		startActivityForResult(intent, viewid);
    		break;
    	default:
    		cancelref();
    		break;
    	}
    }
    
	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data){
		if(resultCode == RESULT_OK){
			switch(requestCode){
			case R.id.defineyellow:
	    	case R.id.definewhite:
	    	case R.id.definered:
	    	case R.id.defineorange:
	    	case R.id.defineblue:
	    	case R.id.definegreen:
	    		int color = data.getIntExtra(DefineColor.colorStudy, 0);
	    		Log.i(TAG+".onActivityResult", "color="+color);
	    		ImageView image = (ImageView) findViewById(requestCode);
	    		image.setBackgroundColor(color);
	    		
	    		// save this color to corresponding color Definition
	    		int idx = mMap.get(requestCode);
	    		mColors[idx] = color;

	    		break;
	    	default:
	    		Log.e(TAG+".onActivityResult", "exception! invalid requestcode =" + requestCode);
	    		break;
			}
		} else {
			Log.i(TAG+".onActivityResult", "resultCode=" + resultCode);
		}
	}
}