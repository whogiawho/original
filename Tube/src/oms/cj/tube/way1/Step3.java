package oms.cj.tube.way1;

import java.util.Stack;

import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;

import android.util.Log;

public class Step3 {
	private final static String TAG = "Step3";
	private Tube mTube;
	
	public Step3(Tube t){
		mTube = t;
	}
	
	public Tube getTube(){
		return mTube;
	}
	
	private Stack<RotateAction> moveTop2Layers(int whichCube, int f1, int f2){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		Cube[] cubes = mTube.getCubes();
		int[] sides = {Tube.top, Tube.equator};
		
		Color c1 = cubes[whichCube].getColor(f1);
		Color c2 = cubes[whichCube].getColor(f2);
		for(int i=0;i<4;i++){
			Color frontCenter = mTube.getVisibleFaceColor(Tube.front, 4);
			Color rightCenter = mTube.getVisibleFaceColor(Tube.right, 4);
			boolean b1 = c1.equals(frontCenter)&&c2.equals(rightCenter);
			boolean b2 = c1.equals(rightCenter)&&c2.equals(frontCenter);
			if(b1||b2){
				break;
			} else {
				commands.push(new RotateAction(sides, Tube.CW));
				mTube.setRotate(sides, Tube.CW);
			}
		}
		
		return commands;
	}
	private Stack<RotateAction> solutionForTypeA(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		//1
		int[] sides1 = { Tube.front };
		commands.push(new RotateAction(sides1, Tube.CW));
		mTube.setRotate(sides1, Tube.CW);
		//2
		int[] sides2 = { Tube.bottom };
		commands.push(new RotateAction(sides2, Tube.CCW));
		mTube.setRotate(sides2, Tube.CCW);
		//3
		int[] sides3 = { Tube.front };
		commands.push(new RotateAction(sides3, Tube.CCW));
		mTube.setRotate(sides3, Tube.CCW);
		
		return commands;
	}
	private Stack<RotateAction> solutionForTypeB(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		//1
		int[] sides1 = { Tube.right };
		commands.push(new RotateAction(sides1, Tube.CCW));
		mTube.setRotate(sides1, Tube.CCW);
		//2
		int[] sides2 = { Tube.bottom };
		commands.push(new RotateAction(sides2, Tube.CW));
		mTube.setRotate(sides2, Tube.CW);
		//3
		int[] sides3 = { Tube.right };
		commands.push(new RotateAction(sides3, Tube.CW));
		mTube.setRotate(sides3, Tube.CW);
		
		return commands;
	}
	private Stack<RotateAction> moveTypeA2Place(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int idx;
		
		while((idx=getPositionOfWhite(candidateTypeAPos))!=-1){
			while(idx<3){
				int[] sides = { Tube.bottom };
				commands.push(new RotateAction(sides, Tube.CW));
				mTube.setRotate(sides, Tube.CW);
				idx++;
			}
			
			// here idx must be 3
			int whichCube = mTube.getCube(candidateTypeAPos[3][0], candidateTypeAPos[3][1]);
			Step1.concat(commands, moveTop2Layers(whichCube, Tube.bottom, Tube.right));
			
			Step1.concat(commands, solutionForTypeA());
		}

		return commands;
	}
	private Stack<RotateAction> moveTypeB2Place(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int idx;
		
		while((idx=getPositionOfWhite(candidateTypeBPos))!=-1){
			while(idx<3){
				int[] sides = { Tube.bottom };
				commands.push(new RotateAction(sides, Tube.CW));
				mTube.setRotate(sides, Tube.CW);
				idx++;
			}
			
			// here idx must be 3
			int whichCube = mTube.getCube(candidateTypeBPos[3][0], candidateTypeBPos[3][1]);
			Step1.concat(commands, moveTop2Layers(whichCube, Tube.front, Tube.bottom));
			
			Step1.concat(commands, solutionForTypeB());
		}
		
		return commands;
	}
	private Stack<RotateAction> moveTypeC2Place(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int idx;
		
		while((idx=getPositionOfWhite(candidateTypeCPos))!=-1){
			while(idx<3){
				int[] sides = { Tube.top, Tube.equator };
				commands.push(new RotateAction(sides, Tube.CW));
				mTube.setRotate(sides, Tube.CW);
				idx++;
			}
			//convert TypeC to TypeA
			Step1.concat(commands, solutionForTypeA());
			int[] sides = { Tube.bottom };
			commands.push(new RotateAction(sides, Tube.CW));
			mTube.setRotate(sides, Tube.CW);
			
			// here idx must be 3
			int whichCube = mTube.getCube(candidateTypeAPos[3][0], candidateTypeAPos[3][1]);
			Step1.concat(commands, moveTop2Layers(whichCube, Tube.bottom, Tube.right));
			
			Step1.concat(commands, solutionForTypeA());
		}
		
		return commands;
	}
	private Stack<RotateAction> moveTypeD2Place(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int idx;
		
		while((idx=getPositionOfWhite(candidateTypeDPos))!=-1){
			while(idx<3){
				int[] sides = { Tube.top, Tube.equator };
				commands.push(new RotateAction(sides, Tube.CW));
				mTube.setRotate(sides, Tube.CW);
				idx++;
			}
			//convert TypeD to TypeB
			Step1.concat(commands, solutionForTypeB());
			int[] sides = { Tube.bottom };
			commands.push(new RotateAction(sides, Tube.CCW));
			mTube.setRotate(sides, Tube.CCW);
			
			// here idx must be 3
			int whichCube = mTube.getCube(candidateTypeBPos[3][0], candidateTypeBPos[3][1]);
			Step1.concat(commands, moveTop2Layers(whichCube, Tube.front, Tube.bottom));
			
			Step1.concat(commands, solutionForTypeB());
		}
		
		return commands;
	}
	private Stack<RotateAction> moveTypeE2Place(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		Cube[] cubes = mTube.getCubes();
		
		for(int i=0;i<4;i++){
			int whichCube = mTube.getCube(candidateTypeEPos[3][0], candidateTypeEPos[3][1]);
			Color c = cubes[whichCube].getColor(Tube.top);
			if(c.equals(Color.white)){
				Color c1 = cubes[whichCube].getColor(Tube.front);
				Color c2 = cubes[whichCube].getColor(Tube.right);
				Color frontCenter = mTube.getVisibleFaceColor(Tube.front, 4);
				Color rightCenter = mTube.getVisibleFaceColor(Tube.right, 4);
				boolean b = c1.equals(frontCenter)&&c2.equals(rightCenter);
				if(b){
					int[] sides = {Tube.top, Tube.equator};
					commands.push(new RotateAction(sides, Tube.CW));
					mTube.setRotate(sides, Tube.CW);
				} else {
					Step1.concat(commands, solutionForTypeA());	
					int[] sides = { Tube.bottom };
					commands.push(new RotateAction(sides, Tube.CW));
					mTube.setRotate(sides, Tube.CW);
					
					int cube = mTube.getCube(candidateTypeBPos[3][0], candidateTypeBPos[3][1]);
					Step1.concat(commands, moveTop2Layers(cube, Tube.front, Tube.bottom));	
					
					Step1.concat(commands, solutionForTypeB());
				}				
			} else {
				int[] sides = {Tube.top, Tube.equator};
				commands.push(new RotateAction(sides, Tube.CW));
				mTube.setRotate(sides, Tube.CW);				
			}
		}
		return commands;
	}
	private Stack<RotateAction> moveTypeF2Place(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int idx;
		
		while((idx=getPositionOfWhite(candidateTypeFPos))!=-1){
			while(idx<3){
				int[] sides = { Tube.bottom };
				commands.push(new RotateAction(sides, Tube.CW));
				mTube.setRotate(sides, Tube.CW);
				idx++;
			}
			
			// here idx must be 3
			int whichCube = mTube.getCube(candidateTypeFPos[3][0], candidateTypeFPos[3][1]);
			Step1.concat(commands, moveTop2Layers(whichCube, Tube.front, Tube.right));
			
			Step1.concat(commands, solutionForTypeA());
			Step1.concat(commands, solutionForTypeA());
			int[] sides = { Tube.bottom };
			commands.push(new RotateAction(sides, Tube.CW));
			mTube.setRotate(sides, Tube.CW);
			Step1.concat(commands, solutionForTypeA());
		}
		
		return commands;
	}
	public Stack<RotateAction> moveWhiteCorner2Top(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		while(getCountInPlace()!=4){
			Stack<RotateAction> c1 = moveTypeA2Place();
			Step1.concat(commands, c1);
			
			c1 = moveTypeB2Place();
			Step1.concat(commands, c1);
			
			c1 = moveTypeC2Place();
			Step1.concat(commands, c1);
			
			c1 = moveTypeD2Place();
			Step1.concat(commands, c1);
			
			c1 = moveTypeF2Place();
			Step1.concat(commands, c1);
			
			c1 = moveTypeE2Place();
			Step1.concat(commands, c1);
		}
		
		int[] sides = {Tube.front, Tube.standing, Tube.back};
		commands.push(new RotateAction(sides, Tube.CW));
		mTube.setRotate(sides, Tube.CW);
		commands.push(new RotateAction(sides, Tube.CW));
		mTube.setRotate(sides, Tube.CW);
		
		return commands;
	}
	
