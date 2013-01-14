package oms.cj.tube.way2;

import java.util.Stack;
import android.util.Log;

import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;

public class Step2 {
	private final static String TAG = "Step2";
	private Tube mTube;
	
	public Step2(Tube t){
		mTube = t;
	}
	
	public Tube getTube(){
		return mTube;
	}
	
	private final static int DELTAOFCCW = -1;
	private final static int DELTAOFCW = 1;
	private final static int CUBEOFBOTTOM = 1;
	private final static int CUBEOFEQUATOR = 4;
	private final static int CCWFACE = 6;
	private final static int CWFACE = 7;
	private final static int[][] pairs = {
		{19, 20, 11, 22, 23, 14, Tube.front, Tube.right},
		{11, 2,  1,  14, 5,  4,  Tube.right, Tube.back},
		{1,  0,  9,  4,  3,  12, Tube.back,  Tube.left},
		{9,  18, 19, 12, 21, 22, Tube.left,  Tube.front},
	};
	private boolean isFaceInPlace(int cube1, int cube2, int face){
		Cube[] cubes = mTube.getCubes();
		boolean bInPlace = false;
		
		Color c1 = cubes[cube1].getColor(face);
		Color c2 = cubes[cube2].getColor(face);
		if(c1.equals(c2))
			bInPlace = true;
		
		return bInPlace;
	}
	private int getIndexNotInPlace() {	
		int i=0;
		for(;i<pairs.length;i++){
			int bottomCube = pairs[i][CUBEOFBOTTOM]; 
			int ccwCube = pairs[i][CUBEOFBOTTOM+DELTAOFCCW];
			int ccwFace = pairs[i][CCWFACE];
			if(!isFaceInPlace(bottomCube, ccwCube, ccwFace))
				break;
			int cwCube = pairs[i][CUBEOFBOTTOM+DELTAOFCW];
			int cwFace = pairs[i][CWFACE];
			if(!isFaceInPlace(bottomCube, cwCube, cwFace))
				break;
			
			int equatorCube = pairs[i][CUBEOFEQUATOR];
			ccwCube = pairs[i][CUBEOFEQUATOR+DELTAOFCCW];
			ccwFace = pairs[i][CCWFACE];
			if(!isFaceInPlace(equatorCube, ccwCube, ccwFace))
				break;
			cwCube = pairs[i][CUBEOFEQUATOR+DELTAOFCW];
			cwFace = pairs[i][CWFACE];
			if(!isFaceInPlace(equatorCube, cwCube, cwFace))
				break;
		}
		
		return i;
	}
	
