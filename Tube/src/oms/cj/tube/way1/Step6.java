package oms.cj.tube.way1;

import java.util.Stack;

import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;

import android.util.Log;

public class Step6 {
	private final static String TAG = "Step6";
	private Tube mTube;
	
	public Step6(Tube t){
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
	private Stack<RotateAction> rotateSide(int side, int dir){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		int[] sides = {side};
		commands.push(new RotateAction(sides, dir));
		mTube.setRotate(sides, dir);
		
		return commands;
	}
	public Stack<RotateAction> moveCorner2Top(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		while(getCountInPlace()!=4){
			int topYellowCount = getTopCount(Color.yellow);
			switch(topYellowCount){
			case 3:
				int idx = getTypeAIdx(typeA1Idx);
				if(idx!=-1){
					while(idx<3){
						Step1.concat(commands, rotateSide(Tube.top, Tube.CCW));
						idx++;
					}
					Step1.concat(commands, solution1());
					continue;
				}
				idx = getTypeAIdx(typeA2Idx);
				if(idx!=-1){
					while(idx<3){
						Step1.concat(commands, rotateSide(Tube.top, Tube.CCW));
						idx++;
					}
					Step1.concat(commands, solution2());
					continue;
				}	
				Log.e(TAG+".moveCorner2Top", "exception! uncorrect cube position!");
				break;
			case 2:
				while(!getColorOf6th(Tube.back).equals(Color.yellow)){
					Step1.concat(commands, rotateSide(Tube.top, Tube.CCW));
				}
				Step1.concat(commands, solution1());
				break;
			case 4:
				while(!getColorOf6th(Tube.left).equals(Color.yellow)){
					Step1.concat(commands, rotateSide(Tube.top, Tube.CCW));
				}
				Step1.concat(commands, solution1());
				break;
			case 9:
				Log.i(TAG+".moveCorner2Top", "all top faces are now yellow!!!");
				break;
			default:
				Log.e(TAG+".moveCorner2Top", "exception! topYellowCount" + topYellowCount);
			}
		}
		
		return commands;
	}
	
	private Color getColorOf6th(int side){
		Cube[] cubes = mTube.getCubes();
		return cubes[6].getColor(side);
	}
	// get the count which is not equal to c on top side
	private int getTopCount(Color c){
		int count=0;
		
		for(int i=0;i<Tube.CubesEachSide;i++){
			Color color = mTube.getVisibleFaceColor(Tube.top, i);
			if(!color.equals(c))
				count++;
		}
		
		return count;
	}
	private int getCountInPlace(){
		int count=0;
		
		for(int i=0;i<topCornerIdx.length;i++){
			Color c = mTube.getVisibleFaceColor(Tube.top, topCornerIdx[i]);
			if(c.equals(Color.yellow))
				count++;
		}
		
		return count;
	};
	private int[] topCornerIdx ={
		2, 0, 6, 8,	
	};
	private int getTypeAIdx(int[][] typeAs){
		int idx = -1;
		Cube[] cubes = mTube.getCubes();
	
_L0:		
		for(int i=0;i<typeAs.length;i++){
			boolean bAllYellow = true;
			for(int j=0;j<typeAs[i].length/2;j++){
				int whichCube = typeAs[i][2*j];
				int whichSide = typeAs[i][2*j+1];
				Color c = cubes[whichCube].getColor(whichSide);
				bAllYellow = bAllYellow && c.equals(Color.yellow);
				if(!bAllYellow)
					continue _L0;
			}
			if(bAllYellow){
				idx = i;
				break;
			}
		}
		return idx;
	}
	private int[][] typeA1Idx = {
			{26, Tube.right, 8,  Tube.back,  6,  Tube.left},
			{8,  Tube.back,  6,  Tube.left,  24, Tube.front},
			{6,  Tube.left,  24, Tube.front, 26, Tube.right},
			{24, Tube.front, 26, Tube.right, 8,  Tube.back},
	};
	private int[][] typeA2Idx = {
			{26, Tube.front, 8,  Tube.right, 6,  Tube.back},
			{8,  Tube.right, 6,  Tube.back,  24, Tube.left},
			{6,  Tube.back,  24, Tube.left,  26, Tube.front},
			{24, Tube.left,  26, Tube.front, 8,  Tube.right},
	};
}
