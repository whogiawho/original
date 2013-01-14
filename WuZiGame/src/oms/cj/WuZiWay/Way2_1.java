package oms.cj.WuZiWay;

import java.util.ArrayList;

import oms.cj.WuZiLogic.VirtualWuZi;
import android.os.Handler;
import android.util.Log;

//Way2_1从Way2而来，它的判断序列基于Way是：
//1.  能成5						Step1
//2.  能成活4					Step1
//3.1 增加了能成冲4活3的判断       	Step2
//3.2 增加了能成双三的判断            	Step2
//4.1 能成冲4					Step3
//4.2  增加了能成活三的判断            	Step3的第二个判断
//5.  可选空位					Step4
//6.  随机位置					Step4

public class Way2_1 extends Way2{
	private final static String TAG="Way2.1";
	
	public Way2_1(VirtualWuZi v, Handler handler){
		super(v, handler);
	}
	
	public int[] getChong4andLive3(int type, ArrayList<Integer> avaiList){
		int[] suggestPos=null;
		boolean isChong4, isLive3;
		
		for(int i=0;i<avaiList.size();i++){
			int[] pos = mV.oneDim2twoDim(avaiList.get(i));
			if(type==VirtualWuZi.BLACK){	//检查是否为三三，or四四禁手
				if(mV.checkJinShou(type, pos)!=VirtualWuZi.CONTINUE){
					continue;
				}
			}
			
			mV.set(pos, type);
			isChong4=false; isLive3=false;
			for(int j=0;j<VirtualWuZi.LINEDIRTOTAL;j++){
				int chong4Count=mV.getChongSiCount(type, pos, j, null, null);
				if(chong4Count==1){
					isChong4=true;
				}
				if(mV.isLiveSan(type, pos, j, null, null)){
					isLive3=true;
				}
			}
			mV.set(pos, VirtualWuZi.EMPTY);
			
			if(isChong4 && isLive3){
				suggestPos=pos;
				break;
			}
		}
		
		return suggestPos;
	}

	public int[] getDoubleLive3(int myType, int hisType){
		int[] pos=null;
		ArrayList<Integer> list1=getCandidateEmptyPos(myType), list2=getCandidateEmptyPos(hisType);
		
		if(mV.getConfig().mSanSan){	//如果要检查黑三三禁手
			if(myType==VirtualWuZi.WHITE){//如果计算机是白棋，且能成双活三，那么返回该suggestPos
				pos=__getDoubleLive3(myType, list1);
				if(pos!=null)
					return pos;
			} else {//否则计算机是黑棋，如果白棋有双活三，返回该suggestPos
				pos=__getDoubleLive3(hisType, list2);
				if(pos!=null)
					return pos;			
			}
		} else {
			pos=__getDoubleLive3(myType, list1);
			if(pos!=null)
				return pos;
			pos=__getDoubleLive3(hisType, list2);
			if(pos!=null)
				return pos;			
		}
		
		return pos;
	}
	
	private int[] __getDoubleLive3(int type, ArrayList<Integer> avaiList){
		int[] suggestPos=null;
		
		for(int i=0;i<avaiList.size();i++){
			int[] pos = mV.oneDim2twoDim(avaiList.get(i));
			mV.set(pos, type);
			if(mV.checkSanSan(type, pos)==VirtualWuZi.LOSE_SANSAN){	//检查是否为双三
				suggestPos = pos;
				mV.set(pos, VirtualWuZi.EMPTY);
				break;
			}
			mV.set(pos, VirtualWuZi.EMPTY);
		}
		
		return suggestPos;		
	}
	
