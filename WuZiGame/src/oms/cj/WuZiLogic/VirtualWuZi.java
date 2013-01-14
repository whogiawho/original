package oms.cj.WuZiLogic;

import java.util.ArrayList;
import oms.cj.WuZiBoard.WuZiPuzzleBoard;
import oms.cj.WuZiWay.EWay1;
import oms.cj.WuZiWay.EWay2;
import oms.cj.WuZiWay.IEvaluate;
import oms.cj.WuZiWay.TypeMap;
import oms.cj.WuZiWay.Way;
import oms.cj.WuZiWay.Way1;
import oms.cj.WuZiWay.Way2;
import oms.cj.WuZiWay.Way2_1;
import oms.cj.WuZiWay.Way3;
import oms.cj.WuZiWay.Way4;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TextView;

@SuppressWarnings("unused")
public class VirtualWuZi extends VirtualWuZiBoard implements IWuZiQiBetweenRealAndVirtual{
	private final static String TAG = "VirtualWuZi";
	public final static boolean debugSwitch = true;
	public final static int IMPOSSIBLE = -2;
		
	public final static int QIZITYPECOUNT = 2;

	public final static int NEWPOSITION=0;

	public final static int WIN = 0;
	public final static int LOSE_CHAHNGLIAN = 1;        //禁手
	public final static int CONTINUE =2;
	public final static int LOSE_SANSAN = 3;
	public final static int LOSE_SISI = 4;
	public final static int TIE=5;
	private final static String[] strStateList={
		"WIN",
		"LOSE_CHAHNGLIAN",
		"CONTINUE",
		"LOSE_SANSAN",
		"LOSE_SISI",
	};
	
	public final static int L2R   = 0;
	public final static int T2B   = 1;
	public final static int LT2RB = 2;
	public final static int LB2RT = 3;
	//   / 3 LB2RT
	//  /
	// /
	//----->0  L2R
	//| \
	//|  \
	//|   \ 2 LT2RB
    //1 T2B
	
	//GUI widget interface
	TextView mStep;
	
	//VirtualWuZi's core data members
	private Way mWay;
	private int[] mFocus=null;
		
	public VirtualWuZi(int row, int col, LogicConfig config, TextView step){
		super(row, col, config);
		
		mStep = step;
		
		//by default mFocus is (row/2, col/2) 
		int[] focusPos={row/2,col/2};
		mFocus=focusPos;

		if(getConfig().mXianHouShou == LogicConfig.HouShou){
			//set Black to the center of qizi board
			set(focusPos, BLACK);
			logger.add(focusPos, BLACK);
			update(mStep, logger);
		}
		
		if(getConfig().mDifficulty==LogicConfig.EASY){
			mWay = new Way2(this, handler);
		} else if(getConfig().mDifficulty==LogicConfig.MEDIUM){
			mWay = new Way2_1(this, handler);
		} else if(getConfig().mDifficulty==LogicConfig.LITTLEHARD){
			mWay = new Way1(this, handler, getConfig().mStrategy);
			//对于littlehard，还要额外考虑策略iStrategy
		} else if(getConfig().mDifficulty==LogicConfig.HARD) {
			mWay = new Way3(this, handler, getConfig().mStrategy);
		} else if(getConfig().mDifficulty==LogicConfig.IMPOSSIBLE){
			mWay = new Way4(this, handler);
		} else {	//default is Way2
			mWay = new Way2(this, handler);
		}
	}

	public ArrayList<Integer> getQiZiList(){
		ArrayList<Integer> list1 = getQiZiList(this.getMyQiZiType());
		ArrayList<Integer> list2 = getQiZiList(this.getHisQiZiType());
		list1.addAll(list2);
		
		return list1;
	}
	
	public int[] getStartOfPoint(int[] pos, int dir){
		int[] start=delta(pos, dir, 0);
		int count=4;
		
		switch(dir){
		case L2R:
			while(count>0&&start[1]>0){
				count--; start[1]--;
			}
			break;
		case T2B:
			while(count>0&&start[0]>0){
				count--; start[0]--;
			}
			break;
		case LT2RB:
			while(count>0&&start[0]>0&&start[1]>0){
				count--;
				start=delta(start,dir,-1);
			}
			break;
		case LB2RT:
			while(count>0&&start[0]<getCols()-1&&start[1]>0){
				count--;
				start=delta(start,dir,-1);
			}
			break;
		}
		
		return start;
	}
	
