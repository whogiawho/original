package oms.cj.tube.way1;

import java.util.Stack;

import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;

import android.util.Log;

public class Step7 {
	private final static String TAG = "Step7";
	private Tube mTube;
	
	public Step7(Tube t){
		mTube = t;
	}
	
	public Tube getTube(){
		return mTube;
	}
	
	private Stack<RotateAction> commonSolution(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		int[] sides = {Tube.left, Tube.middle, Tube.right};
		commands.push(new RotateAction(sides, Tube.CCW));
		mTube.setRotate(sides, Tube.CCW);
		
		Step1.concat(commands, rotateSide(Tube.right, Tube.CW));
		Step1.concat(commands, rotateSide(Tube.right, Tube.CW));
		Step1.concat(commands, rotateSide(Tube.bottom, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.bottom, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.right, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.top, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.right, Tube.CW));
		Step1.concat(commands, rotateSide(Tube.bottom, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.bottom, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.right, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.top, Tube.CW));
		Step1.concat(commands, rotateSide(Tube.right, Tube.CCW));
		
		commands.push(new RotateAction(sides, Tube.CW));
		mTube.setRotate(sides, Tube.CW);
		
		return commands;
	}
	public Stack<RotateAction> adaptCorner2Side(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		while(getCountInPlace()!=4){
			int idx = getIdxOfIdenticalCorner();
			Log.i(TAG+".adaptCorner2Side", "idx = "+idx);
			if(idx==-1){
				Step1.concat(commands, commonSolution());
			} else {
				while(idx<3){
					Step1.concat(commands, rotateSide(Tube.top, Tube.CCW));
					idx++;
				}
				Step1.concat(commands, commonSolution());
			}
		}
		
		Color c1 = mTube.getVisibleFaceColor(Tube.front, 6);
		Color c2 = mTube.getVisibleFaceColor(Tube.front, 4);
		while(!c1.equals(c2)){
			Step1.concat(commands, rotateSide(Tube.top, Tube.CCW));
			c1 = mTube.getVisibleFaceColor(Tube.front, 6);
			c2 = mTube.getVisibleFaceColor(Tube.front, 4);
		}
		
		return commands;
	}
	
	private int getCountInPlace(){
		int count = 0;
		Cube[] cubes = mTube.getCubes();
		
		for(int i=0;i<sideCornerEdgeOfTop.length;i++){
			int cube1 = sideCornerEdgeOfTop[i][0], cube2 = sideCornerEdgeOfTop[i][2];
			int face1 = sideCornerEdgeOfTop[i][1], face2 = sideCornerEdgeOfTop[i][3];
			Color c1 = cubes[cube1].getColor(face1);
			Color c2 = cubes[cube2].getColor(face2);
			if(c1.equals(c2))
				count++;
		}
		
		return count;
	}
	private int[][] sideCornerEdgeOfTop= {
			{8,  Tube.back,  6,  Tube.back},
			{6,  Tube.left,  24, Tube.left},
			{24, Tube.front, 26, Tube.front},
			{26, Tube.right, 8,  Tube.right},
	};
	private int getIdxOfIdenticalCorner(){
		int idx = -1;
		Cube[] cubes = mTube.getCubes();
		
		for(int i=0;i<sideCornerEdgeOfTop.length;i++){
			int cube1 = sideCornerEdgeOfTop[i][0], cube2 = sideCornerEdgeOfTop[i][2];
			int face1 = sideCornerEdgeOfTop[i][1], face2 = sideCornerEdgeOfTop[i][3];
			Color c1 = cubes[cube1].getColor(face1);
			Color c2 = cubes[cube2].getColor(face2);
			if(c1.equals(c2)){
				idx = i;
				break;
			}
		}		
		return idx;
	}
	private Stack<RotateAction> rotateSide(int side, int dir){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		int[] sides = {side};
		commands.push(new RotateAction(sides, dir));
		mTube.setRotate(sides, dir);
		
		return commands;
	}
}
