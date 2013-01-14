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

/* 						change history
 * 2009/12/22      improve ball's distributing rate to be constant
 * 2009/12/22      move distributenextballs() from onClick() to removeballlistener.onAnimationEnd()
 *                 which means that distributenextballs()'s animation only happened when removing 
 *                 ball animation is complete     
 * 2009/12/23      add the feature to save/restore balance configuration
 *                 level/debug/style    
 * 2009/12/23      move the "记录清0" button to the activity records
 *                 add a new button "设置", and its activity configapp 
 * 2009/12/27      improve nextballsout animation from using 2 TranslateAnimation into 1 
 * 				   MultiPointTranslation
 * 2009/12/27      Bug fix: clickable is not possible if 2 or 3 identical nextballs falled into one
 *                 line, and these balls match condition to be removed from chess
 * 2009/12/28      Complete the whole level coding, include easydistribute() and harddistribute()  
 * 2009/12/28      Add the astro style
 * 2009/12/29      Add 3 kinds of game background pics, which are matrix, bluesky and normal
 *                 Ability to select them is also added
 * 2009/12/29      Do not fill Rect anymore in MyTableRow; 
 *                 Remove 2 classes, ThinBottomPreTableRow and PreTableRow
 * 2009/12/29      Refine the method to draw the borders in MyTableRow and game; normally each cell's
 *                 padding is bordersize/2, but is bordersize for the margin borders
 * 2009/12/29      Remove 5 obsolete res/drawable/*bak.png files
 * 2009/12/29      Remove imagesize variable from MyTableRow.java, this value can be accessed from 
 *                 a plate object  
 * 2009/12/29      Add support for both HVGA and WVGA screen         
 * 2010/01/05      2 bug fixes: 
 *                 a. if a move caused balls to be removed from board, that ball of the location will be
 *                    displayed with a cursor, then its animation object is a ball with a cursor, which 
 *                    is not expected 
 *                 b. sometimes a distribution animation will show a ball object with a cursor moving, 
 *                    which is not expected
 *
 */

package oms.cj.balance;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.graphics.*;
import java.util.*;
import android.util.*;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import java.lang.Math;
import oms.cj.ads.AdGlobals;
import com.openfeint.api.OpenFeint;
import com.openfeint.api.resource.Leaderboard;
import com.openfeint.api.resource.Score;
import com.openfeint.api.resource.User;


public class game extends Activity implements View.OnClickListener, DialogInterface.OnClickListener, OnTouchListener{
    /** Called when the activity is first created. */
	public static int WC=TableLayout.LayoutParams.WRAP_CONTENT;
	
	private FrameLayout mgamelayout;
	private TableLayout mplatelayout;
	private TextView mscoreview;
	private MyImageView mMovingImage;
	private plate mplate;
	private static final int SCORESONEBALL=5;
	private Integer mscore;

	private int mlevel;
	private int mdebug;
	private int mstyle;
	private int mbackground;
	private int mdragtype;

	//animation duration definition
	private static final int REMOVEBALLDURATION=500;		//符合消球条件的动画时长
	private static final int NEXTBALLOUTDURATION=250;		//nextball进来动画时长
	private static final int NEXTBALLINDURATION=250;        //nextball出去动画时长
	//在函数showdistributionanimation(mi,mdistributioncount,mtarget)中计算第mi个移动的nextball至mtarget的动画时长
	
	private static final int NEXTBALLROW=0;
	private static final int GOLDLINEROW=1;
	private static final AccelerateInterpolator RemoveBallInterpolator=new AccelerateInterpolator(1.5f);
	
	private static final String TAG = "game";
    private static final String PREFS_NAME = configapp.PREFS_NAME;
    public static final int ON=R.id.debugon;
    public static final int OFF=R.id.debugoff;
    public static final int TOPLEFT = R.id.topleft;
    public static final int TOPRIGHT = R.id.topright;
    public static final int MOVEOFF = R.id.moveoff;
    
    public static final int bordersize=2;		//bordersize must be even
    private static final int goldlineres[]={R.drawable.hvga_goldline, R.drawable.wvga_goldline};
    private static final int delta[][]={
    	{2, 120},  //for hvga
    	{10, 200}		//for wvga
    };
    
	class nextballoutlistener implements AnimationListener{
		private int mcount;  //distribute order of this nextball
		private Hashtable<Integer,Point> mpairs;
		private Point mnewcursor;
		
		nextballoutlistener(Hashtable<Integer,Point> pairs, Point newcursor){
			mcount=0;
			mpairs=pairs;
			mnewcursor=newcursor;
    		setclickable(false);
    		setAnimationOn(true);
		}
		
	    public void onAnimationStart(Animation animation){	    	
	    }
	    
