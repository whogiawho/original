package android.widget;

import oms.cj.balance.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

public class MyButton extends Button {
	private final static String TAG = "MyButton";
	
	private void init(){
		try {
			Resources res = getResources();
			XmlResourceParser xpp;
		
			//重新设置button的背景图像
			Drawable myDrawable = null;
			xpp = res.getXml(R.drawable.mybtn);
			myDrawable = Drawable.createFromXml(res, xpp);
			myDrawable.setAlpha(100);
			setBackgroundDrawable(myDrawable);

			//重新设置button Text的color
			ColorStateList cl = null;
			xpp = res.getXml(R.color.mycolor);
			cl = ColorStateList.createFromXml(res, xpp);
			Log.i(TAG, "onCreate(...):" + "isStateful=" + cl.isStateful());
			setTextColor(cl);
		}catch (Exception e) {}
	}
	
	public MyButton(Context context, AttributeSet attrs){
		super(context,attrs);
		init();
	}
	
	public MyButton(Context context) {
		super(context);
		init();
	}
}
