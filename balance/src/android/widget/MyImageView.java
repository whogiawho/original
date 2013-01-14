package android.widget;

import oms.cj.balance.game;
import oms.cj.balance.plate;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.AttributeSet;

public class MyImageView extends ImageView {
	private Matrix mMatrix = new Matrix();
	
	private final static int DeltaX = plate.getimagesize();
	private final static int DeltaY = plate.getimagesize();
	
	public MyImageView(Context context) {
		super(context);
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private static Point doDelta(int dragtype){
		Point p = new Point();

		switch(dragtype){
		case game.TOPLEFT:
		default:
			p.x = -DeltaX;
			p.y = -DeltaY;
			break;
		case game.TOPRIGHT:
			p.x = DeltaX;
			p.y = -DeltaY;			
			break;
		}
		
		return p;
	}
	
	public void setDelta(int x, int y, int dragtype){
		Point p = doDelta(dragtype);
		
		mMatrix.reset();
		mMatrix.setTranslate(x + p.x, y + p.y);
		setImageMatrix(mMatrix);
	}

	public static Point getDelta(Point p, int dragtype){
		Point d = doDelta(dragtype);

		Point q = new Point();

		q.x = p.x + d.x;
		q.y = p.y + d.y;
		
		return q;
	}
	
	public static Point getCenter(Point p){
		Point q = new Point();
		
		q.x = p.x+plate.getimagesize()/2;
		q.y = p.y+plate.getimagesize()/2;
		
		return q;
	}
}
