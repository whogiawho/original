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
package oms.cj.tube.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;
import javax.microedition.khronos.opengles.GL10;
import oms.cj.tube.ITubeRenderCallbacks;
import oms.cj.tube.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

//a tube is defined with below numbering
//back                  standing                  front    
//  6  7  8               15  16  17                24  25  26 
//  3  4  5               12  13  14                21  22  23
//  0  1  2               9   10  11                18  19  20

public class Tube extends Face{
	private final static int NORMAL=0;
	private final static int ANIMATIONON=1; 
	public final static int AnimationMaxLength=1000;			//by milli seconds
	public final static int AnimationDefaultLength=500;
	public final static int randomRotateNdefault=50;
	public final static int DefaultSize=22;
	public final static int MaxSize = 24;
	public final static int MinSize = 1;
	public final static float DefaultHalfLength=0.47f;
	public final static int CubesEachTube = 27;
	public final static int SidesEachTube = 6;
	public final static int CubesEachSide = 9;
	public final static int left = 0;
	public final static int right = 1;
	public final static int bottom = 2;
	public final static int top = 3;
	public final static int back = 4;
	public final static int front = 5;
	public final static int middle = 6;
	public final static int equator = 7;
	public final static int standing = 8; 
	private final static String TAG = "Tube";
	private final static int[][] axisCoords={{1,0,0}, {0,1,0}, {0,0,1}};
	public final static int[] LAYERAXIS = {
		Cube.axisX, Cube.axisX,	//left, right
		Cube.axisY, Cube.axisY,	//bottom, top
		Cube.axisZ, Cube.axisZ,	//back, front
		Cube.axisX, 			//middle
		Cube.axisY, 			//equator
		Cube.axisZ, 			//standing
	};
	public final static String[] AXISSTR = {
		"x", "y", "z",
	};
	public final static int LAYERSTRTYPE_CAP = 0;
	public final static int LAYERSTRTYPE_LOW = 1;
	public final static String[][] LAYERSTR_S = {	//LAYERSTR in Simple format
		{ "L", "l" },
		{ "R", "r" },
		{ "D", "d" },
		{ "U", "u" },
		{ "B", "b" },
		{ "F", "f" },
		{ "M", "m" },
		{ "E", "e" },
		{ "S", "s" },
	};
	public final static String[] LAYERSTR_F = {		//LAYERSTR in Full format
		"left",
		"right",
		"bottom",
		"top",
		"back",
		"front",
		"middle",
		"equator",
		"standing"
	};
	public final static int CCW=0;
	public final static int CW=1;

	// 0 left      red
	// 1 right     orange
	// 2 bottom    white
	// 3 top       yellow
	// 4 back      blue
	// 5 front     green
	public final static Color[] faceColor = {	
		Color.red, Color.orange,
		Color.white, Color.yellow,
		Color.blue, Color.green,
	};
	private final static int magicdim=3;
	private final static int base[]={1, 3, 9};
	private final static int[] standardorigin={1,1,1};
	//cubebelongtolayers【i】【j】意为第i个cube绕j轴的layers
	//i为cube编号，
	//j为轴编号，x轴=0；y轴=1；z轴=2，对应于magiccube.axisX，magiccube.axisY和magiccube.axisZ
	private final int[][] cubebelongtolayers=new int[CubesEachTube][3];
	
	//mLayers【i】【j】意为第i个layer中第j个格子的cube
	//i为layer编号，j为layer中格子编号
	private final int[][] mLayers = new int [9][9];
	
	//visiblefaces【i】【j】意为第i个cube的第j个可见face
	//i为cube编号
	//j为第i个cube的可见face编号
	private final static int[][] visiblefaces ={
			{left, bottom, back},		//0
			{bottom, back},				//1
			{right, bottom, back},		//2
			{left, back},				//3
			{back},						//4
			{right, back},				//5
			{left, top, back},			//6
			{top, back},				//7
			{right, top, back},			//8
			{left, bottom},				//9
			{bottom},					//10
			{right, bottom},			//11
			{left},						//12
			{},							//13
			{right},					//14
			{left, top},				//15
			{top},						//16
			{right, top},				//17
			{left, bottom, front},		//18
			{bottom, front},			//19
			{right, bottom, front},		//20
			{left, front},				//21
			{front},					//22
			{right, front},				//23
			{left, top, front},			//24
			{top, front},				//25
			{right, top, front}			//26
	};
	
