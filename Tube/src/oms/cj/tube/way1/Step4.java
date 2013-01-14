package oms.cj.tube.way1;

import java.util.Stack;

import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;

import android.util.Log;

public class Step4 {
	private final static String TAG = "Step4";
	private Tube mTube;
	
	public Step4(Tube t){
		mTube = t;
	}
	
	public Tube getTube(){
		return mTube;
	}
	
	private Stack<RotateAction> moveBottom2LayersToFront(Color c){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int[] sides = {Tube.equator, Tube.bottom};
		RotateAction r = new RotateAction(sides, Tube.CCW);
		
		Log.i(TAG+".moveBottom2LayersToFront", "c="+c.toString());
		for(int i=0;i<4;i++){
			Color cCenter = mTube.getVisibleFaceColor(Tube.front, 4);
			Log.i(TAG+".moveBottom2LayersToFront", "cCenter="+cCenter.toString());
			if(!cCenter.equals(c)){
				commands.push(r);
				mTube.setRotate(sides, Tube.CCW);
			} else 
				break;
		}
		
		return commands;
	}
	private Stack<RotateAction> moveTopEdge2Equator(int idx){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		Cube[] cubes = mTube.getCubes();
		
		//move the candidate's top layers to front
		while(idx<3){
			int[] sides = {Tube.top};
			commands.push(new RotateAction(sides, Tube.CCW));
			mTube.setRotate(sides, Tube.CCW);
			idx++;
		}
		int whichCube = 25;
		Color cFront = cubes[whichCube].getColor(Tube.front);
		Log.i(TAG+".moveTopEdge2Equator(.)", "cFront="+cFront.toString());
		//move the bottom 2 layers
		Step1.concat(commands, moveBottom2LayersToFront(cFront));
		
		//check whether the front match the candidate
		Color cTop = cubes[whichCube].getColor(Tube.top);
		Color cFrontCenter = mTube.getVisibleFaceColor(Tube.front, 4);
		Color cRightCenter = mTube.getVisibleFaceColor(Tube.right, 4);
		boolean b1 = cTop.equals(cFrontCenter)&&cFront.equals(cRightCenter);
		boolean b2 = cTop.equals(cRightCenter)&&cFront.equals(cFrontCenter);
		Log.i(TAG+".moveTopEdge2Equator(.)", "cFront = " + cFront.toString());
		Log.i(TAG+".moveTopEdge2Equator(.)", "cTop = " + cTop.toString());
		Log.i(TAG+".moveTopEdge2Equator(.)", "cFrontCenter = " + cFrontCenter.toString());
		Log.i(TAG+".moveTopEdge2Equator(.)", "cRightCenter = " + cRightCenter.toString());
		if(b1||b2){
			//solutionForTypeA
			Step1.concat(commands, solutionForTypeA());
		} else {
			Log.i(TAG+".moveTopEdge2Equator(.)", "move front side to right side starting ...!");
			// if the front does not match the candidate, move it to the right
			int[] sides = {Tube.top, Tube.equator, Tube.bottom};
			RotateAction r = new RotateAction(sides, Tube.CCW);
			commands.push(r);
			mTube.setRotate(sides, Tube.CCW);
			
			//solutionForTypeB
			Step1.concat(commands, solutionForTypeB());
			Log.i(TAG+".moveTopEdge2Equator(.)", "move front side to right side completed ...!");
		}
		
		return commands;
	}
	private Stack<RotateAction> moveTopEdge2Equator(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int idx;
		
		while((idx=getTopCandidate(Color.yellow))!=-1){
			Log.i(TAG+".moveTopEdge2Equator", "idx="+idx);
			Step1.concat(commands, moveTopEdge2Equator(idx));
		}

		return commands;
	}
	private Stack<RotateAction> solutionForTypeA(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		//1
		int[] sides1 = { Tube.top };
		commands.push(new RotateAction(sides1, Tube.CW));
		mTube.setRotate(sides1, Tube.CW);
		//2
		int[] sides2 = { Tube.right };
		commands.push(new RotateAction(sides2, Tube.CW));
		mTube.setRotate(sides2, Tube.CW);
		//3
		int[] sides3 = { Tube.top };
		commands.push(new RotateAction(sides3, Tube.CCW));
		mTube.setRotate(sides3, Tube.CCW);
		//4
		int[] sides4 = { Tube.right };
		commands.push(new RotateAction(sides4, Tube.CCW));
		mTube.setRotate(sides4, Tube.CCW);
		//5
		int[] sides5 = { Tube.top };
		commands.push(new RotateAction(sides5, Tube.CCW));
		mTube.setRotate(sides5, Tube.CCW);	
		//6
		int[] sides6 = { Tube.front };
		commands.push(new RotateAction(sides6, Tube.CCW));
		mTube.setRotate(sides6, Tube.CCW);
		//7
		int[] sides7 = { Tube.top };
		commands.push(new RotateAction(sides7, Tube.CW));
		mTube.setRotate(sides7, Tube.CW);
		//8
		int[] sides8 = { Tube.front };
		commands.push(new RotateAction(sides8, Tube.CW));
		mTube.setRotate(sides8, Tube.CW);
		
		return commands;
	}
	private Stack<RotateAction> solutionForTypeB(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		//1
		int[] sides5 = { Tube.top };
		commands.push(new RotateAction(sides5, Tube.CCW));
		mTube.setRotate(sides5, Tube.CCW);	
		//2
		int[] sides6 = { Tube.front };
		commands.push(new RotateAction(sides6, Tube.CCW));
		mTube.setRotate(sides6, Tube.CCW);
		//3
		int[] sides7 = { Tube.top };
		commands.push(new RotateAction(sides7, Tube.CW));
		mTube.setRotate(sides7, Tube.CW);
		//4
		int[] sides8 = { Tube.front };
		commands.push(new RotateAction(sides8, Tube.CW));
		mTube.setRotate(sides8, Tube.CW);
		//5
		int[] sides1 = { Tube.top };
		commands.push(new RotateAction(sides1, Tube.CW));
		mTube.setRotate(sides1, Tube.CW);
		//6
		int[] sides2 = { Tube.right };
		commands.push(new RotateAction(sides2, Tube.CW));
		mTube.setRotate(sides2, Tube.CW);
		//7
		int[] sides3 = { Tube.top };
		commands.push(new RotateAction(sides3, Tube.CCW));
		mTube.setRotate(sides3, Tube.CCW);
		//8
		int[] sides4 = { Tube.right };
		commands.push(new RotateAction(sides4, Tube.CCW));
		mTube.setRotate(sides4, Tube.CCW);

		return commands;
	}
	
