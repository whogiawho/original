package oms.cj.tube.way1;

import java.util.Stack;

import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;

import android.util.Log;

public class Step8 {
	private final static String TAG = "Step8";
	private Tube mTube;
	
	public Step8(Tube t){
		mTube = t;
	}
	
	public Tube getTube(){
		return mTube;
	}
	
	private Stack<RotateAction> solution1(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		Step1.concat(commands, rotateSide(Tube.right, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.top, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.right, Tube.CW));
		Step1.concat(commands, rotateSide(Tube.top, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.right, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.top, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.top, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.right, Tube.CW));
		
		return commands;
	}
	
	private Stack<RotateAction> solution2(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		Step1.concat(commands, rotateSide(Tube.front, Tube.CW));
		Step1.concat(commands, rotateSide(Tube.top, Tube.CW));
		Step1.concat(commands, rotateSide(Tube.front, Tube.CCW));
		Step1.concat(commands, rotateSide(Tube.top, Tube.CW));
		Step1.concat(commands, rotateSide(Tube.front, Tube.CW));
		Step1.concat(commands, rotateSide(Tube.top, Tube.CW));
		Step1.concat(commands, rotateSide(Tube.top, Tube.CW));
		Step1.concat(commands, rotateSide(Tube.front, Tube.CCW));
		
		return commands;		
	}
	
	private boolean topEdgeEqual(int side1, int idx1, int side2, int idx2){
		boolean bEqual = false;
		
		Color cTop1 = mTube.getVisibleFaceColor(side1, idx1);
		Color cTop2 = mTube.getVisibleFaceColor(side2, idx2);
		Color cCenter1 = mTube.getVisibleFaceColor(side1, 4);
		Color cCenter2 = mTube.getVisibleFaceColor(side2, 4);
		if(cTop1.equals(cCenter2)&&cTop2.equals(cCenter1)){
			bEqual = true;
		}
		
		return bEqual;
	}
	private Stack<RotateAction> rotateTEBCW180(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		int[] sides = {Tube.top, Tube.equator, Tube.bottom};
		commands.push(new RotateAction(sides, Tube.CW));
		mTube.setRotate(sides, Tube.CW);
		commands.push(new RotateAction(sides, Tube.CW));
		mTube.setRotate(sides, Tube.CW);
		
		return commands;
	}
	public Stack<RotateAction> topEdgeInPlace(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		while(getCountInPlace()!=4){
			int idx = getIdenticalColorSideEdge();
			Log.i(TAG+".topEdgeInPlace", "idx = " + idx);
			if(idx==-1){
				if((topEdgeEqual(Tube.back, 7, Tube.front, 7)&&topEdgeEqual(Tube.left, 5, Tube.right, 5)) ||
				   (topEdgeEqual(Tube.back, 7, Tube.left, 5)&&topEdgeEqual(Tube.front, 7, Tube.right, 5))
				  ){
					//scenario 3||4.1 
					Step1.concat(commands, solution1());					
					Step1.concat(commands, rotateTEBCW180());					
					Step1.concat(commands, solution2());
				} else if(topEdgeEqual(Tube.back, 7, Tube.right, 5)&&topEdgeEqual(Tube.front, 7, Tube.left, 5)){
					//scenario 4.2, so convert to scenario 4.1
					Log.i(TAG+".topEdgeInPlace", "converting 4.2 to 4.1!");
					int[] sides = {Tube.top, Tube.equator, Tube.bottom};
					commands.push(new RotateAction(sides, Tube.CCW));
					mTube.setRotate(sides, Tube.CCW);
				} else 
					Log.e(TAG+".topEdgeInPlace", "exception type 1!!!");
			} else {
				//set the right side to identical color
				while(idx<3){
					Step1.concat(commands, rotateSide(Tube.top, Tube.CCW));
					idx++;
				}
				int pos = getCenterIdenticalToColor(mTube.getVisibleFaceColor(Tube.right, 5));
				while(pos!=-1&&pos<3){
					int[] sides = {Tube.equator, Tube.bottom};
					commands.push(new RotateAction(sides, Tube.CCW));
					mTube.setRotate(sides, Tube.CCW);
					pos++;
				}

				//check whether the other 3 edges are CCW or CW
				int orientation = checkTopEdgeOrientation();
				if(orientation==Tube.CCW){
					int[] sides = {Tube.top, Tube.equator, Tube.bottom};
					commands.push(new RotateAction(sides, Tube.CW));
					mTube.setRotate(sides, Tube.CW);
					
					Step1.concat(commands, solution1());				
					Step1.concat(commands, rotateTEBCW180());			
					Step1.concat(commands, solution2());
				} else if(orientation==Tube.CW){
					Step1.concat(commands, solution2());
					Step1.concat(commands, rotateTEBCW180());		
					Step1.concat(commands, solution1());
				} else 
					Log.e(TAG+".checkTopEdgeOrientation", "exception!");
				//
			}
		}
		
		return commands;
	}
	private int checkTopEdgeOrientation(){
		int orientation = -1;
		
		Color topBack = mTube.getVisibleFaceColor(Tube.back, 7);
		Color centerBack = mTube.getVisibleFaceColor(Tube.back, 4);
		Color topLeft = mTube.getVisibleFaceColor(Tube.left, 5);
		Color centerLeft = mTube.getVisibleFaceColor(Tube.left, 4);
		Color topFront = mTube.getVisibleFaceColor(Tube.front, 7);
		Color centerFront = mTube.getVisibleFaceColor(Tube.front, 4);
		
		if(topBack.equals(centerLeft)&&topLeft.equals(centerFront)&&topFront.equals(centerBack))
			orientation = Tube.CCW;
		else if(topBack.equals(centerFront)&&topLeft.equals(centerBack)&&topFront.equals(centerLeft))
			orientation = Tube.CW;
		else 
			orientation = -1;
		
		return orientation;
	}
	
