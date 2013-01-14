package oms.cj.tube.way1;

import java.util.Stack;

import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import android.util.Log;

public class Step1 {
	private final static String TAG = "Step1";
	private Tube mTube;
	
	public Step1(Tube t){
		mTube = t;
	}
	
	public Tube getTube(){
		return mTube;
	}
	
	public Stack<RotateAction> moveTo16(Color c){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int[] colorPos = mTube.searchCenterColor(c);
//		Log.i(TAG+".moveTo16", "color Pos="+colorPos);
		if(colorPos[Tube.CENTERBYCUBE]==16)
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
				if(centerMovesOfCCW[i][j]==colorPos[Tube.CENTERBYCUBE])
					break _L0;
			}
		}
//		Log.i(TAG+".moveYellowTo16", "i="+i);
		
		int[] sides = { idx2SideOf[i] };
		for(;centerMovesOfCCW[i][j]!=16;j++){
			commands.push(new RotateAction(sides, Tube.CCW));
			mTube.setRotate(sides, Tube.CCW);
		}
		return commands;
	}
	public Stack<RotateAction> moveEdge2Top(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		while(getCountInPlace(Color.white)!=4){
			Stack<RotateAction> c1 = moveEquatorWhiteEdge2Top();
			concat(commands, c1);
			
			c1 = moveBottomWhiteEdge2Top();
			concat(commands, c1);
			
			c1 = moveSideMiddleWhiteEdge2Top();
			concat(commands, c1);
		}
		return commands;
	}
	
	private int getCountInPlace(Color c){
		int count = 0;
		int[] topEdgeCubes = {7, 15, 17, 25};
		Cube[] cubes = mTube.getCubes();
		
		//check whether the 4 TopWhiteEdges are already in place
		for(int i=0;i<topEdgeCubes.length;i++){
			int idx = topEdgeCubes[i];
			Color color = cubes[idx].getColor(Tube.top);
			Log.i(TAG+".getCountInPlace", "i="+i+" "+color.toString());
			if(color.equals(c))
				count++;	
		}
		
		return count;
	}
	
	private Stack<RotateAction> moveBottomWhiteEdge2Top(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		int[] pos = null;
		while((pos = getBottomWhiteEdgePos())!=null){
			Log.i(TAG+".moveBottomWhiteEdge2Top", "pos[0]="+pos[0]+";pos[1]="+pos[1]);
			Stack<RotateAction> subCommands = new Stack<RotateAction>();
			subCommands = moveBottomWhiteEdge2Top(pos);
			concat(commands, subCommands);			
		}
		
		return commands;
	}
	private Stack<RotateAction> moveBottomWhiteEdge2Top(int[] pos){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int[] action=mapBottomPosToAction(pos);
		
		Log.i(TAG+".moveBottomWhiteEdge2Top(.)", "side="+action[0]);
		int whichCube = mTube.getTargetCube(pos, action, 2);
		concat(commands, loopTop2GetEmptyCube(whichCube));

		int[] sides = { action[0] };
		commands.push(new RotateAction(sides, action[1]));
		commands.push(new RotateAction(sides, action[1]));
		mTube.setRotate(sides, action[1]);
		mTube.setRotate(sides, action[1]);
		
		return commands;
	}
	
	private Stack<RotateAction> moveSideMiddleWhiteEdge2Top(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		int[] pos = null;
		while((pos = getSideMiddleWhiteEdgePos())!=null){
			Log.i(TAG+".moveSideMiddleWhiteEdge2Top()", "pos[0]="+pos[0]+";pos[1]="+pos[1]);
			Stack<RotateAction> subCommands = new Stack<RotateAction>();
			subCommands = moveSideMiddleWhiteEdge2Top(pos);
			concat(commands, subCommands);			
		}
		
		return commands;
	}

	private Stack<RotateAction> moveSideMiddleWhiteEdge2Top(int[] pos){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int[] action=mapSideMiddlePosToAction(pos);
		
		//move the corresponding top cube to be empty
		int whichCube=mTube.getSideTopCube(pos[0]);
		Step1.concat(commands, loopTop2GetEmptyCube(whichCube));
		
		Log.i(TAG+".moveSideMiddleWhiteEdge2Top(.)", "side to be moved ="+action[0]);
		int[] sides = { action[0] };
		commands.push(new RotateAction(sides, action[1]));
		mTube.setRotate(sides, action[1]);
		
		concat(commands, moveEquatorWhiteEdge2Top());
		
		return commands;
	}
	
	private Stack<RotateAction> moveEquatorWhiteEdge2Top(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int[] pos = null;
		
		while((pos = getEquatorWhiteEdgePos())!=null){
			Log.i(TAG+".moveEquatorWhiteEdge2Top()", "pos[0]="+pos[0]+";pos[1]="+pos[1]);
			Stack<RotateAction> subCommands = new Stack<RotateAction>();
			subCommands = moveEquatorWhiteEdge2Top(pos);
			concat(commands, subCommands);
		}
		return commands;
	}
	private Stack<RotateAction> moveEquatorWhiteEdge2Top(int[] pos){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int[] action=mapEquatorPosToAction(pos);
		
		Log.i(TAG+".moveEquatorWhiteEdge2Top(.)", "side="+action[0]);
		int whichCube = mTube.getTargetCube(pos, action);
		concat(commands, loopTop2GetEmptyCube(whichCube));

		int[] sides = { action[0] };
		commands.push(new RotateAction(sides, action[1]));
		mTube.setRotate(sides, action[1]);
		
		return commands;
	}
	
	private int[] getSideMiddleWhiteEdgePos(){
		int[] pos = null;
		int[] sideMiddleSides = {Tube.left, Tube.right, Tube.back, Tube.front};
		int[][] sideMiddleEdgeFaceIdx = {
				{3, 5},
				{3, 5},
				{1, 7},
				{1, 7},
		};
	
_L0:		
		for(int i=0;i<sideMiddleEdgeFaceIdx.length;i++){
			int side = sideMiddleSides[i];
			for(int j=0;j<sideMiddleEdgeFaceIdx[i].length;j++){
				if(mTube.getVisibleFaceColor(side, sideMiddleEdgeFaceIdx[i][j]).equals(Color.white)){
					pos = new int[2];
					pos[0] = side; pos[1] = sideMiddleEdgeFaceIdx[i][j];
					break _L0;
				}
			}
		}	
		
		return pos;
	}
	private int[] getBottomWhiteEdgePos(){
		int[] pos = null;
		int[] bottomEdgeFaceIdx = {
				1, 3, 5, 7,
		};
		
		for(int i=0;i<bottomEdgeFaceIdx.length;i++){
			if(mTube.getVisibleFaceColor(Tube.bottom, bottomEdgeFaceIdx[i]).equals(Color.white)){
				pos = new int[2];
				pos[0] = Tube.bottom; pos[1] = bottomEdgeFaceIdx[i];
				break;
			}
		}
		return pos;
	}
	private int[] getEquatorWhiteEdgePos(){
		int[] pos = null;
		int[] equatorSides = {Tube.left, Tube.right, Tube.back, Tube.front};
		int[][] equatorEdgeFaceIdx = {
				{1, 7},   //back, front   CW
				{1, 7},   //back, front   CCW
				{3, 5},   //left, right   CCW
				{3, 5},   //left, right   CW
		};
		
_L0:		
		for(int i=0;i<equatorEdgeFaceIdx.length;i++){
			int side = equatorSides[i];
			for(int j=0;j<equatorEdgeFaceIdx[i].length;j++){
				if(mTube.getVisibleFaceColor(side, equatorEdgeFaceIdx[i][j]).equals(Color.white)){
					pos = new int[2];
					pos[0] = side; pos[1] = equatorEdgeFaceIdx[i][j];
					break _L0;
				}
			}
		}
		
		return pos;
	}
	
	public static void concat(Stack<RotateAction> s1, Stack<RotateAction>s2){
		for(int i=0;i<s2.size();i++){
			s1.push(s2.get(i));
		}
	}
	
	private Stack<RotateAction> loopTop2GetEmptyCube(int whichCube){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		Cube[] cubes = mTube.getCubes();
		
		int loops=0;
		while(loops<4){
			Color topColor = cubes[whichCube].getColor(Tube.top);
			Log.i(TAG+".loopTop2GetEmptyCube", "top color="+topColor.toString());
			if(topColor.equals(Color.white)){
				int[] sides = { Tube.top };
				commands.push(new RotateAction(sides, Tube.CCW));
				mTube.setRotate(sides, Tube.CCW);
			} else 
				break;
			loops++;
		}
		if(loops==4)
			Log.e(TAG+".loopTop2GetEmptyCube", "exception!");

		return commands;
	}

	//mapping table:
	//   side   cubesideidx   |   side      dir
	//   left      1          |   back      CW
	//   left      7          |   front     CW
	//   right     1          |   back      CCW
	//   right     7          |   front     CCW
	//   back      3          |   left      CCW
	//   back      5          |   right     CCW
	//   front     3          |   left      CW
	//   front     5          |   right     CW
	private int[] mapEquatorPosToAction(int[] pos){
		int[] action = new int[2];
		
		if(pos[0]==Tube.left||pos[0]==Tube.front)
			action[1] = Tube.CW;
		else if(pos[0]==Tube.right||pos[1]==Tube.back)
			action[1] = Tube.CCW;
		
		if(pos[1]==1)
			action[0]=Tube.back;
		else if(pos[1]==7)
			action[0]=Tube.front;
		else if(pos[1]==3)
			action[0]=Tube.left;
		else if(pos[1]==5)
			action[0]=Tube.right;
		
		return action;
	}
	
	//mapping table:
	//   side   cubesideidx   |   side      dir
	//   bottom    1          |   back      CCW
	//   bottom    3          |   left      CCW
	//   bottom    5          |   right     CCW
	//   bottom    7          |   front     CCW
	private int[] mapBottomPosToAction(int[] pos){
		int[] action = new int[2];
		
		if(pos[1]==1)
			action[0]=Tube.back;
		else if(pos[1]==7)
			action[0]=Tube.front;
		else if(pos[1]==3)
			action[0]=Tube.left;
		else if(pos[1]==5)
			action[0]=Tube.right;
		
		action[1]=Tube.CCW;
		
		return action;
	}
	
	//mapping table:
	//   side   cubesideidx   |   side      dir
	//   left      3          |   left      CCW
	//   left      5          |   left      CCW
	//   right     3          |   right     CCW
	//   right     5          |   right     CCW
	//   back      1          |   back      CCW
	//   back      7          |   back      CCW
	//   front     1          |   front     CCW
	//   front     7          |   front     CCW
	private int[] mapSideMiddlePosToAction(int[] pos){
		int[] action = new int[2];
		
		action[0] = pos[0];
		action[1] = Tube.CCW;
		
		return action;
	}
}
