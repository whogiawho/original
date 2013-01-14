package oms.cj.tube.component;

import java.io.Serializable;
import java.util.Arrays;

import android.util.Log;

public class RotateAction implements Serializable{
	/**
	 * 
	 */	
	private final static long serialVersionUID = 5292655208378317578L+3;
	private final static String TAG = "RotateAction";
	
	private int[] whichLayer;	//+1 int-->int[]
	private int whichDir;
	private int whichAngle;		//+2 new field
	private String mRemark;		//+3 new field
	
	public RotateAction(RotateAction r){
		whichLayer = r.whichLayer.clone();
		whichDir = r.whichDir;
		whichAngle = r.whichAngle;
		mRemark = r.mRemark;
	}

	public RotateAction(int[] layer, int dir, int angle, String remark){
		this(layer, dir, angle);
		mRemark = remark;
	}
	public RotateAction(int layer, int dir, int angle, String remark){
		this(layer, dir, angle);
		mRemark = remark;
	}
	public RotateAction(int layer, int dir, int angle){
		this(layer, dir);
		whichAngle = angle;
	}
	public RotateAction(int[] layer, int dir, int angle){
		this(layer, dir);
		whichAngle = angle;
	}
	public RotateAction(int[] layer, int dir){
		whichLayer = layer;
		whichDir = dir;
		whichAngle = 90;
		mRemark = "";
	}
	public RotateAction(int layer, int dir){
		int[] layers = {layer};
		whichLayer = layers;
		whichDir = dir;
		whichAngle = 90;
		mRemark = "";
	}
	
	public String getRemark(){
		return mRemark;
	}
	public int[] getLayer(){
		return whichLayer;
	}
	public int getDir(){
		return whichDir;
	}
	public int getAngle(){
		return whichAngle;
	}
	public static String toString(int[] layer){
		String layers="";
		
		for(int i=0;i<layer.length;i++){
			layers += Tube.layer2String(layer[i], Tube.LAYERSTRTYPE_CAP);
		}
		
		return layers;
	}
	public String toString(){
		String symbol = "";

		if(whichLayer==null)
			return symbol+"  ";
		
		//calculate layers of rotation
		String layers = "";
		switch(whichLayer.length){
		case 1:
		default:
			layers = Tube.layer2String(whichLayer[0], Tube.LAYERSTRTYPE_CAP);
			break;
		case 2:
			layers = Tube.mapTwoLayers2One(whichLayer[0], whichLayer[1]);
			break;
		case 3:
			int axisType = Tube.LAYERAXIS[whichLayer[0]];
			int i;
			for(i=1;i<whichLayer.length;i++){
				if(Tube.LAYERAXIS[whichLayer[i]]==axisType)
					continue;
			}
			if(i==whichLayer.length)
				layers = Tube.AXISSTR[axisType];
			else {
				Log.e(TAG+".toString", "invalid whichLayer[] = " + toString(whichLayer));
				layers = "!";
			}
			break;
		}
		
		//calculate direction of rotation
		String dir = "";
		switch(whichDir){
		case Tube.CW:
		default:
			break;
		case Tube.CCW:
			dir="'";
			break;
		}
		
		//calculate counts of rotation
		String rotateCount = "";
		switch(getAngle()){
		case 90:
		default:
			break;
		case 180:
			rotateCount += 2;
			break;
		}
		
		symbol = layers + dir + rotateCount;
		if(symbol.length()==1)
			symbol += " ";
		
		return symbol;
	}
	public static long getVersion(){
		return serialVersionUID;
	}
	
	public RotateAction reverse(){
		RotateAction r = new RotateAction(this);
		
		r.whichDir = Tube.reverseDir(whichDir);
		
		return r;
	}
	
	public static boolean equal(RotateAction r1, RotateAction r2){
		boolean bEqual = false;
	
		if(r1==r2)
			bEqual = true;
		else {
			boolean b0=false, b1=false, b2=false;;
			if(r1!=null&&r2!=null){
				b0 = Arrays.equals(r1.getLayer(), r2.getLayer());
				b1 = (r1.getDir() == r2.getDir());
				b2 = (r1.getAngle()==r2.getAngle());
			}		
			bEqual = b0 && b1 && b2;
		}
		
		return bEqual;
	}
}
