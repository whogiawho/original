/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oms.cj.tube;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParserException;
import com.openfeint.api.ui.Dashboard;
import com.waps.AppConnect;
import com.wooboo.adlib_android.ImpressionAdView;
import oms.cj.ads.AdGlobals;
import oms.cj.tb.IBetweenMainAndTB;
import oms.cj.tb.IWorkingMode;
import oms.cj.tb.TB_SMS;
import oms.cj.tube.R;
import oms.cj.widget.IconButton;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

// Where is ad displayed
// a. MainActivity, ChooseFileActivity, MoFangMiJiActivity, TubeSolverActivity1, 
//    TubeSolverActivity2, .camera.way1.Snapshot, .camera.way2.Snapshot,
//    OriginCube, RandomCube, RestoreCubeActivity
// b. .Config, .tutor.basic.step[1-9].TubeTutor[1-9]Activity[1-4].java
// c. Any Activity containing TubePlayer    

// steps to close waps AD:
// 1. set AdGloblas.wapsAdSwitch=false
// 2. disable mMenuList[1].mIsActive
// 3. remove WAPS from AdGlobals.adPercents

// steps to remove the9
// 1. disable mMenuList[2].mIsActive
// 2. set AdGlobals.the9Switch=false

public class MainActivity extends Activity implements View.OnClickListener, IBetweenMainAndTB, IWorkingMode {

    private final static String TAG="MainActivity";
	private final static int PREDEFINEDCNT = 10; 
	private static final int INVALIDIDX=-1;
	
	menu[] mMenuList= null;
	private RelativeLayout mmain;
	
	private int getMiddleIdx(menu[] list){
		int idx=0, totalActive=0, upperLimit;
		
		for(int i=0;i<list.length;i++){
			if(list[i].mIsActive)
				totalActive++;
		}
		Log.i(TAG+".getMiddleIdx", "totalActive="+totalActive);
		if(totalActive%2==0)
			upperLimit = totalActive/2;
		else
			upperLimit = totalActive/2;
		Log.i(TAG+".getMiddleIdx", "upperLimit="+upperLimit);
		
		int i;
		for(i=0;i<list.length;i++){
			if(list[i].mIsActive){
				if(idx<upperLimit)
					idx++;
				else
					break;
			}
		}
		Log.i(TAG+".getMiddleIdx", "i="+i);
		return i;
	}
	
