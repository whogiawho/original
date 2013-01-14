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

package oms.cj.balance;

import android.content.Context;
import android.graphics.Canvas;

import android.graphics.Paint;

import android.util.AttributeSet;
import android.util.Log;

import android.widget.TableRow;

@SuppressWarnings("unused")
public class MyTableRow extends TableRow {
	private static final String TAG="MyTableRow";	
	private static final int bordercolor=0xff1a1a1a;
	private int mrow;
	
	public MyTableRow(Context context, int row){
		super(context);
        setWillNotDraw(false);
        mrow=row;
	}
	
	public MyTableRow(Context context, AttributeSet attrs){
		super(context,attrs);
		setWillNotDraw(false);
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
        int cellwidth=plate.getimagesize()+game.bordersize;
        int cellheight=plate.getimagesize()+game.bordersize;

		super.onDraw(canvas);
	
//		Log.i(TAG,"onDraw(...): "+"height="+this.getHeight());
//		Log.i(TAG,"onDraw(...): "+"width="+this.getWidth());
		
		//画竖线
		Paint paint=new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(bordercolor);
		int xoffset=game.bordersize/2;
		int startx, starty, endx, endy;
		for(int i=0;i<plate.getwidth()+1;i++){
			startx=xoffset+i*(cellwidth);
			starty=0;
			endx=startx;
			endy=cellheight;
			paint.setStrokeWidth(game.bordersize);
				
			canvas.drawLine(startx, starty, endx, endy, paint);
		}
		
		//画上方横线
		startx=0;
		if(mrow==0)
			starty=game.bordersize/2;
		else
			starty=0;
		endx=cellwidth*plate.getwidth(); endy=starty;
		canvas.drawLine(startx, starty, endx, endy, paint);
//		Log.i(TAG,"onDraw(...): "+"startx="+startx+";starty="+starty+";endx="+endx+";endy="+endy);
		
		//画下方横线
		startx=0; 
		starty=starty+cellheight;
		endx=this.getWidth(); endy=starty;
		canvas.drawLine(startx, starty, endx, endy, paint);
//		Log.i(TAG,"onDraw(...): "+"startx="+startx+";starty="+starty+";endx="+endx+";endy="+endy);
	}
}