	private boolean cornerInPlace(int idx){
		boolean bInPlace = true;
		Cube[] cubes = mTube.getCubes();
		int[][] topCornerAdjacentInfo = {
				{6,  Tube.CW,  7,  Tube.back},
				{6,  Tube.CCW, 15, Tube.left},
				{8,  Tube.CW,  17, Tube.right},
				{8,  Tube.CCW, 7,  Tube.back},
				{24, Tube.CW,  15, Tube.left},
				{24, Tube.CCW, 25, Tube.front},
				{26, Tube.CW,  25, Tube.front},
				{26, Tube.CCW, 17, Tube.right},
		};
		if(idx==6||idx==8||idx==24||idx==26){
			int count = 0;
			for(int i=0;i<topCornerAdjacentInfo.length;i++){
				if(idx!=topCornerAdjacentInfo[i][0])
					continue;
				else {
					count++;
					Color c0 = cubes[idx].getColor(topCornerAdjacentInfo[i][3]);
					int adjacentCube = topCornerAdjacentInfo[i][2];
					Color c1 = cubes[adjacentCube].getColor(topCornerAdjacentInfo[i][3]);
					bInPlace = bInPlace && c0.equals(c1);
					if(count==2)
						break;
				}
			}
		} else {
			bInPlace = false;
			Log.e(TAG+".cornerInPlace", "exception!");
		}
		
		return bInPlace;
	}
	private int getCountInPlace(){
		int count = 0;
		int[] topCornerCubes = {6, 8, 24, 26};
		Cube[] cubes = mTube.getCubes();
		
		//check whether the 4 TopWhiteCorners are already in place
		for(int i=0;i<topCornerCubes.length;i++){
			int idx = topCornerCubes[i];
			Color color = cubes[idx].getColor(Tube.top);
			Log.i(TAG+".getCountInPlace", "i="+i+" "+color.toString());
			if(color.equals(Color.white)&&cornerInPlace(idx))
				count++;	
		}
		
		return count;
	}
	
