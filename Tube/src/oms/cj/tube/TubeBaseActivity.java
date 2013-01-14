package oms.cj.tube;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OptionalDataException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import com.openfeint.api.OpenFeint;
import com.openfeint.api.resource.Leaderboard;
import com.openfeint.api.resource.Score;
import com.openfeint.api.resource.User;
import com.wooboo.adlib_android.ImpressionAdView;
import oms.cj.ads.AdGlobals;
import oms.cj.the9component.IThe9;
import oms.cj.the9component.TubeGetUserScoreCB;
import oms.cj.the9component.TubeSubmitToCB;
import oms.cj.tube.R;
import oms.cj.tube.component.Tube;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.view.View;

public class TubeBaseActivity extends Activity implements View.OnClickListener, IThe9{
	private final static String TAG = "TubeBaseActivity";
	private static final int MENU_MAIN = Menu.FIRST;
	private static final int MENU_SAVE = Menu.FIRST + 1;
	private static final int MENU_OPEN = Menu.FIRST + 2;
	private static final int MENU_QUIT = Menu.FIRST + 3;
	private final static int MENU_FACEIDSWITCH = Menu.FIRST + 4;
	private final static int MENU_ORIGINCUBE = Menu.FIRST + 5;
	
	protected PlayView _tubeview;
	protected CJContestRenderer _renderer;
	private int mIDSwitch;
	private ImageButton mNextStep, mPreviousStep;
	
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuItem item;
    	item = menu.add(0, MENU_SAVE, 0, getResources().getString(R.string.save));
    	item.setIcon(R.drawable.save24x24);
    	item = menu.add(0, MENU_OPEN, 0, getResources().getString(R.string.restore));
    	item.setIcon(R.drawable.load24x24);
    	item = menu.add(0, MENU_FACEIDSWITCH, 0, getResources().getString(R.string.switchid));
    	item.setIcon(R.drawable.face123456);
    	item = menu.add(0, MENU_MAIN, 0, getResources().getString(R.string.mainmenu));
    	item.setIcon(R.drawable.mainmenu);
    	item = menu.add(0, MENU_ORIGINCUBE, 0, getResources().getString(R.string.origincube));
    	item.setIcon(R.drawable.origincube);
    	return true;
    }
    
    public final static int DISABLENEXTSTEPBUTTON = 0;
    public final static int ENABLENEXTSTEPBUTTON = 1;
    public final static int ORIGINTUBEREACHED = 2;
    public final static int DISABLEPREVIOUSSTEPBUTTON = 3;
    public final static int ENABLEPREVIOUSSTEPBUTTON = 4;

    Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what){
    		case DISABLENEXTSTEPBUTTON:
    			if(mNextStep!=null)
    				mNextStep.setEnabled(false);
    			break;
    		case ENABLENEXTSTEPBUTTON:
    			if(mNextStep!=null)
    				mNextStep.setEnabled(true);
    			break;
    		case ORIGINTUBEREACHED:
    			if(AdGlobals.getInstance().the9Switch){
        			//submit scores to the 9 here
        			Chronometer m = (Chronometer) TubeBaseActivity.this.findViewById(R.id.chronometer);
        			String s = (String) m.getText();
        			m.stop();
        			int score = parseToSeconds(s);
        			Log.i(TAG+".mHandler.handleMessage", "elapse time = " + s);
        			Log.i(TAG+".mHandler.handleMessage", "elapse time = " + score);
        			TubeGetUserScoreCB cb = new TubeGetUserScoreCB(TubeBaseActivity.this, TubeBaseActivity.this, 
        					TubeApplication.cjLeaderBoardID, score, s);
        			Leaderboard l = new Leaderboard(TubeApplication.cjLeaderBoardID);
        			//read score from the9; real submit will be done in cb.onSuccess()
        			User me = OpenFeint.getCurrentUser();
        			l.getUserScore(me, cb);     				
    			}
    			break;
    		case DISABLEPREVIOUSSTEPBUTTON:
    			if(mPreviousStep!=null)
    				mPreviousStep.setEnabled(false);
    			break;
    		case ENABLEPREVIOUSSTEPBUTTON:
    			if(mPreviousStep!=null)
    				mPreviousStep.setEnabled(true);
    			break; 
    		default:
    			Log.e(TAG+".handler.handleMessage", "exceptional msg = "+msg.what);
    			break;
    		}
    	}
    };
    
	//submit score to the9
    public void submitScoreToThe9(String leaderboardID, int score, String strScore){    	
   		Leaderboard l = new Leaderboard(leaderboardID);
   		final Score s = new Score(score, strScore);
   		TubeSubmitToCB cb = new TubeSubmitToCB(this, s);
		s.submitTo(l, cb);
    }
    
    private int parseToSeconds(String s){
    	int t = 0;
    	final String REGEX = ":";
    	Pattern p = Pattern.compile(REGEX);
    	String[] items = p.split(s);
    	
    	int b = 1;
    	for(int i=items.length-1;i>=0;i--){
    		int a = Integer.parseInt(items[i]);
    		if(i==items.length-1)
    			b = 1;
    		else
    			b = b*60;
        	t = t + a*b;    		
    	}

    	return t;
    }
    
    protected void initTube(String fileName, boolean randomized, Activity act){
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
    	SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, 0);
    	mIDSwitch = settings.getInt(Config.ref[3], R.id.noid);
        _renderer = new CJContestRenderer(fileName, randomized, mIDSwitch, act);
        _renderer.setHandler(mHandler);
        _tubeview=new PlayView(this, _renderer);
        _tubeview.setHeightPercent(0.8f);
        _tubeview.setId(R.id.playview);
        
        setContentView(R.layout.play);
        RelativeLayout play = (RelativeLayout)this.findViewById(R.id.play);
        
        //add tubeview
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, R.id.chronometer);
        play.addView(_tubeview, params);
        
        //add config buttons
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout configbuttons = (RelativeLayout) inflator.inflate(R.layout.configbuttons, null);
		params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.BELOW, R.id.playview);
		play.addView(configbuttons, params);
		
		Button b = (Button) this.findViewById(R.id.grow);
		b.setOnClickListener(this);
		b = (Button) this.findViewById(R.id.shrink);
		b.setOnClickListener(this);
		ImageButton iButton = (ImageButton) this.findViewById(R.id.nextStep);
		mNextStep = iButton; mNextStep.setEnabled(false);
		iButton.setOnClickListener(this);
		iButton = (ImageButton) this.findViewById(R.id.previousStep);
		mPreviousStep = iButton; mPreviousStep.setEnabled(false);
		int bound = _renderer.getBackwardBound();
		int qSize = _renderer.getQueue().size();
		String out = String.format("bound=%d, qSize=%d", bound, qSize);
		Log.i(TAG+".initTube", out);
		if(bound<qSize)
			mPreviousStep.setEnabled(true);
		iButton.setOnClickListener(this);		
		
		AdGlobals.getInstance().adDynamicFlow(_tubeview, this, Gravity.BOTTOM|Gravity.LEFT);
        
        Chronometer meter = (Chronometer) this.findViewById(R.id.chronometer);
        meter.start();
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭 渐入式 广告
		ImpressionAdView.close();
	}

	@Override
	public void onClick(View v) {
		int size = _renderer.getTube().getSize();
		
		switch(v.getId()){
		case R.id.grow:
			if(size<Tube.MaxSize&&size>=Tube.MinSize)
				size++;
			_renderer.getTube().setSize(size);
			break;
		case R.id.shrink:
			if(size<=Tube.MaxSize&&size>Tube.MinSize)
				size--;
			_renderer.getTube().setSize(size);
			break;
		case R.id.previousStep:
			_tubeview.queueEvent(new Runnable(){
				public void run() {
					_renderer.undoManualRotate();
				}
			});
			break;
		case R.id.nextStep:
			_tubeview.queueEvent(new Runnable(){
				public void run() {
					_renderer.forward();
				}
			});			
			break;
		default:
			break;
		}
	}
	
    private void quit() {
    }
    
    private void mainMenu(){
    	finish();
    }
    
    protected void onActivityResult(final int requestCode, int resultCode, Intent data){
    	if(resultCode == RESULT_OK){
    		final String fileName = data.getStringExtra(ChooseFileActivity.FILE2SERIALIZE);
    		
    		switch (requestCode) {
    		case ChooseFileActivity.CHOOSE_FILE_TO_OPEN:
    			_tubeview.queueEvent(new Runnable(){
    				public void run() {
    	    			try {
    	    				_renderer.restoreFromFile(TubeBaseActivity.this, fileName);
    					} catch (OptionalDataException e) {
    						Log.e(TAG, "onActivityResult(...): " + "requestCode=" + requestCode + "; OptionalDataException");
    					} catch (ClassNotFoundException e) {
    						e.printStackTrace();
    						Log.e(TAG, "onActivityResult(...): " + "requestCode=" + requestCode + "; ClassNotFoundException");
    					} catch (IOException e) {
    						Log.e(TAG, "onActivityResult(...): " + "requestCode=" + requestCode + "; IOException");
    					}
    				}
    			});
    			break;
    		case ChooseFileActivity.CHOOSE_FILE_TO_SAVE:
    			_tubeview.queueEvent(new Runnable(){
    				public void run() {
    					try {
    						_renderer.saveToFile(TubeBaseActivity.this, fileName);
    						_renderer.doSnapshot(fileName+TubeApplication.IconSuffix);
    					} catch (FileNotFoundException e) {
    						Log.e(TAG, "onActivityResult(...): " + "requestCode=" + requestCode + "; FileNotFoundException");
    					} catch (IOException e) {
    						Log.e(TAG, "onActivityResult(...): " + "requestCode=" + requestCode + "; IOException");
    					}
    				}
    			});
    			break;
    		default:
    			break;
    		}
    	} else {
    		Log.i(TAG, "onActivityResult(...): " + "resultCode=" + resultCode);
    	}
    }
    
    private void serialize(int requestCode){
    	Intent intent=new Intent();
    	intent.setClass(TubeBaseActivity.this, ChooseFileActivity.class);
    	intent.putExtra(ChooseFileActivity.REQUESTCODE, requestCode);
    	startActivityForResult(intent, requestCode);    	
    }
    
    private void toggleFaceIDSwitch(){
    	Integer[] ids = {R.id.noid, R.id.cubeid};
    	List<Integer> id_list = Arrays.asList(ids);
    	int currentPos = id_list.indexOf(mIDSwitch);
    	int nextPos = (currentPos+1)%id_list.size();
    	mIDSwitch = id_list.get(nextPos);
    	
    	_tubeview.queueEvent(new Runnable(){
    		public void run(){
    			_renderer.setTubeTexture(mIDSwitch);
    		}
    	});
    }
    
    private void generateOriginCube(){
		_tubeview.queueEvent(new Runnable(){
			public void run() {
				_renderer.reset();
			}
		});
    }
    
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case MENU_QUIT:
    		quit();
    		return true;
    	case MENU_FACEIDSWITCH:
    		Log.i(TAG+".onOptionsItemSelected", "MENU_FACEIDSWITCH is called!");
    		toggleFaceIDSwitch();
    		return true;
    	case MENU_MAIN:
    		mainMenu();
    		return true;
    	case MENU_SAVE:
    		serialize(ChooseFileActivity.CHOOSE_FILE_TO_SAVE);
    		return true;
    	case MENU_OPEN:
    		serialize(ChooseFileActivity.CHOOSE_FILE_TO_OPEN);
    		return true;
    	case MENU_ORIGINCUBE:
    		generateOriginCube();
    		return true;
    	}
    	
    	return false;
    }
    
    public void onPause(){
    	super.onPause();
    	Log.i(TAG+"::onPause", "being called!");
    }
    
    public void onResume(){
    	super.onResume();
    	Log.i(TAG+"::onResume", "being called!");
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig){
    	Log.i(TAG+"onConfigurationChanged", "being called!");
    	super.onConfigurationChanged(newConfig);
    }
}