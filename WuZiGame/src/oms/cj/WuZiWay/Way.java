package oms.cj.WuZiWay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import oms.cj.WuZiLogic.BoardPositionEvent;
import oms.cj.WuZiLogic.ISuggest;
import oms.cj.WuZiLogic.VirtualWuZi;
import oms.cj.WuZiLogic.VirtualWuZiBoard;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

@SuppressWarnings("unused")
public class Way implements Runnable, ISuggest{
	private final static String TAG="Way";
	private static String[] rowstr={"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
	private static String[] colstr={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O"};
	protected final static int mQiZiProbeLevel=1;
	
	protected VirtualWuZi mV;
	protected Handler mHandler;
	private BoardPositionEvent mBPEvent;
	public void setBoardPositionEvent(BoardPositionEvent bpEvent){
		mBPEvent = bpEvent;
	}
	public BoardPositionEvent getBoardPositionEvent(){
		return mBPEvent;
	}
	
	Way(VirtualWuZi v, Handler handler){
		mV=v;
		mHandler = handler;
	}
	
	public static String posString(int row, int col){
		String str="";
		
		str = str + "(" + rowstr[row] + "," + colstr[col] + ")";
		return str;
	}
	public static String posString(int[] pos){
		String str="";
		
		str = str + "(" + rowstr[pos[0]] + "," + colstr[pos[1]] + ")";
		return str;
	}	
	public String posString(ArrayList<Integer> list){
		String str="";
		
		for(int i=0;i<list.size();i++){
			int[] pos = mV.oneDim2twoDim(list.get(i));
			str = str + posString(pos) + " ";
		}
		
		return str;
	}
	
	public int[] randomPosFromList(ArrayList<int[]> list){
		int[] pos;
		
		if(list.size()==0)
			return null;
		else {
			int size = list.size();
			Random r = new Random();
			int idx = r.nextInt(size);
			pos = list.get(idx);
		}
		
		return pos;
	}
	
	public void getCandidateEmptyPos(int idx, int level, ArrayList<Integer> list){
		getCandidateEmptyPos(mV, idx, level, list);
	}
	
	public ArrayList<Integer> getCandidateEmptyPos(int type){
		return getCandidateEmptyPos(mV, type);
	}
	
	public static void getCandidateEmptyPos(VirtualWuZiBoard v, int idx, int level, ArrayList<Integer> list){
		if(level==0)
			return;
		else {
			int[] pos=v.oneDim2twoDim(idx);
			for(int i=0;i<VirtualWuZi.LINEDIRTOTAL;i++){
				int[] newPos=VirtualWuZi.delta(pos, i, -1);
				if(!v.inBox(newPos))
					continue;
				else {
					int newIdx=v.twoDim2oneDim(newPos);
					if(v.getQiZiType(newPos)==VirtualWuZi.EMPTY&&list.indexOf(newIdx)==-1){
						list.add(newIdx);
					}
					getCandidateEmptyPos(v, newIdx, level-1, list);
				}
				newPos=VirtualWuZi.delta(pos, i, 1);
				if(!v.inBox(newPos))
					continue;
				else {
					int newIdx=v.twoDim2oneDim(newPos);
					if(v.getQiZiType(newPos)==VirtualWuZi.EMPTY&&list.indexOf(newIdx)==-1){
						list.add(newIdx);
					}
					getCandidateEmptyPos(v, newIdx, level-1, list);					
				}
			}
		}		
	}
	
	//根据mQiZiProbeLevel来返回可选的空位列表
	public static ArrayList<Integer> getCandidateEmptyPos(VirtualWuZiBoard v, int type){
		ArrayList<Integer> candidateList=new ArrayList<Integer>();
		ArrayList<Integer> qiZiList = v.getQiZiList(type);
		
		for(int i=0;i<qiZiList.size();i++){
			int idx = qiZiList.get(i);
			getCandidateEmptyPos(v, idx, mQiZiProbeLevel, candidateList);
		}
		
		return candidateList;		
	}
	
	public static HashSet<Integer> getCandidateEmptyPos(VirtualWuZiBoard v){
		ArrayList<Integer> list1=getCandidateEmptyPos(v, VirtualWuZiBoard.BLACK);
		ArrayList<Integer> list2=getCandidateEmptyPos(v, VirtualWuZiBoard.WHITE);
		
		HashSet<Integer> set = new HashSet<Integer>(list1);
		set.addAll(list2);
		
		return set;
	}

	@Override
	public void run() {
		int[] myPos = suggestPosition();
		//send message back to VirtualWuZi's handler
		if(mHandler!=null){
			Message msg = new Message();
			msg.what = VirtualWuZi.NEWPOSITION;
			msg.obj = myPos;
			mHandler.sendMessage(msg);
		}
	}

	@Override
	public int[] suggestPosition() {
		RandomWay way=new RandomWay(mV);
		return way.suggestPosition();
	}
	
	protected int[] getByCandidateEmptyPos(){
		int myType=mV.getMyQiZiType(), hisType=mV.getHisQiZiType();
		
		ArrayList<Integer> list=getCandidateEmptyPos(myType);
		int size=list.size();
		if(size!=0){
			Random r=new Random();
			int randomNum=r.nextInt(size);
			int idx=list.get(randomNum);
			return mV.oneDim2twoDim(idx);
		} 
		
		list=getCandidateEmptyPos(hisType);
		size=list.size();
		if(size!=0){
			Random r=new Random();
			int randomNum=r.nextInt(size);
			int idx=list.get(randomNum);
			return mV.oneDim2twoDim(idx);
		}
		
		return null;
	}
	
	protected int[] getByRandomWay(){
		RandomWay rway=new RandomWay(mV);
		return rway.suggestPosition();
	}
}