	    public void onAnimationEnd(Animation animation){	 
	    	mcount++;
	    	int distributioncount=mpairs.size();
			
	    	if(mcount==mpairs.size()){    //the last distribute order of this nextball
	    		Log.i(TAG,"nextballoutlistener.onAnimationEnd(...): "+"setclickable(true)");
	    		setclickable(true);
			
	    		for(int i=0;i<distributioncount;i++){
	    			Point target=(Point)mpairs.get(i);
	    			//对点target做检查来确定这次distribution是否有球被消掉
	    			checkandremoveballs(target, false);
	    			//曾经怀疑该行(checkandremoveballs)是可能导致线程冲突的代码，后来发现动画均有同一UI线程完成，所以不可能冲突
	    		}
	    		
	    		//检查棋盘是否已满，满则结束
	    		if(mplate.isfull())
	    			handlefull();
	    		
				//显示下一个在位置i的nextball
	    		for(int i=0;i<distributioncount;i++){
	    			int j=getindexfromdistorder(i);
	    			mplate.gennextballs(j);
	    			shownextball(j);
	    			shownextballanimationin(j);
	    		}
	    		
	    		//显示新位置光标；
		    	mplate.setcursor(mnewcursor);
		    	showball(mnewcursor);
		    	
		    	setAnimationOn(false);
	    	}
	    }
	    
	    public void onAnimationRepeat(Animation animation){
	    }			
	}
	
	private void shownextballanimationin(int i){
		TableRow row=(TableRow)mplatelayout.getChildAt(NEXTBALLROW);
		ImageView nextballiv=(ImageView)row.getChildAt(i);
		int startPoint[]=new int[2];
		int endPoint[]=new int[2];
		
		nextballiv.getLocationOnScreen(endPoint);
		row.getLocationOnScreen(startPoint);

        Animation animation = new TranslateAnimation(
          	  Animation.ABSOLUTE, -endPoint[0]+startPoint[0], Animation.ABSOLUTE, 0.0f,
          	  Animation.ABSOLUTE, -endPoint[1]+startPoint[1], Animation.ABSOLUTE, 0.0f
        	  );

        animation.setDuration(NEXTBALLINDURATION); 
        nextballiv.startAnimation(animation); 
	}
	
	private void createtemplate(){
		int i,j;
		TableRow row;
		ImageView image;
		TextView tv;
		TableRow.LayoutParams rlparam;
		
		row=new TableRow(this);
		row.setClipChildren(false);
		mplatelayout.addView(row, new TableLayout.LayoutParams(WC,WC));  //添加mplatelayout的第一个儿子
		for(i=0;i<mplate.getmaxnextballs();i++){
			image=new ImageView(this);
			image.setPadding(1,1,1,1);
			row.addView(image);
		}
		
		tv=new TextView(this);
		row.addView(tv, new TableRow.LayoutParams(WC,WC));
		rlparam=(TableRow.LayoutParams)tv.getLayoutParams();
		rlparam.gravity=Gravity.RIGHT;
		rlparam.column=3;
		rlparam.span=7;
		tv.setTextSize(20);
		tv.setTypeface(Typeface.create("", Typeface.BOLD_ITALIC));
		tv.setTextColor(Color.YELLOW);
		mscoreview=tv;
		
		//add the goldline below nextballs
		row=new TableRow(this);
		row.setClipChildren(false);
		row.setPadding(0, 0, 0, 3);
		mplatelayout.addView(row, new TableLayout.LayoutParams(WC,WC));   //添加mplatelayout的第二个儿子
		image=new ImageView(this);
		row.addView(image, new TableRow.LayoutParams(WC,WC));
		rlparam=(TableRow.LayoutParams)image.getLayoutParams();
		rlparam.column=0;
		rlparam.span=3;	
		
		//mplatelayout.setBackgroundColor(Color.BLACK);
        for(i=0;i<plate.getheight();i++){
        	row=new MyTableRow(this, i);
        	row.setClipChildren(false);
        	Log.i(TAG,"createtemplate(): "+"drawingcacheenable="+row.isDrawingCacheEnabled());
        	
        	mplatelayout.addView(row, new TableLayout.LayoutParams(WC,WC));

        	for(j=0;j<plate.getwidth();j++){
            	int left=bordersize/2, right=bordersize/2, top=bordersize/2, bottom=bordersize/2;
        		image=new ImageView(this);
        		if(j==0)
        			left=bordersize;
        		if(j==plate.getwidth()-1)
        			right=bordersize;
        		if(i==0)
        			top=bordersize;
        		if(i==plate.getheight()-1)
        			bottom=bordersize;
        		image.setPadding(left, top, right, bottom);
        		image.setOnClickListener(this); 
        		//onTouch listener is only active when drag&drop is active
        		if(mdragtype==game.TOPLEFT||mdragtype==game.TOPRIGHT)
        			image.setOnTouchListener(this); 
        		TableRow.LayoutParams iParams = new TableRow.LayoutParams(WC, WC);
        		image.setLayoutParams(iParams); 
        		
        		row.addView(image);
        	}
        }				
	}
		
    private int remapball(int i){
    	return i+2;
    }
    private int reverseremapball(int i){
    	return i-2;
    }
    
