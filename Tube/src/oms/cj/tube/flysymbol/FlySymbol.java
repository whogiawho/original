package oms.cj.tube.flysymbol;

import java.util.Stack;

import oms.cj.tube.R;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.AdapterView;

public class FlySymbol extends LinearLayout implements AdapterView.OnItemSelectedListener{
	private final static String TAG = "FlySymbol";
	private FlySymbolGallery mG;
	
	public FlySymbol(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater factory = LayoutInflater.from(getContext());
		View v = factory.inflate(R.layout.flysymbol, this);
		mG = (FlySymbolGallery) v.findViewById(R.id.galleryofsymbols);
		
		mG.setCallbackDuringFling(false);
		mG.setOnItemSelectedListener(this);
	}

	@SuppressWarnings("unchecked")
	public void init(Stack<RotateAction> commands) {
		Stack<RotateAction> c = (Stack<RotateAction>) commands.clone();
		c.add(new RotateAction(null, Tube.CCW));
		
		ArrayAdapter<RotateAction> adapter = new ArrayAdapter<RotateAction>(
				getContext(),
				R.layout.flysymbolitem,
				c);
		mG.setAdapter(adapter); 
	}

	private final static int FORWARD = 0;
	private final static int BACKWARD = 1;
	private void scroll(int type){
		View selectedV = mG.getSelectedView();
		int idx = mG.indexOfChild(selectedV);
		switch(type){
		case FORWARD:
		default:
			if(idx<mG.getChildCount()-1)
				idx++;
			break;
		case BACKWARD:
			if(idx>0)
				idx--;			
			break;
		}
		View nextView = mG.getChildAt(idx);
	    int x = nextView.getLeft()+nextView.getWidth()/2;
	    int y = nextView.getTop()+nextView.getHeight()/2;
	    String out = String.format("x=%d, y=%d", x, y);
	    Log.i(TAG+".scroll", out);
	    
	    MotionEvent event = MotionEvent.obtain(100, 100, MotionEvent.ACTION_DOWN, x, y, 0);
	    mG.onDown(event); 
	    boolean res = mG.onSingleTapUp(null);
	    Log.i(TAG+".scroll", "onSingleTapUp return =" + res);		
	}
	
	public void forward(){
		scroll(FORWARD);
	}
	
	public void backward(){
		scroll(BACKWARD);
	}

	public void reset(){
		mG.setSelection(0);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String out = String.format("position(%d) is selected!", position);
		Log.i(TAG+".onItemSelected", out);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Log.i(TAG+"onNothingSelected", "being called!");
	}
}