	public boolean lineComposedOfAllIdenticalQiZi(int qiZiType, int[] start, int dir){
		boolean allIdenticalQiZi = true;
		
		for(int i=0;i<5;i++){
			int[] nextPos = delta(start, dir, i);
			if(getQiZiType(nextPos)!=qiZiType){
				allIdenticalQiZi = false;
				break;
			}
		}
		
		return allIdenticalQiZi;
	}
	
	public int checkWuZiIdentical(int qiZiType, int[] pos){
		int state = CONTINUE;
		
		//loop 4 directions for the point pos[]，检查是否有5子相连
		for(int i=0;i<LINEDIRTOTAL;i++){
			//set its start point，每个方向i均有个起始点
			int[] start = getStartOfPoint(pos, i);
			Log.d(TAG, "checkWuZiIdentical(...): " + "start=" + "(" + start[0] + "," + start[1] + ")");
			//loop at most 5 times
			for(int j=0;j<5;j++){
				//set its end point(start[] + 5 delta)
				int[] end = delta(start, i, 4); 
				if(!inBox(end))
					break;
				//check if the line <start, end> contains continuous identical qiZiType
				boolean allIdentical = lineComposedOfAllIdenticalQiZi(qiZiType, start, i);
				if(allIdentical){
					if(qiZiType == WHITE){		//白棋无禁手，白WIN  
						state = WIN;							
					} else {					//黑棋五子相连，那么检查是否长连
						//check if the line's 2 bounder point is WHITE or EMPTY
						boolean bChangLian=checkChangLian(qiZiType, i, start, end);
						if(getConfig().mChangLian && bChangLian){		//如果要检查黑长连禁手
							state = LOSE_CHAHNGLIAN;
						} else {				//黑WIN
							state = WIN;
						}
					}
					break;
				} else {
					start = delta(start, i, 1);
				}
			}
			if(state != CONTINUE)
				break;
		}
		
		return state;
	}

	private boolean checkChangLian(int qiZiType, int dir, int[] start, int[] end){
		boolean bChangLian=false;

		int[] bounderPos1 = delta(start, dir, -1);
		int[] bounderPos2 = delta(end, dir, 1);
		int pos1Type=IMPOSSIBLE, pos2Type=IMPOSSIBLE;
		if(inBox(bounderPos1))
			pos1Type = getQiZiType(bounderPos1);
		if(inBox(bounderPos2))
			pos2Type = getQiZiType(bounderPos2);
		boolean bHalf1 = !inBox(bounderPos1)||pos1Type!=qiZiType;
		boolean bHalf2 = !inBox(bounderPos2)||pos2Type!=qiZiType;
		bChangLian = !(bHalf1&&bHalf2);
		
		return bChangLian;
	}
	
	//位置pos的棋子类型为qiziType，返回该方向dir上是否有活四
	//list保存的是活四的4颗棋子
	public boolean isLiveSi(int qiZiType, int[] pos, int dir, ArrayList<Integer> list){
		boolean bLiveSi=false;
		
		for(int i=0;i<4;i++){
			if(list!=null)
				list.clear();
			int[] start=delta(pos, dir, i-3);
			if(!inBox(start))
				continue;
			int countQiZi=0;
			for(int j=0;j<4;j++){
				int[] checkPos=delta(start,dir,j);
				if(inBox(checkPos) && getQiZiType(checkPos)==qiZiType){
					countQiZi++;
					if(list!=null)
						list.add(new Integer(twoDim2oneDim(checkPos)));
				} else
					break;
			}
			if(countQiZi==4){
				int[] beforeStartPos=delta(start, dir, -1);
				int[] startPMinus = delta(beforeStartPos, dir, -1);
				boolean b1 = preHandleChangLian(startPMinus, qiZiType);
				int[] afterEndPos=delta(start, dir, 4);
				int[] endPPlus = delta(afterEndPos, dir, 1);
				boolean b2 = preHandleChangLian(endPPlus, qiZiType);
				if(inBox(beforeStartPos)&&getQiZiType(beforeStartPos)==EMPTY&&b1 &&
						inBox(afterEndPos)&&getQiZiType(afterEndPos)==EMPTY&&b2){
					bLiveSi=true;
					break;
				}
			}
		}
			
		return bLiveSi;
	}
	
