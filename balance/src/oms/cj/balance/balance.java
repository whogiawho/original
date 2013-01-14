/*
    balance, An OPhone game to practise da ju guan

    Copyright (C) <2009>  chenjian

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    You can contact me by email ustcchenjian@gmail.com
*/

package oms.cj.balance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import android.util.Log;
import android.view.View;
import java.util.*;
import com.openfeint.api.ui.Dashboard;
import com.waps.AppConnect;

import oms.cj.ads.AdGlobals;
import oms.cj.ads.IHackedActions;
import oms.cj.ads.WapsNode;
import oms.cj.tb.IBetweenMainAndTB;
import oms.cj.tb.IWorkingMode;
import oms.cj.tb.TB_SMS;

//steps to close waps AD:
//1. set AdGloblas.wapsAdSwitch=false
//2. remove WAPS from AdGlobals.adPercents

//steps to remove the9 
//1. set AdGlobals.the9Switch=false

public class balance extends Activity implements View.OnClickListener, IBetweenMainAndTB, IWorkingMode{
	Button mbnewgame,mbrecords,mbhelp,mbabout,mbexit,mconfiguration, mbthe9records, mfreecoins;
	LinearLayout mlmainlayout;
	ArrayList<Map<String,String>> mrecords;
	private final static String TAG="balance";
    private final static int NEWGAME = 0;
    
    /** Called when the activity is first created. */
    @Override 
    public void onCreate(Bundle savedInstanceState) {  	
        super.onCreate(savedInstanceState);
        
        if(AdGlobals.getInstance().wapsAdSwitch)
        	AppConnect.getInstance(this); 
        AdGlobals.getInstance().init(Globals.YoumiID, Globals.YoumiPass, this);
        
        if(getWorkingMode()==IWorkingMode.TB) {
            //codes to support T&B
            TB_SMS tbSMS = new TB_SMS(this, this, "000205", "101443", "00020501");
            if(tbSMS.checkQualificationToContinue()){
            	normalFlow();
            } else {
            	tbSMS.promptToBuy(3.0);
            }
            tbSMS.setTitle();
        } else {
        	normalFlow();
        }
    }
    
    @Override
    public void onStop(){
		super.onStop();
    }

    private void newgame(){
        Intent intent=new Intent();
        intent.setClass(this, game.class);
        //Bundle mBundle=new Bundle();
        //mBundle.putString("digitlist", str);
        //intent.putExtras(mBundle);
        
        startActivityForResult(intent, NEWGAME);    	
    }
    private void showrecords(){
        Intent intent=new Intent();
        intent.setClass(this, records.class);
        startActivity(intent);    	    	
    }
    private void showhelp(){
        Intent intent=new Intent();
        intent.setClass(this, help.class);
        startActivity(intent);    	   
    }
    private void showabout(){
    	String author = getString(R.string.author);
    	String email = getString(R.string.email);
    	String copyright = getString(R.string.copyright);
    	String strAbout = getString(R.string.about);
    	String strAboutInfo = getString(R.string.aboutinfo, author, email, copyright);
    	
    	Dialog dialog=new AlertDialog.Builder(this)
        .setTitle(strAbout)
        .setMessage(strAboutInfo)
        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
        	public void onClick(DialogInterface dialog, int whichButton){
        		return;
        	}
        })
        .create();
    	dialog.show();    	
    }
    private void exit(){
    	finish();
    }
    private void configuration(){
    	Intent intent=new Intent();
    	intent.setClass(this, configapp.class);
    	startActivity(intent);
    	return;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	String out = String.format("resultCode=%d, requestCode=%d", resultCode, requestCode);
		Log.i(TAG+".onActivityResult", out);
		
    	if(resultCode==Activity.RESULT_OK){
        	switch(requestCode){
        	case NEWGAME:
        		if(AdGlobals.getInstance().the9Switch){
            		//only enter leaderboard if RESULT_OK and requestCode==NEWGAME
            		BalanceApplication app = (BalanceApplication) getApplication();
                    SharedPreferences settings = getSharedPreferences(configapp.PREFS_NAME, 0);
                    int level = settings.getInt(configapp.ref[0], plate.MEDIUM); 
            		String leaderboardID = app.getLeaderboardID(level);
            		Dashboard.openLeaderboard(leaderboardID);       			
        		}

        		break;
        	default:
        		super.onActivityResult(requestCode, resultCode, data);
        		break;
        	}    		
    	}
    }
    
    public void onClick(View view){
    	int viewid;
    	
    	viewid=view.getId();
    	switch(viewid){
    	case R.id.newgame:
			if(AdGlobals.getInstance().wapsAdSwitch){
				//define hacked actions here
				IHackedActions action = new IHackedActions(){
					@Override
					public void playAfterPassCheck() {
						newgame();
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
    		newgame();
    		break;
    	case R.id.records:
    		showrecords();
    		break;
    	case R.id.the9records:
    		if(AdGlobals.getInstance().the9Switch)
    			Dashboard.openLeaderboards();
    		break;
    	case R.id.help:
    		showhelp();
    		break;
    	case R.id.about:
    		showabout();
    		break;
    	case R.id.exit:
    		exit();
    		break;
    	case R.id.configuration:
    		configuration();
    		break;
    	case R.id.freecoins:
    		if(AdGlobals.getInstance().wapsAdSwitch)
    			AdGlobals.getInstance().showWapsOffer(this);
    		break;
    	}
    }  
    
    //helper 1 to support T&B
    public void normalFlow(){
        
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.main, AdGlobals.getInstance().getAdInterface());
        setContentView(view);
        
        mlmainlayout=(LinearLayout)findViewById(R.id.mainlayout);
        mlmainlayout.setBackgroundResource(R.drawable.background);
        mlmainlayout.setPadding(0, 20, 0, 0);
        
        mbnewgame=(Button)findViewById(R.id.newgame);
        mbrecords=(Button)findViewById(R.id.records);
        mbhelp=(Button)findViewById(R.id.help);
        mbabout=(Button)findViewById(R.id.about);
        mbexit=(Button)findViewById(R.id.exit);
        mconfiguration=(Button)findViewById(R.id.configuration);
        mbthe9records=(Button)findViewById(R.id.the9records);
        mfreecoins = (Button)findViewById(R.id.freecoins);
        
        mbnewgame.setOnClickListener(this);
        mbrecords.setOnClickListener(this);
        mbhelp.setOnClickListener(this);
        mbabout.setOnClickListener(this);
        mbexit.setOnClickListener(this);
        mconfiguration.setOnClickListener(this);
        mbthe9records.setOnClickListener(this); 
        mfreecoins.setOnClickListener(this);
        
        if(AdGlobals.getInstance().the9Switch){
            mbthe9records.setVisibility(View.VISIBLE);
        } else {
        	mbthe9records.setVisibility(View.GONE);
        }
        if(AdGlobals.getInstance().wapsAdSwitch){
        	mfreecoins.setVisibility(View.VISIBLE);
        } else {
        	mfreecoins.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume(){
    	super.onResume();
    	
    }
    
    //helper 2 to support T&B
	public int getWorkingMode() {
		
		return IWorkingMode.NORMAL;
	}  
	@Override
    protected void onDestroy() {
		if(AdGlobals.getInstance().wapsAdSwitch)
			AppConnect.getInstance(this).finalize();
        super.onDestroy();
    }	
}