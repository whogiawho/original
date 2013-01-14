package oms.cj.tube.tutor;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import oms.cj.tube.ITubeRenderCallbacks;
import oms.cj.tube.PlayRenderer;
import oms.cj.tube.PlayView;
import oms.cj.tube.R;
import oms.cj.tube.TubeBasicRenderer;
import oms.cj.tube.component.Tube;

public class TutorQuizView extends PlayView {

	public TutorQuizView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TubePlayer);
		//set heightPercent
		float heightPercent = a.getFloat(R.styleable.TubePlayer_heightpercent, 1.0f);
		setHeightPercent(heightPercent);
		
		boolean bRandomized = a.getBoolean(R.styleable.TubePlayer_randomized, false);
		boolean bOverride = a.getBoolean(R.styleable.TubePlayer_override, true);
		a.recycle();
		
		TutorQuizRenderer _renderer = new TutorQuizRenderer(null, bRandomized, 
				TubeBasicRenderer.TEXTURENOID,(Activity)getContext());
		init(_renderer);
		
		if(bOverride) {
			//redefine color from attrs, which is from xml
			int[][] colors = Tube.loadCubesColor(this.getContext(), attrs);
			setCubesColor(colors);
		}
	}

	public void setCubesColor(final int[][] colors){
		queueEvent(new Runnable(){
			public void run() {
				PlayRenderer _renderer = getRenderer();
				_renderer.setCubesColor(colors);
			}
		});
	}
	
    public void setCallbacks(ITubeRenderCallbacks cb){
    	TutorQuizRenderer r = (TutorQuizRenderer) getRenderer();
    	r.setCallbacks(cb);
    }
}