	//movingcube【i】【j】描述了各sides的逆(0)顺(1)时针运动规律
	public final static int movingcubes[][][] = new int[][][] {
		{ 	{6, 3, 0, 7, 4, 1, 8, 5, 2},
			{2, 5, 8, 1, 4, 7, 0, 3, 6}
		}, //left
		
		{ 	{6, 3, 0, 7, 4, 1, 8, 5, 2},
			{2, 5, 8, 1, 4, 7, 0, 3, 6}
		}, //right
		
		{	{2, 5, 8, 1, 4, 7, 0, 3, 6},
			{6, 3, 0, 7, 4, 1, 8, 5, 2}
		}, //bottom
		
		{	{2, 5, 8, 1, 4, 7, 0, 3, 6},
			{6, 3, 0, 7, 4, 1, 8, 5, 2}
		}, //top
		
		{ 	{6, 3, 0, 7, 4, 1, 8, 5, 2},
			{2, 5, 8, 1, 4, 7, 0, 3, 6}
		}, //back
		
		{ 	{6, 3, 0, 7, 4, 1, 8, 5, 2},
			{2, 5, 8, 1, 4, 7, 0, 3, 6}
		}, //front
		
		{ 	{6, 3, 0, 7, 4, 1, 8, 5, 2},
			{2, 5, 8, 1, 4, 7, 0, 3, 6}
		}, //middle
		
		{	{2, 5, 8, 1, 4, 7, 0, 3, 6},
			{6, 3, 0, 7, 4, 1, 8, 5, 2}
		}, //equator
		
		{ 	{6, 3, 0, 7, 4, 1, 8, 5, 2},
			{2, 5, 8, 1, 4, 7, 0, 3, 6}
		}, //standing		
	};
	
    public static int[] idxsOfCrossCube = {
		1, 3, 5, 7
    };
    public static int[] idxsOfCornerCube = {
		0, 2, 6, 8
    };
	
	private int animationLength=AnimationMaxLength - AnimationDefaultLength;   				
	private int randomRotateN=randomRotateNdefault;		//随机旋转圈数
	private int size=DefaultSize;
	public static int size2distance(int size){
		return 31-size;
	}
	public void setAnimationLength(int length){
		animationLength = length;
	}
	public void setRandomRotateN(int rN){
		randomRotateN = rN;
	}
	public void setSize(int s){
		size = s;
	}
	public int getSize(){
		return size;
	}
	//cube被编号为0-26
	//1. z->y->x,    z为最高位，x为最低位
	//2. 各轴value follow"先小后大"的原则
	//3. 该轴满后向高位前进
	private Cube[] cubes = new Cube[CubesEachTube];
	private ArrayList<RotateAction> mAnimationQueue = new ArrayList<RotateAction>();
	private int mMode=NORMAL;
	private int getMode(){
		return mMode;
	}
	private void setMode(int mode){
		mMode = mode;
	}
	// below variables are only effective when mode==ANIMATIONON
		private RotateAction mRotateAction;
		private long animationStartTime;


	public Cube[] getCubes(){
		return cubes;
	}
	
	public static boolean isVisibleFace(int cube, int face){
		boolean isVisible = false;
		
		for(int i=0;i<visiblefaces[cube].length;i++){
			if(visiblefaces[cube][i]==face){
				isVisible=true;
				break;
			}
		}
		
		return isVisible;
	}
	public static int[] getVisibleFaces(int idxcube){
		return visiblefaces[idxcube]; 
	}
	public String toString(){
		String cubes = "";
		
		for(int i=0;i<Tube.SidesEachTube;i++){
			String sideStr = Tube.LAYERSTR_F[i] + ":";

			for(int j=0;j<Tube.CubesEachSide;j++){
				Color c = getVisibleFaceColor(i, j);
				sideStr = sideStr + c.toString() + ", ";
			}
			cubes = cubes + sideStr + "\n";
		}
		return cubes;
	}
	public int getCubeBelongToLayerBy(int whichcube, int whichaxis){
		return cubebelongtolayers[whichcube][whichaxis];
	}
	
	public void clearRequestQueue(){
		mAnimationQueue.clear();
		setMode(NORMAL);
	}
	public void enqueueRotateRequest(RotateAction action){
		mAnimationQueue.add(action);
		if(getMode()!=ANIMATIONON){
			RotateAction rAction = mAnimationQueue.remove(0);
			markRotate(rAction);
		}
	}
	private void markRotate(RotateAction r){
		if(mCallbacks!=null)
			mCallbacks.onRotateStart(r);
		setMode(ANIMATIONON);
		mRotateAction = r;
		animationStartTime = System.currentTimeMillis(); 
		Log.i(TAG, "markRotate(...): " + "animationStartTime=" + animationStartTime);		
	}
	
