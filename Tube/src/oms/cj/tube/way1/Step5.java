package oms.cj.tube.way1;

import java.util.Stack;

import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;

import android.util.Log;

public class Step5 {
	private final static String TAG = "Step5";
	private Tube mTube;
	
	public Step5(Tube t){
		mTube = t;
	}
	
	public Tube getTube(){
		return mTube;
	}
	
	private Stack<RotateAction> commonSolution(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		int[] sides1 = {Tube.front};
		commands.push(new RotateAction(sides1, Tube.CW));
		mTube.setRotate(sides1, Tube.CW);
		
		int[] sides2 = {Tube.right};
		commands.push(new RotateAction(sides2, Tube.CW));
		mTube.setRotate(sides2, Tube.CW);
		
		int[] sides3 = {Tube.top};
		commands.push(new RotateAction(sides3, Tube.CW));
		mTube.setRotate(sides3, Tube.CW);

		int[] sides4 = {Tube.right};
		commands.push(new RotateAction(sides4, Tube.CCW));
		mTube.setRotate(sides4, Tube.CCW);
		
		int[] sides5 = {Tube.top};
		commands.push(new RotateAction(sides5, Tube.CCW));
		mTube.setRotate(sides5, Tube.CCW);
		
		int[] sides6 = {Tube.front};
		commands.push(new RotateAction(sides6, Tube.CCW));
		mTube.setRotate(sides6, Tube.CCW);
		
		return commands;
	}
	private Stack<RotateAction> moveEdgeTypeA2Top(int idx){
		Log.i(TAG+".moveEdgeTypeA2Top", "starting ... ");
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		if(idx>=typeATopIdx.length){
			Log.e(TAG+".moveEdgeTypeA2Top", "exeption! idx =" + idx);
			return commands;
		}
		
		if(idx==1){	//TypeA1, so convert to TypeA0
			int[] sides = {Tube.top};
			commands.push(new RotateAction(sides, Tube.CCW));
			mTube.setRotate(sides, Tube.CCW);
		}
		//common solution for Step5
		Step1.concat(commands, commonSolution());
		
		return commands;
	}
	private Stack<RotateAction> moveEdgeTypeB2Top(int idx){
		Log.i(TAG+".moveEdgeTypeB2Top", "starting ... ");
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		while(idx<3){
			int[] sides = {Tube.top};
			commands.push(new RotateAction(sides, Tube.CCW));
			mTube.setRotate(sides, Tube.CCW);
			idx++;
		}
		
		//common solution
		Step1.concat(commands, commonSolution());
		//top CCW once
		int[] sides = {Tube.top};
		commands.push(new RotateAction(sides, Tube.CCW));
		mTube.setRotate(sides, Tube.CCW);
		//common solution again
		Step1.concat(commands, commonSolution());
		
		return commands;
	}
	private Stack<RotateAction> moveEdgeTypeC2Top(int idx){
		Log.i(TAG+".moveEdgeTypeC2Top", "starting ... ");
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		while(idx<3){
			int[] sides = {Tube.top};
			commands.push(new RotateAction(sides, Tube.CCW));
			mTube.setRotate(sides, Tube.CCW);
			idx++;
		}
		
		//common solution
		Step1.concat(commands, commonSolution());
		Step1.concat(commands, commonSolution());
		//top CCW once
		int[] sides = {Tube.top};
		commands.push(new RotateAction(sides, Tube.CCW));
		mTube.setRotate(sides, Tube.CCW);
		//common solution again
		Step1.concat(commands, commonSolution());
		
		return commands;	
	}
	public Stack<RotateAction> moveEdge2Top(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		int i;	
		
		if(getCountInPlace(Color.yellow)==4)
			return commands;
		
		//typeA
		for(i=0;i<typeATopIdx.length;i++){
			Color c1 = mTube.getVisibleFaceColor(Tube.top, typeATopIdx[i][0]);
			Color c2 = mTube.getVisibleFaceColor(Tube.top, typeATopIdx[i][1]);
			if(c1.equals(Color.yellow)&&c2.equals(Color.yellow))
				break;
		}
		if(i<typeATopIdx.length){
			Step1.concat(commands, moveEdgeTypeA2Top(i));
			return commands;
		} 
		
		//typeB
		for(i=0;i<typeBTopIdx.length;i++){
			Color c1 = mTube.getVisibleFaceColor(Tube.top, typeBTopIdx[i][0]);
			Color c2 = mTube.getVisibleFaceColor(Tube.top, typeBTopIdx[i][1]);
			if(c1.equals(Color.yellow)&&c2.equals(Color.yellow))
				break;
		}
		if(i<typeBTopIdx.length){
			Step1.concat(commands, moveEdgeTypeB2Top(i));
			return commands;
		}
		
		//typeC
		Cube[] cubes = mTube.getCubes();
		for(i=0;i<typeCTopIdx.length;i++){
			int wc1 = typeCTopIdx[i][0], wc2 = typeCTopIdx[i][2];
			Color c1 = cubes[wc1].getColor(typeCTopIdx[i][1]);
			Color c2 = cubes[wc2].getColor(typeCTopIdx[i][3]);
			if(c1.equals(Color.yellow)&&c2.equals(Color.yellow))
				break;
		}
		if(i<typeCTopIdx.length){
			Step1.concat(commands, moveEdgeTypeC2Top(i));
			return commands;
		}
		
		return commands;
	}
	
	private int[][] typeATopIdx = {
			{3, 5},
			{1, 7},
	};
	
	private int[][] typeBTopIdx = {
			{5, 1},
			{1, 3},
			{3, 7},
			{7, 5},
	};
	
	private int[][] typeCTopIdx = {
			{17, Tube.right, 7,  Tube.back},
			{7,  Tube.back,  15, Tube.left},
			{15, Tube.left,  25, Tube.front},
			{25, Tube.front, 17, Tube.right},
	};
	private int getCountInPlace(Color c){
		int count = 0;
		
		for(int i=0;i<topEdgeIdx.length;i++){
			if(mTube.getVisibleFaceColor(Tube.top, topEdgeIdx[i]).equals(c))
				count++;
		}
		
		return count;
	}
	private int[] topEdgeIdx = {
			5, 1, 3, 7,
	};
}
