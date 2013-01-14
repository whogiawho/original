package oms.cj.balance;

import oms.cj.ads.AdGlobals;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

public class configapp extends Activity implements View.OnClickListener{
    public static final String PREFS_NAME = "BalancePrefs";
    private RadioGroup mlevelgrp, mdebuggrp, mstylegrp, mbackgroundgrp, mmoveshowgroup;
    private Button mbsaveref, mbcancelref;
    public final static String[] ref={
    		"level",
    		"debug",
    		"style",
    		"background",
    		"dragtype",
    };
    
    @Override
    public void onResume(){
    	super.onResume();
    	
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {  		
    	super.onCreate(savedInstanceState);
        
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.configapp, AdGlobals.getInstance().getAdInterface());
        setContentView(view);
    	
    	mlevelgrp=(RadioGroup)findViewById(R.id.levelgroup);
    	mdebuggrp=(RadioGroup)findViewById(R.id.debuggroup);
    	mstylegrp=(RadioGroup)findViewById(R.id.stylegroup);
    	mbackgroundgrp=(RadioGroup)findViewById(R.id.backgroundgroup);
    	mmoveshowgroup=(RadioGroup)findViewById(R.id.moveshowgroup);
    	
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int level = settings.getInt(ref[0], plate.MEDIUM); 
        int debug = settings.getInt(ref[1], game.OFF);
        int style = settings.getInt(ref[2], plate.BALLSTYLE);
        int background = settings.getInt(ref[3], R.drawable.base1);
        int dragtype = settings.getInt(ref[4], game.TOPRIGHT);
        
        mlevelgrp.check(level);
        mdebuggrp.check(debug);
        mstylegrp.check(style);
        mbackgroundgrp.check(background);
        mmoveshowgroup.check(dragtype);
        
        mbsaveref=(Button)findViewById(R.id.saveref);
        mbcancelref=(Button)findViewById(R.id.cancelref);
        mbsaveref.setOnClickListener(this);
        mbcancelref.setOnClickListener(this);
    }
    
    private void cancelref(){
    	finish();
    }
    
    private void saveref(){
    	int level = mlevelgrp.getCheckedRadioButtonId();
    	int debug = mdebuggrp.getCheckedRadioButtonId();
    	int style = mstylegrp.getCheckedRadioButtonId();
    	int background = mbackgroundgrp.getCheckedRadioButtonId();
    	int dragtype = mmoveshowgroup.getCheckedRadioButtonId();
    	
        // Save user preferences. We need an Editor object to
        // make changes. All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(ref[0], level);
        editor.putInt(ref[1], debug);
        editor.putInt(ref[2], style);
        editor.putInt(ref[3], background);
        editor.putInt(ref[4], dragtype);
        
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
}