	public void randomize_old(Stack<RotateAction> actions){
		Random rLayer = new Random(), rdir=new Random();
		
		for(int i=0;i<randomRotateN;i++){
			int[] layers={rLayer.nextInt(9)};
			int dir=rdir.nextInt(2);
			setRotate(layers, dir);
			if(actions!=null)
				actions.push(new RotateAction(layers, dir));
		}
	}
	
	public void randomize(Stack<RotateAction> actions){
		Random rLayer = new Random(), rdir=new Random();
		RotateAction previous = null;
		
		int i=0;
		while(i<randomRotateN){
			int[] layers={rLayer.nextInt(9)};
			int dir=rdir.nextInt(2);
			RotateAction randomThis = new RotateAction(layers, dir);
			RotateAction previousR=null;
			if(previous!=null)
				previousR = previous.reverse();
			if(RotateAction.equal(randomThis, previousR))
				continue;
			else {
				previous = randomThis;
				setRotate(randomThis);
				if(actions!=null)
					actions.push(randomThis);			
				i++;
			}
		}
	}
	
	public void setRotate(RotateAction r){
		int angle = r.getAngle();
		if(angle%90!=0)
			Log.e(TAG+".setRotate", "invalid angle = " + angle);
		
		int times = angle/90;
		for(int i=0;i<times;i++)
			setRotate(r.getLayer(), r.getDir());
	}
	//rotate 90
	public void setRotate(int[] layers, int direction){
		for(int i=0;i<layers.length;i++)
			setRotate(layers[i], direction);
	}
	//rotate 90
	public void setRotate(int layer, int direction){
		int[] oldidxoncube = new int[9];
		int[] newidxoncube = new int[9];
		Color[][] colors = new Color[9][];
		int[][][] textureID = new int[9][][];
		
		for(int i=0;i<9;i++){
			oldidxoncube[i] = mLayers[layer][i];
			int newidxonface = movingcubes[layer][direction][i];
			newidxoncube[i] = mLayers[layer][newidxonface];			
//			Log.i(TAG, "oldidxoncube=" + oldidxoncube[i]);
//			Log.i(TAG, "newidxoncube=" + newidxoncube[i]);
			
			colors[i] = cubes[newidxoncube[i]].getColorOfRotate90(LAYERAXIS[layer], direction);  //newcubes【i】做90度转动
			textureID[i] = cubes[newidxoncube[i]].getTextureIDOfRotate90(LAYERAXIS[layer], direction);
		}

		for(int i=0;i<9;i++){
			for(int j=0;j<colors[i].length;j++){
//				Log.i(TAG, "color[j]=" + colors[j].toString());
				cubes[oldidxoncube[i]].setColor(j, colors[i][j]);
				cubes[oldidxoncube[i]].setTextureID(j, textureID[i][j]);
			}
		}
	}

	private int axisOfLayer(int layer){
		int axis;
		
		if(layer == left || layer == right || layer == middle){
			axis = Cube.axisX;
		} else if(layer == bottom || layer == top || layer == equator){
			axis = Cube.axisY;
		} else {
			axis = Cube.axisZ;
		}
		return axis;
	}
		
	private void initCubeBelongToLayers(){
		for(int i=0;i<mLayers.length;i++){
			int whichaxis = axisOfLayer(i);
			for(int j=0;j<mLayers[i].length;j++){
				int whichcube = mLayers[i][j];
				cubebelongtolayers[whichcube][whichaxis]=i;
			}
		}
/*		
		for(int i=0;i<27;i++){
			for(int j=0;j<3;j++){
				Log.i(TAG, "("+i+","+j+")="+cubebelongtolayers[i][j]);
			}
		}
*/		
	}
	
	public int getSideTopCube(int whichSide){
		int whichCube = -1;
		
		switch(whichSide){
		case Tube.front:
		case Tube.back:
			whichCube = getCube(whichSide, 7);
			break;
		case Tube.left:
		case Tube.right:
			whichCube = getCube(whichSide, 5);
			break;
		default:
			Log.e(TAG+".getSideTopCube(.)", "exception! side = " + LAYERSTR_F[whichSide]);
			break;
		}
		
		return whichCube;
	}
	
