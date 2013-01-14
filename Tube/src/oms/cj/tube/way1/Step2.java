package oms.cj.tube.way1;

import java.util.Stack;

import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;

import android.util.Log;

public class Step2 {
	private final static String TAG = "Step2";
	private Tube mTube;
	
	public Step2(Tube t){
		mTube = t;
	}
	
	public Tube getTube(){
		return mTube;
	}

	public Stack<RotateAction> moveBottom2Top(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int[] sides = {
				Tube.back,Tube.front,Tube.standing,
		};
		
		RotateAction r = new RotateAction(sides, Tube.CCW);
		commands.push(r);
		mTube.setRotate(sides, Tube.CCW);
		commands.push(r);
		mTube.setRotate(sides, Tube.CCW);
		
		return commands;
	}
	
	private Stack<RotateAction> moveTopWhiteEdge2Bottom(int pos){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		Cube[] cubes = mTube.getCubes();
		final int centerIdx = 4;
		int[] sides1 = {Tube.top};
		
		int side = mapTopSideIdx2Side(pos);
		int whichCube = mTube.getCube(Tube.top, pos);
		Color color1 = cubes[whichCube].getColor(side);
		Color color2 = mTube.getVisibleFaceColor(side, centerIdx);
		while(!color1.equals(color2)){
			RotateAction r = new RotateAction(sides1, Tube.CCW);
			commands.push(r);
			mTube.setRotate(sides1, Tube.CCW);
			
			Log.i(TAG+".moveTopWhiteEdge2Bottom(.)", "new pos = " + pos);
			pos = mTube.getTargetSideIdx(pos, r);
			side  = mapTopSideIdx2Side(pos);
			whichCube = mTube.getCube(Tube.top, pos);
			color1 = cubes[whichCube].getColor(side);
			color2 = mTube.getVisibleFaceColor(side, centerIdx);
		}
		
		int[] sides2 = { side };
		RotateAction r = new RotateAction(sides2, Tube.CCW);
		commands.push(r);
		mTube.setRotate(sides2, Tube.CCW);
		commands.push(r);
		mTube.setRotate(sides2, Tube.CCW);
		
		return commands;
	}
	public Stack<RotateAction> moveTopWhiteEdge2Bottom(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int pos=-1;
		
		while((pos=getTopWhiteEdgePos())!=-1){
			Log.i(TAG+".moveTopWhiteEdge2Bottom", "pos = " + pos);
			Stack<RotateAction> subCommands = moveTopWhiteEdge2Bottom(pos);
			Step1.concat(commands, subCommands);
		}
		
		return commands;
	}
	
	private int getTopWhiteEdgePos(){
		int pos = -1;
		int[] topSideIdx = {1, 3, 5, 7};
		
		for(int i=0;i<topSideIdx.length;i++){
			int idx = topSideIdx[i];
			Log.i(TAG+".getTopWhiteEdgePos", "i="+i+" "+mTube.getVisibleFaceColor(Tube.top, idx).toString());
			if(mTube.getVisibleFaceColor(Tube.top, idx).equals(Color.white)){
				pos = topSideIdx[i];
				break;
			}
		}
		
		return pos;
	}
	
	private int mapTopSideIdx2Side(int idx){
		int side=-1;
		
		if(idx==1)
			side = Tube.back;
		else if(idx==3)
			side = Tube.left;
		else if(idx==5)
			side = Tube.right;
		else if(idx==7)
			side = Tube.front;
		else 
			side = Tube.back;
		
		return side;
	}
}