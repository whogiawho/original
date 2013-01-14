package oms.cj.tube.way2;

import java.util.ArrayList;
import java.util.Stack;

import android.util.Log;

import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;

public class Step1 {
	private final static String TAG = "Step1";
	private Tube mTube;
	
	public Step1(Tube t){
		mTube = t;
	}
	
	public Tube getTube(){
		return mTube;
	}
	
	private int searchCenterColor(Color c){
		int position = 0; int centerIdx = 4;
		
		Log.i(TAG+".searchCenterColor", "c="+c.toInt());
		for(int i=0;i<Tube.SidesEachTube;i++){
			Color color = mTube.getVisibleFaceColor(i, centerIdx); 
			Log.i(TAG+".searchCenterColor", "color="+color.toInt());
			if(color.equals(c)){
				position = mTube.getCube(i, centerIdx); 
				break;
			}	
		}
		Log.i(TAG+".searchCenterColor", "white="+Color.white.toInt());
		Log.i(TAG+".searchCenterColor", "red="+Color.red.toInt());
		Log.i(TAG+".searchCenterColor", "blue="+Color.blue.toInt());
		Log.i(TAG+".searchCenterColor", "green="+Color.green.toInt());
		Log.i(TAG+".searchCenterColor", "orange="+Color.orange.toInt());
		Log.i(TAG+".searchCenterColor", "yellow="+Color.yellow.toInt());
		
		return position;
	}
	
	public Stack<RotateAction> moveTo16(Color c){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int colorPos = searchCenterColor(c);
//		Log.i(TAG+".moveTo16", "color Pos="+colorPos);
		if(colorPos==16)
			return commands;
		
		final int[] idx2SideOf = {Tube.standing, Tube.middle};
		final int[][] centerMovesOfCCW = {
				{12, 10, 14, 16},
				{22, 10, 4, 16},
		};
		int i,j=0;
_L0:
		for(i=0;i<centerMovesOfCCW.length;i++){
			for(j=0;j<centerMovesOfCCW[i].length;j++){
//				Log.i(TAG+".moveYellowTo16", "("+i+","+j+") "+"centerMovesOfCCW[i][j]="+centerMovesOfCCW[i][j]);
				if(centerMovesOfCCW[i][j]==colorPos)
					break _L0;
			}
		}
//		Log.i(TAG+".moveYellowTo16", "i="+i);
		
		
		int count=0;
		for(;centerMovesOfCCW[i][j]!=16;j++){
			count++;
		}
		moveSide(commands, idx2SideOf[i], -count, mTube);

		return commands;
	}