	public int getTargetCube(int srcCube, int[] action){
		return getTargetCube(srcCube, action, 1);
	}
	public int getTargetCube(int[] pos, int[] action){
		return getTargetCube(pos, action, 1);	
	}
	public int getTargetCube(int srcCube, int[] action, int count){
		int targetCube=0;
		
		int origSideIdx = getCubeSideIdx(srcCube, action[0]);
		int targetSideIdx = getTargetSideIdx(origSideIdx, action, count);
		targetCube=mLayers[action[0]][targetSideIdx];
		
		return targetCube;
	}
	public int getTargetCube(int[] pos, int[] action, int count){
		int targetCube=0;
		
		int whichCube = mLayers[pos[0]][pos[1]];
		int origSideIdx = getCubeSideIdx(whichCube, action[0]);
		int targetSideIdx = getTargetSideIdx(origSideIdx, action, count);
		targetCube=mLayers[action[0]][targetSideIdx];
		
		return targetCube;
	}
	
	public int getTargetSideIdx(int origSideIdx, int[] action, int count){
		int targetSideIdx=-1;
		
		for(int i=0;i<count;i++){
			targetSideIdx = getTargetSideIdx(origSideIdx, action);
			origSideIdx = targetSideIdx;
		}
		return targetSideIdx;
	}
	public int getTargetSideIdx(int origSideIdx, int[] action){
		int targetSideIdx=-1;
		
		int[] m = Tube.movingcubes[action[0]][action[1]];
		for(int i=0;i<Tube.CubesEachSide;i++)
			if(m[i]==origSideIdx){
				targetSideIdx = i;
				break;
			}
		
		return targetSideIdx;
	}
	public int getTargetSideIdx(int origSideIdx, RotateAction r){
		int[] action = {
				r.getLayer()[0], r.getDir() 
		};
		int targetSideIdx=getTargetSideIdx(origSideIdx, action);
		
		return targetSideIdx;
	}
	public int getCube(int layer, int idx){
		return mLayers[layer][idx];
	}
	public int getCube(int[] sides){
		int idx = -1;
		
		if(sides==null||sides.length>3)
			return idx;
		
		for(int i=0;i<visiblefaces.length;i++){
			int[] faces = visiblefaces[i];
			if(faces.length!=sides.length)
				continue;
			if(Arrays.equals(sides, faces)){
				idx = i;
				break;
			}
		}
		
		return idx;
	}
		
	//given a cube's idx(0-26), and one of its side
	//this function returns its layer idx of this side
	//(cube, side) --> layer idx of side
	public int getCubeSideIdx(int cube, int side){
		int layerIdxOfSide=-1;
		
		for(int i=0;i<CubesEachSide;i++){
			if(mLayers[side][i]==cube){
				layerIdxOfSide = i;
				break;
			}	
		}
		return layerIdxOfSide;
	}
	//Note:
	//1. define the mapping relationship 
	//              Input               Output      
	//   mapping    layer idx of side   cube idx 
	//   range      (0,8)               (0,26)
	//2. layer idx of side is also utilized in movingcubes[][][]
	// left:     24 15 6    8 5 2
	//           21 12 3    7 4 1
	//           18 9  0    6 3 0
	
	// right:    26 17 8    8 5 2
	//           23 14 5    7 4 1
	//           20 11 2    6 3 0
	
	// bottom:   0  1  2    0 1 2
	//           9  10 11   3 4 5
	//           18 19 20   6 7 8
	
	// top:      6  7  8    0 1 2
	//           15 16 17   3 4 5
	//           24 25 26   6 7 8
	
	// back:     6  7  8    6 7 8
	//           3  4  5    3 4 5
	//           0  1  2    0 1 2
	
	// front:    24 25 26   6 7 8
	//           21 22 23   3 4 5
	//           18 19 20   0 1 2
	
	// middle:   25 16 7    8 5 2
	//           22 13 4    7 4 1
	//           19 10 1    6 3 0
	
	// equator:  3  4  5    0 1 2
	//           12 13 14   3 4 5
	//           18 19 20   6 7 8
	
	// standing: 15 16 17   6 7 8
	//           12 13 14   3 4 5
	//           9  10 11   0 1 2
	
	private void initLayers(){
		for(int i=0;i<CubesEachSide;i++){
			mLayers[Tube.left][i]=3*i;
			mLayers[Tube.right][i]=3*i+2;
			mLayers[Tube.bottom][i]=i/3*9+i%3;
			mLayers[Tube.top][i]=i/3*9+i%3+6;
			mLayers[Tube.back][i]=i;
			mLayers[Tube.front][i]=i+18;
			mLayers[Tube.middle][i]=i/3*9+i%3*3+1;
			mLayers[Tube.equator][i]=i/3*9+i%3+3;
			mLayers[Tube.standing][i]=i+9;
		}
		
		for(int i=0;i<6;i++) {
			for(int j=0;j<9;j++){
//				Log.i(TAG+".initLayers", "mLayers[" + i + "]=" + mLayers[i][j]);
			}	
		}		
	}

