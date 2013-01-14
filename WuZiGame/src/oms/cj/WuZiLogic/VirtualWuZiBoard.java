package oms.cj.WuZiLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.util.Log;
import oms.cj.WuZiWay.Way;

public class VirtualWuZiBoard {
	private final static String TAG = "VirtualWuZiBoard";
	public final static int LINEDIRTOTAL = 4;	
	public final static int EMPTY = -1;
	public final static int BLACK = 0;
	public final static int WHITE = 1;
	public final static int INVALID=0;
	public final static int CHONG=1;
	public final static int LIVE=2;
	private final static int[][] linedelta = {
		{0, 1},		//L2R
		{1, 0},		//T2B
		{1, 1},		//LT2RB
		{-1, 1}, 	//LB2RT
	};
	public final static String[] strLiveOrChong={
		"无效", "冲", "活", 
	};
	public final static String[] dirStrList = {
		"水平", "垂直", "135对角", "45对角",
	};
	public final static String[] strQiZiList={
		"黑", "白",
	};
	public final static String[] strLiveChongType={
		"",
		"CHONG1",
		"CHONG2",
		"CHONG3",
		"CHONG4",
		"CHONG5",
		"LIVE1",
		"LIVE2",
		"LIVE3",
		"LIVE4",
		"LIVE5",
	};
	public final static int CHONG1 = CHONG*1;
	public final static int CHONG2 = CHONG*2;	
	public final static int CHONG3 = CHONG*3; 
	public final static int CHONG4 = CHONG*4;	
	public final static int CHONG5 = CHONG*5;
	public final static int LIVE1 = 5+1;
	public final static int LIVE2 = 5+2;
	public final static int LIVE3 = 5+3;
	public final static int LIVE4 = 5+4;
	public final static int LIVE5 = 5+5;	

	public final static Integer[] priority = {
		CHONG5,
		LIVE4,
		CHONG4,
		LIVE3,
		CHONG3,
		LIVE2,
		CHONG2,
		LIVE1,
		CHONG1,
	};
	public final static ArrayList<Integer> priorityList = new ArrayList<Integer>(Arrays.asList(priority));
	
	private int mRow, mCol;
	private int mMyQiZi, mHisQiZi;
	private LogicConfig mConfig;
	private int[][] mVBoard;

	VirtualWuZiBoard(int row, int col, LogicConfig config){
		mRow = row;
		mCol = col;
		mConfig = config;
		mVBoard = new int[mRow][mCol];
		
		if(getConfig().mXianHouShou == LogicConfig.HouShou){
			mMyQiZi = BLACK; mHisQiZi = WHITE;
		} else {
			mMyQiZi = WHITE; mHisQiZi = BLACK;
		}
		
		for(int i=0;i<mRow;i++)
			for(int j=0;j<mCol;j++){
				mVBoard[i][j] = VirtualWuZi.EMPTY;
			}
	}

	public VirtualWuZiBoard(VirtualWuZiBoard v){
		this(v.getBoard(), v.getConfig());
		
		mMyQiZi = v.getMyQiZiType();
		mHisQiZi = v.getHisQiZiType();
	}
	
	VirtualWuZiBoard(int[][] board, LogicConfig config){
		mRow = board.length;
		mCol = board[0].length;
		mVBoard = new int[mRow][mCol];
		mConfig = config;
 
		for(int i=0;i<mRow;i++)
			System.arraycopy(board[i], 0, mVBoard[i], 0, board[i].length);
	}
	
	public int getHisQiZiType(){
		return mHisQiZi;
	}
	public int getMyQiZiType(){
		return mMyQiZi;
	}
	
	public int[][] getBoard(){
		return mVBoard;
	}
	
	public int getRows(){
		return mRow;
	}
	public int getCols(){
		return mCol;
	}
	public LogicConfig getConfig(){
		return mConfig;
	}
	
	public void set(int idx, int type){
		int[] pos = this.oneDim2twoDim(idx);
		set(pos, type);
	}
	public void set(int[] pos, int type){
		set(pos[0], pos[1], type);
	}
	public void set(int row, int col, int type){
		mVBoard[row][col] = type;
	}
	
	public int getDistance(int idx1, int idx2){
		int[] pos1 = this.oneDim2twoDim(idx1);
		int[] pos2 = this.oneDim2twoDim(idx2);
		return getDistance(pos1, pos2);
	}
	
	public int getDistance(int[] pos1, int[] pos2){
		int distance = -1;
		int deltaX = Math.abs(pos1[0] - pos2[0]);
		int deltaY = Math.abs(pos1[1] - pos2[1]);
		
		if(deltaX==0){
			return deltaY;
		} else if(deltaY==0) {
			return deltaX;
		} else if(deltaX==deltaY) {
			return deltaX;
		}
		
		return distance;
	}
	
