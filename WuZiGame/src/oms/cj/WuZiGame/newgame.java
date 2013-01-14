package oms.cj.WuZiGame;

import java.util.HashMap;
import com.wooboo.adlib_android.ImpressionAdView;
import com.wostore.adsdk.ADSdkView;
import oms.cj.WuZiBoard.INewGameActivity;
import oms.cj.WuZiBoard.WuZiCoordPuzzleBoard;
import oms.cj.WuZiBoard.WuZiPuzzleBoard;
import oms.cj.WuZiGame.R;
import oms.cj.WuZiLogic.BoardPositionEvent;
import oms.cj.WuZiLogic.LogicResource;
import oms.cj.WuZiLogic.ScreenInfo;
import oms.cj.WuZiLogic.VirtualWuZi;
import oms.cj.WuZiWay.Way;
import oms.cj.ads.AdGlobals;
import oms.cj.musicservice.MusicService;
import oms.cj.musicservice.SoundService;
import oms.cj.view.SimArrowsView;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;

public class newgame extends Activity implements OnClickListener, INewGameActivity{
	private final static String TAG = "newgame";
	
	private ScreenInfo mScreenInfo;
	private TableLayout mWuZiBoardLayout;
	private RelativeLayout mgamelayout;
	private ImageButton[] mButtons = new ImageButton[1];
	VirtualWuZi mV;
	WuZiPuzzleBoard mWuZiBoard;
	private boolean bGameOver;
	private SoundService mS;
	private int iSoundEffects;		
	
	private int[] getRowAndCol(int resolution){
		int[] sizes = new int[2];
		
		if(resolution == ScreenInfo.HVGA){
			sizes[0] = 15;
			sizes[1] = 15;
		} else if(resolution == ScreenInfo.WVGA) {
			sizes[0] = 15;
			sizes[1] = 15;
		}
		
		return sizes;
	}
	
	public void disableAllButtons(){
		for(int i=0;i<mButtons.length;i++)
			mButtons[i].setClickable(false);
	}
	
	private void setupArrowsAndOK(){
		
		mButtons[0] = (ImageButton) findViewById(R.id.ok);
		mButtons[0].setOnClickListener(this);
	}
	
	public void stopBackgroundMusic(){
    	SharedPreferences settings = getSharedPreferences(config.PREFS_NAME, 0);
    	int iVoiceSwitch = settings.getInt(config.ref[6], WuZiConfig.VOICEOFF);
    	if(iVoiceSwitch==WuZiConfig.VOICEON && mMusicService!=null){
    		mMusicService.stopBackgroundMusic();
    	}
	}
	
	private void playBackgroundMusic(){
    	SharedPreferences settings = getSharedPreferences(config.PREFS_NAME, 0);
    	int iVoiceSwitch = settings.getInt(config.ref[6], WuZiConfig.VOICEOFF);
    	if(iVoiceSwitch==WuZiConfig.VOICEON && mMusicService!=null){
    		mMusicService.playBackgroundMusic();
    	}		
	}
	