	public void getCrossOtherSideColorOfCenter(int[] center, Color[] cList, int[] layerList){
		int side = center[Tube.CENTERBYSIDE];
		int[] crossCubes = getCrossCube(side);
		for(int i=0;i<crossCubes.length;i++){
			int cube = crossCubes[i];
			int other = getOtherSideOfCross(cube, side);
			if(layerList!=null)
				layerList[i] = other;
			if(cList!=null)
				cList[i] = cubes[cube].getColor(other);
		}
	}
	
	public int[] getOther2SidesOfCorner(int cube, int thisSide){
		int[] otherSides = new int[2];
		int count = 0;
		
		int[] visibles = getVisibleFaces(cube);
		if(visibles.length!=3){
			String out = String.format("visibles length=%d, cube=%d, thisSide=%d", 
					visibles.length, cube, thisSide);
			throw new IllegalStateException(out);
		}
		for(int i=0;i<visibles.length;i++){
			if(visibles[i]!=thisSide)
				otherSides[count++]=visibles[i];
		}
		
		return otherSides;
	}
	
	public int getOtherSideOfCross(int cube, int thisSide){
		int other = -1;
		
		int[] visibles = getVisibleFaces(cube);
		if(visibles.length!=2){
			String out = String.format("visibles length=%d", visibles.length);
			throw new IllegalStateException(out);
		}
		if(visibles[0]==thisSide)
			other = visibles[1];
		else if(visibles[1]==thisSide)
			other = visibles[0];
		else {
			String out = String.format("thisSide(%d) is not in visibles(%s),", 
					thisSide, Arrays.toString(visibles));
			throw new IllegalStateException(out);
		}
		
		return other;
	}
	
	public int[] getCornerCube(int layer){
		int[] cubes = new int[4];
		
		for(int i=0;i<Tube.idxsOfCornerCube.length;i++){
			int idx = Tube.idxsOfCornerCube[i];
			cubes[i] = getCube(layer, idx);
		}
		
		return cubes;		
	}
	
	public int[] getCrossCube(int layer){
		int[] cubes = new int[4];
		
		for(int i=0;i<Tube.idxsOfCrossCube.length;i++){
			int idx = Tube.idxsOfCrossCube[i];
			cubes[i] = getCube(layer, idx);
		}
		
		return cubes;
	}

	public final static int CENTERBYCUBE = 0;
	public final static int CENTERBYSIDE = 1;
	//position[0]: cube idx
	//position[1]: side idx
	public int[] searchCenterColor(Color c){
		int[] position = new int[2]; 
		int centerIdx = 4;
		
		//Log.i(TAG+".searchCenterColor", "c="+c.toInt());
		for(int i=0;i<Tube.SidesEachTube;i++){
			Color color = getVisibleFaceColor(i, centerIdx); 
			Log.i(TAG+".searchCenterColor", "color="+color.toInt());
			if(color.equals(c)){
				position[0] = getCube(i, centerIdx);
				position[1] = i;
				break;
			}	
		}
		/*
		Log.i(TAG+".searchCenterColor", "white="+Color.white.toInt());
		Log.i(TAG+".searchCenterColor", "red="+Color.red.toInt());
		Log.i(TAG+".searchCenterColor", "blue="+Color.blue.toInt());
		Log.i(TAG+".searchCenterColor", "green="+Color.green.toInt());
		Log.i(TAG+".searchCenterColor", "orange="+Color.orange.toInt());
		Log.i(TAG+".searchCenterColor", "yellow="+Color.yellow.toInt());
		*/
		return position;
	}
	
	//whichLayer: (0,5) left, right, back, front, bottom, top
	//idx:       (0,8)
	public Color getVisibleFaceColor(int whichLayer, int idx) {
		int whichCube = mLayers[whichLayer][idx];
		return cubes[whichCube].getColor(whichLayer);
	}
	public void setVisibleFaceColor(int whichLayer, int idx, Color c){
		int whichCube = mLayers[whichLayer][idx];
		cubes[whichCube].setColor(whichLayer, c);
	}

	public void getCenterColors(Color[] faceColor){
		if(faceColor!=null){
			for(int i=0;i<Tube.SidesEachTube;i++){
				faceColor[i] = this.getVisibleFaceColor(i, 4);
			}
		}
	}