	private class DummyAnimationListener implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			Log.i(TAG+".DummyAnimationListener.onAnimationRepeat", "being called!");
		}

		@Override
		public void onAnimationStart(Animation animation) {
			Log.i(TAG+".DummyAnimationListener.onAnimationStart", "being called!");
		}
		
	}

	private class MenuButtonAnimationListener extends DummyAnimationListener{
		Button mButton;
		private MenuButtonAnimationListener(Button button){
			mButton = button;
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			Log.i(TAG+".MenuButtonAnimationListener.onAnimationEnd", "being called!");
			MainActivity.this.onButtonClicked(mButton);
		}
		
	}
	
	abstract class menu{
		String mMenuName;
		boolean mIsActive;
		Animation mAnimation;
		Button mButton;
		int mIconID;
		
		abstract void menucallback();
		public void setAnimation(Animation animation){
			mAnimation = animation;
		}
		public Animation getAnimation(){
			return mAnimation;
		}
		public Button getButton(){
			return mButton;
		}
		public void setButton(Button button){
			mButton = button;
		}
		menu(String name, boolean isActive, int iconID){
			mMenuName=name;
			mIsActive=isActive;
			mIconID = iconID;
			
			mAnimation=null;
		}
		menu(String name, boolean isActive){
			this(name, isActive, R.drawable.nulldrawable);
		}
		public int getIconID(){
			return mIconID;
		}
	}

	class exitmenu extends menu{
		exitmenu(String name, boolean isactive, int iconID) {
			super(name, isactive, iconID);
		}

		exitmenu(String name, boolean isactive) {
			this(name, isactive, R.drawable.nulldrawable);
		}
		void menucallback(){
			finish();
		}
	}
	
	class wapsmenu extends exitmenu{
		wapsmenu(String name, boolean isactive, int iconID) {
			super(name, isactive, iconID);
		}
		wapsmenu(String name, boolean isactive) {
			super(name, isactive);
		}
		void menucallback(){
			if(AdGlobals.getInstance().wapsAdSwitch)
				AdGlobals.getInstance().showWapsOffer(MainActivity.this);
		}		
	}
	
	class the9menu extends menu{
		Context mctx;
		
		the9menu(String name, boolean isActive, Context ctx, int iconID) {
			super(name, isActive, iconID);
			mctx = ctx;
		}
		the9menu(String name, boolean isActive, Context ctx) {
			this(name, isActive, ctx, R.drawable.nulldrawable);
		}
		
		void menucallback(){
			if(AdGlobals.getInstance().the9Switch)
				Dashboard.open();
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
	        .setMessage("作者：陈剑\n"+"电邮：whogiawho@gmail.com"+"\n"+"版权所有       2009-2011\n"+"本游戏遵循Apache2.0 License\n")
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

		normalmenu(String name, boolean isActive, Context ctx, String activityname, int iconID) {
			super(name, isActive, iconID);
			mctx=ctx;
			mactivityname=activityname;
		}

		normalmenu(String name, boolean isActive, Context ctx, String activityName) {
			this(name, isActive, ctx, activityName, R.drawable.nulldrawable);
		}

		void menucallback(){
	        Intent intent=new Intent();
	        //replace <dragon> with ur own activity class
	        intent.setClassName(mctx, mactivityname); 
	        startActivity(intent);  
	        overridePendingTransition(R.anim.hyperspace_in, R.anim.hyperspace_out);
		}
	}
	
	static Object[] getPredefinedNames(){
		ArrayList<String> supportedNamesList = new ArrayList<String>();
		for(int i=0;i<PREDEFINEDCNT;i++){
			supportedNamesList.add(Integer.toString(i));
		}

		return supportedNamesList.toArray(); 
	}
	
	void initCubeFiles(){
		String[] cubesArray = this.fileList();
		List<String> currentFilesList = Arrays.asList(cubesArray);
		Object[] supportedNames = getPredefinedNames();
		
		for(int i=0;i<supportedNames.length;i++){
			String name=(String) supportedNames[i];
			if(!currentFilesList.contains(name)){
				try {
					//create the file
					FileOutputStream fos;
					fos = this.openFileOutput(name, Context.MODE_PRIVATE);
					fos.close();
					//save origin cube to the file
					PlayRenderer.saveOriginToFile(this, name);
					
					//copy the icon files
					Globals.copyAssetFileTo(this, TubeApplication.OriginCubeIcon, name+TubeApplication.IconSuffix);
				} catch (FileNotFoundException e) {
					Log.e(TAG, "initCubeFiles(...): " + "FileNotFoundException");
				} catch (IOException e) {
					Log.e(TAG, "initCubeFiles(...): " + "IOException");				
				}
			}
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		int delta = 100;
		for(int i=0;i<mMenuList.length;i++){
			if(mMenuList[i].mIsActive){
				Animation menuSlideIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
				menuSlideIn.setStartOffset(i*delta);
				menuSlideIn.setDuration(200);
				mMenuList[i].getButton().startAnimation(menuSlideIn);				
			}
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String packageStr = this.getPackageName();
        Log.i(TAG+".onCreate", "package name = " + packageStr);
        Globals.init(packageStr);
        
        if(AdGlobals.getInstance().wapsAdSwitch)
        	AppConnect.getInstance(this); 
		AdGlobals.getInstance().init(Globals.YoumiID, Globals.YoumiPass, this);
        
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        if(getWorkingMode()==IWorkingMode.TB) {
        	//codes to support T&B
        	TB_SMS tbSMS = new TB_SMS(this, this, "000162", "101224", "00016201");
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

    private int getidx(View view){
    	if(view==null)
    		return INVALIDIDX;
    	Button button=(Button)view;
    	for(int i=0;i<mMenuList.length;i++){
    		if(mMenuList[i].mMenuName==button.getText()){
    			return i;
    		}
    	}
    	
    	return INVALIDIDX;
    }
    
	@Override
	public void onClick(View view) {
		//start button animation
		int idx=getidx(view);
		if(idx!=INVALIDIDX && idx<mMenuList.length){
			Animation animation = mMenuList[idx].getAnimation();
			view.startAnimation(animation);
		}
		else
			Log.e(TAG+"onClick", "invalid idx = "+idx);
	}
	
	private void onButtonClicked(View view){
    	int idx;
    	idx=getidx(view);
    	Log.i(TAG,"onClick(...): "+"idx="+idx);
    	if(idx!=INVALIDIDX && idx<mMenuList.length)
    		mMenuList[idx].menucallback();
    	else
			Log.e(TAG+"onClick", "invalid idx = "+idx);
	}
	
	public static Typeface getTypeface(Context context,String fontPath) {
		return Typeface.createFromAsset(context.getAssets(), fontPath); 
	}

	private void setButtonProperty(Button button, Typeface tf, int buttonIdx, int viewId){
		button.setTypeface(tf);  
		button.setBackgroundDrawable(null);
		button.setTextSize(getResources().getDisplayMetrics().density * 15);
		button.setId(viewId);
		button.setText(mMenuList[buttonIdx].mMenuName); 
		button.setOnClickListener(this);
		mMenuList[buttonIdx].setButton(button);
		
		XmlResourceParser xpp = getResources().getXml(R.color.text_button);
		ColorStateList colors;
		try {
			colors = ColorStateList.createFromXml(getResources(), xpp);
			button.setTextColor(colors);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.squash);
		MenuButtonAnimationListener listener = new MenuButtonAnimationListener(button);
		animation.setAnimationListener(listener);
		mMenuList[buttonIdx].setAnimation(animation);
	}
	
	private Button makeMenuButton(menu m, Context cxt){
		Button b = null;
		
		int iconID = m.getIconID();
		if(iconID!=R.drawable.nulldrawable){
			b = new IconButton(cxt, iconID);
		} else 
			b = new Button(cxt);
		
		return b;
	}
	
    //helper 1 to support T&B
    @SuppressWarnings("unused")
	@Override
    public void normalFlow(){
    	mMenuList = new menu[11];
    	mMenuList[0] = new normalmenu(getResources().getString(R.string.randomcube), true, this, "oms.cj.tube.RandomCube", R.drawable.newgame);
		mMenuList[1] = new wapsmenu(getResources().getString(R.string.freecoins), false, R.drawable.freecoins);
    	mMenuList[2] = new the9menu(getResources().getString(R.string.the9gamecenter), false, this, R.drawable.the9gc);
    	mMenuList[3] = new normalmenu(getResources().getString(R.string.origincube), false, this, "oms.cj.tube.OriginCube");
    	mMenuList[4] = new normalmenu(getResources().getString(R.string.cubesolver), true, this, "oms.cj.tubesolveractivity.TubeSolverActivity1", R.drawable.solver);
    	mMenuList[5] = new normalmenu(getResources().getString(R.string.mofangmiji), true, this, "oms.cj.tube.tutor.MoFangMiJiActivity", R.drawable.tubetutor);
    	mMenuList[6] = new normalmenu(getResources().getString(R.string.restore), true, this, "oms.cj.tube.RestoreCubeActivity", R.drawable.load);
    	mMenuList[7] = new normalmenu(getResources().getString(R.string.settings), true, this, "oms.cj.tube.Config", R.drawable.settings);
    	mMenuList[8] = new normalmenu(getResources().getString(R.string.thanks), true, this, "oms.cj.tube.Thanks", R.drawable.thanks);
    	mMenuList[9] = new aboutmenu(getResources().getString(R.string.about), false, this);
    	mMenuList[10] = new exitmenu(getResources().getString(R.string.exit), true, R.drawable.exit);
    	 
        setContentView(R.layout.main); 
        mmain=(RelativeLayout) findViewById(R.id.main);
		if(mmain==null)
			Log.e(TAG+".normalFlow", "mmain==null");
        
        String localePath = "default.ttf";
        
        //codes to set ur own typeface's file font path
		Log.i(TAG+".normalFlow", "current locale ="+Locale.getDefault());
		Log.i(TAG+".normalFlow", "localePath ="+localePath);
		
		Typeface tf = getTypeface(this, localePath);
		if(tf!=null){
			Log.i(TAG+"normalFlow", "tf =" + tf.toString());
		}
		
        //configure the middle button
        int middleIdx = this.getMiddleIdx(mMenuList);
        Log.i(TAG+"normalFlow", "middleIdx="+middleIdx);
		Button button = makeMenuButton(mMenuList[middleIdx], this);
		if(button==null)
			Log.e(TAG+".normalFlow", "button==null");
		RelativeLayout.LayoutParams lparam=new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		);
		lparam.addRule(RelativeLayout.CENTER_IN_PARENT);
		if(lparam==null)
			Log.e(TAG+".normalFlow", "lparam==null"); 

		mmain.addView(button, lparam);
		setButtonProperty(button, tf, middleIdx, R.id.middleButton);
		
		int middleId = button.getId();
		
		//configure buttons above middleIdx
		int aboveId = middleId;
		for(int i=middleIdx-1;i>=0;i--){
			if(mMenuList[i].mIsActive){
				button=makeMenuButton(mMenuList[i], this); 
				lparam=new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT
				);
				lparam.addRule(RelativeLayout.ABOVE, aboveId);
				lparam.addRule(RelativeLayout.CENTER_HORIZONTAL, aboveId);
				mmain.addView(button, lparam);
				aboveId--;
				setButtonProperty(button, tf, i, aboveId); 
			}
		}
		//configure buttons below middleIdx
		int belowId = middleId;
		for(int i=middleIdx+1;i<mMenuList.length;i++){
			if(mMenuList[i].mIsActive){
				button=makeMenuButton(mMenuList[i], this);
				lparam=new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT
				);
				lparam.addRule(RelativeLayout.BELOW, belowId);
				lparam.addRule(RelativeLayout.CENTER_HORIZONTAL, belowId);
				mmain.addView(button, lparam);
				belowId++;
				setButtonProperty(button, tf, i, belowId); 
			}
		}
        
        //mmain.setBackgroundResource(R.drawable.background);
		ImageView background = new ImageView(this); 
		background.setImageDrawable(getResources().getDrawable(R.drawable.background));
		background.setAlpha(100);
		lparam = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT
		); 
		this.addContentView(background, lparam);
        
        //call initCubeFiles() to generate the 10 files predefined if they does not exist
        initCubeFiles();
        
        AdGlobals.getInstance().adDynamicFlow(mmain, this, Gravity.BOTTOM|Gravity.LEFT);
    }

    //helper 2 to support T&B
	@Override
	public int getWorkingMode() {
		
		return IWorkingMode.NORMAL;
	} 
    
	@Override
	protected void onDestroy() {
		if(AdGlobals.getInstance().wapsAdSwitch)
			AppConnect.getInstance(this).finalize();
		// 关闭 渐入式 广告
		ImpressionAdView.close();
		
		super.onDestroy();

	}
}