	private int getSideCandidate(Color c){
		int idx = -1;
		Cube[] cubes = mTube.getCubes();
		
		for(int i=0;i<equatorEdgeCube.length;i++){
			int whichCube = equatorEdgeCube[i][0];
			Color c1 = cubes[whichCube].getColor(equatorEdgeCube[i][1]);
			Color c2 = cubes[whichCube].getColor(equatorEdgeCube[i][2]);
			if(c1.equals(c)||c2.equals(c))
				continue;
			Color c3 = mTube.getVisibleFaceColor(equatorEdgeCube[i][1], 4);
			Color c4 = mTube.getVisibleFaceColor(equatorEdgeCube[i][2], 4);
			if(c1.equals(c3)&&c2.equals(c4))
				continue;
			idx = i;
			break;
		}
		return idx;
	}
	private Stack<RotateAction> moveSideEdge2Equator(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		int idx=getSideCandidate(Color.yellow);
		Log.i(TAG+".moveSideEdge2Equator", "idx = " + idx);
		if(idx!=-1){
			Log.i(TAG+".moveSideEdge2Equator", "There is a cube in equator edge!");
			//move the candidate's top layers to front
			while(idx<3){
				int[] sides = {Tube.equator, Tube.bottom};
				commands.push(new RotateAction(sides, Tube.CCW));
				mTube.setRotate(sides, Tube.CCW);
				idx++;
			}
			
			//move the 23th cube to place where idx=1
			Step1.concat(commands, solutionForTypeA());
			
			//move the cube where idx=1 to correct place
			Step1.concat(commands, moveTopEdge2Equator(1));
		}
		
		return commands;
	}
	public Stack<RotateAction> sideEdgeInPlace(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		while(getCountInPlace()!=4){	
			Stack<RotateAction> c1 = moveTopEdge2Equator();
			Step1.concat(commands, c1);
			
			c1 = moveSideEdge2Equator();
			Step1.concat(commands, c1);
		}
		
		return commands;
	}
	
	private int getTopCandidate(Color excludedColor){
		int pos = -1;
		Cube[] cubes = mTube.getCubes();
		
		for(int i=0;i<topEdgeCube.length;i++){
			int whichCube = topEdgeCube[i][0];
			int side = topEdgeCube[i][1];
			Color c1 = cubes[whichCube].getColor(Tube.top);
			Color c2 = cubes[whichCube].getColor(side);
			if(!c1.equals(excludedColor)&&!c2.equals(excludedColor)){
				pos = i;
				break;
			}
		}
		
		return pos;
	}
	
	private int getCountInPlace(){
		int count = 0;
		int[] equatorEdgeCubes = {5, 3, 21, 23};
		int[][] equatorEdgeAdjacentInfo = {
				{5,  Tube.CW,  14, Tube.right},
				{5,  Tube.CCW, 4,  Tube.back},
				{3,  Tube.CW,  4,  Tube.back},
				{3,  Tube.CCW, 12, Tube.left},
				{21, Tube.CW,  12, Tube.left},
				{21, Tube.CCW, 22, Tube.front},
				{23, Tube.CW,  22, Tube.front},
				{23, Tube.CCW, 14, Tube.right},
		};
		Cube[] cubes = mTube.getCubes();
		
		//check whether the 4 EquatorEdgeCubes are already in place
		for(int i=0;i<equatorEdgeCubes.length;i++){
			int idx = equatorEdgeCubes[i];
			if(idx==5||idx==3||idx==21||idx==23){
				boolean bInPlace=true;
				int c=0;
				for(int j=0;j<equatorEdgeAdjacentInfo.length;j++){
					if(idx!=equatorEdgeAdjacentInfo[j][0])
						continue;
					else {
						c++;
						Color c0 = cubes[idx].getColor(equatorEdgeAdjacentInfo[j][3]);
						int adjacentCube = equatorEdgeAdjacentInfo[j][2];
						Color c1 = cubes[adjacentCube].getColor(equatorEdgeAdjacentInfo[j][3]);
						bInPlace = bInPlace && c0.equals(c1);
						if(c==2)
							break;
					}
				}
				if(bInPlace)
					count++;
				else 
					break;
			} else {
				Log.e(TAG+".getCountInPlace", "exception!");
			}
		}
		
		return count;
	}
	
	int[][] topEdgeCube = {
			{17, Tube.right},
			{7,  Tube.back},
			{15, Tube.left},
			{25, Tube.front},
	};
	
	int[][] equatorEdgeCube = {
			{5,  Tube.right, Tube.back},
			{3,  Tube.back,  Tube.left},
			{21, Tube.left,  Tube.front},
			{23, Tube.front, Tube.right},
	};
}