	public void resetColor(){
		resetColor(faceColor);
	}
	public void resetColor(Color[] faceColor){
		//default all 27 cubes' colre are defaultcolor
		setColor(Face.defaultcolor);
		//special color for visible faces
		for(int i=0;i<SidesEachTube;i++)
			setColor(i, faceColor[i]);		
	}
	
	public void resetTexture(){
		for(int i=0;i<Tube.CubesEachTube;i++)
			for(int j=0;j<Cube.FacesEachCube;j++)
				cubes[i].setTextureID(j, i, j);
	}
	
	public Tube(float len, Color[] faceColor){
		this(len);
		resetColor(faceColor);
	}
	
	//create a tube at center, with each cube of length len
	public Tube(float len){
		for(int i=0;i<cubes.length;i++){
//			Log.i(TAG+".Tube", "i="+i);
			float coords[] = getCoords(i);
//			Log.i(TAG+".Tube", "coords[0]="+Float.toString(coords[0]));
//			Log.i(TAG+".Tube", "coords[1]="+Float.toString(coords[1]));
//			Log.i(TAG+".Tube", "coords[2]="+Float.toString(coords[2]));

			Vertex v=new Vertex(coords[0], coords[1], coords[2]);
			cubes[i]=new Cube(v, len);
		}
		
		initLayers();
	
		resetColor(faceColor);
		resetTexture();
		
		//initialize the array cubebelongtolayers[][]
		initCubeBelongToLayers();
	}
	
	public Tube(Tube t){
		Cube[] c = t.getCubes();
		for(int i=0;i<CubesEachTube;i++){
			cubes[i] = new Cube(c[i]);
		}
		initLayers();
		initCubeBelongToLayers();
	}
	
	private float[] getCoords(int code){

		float[] coords = new float[3];
	
		for(int i=magicdim-1;i>=0;i--){
			coords[i]=code/base[i];
			code = code - (int)coords[i]*base[i];
		}

		coords = transCoords(coords);
		return coords;
	}
	
	private float[] transCoords(float[] coords){
		for(int i=0;i<magicdim;i++){
			coords[i]=coords[i] - standardorigin[i];;
		}
		
		return coords;
	}

	private int isCubeBelongToLayer(int c, int[] layers){
		int belong  = -1;

L0:
		for(int i=0;i<cubebelongtolayers[c].length;i++){
			for(int j=0;j<layers.length;j++)
				if(cubebelongtolayers[c][i] == layers[j]){
					belong = layers[j];
					break L0;
				}
		}
		return belong;
	}
	
	private void normalDraw2(GL10 gl, ITubeTexture iTexture){
		for(int i=0;i<cubes.length;i++)
			cubes[i].draw(gl, iTexture);
	}
	
	private float getAnimationAngle(int totalAngle){
		float angle;
		
		long currenttime = System.currentTimeMillis();
		//Log.i(TAG, "getanimationangle(...): " + "currenttime=" + currenttime);
		float v = (float)(currenttime - animationStartTime)/(animationLength * totalAngle/90);
		angle = (mRotateAction.getAngle() * v);
		
		return angle;
	}
	
	private ITubeRenderCallbacks mCallbacks=null;
	public void setCallbacks(ITubeRenderCallbacks cb){
		mCallbacks = cb;
	}
	private void drawAnimation2(GL10 gl, ITubeTexture iTexture){
		float rotateangle = getAnimationAngle(mRotateAction.getAngle());
		if(rotateangle > mRotateAction.getAngle()) {	//该rotateRequest完成了
			setRotate(mRotateAction);
			normalDraw2(gl, iTexture);
			if(mCallbacks!=null)
				mCallbacks.onRotateFinish(mRotateAction);
			if(mAnimationQueue.size()==0){	//没有其他rotate请求
				setMode(NORMAL);
			} else {						//还有其他rotate请求
				RotateAction action = mAnimationQueue.remove(0);
				markRotate(action);
			}
			return;
		}
		
		//根据rotatedir设置旋转角度
		if(mRotateAction.getDir() == Tube.CW)
			rotateangle = rotateangle * (-1);
		//Log.i(TAG, "drawanimation(...): " + "rotateangle=" + Float.valueOf(rotateangle).toString());			

		for(int i=0;i<cubes.length;i++){
			int layer = isCubeBelongToLayer(i, mRotateAction.getLayer());
			if(layer!=-1){
				//rotate draw
				int axis = LAYERAXIS[layer];
				drawRotateCube2(gl, iTexture, i, rotateangle, axis);
			} else {
				//正常draw
				cubes[i].draw(gl, iTexture);
			}
		}
	}
	