	private void show(){
		int i,j;
		TableRow row;
		ImageView image;
		TextView tv;
		
		//show balls being sent to plate, and the score
		row=(TableRow)mplatelayout.getChildAt(NEXTBALLROW);
		Log.i(TAG,"show(): "+"row[0]_child_count="+row.getChildCount());
		for(i=0;i<mplate.getmaxnextballs();i++){
			image=(ImageView)row.getChildAt(i);
			Log.i(TAG,"show(): "+"i="+i);
			Log.i(TAG,"show(): "+"mplate.nextballres(i)="+mplate.nextballres(i));
			Log.i(TAG,"show(): "+"image="+image.toString());
			image.setImageDrawable(getResources().getDrawable(mplate.nextballres(i)));
		}
		tv=(TextView)row.getChildAt(mplate.getmaxnextballs());
		tv.setText(mscore.toString());
		//show the goldline
		row=(TableRow)mplatelayout.getChildAt(GOLDLINEROW);
		image=(ImageView)row.getChildAt(0);
		image.setImageDrawable(getResources().getDrawable(goldlineres[plate.getscmode()]));
		//show ball in plate
        for(i=0;i<plate.getheight();i++){
        	row=(TableRow)mplatelayout.getChildAt(remapball(i));
        	for(j=0;j<plate.getwidth();j++){
        		image=(ImageView)row.getChildAt(j);
        		image.setImageDrawable(getResources().getDrawable(mplate.ballres(i,j)));
        	}
        }	
	}

	private void shownextball(int i){
		TableRow row;
		ImageView iv;
		
		row=(TableRow)mplatelayout.getChildAt(NEXTBALLROW);
		iv=(ImageView)row.getChildAt(i);
		iv.setImageDrawable(getResources().getDrawable(mplate.nextballres(i)));
	}
	
	/*
	private void showallnextballs(){
		int i;
		
		for(i=0;i<mplate.getmaxnextballs();i++){
			shownextball(i);
		}
	}*/
	
	private void setclickable(boolean clickable){
		int i,j;
		
		for(i=0;i<plate.getheight();i++)
			for(j=0;j<plate.getwidth();j++)
				getviewbylocation(new Point(i,j)).setClickable(clickable);
	}
	
	// given a p(x,y), 0<=x<plate.getheight, 0<=y<plate.getwidth 
	// getScreenXY return (x,y) relative to R.id.gamelayout 
	private Point getScreenXY(Point p){
		Point q=null;
		
		if(p.x>=0&&p.x<plate.getheight()&&p.y>=0&&p.y<plate.getwidth()){
			View v3rd = mplatelayout.getChildAt(2);
			int v3rdX = v3rd.getLeft();
			int v3rdY = v3rd.getTop();
			//String out = String.format("v3rdX=%d, v3rdY=%d", v3rdX, v3rdY);
			//Log.i(TAG+".getScreenXY", out);
			
			int cellSize = plate.getimagesize()+bordersize;
			int relativeX, relativeY;
			if(p.x==0)
				relativeY=0;
			else
				relativeY=cellSize+1+(p.x-1)*cellSize;
			if(p.y==0)
				relativeX=0;
			else 
				relativeX=cellSize+1+(p.y-1)*cellSize;
			int targetX = v3rdX+relativeX;
			int targetY = v3rdY+relativeY;
			q = new Point(targetX, targetY);
			
			//out = String.format("targetX=%d, targetY=%d", targetX, targetY);
			//Log.i(TAG+".getScreenXY", out);
		}
		
		return q;
	}
	
	private Point map2ScreenXY(View v, MotionEvent event){
		Point p = getviewlocation(v), q=null;
		
		q = getScreenXY(p);
		if(q!=null){
			q.x = (int) (q.x + event.getX());
			q.y = (int) (q.y + event.getY());	

		}

		return q;
	}
	
	private Point map2Location(View v, MotionEvent event){
		Point p = getviewlocation(v), q=null;
		float X = event.getX();
		float Y = event.getY();
		
		//int parentIdx = this.remapball(p.x);
		//View parent = mplatelayout.getChildAt(parentIdx);		
		//String out = String.format("the (%dth) child", parentIdx);
		//Log.i(TAG+".mapXYOfParent2Location", out);
		//out = String.format("parent: x=%d, y=%d", parent.getLeft(), parent.getTop());
		//Log.i(TAG+".mapXYOfParent2Location", out);
		//out = String.format("X=%f, Y=%f", X, Y);
		//Log.i(TAG+".mapXYOfParent2Location", out);
		
		int cellSize = plate.getimagesize()+bordersize;
		float relativeX = X/cellSize;
		float relativeY = Y/cellSize;
		int targetX, targetY;
		if(X>=0)	//p.y means column coord
			targetX = p.y + (int)relativeX;
		else
			targetX = p.y + (int)relativeX - 1;
		if(Y>=0)	//p.x means row cocord
			targetY = p.x + (int)relativeY;
		else
			targetY = p.x + (int)relativeY - 1;
		
		//now targetX means column coord, and targetY means row coord
		if(targetX>=0 && targetX<plate.getwidth() && targetY>=0 && targetY<plate.getheight()){
			q = new Point(targetY, targetX);
			Log.i(TAG+".map2Location", q.toString());
		}
		
		return q;
	}
	
	private ImageView getviewbylocation(Point p){
		TableRow row;
		ImageView iv;
		
		row=(TableRow)mplatelayout.getChildAt(remapball(p.x));
		iv=(ImageView)row.getChildAt(p.y);
		
		return iv;
	}
	
	private Point getviewlocation(View view){
		Point p=new Point(-1,-1);
		int rows=mplatelayout.getChildCount();
		int i,j;
		TableRow row;
		
		for(i=0;i<rows;i++){
			row=(TableRow)mplatelayout.getChildAt(i);
			j=row.indexOfChild(view);
			if(j==-1)
				continue;
			else{
				p.x=reverseremapball(i);
				p.y=j;
				break;
			}
		}
		
		return p;
	}
	
