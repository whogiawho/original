package oms.cj.tube.player;


import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.Stack;
import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.TubeBasicRenderer;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import oms.cj.tube.flysymbol.FlySymbol;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class TubePlayer extends LinearLayout implements View.OnClickListener, ViewSwitcher.ViewFactory{
	private final static String TAG = "TubePlayer";
	
	private PlayerView mPlayerView; 
	private ImageButton[] mButtons = new ImageButton[4];
	private int[] mOrigColors;
	private boolean bStatePlay = true;
	private final String mPlayString, mPauseString;
	private FlySymbol mFlySym = null;
	private TextSwitcher mSwitcher = null;
	
	public FlySymbol getFlySymbol(){
		return mFlySym;
	}
	
	public TextSwitcher getSwitcher(){
		return mSwitcher;
	}
	
	private void initImageButton(View v, int idx, int resID, int contentStringResID){
		ImageButton button = (ImageButton) v.findViewById(resID); 
		button.setOnClickListener(this); 
		button.setEnabled(false); 
		mButtons[idx]=button;
		button.setContentDescription(getContext().getString(contentStringResID));
	}
	
	public TubePlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPlayString = context.getResources().getString(R.string.play);
		mPauseString = context.getResources().getString(R.string.pause);
		
		Log.i(TAG+".TubePlayer", "constructor starting ...");
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=layoutInflater.inflate(R.layout.tubeplayer, this); 
		
		initImageButton(view, 0, R.id.previousStep, R.string.previousStep);
		initImageButton(view, 1, R.id.nextStep, R.string.nextStep);
		initImageButton(view, 2, R.id.play, R.string.play);
		initImageButton(view, 3, R.id.reset, R.string.reset);
		
		mFlySym = (FlySymbol) view.findViewById(R.id.flysymbol);
		//initialize the switcher
		mSwitcher = (TextSwitcher) view.findViewById(R.id.rotateremark);
		mSwitcher.setFactory(this);
        Animation in = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.fade_out);
        mSwitcher.setInAnimation(in);
        mSwitcher.setOutAnimation(out);
        mSwitcher.setText("");
        
		ViewStub stub = (ViewStub) view.findViewById(R.id.playerviewstub);
		String className = this.getClass().getName();
		Log.i(TAG+".TubePlayer", "className="+className);
		if(className.equals("oms.cj.tube.solver.TubeSolver")){
			stub.setLayoutResource(R.layout.solverview);
		} else {
			stub.setLayoutResource(R.layout.playerview);
		}
		mPlayerView = (PlayerView) stub.inflate();
		mPlayerView.setTubePlayer(this);
		
		AdGlobals.getInstance().inflateAdView(view.getContext(), view, AdGlobals.getInstance().getAdInterface());
		
		//redefine color from attrs, which is from xml
		int[][] colors = Tube.loadCubesColor(context, attrs);
		setColor(colors);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TubePlayer);
		String commandFile=null;
		commandFile = a.getString(R.styleable.TubePlayer_commandsfile);
		//set commandFile
		try {
			if(commandFile!=null){
				Stack<RotateAction> commands = mPlayerView.getActionsFrom(commandFile);
				buttonSwitchRoutines(true);
				if(mFlySym!=null)
					mFlySym.init(commands);
			}
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		a = context.obtainStyledAttributes(attrs, R.styleable.TubePlayer);
		//set heightPercent
		float heightPercent = a.getFloat(R.styleable.TubePlayer_heightpercent, 1.0f);
		mPlayerView.setHeightPercent(heightPercent);
		
		//set rotatecube and rotateface
		boolean bRotateCube = a.getBoolean(R.styleable.TubePlayer_rotatecube, true);
		mPlayerView.enableFeature(TubeBasicRenderer.FEATURE_ROTATECUBE, bRotateCube);
		boolean bRotateFace = a.getBoolean(R.styleable.TubePlayer_rotateface, false);
		mPlayerView.enableFeature(TubeBasicRenderer.FEATURE_ROTATEFACE, bRotateFace);
		
		a.recycle();
	}
    
	public void setColor(Tube t){
		int[] origColors = new int[Tube.CubesEachTube*Cube.FacesEachCube];
		
		 t.getColor(origColors);
		 setColor(origColors);
	}
	public void setColor(int[][] colors){
		int[] origColors = new int[Tube.CubesEachTube*Cube.FacesEachCube];
		
		for(int i=0;i<Tube.CubesEachTube;i++)
			for(int j=0;j<Cube.FacesEachCube;j++){
				origColors[i*Cube.FacesEachCube+j]=colors[i][j];
			}
		setColor(origColors);
	}
	public void setColor(int[] colors){
		mOrigColors = colors;
		mPlayerView.setCubesColor(colors);
	}
	
	public void togglePlayButton(boolean bPlay){
		String playButtonText = null;
		TransitionDrawable drawable = (TransitionDrawable) mButtons[2].getDrawable();
		if(bPlay){				//when being played, playText is Pause
			bPlay = false;
			playButtonText = mPauseString;
			drawable.startTransition(500);
		} else {				//otherwise, playText is Play
			bPlay = true;
			playButtonText = mPlayString;
			drawable.resetTransition();
		}
		mButtons[2].setContentDescription(playButtonText);
		
		bStatePlay = bPlay;
	}
	
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.play:
			mPlayerView.play(bStatePlay);
			togglePlayButton(bStatePlay);
			break;
		case R.id.previousStep:
			mPlayerView.backward();
			break;
		case R.id.nextStep:
			mPlayerView.forward();
			break;
		case R.id.reset:
			mPlayerView.reset(mOrigColors);
			mFlySym.reset();
			break;
		}
	}
	
	public void buttonSwitchRoutines(boolean bSwitch){
		for(int i=0;i<mButtons.length;i++)
			mButtons[i].setEnabled(bSwitch);
	}
	
	public void onPause(){
		if(mPlayerView!=null)
			mPlayerView.onPause();
	}
	public void onResume(){
		if(mPlayerView!=null)
			mPlayerView.onResume();
	}
	
	public PlayerView getPlayerView(){
		return mPlayerView;
	}

	@Override
	public View makeView() {
        TextView t = new TextView(getContext());
        
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        t.setTextSize(24);
        return t;
	}
}
