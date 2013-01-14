/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oms.cj.tube.solver;

import java.util.Stack;
import org.kociemba.twophase.Search;
import oms.cj.tube.component.Color;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import oms.cj.tube.player.PlayerView;
import oms.cj.tube.way1.Step1;
import oms.cj.tube.way1.Step2;
import oms.cj.tube.way1.Step3;
import oms.cj.tube.way1.Step4;
import oms.cj.tube.way1.Step5;
import oms.cj.tube.way1.Step6;
import oms.cj.tube.way1.Step7;
import oms.cj.tube.way1.Step8;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

public class SolverView extends PlayerView {
	private static final String TAG="SolverView";

	public SolverView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void basicSolve(final Handler handler){
		queueEvent(new Runnable(){
			public void run() {
				Stack<RotateAction> commands = _basicSolve(getTube(), null);
				Message msg = new Message();
				msg.obj = commands;
				msg.what = TubeSolver.SOLVECOMPLETED;
				handler.sendMessage(msg);
				
				clearBeforeSolve();
				setCommands(commands);
			}
		});
	}
	public void fridchSolve(final Handler handler){
		queueEvent(new Runnable(){
			public void run() {
				Stack<RotateAction> commands = _fridchSolve(getTube());
				Message msg = new Message();
				msg.obj = commands;
				msg.what = TubeSolver.SOLVECOMPLETED;
				handler.sendMessage(msg);
				
		    	clearBeforeSolve();
				setCommands(commands);
			}
		});
	}	
	public void advancedSolve(final Handler handler){
		queueEvent(new Runnable(){
			public void run() {
				Stack<RotateAction> commands = _advancedSolve(getTube());
				Message msg = new Message();
				msg.obj = commands;
				msg.what = TubeSolver.SOLVECOMPLETED;
				handler.sendMessage(msg);
				
		    	clearBeforeSolve();
				setCommands(commands);
			}
		});		
	}
	
    public static Stack<RotateAction> _basicSolve(Tube tube, Color[] faceColor){
    	Stack<RotateAction> returnCommands = new Stack<RotateAction>();
    	
		Tube copyTube = new Tube(tube);
		Step1 s1 = new Step1(copyTube);
		returnCommands = s1.moveTo16(Color.yellow);
		Stack<RotateAction> commands = s1.moveEdge2Top();
		concat(returnCommands, commands);
		
		Step2 s2 = new Step2(s1.getTube());
		commands = s2.moveTopWhiteEdge2Bottom();
		concat(returnCommands, commands);
		commands = s2.moveBottom2Top();
		concat(returnCommands, commands);
		
		Step3 s3 = new Step3(s2.getTube());
		commands = s3.moveWhiteCorner2Top();
		concat(returnCommands, commands);

		Step4 s4 = new Step4(s3.getTube());
		commands = s4.sideEdgeInPlace();
		concat(returnCommands, commands);
		
		Step5 s5 = new Step5(s4.getTube());
		commands = s5.moveEdge2Top();
		concat(returnCommands, commands);
	
		Step6 s6 = new Step6(s5.getTube());
		commands = s6.moveCorner2Top();
		concat(returnCommands, commands);
		
		Step7 s7 = new Step7(s6.getTube());
		commands = s7.adaptCorner2Side();
		concat(returnCommands, commands);
		
		Step8 s8 = new Step8(s7.getTube());
		commands = s8.topEdgeInPlace();
		concat(returnCommands, commands);
		
		if(faceColor!=null){
			s8.getTube().getCenterColors(faceColor);
		}
		return returnCommands;
    }
    
    public static Stack<RotateAction> _fridchSolve(Tube tube){
    	Stack<RotateAction> returnCommands = new Stack<RotateAction>();
      	
		Tube copyTube = new Tube(tube);
		oms.cj.tube.way2.Step1 s1 = new oms.cj.tube.way2.Step1(copyTube);
		returnCommands = s1.moveTo16(Color.white);  
		Stack<RotateAction> commands = s1.moveEdge2Top(Color.white);
		concat(returnCommands, commands);
		
		oms.cj.tube.way2.Step2 s2 = new oms.cj.tube.way2.Step2(s1.getTube());
		commands = s2.f2l();
		concat(returnCommands, commands);
		
		oms.cj.tube.way2.Step3 s3 = new oms.cj.tube.way2.Step3(s2.getTube());
		commands = s3.oll();
		concat(returnCommands, commands);
		
		oms.cj.tube.way2.Step4 s4 = new oms.cj.tube.way2.Step4(s3.getTube());
		commands = s4.pll();
		concat(returnCommands, commands);
		
		return returnCommands;
    }
    
    public static Stack<RotateAction> _advancedSolve(Tube tube){
    	Stack<RotateAction> returnCommands = new Stack<RotateAction>();
    	
    	int maxDepth = 24;
    	long timeOut = 30;
    	boolean useSeparator = false;
    	String facelets = tube.toKociembaFacelet();
    	Log.i(TAG+"advancedSolve", "facelets="+facelets);

		String solutionString = Search.solution(facelets, maxDepth, timeOut, 
				useSeparator);
		
	    Log.i(TAG+".advancedSolve", "solutionString="+solutionString);
	    returnCommands = Tube.parseKociembaSolution(solutionString);
	    
	    return returnCommands;
    }
	
	private static void concat(Stack<RotateAction> s1, Stack<RotateAction>s2){
		for(int i=0;i<s2.size();i++){
			s1.push(s2.get(i));
		}
	}
}