	private int decidemode(int width, int height){
		int min=Math.min(width, height);
		
		if(min<480)
			return plate.IMAGEMODEHVGA;
		if(min<640)
			return plate.IMAGEMODEWVGA;
		
		//default return HVGA
		return plate.IMAGEMODEHVGA;
	}

	//0-left
	//1-top
	//2-right
	private void getpadding(int width, int height, int[] paddings){
		Log.i(TAG, "getpadding(...): " + "imagesize=" + plate.getimagesize());
		
		paddings[0]=(width - (plate.getimagesize()+bordersize) * plate.getwidth() - delta[plate.getscmode()][0])/2;
		paddings[1]=(height - (plate.getimagesize()+bordersize) * plate.getheight() - delta[plate.getscmode()][1] )/2;
		paddings[2]=paddings[0];
		/*
        paddings[0]=(width-260)/2;
        paddings[1]=(height-260)/2;
        paddings[2]=paddings[0];
		*/
	}

    @Override
    public void onResume(){
    	super.onResume();
    	
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {  	
        super.onCreate(savedInstanceState);
        
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.game, AdGlobals.getInstance().getAdInterface());
        setContentView(view);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Log.i(TAG, "display.height="+display.getHeight());
        Log.i(TAG, "display.width="+display.getWidth());        
        int scwidth = display.getWidth();
        int scheight = display.getHeight();
        
        int screenmode=decidemode(scwidth, scheight);
        plate.setscmode(screenmode);

        int[] paddings={0,0,0};
        getpadding(scwidth, scheight, paddings);
        Log.i(TAG,"onCreate(...): "+"left="+paddings[0]);
        Log.i(TAG,"onCreate(...): "+"right="+paddings[1]);
        Log.i(TAG,"onCreate(...): "+"top="+paddings[2]);
		
        mplatelayout=(TableLayout) findViewById(R.id.balancelayout);
        mplatelayout.setPadding(paddings[0], paddings[1], paddings[2], 0);
		
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mlevel = settings.getInt(configapp.ref[0], plate.MEDIUM); 
        mdebug = settings.getInt(configapp.ref[1], OFF);
        mstyle = settings.getInt(configapp.ref[2], plate.BALLSTYLE);
        mbackground = settings.getInt(configapp.ref[3], R.drawable.base1);
        mdragtype = settings.getInt(configapp.ref[4], TOPRIGHT);

        //set the game's background
        mgamelayout=(FrameLayout)findViewById(R.id.gamelayout);
        mgamelayout.setBackgroundResource(mbackground);
        
        mMovingImage=(MyImageView)findViewById(R.id.movingimage);
        
        mscore=0;
        
        mplate=new plate(mstyle);
        createtemplate();
        if(mdebug==ON)
        	debugspecificscenario();
        show();
        Log.i(TAG,"onCreate(...): "+"tid="+android.os.Process.myTid());
    }
    
    private void debugspecificscenario(){
/*    	
    	int externalboard[][]={
    			{3,4,3,2,4,3,3,1,3,3},
    			{4,4,3,2,2,2,3,2,4,1},	
    			{3,4,3,4,1,1,4,3,4,1},	
    			{4,0,1,3,2,1,4,3,4,1},	
    			{3,0,4,4,2,2,1,1,4,3},	
    			{3,4,3,1,2,1,3,4,0,4},	
    			{2,4,1,3,4,4,2,2,1,4},	
    			{2,3,2,1,1,3,4,2,4,3},	
    			{3,3,4,3,3,4,1,3,3,4},	
    			{4,2,1,3,3,1,1,4,4,2}	
    	};
*/
    	int externalboard[][]={
    			{3,4,3,2,4,3,3,1,3,3},
    			{4,3,3,2,2,2,3,2,4,1},	
    			{3,4,3,4,1,1,4,3,4,1},	
    			{4,0,1,3,2,1,4,3,3,1},	
    			{3,0,4,4,2,2,1,1,4,3},	
    			{3,4,3,1,2,1,3,4,0,4},	
    			{2,3,1,3,4,4,2,2,1,4},	
    			{2,3,2,1,1,3,4,2,4,3},	
    			{3,3,4,3,3,4,1,3,3,4},	
    			{4,2,1,3,3,1,1,4,4,2}	
    	};

    	int externalnextballs[]={
    			4,4,4
    	};

    	mplate.setboard(externalboard);
        mplate.setnextballs(externalnextballs);	
    }
    