	public int[] getLive3(int type, ArrayList<Integer> avaiList){
		ArrayList<int[]> list=new ArrayList<int[]>();
		int[] suggestPos=null;
		
		for(int i=0;i<avaiList.size();i++){
			int[] pos = mV.oneDim2twoDim(avaiList.get(i));
			mV.set(pos, type);

			if(type==VirtualWuZi.BLACK && mV.checkSanSan(type, pos)==VirtualWuZi.LOSE_SANSAN && mV.getConfig().mSanSan){
				//如果要检查三三禁手，且pos是三三禁手，那么忽略掉pos
				mV.set(pos, VirtualWuZi.EMPTY);
				continue;
			}
			for(int j=0;j<VirtualWuZi.LINEDIRTOTAL;j++){
				if(mV.isLiveSan(type, pos, j, null, null)){
					list.add(pos);
					break;
				}
			}
			mV.set(pos, VirtualWuZi.EMPTY);
		}
		suggestPos = randomPosFromList(list);
		
		if(suggestPos!=null)
			Log.i(TAG, "getLive3(...): " + "type=" + VirtualWuZi.strQiZiList[type] + ";" + "pos =" + Way.posString(suggestPos));
		return suggestPos;		
	}

	private int[] Step1(int myType, int hisType){
		int[] pos=null;
		
		//如果计算机或对方有活4或冲4，那么返回该suggestPos
		pos=getLive4orChong4Must(myType);
		if(pos!=null)
			return pos;
		pos=getLive4orChong4Must(hisType);
		if(pos!=null)
			return pos;
		
		//如果计算机或对方有活3，那么返回该suggestPos
		pos=getLive3Must(myType);
		if(pos!=null)
			return pos;
		pos=getLive3Must(hisType);
		if(pos!=null)
			return pos;

		return pos;
	}
	
	private int[] Step2(int myType, int hisType){
		int[] pos=null;
		
		//如果能成冲四.活三，那么返回该suggestPos
		ArrayList<Integer> list1=getCandidateEmptyPos(myType), list2=getCandidateEmptyPos(hisType);
		pos=getChong4andLive3(myType, list1);
		if(pos!=null)
			return pos;		
		pos=getChong4andLive3(hisType, list2);
		if(pos!=null)
			return pos;	
		
		//检查是否有三三存在
		pos=getDoubleLive3(myType, hisType);
		if(pos!=null)
			return pos;
		
		return pos;
	}
	
	private int[] Step3(int myType, int hisType){
		int[] pos=null;

		//如果计算机或对方有冲3，那么随机返回一个可能的冲三空位
		pos=getChong3Opt(myType);
		if(pos!=null)
			return pos;
		pos=getChong3Opt(hisType);
		if(pos!=null)
			return pos;

		//到此处，牌面上最好型是活2；所以，在可选空位中挑一个位置，使之能成活3，
		ArrayList<Integer> list1=getCandidateEmptyPos(myType), list2=getCandidateEmptyPos(hisType);
		pos=getLive3(myType, list1);
		if(pos!=null)
			return pos;
		pos=getLive3(hisType, list2);
		if(pos!=null)
			return pos;

		return pos;
	}

	private int[] Step4(int myType, int hisType){
		int[] pos=null;

		//随机返回可选空位中的一个
		pos=this.getByCandidateEmptyPos();
		if(pos!=null)
			return pos;
		
		//最后的方法是用RandomWay随机返回一个空位
		pos=getByRandomWay();
		return pos;
	}
	
	@Override
	public int[] suggestPosition() {
		//先更新deathList
		updateDeathList();
		
		int[] suggestPos=null;
		int hisType=mV.getHisQiZiType();
		int myType=mV.getMyQiZiType();
			
		suggestPos=Step1(myType, hisType);
		if(suggestPos!=null)
			return suggestPos;
		
		suggestPos=Step2(myType, hisType);
		if(suggestPos!=null)
			return suggestPos;
		
		suggestPos=Step3(myType, hisType);
		if(suggestPos!=null)
			return suggestPos;
		
		Log.i(TAG, "suggestPosition(...): " + "Comming to Step4!");
		suggestPos=Step4(myType, hisType);
		if(suggestPos!=null)
			return suggestPos;
		
		return suggestPos;
	}
}