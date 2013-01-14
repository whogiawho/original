package oms.cj.twenty4upgrade;

import java.util.ArrayList;

import com.waps.AppConnect;

import oms.cj.ads.AdGlobals;
import oms.cj.tb.IWorkingMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class twenty4upgrade extends Activity implements View.OnClickListener{
	public static final int DefaultExpectedResult = 24;
	public static final int FOUROPERANDS = R.id.four;
	public static final int FIVEOPERANDS = R.id.five;
	public static final int SIXOPERRANDS = R.id.six;

    private ArrayList<EditText> number;
    private Button submitButton,addnumber,removenumber;
    private TextView label;
 
    private Integer numitem;
    private static int MAXNUMITEM=6;
    private static int MINNUMITEM=4;

    private int mExpectedResult;
    
    private void setlabel(int numitem){
    	label.setText("请输入"+numitem+"个数字");
    }
    
    private void setbuttonwidth(){
    	ScreenInfo screen = new ScreenInfo(this);
    	int viewwidth = screen.getWidth()/3;
    	
    	for(int i=0;i<MAXNUMITEM;i++){
    		number.get(i).setWidth(viewwidth);
    	}
    	submitButton.setWidth(viewwidth);
    	addnumber.setWidth(viewwidth);
    	removenumber.setWidth(viewwidth);
    }
    
    private void createviews(){
        label=(TextView)findViewById(R.id.numberofoperandslabel);
        
        number=new ArrayList<EditText>();
        number.add((EditText)findViewById(R.id.firstnumber));
        number.add((EditText)findViewById(R.id.secondnumber));
        number.add((EditText)findViewById(R.id.thirdnumber));
        number.add((EditText)findViewById(R.id.fourthnumber));
        number.add((EditText)findViewById(R.id.fifthnumber));
        number.add((EditText)findViewById(R.id.sixthnumber));
               
        submitButton=(Button)findViewById(R.id.submitButton);
        addnumber=(Button)findViewById(R.id.addnumber);
        removenumber=(Button)findViewById(R.id.removenumber);
        submitButton.setOnClickListener(this);
        addnumber.setOnClickListener(this);
        removenumber.setOnClickListener(this);
    }
    
    private void loadpref(){
        //读入preferences
        SharedPreferences settings = getSharedPreferences(config.PREFS_NAME, 0);
        int numberofoperands = settings.getInt(config.ref[0], FOUROPERANDS);
        if(numberofoperands == R.id.four){
        	//第56个输入框不可见，removenumber disabled
        	numitem = 4;
            number.get(4).setVisibility(View.INVISIBLE);
            number.get(5).setVisibility(View.INVISIBLE);
            removenumber.setEnabled(false);
        } else if(numberofoperands == R.id.five){
        	//第6个输入框不可见
        	number.get(5).setVisibility(View.INVISIBLE);
        	numitem = 5;
        } else { 
        	//addnumber disabled
        	numitem = 6;
        	addnumber.setEnabled(false);
        }
        setlabel(numitem);
        mExpectedResult = settings.getInt(config.ref[1], DefaultExpectedResult);    	
    }

    private void trialFlow(){
    	EditText firstNumber = (EditText) findViewById(R.id.firstnumber);
    	firstNumber.setText("3");
    	firstNumber.setEnabled(false);
    	addnumber.setEnabled(false);
    	removenumber.setEnabled(false);
    }
	
    @Override
    public void onResume(){
    	super.onResume();
    	
    }
    
    @Override    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.twenty4upgrade, AdGlobals.getInstance().getAdInterface());
        setContentView(view);

        //创建6个数字输入框，+button, -button, 求解button
        createviews();
        //设置以上9个view的width
        setbuttonwidth();
        //load preference
        loadpref();
        
        Globals g = new Globals();
        if(g.getTrial() == IWorkingMode.TRIAL){
        	trialFlow();
        }
    }
    
    private void clicksubmitbutton(){
    	Integer i;
    	String str="", curstr="";
    	
        try {
        	for(i=0;i<numitem;i++){
            	curstr=number.get(i).getText().toString();
            	Integer.parseInt(curstr);
            	str=str+curstr+" ";
        	}
        } catch (NumberFormatException ex){
        	Dialog dialog=new AlertDialog.Builder(this)
        	        .setTitle("提示对话框")
        	        .setMessage("数字输入有错！请改正")
        	        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
        	        	public void onClick(DialogInterface dialog, int whichButton){
        	        		return;
        	        	}
        	        })
        	        .create();
        	dialog.show();
        	return;
        }
        
        Intent intent=new Intent();
        intent.setClass(twenty4upgrade.this, caltwenty4.class);
        Bundle mBundle=new Bundle();
        mBundle.putString("digitlist", str);
        mBundle.putInt("expectedresult", mExpectedResult);
        intent.putExtras(mBundle);
        
        int adType = AdGlobals.getInstance().getAdType();
        startActivityForResult(intent, adType);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(requestCode==AdGlobals.MM){
    		AdGlobals.getInstance().playMMInterstitialAd(this);
    	} else if(requestCode==AdGlobals.WAPS && AdGlobals.getInstance().wapsAdSwitch){
    		AppConnect.getInstance(this).showOffers(this);
    		AppConnect.getInstance(this).getPushAd();
    	}
    }

    private void clickaddnumber(){
    	number.get(numitem).setVisibility(View.VISIBLE);
    	numitem=numitem+1;
    	setlabel(numitem);
    	
    	if(numitem==MAXNUMITEM){
    		//addnumber.setVisibility(View.INVISIBLE);
    		addnumber.setEnabled(false);
    	}
    	if(numitem>MINNUMITEM){
    		//removenumber.setVisibility(View.VISIBLE);
    		removenumber.setEnabled(true);
    	}
    }
    
    private void clickremovenumber(){
    	numitem=numitem-1;
    	setlabel(numitem);
    	number.get(numitem).setVisibility(View.INVISIBLE);
    	
    	if(numitem==MINNUMITEM){
    		//removenumber.setVisibility(View.INVISIBLE);
    		removenumber.setEnabled(false);
    	}    	
    	if(numitem<MAXNUMITEM){
    		//addnumber.setVisibility(View.VISIBLE);
    		addnumber.setEnabled(true);
    	}
    }
    
    public void onClick(View view) {
    	int buttonid;
    	
    	buttonid=view.getId();
    	switch (buttonid){
    	case R.id.submitButton:
    		clicksubmitbutton();
    		break;
    	case R.id.removenumber:
    		clickremovenumber();
    		break;
    	case R.id.addnumber:
    		clickaddnumber();
    		break;
    	}
    }
}