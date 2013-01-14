package oms.cj.twenty4upgrade;

import com.waps.AppConnect;
import oms.cj.ads.AdGlobals;
import oms.cj.ads.IHackedActions;
import oms.cj.ads.WapsNode;
import oms.cj.tb.IBetweenMainAndGlobals;
import oms.cj.tb.IWorkingMode;
import oms.cj.tb.TB_SMS;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class twenty4upgrademain extends Activity implements View.OnClickListener, IBetweenMainAndGlobals{
	menu[] buttonlist={	
			new normalmenu("开始", true, this, "oms.cj.twenty4upgrade.twenty4upgrade"),
			new normalmenu("设置", true, this, "oms.cj.twenty4upgrade.config"),
			new wapsmenu("免费赚积分", true),
			new aboutmenu("关于", true, this),
			new exitmenu("退出", true)
	};
	
	//在getpaddings中修改主菜单的layout填充值
	//---------------------------------------------------------
	//--------------------  top  ------------------------------
	//-------------------    菜       ------------------------------
	//------  left  -----    单       -------------   right   ------
	//-------------------- bottom  -----------------------------
	//0-left
	//1-top
	//2-right
	//3-bottom
	private void getpaddings(int width, int height, int[] paddings){
		paddings[0]=0;
		paddings[1]=60;          
		paddings[2]=0;
		paddings[3]=0;
	}


	//image mode
	private static final int INVALIDIDX=-1;
	private static final String TAG="twenty4upgrademain";
	public static int WC=LinearLayout.LayoutParams.WRAP_CONTENT;
	
	private LinearLayout mmain;
	abstract class menu{
		String mmenuname;
		boolean misactive;
		abstract void menucallback();
		menu(String name, boolean isactive){
			mmenuname=name;
			misactive=isactive;
		}
	}

	class exitmenu extends menu{
		exitmenu(String name, boolean isactive) {
			super(name, isactive);
		}
		void menucallback(){
			finish();
		}
	}
	
	class wapsmenu extends exitmenu{
		wapsmenu(String name, boolean isactive) {
			super(name, isactive);
		}
		void menucallback(){
			if(AdGlobals.getInstance().wapsAdSwitch)
				AdGlobals.getInstance().showWapsOffer(twenty4upgrademain.this);
		}		
	}

	class aboutmenu extends menu{
		Context mctx;
		aboutmenu(String name, boolean isactive, Context ctx) {
			super(name, isactive);
			mctx=ctx;
		}
		void menucallback(){
	    	Dialog dialog=new AlertDialog.Builder(mctx)
	        .setTitle("关于")
	        .setMessage("作者：陈剑\n"+"电邮：whogiawho@gmail.com"+"\n"+"版权所有       2009\n")
	        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
	        	public void onClick(DialogInterface dialog, int whichButton){
	        		return;
	        	}
	        })
	        .create();
	    	dialog.show();    	
		}
	}
	
	class normalmenu extends menu{
		Context mctx;
		String mactivityname;
		normalmenu(String name, boolean isactive, Context ctx, String activityname) {
			super(name, isactive);
			mctx=ctx;
			mactivityname=activityname;
		}

		void menucallback(){
	        Intent intent=new Intent();
	        //replace <dragon> with ur own activity class
	        intent.setClassName(mctx, mactivityname); 
	        startActivity(intent);  			
		}
	}
	
    @Override
    public void onResume(){
    	super.onResume();
    	
    }
	@Override
    protected void onDestroy() {
		if(AdGlobals.getInstance().wapsAdSwitch)
			AppConnect.getInstance(this).finalize();
        super.onDestroy();
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AdGlobals.getInstance().wapsAdSwitch)
        	AppConnect.getInstance(this); 
        AdGlobals.getInstance().init(Globals.YoumiID, Globals.YoumiPass, this);
        
        Globals g = new Globals();
        if(g.getTrial()==IWorkingMode.TRIAL) {
        	trialFlow();
        }

        if(g.getWorkingMode()==IWorkingMode.TB) {
            //codes to support T&B
            TB_SMS tbSMS = new TB_SMS(this, this, "000202", "101436", "00020201");
            if(tbSMS.checkQualificationToContinue()){
            	normalFlow();
            } else {
            	tbSMS.promptToBuy(2.0);
            }
            tbSMS.setTitle();
        } else {
        	normalFlow();
        }        
    }
    
    private int getidx(View view){
    	if(view==null)
    		return INVALIDIDX;
    	Button button=(Button)view;
    	for(int i=0;i<buttonlist.length;i++){
    		if(buttonlist[i].mmenuname==button.getText()){
    			return i;
    		}
    	}
    	
    	return INVALIDIDX;
    }
    
    public void onClick(View view){
    	final int idx;
    	
    	idx=getidx(view);
    	Log.i(TAG+"onClick", "idx="+idx);
    	
    	switch(idx){
    	case 0:
			if(AdGlobals.getInstance().wapsAdSwitch){
				//define hacked actions here
				IHackedActions action = new IHackedActions(){
					@Override
					public void playAfterPassCheck() {
						buttonlist[0].menucallback();
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
			buttonlist[0].menucallback();
    		break;
    	default:
        	if(idx!=INVALIDIDX && idx<buttonlist.length){
        		buttonlist[idx].menucallback();
        	}
    		break;
    	}
    }   
    
    //helper 1 to support T&B
    public void normalFlow(){
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.main, AdGlobals.getInstance().getAdInterface());
        setContentView(view);
        
        mmain=(LinearLayout) findViewById(R.id.main);
        
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Log.i(TAG, "display.height="+display.getHeight());
        Log.i(TAG, "display.width="+display.getWidth());        
        int scwidth = display.getWidth();
        int scheight = display.getHeight();
        int paddings[]={0,0,0,0};
        getpaddings(scwidth, scheight, paddings);
        mmain.setPadding(paddings[0], paddings[1], paddings[2], paddings[3]);

        for(int i=0;i<buttonlist.length;i++){
        	if(buttonlist[i].misactive){
        		Button button=new Button(this); 
        		LinearLayout.LayoutParams lparam=new LinearLayout.LayoutParams(130, WC);
        		lparam.gravity=Gravity.CENTER;
        		mmain.addView(button, lparam);
        		button.setText(buttonlist[i].mmenuname);
        		button.setOnClickListener(this);
        	}
        }
        
        mmain.setBackgroundResource(R.drawable.background);    	
        
    }
    
    //helper 2 to support trial version
    public void trialFlow(){
    	buttonlist[1].misactive = false;    	
    }

	@Override
	public void adFlow() {	
		
	}
}