	public int getDistance(int qiZiType, int[] pos, int dir, int[] startP, int[] endP){
		int distance;
		
		//negative direction
		int count=0;
		int checkPos[]=VirtualWuZi.delta(pos, dir, count);
		while(inBox(checkPos) && getQiZiType(checkPos)==qiZiType){
			count--;
			checkPos = VirtualWuZi.delta(pos, dir, count);
		}
		int start = count;
		if(startP!=null)
			System.arraycopy(checkPos, 0, startP, 0, 2);
		
		//positive direction
		count=0;
		checkPos=VirtualWuZi.delta(pos, dir, count);
		while(inBox(checkPos) && getQiZiType(checkPos)==qiZiType){
			count++;
			checkPos = VirtualWuZi.delta(pos, dir, count);
		}
		int end = count;
		if(endP!=null)
			System.arraycopy(checkPos, 0, endP, 0, 2);
		
		distance = end - start;
		
		return distance;
	}
	
	public boolean inBox(int[] pos){
		if(pos[0]<0||pos[0]>mRow-1)
			return false;
		if(pos[1]<0||pos[1]>mCol-1)
			return false;
		
		return true;
	}
	
	public int getQiZiType(int x, int y) {
		return mVBoard[x][y];
	}
	
	public int getQiZiType(int[] pos){
		return mVBoard[pos[0]][pos[1]];
	}

	public int[] oneDim2twoDim(int oneDimIndex){
		return oneDim2twoDim(mCol, oneDimIndex);
	}
	public int twoDim2oneDim(int[] pos){
		return twoDim2oneDim(pos[0], pos[1]);
	}
	public int twoDim2oneDim(int row, int col){
		int oneDimIndex=-1;
		
		oneDimIndex = row *mCol + col;
		return oneDimIndex;
	}
	
	public void handleEx4Live(int[] pos, int dir, 
			HashSet<Integer> set, ArrayList<Integer> steps){
		int stepSize = steps.size();
		switch(stepSize){
		case 3: //adjacent LIVE2
			int[] negative = VirtualWuZiBoard.delta(pos, dir, -1);
			int[] positive = VirtualWuZiBoard.delta(pos, dir, 1);
			if(getQiZiType(negative)!=VirtualWuZiBoard.EMPTY ||
					getQiZiType(positive)!=VirtualWuZiBoard.EMPTY)
				set.add(steps.get(1));
			break;
		case 4:
			set.add(steps.get(1));
			set.add(steps.get(2));
			break;
		}
	}
	
	public void handleEx4Chong(int[] pos, int dir, 
			HashSet<Integer>set, ArrayList<Integer> steps){
		int stepSize = steps.size();
		switch(stepSize){
		case 2:	//chong3
			set.add(steps.get(1));
			break;
		case 3: //chong2
			set.add(steps.get(1));
			set.add(steps.get(2));
			break;
		}
	}
	
	public ArrayList<Integer> getQiZiList(int type){
		ArrayList<Integer> list=new ArrayList<Integer>();
		
		for(int i=0;i<mRow;i++)
			for(int j=0;j<mCol;j++){
				if(getQiZiType(i,j)==type)
					list.add(twoDim2oneDim(i,j));
			}
		
		return list;
	}
	
	//位置pos的棋子类型为qiziType，返回该方向dir上的冲四的数目
	//list是冲四的棋子位置列表，emptyList是冲四的空格位置列表
	public int getChongSiCount(int qiZiType, int[] pos, int dir, ArrayList<Integer> list, ArrayList<Integer> emptyList){
		int cntChongSi=0;
		ArrayList<Integer> idxList=new ArrayList<Integer>();
		ArrayList<Integer> candidateEmptyList=new ArrayList<Integer>();
		
		for(int i=0;i<5;i++){
			idxList.clear(); candidateEmptyList.clear(); 
			int[] start=delta(pos, dir, i-4);
			if(!inBox(start))
				continue;
			int qiZiCnt=0, emptyCnt=0;
			for(int j=0;j<5;j++){
				int[] checkPos=delta(start,dir,j);
				if(inBox(checkPos)){
					int type=getQiZiType(checkPos);
					if(type==qiZiType){
						qiZiCnt++;
						idxList.add(new Integer(twoDim2oneDim(checkPos)));
					} else if(type==EMPTY){
						emptyCnt++;
						candidateEmptyList.add(new Integer(twoDim2oneDim(checkPos)));
					} else
						break;
				} else 
					break;
			}
			if(qiZiCnt==4&&emptyCnt==1){
				int[] beforeStartPos=delta(start, dir, -1);
				int[] afterEndPos=delta(start, dir, 5);
				boolean half1=preHandleChangLian(beforeStartPos, qiZiType); 
				boolean half2=preHandleChangLian(afterEndPos, qiZiType);
				if(half1&&half2){
					cntChongSi++;
					if(list!=null){
						for(int k=0;k<idxList.size();k++){
							if(list.indexOf(idxList.get(k))==-1)
								list.add(idxList.get(k));
						}
					}
					if(emptyList!=null){
						emptyList.add(candidateEmptyList.get(0));
					}
				}	
			}
		}
		return cntChongSi;
	}

