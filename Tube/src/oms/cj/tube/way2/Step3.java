package oms.cj.tube.way2;

import java.util.Stack;
import android.util.Log;
import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;

public class Step3 {
	private final static String TAG = "Step3";
	private Tube mTube;
	
	public Step3(Tube t){
		mTube = t;
	}
	
	public Tube getTube(){
		return mTube;
	}
	
	private boolean isComplete(Tube t, Color color){
		boolean bComplete = true;
		
		for(int i=0;i<Tube.CubesEachSide;i++){
			Color c = t.getVisibleFaceColor(Tube.top, i);
			if(!c.equals(color)){
				bComplete = false;
				break;
			}
		}
		
		return bComplete;
	}
	
	public final static int[][] topSides = {
			{6,  Tube.back},
			{7,  Tube.back},
			{8,  Tube.back},
			{8,  Tube.right},
			{17, Tube.right},
			{26, Tube.right},
			{26, Tube.front},
			{25, Tube.front},
			{24, Tube.front},
			{24, Tube.left},
			{15, Tube.left},
			{6 , Tube.left},
	};
	public static void logSides(Tube t, String TAG, int[][] sides){
		Cube[] cubes = t.getCubes();
		String str = "";
		
		for(int i=0;i<sides.length;i++){
			if(i%3==0)
				str = "";
			int side = sides[i][1];
			int cube = sides[i][0];
			Color c = cubes[cube].getColor(side);
			str = str + c.toString() + "  ";
			if(i%3==2)
				Log.i(TAG+".logSides", str);
		}
	}
	private void logFace(Tube t, String TAG, int face){
		for(int i=0;i<Tube.CubesEachSide/3;i++){
			String str = "";
			for(int j=0;j<Tube.CubesEachSide/3;j++){
				Color c = t.getVisibleFaceColor(face, i*Tube.CubesEachSide/3+j);
				str = str + c.toString() + "  ";
			}
			Log.i(TAG+".logFace", str);
		}
	}
	public Stack<RotateAction> oll(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		
		while(!isComplete(mTube, Color.yellow)){    //should loop only once
			int i=0;
			for(;i<solverList.length;i++){
				Solver solver = solverList[i];
				//call the override method "getCount2Match(Tube t)"
				int count = solver.getCount2Match(mTube);	
				if(count!=-1){
					Step1.moveSide(commands, Tube.top, count, mTube);
					break;
				}
			}
			
			logFace(mTube, TAG, Tube.top);
			logSides(mTube, TAG, topSides);
			if(i!=solverList.length)
				solverList[i].solve(commands, mTube);
			else {
				logFace(mTube, TAG, Tube.top);
				logSides(mTube, TAG, topSides);
				Log.e(TAG+".oll", "exception! loop more than once!");
			}
		}
		
		return commands;
	}
	
	Solver[] solverList = {
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
			new Solver42(),
			new Solver43(),
			new Solver44(),
			new Solver45(),
			new Solver46(),
			new Solver47(),
			new Solver48(),
			new Solver49(),
			new Solver50(),
			new Solver51(),
			new Solver52(),
			new Solver53(),
			new Solver54(),
			new Solver55(),
			new Solver56(),
			new Solver57(),
			new Solver3R(),
			new Solver5R(),
			new Solver6R(),
			new Solver10R(),
			new Solver12R(),
			new Solver13R(),
			new Solver42R(),
			new Solver47R(),
	};
	abstract class Solver{
		protected void solve(Stack<RotateAction> commands, Tube t, int[][][] stepList){
			for(int i=0;i<stepList.length;i++){
				int[] sides = stepList[i][0];
				int dir = stepList[i][1][0];
				RotateAction r = new RotateAction(sides, dir);
				commands.push(r);
				t.setRotate(sides, dir);
			}
		}
		protected boolean match(Tube t, int[] typeDefs, Color color){
			boolean bMatch = true;
			Cube[] cubes = t.getCubes();
			
			for(int j=0;j<Tube.CubesEachSide;j++){
				int cube = t.getCube(Tube.top, j);
				int face = typeDefs[j];
				Color c = cubes[cube].getColor(face);
				if(!c.equals(color)){
					bMatch = false;
					break;
				}
			}
			
			return bMatch;
		}
		// return values:
		// 0-3,   which means a match is found, and such number CW turns are done
		// -1,    which means a match is not found
		protected int getCount2Match(Tube t, int[] typeDefs){
			int i=0;
			for(;i<4;i++){
				boolean bMatch = match(t, typeDefs, Color.yellow);
				if(bMatch)
					break;
				else{
					t.setRotate(Tube.top, Tube.CW);
				}
			}
			if(i!=4){
				for(int j=0;j<i;j++)
					t.setRotate(Tube.top, Tube.CCW);
			} else 
				i=-1;
			
			return i;
		}
		