    public void onClick(DialogInterface dialog, int whichButton){
    	AlertDialog dlg=(AlertDialog)dialog;
    	EditText et;
    	String playername;
    	String anchorite = getString(R.string.noname);
/*    	
    	et=(EditText)mdlglayout.getChildAt(1);
       	playername=et.getText().toString();    
       	Log.i(TAG,"onclick(dialog,whichbutton): "+"edittext's id"+et.getId());
       	Log.i(TAG,"onclick(dialog.whichbutton): "+"et.getText()="+playername);
*/       	
       	et=(EditText)dlg.findViewById(R.id.username_edit);
       	playername=et.getText().toString();
       	if(playername.equals("")||playername.matches("[ \t]+"))
       		playername=anchorite;
       	Log.i(TAG,"onclick(dialog,whichbutton): " + "from dialog text=" + et.getText().toString());
    	
    	String currentLevelFile = getCurrentLevelFile();
       	recordfile recfile=new recordfile(this, currentLevelFile);
       	recfile.getrecords();
    	Integer idx=recfile.findrecordlocation(mscore);
    	Log.i(TAG,"onclick(dialog,whichbutton): "+"idx="+idx);
    	Map<String,String> m=new HashMap<String,String>();
		Integer order=idx+1;
		m.put(recordfile.col[0], order.toString());
		m.put(recordfile.col[1], playername);
		m.put(recordfile.col[2], mscore.toString());
		recfile.mrecords.add(idx, m);
		recfile.mrecords.remove(recfile.mrecords.size()-1);
		for(int i=idx+1;i<recfile.mrecords.size();i++){
			m=recfile.mrecords.get(i);
			Integer j=new Integer(m.get(recordfile.col[0]));
			j=j+1;
			m.put(recordfile.col[0], j.toString());
		}
		Log.i(TAG,"onclick(dialog,whichbutton): "+"mrecords="+recfile.mrecords.toString());
		recfile.saverecords();
		
		if(AdGlobals.getInstance().the9Switch)
			readScoreFromThe9(mscore);
    }
    
    private void getplayername(){
        LayoutInflater factory = LayoutInflater.from(this);
        View dlglayout = factory.inflate(R.layout.inputname, null);
        String title = this.getString(R.string.first5record);
        
        AlertDialog dlg = new AlertDialog.Builder(this)
        .setTitle(title)
        .setView(dlglayout)
        .setPositiveButton("OK", this)
        .create();
        dlg.show();
    }
    
    private String getCurrentLevelFile(){    	
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int level = settings.getInt(configapp.ref[0], plate.MEDIUM); 
        
        return records.getLevelFile(level);
    }
    
    public void handlefull(){
    	String currentLevelFile = getCurrentLevelFile();
    	recordfile recfile=new recordfile(this, currentLevelFile);
    	Integer idx;
    	
    	recfile.getrecords();
    	//mscore是否可以放入高分中
    	idx=recfile.findrecordlocation(mscore);
    	Log.i(TAG,"handlefull(): "+"mrecords.size()="+recfile.mrecords.size());
    	if(idx<recfile.mrecords.size()){
    		Log.i(TAG,"handlefull(): "+"calling getplayername()");
        	getplayername();
    	} else {
    		localShowGameOverDialog();
    	}
    }
    
    private class BalanceSubmitToCB extends Score.SubmitToCB {
    	private Score mScore;
    	
    	BalanceSubmitToCB(Score score){
    		mScore = score;
    	}
		private final void finishUp(int resultCode) {
			// sweet, pop the thingerydingery
			game.this.setResult(resultCode);
			game.this.finish();
		}
		
		@Override public void onSuccess(boolean newHighScore) {
			Toast.makeText(game.this, "Score posted.", Toast.LENGTH_SHORT).show();
			if (mScore.blob == null) finishUp(Activity.RESULT_OK);
		}

		@Override public void onFailure(String exceptionMessage) {
			Toast.makeText(game.this, "Error (" + exceptionMessage + ") posting score.", Toast.LENGTH_SHORT).show();
			finishUp(Activity.RESULT_CANCELED);
		}
		
		@Override public void onBlobUploadSuccess() {
			Toast.makeText(game.this, "Blob uploaded.", Toast.LENGTH_SHORT).show();
			finishUp(Activity.RESULT_OK);
		}
		
		@Override public void onBlobUploadFailure(String exceptionMessage) {
			Toast.makeText(game.this, "Error (" + exceptionMessage + ") uploading blob.", Toast.LENGTH_SHORT).show();
			finishUp(Activity.RESULT_CANCELED);
		}
    	
    }
    
    private class BalanceGetUserScoreCB extends Leaderboard.GetUserScoreCB{
    	private int mScore;
    	
    	BalanceGetUserScoreCB(int score){
    		mScore = score;
    	}
    	
		@Override
		public void onSuccess(Score score) {
			if(score==null){
				Log.i(TAG+".BalanceGetUserScoreCB.onSuccess", "scores==null!");
				submitScoreToThe9(mScore);
			} else {
				Log.i(TAG+".BalanceGetUserScoreCB.onSuccess", "s1="+score.score);
				if(score.score<mScore)
					submitScoreToThe9(mScore);				
			}
		}
    	
		@Override
		public void onFailure(String exceptionMessage) {
			String out = String.format("Error (%s) reading score.", exceptionMessage);
			Toast t = Toast.makeText(game.this, out, Toast.LENGTH_SHORT);
			t.show();
		}
    }
    
	private void submitScoreToThe9(int score){
    	BalanceApplication app = (BalanceApplication) getApplication();
    	String leaderboardID = app.getLeaderboardID(mlevel);
		Leaderboard l = new Leaderboard(leaderboardID);
    	if(leaderboardID!=null){
    		final Score s2 = new Score(score);
    		BalanceSubmitToCB cb = new BalanceSubmitToCB(s2);
			s2.submitTo(l, cb);
    	} else {
    		String out = String.format("%s", "invalid leaderboardID");
    		throw new IllegalStateException(out); 
    	}		    		
	}
	
