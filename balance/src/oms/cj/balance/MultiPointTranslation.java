package oms.cj.balance;

import java.util.ArrayList;
import android.graphics.Point;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

@SuppressWarnings("unused")
public class MultiPointTranslation extends Animation{
	//mptlist用于描述动画的路径上的点
	ArrayList<Point> mptlist;  
	ArrayList<Double> mdistance=new ArrayList<Double>();
	Double totaldistance=0.0d;
	//调试用
	private static final String TAG="MultiPointTranslation";
	
    public MultiPointTranslation(ArrayList<Point> ptlist) {
    	mptlist=ptlist;
    	Point start, end;
    	
    	mdistance.add(0.0d);
    	
    	start=mptlist.get(0);    	
    	for(int i=1;i<ptlist.size();i++){
    		Double distance=new Double(0.0d);
    		end=mptlist.get(i);
    		distance=Math.hypot(end.x-start.x, end.y-start.y);
    		distance+=totaldistance;
    		
    		mdistance.add(distance);
    		totaldistance=distance;
    		start=mptlist.get(i);
    	}
    	
    	/*
    	for(Double d: mdistance){
    		Log.i(TAG, "MultiPointTranslation(...): "+"d="+d.toString());
    	}
    	Log.i(TAG, "MultiPointTranslation(...): "+"totaldistance="+totaldistance.toString());
    	*/
    }
    
    private int getidx(Double distance){
    	int i=0;
    	
    	while(i<mdistance.size() && mdistance.get(i) < distance){
    		i++;
    	}
    	
    	return i;
    }
    
    
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
    	Double currentdistance = totaldistance * interpolatedTime;
    	//Log.i(TAG, "applyTransformation(...): "+"currentdistance="+currentdistance.toString());
    	int idx = getidx(currentdistance);
    	if(idx==0){
    		t.getMatrix().setTranslate(mptlist.get(0).x, mptlist.get(0).y);
    		return;
    	}
    	Point startpoint = mptlist.get(idx-1);
    	Point endpoint = mptlist.get(idx);
        Double dx = new Double(startpoint.x);
        Double dy = new Double(startpoint.y);
    	Double startdistance = mdistance.get(idx-1);
    	Double enddistance= mdistance.get(idx);
    	Double rationx=(endpoint.x-startpoint.x)/(enddistance-startdistance), 
    		   rationy=(endpoint.y-startpoint.y)/(enddistance-startdistance);

        Double deltadistance=currentdistance-startdistance;        
        

       	dx = dx + deltadistance * rationx;
       	dy = dy + deltadistance * rationy;
        
        t.getMatrix().setTranslate(dx.floatValue(), dy.floatValue());
    }
}