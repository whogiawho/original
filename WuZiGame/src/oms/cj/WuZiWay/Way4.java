package oms.cj.WuZiWay;

import java.util.Arrays;
import oms.cj.WuZiLogic.IGenerateTypeMap;
import oms.cj.WuZiLogic.VirtualWuZi;
import oms.cj.WuZiLogic.VirtualWuZiBoard;
import oms.cj.WuZiLogic.Way1ToGenTM;
import android.os.Handler;
import android.util.Log;

public class Way4 extends Way {
	public final static String TAG = "Way4";
	public final static int Depth = 6;
	public final static int VIRGIN = 0;
	public final static int INVALID = 1;
	
	private int[][] mInvalid;
	private IGenerateTypeMap mWayGen;
	
	//for statistics
	private int mSuggestTimes=0;
	private int mNodeCount[] = new int[mV.getRows()*mV.getCols()];
	private int stepCounters[] = new int[8];
	
	public void incStepCounter(int step){
		stepCounters[step]++;
	}
	public IGenerateTypeMap getIOfGenTypeMap(){
		return mWayGen;
	}
	public int[][] getInvalidMatrix(){
		return mInvalid;
	}
	
	public Way4(VirtualWuZi v, Handler handler){
		super(v, handler);
		
		for(int i=0;i<mNodeCount.length;i++){
			mNodeCount[i]=0;
		}
		
		mInvalid = new int[v.getRows()*v.getCols()][VirtualWuZiBoard.LINEDIRTOTAL];
		for(int i=0;i<mInvalid.length;i++)
			Arrays.fill(mInvalid[i], VIRGIN);
		
		mWayGen = new Way1ToGenTM(v);
	}
	
	@Override
	public int[] suggestPosition() {
		int[] retPos = null;
		
		mWayGen.updateMatrixInvalid(mInvalid);
		
		Node root = new Node1(this, mV, mV.getMyQiZiType());
		mNodeCount[mSuggestTimes] = root.evaluate(Depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
		mSuggestTimes++;
		Log.i(TAG+".suggestPosition", Arrays.toString(mNodeCount));
		Log.i(TAG+".suggestPosition", "mSuggestTimes="+mSuggestTimes);
		
		int idx = root.getIndex();
		root.print();
		if(idx==-1){
			String out = String.format("idx=%d, %s", idx, mV.boardToString());
			throw new IllegalStateException(out);
		} else {
			retPos = root.getCalculatedPos(idx);			
		}
		
		Log.i(TAG+".suggestPosition", Arrays.toString(stepCounters));
		
		return retPos;
	}
}