	private void drawRotateCube2(GL10 gl, ITubeTexture iTexture, int whichcube, float angle, int axis){
		gl.glPushMatrix();
		{	//挨个对这些cubes的可见face做旋转动作
			gl.glRotatef(angle, axisCoords[axis][0], axisCoords[axis][1], axisCoords[axis][2]);
			cubes[whichcube].draw(gl, iTexture);	
		}
		gl.glPopMatrix();
	}

	@Override
	public	void draw(GL10 gl, ITubeTexture iTexture) {
		//为了避免闪烁，drawanimation和normaldraw的画cube顺序应当一致
		switch(getMode()){
		case ANIMATIONON:
			drawAnimation2(gl, iTexture);
			break;
		case NORMAL:
			normalDraw2(gl, iTexture);
			break;
		default:
			break;
		}
	}

	@Override
	public	void setColor(Color color) {
		for(int i=0;i<cubes.length;i++){
			cubes[i].setColor(color);
		}
	}

	@Override
	public	void setColor(int side, Color color) {
		for(int i=0;i<CubesEachSide;i++){
//			Log.i(TAG+".setColor", "side=" + LAYERSTR_F[side]);
//			Log.i(TAG+".setColor", "cube=" + mLayers[side][i]);
			cubes[mLayers[side][i]].setColor(side, color);
		}
	}
	
	public void getColor(int[] colors){
		for(int i=0;i<Tube.CubesEachTube;i++)
			for(int j=0;j<Cube.FacesEachCube;j++){
				int idx = i*Cube.FacesEachCube+j;
				colors[idx] = cubes[i].getColor(j).toInt();
			}
	}
	public void setColor(int[] colors){
		for(int i=0;i<Tube.CubesEachTube;i++)
			for(int j=0;j<Cube.FacesEachCube;j++){
				int idx = i*Cube.FacesEachCube+j;
				cubes[i].setColor(j, colors[idx]);
			}
	}
	
	public boolean sideColorIdentical(int side){
		boolean bIdentical = true;
		
		Color c1 = getVisibleFaceColor(side, 0);
		for(int i=1;i<Tube.CubesEachSide;i++){
			Color c2= getVisibleFaceColor(side, i);
			if(!c2.equals(c1)){
				bIdentical = false;
				break;
			}
		}
		return bIdentical;
	}
	
	public boolean inOriginState(){
		boolean bInOrigin = true;
		
		for(int side=0;side<Tube.SidesEachTube;side++){
			if(!sideColorIdentical(side)){
				bInOrigin = false;
				break;
			}
		}
		
		return bInOrigin;
	}
	
	public float[][] getCenters(int side){
		float[][] centers = new float[Tube.CubesEachSide][3];
		
		for(int i=0;i<Tube.CubesEachSide;i++){
			int idx = getCube(side, i);
			Cube c = cubes[idx];
			centers[i] = c.getCenter(side);
		}
		return centers;
	}
	
	public String toKociembaFacelet(){
		String kociemba = "";
		String[] kociembaColors={
				"L",
				"R",
				"D",
				"U",
				"B",
				"F",
		};
		int[][] topFacesMap = {
				{top, 0}, {top, 1}, {top, 2}, {top, 3}, 
				{top, 4}, {top, 5}, {top, 6}, {top, 7}, {top, 8}, 	
		};
		int[][] rightFacesMap = {
				{right, 8}, {right, 5}, {right, 2}, {right, 7}, 
				{right, 4}, {right, 1}, {right, 6}, {right, 3}, {right, 0},
		};
		int[][] frontFacesMap = {
				{front, 6}, {front, 7}, {front, 8}, {front, 3}, 
				{front, 4}, {front, 5}, {front, 0}, {front, 1}, {front, 2},
		};
		int[][] bottomFacesMap = {
				{bottom, 6}, {bottom, 7}, {bottom, 8}, {bottom, 3}, 
				{bottom, 4}, {bottom, 5}, {bottom, 0}, {bottom, 1}, {bottom, 2},
		};
		int[][] leftFacesMap = {
				{left, 2}, {left, 5}, {left, 8}, {left, 1}, 
				{left, 4}, {left, 7}, {left, 0}, {left, 3}, {left, 6},
		};
		int[][] backFacesMap = {
				{back, 8}, {back, 7}, {back, 6}, {back, 5}, 
				{back, 4}, {back, 3}, {back, 2}, {back, 1}, {back, 0},
		};
		int[][][] facesMap = {
				topFacesMap,
				rightFacesMap,
				frontFacesMap,
				bottomFacesMap,
				leftFacesMap,
				backFacesMap,
		};
		HashMap<String, String> maps = new HashMap<String, String>();
		
		for(int i=0;i<6;i++){
			Color c = getVisibleFaceColor(i, 4);
			maps.put(c.toString(), kociembaColors[i]);
		}
		
		for(int i=0;i<facesMap.length;i++){
			int[][] map = facesMap[i];
			for(int j=0;j<map.length;j++){
				int side = map[j][0];
				int faceIdx = map[j][1];
				Color color = this.getVisibleFaceColor(side, faceIdx);
				kociemba += maps.get(color.toString());
			}
		}
		
		return kociemba;
	}
	