	//list保存的是活3的3颗棋子，emptyPos保存的填入该空位形成活4的那个位置
	public boolean isLiveSan(int qiZiType, int[] pos, int dir, ArrayList<Integer> list, int[] emptyPos){
		boolean bLiveSan=false;
		
		for(int i=0;i<7;i++){
			if(list!=null)
				list.clear();
			int[] checkPos=delta(pos, dir, i-3);
			if(!inBox(checkPos)||getQiZiType(checkPos)!=EMPTY)
				continue;
			set(checkPos, qiZiType);
			if(isLiveSi(qiZiType, pos, dir, list)){
				bLiveSan=true;
				set(checkPos, EMPTY);
				if(list!=null)
					list.remove(new Integer(twoDim2oneDim(checkPos)));
				if(emptyPos!=null){
					emptyPos[0]=checkPos[0];
					emptyPos[1]=checkPos[1];
				}
				break;
			} else
				set(checkPos, EMPTY);
		}
		
		return bLiveSan;
	}

	public boolean isChongSan(int qiZiType, int[] pos, int dir){
		boolean bChongSan = false;
		
		for(int i=0;i<9;i++){
			int[] checkPos=delta(pos, dir, i-4);
			if(!inBox(checkPos)||getQiZiType(checkPos)!=EMPTY)
				continue;
			set(checkPos, qiZiType);
			if(getChongSiCount(qiZiType, pos, dir, null, null)==1){
				bChongSan = true;
				set(checkPos, EMPTY);
				break;
			} else
				set(checkPos, EMPTY);
		}
		
		return bChongSan;
	}
	
	public boolean isLive2(int qiZiType, int[] pos, int dir){
		boolean bLive2 = false;
		
		for(int i=0;i<7;i++){
			int[] checkPos=delta(pos, dir, i-3);
			if(!inBox(checkPos)||getQiZiType(checkPos)!=EMPTY)
				continue;
			set(checkPos, qiZiType);
			if(isLiveSan(qiZiType, pos, dir, null, null)){
				bLive2 = true;
				set(checkPos, EMPTY);
				break;
			} else
				set(checkPos, EMPTY);
		}
		
		return bLive2;
	}
	
	public boolean isChong2(int qiZiType, int[] pos, int dir){
		boolean bChong2 = false;
		
		for(int i=0;i<7;i++){
			int[] checkPos=delta(pos, dir, i-3);
			if(!inBox(checkPos)||getQiZiType(checkPos)!=EMPTY)
				continue;
			set(checkPos, qiZiType);
			if(isChongSan(qiZiType, pos, dir)){
				bChong2 = true;
				set(checkPos, EMPTY);
				break;
			} else
				set(checkPos, EMPTY);
		}
		
		return bChong2;		
	}
	
	public int checkSiSi(int qiZiType, int[] pos){
		int state = CONTINUE, count=0;
		
		for(int i=0;i<LINEDIRTOTAL;i++){	//loop 4 directions
			//先检查活四
			if(isLiveSi(qiZiType, pos, i, null)){
				count++;
				if(count==2){
					state=LOSE_SISI;
					break;
				} else
					continue;
			}
			//再检查冲四
			int cntChongSi=getChongSiCount(qiZiType, pos, i, null, null);
			Log.d(TAG, "checkSiSi(...): " + "cntChongSi=" + cntChongSi);
			if(cntChongSi==1)
				count++;
			if(count==2||cntChongSi>=2){
				state=LOSE_SISI;
				break;
			}
		}
		
		return state;
	}
 
	public int checkSanSan(int qiZiType, int[] pos){
		int state=CONTINUE, count=0;
		
		for(int i=0;i<LINEDIRTOTAL;i++){
			if(isLiveSi(qiZiType, pos, i, null))
				continue;
			if(getChongSiCount(qiZiType, pos, i, null, null)>=1)
				continue;
			if(isLiveSan(qiZiType, pos, i, null, null))
				count++;
		}

		if(count>=2){
			state=LOSE_SANSAN;
		}

		return state;
	}
	
	public int checkJinShou(int qiZiType, int[] pos){
		int state = CONTINUE;
	
		Log.i(TAG, "checkJinShou(...): " + "mConfig=" + getConfig().toString());
		if(getConfig().mSiSi){	//如果要检查黑44禁手
			state = checkSiSi(qiZiType, pos);
			if(state != CONTINUE)
				return state;
		} 
		if(getConfig().mSanSan){	//如果要检查黑33禁手	
			state = checkSanSan(qiZiType, pos);
			if(state != CONTINUE)
				return state;
		}

		return state;
	}
	
	private int doesWin(int qiZiType, int[] pos){
		int state = CONTINUE;
		
		//检查是否有五子相连
		state = checkWuZiIdentical(qiZiType, pos);
			
		//如果是黑且没赢没输，还要检查是否有三.三和四.四禁手
		if(qiZiType == BLACK && state == CONTINUE)
			state = checkJinShou(BLACK, pos); 
		
		return state;
	}
	
