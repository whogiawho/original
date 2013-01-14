package oms.cj.tube.way2;

import java.util.Stack;

import android.util.Log;
import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;

public class Step4 {
	private final static String TAG = "Step4";
	private Tube mTube;
	
	public Step4(Tube t){
		mTube = t;
	}
	
	public Tube getTube(){
		return mTube;
	}
	
	private boolean isComplete(Tube t){
		Cube cubes[] = t.getCubes();
		boolean bComplete = true;
		int[][] checkTopList = {
				{6, 7, 8, Tube.back, 4},
				{8, 17, 26, Tube.right, 14},
				{24, 25, 26, Tube.front, 22},
				{6, 15, 24, Tube.left, 12},
		};
		for(int i=0;i<checkTopList.length;i++){
			int center = checkTopList[i][4];
			int face = checkTopList[i][3];
			Color c1 = cubes[center].getColor(face);
			int j=0;
			for(;j<checkTopList[i].length-2;j++){
				int idx = checkTopList[i][j];
				Color c2 = cubes[idx].getColor(face);
				if(!c1.equals(c2))
					break;
			}
			if(j!=checkTopList[i].length-2){
				bComplete = false;
				break;
			}
		}
		
		return bComplete;
	}
	
	public Stack<RotateAction> pll(){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		Log.i(TAG+".pll", "starting ... ");
		
		while(!isComplete(mTube)){    //should loop only once
			int i=0;
			for(;i<solverList.length;i++){
				Solver solver = solverList[i];
				int[] dSides = {Tube.bottom, Tube.equator};
				int j=0;
				for(;j<4;j++){
					//call the override method "getCount2Match(Tube t)"
					int count = solver.getCount2Match(mTube);	
					if(count!=-1){
						for(int k=0;k<j;k++)
							mTube.setRotate(dSides, Tube.CCW);
						Step1.moveSide(commands, dSides, j, mTube);
						Step1.moveSide(commands, Tube.top, count, mTube);
						break;
					} else {
						Log.i(TAG+".pll", "i="+i+"  j="+j);
						mTube.setRotate(dSides, Tube.CW);
					}
				}
				if(j!=4)
					break;
			}
			
			Log.i(TAG+".pll", "i="+i);
			if(i!=solverList.length)
				solverList[i].solve(commands, mTube);
			else {
				Log.i(TAG+".pll", "back center="+mTube.getVisibleFaceColor(Tube.back, 4).toString());
				Log.i(TAG+".pll", "right center="+mTube.getVisibleFaceColor(Tube.right, 4).toString());
				Log.i(TAG+".pll", "front center="+mTube.getVisibleFaceColor(Tube.front, 4).toString());
				Log.i(TAG+".pll", "left center="+mTube.getVisibleFaceColor(Tube.left, 4).toString());
				Step3.logSides(mTube, TAG, Step3.topSides);
				Log.e(TAG+".pll", "exception! loop more than once!");
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
			new Solver1R(),
			new Solver2R(),
			new Solver9R(),
			new Solver10R(),
			new Solver6R(),
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
		protected boolean match(Tube t, int[][][] typeDefs1, int[][][] typeDefs2){
			boolean bMatch = true;
			Cube[] cubes = t.getCubes();
			
			for(int i=0;i<typeDefs1.length;i++){
				int face = typeDefs1[i][1][0];
				int center = typeDefs1[i][1][1];
				Color c1 = cubes[center].getColor(face);
				int j=0;
				for(;j<typeDefs1[i][0].length;j++){
					int idx = typeDefs1[i][0][j];
					Color c2 = cubes[idx].getColor(face);
					if(!c1.equals(c2))
						break;
				}
				if(j!=typeDefs1[i][0].length){
					bMatch = false;
					break;
				}
			}
			if(!bMatch)
				return bMatch;
			int i=0;
			for(;i<typeDefs2.length;i++){
				int idx1 = typeDefs2[i][0][0];
				int f1 = typeDefs2[i][0][1];
				int idx2 = typeDefs2[i][1][0];
				int f2 = typeDefs2[i][1][1];
				Color c1 = cubes[idx1].getColor(f1);
				Color c2 = cubes[idx2].getColor(f2);
				if(!c1.equals(c2))
					break;
			}
			if(i!=typeDefs2.length){
				bMatch = false;
			}
			
			return bMatch;
		}
		// return values:
		// 0-3,   which means a match is found, and such number CW turns are done
		// -1,    which means a match is not found
		protected int getCount2Match(Tube t, int[][][] typeDefs1, int[][][] typeDefs2){
			int i=0;
			for(;i<4;i++){
				boolean bMatch = match(t, typeDefs1, typeDefs2);
				Log.i(TAG+".getCount2Match", "bMatch="+bMatch);
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
		private int[][][] typeDefs1 = {
				{ {6, 7, 8}, {Tube.back, 4} },
				{ {8, 26},   {Tube.right, 14} },
				{ {24, 26},  {Tube.front, 22} },
				{ {24, 6},   {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {17, Tube.right}, {12, Tube.left} },
				{ {15, Tube.left},  {22, Tube.front} },
				{ {25, Tube.front}, {14, Tube.right} },
		};
		//(R U ' R) U (R U R U') (R' U' R2)
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top},   {Tube.CCW}  },
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top},   {Tube.CW}  },

				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top},   {Tube.CW}  },	
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top},   {Tube.CCW}  },
				
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.top},   {Tube.CCW}  },	
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.right}, {Tube.CW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver2 extends Solver{
		private int[][][] typeDefs1 = {
				{ {6, 7, 8}, {Tube.back, 4} },
				{ {8, 26},   {Tube.right, 14} },
				{ {24, 26},  {Tube.front, 22} },
				{ {24, 6},   {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {17, Tube.right}, {22, Tube.front} },
				{ {15, Tube.left},  {14, Tube.right} },
				{ {25, Tube.front}, {12, Tube.left} },
		};
		//(R2' U)(R U R' U')(R' U')(R' U R')
		private int[][][] stepList = {
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.right}, {Tube.CCW}  },
				{ {Tube.top},   {Tube.CW}  },	
				
				{ {Tube.right}, {Tube.CW}  },
				{ {Tube.top},   {Tube.CW}  },	
				{ {Tube.right}, {Tube.CCW}  },	
				{ {Tube.top},   {Tube.CCW}  },	
				
				{ {Tube.right}, {Tube.CCW}  },	
				{ {Tube.top},   {Tube.CCW}  },	
				
				{ {Tube.right}, {Tube.CCW}  },	
				{ {Tube.top},   {Tube.CW}  },	
				{ {Tube.right}, {Tube.CCW}  },	
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver3 extends Solver{
		private int[][][] typeDefs1 = {
				{ {6,  8}, 	{Tube.back, 4} },
				{ {8,  26}, {Tube.right, 14} },
				{ {24, 26}, {Tube.front, 22} },
				{ {24, 6},  {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {7, Tube.back}, 	{22, Tube.front} },
				{ {25, Tube.front}, {4, Tube.back} },
				{ {15, Tube.left}, 	{14, Tube.right} },
				{ {17, Tube.right}, {12, Tube.left} },		
		};
		//M2 U M2 U2 M2 U M2
		private int[][][] stepList = {
				{ {Tube.middle}, {Tube.CCW}  },	
				{ {Tube.middle}, {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.middle}, {Tube.CCW}  },	
				{ {Tube.middle}, {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.middle}, {Tube.CCW}  },	
				{ {Tube.middle}, {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.middle}, {Tube.CCW}  },	
				{ {Tube.middle}, {Tube.CCW}  },	
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver4 extends Solver{
		private int[][][] typeDefs1 = {
				{ {6,  8}, 	{Tube.back, 4} },
				{ {8,  26}, {Tube.right, 14} },
				{ {24, 26}, {Tube.front, 22} },
				{ {24, 6},  {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {7, Tube.back}, 	{12, Tube.left} },
				{ {15, Tube.left}, 	{4, Tube.back} },
				{ {25, Tube.front}, {14, Tube.right} },
				{ {17, Tube.right}, {22, Tube.front} },	
		};
		//(U R'U')(R U'R) U (R U'R'U)(R U R2 U')(R'U)
		private int[][][] stepList = {
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				
				{ {Tube.top},    {Tube.CW}  },
				
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },	
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver5 extends Solver{
		private int[][][] typeDefs1 = {
				{ {6,  7}, 	{Tube.back, 4} },
				{ {17},     {Tube.right, 14} },
				{ {25},     {Tube.front, 22} },
				{ {15, 6},  {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {8, Tube.back}, 	{14, Tube.right} },
				{ {8, Tube.right},  {22, Tube.front} },
				{ {26, Tube.right}, {22, Tube.front} },
				{ {26, Tube.front}, {12, Tube.left} },	
				{ {24, Tube.front},	{4,  Tube.back} },
				{ {24, Tube.left},  {14, Tube.right} },				
		};
		//x' R2 D2(R' U' R)D2(R' U R')
		private int[][][] stepList = {
				{ {Tube.left, Tube.middle, Tube.right},  {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.bottom}, {Tube.CCW}  },	
				{ {Tube.bottom}, {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.bottom}, {Tube.CCW}  },	
				{ {Tube.bottom}, {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver6 extends Solver{
		private int[][][] typeDefs1 = {
				{ {6,  7}, 	{Tube.back, 4} },
				{ {17},     {Tube.right, 14} },
				{ {25},     {Tube.front, 22} },
				{ {15, 6},  {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {8, Tube.back}, 	{22, Tube.front} },
				{ {8, Tube.right},  {12, Tube.left} },
				{ {26, Tube.right}, {4,  Tube.back} },
				{ {26, Tube.front}, {14, Tube.right} },	
				{ {24, Tube.front},	{14, Tube.right} },
				{ {24, Tube.left},  {22,  Tube.front} },	
		};
		// x'(R U'R) D2 (R'U R) D2 R2
		private int[][][] stepList = {
				{ {Tube.left, Tube.middle, Tube.right},  {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.bottom}, {Tube.CCW}  },	
				{ {Tube.bottom}, {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.bottom}, {Tube.CCW}  },	
				{ {Tube.bottom}, {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver7 extends Solver{
		private int[][][] typeDefs1 = {
				{ {7}, 	{Tube.back, 4} },
				{ {17}, {Tube.right, 14} },
				{ {25}, {Tube.front, 22} },
				{ {15}, {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {8, Tube.back}, 	{14, Tube.right} },
				{ {8, Tube.right},  {22, Tube.front} },
				{ {26, Tube.right}, {4,  Tube.back} },
				{ {26, Tube.front}, {14, Tube.right} },	
				{ {24, Tube.front}, {12, Tube.left} },
				{ {24, Tube.left},  {4,  Tube.back} },
				{ {6, Tube.left}, 	{22, Tube.front} },
				{ {6, Tube.back},   {12, Tube.left} },	
		};
		//x'(R U' R') D (R U R')u2'(R' U R)D(R' U' R)
		private int[][][] stepList = {
				{ {Tube.left, Tube.middle, Tube.right},  {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.bottom}, {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.equator, Tube.top}, {Tube.CCW} },
				{ {Tube.equator, Tube.top}, {Tube.CCW} },
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.bottom}, {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver8 extends Solver{
		private int[][][] typeDefs1 = {
				{ {6,  7}, 	{Tube.back, 4} },
				{ {24, 25}, {Tube.front, 22} },
				{ {24, 6},  {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {8, Tube.back}, 	{14, Tube.right} },
				{ {8, Tube.right},  {22, Tube.front} },
				{ {26, Tube.right}, {4,  Tube.back} },
				{ {26, Tube.front}, {14, Tube.right} },	
				{ {17, Tube.right}, {12, Tube.left} },
				{ {15, Tube.left},  {14, Tube.right} },
		};
		//(R U R' U')(R' F)(R2 U' R' U')(R U R' F')
		private int[][][] stepList = {
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.front},  {Tube.CW}  },	
				
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },
				
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.front},  {Tube.CCW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver9 extends Solver{
		private int[][][] typeDefs1 = {
				{ {6}, 	        {Tube.back,  4} },
				{ {17},         {Tube.right, 14} },
				{ {24},         {Tube.front, 22} },
				{ {24, 15, 6},  {Tube.left,  12} },
		};
		private int[][][] typeDefs2 = {
				{ {8, Tube.back}, 	{14, Tube.right} },
				{ {8, Tube.right},  {22, Tube.front} },
				{ {26, Tube.right}, {4,  Tube.back} },
				{ {26, Tube.front}, {14, Tube.right} },	
				{ {7,  Tube.back},  {22, Tube.front} },
				{ {25, Tube.front}, {4,  Tube.back} },
		};
		// U'(R'U R U' R'2 b')x(R'U R)y'(R U R' U' R2)
		private int[][][] stepList = {
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.standing, Tube.back},  {Tube.CW}  },	
				{ {Tube.left, Tube.middle, Tube.right},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top, Tube.equator, Tube.bottom},  {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver10 extends Solver{
		private int[][][] typeDefs1 = {
				{ {8}, 	        {Tube.back,  4} },
				{ {8},          {Tube.right, 14} },
				{ {24, 25},     {Tube.front, 22} },
				{ {24, 15},     {Tube.left,  12} },
		};
		private int[][][] typeDefs2 = {
				{ {6, Tube.back}, 	{22, Tube.front} },
				{ {6, Tube.left},   {14, Tube.right} },
				{ {26, Tube.right}, {12, Tube.left} },
				{ {26, Tube.front}, {4,  Tube.back} },	
				{ {7,  Tube.back},  {14, Tube.right} },
				{ {17, Tube.right}, {4,  Tube.back} },
		};
		//(R' U R' U')yx2(R' U R' U'R2)xz'(R'U'R U R)
		private int[][][] stepList = {
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.top, Tube.equator, Tube.bottom},  {Tube.CW}  },
				{ {Tube.left, Tube.middle, Tube.right},  {Tube.CW}  },	
				{ {Tube.left, Tube.middle, Tube.right},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.left, Tube.middle, Tube.right},  {Tube.CW}  },	
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },	
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver11 extends Solver{
		private int[][][] typeDefs1 = {
				{ {8,  17},     {Tube.right, 14} },
				{ {24, 25},     {Tube.front, 22} },
		};
		private int[][][] typeDefs2 = {
				{ {6, Tube.back}, 	{22, Tube.front} },
				{ {6, Tube.left},   {14, Tube.right} },
				{ {26, Tube.right}, {12, Tube.left} },
				{ {26, Tube.front}, {4,  Tube.back} },	
				{ {7,  Tube.back},  {12, Tube.left} },
				{ {15, Tube.left},  {4,  Tube.back} },
		};
		//F(R U'R' U')(R U R' F')(R U R' U')(R' F R F')
		private int[][][] stepList = {
				{ {Tube.front},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.front},  {Tube.CCW}  },	
				
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.front},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.front},  {Tube.CCW}  },	
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver12 extends Solver{
		private int[][][] typeDefs1 = {
				{ {6, 7, 8},    {Tube.back, 4} },
				{ {8},          {Tube.right, 14} },
				{ {6, 15},      {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {24, Tube.front}, {14, Tube.right} },
				{ {24, Tube.left},  {22, Tube.front} },
				{ {26, Tube.front}, {12, Tube.left} },
				{ {26, Tube.right}, {22, Tube.front} },
				{ {25, Tube.front}, {14, Tube.right} },
				{ {17, Tube.right}, {22, Tube.front} },	
		};
		//z(U' R D')(R2 U R' U' R2 U) D R'
		private int[][][] stepList = {
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.bottom}, {Tube.CW}  },	
				
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				
				{ {Tube.bottom}, {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver13 extends Solver{
		private int[][][] typeDefs1 = {
				{ {6, 7},           {Tube.back, 4} },
				{ {24},             {Tube.front, 22} },
				{ {6, 15, 24},      {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {8, Tube.back}, 	{14, Tube.right} },
				{ {8, Tube.right},  {22, Tube.front} },
				{ {26, Tube.right}, {4,  Tube.back} },
				{ {26, Tube.front}, {14, Tube.right} },	
				{ {25, Tube.front}, {14, Tube.right} },
				{ {17, Tube.right}, {22, Tube.front} },	
		};
		//(R U R'F')(R U R'U')(R'F R2 U'R'U')
		private int[][][] stepList = {
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.front},  {Tube.CCW}  },	
				
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.front},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver14 extends Solver{
		private int[][][] typeDefs1 = {
				{ {7},              {Tube.back, 4} },
				{ {26},             {Tube.right, 14} },
				{ {24, 26},         {Tube.front, 22} },
				{ {15, 24},         {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {6, Tube.back},   {14, Tube.right} },
				{ {6, Tube.left},   {4, Tube.back} },	
				{ {8, Tube.back},   {12, Tube.left} },
				{ {8, Tube.right},  {4, Tube.back} },	
				{ {25, Tube.front}, {14, Tube.right} },
				{ {17, Tube.right}, {22, Tube.front} },	
		};
		//(R' U2)(R U'U')(R' F R U R' U')(R'F' R2 U')
		private int[][][] stepList = {
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.front},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.front},  {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver15 extends Solver{
		private int[][][] typeDefs1 = {
				{ {6, 8},           {Tube.back, 4} },
				{ {8},              {Tube.right, 14} },
				{ {25},             {Tube.front, 22} },
				{ {6, 15},          {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {24, Tube.front}, {14, Tube.right} },
				{ {24, Tube.left},  {22, Tube.front} },
				{ {26, Tube.front}, {12, Tube.left} },
				{ {26, Tube.right}, {22, Tube.front} },
				{ {7,  Tube.back},  {14, Tube.right} },
				{ {17, Tube.right}, {4, Tube.back} },
		};
		//(R U'U')(R' U2)(R B' R' U')(R U R B R2' U)
		private int[][][] stepList = {
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.back},   {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },		
				
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.back},   {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },	
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver16 extends Solver{
		private int[][][] typeDefs1 = {
				{ {7, 8},       {Tube.back, 4} },
				{ {8},          {Tube.right, 14} },
		};
		private int[][][] typeDefs2 = {
				{ {17, Tube.right}, {22, Tube.front} },
				{ {15, Tube.left},  {14, Tube.right} },
				{ {25, Tube.front}, {12, Tube.left} },
				{ {6, Tube.back},   {12, Tube.left} },
				{ {6, Tube.left},   {22, Tube.front} },
				{ {24, Tube.left},  {22, Tube.front} },
				{ {24, Tube.front}, {14, Tube.right} },
				{ {26, Tube.front}, {4, Tube.back} },
				{ {26, Tube.right}, {12, Tube.left} },
		};
		//(R2' u' R U' R)(U R' u)(R2 B U'B')
		private int[][][] stepList = {
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top, Tube.equator},  {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top, Tube.equator},  {Tube.CW}  },	
				
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.back},   {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CCW}  },	
				{ {Tube.back},   {Tube.CW}  },	
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver17 extends Solver{
		private int[][][] typeDefs1 = {
				{ {17, 26},     {Tube.right, 14} },
				{ {26},         {Tube.front, 22} },
		};
		private int[][][] typeDefs2 = {
				{ {7,  Tube.back},  {12, Tube.left} },
				{ {15, Tube.left},  {22, Tube.front} },
				{ {25, Tube.front}, {4, Tube.back} },
				{ {6, Tube.left},   {4, Tube.back} },
				{ {6, Tube.back},   {14, Tube.right} },
				{ {8, Tube.back},   {22, Tube.front} },
				{ {8, Tube.right},  {12, Tube.left} },
				{ {24, Tube.front}, {12, Tube.left} },
				{ {24, Tube.left},  {4,  Tube.back} },
		};
		//(R U R')y'(R2' u' R U')(R' U R' u R2)
		private int[][][] stepList = {
				{ {Tube.right},  {Tube.CW}  },	
				{ {Tube.top},    {Tube.CW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.bottom, Tube.top, Tube.equator},  {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top, Tube.equator},  {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },	
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top, Tube.equator},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver18 extends Solver{
		private int[][][] typeDefs1 = {
				{ {26},     	{Tube.right, 14} },
				{ {25, 26},     {Tube.front, 22} },
		};
		private int[][][] typeDefs2 = {
				{ {7,  Tube.back},  {12, Tube.left} },
				{ {15, Tube.left},  {14, Tube.right} },
				{ {17, Tube.right}, {4, Tube.back} },
				{ {6, Tube.left},   {4, Tube.back} },
				{ {6, Tube.back},   {14, Tube.right} },
				{ {8, Tube.back},   {22, Tube.front} },
				{ {8, Tube.right},  {12, Tube.left} },
				{ {24, Tube.front}, {12, Tube.left} },
				{ {24, Tube.left},  {4,  Tube.back} },
		};
		//(R2 u R')(U R' U' R u')(R2' F ' U F)
		private int[][][] stepList = {
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top, Tube.equator},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top, Tube.equator},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.front},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.front},  {Tube.CW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver19 extends Solver{
		private int[][][] typeDefs1 = {
				{ {8},     		{Tube.back, 4} },
				{ {8, 17},     	{Tube.right, 14} },
		};
		private int[][][] typeDefs2 = {
				{ {7,  Tube.back},  {22, Tube.front} },
				{ {15, Tube.left},  {4, Tube.back} },
				{ {25, Tube.front}, {12, Tube.left} },
				{ {6, Tube.back},   {12, Tube.left} },
				{ {6, Tube.left},   {22, Tube.front} },
				{ {24, Tube.left},  {22, Tube.front} },
				{ {24, Tube.front}, {14, Tube.right} },
				{ {26, Tube.front}, {4, Tube.back} },
				{ {26, Tube.right}, {12, Tube.left} },
		};
		//(R' d' F)(R2 u)(R' U)(R U' R u' R2)
		private int[][][] stepList = {
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.equator, Tube.bottom}, {Tube.CW} },
				{ {Tube.front},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.equator, Tube.top}, {Tube.CW} },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.equator, Tube.top}, {Tube.CCW} },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver20 extends Solver{
		private int[][][] typeDefs1 = {
				{ {8},     		{Tube.back, 4} },
				{ {8, 17},     	{Tube.right, 14} },
				{ {24},         {Tube.front, 22} },
				{ {24, 15},     {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {7,  Tube.back},  {22, Tube.front} },
				{ {25, Tube.front}, {4,  Tube.back} },
				{ {6, Tube.back}, 	{22, Tube.front} },
				{ {6, Tube.left},   {14, Tube.right} },
				{ {26, Tube.right}, {12, Tube.left} },
				{ {26, Tube.front}, {4,  Tube.back} },	
		};
		//z(R' U R')z'(R U2 L' U R')z(U R')z'(R U2 L' U R')
		private int[][][] stepList = {
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.left},   {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.left},   {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver21 extends Solver{
		private int[][][] typeDefs1 = {
				{ {6},     		{Tube.back, 4} },
				{ {26, 17},     {Tube.right, 14} },
				{ {26},         {Tube.front, 22} },
				{ {6, 15},      {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {7,  Tube.back},  {22, Tube.front} },
				{ {25, Tube.front}, {4,  Tube.back} },
				{ {8, Tube.back}, 	{22, Tube.front} },
				{ {8, Tube.right},  {12, Tube.left} },
				{ {24, Tube.left},  {14, Tube.right} },
				{ {24, Tube.front}, {4,  Tube.back} },
		};
		//z(U'R D')(R2 U R'U')z'(R U R')z(R2 U R')z'(R U')
		private int[][][] stepList = {
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.bottom}, {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CCW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver1R extends Solver{
		private int[][][] typeDefs1 = {
				{ {6, 8},        {Tube.back, 4} },
				{ {8, 26},       {Tube.right, 14} },
				{ {24, 25, 26},  {Tube.front, 22} },
				{ {24, 6},       {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {7,  Tube.back},  {12, Tube.left} },
				{ {15, Tube.left},  {14, Tube.right} },
				{ {17, Tube.right}, {4, Tube.back} },
		};
		//(R2 U ')(R' U' R U R U)(R U 'R)
		private int[][][] stepList = {
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver2R extends Solver{
		private int[][][] typeDefs1 = {
				{ {6, 8},        {Tube.back, 4} },
				{ {8, 26},       {Tube.right, 14} },
				{ {24, 25, 26},  {Tube.front, 22} },
				{ {24, 6},       {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {7,  Tube.back},  {14, Tube.right} },
				{ {15, Tube.left},  {4, Tube.back} },
				{ {17, Tube.right}, {12, Tube.left} },
		};
		//(R' U R' U')(R' U')(R' U)(R U R'2)
		private int[][][] stepList = {
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver9R extends Solver{
		private int[][][] typeDefs1 = {
				{ {8}, 	        {Tube.back,  4} },
				{ {8, 17, 26},  {Tube.right, 14} },
				{ {26},         {Tube.front, 22} },
				{ {15},         {Tube.left,  12} },
		};
		private int[][][] typeDefs2 = {
				{ {7,  Tube.back},  {22, Tube.front} },
				{ {25, Tube.front}, {4,  Tube.back} },
				{ {24, Tube.front}, {12, Tube.left} },
				{ {24, Tube.left},  {4,  Tube.back} },
				{ {6, Tube.left}, 	{22, Tube.front} },
				{ {6, Tube.back},   {12, Tube.left} },	
		};
		//z(R U R' U' R U ' U')(x' z')(R U R'U') x(U'R'U R U 'U')
		private int[][][] stepList = {
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.left, Tube.middle, Tube.right},  {Tube.CCW}  },
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.left, Tube.middle, Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.top},    {Tube.CCW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver10R extends Solver{
		private int[][][] typeDefs1 = {
				{ {7, 8}, 	    {Tube.back,  4} },
				{ {8, 17},      {Tube.right, 14} },
				{ {24},         {Tube.front, 22} },
				{ {24},         {Tube.left,  12} },
		};
		private int[][][] typeDefs2 = {
				{ {6, Tube.back}, 	{22, Tube.front} },
				{ {6, Tube.left},   {14, Tube.right} },
				{ {26, Tube.right}, {12, Tube.left} },
				{ {26, Tube.front}, {4,  Tube.back} },	
				{ {25, Tube.front}, {12, Tube.left} },
				{ {15, Tube.left},  {22, Tube.front} },
		};
		//z(U' R U' l')z(R' U R'U')(l R) (U' R' U R U)
		private int[][][] stepList = {
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.left, Tube.middle},    {Tube.CW}  },
				{ {Tube.back, Tube.standing, Tube.front},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.left, Tube.middle},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
	class Solver6R extends Solver{
		private int[][][] typeDefs1 = {
				{ {7}, 	    {Tube.back, 4} },
				{ {17, 26}, {Tube.right, 14} },
				{ {25, 26}, {Tube.front, 22} },
				{ {15},     {Tube.left, 12} },
		};
		private int[][][] typeDefs2 = {
				{ {8, Tube.back}, 	{12, Tube.left} },
				{ {8, Tube.right},  {4,  Tube.back} },
				{ {6, Tube.left}, 	{22, Tube.front} },
				{ {6, Tube.back},   {12, Tube.left} },	
				{ {24, Tube.front},	{4,  Tube.back} },
				{ {24, Tube.left},  {14, Tube.right} },	
		};
		//x'(R U' R)z'(R'2 U' L U R2' x y R2)
		private int[][][] stepList = {
				{ {Tube.left, Tube.middle, Tube.right},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.back, Tube.standing, Tube.front},    {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.top},    {Tube.CCW}  },
				{ {Tube.left},   {Tube.CCW}  },
				{ {Tube.top},    {Tube.CW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.right},  {Tube.CCW}  },
				{ {Tube.left, Tube.middle, Tube.right},    {Tube.CW}  },
				{ {Tube.top, Tube.equator, Tube.bottom},   {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
				{ {Tube.right},  {Tube.CW}  },
		};
		
		@Override
		public void solve(Stack<RotateAction> commands, Tube t) {
			solve(commands, t, stepList);
		}
		@Override
		protected int getCount2Match(Tube t) {
			return getCount2Match(t, typeDefs1, typeDefs2);
		}
	}
}