	private MusicService mMusicService;
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG+".ServiceConnection", "onServiceConnected(...): " + "MusicService is connected!");
			mMusicService = ((MusicService.MusicBinder)service).getService();
			playBackgroundMusic();
		}

		public void onServiceDisconnected(ComponentName name) {
			mMusicService = null;
		}
	};
	
	@Override 
	protected void onStart() {
		Log.i(TAG, "onStart(...): " + "onStart() is called!");
		super.onStart();
	}
    
	@Override 
	protected void onRestart() {
		Log.i(TAG, "onRestart(...): " + "onRestart() is called!");
		super.onStart();
	}

	@Override 
	protected void onResume() {
		Log.i(TAG, "onResume(...): " + "onResume() is called!");
		playBackgroundMusic();
		super.onStart();
	}

	@Override 
	protected void onPause() {
		Log.i(TAG, "onPause(...): " + "onPause() is called!");
		stopBackgroundMusic();
		super.onStart();
	}

	@Override 
	protected void  onStop() {
		Log.i(TAG, "onStop(...): " + "onStop() is called!");
		stopBackgroundMusic();
    	super.onStop();
	}
	
	@Override
    public void onDestroy(){
		Log.i(TAG, "onDestroy(...): " + "onDestroy() is called!");
    	unbindService(mConnection);
    	super.onDestroy();
    	
		// 关闭 渐入式 广告
		ImpressionAdView.close();
    }
	
	private boolean gameOver(){
		return bGameOver;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(gameOver())
			return super.onKeyDown(keyCode, event);
		int direction = SimArrowsView.STILL;
		boolean bHandled = false;
		
		Log.i(TAG+".onKeyDown", "keyCode = " + keyCode);
		
		switch(keyCode){
		case KeyEvent.KEYCODE_DPAD_LEFT:
			direction = SimArrowsView.LEFT;
			onArrowsClick(direction);
			bHandled = true;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			direction = SimArrowsView.RIGHT;
			onArrowsClick(direction);
			bHandled = true;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			direction = SimArrowsView.DOWN;
			onArrowsClick(direction);
			bHandled = true;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			direction = SimArrowsView.UP;
			onArrowsClick(direction);
			bHandled = true;
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			onEnterClick();
			bHandled = true;
			break;
		case KeyEvent.KEYCODE_BACK:
			if (event.getRepeatCount()==0&&iSoundEffects==WuZiConfig.SOUNDON) {
				mS.playSound(SoundService.ESCCLICKED);
				
			}  
			bHandled = false;
			break;
		default:
			bHandled = false;
			break;	
		}

		if(bHandled)
			return bHandled;
		else 
			return super.onKeyDown(keyCode, event);
	}
	
	public View onCreateView (String name, Context context, AttributeSet attrs){
		Log.i(TAG+".onCreateView", "being called!");
		
		String out = String.format("name=%s", name);
		Log.i(TAG+".onCreateView", out);
		
		return super.onCreateView(name, context, attrs);
	}
	
	public void setupWostoreAd(int container, int simarrowpadId){
		int adType = AdGlobals.getInstance().getAdType();
		if(adType==AdGlobals.WOSTORE){
	        RelativeLayout rLayout = (RelativeLayout) this.findViewById(container);
	        SimArrowsView sV = (SimArrowsView)this.findViewById(simarrowpadId);
	        if(rLayout!=null&&sV!=null){
	        	Log.i(TAG+".setupWostoreAd", "adding wostore adview!");
	        	int width = sV.getUnitWidth();
	        	String out = String.format("width=%d", width);
	        	Log.i(TAG+".setupWostoreAd", out);
	        	RelativeLayout.LayoutParams params = (LayoutParams) rLayout.getLayoutParams();
	        	params.width = width;
	        	rLayout.setLayoutParams(params);

	        	ADSdkView adSdkView = new ADSdkView(this);

	    		adSdkView.setLayoutParams(new RelativeLayout.LayoutParams( 
	    				RelativeLayout.LayoutParams.FILL_PARENT, 
	    				RelativeLayout.LayoutParams.WRAP_CONTENT)
	    		);
	    		
	    		rLayout.addView(adSdkView);
	        }
			
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	initIdMap();
    	bGameOver = false;
    	
    	// bind to MusicService
    	boolean bBind = bindService(new Intent(newgame.this, MusicService.class), mConnection, Context.BIND_AUTO_CREATE);
    	Log.i(TAG, "onCreate(...): " + "bBind=" + bBind);

    	//get preference
    	SharedPreferences settings = getSharedPreferences(config.PREFS_NAME, 0);
    	int iXianHouShou = settings.getInt(config.ref[0], WuZiConfig.XianShou);
    	int iDifficulty = settings.getInt(config.ref[1], WuZiConfig.EASY);
    	int iStrategy = settings.getInt(config.ref[2], R.id.attack);
    	boolean bSanSan = settings.getBoolean(config.ref[3], true);
    	boolean bSiSi = settings.getBoolean(config.ref[4], true);
    	boolean bChangLian = settings.getBoolean(config.ref[5], true);
    	Log.i(TAG, "onCreate(...): " + "iStrategy=" + iStrategy);
    	int iWhenHitBoard = settings.getInt(config.ref[7], WuZiConfig.MOVEFOCUSONLY);
    	iSoundEffects=settings.getInt(config.ref[8], WuZiConfig.SOUNDOFF);
    	
        super.onCreate(savedInstanceState);
        
        mS = new SoundService(this);
        
		// fullscreen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.wuzimain);

        //setup arrow button's onclicklistener
        setupArrowsAndOK();
    	
        setupWostoreAd(R.id.wostoreAdContainer, R.id.simarrowpad);
        
        //set the game's background
        mgamelayout=(RelativeLayout)findViewById(R.id.gamelayout);
        mgamelayout.setBackgroundResource(R.drawable.base1);
        
        //get screen's resolution
        mScreenInfo = new ScreenInfo(this);
        int resolution = mScreenInfo.getResolution();
        Log.i(TAG+".onCreate", "resolution = " + resolution);
        //calculate the size of row and col
        int[] list = getRowAndCol(resolution);
        
        //create a puzzle board
        mWuZiBoard = new WuZiCoordPuzzleBoard(list[0], list[1], resolution, this, WuZiConfig.mapFocusMovingMethod(iWhenHitBoard));
        WuZiConfig config = new WuZiConfig(iXianHouShou, iDifficulty, iStrategy, bSanSan, bSiSi, bChangLian);
        TextView step = (TextView) findViewById(R.id.step);
        mV = new VirtualWuZi(list[0], list[1], config.convert2LogicConfig(), step);
        mWuZiBoard.attachTo(mV);
        
        //get WuZiPuzzleBoard TableLayout
        mWuZiBoardLayout = (TableLayout)findViewById(R.id.chessboardlayout);
        int[] paddings={0,0,0};
        mWuZiBoard.getpaddings(mScreenInfo.getWidth(), mScreenInfo.getHeight(), paddings);
        Log.i(TAG+"::onCreate", "paddings[0]="+paddings[0]);
        Log.i(TAG+"::onCreate", "paddings[1]="+paddings[1]);
        Log.i(TAG+"::onCreate", "paddings[2]="+paddings[2]);
        mWuZiBoardLayout.setPadding(paddings[0], paddings[1], paddings[2], 1);
        mWuZiBoardLayout.setClipChildren(false);
        
        mWuZiBoard.createTemplate(mWuZiBoardLayout);
        
        mWuZiBoard.show(mWuZiBoardLayout);
                
        //initialize the SimArrowsView
        SimArrowsView sV = (SimArrowsView)this.findViewById(R.id.simarrowpad);
        SimArrowsView.IOnArrowDownListener listener = new SimArrowsView.IOnArrowDownListener() {
			
			@Override
			public void onArrwoDown(int direction) {
				Log.i("listener.onArrwoDown", "direction = " + direction);
				onArrowsClick(direction);
			}
		};
        sV.setOnArrowDownListener(listener);
        
        AdGlobals.getInstance().displayWoobooDynamicAd(mgamelayout, this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
    	Log.i(TAG, "onConfigurationChanged() is called!");
    	super.onConfigurationChanged(newConfig);
    }
    
    private void onArrowsClick(int dirID){
    	if(gameOver())
    		return;    	
    	if(iSoundEffects==R.id.soundon)
    		mS.playSound(SoundService.ARROWCLICKED);

		int[] prevFocus = mV.getFocus();
		int[] currentFocus = mV.moveFocus(WuZiConfig.mapDirID(dirID));
		Log.i(TAG, "onClick(...): " + "previous focus =" + Way.posString(prevFocus));
		Log.i(TAG, "onClick(...): " + "current focus =" + Way.posString(currentFocus));
	
		int prevType = mV.getQiZiType(prevFocus), currentType = mV.getQiZiType(currentFocus);
		mWuZiBoard.setQiZi(mWuZiBoardLayout, prevFocus, prevType, WuZiPuzzleBoard.NORMAL);
		mWuZiBoard.setQiZi(mWuZiBoardLayout, currentFocus, currentType, WuZiPuzzleBoard.FOCUS);    	
    }

    private void onEnterClick(){
    	if(gameOver())
    		return;
    	if(iSoundEffects==R.id.soundon)
    		mS.playSound(SoundService.ENTERCLICKED);

		int[] currentPos = mV.getFocus();
		if(mV.getQiZiType(currentPos)==VirtualWuZi.EMPTY){
			BoardPositionEvent event = new BoardPositionEvent(mWuZiBoard, currentPos[0], currentPos[1], mWuZiBoardLayout);
			mV.onQiZi2Position(event);
		}
    }
    
	@Override
	public void onClick(View v) {
		int dirID = v.getId();
		
		if(dirID == R.id.ok){	//ok button is clicked
			onEnterClick();
		} else {	
		}
	}

	@Override
	public void onGameOver() {
		disableAllButtons();
		stopBackgroundMusic();
		bGameOver = true;
	}

	@Override 
	public Resources getContextResources (){
		return getResources();
	}
	
	@Override
	public Context getContext() {
		return this;
	}

	private HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();
	
	private void initIdMap(){
		//NPB
		idMap.put(LogicResource.NPB_HVGA_DRAWABLE_BASE, R.drawable.grid16x16);
		idMap.put(LogicResource.NPB_WVGA_DRAWABLE_BASE, R.drawable.grid28x28);

		//CPB
		idMap.put(LogicResource.CPB_HVGA_DRAWABLE_BASE, R.drawable.cross_grid16x16);
		idMap.put(LogicResource.CPB_WVGA_DRAWABLE_BASE, R.drawable.cross_grid28x28);
		
		idMap.put(LogicResource.CPB_HVGA_DRAWABLE_BORDER_1, R.drawable.hvga_cross_0_grid8x8);
		idMap.put(LogicResource.CPB_HVGA_DRAWABLE_BORDER_2, R.drawable.hvga_cross_1_grid16x8);
		idMap.put(LogicResource.CPB_HVGA_DRAWABLE_BORDER_3, R.drawable.hvga_cross_2_grid8x8);
		idMap.put(LogicResource.CPB_HVGA_DRAWABLE_BORDER_4, R.drawable.hvga_cross_3_grid8x16);
		idMap.put(LogicResource.CPB_HVGA_DRAWABLE_BORDER_5, R.drawable.hvga_cross_4_grid8x8);
		idMap.put(LogicResource.CPB_HVGA_DRAWABLE_BORDER_6, R.drawable.hvga_cross_5_grid16x8);
		idMap.put(LogicResource.CPB_HVGA_DRAWABLE_BORDER_7, R.drawable.hvga_cross_6_grid8x8);
		idMap.put(LogicResource.CPB_HVGA_DRAWABLE_BORDER_8, R.drawable.hvga_cross_7_grid8x16);
		idMap.put(LogicResource.CPB_WVGA_DRAWABLE_BORDER_1, R.drawable.wvga_cross_0_grid14x14);
		idMap.put(LogicResource.CPB_WVGA_DRAWABLE_BORDER_2, R.drawable.wvga_cross_1_grid28x14);
		idMap.put(LogicResource.CPB_WVGA_DRAWABLE_BORDER_3, R.drawable.wvga_cross_2_grid14x14);
		idMap.put(LogicResource.CPB_WVGA_DRAWABLE_BORDER_4, R.drawable.wvga_cross_3_grid14x28);
		idMap.put(LogicResource.CPB_WVGA_DRAWABLE_BORDER_5, R.drawable.wvga_cross_4_grid14x14);
		idMap.put(LogicResource.CPB_WVGA_DRAWABLE_BORDER_6, R.drawable.wvga_cross_5_grid28x14);
		idMap.put(LogicResource.CPB_WVGA_DRAWABLE_BORDER_7, R.drawable.wvga_cross_6_grid14x14);
		idMap.put(LogicResource.CPB_WVGA_DRAWABLE_BORDER_8, R.drawable.wvga_cross_7_grid14x28);
		
		//WZPB
		idMap.put(LogicResource.WZPB_HVGA_DRAWABLE_QIZI_BLACK, R.drawable.hvga_blackqizi16x16);
		idMap.put(LogicResource.WZPB_HVGA_DRAWABLE_QIZI_WHITE, R.drawable.hvga_whiteqizi16x16);
		idMap.put(LogicResource.WZPB_HVGA_DRAWABLE_QIZI_BLACK_FOCUS, R.drawable.hvga_blackqizi16x16_focus);
		idMap.put(LogicResource.WZPB_HVGA_DRAWABLE_QIZI_WHITE_FOCUS, R.drawable.hvga_whiteqizi16x16_focus);
		idMap.put(LogicResource.WZPB_WVGA_DRAWABLE_QIZI_BLACK, R.drawable.wvga_blackqizi28x28);
		idMap.put(LogicResource.WZPB_WVGA_DRAWABLE_QIZI_WHITE, R.drawable.wvga_whiteqizi28x28);
		idMap.put(LogicResource.WZPB_WVGA_DRAWABLE_QIZI_BLACK_FOCUS, R.drawable.wvga_blackqizi28x28_focus);
		idMap.put(LogicResource.WZPB_WVGA_DRAWABLE_QIZI_WHITE_FOCUS, R.drawable.wvga_whiteqizi28x28_focus);
		idMap.put(LogicResource.WZPB_HVGA_DRAWABLE_BLANK_FOCUS, R.drawable.focus_cross_grid16x16);
		idMap.put(LogicResource.WZPB_WVGA_DRAWABLE_BLANK_FOCUS, R.drawable.focus_cross_grid28x28);

		//WZCPB
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_a, R.drawable.hvga_cross_1_grid16x16_a);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_b, R.drawable.hvga_cross_1_grid16x16_b);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_c, R.drawable.hvga_cross_1_grid16x16_c);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_d, R.drawable.hvga_cross_1_grid16x16_d);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_e, R.drawable.hvga_cross_1_grid16x16_e);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_f, R.drawable.hvga_cross_1_grid16x16_f);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_g, R.drawable.hvga_cross_1_grid16x16_g);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_h, R.drawable.hvga_cross_1_grid16x16_h);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_i, R.drawable.hvga_cross_1_grid16x16_i);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_j, R.drawable.hvga_cross_1_grid16x16_j);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_k, R.drawable.hvga_cross_1_grid16x16_k);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_l, R.drawable.hvga_cross_1_grid16x16_l);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_m, R.drawable.hvga_cross_1_grid16x16_m);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_n, R.drawable.hvga_cross_1_grid16x16_n);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_o, R.drawable.hvga_cross_1_grid16x16_o);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_a, R.drawable.wvga_cross_1_grid28x28_a);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_b, R.drawable.wvga_cross_1_grid28x28_b);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_c, R.drawable.wvga_cross_1_grid28x28_c);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_d, R.drawable.wvga_cross_1_grid28x28_d);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_e, R.drawable.wvga_cross_1_grid28x28_e);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_f, R.drawable.wvga_cross_1_grid28x28_f);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_g, R.drawable.wvga_cross_1_grid28x28_g);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_h, R.drawable.wvga_cross_1_grid28x28_h);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_i, R.drawable.wvga_cross_1_grid28x28_i);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_j, R.drawable.wvga_cross_1_grid28x28_j);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_k, R.drawable.wvga_cross_1_grid28x28_k);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_l, R.drawable.wvga_cross_1_grid28x28_l);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_m, R.drawable.wvga_cross_1_grid28x28_m);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_n, R.drawable.wvga_cross_1_grid28x28_n);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_o, R.drawable.wvga_cross_1_grid28x28_o);
		
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_1, R.drawable.hvga_cross_7_grid16x16_1);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_2, R.drawable.hvga_cross_7_grid16x16_2);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_3, R.drawable.hvga_cross_7_grid16x16_3);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_4, R.drawable.hvga_cross_7_grid16x16_4);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_5, R.drawable.hvga_cross_7_grid16x16_5);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_6, R.drawable.hvga_cross_7_grid16x16_6);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_7, R.drawable.hvga_cross_7_grid16x16_7);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_8, R.drawable.hvga_cross_7_grid16x16_8);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_9, R.drawable.hvga_cross_7_grid16x16_9);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_10, R.drawable.hvga_cross_7_grid16x16_10);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_11, R.drawable.hvga_cross_7_grid16x16_11);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_12, R.drawable.hvga_cross_7_grid16x16_12);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_13, R.drawable.hvga_cross_7_grid16x16_13);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_14, R.drawable.hvga_cross_7_grid16x16_14);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_15, R.drawable.hvga_cross_7_grid16x16_15);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_1, R.drawable.wvga_cross_7_grid28x28_1);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_2, R.drawable.wvga_cross_7_grid28x28_2);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_3, R.drawable.wvga_cross_7_grid28x28_3);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_4, R.drawable.wvga_cross_7_grid28x28_4);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_5, R.drawable.wvga_cross_7_grid28x28_5);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_6, R.drawable.wvga_cross_7_grid28x28_6);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_7, R.drawable.wvga_cross_7_grid28x28_7);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_8, R.drawable.wvga_cross_7_grid28x28_8);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_9, R.drawable.wvga_cross_7_grid28x28_9);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_10, R.drawable.wvga_cross_7_grid28x28_10);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_11, R.drawable.wvga_cross_7_grid28x28_11);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_12, R.drawable.wvga_cross_7_grid28x28_12);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_13, R.drawable.wvga_cross_7_grid28x28_13);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_14, R.drawable.wvga_cross_7_grid28x28_14);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_15, R.drawable.wvga_cross_7_grid28x28_15);
		
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_1, R.drawable.hvga_cross_0_grid8x8);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_2, R.drawable.hvga_cross_1_grid16x8);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_3, R.drawable.hvga_cross_2_grid8x8);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_4, R.drawable.hvga_cross_3_grid8x16);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_5, R.drawable.hvga_cross_4_grid8x8);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_6, R.drawable.hvga_cross_5_grid16x8);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_7, R.drawable.hvga_cross_6_grid8x8);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_8, R.drawable.hvga_cross_7_grid8x16);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_9, R.drawable.hvga_cross_8_grid16x16);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_10, R.drawable.hvga_cross_9_grid8x16);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_11, R.drawable.hvga_cross_10_grid16x8);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_12, R.drawable.hvga_cross_11_grid8x16);
		idMap.put(LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_13, R.drawable.hvga_cross_12_grid16x8);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_1, R.drawable.wvga_cross_0_grid14x14);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_2, R.drawable.wvga_cross_1_grid28x14);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_3, R.drawable.wvga_cross_2_grid14x14);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_4, R.drawable.wvga_cross_3_grid14x28);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_5, R.drawable.wvga_cross_4_grid14x14);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_6, R.drawable.wvga_cross_5_grid28x14);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_7, R.drawable.wvga_cross_6_grid14x14);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_8, R.drawable.wvga_cross_7_grid14x28);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_9, R.drawable.wvga_cross_8_grid28x28);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_10, R.drawable.wvga_cross_9_grid14x28);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_11, R.drawable.wvga_cross_10_grid28x14);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_12, R.drawable.wvga_cross_11_grid14x28);
		idMap.put(LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_13, R.drawable.wvga_cross_12_grid28x14);		
	}
	
	@Override
	public int map2DrawableResId(int localId) {
		return idMap.get(localId);
	}
	
}