	private static HashMap<String, Integer> sideMaps = new HashMap<String, Integer>();
	static {
		sideMaps.put("L", 0);
		sideMaps.put("R", 1);
		sideMaps.put("D", 2);
		sideMaps.put("U", 3);
		sideMaps.put("B", 4);
		sideMaps.put("F", 5);
		
		Log.i(TAG+".static", "running get(1)");
		sideMaps.get(1);
	}

	
	public static Stack<RotateAction> parseKociembaSolution(String solution){
		Stack<RotateAction> commands = new Stack<RotateAction>();
		String sides = "LRDUFB";
		
		int i = 0;
		while(i<solution.length()) {
			char chSide = solution.charAt(i);
			if(chSide==' '){
				i++;
				continue;
			}
			if(sides.indexOf(chSide)==-1){
				Log.e(TAG+".parseKociembaSolution", "invalid solution = "+solution);
				return null;
			}
			
			Log.i(TAG+".parseKociembaSolution", "chSide="+chSide);
			if(sideMaps==null){
				Log.e(TAG+".parseKociembaSolution", "sideMaps=null");
				return null;
			}
			
			int side = sideMaps.get(""+chSide);
			int dir = Tube.CW, times = 1;
			if(i+1<solution.length()){
				char next = solution.charAt(i+1);
				if(next=='\''){
					dir = Tube.CCW;
					i++;
				} else if(next=='2') {
					times = 2;
					i++;
				} 
				//reverse dir for these 3 sides
				if((chSide=='B'||chSide=='L'||chSide=='D')&&(next!='2')){
					dir = Tube.reverseDir(dir);
				}
				int angle = 90*times;
				Log.i(TAG+".parseKociembaSolution", "angle = "+angle);
				RotateAction r = new RotateAction(side, dir, angle);
				commands.add(r);
			} 
			i++;
		}
		
		return commands;
	}
	
	public static int reverseDir(int dir){
		int rDir;
		
		if(dir==Tube.CCW)
			rDir = Tube.CW;
		else 
			rDir = Tube.CCW;
		
		return rDir;
	}
	
	public static int[][] loadCubesColor(Context context, AttributeSet attrs){
		int[][] colors = new int[Tube.CubesEachTube][Cube.FacesEachCube];
		
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TubePlayer);
        for(int i=0;i<Tube.CubesEachTube;i++){
        	for(int j=0;j<Cube.FacesEachCube;j++) {
        		int idx = i*Cube.FacesEachCube+j;
        		int defaultColor;
        		if(Tube.isVisibleFace(i, j))
        			defaultColor = Color.gray.toInt();
        		else 
        			defaultColor = Color.black.toInt();
        		colors[i][j] = a.getColor(idx, defaultColor); 
        	}
        }
        a.recycle();
        
		return colors;
	}
	public static String layer2String(int layer, int type){
		return LAYERSTR_S[layer][type];
	}
	public static String mapTwoLayers2One(int layer1, int layer2){
		String simpleRep="";
		int[] layer = {layer1, layer2};
		
		if(layer1==left&&layer2==middle||layer1==middle&&layer2==left)
			simpleRep = "l";
		else if(layer1==right&&layer2==middle||layer1==middle&&layer2==right)
			simpleRep = "r";
		else if(layer1==bottom&&layer2==equator||layer1==equator&&layer2==bottom)
			simpleRep = "d";
		else if(layer1==top&&layer2==equator||layer1==equator&&layer2==top)
			simpleRep = "u";
		else if(layer1==back&&layer2==standing||layer1==standing&&layer2==back)
			simpleRep = "b";
		else if(layer1==front&&layer2==standing||layer1==standing&&layer2==front)
			simpleRep = "f";
		else {
			Log.e(TAG+".mapTwoSides2One", "invalid layer[] = " + RotateAction.toString(layer));
			simpleRep = "!";
		}
		
		return simpleRep;
	}
}