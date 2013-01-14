package oms.cj.tube.way2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import android.util.Log;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;

public class EdgeInfo {
	// cube      side     |      side       dir     count
	// 21        left     |      front      CW      1
	// 21        front    |      left       CW      1
	// 23        front    |      right      CW      1
	// 23        right    |      front      CCW     1
	// 5         right    |      back       CCW     1
	// 5         back     |      right      CCW     1
	// 3         back     |      left       CCW     1
	// 3         left     |      back       CW      1
	// 1         bottom   |      back       CW      2
	// 9         bottom   |      left       CW      2
	// 19        bottom   |      front      CW      2
	// 11        bottom   |      right      CW      2
	// ----- calculate top moving count for above -----
	// 19        front    |      front      CCW     1
	// 25        front    |      front      CCW     1
	// 11        right    |      right      CCW     1
	// 17        right    |      right      CCW     1
	// 1         back     |      back       CCW     1
	// 7         back     |      back       CCW     1
	// 9         left     |      left       CCW     1
	// 15        left     |      left       CCW     1
	public final static int TYPE0 = 0;
	public final static int TYPE1 = 1;
	public final static int TYPE2 = 2;
	public final static int TYPEINVALID = -1;
	private final static String TAG = "EdgeInfo";
	private final static int SRCCUBE = 0;
	private final static int SRCSIDE = 1;
	private final static int DSTSIDE = 2;
	private final static int DSTDIR = 3;
	private final static int DSTCOUNT = 4;
	
	private int mCubeIdx;
	private int mSide;
	private int mType;
	private int mActionTableIdx;
	private static ArrayList<Integer> topEdgeList = new ArrayList<Integer>();
	private static HashMap<Integer, Integer> mapBottomTop = new HashMap<Integer, Integer>(); 
	static {
		topEdgeList.add(7);
		topEdgeList.add(17);
		topEdgeList.add(25);
		topEdgeList.add(15);

		mapBottomTop.put(1,  7);
		mapBottomTop.put(11, 17);
		mapBottomTop.put(19, 25);
		mapBottomTop.put(9,  15);		
	}
	public final static int[][] actionTable = {
		// ----- TYPE0 -----
		{21, Tube.left,    Tube.front, Tube.CW,  1},   //0
		{21, Tube.front,   Tube.left,  Tube.CW,  1},
		{23, Tube.front,   Tube.right, Tube.CW,  1},
		{23, Tube.right,   Tube.front, Tube.CCW, 1},
		{5,  Tube.right,   Tube.back,  Tube.CCW, 1},
		{5,  Tube.back,    Tube.right, Tube.CCW, 1},
		{3,  Tube.back,    Tube.left,  Tube.CCW, 1},
		{3,  Tube.left,    Tube.back,  Tube.CW,  1},   //7
		// ----- TYPE1 -----
		{1,  Tube.bottom,  Tube.back,  Tube.CW,  2},
		{9,  Tube.bottom,  Tube.left,  Tube.CW,  2},
		{19, Tube.bottom,  Tube.front, Tube.CW,  2},
		{11, Tube.bottom,  Tube.right, Tube.CW,  2},   //11
		// ----- TYPE2 -----
		{19, Tube.front,   Tube.front, Tube.CCW, 1},
		{25, Tube.front,   Tube.front, Tube.CCW, 1},
		{11, Tube.right,   Tube.right, Tube.CCW, 1},
		{17, Tube.right,   Tube.right, Tube.CCW, 1},
		{1,  Tube.back,    Tube.back,  Tube.CCW, 1},
		{7,  Tube.back,    Tube.back,  Tube.CCW, 1},
		{9,  Tube.left,    Tube.left,  Tube.CCW, 1},
		{15, Tube.left,    Tube.left,  Tube.CCW, 1},
	};
	
	private void calculate(int cubeIdx, int side){
		mActionTableIdx=-1;
		for(int i=0;i<actionTable.length;i++){
			int[] e = actionTable[i];
			if(cubeIdx==e[SRCCUBE] && side==e[SRCSIDE]){
				mActionTableIdx=i;
				break;
			}
		}
		if(mActionTableIdx!=-1){
			if(mActionTableIdx>=0&&mActionTableIdx<=7)
				mType = TYPE0;
			else if(mActionTableIdx>=8&&mActionTableIdx<=11)
				mType = TYPE1;
			else
				mType = TYPE2;
		} else {
			mType = TYPEINVALID;
			Log.e(TAG+".constructor", "invalid params! cubeIdx="+cubeIdx+";side="+side);
		}
	}
	EdgeInfo(int cubeIdx, int side){
		mCubeIdx = cubeIdx;
		mSide = side;
		calculate(mCubeIdx, mSide);
	}
	
	public int getType(){
		return mType;
	}
	
	//for TYPE0 and TYPE1
	private void adjustTopSide(Stack<RotateAction> commands, Tube t, int topCommonDistance){
		//move top side
		int pos = actionTable[mActionTableIdx][SRCCUBE];
		int side = actionTable[mActionTableIdx][SRCSIDE];
		int distance = Step1.getDistance(pos, t, side);
		Log.i(TAG+".adjustTopSide", "distance="+distance);
		
		int delta = topCommonDistance - distance;
		Step1.moveSide(commands, Tube.top, delta, t);
	}
	//for TYPE2
	private void adjustTopSide(Stack<RotateAction> commands, Tube t, int topCommonDistance, ArrayList<Integer>[] list){		
		if(topCommonDistance==-1){
			Log.e(TAG+".adjustTopSide(...)", "exception! topCommonDistance="+topCommonDistance);
			return;
		}
		
		int pos = actionTable[mActionTableIdx][SRCCUBE];
		//skip when pos is top cube
		if(topEdgeList.contains(pos))
			return;
		
		//move bottom layer
		int distance = 0;
		Integer topCube = mapBottomTop.get(pos);
		int idx = topEdgeList.indexOf(topCube);
		while(topCube!=null && list[topCommonDistance].contains(topCube)){
			idx = (idx+1)%topEdgeList.size();
			topCube = topEdgeList.get(idx);
			distance++;
		}
		if(topCube==null){
			Log.e(TAG+".adjustBottomSide", "exception! topCube=null!");
			return;
		}
		Step1.moveSide(commands, Tube.top, -distance, t);
	}
	public Stack<RotateAction> move(Tube t, int topCommonDistance, ArrayList<Integer>[] list){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		switch(mType){
		case TYPE0:
		case TYPE1:			
			if(topCommonDistance!=-1)
				adjustTopSide(commands, t, topCommonDistance);
			//move the cube to be in place
			moveSide(commands, t);
			break;
		case TYPE2:
			if(topCommonDistance!=-1)
				adjustTopSide(commands, t, topCommonDistance, list);
			moveSide(commands, t);
			break;
		case TYPEINVALID:
		default:
			break;
		}
		
		return commands;
	}
	
	private void moveSide(Stack<RotateAction> commands, Tube t){
		int[] sides = { actionTable[mActionTableIdx][DSTSIDE] };
		int dir = actionTable[mActionTableIdx][DSTDIR];
		int count = actionTable[mActionTableIdx][DSTCOUNT];
		RotateAction r = new RotateAction(sides, dir);
		
		for(int i=0;i<count;i++){
			t.setRotate(sides, dir);
			commands.push(r);
		}
	}
}
