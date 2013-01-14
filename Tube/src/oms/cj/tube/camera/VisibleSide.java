package oms.cj.tube.camera;

import oms.cj.tube.R;
import oms.cj.tube.component.Color;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;

public class VisibleSide extends TableLayout implements View.OnClickListener{
	private final static String TAG = "VisibleSide";
	private ImageView[][] imageMatrix = new ImageView[3][3];
	private View.OnClickListener mOnClickListener;
	
	public void setOnClickListener(View.OnClickListener listener){
		mOnClickListener = listener;
	}
	public VisibleSide(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=layoutInflater.inflate(R.layout.visibleside, this); 
		
		imageMatrix[0][0] = (ImageView) view.findViewById(R.id.element00);
		imageMatrix[0][1] = (ImageView) view.findViewById(R.id.element01);
		imageMatrix[0][2] = (ImageView) view.findViewById(R.id.element02);
		imageMatrix[1][0] = (ImageView) view.findViewById(R.id.element10);
		imageMatrix[1][1] = (ImageView) view.findViewById(R.id.element11);
		imageMatrix[1][2] = (ImageView) view.findViewById(R.id.element12);
		imageMatrix[2][0] = (ImageView) view.findViewById(R.id.element20);
		imageMatrix[2][1] = (ImageView) view.findViewById(R.id.element21);
		imageMatrix[2][2] = (ImageView) view.findViewById(R.id.element22);
		
		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++)
				imageMatrix[i][j].setOnClickListener(this);
	}

	public void setCellColor(int idx, int c){
		if(idx<0||idx>8){
			Log.e(TAG+".setCellColor", "exception! invalid idx="+idx);
			return;
		}
		int x = idx/3;
		int y = idx%3;
		setCellColor(x, y, c);
	}
	public void setCellColor(int x, int y, Color color){
		if(x<0||x>2){
			Log.e(TAG+".setCellColor", "exception! invalid x="+x);
			return;
		}
		if(y<0||y>2){
			Log.e(TAG+".setCellColor", "exception! invalid y="+y);
			return;
		}
		
		imageMatrix[x][y].setBackgroundColor(color.toInt());
	}
	public void setCellColor(int x, int y, int c){
		Color color = new Color(c);
		
		setCellColor(x, y, color);
	}
	public void setCellColor(Color[][] colors){
		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++){
				imageMatrix[i][j].setBackgroundColor(colors[i][j].toInt());
			}
	}
	
	public void setDefaultImage(int resID){
		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++){
				imageMatrix[i][j].setImageResource(resID);
			}
	}
	public void setImage(int x, int y, int RId){
		if(x<0||x>2){
			Log.e(TAG+".setCellColor", "exception! invalid x="+x);
			return;
		}
		if(y<0||y>2){
			Log.e(TAG+".setCellColor", "exception! invalid y="+y);
			return;
		}
		
		imageMatrix[x][y].setImageResource(RId);
	}

	@Override
	public void onClick(View v) {
		if(mOnClickListener!=null){
			mOnClickListener.onClick(this);
		}
	}
	
	//size metric is dp
	public void setCellSize(int size){
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE));
        Display dp = wm.getDefaultDisplay();
        dp.getMetrics(metrics);
        
		int byPixel = (int) (size * metrics.density);
		Log.i(TAG+".setCellSize", "byPixel="+byPixel);
		Log.i(TAG+".setCellSize", "metrics.density="+metrics.density);
		
		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++){
				imageMatrix[i][j].setAdjustViewBounds(true);
				imageMatrix[i][j].setMaxHeight(byPixel);
				imageMatrix[i][j].setMaxWidth(byPixel);
			}
	}
}