	//submit score to the9
    private void readScoreFromThe9(int score){
    	BalanceApplication app = (BalanceApplication) getApplication();
    	String leaderboardID = app.getLeaderboardID(mlevel);
		Leaderboard l = new Leaderboard(leaderboardID);
	    BalanceGetUserScoreCB cb = new BalanceGetUserScoreCB(score);
	    //read score from the9; real submit will be done at cb.onSuccess() 
		User me = OpenFeint.getCurrentUser();
		l.getUserScore(me, cb);
    }
    
    private void localShowGameOverDialog(){
		//显示游戏结束，并返回主界面
    	String title = getString(R.string.submit2the9);
    	
        AlertDialog dlg = new AlertDialog.Builder(this)
        .setTitle(title)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	if(AdGlobals.getInstance().the9Switch)
            		readScoreFromThe9(mscore);
            }
        })
        .create();
        dlg.show();    	
    }

    private int getwidthofallnextballs(){
    	return (plate.getimagesize()+bordersize)*mplate.getmaxnextballs();
    }
    
    private void shownextballoutanimation(Hashtable<Integer,Point> pairs, Point newcursor){
		TableRow row=(TableRow)mplatelayout.getChildAt(NEXTBALLROW);
		int distributioncount=pairs.size();
		nextballoutlistener listener=new nextballoutlistener(pairs, newcursor);
			
		for(int i=0;i<distributioncount;i++){
			int j=getindexfromdistorder(i);
			ImageView iv=(ImageView)row.getChildAt(j);
			Point target=(Point)pairs.get(i);
			ImageView tv=(ImageView)getviewbylocation(target);
			Log.i(TAG,"shownextballanimationout(...): "+"iv="+iv.toString());
			
			//确定移动线路上的三个点
			ArrayList<Point> ptlist=new ArrayList<Point>();
	    	int[] endPoint=new int[2];
	    	int[] startPoint=new int[2];
	    	tv.getLocationOnScreen(endPoint);
			iv.getLocationOnScreen(startPoint);
			ptlist.add(new Point(-endPoint[0]+startPoint[0],-endPoint[1]+startPoint[1]));
			startPoint[0]+=getwidthofallnextballs();
			ptlist.add(new Point(-endPoint[0]+startPoint[0],-endPoint[1]+startPoint[1]));
			ptlist.add(new Point(0,0));
		
			//确定动画类，及其播放时间和AnimationListener接口
	        Animation animation = new MultiPointTranslation(ptlist);	
	        Log.i(TAG,"shownextballoutanimation(...): "+"calduration="+calduration(startPoint, endPoint));
	        animation.setDuration(NEXTBALLOUTDURATION+calduration(startPoint, endPoint));
	        animation.setInterpolator(new LinearInterpolator());
			animation.setAnimationListener(listener);
			tv.startAnimation(animation);
		}		
    }
    
    /*
    private int calduration(ImageView start, ImageView end){
    	double distance=caldistance(start,end);
    	return calduration(distance);
    }*/
    
    private int calduration(int[] start, int[] end){
    	double distance=caldistance(start, end);
    	return calduration(distance);
    }
    
    private int calduration(double distance){
    	double speed=0.2;
    	return (int)(distance/speed);
    }

    private double caldistance(int[] start, int[] end){
    	double distance=0.0f;
    	distance=Math.hypot(end[0]-start[0], end[1]-start[1]);
    	return distance;
    }
    
    /*
    private double caldistance(ImageView start, ImageView end){
    	double distance=0.0f;
    	int[] startCoor=new int[2];
    	int[] endCoor=new int[2];
    	
    	start.getLocationOnScreen(startCoor);
    	end.getLocationOnScreen(endCoor);
    	distance=caldistance(startCoor, endCoor);
    	return distance;
    }*/
        
    class removeballlistener implements AnimationListener{
    	Point mp;
    	Point mq;
    	boolean mmanualmove;
    	int morder;
    	int mtotalpoints;
    	
		removeballlistener(Point p, Point q, boolean manualmove, int order, int total){
			mp=p;
			mq=q;
			mmanualmove=manualmove;
			morder=order;
			mtotalpoints=total;
			setAnimationOn(true);
		}
	    public void onAnimationStart(Animation animation){
	    }
	    public void onAnimationEnd(Animation animation){
		    showball(mq);   //动画结束，更新该点mp
		    
		    //如果满足两个条件：
		    //   1. 手工移动
		    //   2. 最后一个球被消动画播放完毕
		    //就要做一件事情
		    //   1. 分配nextballs	    
		    if(mmanualmove && morder==mtotalpoints-1){
		    	Log.i(TAG,"removeballlistener.onAnimationEnd(...): "+"mp="+mp.toString());
		    	distributenextballs(mlevel,mp);
		    }
		    if(morder==mtotalpoints-1){
		    	setAnimationOn(false);
		    }
	    }
	    public void onAnimationRepeat(Animation animation){
	    }			
    }
    
    //检查实棋盘是否有球被消掉，有则动画放映
    private void checkandremoveballs(Point p, boolean manualmove){
		ArrayList<Point> removedpoints=new ArrayList<Point>();
		int score=0;
		int totalremovedpoints;
		
		mplate.check(p, removedpoints);
		totalremovedpoints=removedpoints.size();
		Log.i(TAG,"checkandremoveballs(...) "+"removedpoints.size()="+removedpoints.size());
		if(totalremovedpoints==0 && manualmove) {  
			//如果满足两个条件：
			//   1. 没有球可以移除，
			//   2. 手工移动
			//就要做一件事
			//   1. 分配nextballs	
			distributenextballs(mlevel, p);
		}
		else {  
			//否则就是三种情况之一：
			//   1. 非手工移动 && 无球可移           
			//      此种情况时，不进入下面的for循环，相当于啥都没做
			//   2. 非手工移动 && 有球可移
			//   3. 手工移动 && 有球可移
			if(totalremovedpoints!=0){	//如果是后两种情况
				for(int i=0;i<totalremovedpoints;i++){
					Point q=removedpoints.get(i);
					ImageView iv=getviewbylocation(q);
		
					Animation scale=new ScaleAnimation(1.0f, 0.0f,
					   1.0f, 0.0f,
					   Animation.RELATIVE_TO_SELF, 0.5f,
					   Animation.RELATIVE_TO_SELF, 0.5f);
					scale.setDuration(REMOVEBALLDURATION);
					scale.setAnimationListener(new removeballlistener(p,q,manualmove,i,totalremovedpoints));
					scale.setInterpolator(RemoveBallInterpolator);
					iv.startAnimation(scale);
			
					score=score+SCORESONEBALL;
				}
				mscore=mscore+score;
				mscoreview.setText(mscore.toString());
			}
		}
    }
        
    private int getindexfromdistorder(int i){
    	return mplate.getmaxnextballs()-1-i;
    }
    
    private void distributenextballs(int level, Point newcursor){
    	Point q;
    	int ndistributedballs=0;
    	Hashtable<Integer, Point> pairs=new Hashtable<Integer, Point>();
    	
    	ndistributedballs=mplate.getavpositionscount();
    	if(ndistributedballs>mplate.getmaxnextballs())
    		ndistributedballs=mplate.getmaxnextballs();

    	//原来的nextballs被分配到board上
		for(int i=0;i<ndistributedballs;i++){
			int j=getindexfromdistorder(i);
			q=mplate.distribute(mplate.nextball(j),level);
			Log.i(TAG,"onclick(): "+q.toString());
			
			//显示移动的最终结果
			showball(q); 
			//抹掉nextballs处的小球，将它从mnextlayout中移走
			mplate.removenextballs(j);
			shownextball(j);

			//将nextball[i]和目标q加入pairs
			pairs.put(i, q);  
		}
		
		//动画显示这个分配过程,动作分两步：先是横移,然后移动到棋盘正确位置
		shownextballoutanimation(pairs, newcursor);
    }
    
    private void showball(Point p){
    	ImageView iv;
    	
    	iv=getviewbylocation(p);
    	iv.setImageDrawable(getResources().getDrawable(mplate.ballres(p)));
    }
    
    private void updatecursor(Point oldcursor, Point newcursor, boolean newcursoron){
    	if(newcursoron==true)
    		mplate.setcursor(newcursor);
    	else
    		mplate.offcursor();
		showball(oldcursor);
		showball(newcursor);    	
    }
    
    private void moveBall(Point oldcursor, Point newcursor){
		ArrayList<Point> plist=new ArrayList<Point>();
		String msg = getString(R.string.illegalmove);
		String title = getString(R.string.reminder);
		
		mplate.getconnectedpoints(newcursor, plist);
		if(plist.indexOf(oldcursor)!=-1){
			mplate.moveball(oldcursor, newcursor);
			updatecursor(oldcursor, newcursor, false);
			checkandremoveballs(newcursor, true);
		} else {
	    	Dialog dialog=new AlertDialog.Builder(this)
        		.setTitle(title)
        		.setMessage(msg)
        		.setPositiveButton("OK", new DialogInterface.OnClickListener(){
        		public void onClick(DialogInterface dialog, int whichButton){
        			return;
        		}
        	})
        	.create();
	    	dialog.show();
	    	
	    	//仅是光标变化了，所以更新oldcursor和newcursor
	    	updatecursor(oldcursor, newcursor, true);    		
	    }
    }
    
    public void onClick(View v){
    	Log.i(TAG+".onClick", "being called!");
    	
    	Point newcursor,oldcursor;
    	oldcursor=mplate.getcursor();
    	newcursor=getviewlocation(v);
    	    	
    	if(mplate.ball(oldcursor)!=plate.NULL && mplate.ball(newcursor)==plate.NULL){
    		moveBall(oldcursor, newcursor);
    	} else if(mplate.ball(oldcursor)==plate.NULL && mplate.ball(newcursor)==plate.NULL){
    		//仅是光标变化了，所以更新oldcursor和newcursor
    		updatecursor(oldcursor, newcursor, true);
    	} else {
    		//2011/06/29 - comment1 
    		//in fact, sometimes we still can receive onClick after onTouch(even return true when handling ACTION_DOWN event)
    		//so "throw new IllegalStateException(TAG+".onClick: "+"should not arrive here!");" 
    		//is discarded, and just print info here
    		//2001/06/29 - comment2
    		//comment1's answer lied in this: 
    		//   we need to return true when handling ACTION_UP

    		if(mdragtype==game.MOVEOFF)	
    			//we still need to handle the other 2 scenarios here when drag&drop is off
    			updatecursor(oldcursor, newcursor, true);
    		else {
        		Log.e(TAG+".onClick", "mplate.ball(oldcursor)="+mplate.ball(oldcursor));
        		Log.e(TAG+".onClick", "mplate.ball(newcursor)="+mplate.ball(newcursor));
    		}
    	}
    } 
    
	public boolean onTouch(View v, MotionEvent event) { 	
    	
    	switch(event.getAction()){
    	case MotionEvent.ACTION_DOWN:
    		Log.i(TAG+".onTouch.ACTION_DOWN", "being called!");
    		//if there is animation being on, skip
    		if(getAnimationOn())
    			break;
    		
        	//old cursor and old imageview
        	Point oldcursor, newcursor;
        	oldcursor=mplate.getcursor();
        	newcursor=getviewlocation(v);
    		//skip if oldcursor and new cursor is invalid
    		if(oldcursor.equals(plate.NULLCURSOR)||newcursor.equals(plate.NULLCURSOR))
    			break;
        	
    		String out = String.format("oldcursor=(%d,%d), newcursor=(%d,%d)", 
    				oldcursor.x, oldcursor.y,
    				newcursor.x, newcursor.y);
    		Log.i(TAG+".onTouch.ACTION_DOWN", out);
    		
        	if(mplate.ball(oldcursor)!=plate.NULL && mplate.ball(newcursor)==plate.NULL){			//state0
        		// this scenario will be handled by onClick();
        	} else if(mplate.ball(oldcursor)==plate.NULL && mplate.ball(newcursor)==plate.NULL){	//state0
        		// this scenario will also be handled by onClick();
        	} else if(mplate.ball(oldcursor)!=plate.NULL && mplate.ball(newcursor)!=plate.NULL||	//state1
        			mplate.ball(oldcursor)==plate.NULL && mplate.ball(newcursor)!=plate.NULL){
        		// onTouch will handle this
        		
        		mMovingDrawable = getResources().getDrawable(mplate.ballres(newcursor));
        		mMovingImage.setImageDrawable(mMovingDrawable);
        		Point p = map2ScreenXY(v, event);
        		mMovingImage.setDelta(p.x, p.y, mdragtype);
        		mMovingImage.setVisibility(View.VISIBLE);
        		
        		// 1. select the ball at newcursor, 
        		// 2. mSavedCursor being set, which is the flag to decide whether a ball 
        		//    should be being moved in subsequent MOTIONEVENT.ACTION_MOVE messages
        		updatecursor(oldcursor, newcursor, true);
        		mSavedCursor = newcursor;
               	mSavedBall = mplate.ball(mSavedCursor);
               	mplate.removeball(mSavedCursor);
               	mplate.offcursor();
               	showball(mSavedCursor); 

        		return true;
        	} else {
        		throw new IllegalStateException(TAG+".onTouch.ACTION_DOWN"+"should not arrive here!");
        	}
    		break;
    	case MotionEvent.ACTION_MOVE:
    		Log.i(TAG+".onTouch.ACTION_MOVE", "being called");
    		if(mSavedCursor!=null){		//state1
        		Point p = map2ScreenXY(v, event);
    			out = String.format("x=%d, y=%d", p.x, p.y);
    			Log.i(TAG+".onTouch.ACTION_MOVE", out);
    			
        		//update the moving ball in screen
    	        mMovingImage.setDelta(p.x, p.y, mdragtype);
    		} 
    		break;
    	case MotionEvent.ACTION_UP:
    		Log.i(TAG+".onTouch.ACTION_UP", "being called!");
    		if(mSavedCursor!=null){		//state1
    			Point p = new Point();
    			p.x = (int) event.getX(); p.y = (int) event.getY();
    			Point q = MyImageView.getDelta(p, mdragtype);
    			Point c = MyImageView.getCenter(q);
    			Log.i(TAG+".onTouch.ACTION_UP", "c="+c);
    			MotionEvent e = MotionEvent.obtain(event);
    			e.setLocation(c.x, c.y);
    			
    			newcursor = map2Location(v, e);
    			Log.i(TAG+".onTouch.ACTION_UP", "newcursor="+newcursor);
    			//1. newcursor is out of the board
    			//2. newcursor is not null
    			//3. newcursor is identical to oldcursor
    			if(newcursor==null||mplate.ball(newcursor)!=plate.NULL||
    					newcursor.equals(mSavedCursor)){
            		mplate.setballtop(mSavedCursor, mSavedBall);
            		mplate.setcursor(mSavedCursor);
            		showball(mSavedCursor);
            		mSavedCursor = null;    				
    			} else {
            		mplate.setballtop(mSavedCursor, mSavedBall);
            		mplate.setcursor(mSavedCursor);
            		showball(mSavedCursor);
    				moveBall(mSavedCursor, newcursor);
    				mSavedCursor = null;
    			}
    			
        		mMovingImage.setVisibility(View.GONE);
        		
        		return true;
    		} 
    		break;
    	}
    	
		return false;
	}
	
	private int mSavedBall;
	private Point mSavedCursor=null;
	private Drawable mMovingDrawable=null;
	
	private boolean bRunningAnimation=false;
	private void setAnimationOn(boolean flag){
		bRunningAnimation = flag;
	}
	private boolean getAnimationOn(){
		return bRunningAnimation;
	}
}