	public boolean preHandleChangLian(int[] pos, int qiZiType){
		boolean b = false;
		if(qiZiType==WHITE ||
				(!mConfig.mChangLian&&qiZiType==BLACK))
			b = true;
		else {
			if(!inBox(pos)||(inBox(pos)&&getQiZiType(pos)!=qiZiType))
				b = true;			
		}
		
		return b;
	}
	
	public static int[] delta(int[] start, int dir, int times){
		int[] deltaPos = { start[0], start[1] };
		
		deltaPos[0] += times*linedelta[dir][0];
		deltaPos[1] += times*linedelta[dir][1];
		
		return deltaPos;
	}
	
	public String toString(Set<Integer> steps){
		String out = "";
		
		if(steps==null)
			return out;
		
		Iterator<Integer> itr = steps.iterator();
		while(itr.hasNext()){
			int idx = itr.next();
			int[] pos = oneDim2twoDim(idx);
			out = out + Way.posString(pos) + "  ";
		}
		
		return out;
	}
	public void print(ArrayList<ArrayList<Integer>> sets){
		for(int i=0;i<sets.size();i++){
			ArrayList<Integer> steps = sets.get(i);
			String out="size="+steps.size()+": ";
			out += toString(new HashSet<Integer>(steps));
			Log.i(TAG+".print", out);
		}
	}
	public String boardToString(){
		String out=String.format("%4d: %s", 0, "A B C D E F G H I J K L M N O\n");
		for(int i=0;i<getRows();i++){
			String out1=String.format("%4d: ", i+1);
			for(int j=0;j<getCols();j++){
				int type = getQiZiType(i,j);
				if(type==EMPTY)
					out1+="空";
				else 
					out1+=strQiZiList[type];
			}
			out+=out1+"\n \n";
		}

		return out;
	}
	
	public static String ResultListToString(ArrayList<Result> rList){
		String out = "";
		
		for(int i=0;i<rList.size();i++){
			Result r = rList.get(i);
			out += r.toString() + "\n";
		}
		
		return out;
	}
	public static String toString(ArrayList<int[]> posList){
		String out = "";
		
		for(int i=0;i<posList.size();i++){
			int[] pos = posList.get(i);
			out += Way.posString(pos) + " ";
		}
		return out;
	}	
	public static String lcToString(int[] ret){
		String str="";
		if(ret[0]==INVALID)
			str = strLiveOrChong[INVALID];
		else
			str = String.format("%s%d", strLiveOrChong[ret[0]], ret[1]);

		return str;
	}
	
	public static int getType(int ret0, int ret1){
		if(ret0==INVALID||ret0==CHONG)
			return ret0*ret1;
		else
			return 5+ret1;
	}
	public static int getType(int[] ret){
		return getType(ret[0], ret[1]);
	}

	public static int getLevel(int type){
		int level=0;
		
		int attr = getAttr(type);
		switch(attr){
		case CHONG:
			level = type;
			break;
		case LIVE:
			level = type - 5;
			break;
		case INVALID:
			break;
		}
		
		return level;
	}
	public static int getAttr(int type){
		int ret = INVALID;
		
		int idx = priorityList.indexOf(type);
		if(idx!=-1){
			if(idx%2==0)
				ret = CHONG;
			else
				ret = LIVE;
		}
		
		return ret;
	}
	
	private static int[] oneDim2twoDim(int col, int oneDimIndex){
		int[] twoDimIndex = new int[2];
		
		twoDimIndex[0] = oneDimIndex/col;
		twoDimIndex[1] = oneDimIndex%col;
		
		return twoDimIndex;
	} 
	public static int[] oneDim2twoDim(VirtualWuZiBoard b, int oneDimIndex){
		return oneDim2twoDim(b.getCols(), oneDimIndex);
	}

	public static int reverse(int type){
		if(type==BLACK)
			return WHITE;
		else
			return BLACK;
	}
}
