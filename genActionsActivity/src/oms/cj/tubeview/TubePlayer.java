package oms.cj.tubeview;

import java.io.IOException;
import java.io.StreamCorruptedException;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.Tube;
import oms.cj.genActions.R;
import oms.cj.tube.component.Color;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class TubePlayer extends LinearLayout implements View.OnClickListener{
	@SuppressWarnings("unused")
	private final static String TAG = "TubePlayer";
	private TubeView mTubeView;
	
	public TubePlayer(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=layoutInflater.inflate(R.layout.tubeplayer, this); 
		
		Button button = (Button) view.findViewById(R.id.previousStep);
		button.setOnClickListener(this);
		button = (Button) view.findViewById(R.id.nextStep);
		button.setOnClickListener(this);
		button = (Button) view.findViewById(R.id.play);
		button.setOnClickListener(this);
		button = (Button) view.findViewById(R.id.reset);
		button.setOnClickListener(this);
		
		mTubeView = (TubeView) view.findViewById(R.id.tubeview);
		//redefine color from attrs, which is from xml
		int[][] colors = loadCubesColor(context, attrs);
		mTubeView.setCubesColor(colors);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TubePlayer);
		String commandFile=null;
		commandFile = a.getString(R.styleable.TubePlayer_commandsfile);
		//set commandFile
		try {
			if(commandFile!=null)
				mTubeView.getActionsFrom(commandFile);
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//set heightPercent
		float heightPercent = a.getFloat(R.styleable.TubePlayer_heightpercent, 1.0f);
		mTubeView.setHeightPercent(heightPercent);
		
		//set rotatecube and rotateface
		boolean bRotateCube = a.getBoolean(R.styleable.TubePlayer_rotatecube, true);
		mTubeView.enableFeature(TubeRenderer.FEATURE_ROTATECUBE, bRotateCube);
		boolean bRotateFace = a.getBoolean(R.styleable.TubePlayer_rotateface, true);
		mTubeView.enableFeature(TubeRenderer.FEATURE_ROTATEFACE, bRotateFace);
		
		a.recycle();
	}
	
	private int[][] loadCubesColor(Context context, AttributeSet attrs){
		int[][] colors = new int[Tube.CubesEachTube][Cube.FacesEachCube];
		
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TubePlayer);
        for(int i=0;i<Tube.CubesEachTube;i++){
        	for(int j=0;j<Cube.FacesEachCube;j++) {
        		int idx = i*Cube.FacesEachCube+j;
        		colors[i][j] = a.getColor(idx, Color.gray.toInt()); 
        	}
        }
        a.recycle();
        
		return colors;
	}
	
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.play:
			mTubeView.play();
			break;
		case R.id.previousStep:
			mTubeView.backward();
			break;
		case R.id.nextStep:
			mTubeView.forward();
			break;
		case R.id.reset:
			mTubeView.reset();
			break;
		}
	}
}