	private void y(Stack<RotateAction> commands, int loops, int dir){
		int[] sides = {Tube.top, Tube.equator, Tube.bottom};
		RotateAction r = new RotateAction(sides, dir);
		for(int i=0;i<loops;i++){
			commands.push(r);
			mTube.setRotate(sides, dir);
		}
	}
	private int[] edgeList = {1, 3, 5, 7, 15, 17, 11, 9, 21, 25, 23, 19};
	private int searchEdge(Color c1, Color c2){
		int pos = -1;
		Cube[] cubes = mTube.getCubes();
		
		for(int i=0;i<edgeList.length;i++){
			int idx = edgeList[i];
			int[] vfs = Tube.getVisibleFaces(idx);
			int j=0;
			for(;j<vfs.length;j++){
				Color c = cubes[idx].getColor(vfs[j]);
				if(c.equals(c1)||c.equals(c2))
					continue;
				else
					break;
			}
			if(j==vfs.length){
				pos = idx;
				break;
			}
		}
		
		return pos;
	}
	private int[] cornerList = {6, 8, 0, 2, 24, 26, 18, 20};
	private int searchCorner(Color c1, Color c2, Color c3){
		int pos = -1;
		Cube[] cubes = mTube.getCubes();
		
		for(int i=0;i<cornerList.length;i++){
			int idx = cornerList[i];
			int[] vfs = Tube.getVisibleFaces(idx);
			int j=0;
			for(;j<vfs.length;j++){
				Color c = cubes[idx].getColor(vfs[j]);
				if(c.equals(c1)||c.equals(c2)||c.equals(c3))
					continue;
				else
					break;
			}
			if(j==vfs.length){
				pos = idx;
				break;
			}
		}
		
		return pos;
	}
	private boolean isFaceEqual2Color(int cube, int face, Color color){
		Cube[] cubes = mTube.getCubes();

		return cubes[cube].getColor(face).equals(color);
	}
	private int getType(int edge, int corner){
		int type = -1;
		Cube[] cubes = mTube.getCubes();
		Color cFront = cubes[22].getColor(Cube.front);
		Color cRight = cubes[14].getColor(Cube.right);

		if(edge==23&&corner==20&&isFaceEqual2Color(corner, Tube.bottom, Color.white)){  		//row1
			return 1;
		}else if(edge==25&&corner==20&&isFaceEqual2Color(corner, Tube.bottom, Color.white)){
			return 2;
		}else if(edge==17&&corner==20&&isFaceEqual2Color(corner, Tube.bottom, Color.white)){
			return 3;
		}else if(edge==23&&corner==20&&isFaceEqual2Color(corner, Tube.front, Color.white) &&	//row2
				isFaceEqual2Color(edge, Tube.front, cFront)){
			return 4;
		}else if(edge==23&&corner==20&&isFaceEqual2Color(corner, Tube.front, Color.white) &&
				isFaceEqual2Color(edge, Tube.front, cRight)){
			return 5;
		}else if(edge==25&&corner==20&&isFaceEqual2Color(corner, Tube.front, Color.white) &&
				isFaceEqual2Color(edge, Tube.front, cFront)){
			return 6;
		}else if(edge==17&&corner==20&&isFaceEqual2Color(corner, Tube.front, Color.white) &&
				isFaceEqual2Color(edge, Tube.right, cRight)){
			return 7;
		}else if(edge==23&&corner==20&&isFaceEqual2Color(corner, Tube.right, Color.white) &&	//row3
				isFaceEqual2Color(edge, Tube.right, cRight)){
			return 8;
		}else if(edge==23&&corner==20&&isFaceEqual2Color(corner, Tube.right, Color.white) &&
				isFaceEqual2Color(edge, Tube.right, cFront)){
			return 9;
		}else if(edge==25&&corner==20&&isFaceEqual2Color(corner, Tube.right, Color.white) &&
				isFaceEqual2Color(edge, Tube.front, cFront)){
			return 10;
		}else if(edge==17&&corner==20&&isFaceEqual2Color(corner, Tube.right, Color.white) &&
				isFaceEqual2Color(edge, Tube.right, cRight)){
			return 11;
		}else if(edge==23&&corner==26&&isFaceEqual2Color(corner, Tube.top, Color.white) &&		//row4
				isFaceEqual2Color(edge, Tube.front, cFront)){
			return 12;
		}else if(edge==23&&corner==26&&isFaceEqual2Color(corner, Tube.top, Color.white) &&
				isFaceEqual2Color(edge, Tube.front, cRight)){
			return 13;
		}else if(edge==25&&corner==26&&isFaceEqual2Color(corner, Tube.top, Color.white) &&		//row5
				isFaceEqual2Color(edge, Tube.top, cRight)){
			return 14;
		}else if(edge==15&&corner==26&&isFaceEqual2Color(corner, Tube.top, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cRight)){
			return 15;
		}else if(edge==7&&corner==26&&isFaceEqual2Color(corner, Tube.top, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cRight)){
			return 16;
		}else if(edge==17&&corner==26&&isFaceEqual2Color(corner, Tube.top, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cRight)){
			return 17;
		}else if(edge==25&&corner==26&&isFaceEqual2Color(corner, Tube.top, Color.white) &&		//row6
				isFaceEqual2Color(edge, Tube.top, cFront)){
			return 18;
		}else if(edge==15&&corner==26&&isFaceEqual2Color(corner, Tube.top, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cFront)){
			return 19;
		}else if(edge==7&&corner==26&&isFaceEqual2Color(corner, Tube.top, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cFront)){
			return 20;
		}else if(edge==17&&corner==26&&isFaceEqual2Color(corner, Tube.top, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cFront)){
			return 21;
		}else if(edge==23&&corner==26&&isFaceEqual2Color(corner, Tube.front, Color.white) &&	//row7
				isFaceEqual2Color(edge, Tube.front, cFront)){
			return 22;
		}else if(edge==23&&corner==26&&isFaceEqual2Color(corner, Tube.front, Color.white) &&
				isFaceEqual2Color(edge, Tube.front, cRight)){
			return 23;
		}else if(edge==25&&corner==26&&isFaceEqual2Color(corner, Tube.front, Color.white) &&	//row8
				isFaceEqual2Color(edge, Tube.top, cRight)){
			return 24;
		}else if(edge==15&&corner==26&&isFaceEqual2Color(corner, Tube.front, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cRight)){
			return 25;
		}else if(edge==7&&corner==26&&isFaceEqual2Color(corner, Tube.front, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cRight)){
			return 26;
		}else if(edge==17&&corner==26&&isFaceEqual2Color(corner, Tube.front, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cRight)){
			return 27;
		}else if(edge==25&&corner==26&&isFaceEqual2Color(corner, Tube.front, Color.white) &&	//row9
				isFaceEqual2Color(edge, Tube.top, cFront)){
			return 28;
		}else if(edge==15&&corner==26&&isFaceEqual2Color(corner, Tube.front, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cFront)){
			return 29;
		}else if(edge==7&&corner==26&&isFaceEqual2Color(corner, Tube.front, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cFront)){
			return 30;
		}else if(edge==17&&corner==26&&isFaceEqual2Color(corner, Tube.front, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cFront)){
			return 31;
		}else if(edge==23&&corner==26&&isFaceEqual2Color(corner, Tube.right, Color.white) &&	//row10
				isFaceEqual2Color(edge, Tube.front, cFront)){
			return 32;
		}else if(edge==23&&corner==26&&isFaceEqual2Color(corner, Tube.right, Color.white) &&
				isFaceEqual2Color(edge, Tube.front, cRight)){
			return 33;
		}else if(edge==25&&corner==26&&isFaceEqual2Color(corner, Tube.right, Color.white) &&	//row11
				isFaceEqual2Color(edge, Tube.top, cRight)){
			return 34;
		}else if(edge==15&&corner==26&&isFaceEqual2Color(corner, Tube.right, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cRight)){
			return 35;
		}else if(edge==7&&corner==26&&isFaceEqual2Color(corner, Tube.right, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cRight)){
			return 36;
		}else if(edge==17&&corner==26&&isFaceEqual2Color(corner, Tube.right, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cRight)){
			return 37;
		}else if(edge==25&&corner==26&&isFaceEqual2Color(corner, Tube.right, Color.white) &&	//row12
				isFaceEqual2Color(edge, Tube.top, cFront)){
			return 38;
		}else if(edge==15&&corner==26&&isFaceEqual2Color(corner, Tube.right, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cFront)){
			return 39;
		}else if(edge==7&&corner==26&&isFaceEqual2Color(corner, Tube.right, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cFront)){
			return 40;
		}else if(edge==17&&corner==26&&isFaceEqual2Color(corner, Tube.right, Color.white) &&
				isFaceEqual2Color(edge, Tube.top, cFront)){
			return 41;
		}
		
		return type;
	}
	Solver[] solverList = {
		null,
		new Solver1(),	
		new Solver2(),
		new Solver3(),
		new Solver4(),
		new Solver5(),
		new Solver6(),
		new Solver7(),
		new Solver8(),
		new Solver9(),
		new Solver10(),
		new Solver11(),
		new Solver12(),
		new Solver13(),
		new Solver14(),
		new Solver15(),
		new Solver16(),
		new Solver17(),
		new Solver18(),
		new Solver19(),
		new Solver20(),
		new Solver21(),
		new Solver22(),
		new Solver23(),
		new Solver24(),
		new Solver25(),
		new Solver26(),
		new Solver27(),
		new Solver28(),
		new Solver29(),
		new Solver30(),
		new Solver31(),
		new Solver32(),
		new Solver33(),
		new Solver34(),
		new Solver35(),
		new Solver36(),
		new Solver37(),
		new Solver38(),
		new Solver39(),
		new Solver40(),
		new Solver41(),
	};
	public Stack<RotateAction> f2l(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		Cube[] cubes = mTube.getCubes();
		
		int idx;
		while((idx = getIndexNotInPlace())!=pairs.length){
			y(commands, idx, Tube.CW);
			Color cFront = cubes[22].getColor(Cube.front);
			Color cRight = cubes[14].getColor(Cube.right);
			Log.i(TAG+".f2l", "cFront="+cFront.toString()+";cRight="+cRight.toString());
			int edge = searchEdge(cFront, cRight);
			int corner = searchCorner(cFront, cRight, Color.white);
			Log.i(TAG+".f2l", "edge="+edge+";corner="+corner);
			
			int type = getType(edge, corner);
			Log.i(TAG+".f2l", "type="+type);
			if(type==-1){
				if(corner==18||corner==0||corner==2){
					moveCorner2Top(commands, mTube, corner);
				}
				corner = searchCorner(cFront, cRight, Color.white);
				edge = searchEdge(cFront, cRight);
				Log.i(TAG+".f2l", "edge="+edge+" for 2nd adjustment!");
				if(edge==21||edge==3||edge==5){
					moveEdge2Top(commands, mTube, edge, corner);
				}
				
				int count = getCount2Match(mTube);
				//rotate top side to meet 1-41
				Step1.moveSide(commands, Tube.top, count, mTube);

			} else {
				Solver solver = solverList[type];
				solver.solve(commands, mTube);
			}
		}
		
		return commands;
	}
	private int getCount2Match(Tube t){
		Cube[] cubes = mTube.getCubes();
		Color cFront = cubes[22].getColor(Cube.front);
		Color cRight = cubes[14].getColor(Cube.right);
		
		int i=0;
		for(;i<4;i++){
			int corner = searchCorner(cFront, cRight, Color.white);
			int edge = searchEdge(cFront, cRight);
			int type = getType(edge, corner);
			logCube(edge);
			logCube(corner);
			if(type!=-1)
				break;
			else{
				t.setRotate(Tube.top, Tube.CW);
			}
		}
		if(i==4){
			Log.e(TAG+".f2l", "exception! after moving both edge nad corner to top, can not find a correct type!");
		} else {
			for(int j=0;j<i;j++)
				t.setRotate(Tube.top, Tube.CCW);
		}
		
		return i%4;
	}
	private void logCube(int cube){
		Cube[] cubes = mTube.getCubes();
		int[] vfs = Tube.getVisibleFaces(cube);
		String logStr="cube="+cube+"    ";
		
		for(int i=0;i<vfs.length;i++){
			int face = vfs[i];
			Color c = cubes[cube].getColor(face);
			String str = Tube.LAYERSTR_F[face] + ":" + c.toString() + "  ";
			logStr = logStr + str;
		}
		Log.i(TAG+".logCube", logStr);
	}
	private final static int[][] cornerSteps = {
		{18, 7,  Tube.left,  Tube.CW,  Tube.top, Tube.CCW, Tube.left,  Tube.CCW},
		{0,  17, Tube.back,  Tube.CW,  Tube.top, Tube.CCW, Tube.back,  Tube.CCW},
		{2,  25, Tube.right, Tube.CCW, Tube.top, Tube.CCW,  Tube.right, Tube.CW},
	};
	private void moveCorner2Top(Stack<RotateAction> commands, Tube t, int corner){
		int idx = -1;
		for(int i=0;i<cornerSteps.length;i++){
			if(cornerSteps[i][0]==corner){
				idx = i;
				break;
			}
		}
		if(idx!=-1){
			if(corner == cornerSteps[idx][1]){
				Step1.moveSide(commands, Tube.top, 1, t);
			}
			for(int i=0;i<3;i++){
				int side = cornerSteps[idx][2*i+2];
				int dir = cornerSteps[idx][2*i+3];
				RotateAction r = new RotateAction(side, dir);
				commands.push(r);
				t.setRotate(side, dir);
			}
		} else {
			Log.e(TAG+".moveCorner2Top", "exception! pass invalid corner ... corner="+corner);
		}
	}
	private final static int[][] edgeSteps = {
		{21, 24, Tube.left,  Tube.CW,  Tube.top, Tube.CCW, Tube.left,  Tube.CCW},
		{3,  6,  Tube.back,  Tube.CW,  Tube.top, Tube.CCW, Tube.back,  Tube.CCW},
		{5,  8,  Tube.right, Tube.CCW, Tube.top, Tube.CCW,  Tube.right, Tube.CW},
	};
	private void moveEdge2Top(Stack<RotateAction> commands, Tube t, int edge, int corner){
		int idx = -1;
		for(int i=0;i<edgeSteps.length;i++){
			if(edgeSteps[i][0]==edge){
				idx = i;
				break;
			}
		}
		if(idx!=-1){
			if(corner == edgeSteps[idx][1]){
				Step1.moveSide(commands, Tube.top, 1, t);
			}
			for(int i=0;i<3;i++){
				int side = edgeSteps[idx][2*i+2];
				int dir = edgeSteps[idx][2*i+3];
				RotateAction r = new RotateAction(side, dir);
				commands.push(r);
				t.setRotate(side, dir);
			}
		} else {
			Log.e(TAG+".moveEdge2Top", "exception! pass invalid edge ... edge="+edge);
		}
	}
	abstract class Solver{
		public void solve(Stack<RotateAction> commands, Tube t, int[][][] stepList){
			for(int i=0;i<stepList.length;i++){
				int[] sides = stepList[i][0];
				int dir = stepList[i][1][0];
				RotateAction r = new RotateAction(sides, dir);
				commands.push(r);
				t.setRotate(sides, dir);
			}
		}
		public abstract void solve(Stack<RotateAction> commands, Tube t);
	}
	class Solver1 extends Solver{
		//(R U'U'R'U)2 y'(R'U'R)
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW}  },
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW}  },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },
		};

		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}
	class Solver2 extends Solver{
		//(U R U'R'U')y'(R'U R)
		private int[][][] stepList = {
				{ {Tube.top},   {Tube.CW}  },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW}  },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },
		};

		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}
	class Solver3 extends Solver{
		//U'(F'RUR'U')(R'FR)
		private int[][][] stepList = {
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}
	class Solver4 extends Solver{
		//(RUR'U')(RU'U'R'U')(RUR')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}
	class Solver5 extends Solver{
		//(R U'R U)y(R U'R'F2)
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver6 extends Solver{
		//y'(R'U' R U)(R'U'R)
		private int[][][] stepList = {
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },	
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver7 extends Solver{
		//(R U'R'U)(R U'R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver8 extends Solver{
		//(RU'R'U)(R U'U'R'U)(RU'R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}
	class Solver9 extends Solver{
		//R2 y(R U R'U')y'(R'U R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },	
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver10 extends Solver{
		//y'(R'U)(R U')(R'U R)¡¡
		private int[][][] stepList = {
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },	
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver11 extends Solver{
		//(R U R'U')(R U R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver12 extends Solver{
		//(R U R'U')2 (R U R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },				
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver13 extends Solver{
		//(R U'R') y'(R'U2 R)
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },	
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },	
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver14 extends Solver{
		//y'(R'U2)(R U R'U')R
		private int[][][] stepList = {
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CCW} },	
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },	
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },	
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver15 extends Solver{
		//y'U'(R' U2)(R U'R'U)R
		private int[][][] stepList = {
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },	
				{ {Tube.top},   {Tube.CCW} },	
				{ {Tube.right}, {Tube.CCW} },	
				{ {Tube.top},   {Tube.CW} },	
				{ {Tube.top},   {Tube.CW} },	
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },	
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver16 extends Solver{
		//y'(R'U R U'U')(R'U'R)
		private int[][][] stepList = {
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },	
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver17 extends Solver{
		//(R U R'U)(R U'U'R'd')(R'U R)
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver18 extends Solver{
		//(RUR')U2(R U R'U')(RUR')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver19 extends Solver{
		//(R U'R' U2)(R U R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver20 extends Solver{
		//U(R U'U')(R'U R U')R'
		private int[][][] stepList = {
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver21 extends Solver{
		//(R U'U')(R'U'R U)R'
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver22 extends Solver{
		//U'(R U')(R'U2)(R U'R')
		private int[][][] stepList = {
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver23 extends Solver{
		//U'(R U R') d (R'U'R)
		private int[][][] stepList = {
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver24 extends Solver{
		//d(R'U R U')(R'U'R)
		private int[][][] stepList = {
				{ {Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver25 extends Solver{
		//y'(R'U'R)
		private int[][][] stepList = {
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },	
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver26 extends Solver{
		//(d R'U'R U')(R'U'R)
		private int[][][] stepList = {
				{ {Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver27 extends Solver{
		//y'(R U'U')R'2 U'R2 U'R'
		private int[][][] stepList = {
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },	
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver28 extends Solver{
		//y'(R'U)(R d'U')(R U R')
		private int[][][] stepList = {
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver29 extends Solver{
		//U'(R U'U')(R'U2)(R U'R')
		private int[][][] stepList = {
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver30 extends Solver{
		//U'(R U R' U')(R U'U'R')
		private int[][][] stepList = {
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver31 extends Solver{
		//U R U'R'
		private int[][][] stepList = {
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver32 extends Solver{
		//U'(R U'U'R'U)(R U R')
		private int[][][] stepList = {
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver33 extends Solver{
		//d(R'U'R)d'(R U R')
		private int[][][] stepList = {
				{ {Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver34 extends Solver{
		//y'U'(R'U R)
		private int[][][] stepList = {
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },	
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },	
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver35 extends Solver{
		//(d R'U'R U'U')(R' U R)
		private int[][][] stepList = {
				{ {Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver36 extends Solver{
		//d(R'U2)(R U'U')(R'U R)
		private int[][][] stepList = {
				{ {Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver37 extends Solver{
		//(R U'R'U)(d R'U'R)
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver38 extends Solver{
		//(R U')(R'U)(R U')(R'U2)(R U'R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver39 extends Solver{
		//U'(R U R' U)(R U R')
		private int[][][] stepList = {
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver40 extends Solver{
		//(R U R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}	
	class Solver41 extends Solver{
		//U'(R U'R'U)(R U R')
		private int[][][] stepList = {
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top},   {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
	}
}
