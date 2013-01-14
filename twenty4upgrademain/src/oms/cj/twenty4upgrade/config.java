package oms.cj.twenty4upgrade;

import oms.cj.ads.AdGlobals;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.view.*;;

public class config extends Activity implements View.OnClickListener{
	public static final String PREFS_NAME = "twenty4upgradePrefs";
	//define ur preference name here
	public static String[] ref={"numberofoperands", "expectedresult"};
	
	LinearLayout mconfig;
	private Button mbsaveref, mbcancelref;

	//for each preference, normally below tasks should be done
	// 1. define it on config.xml
	// 2. write codes in this file to access/edit this preference
	//    follow 4 steps listed below
	

	private EditText mExpectedResult;
	private RadioGroup mNumberOfOperandsGrp;
	
    @Override
    public void onResume(){
    	super.onResume();
    	
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.config, AdGlobals.getInstance().getAdInterface());
        setContentView(view);
        
        mconfig=(LinearLayout) findViewById(R.id.config);
        
        mbsaveref=(Button)findViewById(R.id.saveref);
        mbcancelref=(Button)findViewById(R.id.cancelref);
        mbsaveref.setOnClickListener(this);
        mbcancelref.setOnClickListener(this);
        

        mExpectedResult=(EditText)findViewById(R.id.expectedresult);      
        mNumberOfOperandsGrp = (RadioGroup)findViewById(R.id.numberofoperandsgroup);
        
        loadref();
    }  
    
    private void cancelref(){
    	finish();
    }
    
    private void loadref(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        
        int numberofoperands = settings.getInt(ref[0], twenty4upgrade.FOUROPERANDS);
        mNumberOfOperandsGrp.check(numberofoperands);
        
        int expectedResult = settings.getInt(ref[1], twenty4upgrade.DefaultExpectedResult); 
        mExpectedResult.setText(Integer.valueOf(expectedResult).toString());
    }
    
    private void saveref(){
    	try {
        	int numberofoperands = mNumberOfOperandsGrp.getCheckedRadioButtonId();
    		int expectedResult = new Integer(mExpectedResult.getText().toString());
    	
    		// Save user preferences. We need an Editor object to
    		//make changes. All objects are from android.context.Context
    		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    		SharedPreferences.Editor editor = settings.edit();
        
        	editor.putInt(ref[0], numberofoperands);        
    		editor.putInt(ref[1], expectedResult);
        
    		// Don't forget to commit your edits!!!
    		editor.commit();
        
    		// 返回到main Activity
    		finish();
    	} catch (NumberFormatException ex) {
        	Dialog dialog=new AlertDialog.Builder(this)
	        .setTitle("数字输入有错")
	        .setMessage("有效值0-99999！请改正")
	        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
	        	public void onClick(DialogInterface dialog, int whichButton){
	        		return;
	        	}
	        })
	        .create();
        	dialog.show();
        	return;
    	}
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