	int[][] candidateTypeAPos = {
			{Tube.left, 6},	
			{Tube.back, 0},
			{Tube.right, 0},
			{Tube.front, 2},	//standard position
	};
	int[][] candidateTypeBPos = {
			{Tube.front, 0},
			{Tube.left, 0},
			{Tube.back, 2},
			{Tube.right, 6},
	};
	int[][] candidateTypeCPos = {
			{Tube.left, 8},	
			{Tube.back, 6},
			{Tube.right, 2},
			{Tube.front, 8},	//standard position
	};
	int[][] candidateTypeDPos = {
			{Tube.front, 6},
			{Tube.left, 2},
			{Tube.back, 8},
			{Tube.right, 8},
	};	
	int[][] candidateTypeEPos = {
			{Tube.top, 6},
			{Tube.top, 0},
			{Tube.top, 2},
			{Tube.top, 8},
	};	
	int[][] candidateTypeFPos = {
			{Tube.bottom, 6},
			{Tube.bottom, 0},
			{Tube.bottom, 2},
			{Tube.bottom, 8},
	};	
	private int getPositionOfWhite(int[][] candidatePos){
		int pos = -1;
		
		for(int i=0;i<candidatePos.length;i++){
			Color c = mTube.getVisibleFaceColor(candidatePos[i][0], candidatePos[i][1]);
			if(c.equals(Color.white)){
				pos = i;
				break;
			}
		}
		return pos;
	}
}