	private void addNewQiZi(int[] pos, int type, int focus, WuZiPuzzleBoard realBoard, TableLayout layout){
		int[] prevFocus=getFocus();
		if(prevFocus!=null){
			realBoard.setQiZi(layout, prevFocus, this.getQiZiType(prevFocus), WuZiPuzzleBoard.NORMAL);
		}

		set(pos, type);
		realBoard.setQiZi(layout, pos, type, focus);
	}
	
	private boolean isJinShou(int state){
		if(state == LOSE_CHAHNGLIAN)
			return true;
		else if(state == LOSE_SISI)
			return true;
		else if(state == LOSE_SANSAN)
			return true;
		else
			return false;
	}
	
	private void printList(){
		ArrayList<Integer> blackQiZiList=getQiZiList(BLACK);
		ArrayList<Integer> whiteQiZiList=getQiZiList(WHITE);
		ArrayList<Integer> emptyQiZiList=getQiZiList(EMPTY);
		Log.i(TAG, "number of blackqizi = " + blackQiZiList.size() + ";blackqizi list = " + blackQiZiList.toString());
		Log.i(TAG, "number of whiteqizi = " + whiteQiZiList.size() + ";whiteqizi list = " + whiteQiZiList.toString());
		Log.i(TAG, "number of empty =" + emptyQiZiList.size() + ";empty list = " + emptyQiZiList.toString());
	}
	
	private class Step{
		private int[] position;
		private int type;
		
		Step(int[] position, int type){
			this.setPosition(position);
			this.setType(type);
		}

		public void setPosition(int[] position) {
			this.position = position;
		}

		public int[] getPosition() {
			return position;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}
	
	private class SequenceLogger {
		private ArrayList<Step> mSteps = new ArrayList<Step>();
		private int mCurrent = 0;
		
		public void add(int[] pos, int type){
			Step s = new Step(pos, type);
			mSteps.add(s);
			mCurrent = mSteps.size()-1;
		}
		
		public Step getStep(int idx){
			return mSteps.get(idx);
		}
		public Step getCurrentStep(){
			return mSteps.get(mCurrent);
		}
		
		public int getCounter(){
			return mCurrent;
		}
		
		public String getCurrentRoundString(){
			String str="";
			if(mCurrent%2==0){
				Step s = getCurrentStep();
				int type = s.getType();
				int[] pos = s.getPosition();
				str=String.format("第%d回合：%s%s\n", logger.getCounter()/2, strQiZiList[type], Way.posString(pos));
				
			} else {
				Step s0 = getStep(mCurrent-1);
				Step s1 = getStep(mCurrent);
				int type0=s0.getType(), type1=s1.getType();
				int[] pos0=s0.getPosition(), pos1=s1.getPosition();
				str=String.format("第%d回合：%s%s %s%s\n", logger.getCounter()/2, strQiZiList[type0], Way.posString(pos0), strQiZiList[type1], Way.posString(pos1) );
			}
			
			return str;
		}
	}
	
	private void update(TextView step, SequenceLogger logger){
		String out = logger.getCurrentRoundString();
		step.setText(out);
	}
	
	private SequenceLogger logger = new SequenceLogger();
	private boolean bInSuggestion = false;
	private synchronized boolean getInSuggestion(){
		return bInSuggestion;
	}
	private synchronized void setInSuggestion(boolean b){
		bInSuggestion = b;
	}
	
