package oms.cj.WuZiGame;

import com.waps.AppConnect;

import oms.cj.WuZiGame.R;
import oms.cj.ads.AdGlobals;
import oms.cj.ads.IHackedActions;
import oms.cj.ads.WapsNode;
import oms.cj.musicservice.SoundService;
import oms.cj.tb.IBetweenMainAndTB;
import oms.cj.tb.IWorkingMode;
import oms.cj.tb.TB_SMS;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class WuZiGame extends Activity implements View.OnClickListener, IBetweenMainAndTB, IWorkingMode{
	//注一：
	//如果要减少一个菜单，只要将menu的第二项参数设为false即可
	//如果要增加一个菜单，需要做的事情有：
	//  1. new一个menu类，添至buttonlist中
	//  2. 如果normalmenu不能满足需求，请从menu类派生新子类
	//  3. 传一个字符串 "oms.cj.WuZiGame.<urname>"到menu对象中
	//  4. 生成和<urname>一致的Activity
	//  5. 在AndroidManifest.xml声明<urname> Activity
	//注二：
	//复用此WuZiGame应用的步骤
	//  1. copy WuZiGame目录生成新目录<newappname>
	//  2. 替换newname下所有的文件“s/WuZiGame/<newappname>/g”
	
	//需要配置的参数
	menu[] buttonlist={	new normalmenu("开始", true, this, "oms.cj.WuZiGame.newgame"),
						new wapsmenu("免费赚积分", true),
			   			new normalmenu("设置", true, this, "oms.cj.WuZiGame.config"),
			   			new normalmenu("帮助", true, this, "oms.cj.WuZiGame.help"),
			   			new aboutmenu("关于", false, this),
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
		paddings[1]=0;                  
		paddings[2]=0;
		paddings[3]=0;
	}
		

	//image mode
	private static final int INVALIDIDX=-1;
	public static final int IMAGEMODEHVGA=0;
	public static final int IMAGEMODEWVGA=1;
	private static final String TAG="WuZiGame";
	public static int WC=LinearLayout.LayoutParams.WRAP_CONTENT;
	private SoundService mS;
	
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
				AdGlobals.getInstance().showWapsOffer(WuZiGame.this);
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
	        .setMessage("作者：陈剑\n"+"电邮：whogiawho@gmail.com"+"\n"+"版权所有       2009-2011\n")
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
		
	@SuppressWarnings("unused")
	private int decidemode(int width, int height){
		int min=Math.min(width, height);
		
		if(min<480)
			return IMAGEMODEHVGA;
		if(min<640)
			return IMAGEMODEWVGA;
		
		//default return HVGA
		return IMAGEMODEHVGA;
	}

	private int getSoundEffects(){
		SharedPreferences settings = getSharedPreferences(config.PREFS_NAME, 0);
		int se = settings.getInt(config.ref[8], WuZiConfig.SOUNDOFF);
		return se;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	/*
    	int[][] a = {
    			{2, 3},
    			{1, 4}
    	};
    	int[][] b = new int[2][2];
    	System.arraycopy(a, 0, b, 0, 4);
    	for(int i=0;i<2;i++)
    		for(int j=0;j<2;j++){
    			Log.i(TAG+".onCreate", ""+b[i][j]);
    		}
    	*/
    	if(AdGlobals.getInstance().wapsAdSwitch)
    		AppConnect.getInstance(this);
    	AdGlobals.getInstance().init(Globals.YoumiID, Globals.YoumiPass, this);

    	long heapSize = Runtime.getRuntime().maxMemory(); 
    	Log.i(TAG+".onCreate", "heapSize="+heapSize);
    	
        super.onCreate(savedInstanceState);
        
        //check SoundEffects switch
        mS = new SoundService(this);
        
		// fullscreen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);        
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if(getWorkingMode()==IWorkingMode.TB) {
            //codes to support T&B
            TB_SMS tbSMS = new TB_SMS(this, this, "000204", "101442", "00020401");
            if(tbSMS.checkQualificationToContinue()){
            	normalFlow();
            } else {
            	tbSMS.promptToBuy(4.99);
            }
            tbSMS.setTitle();
        } else {
        	normalFlow();
        }
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig){
    	Log.i(TAG, "onConfigurationChanged() is called!");
    	super.onConfigurationChanged(newConfig);
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
    
    private void onButtonClick(int idx){
    	if(idx!=INVALIDIDX && idx<buttonlist.length){
    		if(getSoundEffects()==R.id.soundon)
    			mS.playSound(SoundService.MENUCLICKED);
    		buttonlist[idx].menucallback();
    	}
    }
    
    public void onClick(View view){
    	final int idx;
    	
    	idx=getidx(view);
    	Log.i(TAG,"onClick(...): "+"idx="+idx);
    	
    	switch(idx){
    	case 0:
			if(AdGlobals.getInstance().wapsAdSwitch){
				//define hacked actions here
				IHackedActions action = new IHackedActions(){
					@Override
					public void playAfterPassCheck() {
						onButtonClick(idx);
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
			onButtonClick(idx);
    		break;
    	default:
        	onButtonClick(idx);
    		break;
    	}
    } 
    
    //helper 1 to support T&B
    public void normalFlow(){
    	View v = AdGlobals.getInstance().inflateContentView(this, R.layout.main, AdGlobals.getInstance().getAdInterface());
        setContentView(v);

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
        		LinearLayout.LayoutParams lparam=new LinearLayout.LayoutParams(230, WC);
        		lparam.gravity=Gravity.CENTER;
        		mmain.addView(button, lparam);
        		button.setText(buttonlist[i].mmenuname);
        		button.setOnClickListener(this);
        		try {
        			Resources res = getResources();
        			XmlResourceParser xpp;
        			
            		//重新设置button的背景图像
        			Drawable myDrawable = null;
        			xpp = res.getXml(R.drawable.mybtn);
        			myDrawable = Drawable.createFromXml(res, xpp);
        			myDrawable.setAlpha(100);
        			button.setBackgroundDrawable(myDrawable);

            		//重新设置button Text的color
        			ColorStateList cl = null;
        			xpp = res.getXml(R.color.mycolor);
        			cl = ColorStateList.createFromXml(res, xpp);
        			Log.i(TAG, "onCreate(...):" + "isStateful=" + cl.isStateful());
        			button.setTextColor(cl);
        		} catch (Exception e) {}
        	}
        }
        
        //mmain.setBackgroundResource(R.drawable.godchess);
        mmain.setBackgroundResource(R.drawable.wuziqifront);
    }
    
    //helper 2 to support T&B
	@Override
	public int getWorkingMode() {
		
		return IWorkingMode.NORMAL; 
	}    
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0) {
			SharedPreferences settings = getSharedPreferences(config.PREFS_NAME, 0);
			int soundEffect = settings.getInt(config.ref[8], WuZiConfig.SOUNDON);
			if(soundEffect==WuZiConfig.SOUNDON)
				mS.playSound(SoundService.ESCCLICKED);
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
    protected void onDestroy() {
		if(AdGlobals.getInstance().wapsAdSwitch)
			AppConnect.getInstance(this).finalize();
        super.onDestroy();
    }
}