		protected abstract int getCount2Match(Tube t);
		public abstract void solve(Stack<RotateAction> commands, Tube t);
	}
	class Solver1 extends Solver{
		private int[] typeDefs = {
				Tube.left, Tube.back,  Tube.right,
				Tube.left, Tube.top,   Tube.right,
				Tube.left, Tube.front, Tube.right,
		};
		// (R U'U') (R2' F R F') U2 (R' F R F')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top},   {Tube.CCW}  },
				{ {Tube.top},   {Tube.CCW}  },
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.front}, {Tube.CW}  },
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.front}, {Tube.CCW}  },
				{ {Tube.top},   {Tube.CW}  },
				{ {Tube.top},   {Tube.CW}  },
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.front}, {Tube.CW}  },
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.front}, {Tube.CCW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver2 extends Solver{
		private int[] typeDefs = {
				Tube.left, Tube.back,  Tube.back,
				Tube.left, Tube.top,   Tube.right,
				Tube.left, Tube.front, Tube.front,
		};
		//(F R U R' U' F') (f R U R' U' f')
		private int[][][] stepList = {
				{ {Tube.front}, {Tube.CW}  },
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top}, {Tube.CW}  },
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.top}, {Tube.CCW}  },
				{ {Tube.front}, {Tube.CCW}  },
				{ {Tube.front, Tube.standing}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top}, {Tube.CW}  },
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.top}, {Tube.CCW}  },
				{ {Tube.front, Tube.standing}, {Tube.CCW} },				
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver3 extends Solver{
		private int[] typeDefs = {
				Tube.left, Tube.back,  Tube.right,
				Tube.left, Tube.top,   Tube.right,
				Tube.left, Tube.front, Tube.right,
		};
		//f(R U R' U')f' U' F(R U R' U')F'
		private int[][][] stepList = {
				{ {Tube.front, Tube.standing}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top}, {Tube.CW}  },
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.top}, {Tube.CCW}  },
				{ {Tube.front, Tube.standing}, {Tube.CCW} },	
				{ {Tube.top}, {Tube.CCW}  },
				{ {Tube.front}, {Tube.CW}  },
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top}, {Tube.CW}  },
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.top}, {Tube.CCW}  },
				{ {Tube.front}, {Tube.CCW}  },				
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver4 extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.back,  Tube.top,
				Tube.left,  Tube.top,   Tube.right,
				Tube.front, Tube.front, Tube.right,
		};
		// f(R U R' U')y x(R' F)(R U R' U')F'
		private int[][][] stepList = {
				{ {Tube.front, Tube.standing}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top}, {Tube.CW}  },
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.top}, {Tube.CCW}  },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },
				{ {Tube.left, Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top}, {Tube.CW}  },
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.top}, {Tube.CCW}  },
				{ {Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver5 extends Solver{
		private int[] typeDefs = {
				Tube.back, Tube.back,  Tube.right,
				Tube.left, Tube.top,   Tube.top,
				Tube.left, Tube.top,   Tube.top,
		};
		//(r' U2) (R U R'U) r
		private int[][][] stepList = {
				{ {Tube.right, Tube.middle}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top}, {Tube.CW}  },
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.top}, {Tube.CW}  },
				{ {Tube.right, Tube.middle}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver6 extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.top,   Tube.top,
				Tube.left,  Tube.top,   Tube.top,
				Tube.front, Tube.front, Tube.right,
		};
		//(r U'U') (R' U' R U' r')
		private int[][][] stepList = {
				{ {Tube.right, Tube.middle}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.top}, {Tube.CCW}  },
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top}, {Tube.CCW}  },
				{ {Tube.right, Tube.middle}, {Tube.CCW} },				
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver7 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.top,   Tube.right,
				Tube.top,   Tube.top,   Tube.right,
				Tube.top,   Tube.front, Tube.front,
		};
		//r U R' U R U'U' r'
		private int[][][] stepList = {
				{ {Tube.right, Tube.middle}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right, Tube.middle}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver8 extends Solver{
		private int[] typeDefs = {
				Tube.top,   Tube.back,  Tube.back,
				Tube.top,   Tube.top,   Tube.right, 
				Tube.front, Tube.top,  Tube.right,
		};
		// r' U' R U' R' U2 r
		private int[][][] stepList = {
				{ {Tube.right, Tube.middle}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right, Tube.middle}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver9 extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.top,   Tube.back,
				Tube.top,   Tube.top,   Tube.right,
				Tube.front, Tube.front, Tube.top,
		};
		//(R' U' R) y' x' (R U')(R'F) (R U R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.left, Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.left, Tube.middle, Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver10 extends Solver{
		private int[] typeDefs = {
				Tube.back, Tube.back, Tube.top,
				Tube.top,  Tube.top,  Tube.right,
				Tube.left, Tube.top,  Tube.front,
		};
		//(R U R'U)(R'F R F') (RU'U'R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CCW} },			
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver11 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.back,  Tube.right,
				Tube.left,  Tube.top,   Tube.top,
				Tube.top,   Tube.top,   Tube.front,
		};
		//r'(R2 U R' U)(R U'U' R' U) (r R')
		private int[][][] stepList = {
				{ {Tube.right, Tube.middle}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },		
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CCW} },	
				{ {Tube.top}, {Tube.CW} },	
				{ {Tube.right, Tube.middle}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver12 extends Solver{
		private int[] typeDefs = {
				Tube.top,   Tube.top,  Tube.back,
				Tube.left,  Tube.top,  Tube.top,
				Tube.front, Tube.front, Tube.right,
		};
		// (r R'2 U' R U')(R' U2 R U' R)r'
		private int[][][] stepList = {
				{ {Tube.right, Tube.middle}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },	
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.right, Tube.middle}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver13 extends Solver{
		private int[] typeDefs = {
				Tube.back, Tube.back,  Tube.right,
				Tube.top,  Tube.top,   Tube.top,
				Tube.top,  Tube.front, Tube.front,
		};
		//(r U' r' U')(r U r') (F' U F)
		private int[][][] stepList = {
				{ {Tube.right, Tube.middle}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.right, Tube.middle}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.right, Tube.middle}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right, Tube.middle}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.front}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver14 extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.back,  Tube.back,
				Tube.top,   Tube.top,   Tube.top,
				Tube.front, Tube.front, Tube.top,
		};
		//R' F R U R' F'R (F U' F')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver15 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.back,  Tube.right,
				Tube.top,   Tube.top,   Tube.top,
				Tube.left,  Tube.front, Tube.top,
		};
		//(r' U' r) (R'U'R U) (r' U r)
		private int[][][] stepList = {
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },		
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver16 extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.back,  Tube.top,
				Tube.top,   Tube.top,   Tube.top,
				Tube.front, Tube.front, Tube.right,
		};
		// (r U r') (R U R' U') (r U' r')
		private int[][][] stepList = {
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },		
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },		
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver17 extends Solver{
		private int[] typeDefs = {
				Tube.top,   Tube.back,  Tube.right,
				Tube.left,  Tube.top,   Tube.right,
				Tube.front, Tube.front, Tube.top,
		};
		// F(U R' U'F')U (F R2 U R'U'F ')
		private int[][][] stepList = {
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },				
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver18 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.back,  Tube.back,
				Tube.left,  Tube.top,   Tube.right,
				Tube.top,   Tube.front, Tube.top,
		};
		//F (R U R' d)(R' U2) (R' F R F')
		private int[][][] stepList = {
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver19 extends Solver{
		private int[] typeDefs = {
				Tube.top,  Tube.back,  Tube.top,
				Tube.left, Tube.top,   Tube.right,
				Tube.left, Tube.front, Tube.right,
		};
		//(r' R U)(R U R' U' r) (R'2 F R F')
		private int[][][] stepList = {
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver20 extends Solver{
		private int[] typeDefs = {
				Tube.top,  Tube.back,  Tube.top,
				Tube.left, Tube.top,   Tube.right,
				Tube.top,  Tube.front, Tube.top,
		};
		// r'(R U) (R U R'U' r2)(R2'U) (R U') r'
		private int[][][] stepList = {
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver21 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.top, Tube.back,
				Tube.top,   Tube.top, Tube.top,
				Tube.front, Tube.top, Tube.front,
		};
		//(R U'U') (R' U' R U R' U') (R U' R')
		private int[][][] stepList = {
				
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver22 extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.top,  Tube.back,
				Tube.top,   Tube.top,  Tube.top,
				Tube.left,  Tube.top,  Tube.front,
		};
		//R U'U' (R'2 U') (R2 U') R'2 U2 R
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver23 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.top,  Tube.back,
				Tube.top,   Tube.top,  Tube.top,
				Tube.top,   Tube.top,  Tube.top,
		};
		//(R2 D') (R U'U') (R' D) (R U'U' R)
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.bottom}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver24 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.top,  Tube.top,
				Tube.top,   Tube.top,  Tube.top,
				Tube.front, Tube.top,  Tube.top,
		};
		//(r U R' U') (r' F R F')
		private int[][][] stepList = {
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver25 extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.top,  Tube.top,
				Tube.top,   Tube.top,  Tube.top,
				Tube.top,   Tube.top,  Tube.front,
		};
		//F'(r U R' U') (r' F R)
		private int[][][] stepList = {
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver26 extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.top,  Tube.top,
				Tube.top,   Tube.top,  Tube.top,
				Tube.front, Tube.top,  Tube.right,
		};
		// R U' U' R' U' R U' R'
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },				
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver27 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.top,  Tube.right,
				Tube.top,   Tube.top,  Tube.top,
				Tube.left,  Tube.top,  Tube.top,
		};
		//R' U2 R U R' U R
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver28 extends Solver{
		private int[] typeDefs = {
				Tube.top, Tube.top,  Tube.top,
				Tube.top, Tube.top,  Tube.right,
				Tube.top, Tube.front, Tube.top,
		};
		// (r U R' U') (r' R U) (R U' R')
		private int[][][] stepList = {
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },	
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver29 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.top,   Tube.top,
				Tube.top,   Tube.top,   Tube.right,
				Tube.front, Tube.front, Tube.top,
		};
		//(r U R' U')(R r'2 F R F') (r R')
		private int[][][] stepList = {
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver30 extends Solver{
		private int[] typeDefs = {
				Tube.top,  Tube.back,  Tube.top,
				Tube.left, Tube.top,   Tube.top,
				Tube.left, Tube.top,   Tube.right,
		};
		//(R2 U R' B')(RU') (R2' U) (R B R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.back}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.back}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver31 extends Solver{
		private int[] typeDefs = {
				Tube.top,  Tube.back,  Tube.back,
				Tube.top,  Tube.top,   Tube.right,
				Tube.top,  Tube.top,   Tube.front,
		};
		//(r' F' U F) (L F' L' U' r)
		private int[][][] stepList = {
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.left}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.left}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver32 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.back,  Tube.top,
				Tube.left,  Tube.top,   Tube.top,
				Tube.front, Tube.top,   Tube.top,
		};
		// (R U)(B' U')(R' U R B R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.back}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.back}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver33 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.back,  Tube.top,
				Tube.top,   Tube.top,   Tube.top,
				Tube.front, Tube.front, Tube.top,
		};
		//(R U R' U') (R' F R F')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver34 extends Solver{
		private int[] typeDefs = {
				Tube.top,  Tube.back,  Tube.top,
				Tube.top,  Tube.top,   Tube.top,
				Tube.left, Tube.front, Tube.right,
		};
		//(R'U'R U) y(r U R' U')r' R
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver35 extends Solver{
		private int[] typeDefs = {
				Tube.top,   Tube.back, Tube.right,
				Tube.left,  Tube.top,  Tube.top,
				Tube.front, Tube.top, Tube.top,
		};
		// R U'U'R2' F R F'(R U'U'R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver36 extends Solver{
		private int[] typeDefs = {
				Tube.top,   Tube.back, Tube.right,
				Tube.top,   Tube.top,  Tube.right,
				Tube.front, Tube.top, Tube.top,
		};
		//R'U'R U' R'U R U l U'R'U
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },		
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },	
				{ {Tube.left, Tube.middle}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.left, Tube.middle, Tube.right}, {Tube.CW} },
				
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver37 extends Solver{
		private int[] typeDefs = {
				Tube.top,   Tube.top,  Tube.right,
				Tube.top,   Tube.top,  Tube.right,
				Tube.front, Tube.front, Tube.top,
		};
		//F (R U' R'U'R U) (R' F')
		private int[][][] stepList = {
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver38 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.top,   Tube.top,
				Tube.top,   Tube.top,   Tube.right,
				Tube.top,   Tube.front, Tube.right,
		};
		//(R U R'U) (R U'R'U') (R'F R F')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },	
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver39 extends Solver{
		private int[] typeDefs = {
				Tube.back, Tube.back,  Tube.top,
				Tube.top,  Tube.top,   Tube.top,
				Tube.top,  Tube.front, Tube.right,
		};
		//(r U' r' U' r)y(R U R' f')
		private int[][][] stepList = {
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },	
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },	
				{ {Tube.standing, Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver40 extends Solver{
		private int[] typeDefs = {
				Tube.top,  Tube.back, Tube.back,
				Tube.top,  Tube.top,  Tube.top,
				Tube.left, Tube.front, Tube.top
		};
		//(R' F R U R'U') (F' U R)
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver41 extends Solver{
		private int[] typeDefs = {
				Tube.top,   Tube.back,  Tube.top,
				Tube.left,  Tube.top,   Tube.top,
				Tube.front, Tube.top, Tube.front,
		};
		//R U' R' U2 R U y R U' R' U' F'
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },		
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },		
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.front}, {Tube.CCW} },	
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver42 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.top,   Tube.back,
				Tube.left,  Tube.top,   Tube.top,
				Tube.top,   Tube.front, Tube.top,
		};
		//(R'U R U'U'R'U')(F'U)(F U R)
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CCW} },	
				{ {Tube.top}, {Tube.CW} },	
				{ {Tube.right}, {Tube.CW} },	
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.right}, {Tube.CCW} },	
				{ {Tube.top}, {Tube.CCW} },	
				{ {Tube.front}, {Tube.CCW} },	
				{ {Tube.top}, {Tube.CW} },	
				{ {Tube.front}, {Tube.CW} },	
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver43 extends Solver{
		private int[] typeDefs = {
				Tube.top,  Tube.back,  Tube.right,
				Tube.top,  Tube.top,   Tube.right,
				Tube.top,  Tube.top,   Tube.right,
		};
		// (B' U') (R' U R B)
		private int[][][] stepList = {
				{ {Tube.back}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.back}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver44 extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.back,  Tube.top,
				Tube.left,  Tube.top,   Tube.top,
				Tube.left,  Tube.top,   Tube.top,
		};
		//f (R U R' U')f'
		private int[][][] stepList = {
				{ {Tube.standing, Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.standing, Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver45 extends Solver{
		private int[] typeDefs = {
				Tube.left, Tube.back,  Tube.top,
				Tube.top,  Tube.top,   Tube.top,
				Tube.left, Tube.front, Tube.top,
		};
		//F (R U R' U') F'
		private int[][][] stepList = {
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver46 extends Solver{
		private int[] typeDefs = {
				Tube.top,  Tube.top,  Tube.right,
				Tube.left, Tube.top,  Tube.right,
				Tube.top,  Tube.top,  Tube.right,
		};
		//(R' U') R' F R F' (U R)
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver47 extends Solver{
		private int[] typeDefs = {
				Tube.left, Tube.back, Tube.back,
				Tube.top,  Tube.top,  Tube.right,
				Tube.left, Tube.top,  Tube.front,
		};
		//B'(R' U' R U)2 B
		private int[][][] stepList = {
				{ {Tube.back}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.back}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver48 extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.top,   Tube.back,
				Tube.top,   Tube.top,   Tube.right,
				Tube.left,  Tube.front, Tube.front,
		};
		//F (R U R' U')2 F'
		private int[][][] stepList = {
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver49 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.back, Tube.right,
				Tube.top,   Tube.top,  Tube.right,
				Tube.front, Tube.top, Tube.right,
		};
		//R B'(R2 F)(R2 B) R2 F' R
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.back}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.back}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver50 extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.back,  Tube.back,
				Tube.left,  Tube.top,   Tube.top,
				Tube.left,  Tube.top,   Tube.front,
		};
		// L'B (L2 F')(L2 B') L2 F L'
		private int[][][] stepList = {
				{ {Tube.left}, {Tube.CW} },
				{ {Tube.back}, {Tube.CCW} },
				{ {Tube.left}, {Tube.CCW} },
				{ {Tube.left}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.left}, {Tube.CCW} },
				{ {Tube.left}, {Tube.CCW} },
				{ {Tube.back}, {Tube.CW} },
				{ {Tube.left}, {Tube.CCW} },
				{ {Tube.left}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.left}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver51 extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.back,  Tube.back,
				Tube.top,   Tube.top,   Tube.top,
				Tube.left,  Tube.front, Tube.front,
		};
		//f (R U R' U')2 f'
		private int[][][] stepList = {
				{ {Tube.standing, Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.standing, Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver52 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.top,  Tube.right,
				Tube.left,  Tube.top,  Tube.right,
				Tube.front, Tube.top,  Tube.right,
		};
		// R'U' R U' R' d R' U l U x
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.middle, Tube.left}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.left, Tube.middle, Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver53 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.back,  Tube.back,
				Tube.top,   Tube.top,   Tube.right,
				Tube.front, Tube.top,   Tube.front,
		};
		// (r' U2) (R U R'U') (R U R'U) r
		private int[][][] stepList = {
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver54 extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.top,   Tube.back,
				Tube.top,   Tube.top,   Tube.right,
				Tube.front, Tube.front, Tube.front,
		};
		//(r U'U') (R' U' R U R' U') (R U' r')
		private int[][][] stepList = {
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver55 extends Solver{
		private int[] typeDefs = {
				Tube.left, Tube.top, Tube.right,
				Tube.left, Tube.top, Tube.right,
				Tube.left, Tube.top, Tube.right,
		};
		//(R U'U') (R'2 U') R U' R'U2 (F R F')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver56 extends Solver{
		private int[] typeDefs = {
				Tube.left, Tube.back,  Tube.right,
				Tube.top,  Tube.top,   Tube.top,
				Tube.left, Tube.front, Tube.right,
		};
		// F (R U R'U')(R F')(r U R'U')r'
		private int[][][] stepList = {
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver57 extends Solver{
		private int[] typeDefs = {
				Tube.top, Tube.back,  Tube.top,
				Tube.top, Tube.top,   Tube.top,
				Tube.top, Tube.front, Tube.top
		};
		//(R U R' U' r)(R' U) (R U' r')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver3R extends Solver{
		private int[] typeDefs = {
				Tube.back, Tube.back,  Tube.right,
				Tube.left, Tube.top,   Tube.right,
				Tube.top,  Tube.front, Tube.front,
		};
		//(r' R2 U R' U)(r U2 )(r' U r R')
		private int[][][] stepList = {
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.middle, Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.middle, Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver5R extends Solver{
		private int[] typeDefs = {
				Tube.top,  Tube.top,   Tube.right,
				Tube.top,  Tube.top,   Tube.right,
				Tube.left, Tube.front, Tube.front,
		};
		//(l' U2) (L U L' U l)
		private int[][][] stepList = {
				{ {Tube.middle, Tube.left}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.left}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.left}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.middle, Tube.left}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver6R extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.back,  Tube.back,
				Tube.top,   Tube.top,   Tube.right,
				Tube.top,   Tube.top,   Tube.right,
		};
		// l U2 L' U' L U' l'
		private int[][][] stepList = {
				{ {Tube.middle, Tube.left}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.left}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.left}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.middle, Tube.left}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver10R extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.top,   Tube.right,
				Tube.left,  Tube.top,   Tube.top,
				Tube.top,   Tube.front, Tube.front,
		};
		// (L U L') y' (R'F R U') (R' F ' R)
		private int[][][] stepList = {
				{ {Tube.left}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.left}, {Tube.CW} },	
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver12R extends Solver{
		private int[] typeDefs = {
				Tube.left,  Tube.back,  Tube.top,
				Tube.left,  Tube.top,   Tube.top,
				Tube.front, Tube.top, Tube.right,
		};
		// F (R U R' U') y L' F (R U R' U') F'
		private int[][][] stepList = {
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },
				{ {Tube.left}, {Tube.CW} },
				{ {Tube.front}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver13R extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.back,  Tube.top,
				Tube.top,   Tube.top,   Tube.top,
				Tube.left,  Tube.front, Tube.front,
		};
		//l U' R' F' R U l' y R' U R
		private int[][][] stepList = {
				{ {Tube.left, Tube.middle}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.front}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.left, Tube.middle}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver42R extends Solver{
		private int[] typeDefs = {
				Tube.top,   Tube.back,  Tube.top,
				Tube.top,   Tube.top,   Tube.right,
				Tube.front, Tube.top,   Tube.front,
		};
		// (r' R2)y (R U R' U') y' (R' U R')r
		private int[][][] stepList = {
				{ {Tube.right, Tube.middle}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.right, Tube.middle}, {Tube.CW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
	class Solver47R extends Solver{
		private int[] typeDefs = {
				Tube.back,  Tube.top,   Tube.right,
				Tube.left,  Tube.top,   Tube.top,
				Tube.front, Tube.front, Tube.right,
		};
		//b' U' (R' U R U' R' U R)b
		private int[][][] stepList = {
				{ {Tube.back, Tube.standing}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.top}, {Tube.CCW} },
				{ {Tube.right}, {Tube.CCW} },
				{ {Tube.top}, {Tube.CW} },
				{ {Tube.right}, {Tube.CW} },
				{ {Tube.back, Tube.standing}, {Tube.CCW} },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs);
		}
	}
}
