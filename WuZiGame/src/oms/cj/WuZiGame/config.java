package oms.cj.WuZiGame;

import oms.cj.WuZiGame.R;
import oms.cj.ads.AdGlobals;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.util.Log;
import android.view.*;;

public class config extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{
	private final static String TAG="config";
	public static final String PREFS_NAME = "WuZiGamePrefs";
	LinearLayout mconfig;
	private Button mbsaveref, mbcancelref;
	
	//define ur preference name here
	public static String[] ref={
		"xianhoushou", 
		"difficulty", 
		"strategy", 
		"sansan", 
		"sisi", 
		"changlian",  
		"voiceswitch",
        "whenhitboard",
		"soundeffects"
	};

	//for each preference, normally below tasks should be done
	// 1. define it on config.xml
	// 2. write codes in this file to access/edit this preference
	//    follow 4 steps listed below
	
/* step 1 */
	private RadioGroup mXianHouGrp;	
	private RadioGroup mDifficulty;
	private RadioGroup mStrategy;
	private RadioGroup mVoiceSwitch;
	private RadioGroup mSoundEffects;
	private CheckBox mCheckSanSan, mCheckSiSi, mCheckChangLian;
	
	private void enableHard(boolean status){
		RadioButton rb = (RadioButton) findViewById(R.id.hard);
		rb.setEnabled(status);
	}
	private RadioGroup mWhenHitBoard;
	
    @Override
    public void onResume(){
    	super.onResume();
    	
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		// fullscreen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.config, AdGlobals.getInstance().getAdInterface());
        setContentView(view);

        enableHard(true);
        
        mbsaveref=(Button)findViewById(R.id.saveref);
        mbcancelref=(Button)findViewById(R.id.cancelref);
        mbsaveref.setOnClickListener(this);
        mbcancelref.setOnClickListener(this);
        
/* step 2 */
        mXianHouGrp=(RadioGroup)findViewById(R.id.xianhougroup);
        mDifficulty=(RadioGroup)findViewById(R.id.difficulty);
        mStrategy=(RadioGroup)findViewById(R.id.strategy);
        mVoiceSwitch=(RadioGroup)findViewById(R.id.voiceswitch);
        mDifficulty.setOnCheckedChangeListener(this);
        mCheckSanSan=(CheckBox)findViewById(R.id.checksansan);
        mCheckSiSi=(CheckBox)findViewById(R.id.checksisi);
        mCheckChangLian=(CheckBox)findViewById(R.id.checkchanglian);
        mWhenHitBoard=(RadioGroup)findViewById(R.id.whenhitboard);
        mSoundEffects=(RadioGroup)findViewById(R.id.soundeffects);
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int xianhoushou = settings.getInt(ref[0], WuZiConfig.XianShou);
        int difficulty = settings.getInt(ref[1], WuZiConfig.EASY);
        int voiceswitch = settings.getInt(ref[6], WuZiConfig.VOICEOFF);
        int strategy = settings.getInt(ref[2], WuZiConfig.ATTACK);
        boolean bSanSan = settings.getBoolean(ref[3], true);
        boolean bSiSi = settings.getBoolean(ref[4], true);
        boolean bChangLian = settings.getBoolean(ref[5], true);
        int whenhitboard = settings.getInt(ref[7], WuZiConfig.MOVEFOCUSONLY);
        int soundeffects = settings.getInt(ref[8], WuZiConfig.SOUNDOFF);
        
        mXianHouGrp.check(xianhoushou);      
        mDifficulty.check(difficulty);
        //根据difficulty来决定strategy group的显示与否
        mStrategy.check(strategy);
		if(difficulty==WuZiConfig.LITTLEHARD || difficulty==WuZiConfig.HARD){
			setStrategyRadioGroup(View.VISIBLE);
		} else {
			setStrategyRadioGroup(View.GONE);
		}
		mCheckSanSan.setChecked(bSanSan);
		mCheckSiSi.setChecked(bSiSi);
		mCheckChangLian.setChecked(bChangLian);
		mVoiceSwitch.check(voiceswitch);
		mWhenHitBoard.check(whenhitboard);
		mSoundEffects.check(soundeffects);
		
		mconfig=(LinearLayout) findViewById(R.id.config);
		mconfig.setBackgroundResource(R.drawable.background);
    }  
    
    private void cancelref(){
    	finish();
    }
    
    private void saveref(){
/* step 3 */   	
    	int xianhoushou = mXianHouGrp.getCheckedRadioButtonId();
    	int difficulty = mDifficulty.getCheckedRadioButtonId();
		int strategy = mStrategy.getCheckedRadioButtonId();
		int voiceswitch = mVoiceSwitch.getCheckedRadioButtonId();
		boolean bSanSan = mCheckSanSan.isChecked();
		boolean bSiSi = mCheckSiSi.isChecked();
		boolean bChangLian = mCheckChangLian.isChecked();
    	int whenhitboard = mWhenHitBoard.getCheckedRadioButtonId();
    	int soundeffects = mSoundEffects.getCheckedRadioButtonId();
		
        // Save user preferences. We need an Editor object to
        // make changes. All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        
/* step 4 */      
        editor.putInt(ref[0], xianhoushou);
        editor.putInt(ref[1], difficulty);
        //保存strategy的值
		editor.putInt(ref[2], strategy);
		editor.putBoolean(ref[3], bSanSan);
		editor.putBoolean(ref[4], bSiSi);
		editor.putBoolean(ref[5], bChangLian);
		editor.putInt(ref[6], voiceswitch);
		editor.putInt(ref[7], whenhitboard);
		editor.putInt(ref[8], soundeffects);

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
    	default:
    		cancelref();
    		break;
    	}
    }

    //type must be these two values:
    //1. View.GONE
    //2. View.VISIBLE
    private void setStrategyRadioGroup(int type){
		mStrategy.setVisibility(type); 
		TextView strategyLabel = (TextView)findViewById(R.id.strategylabel);
		strategyLabel.setVisibility(type);
    }
    
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int buttonID = group.getCheckedRadioButtonId();
		switch(buttonID){
		case R.id.littlehard:
		case R.id.hard:
			setStrategyRadioGroup(View.VISIBLE);
			break;
		case R.id.easy:
		case R.id.medium:
		case R.id.impossible:
		default:
			setStrategyRadioGroup(View.GONE);
			break;
		}
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig){
    	Log.i(TAG, "onConfigurationChanged() is called!");
    	super.onConfigurationChanged(newConfig);
    }
}