	@Override
	public void onQiZi2Position(BoardPositionEvent e) {
		if(getInSuggestion())
			return;
		
		//从e中取出puzzleboard，和layout
		WuZiPuzzleBoard realBoard = (WuZiPuzzleBoard) e.getSource();
		TableLayout layout = e.getLayout();
		int[] position = e.getPosition();
		int state;
		
		if(getQiZiType(position) != EMPTY){
			return;
		} else {
			//设置它的新棋子到实棋盘和虚棋盘
			Log.i(TAG, "onQiZi2Position(...): " + "u put qiZi to: " + Way.posString(position));
			addNewQiZi(position, getHisQiZiType(), WuZiPuzzleBoard.FOCUS, realBoard, layout);
			setFocus(position);
			logger.add(position, getHisQiZiType());
			update(mStep, logger);
			
			if(debugSwitch){
				printList();
				
				int qiZiType = getQiZiType(position);
				TypeMap tMap = new Way1ToGenTM(this).generateTypeMap(this, qiZiType, null, null);
				Log.i(TAG+".onQiZiPosition", tMap.toString());
				Log.i(TAG+".onQiZiPosition", "---------------------------------------");
				tMap = new Way2ToGenTM(this).generateTypeMap(this, qiZiType, null, null);
				Log.i(TAG+".onQiZiPosition", tMap.toString());
				Log.i(TAG+".onQiZiPosition", "---------------------------------------");
			}

			realBoard.enableInput(layout, false);
			state = doesWin(getHisQiZiType(), position);
			Log.d(TAG, "onQiZi2Position(...): " + strQiZiList[getHisQiZiType()] + ";state=" + strStateList[state]);
			if(state == WIN || isJinShou(state)){
				realBoard.enableInput(layout, true);
				//通知WuZiPuzzleBoard这个消息
				realBoard.gameOver(layout, getHisQiZiType(), state);
			} else {	//此处可以fork a thread来完成
				if(debugSwitch){
					int qiZiType = getQiZiType(position);
					TypeMap tMap = new Way1ToGenTM(this).generateTypeMap(this, qiZiType, null, null);
					IEvaluate eWay = new EWay1();
					String out = String.format("%s score = %d", strQiZiList[qiZiType], eWay.evaluate(tMap));
					Log.i(TAG+".onQiZiPosition", out);					
				}

				mWay.setBoardPositionEvent(e);
				setInSuggestion(true);
				Thread workerThread = new Thread(mWay);
				workerThread.start();
			}
		}
	}

	@Override
	public void setFocus(int[] focus){
		mFocus=focus;
	}
	
	@Override
	public int[] getFocus() {
		
		return mFocus;
	}
	
	@Override
	public int getQiZiType(int x, int y) {
		return super.getQiZiType(x, y);
	}
	
	@Override
	public int getQiZiType(int[] pos){
		return super.getQiZiType(pos);
	}
	
	private boolean isThereEmptyPosition(){
		boolean bEmpty=true;
		
		ArrayList<Integer> list = this.getQiZiList(VirtualWuZi.EMPTY);
		if(list.size()==0)
			bEmpty=false;
		
		return bEmpty;
	}
	
    private Handler handler=new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what){
    		case NEWPOSITION:
				BoardPositionEvent e = mWay.getBoardPositionEvent();
				WuZiPuzzleBoard realBoard = (WuZiPuzzleBoard) e.getSource();
				TableLayout layout = e.getLayout();
    			int[] pos = (int[]) msg.obj;
				if(pos!=null){
					//设置俺的新棋子到实棋盘和虚棋盘
					Log.i("handler.handleMessage", "I put qiZi to: " + Way.posString(pos));
					addNewQiZi(pos, getMyQiZiType(), WuZiPuzzleBoard.FOCUS, realBoard, layout);
					setFocus(pos);
					logger.add(pos, getMyQiZiType());
					update(mStep, logger);
					printList();
				
					int state = doesWin(getMyQiZiType(), pos);
					Log.d("handler.handleMessage(...): ", strQiZiList[getMyQiZiType()] + ";state=" + strStateList[state]);
					realBoard.enableInput(layout, true);
					if(state == WIN || isJinShou(state)){
						//通知WuZiPuzzleBoard这个消息
						realBoard.gameOver(layout, getMyQiZiType(), state);
					} else if(!isThereEmptyPosition()){
						realBoard.gameOver(layout, getMyQiZiType(), TIE);
					}				
				} else {
					realBoard.gameOver(layout, getMyQiZiType(), TIE);
				}
				setInSuggestion(false); 

    			break;
    		default:
    			break;
    		}
    	}
    };
    
	public int[] moveFocus(int dirID){
		int[] currentPos = getFocus(), newPos;
		int dir, times;
		
		switch(dirID){
		case LogicConfig.LEFT:
			dir = L2R; times = -1;
			break;
		case LogicConfig.RIGHT:
			dir = L2R; times = 1;
			break;
		case LogicConfig.UP:
			dir = T2B; times = -1;
			break;
		case LogicConfig.DOWN:
			dir = T2B; times = 1;
			break;
		case LogicConfig.UPLEFT:
			dir = LT2RB; times = -1;
			break;
		case LogicConfig.UPRIGHT:
			dir = LB2RT; times = 1;
			break;
		case LogicConfig.DOWNLEFT:
			dir = LB2RT; times = -1;
			break;
		case LogicConfig.DOWNRIGHT:
			dir = LT2RB; times = 1;
			break;
		case LogicConfig.STILL:
		default:
			dir = L2R; times = 0;
			break;
		}
		newPos = delta(currentPos, dir, times);
		if(!inBox(newPos))
			newPos = currentPos;
		setFocus(newPos);
		
		return newPos; 
	}
}