	public static int[][] centerPos = {
		{4,  Tube.back},
		{14, Tube.right},
		{22, Tube.front},
		{12, Tube.left},
	};
	private static int[] topEdgePos = {
		7, 17, 25, 15,
	};
	private static int[][] infoOfEdgePos = {
		// ----- top -----
		{7,  Tube.top,    4,  Tube.back},
		{15, Tube.top,    12, Tube.left},
		{25, Tube.top,    22, Tube.front}, 
		{17, Tube.top,    14, Tube.right},
		// ----- bottom -----
		{1,  Tube.bottom, 4,  Tube.back},
		{9,  Tube.bottom, 12, Tube.left},
		{19, Tube.bottom, 22, Tube.front}, 
		{11, Tube.bottom, 14, Tube.right},
		// ----- middle -----
		{21, Tube.left,   22, Tube.front},
		{21, Tube.front,  12, Tube.left},
		{23, Tube.front,  14, Tube.right},
		{23, Tube.right,  22, Tube.front},
		{5,  Tube.right,  4,  Tube.back},
		{5,  Tube.back,   14, Tube.right},
		{3,  Tube.back,   12, Tube.left},
		{3,  Tube.left,   4,  Tube.back},
	};
	private boolean isComplete(Color c) {
		Cube[] cubes = mTube.getCubes();
		boolean bAllInPlace = true;

		for(int i=0;i<topEdgePos.length;i++){
			int topPos = topEdgePos[i];
			int idx = getIndex(topPos, Tube.top);
			int commonSide = infoOfEdgePos[idx][3];
			Color c1 = cubes[topPos].getColor(Tube.top);
			Color c2 = cubes[topPos].getColor(commonSide);
			int centerPos = infoOfEdgePos[idx][2];
			Color c3 = cubes[centerPos].getColor(commonSide);
			if(!c1.equals(c)||!c2.equals(c3)){
				bAllInPlace = false;
				break;
			}
		}
		
		return bAllInPlace;
	}
	private final static int INFINITE = 4;
	private static int getIndex(int pos, int type){
		int idx = -1;
			
		for(int i=0;i<infoOfEdgePos.length;i++){
			if(infoOfEdgePos[i][0]==pos && infoOfEdgePos[i][1]==type){
				idx = i;
				break;
			}
		}
		return idx;
	}
	private static int getCenterIdx(int pos){
		int idx = -1;
		
		for(int i=0;i<centerPos.length;i++){
			if(pos==centerPos[i][0]){
				idx = i;
				break;
			}
		}
		return idx;
	}
	//Return value range: [0, 1, 2, 3, INFINITE], which means turn such times CW
	public static int getDistance(int pos, Tube t, int type){
		Cube[] cubes = t.getCubes();
		int distance = INFINITE;

		int idx = getIndex(pos, type);
		if(idx==-1)
			return distance;
		
		//get pos cube's color of another side
		int commonSide = infoOfEdgePos[idx][3];
		Color c1 = cubes[pos].getColor(commonSide);
		Log.i(TAG+".getDistance", "c1="+c1.toString());
		
		//loop all centers to calculate the distance
		int centerCube = infoOfEdgePos[idx][2];
		int startCenterIdx = getCenterIdx(centerCube);
		for(int i=0;i<centerPos.length;i++){
			int centerIdx = (startCenterIdx+i)%4;
			commonSide = centerPos[centerIdx][1];
			centerCube = centerPos[centerIdx][0];
			Color c2 = cubes[centerCube].getColor(commonSide);
			Log.i(TAG+".getDistance", "c2="+c2.toString());
			if(c2.equals(c1)){
				distance = i;
				break;
			}
		}
		
		return distance;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Integer>[] getCandidateTopPos(Color c){
		ArrayList<Integer>[] lists = new ArrayList[] {
				new ArrayList<Integer>(),
				new ArrayList<Integer>(),
				new ArrayList<Integer>(),
				new ArrayList<Integer>(),
				new ArrayList<Integer>(),
		};
		Cube[] cubes = mTube.getCubes();
		
		for(int i=0;i<topEdgePos.length;i++){
			int topPos = topEdgePos[i];
			if(cubes[topPos].getColor(Tube.top).equals(c)){
				int distance = getDistance(topPos, mTube, Tube.top);
				lists[distance].add(topPos);
			}	
		}
		
		return lists;
	}
	
	private ArrayList<EdgeInfo> getCandidateEdgeList(Color color){
		Cube[] cubes = mTube.getCubes();
		ArrayList<EdgeInfo> list = new ArrayList<EdgeInfo>();
		int[][] candidateCubes = {
				// middle layer
				{3,  Tube.left},
				{3,  Tube.back},
				{5,  Tube.back},
				{5,  Tube.right},
				{23, Tube.right},
				{23, Tube.front},
				{21, Tube.front},
				{21, Tube.left},
				// bottom cube, bottom white 
				{1,  Tube.bottom},
				{11, Tube.bottom},
				{19, Tube.bottom},
				{9,  Tube.bottom},
				// bottom cube, side white
				{1,  Tube.back},
				{11, Tube.right},
				{19, Tube.front},
				{9,  Tube.left},
				// top cube, side white
				{7,  Tube.back},
				{17, Tube.right},
				{15, Tube.left},
				{25, Tube.front},
		};
		
		for(int i=0;i<candidateCubes.length;i++){
			int cube = candidateCubes[i][0];
			int side = candidateCubes[i][1];
			Color c = cubes[cube].getColor(side);
			if(c.equals(color)){
				list.add(new EdgeInfo(cube, side));
				Log.i(TAG+".getCandidateEdgeList", "cube="+cube+";side="+Tube.LAYERSTR_F[side]);
			}
		}
		
		return list;
	}
	public Stack<RotateAction> moveEdge2Top(Color c){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		while(!isComplete(c)){
			int topCommonDistance = -1;
			//update topCommonDistance and its top cubes which are ready in place
			ArrayList<Integer>[] list = getCandidateTopPos(c);
			for(int i=0;i<list.length-1;i++){
				int size = list[i].size();
				if(size!=0 && size>topCommonDistance){
					topCommonDistance = i;
				}
			}
			Log.i(TAG+".moveEdge2Top", "topCommonDistance="+topCommonDistance);
			
			//update candidate edge cubes
			ArrayList<EdgeInfo> cList = getCandidateEdgeList(c);
			Log.i(TAG+".moveEdge2Top", "cList.size="+cList.size());
			
			if(cList.size()!=0){
				EdgeInfo e = cList.get(0);
				Stack<RotateAction> subCommands = e.move(mTube, topCommonDistance, list);
				Step1.concat(commands, subCommands);
			} else {
				if(list[topCommonDistance].size()==4){
					//move top side to make all 4 cubes in place
					moveSide(commands, Tube.top, topCommonDistance, mTube);
				} else {
					//select one cube, which is not in list[topCommonDistance]
					//, and do a CW move
					int i=0;
					for(;i<topEdgePos.length;i++){
						int cube = topEdgePos[i];
						if(!list[topCommonDistance].contains(cube))
							break;
					}
					int idx = getIndex(topEdgePos[i], Tube.top);
					moveSide(commands, infoOfEdgePos[idx][3], 1, mTube);
				}
			}
		}
		
		moveTop2Bottom(commands, mTube);
		return commands;
	}
	
	private void moveTop2Bottom(Stack<RotateAction> commands, Tube t) {
		int[] sides = {Tube.front, Tube.back, Tube.standing};
		RotateAction r = new RotateAction(sides, Tube.CW);
		
		for(int i=0;i<2;i++){
			commands.push(r);
			t.setRotate(sides, Tube.CW);
		}
	}
	
	public static void concat(Stack<RotateAction> s1, Stack<RotateAction>s2){
		for(int i=0;i<s2.size();i++){
			s1.push(s2.get(i));
		}
	}
	
	//count<0 means CCW, count>0 means CW
	public static void moveSide(Stack<RotateAction> commands, int side, int count, Tube t){
		int[] sides = {side};
		moveSide(commands, sides, count, t);
	}
	public static void moveSide(Stack<RotateAction> commands, int[] sides, int count, Tube t){
		int dir = Tube.CW;
		
		if(count<0) {
			count = -count;
			dir = Tube.CCW;
		}
		
		if(count==3){
			count=1;
			if(dir==Tube.CCW)
				dir=Tube.CW;
			else
				dir=Tube.CCW;
		}
		
		RotateAction r = new RotateAction(sides, dir);
		for(int i=0;i<count;i++){
			commands.push(r);
			t.setRotate(sides, dir);
		}
	}
}