	private int getCountInPlace(){
		int count = 0;
		Cube[] cubes = mTube.getCubes();
		
		for(int i=0;i<topEdge.length;i++){
			int whichCube = topEdge[i][0];
			int whichSide = topEdge[i][1];
			Color c1 = cubes[whichCube].getColor(whichSide);
			Color c2 = mTube.getVisibleFaceColor(whichSide, 4);
			Log.i(TAG+".getCountInPlace", "c1 = " + c1.toString());
			Log.i(TAG+".getCountInPlace", "c2 = " + c2.toString());
			if(c1.equals(c2))
				count++;
		}
		
		Log.i(TAG+".getCountInPlace", "count="+count);
		return count;
	}
	private int[][] topEdge = {
			{7,  Tube.back},
			{15, Tube.left},
			{25, Tube.front},
			{17, Tube.right},
	};
	private int[][] sideEdge = {
			{Tube.back,  6, 7, 8},
			{Tube.left,  2, 5, 8},
			{Tube.front, 6, 7, 8},
			{Tube.right, 2, 5, 8},
	};
	private int getCenterIdenticalToColor(Color c){
		int idx = -1;
		
		for(int i=0;i<sideEdge.length;i++){
			Color color = mTube.getVisibleFaceColor(sideEdge[i][0], 4);
			if(color.equals(c)){
				idx = i;
				break;
			}	
		}
		
		return idx;
	}
	private int getIdenticalColorSideEdge(){
		int idx = -1;
		
		for(int i=0;i<sideEdge.length;i++){
			int whichSide = sideEdge[i][0];
			Color c1 = mTube.getVisibleFaceColor(whichSide, sideEdge[i][1]);
			Color c2 = mTube.getVisibleFaceColor(whichSide, sideEdge[i][2]);
			Color c3 = mTube.getVisibleFaceColor(whichSide, sideEdge[i][3]);
			if(c1.equals(c2)&&c1.equals(c3)){
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
