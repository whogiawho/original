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
import java.util.Random;
import java.util.Stack;
import javax.microedition.khronos.opengles.GL10;
import android.util.Log;

//a tube is defined with below numbering
//back                  standing                  front    
//  6  7  8               15  16  17                24  25  26 
//  3  4  5               12  13  14                21  22  23
//  0  1  2               9   10  11                18  19  20

public class Tube extends Face{
	private final static int NORMAL=0;
	private final static int ANIMATIONON=1; 
	public final static int animationmaxlength=1000;			//by milli seconds
	public final static int animationdefaultlength=500;
	public final static int randomRotateNdefault=50;
	public static int defaultsize=23;
	public static int animationlength=animationmaxlength - animationdefaultlength;   				
	public static int randomRotateN=randomRotateNdefault;		//随机旋转圈数
	public static int size=defaultsize;
	public static int size2distance(int size){
		return 31-size;
	}

	public final static float defaulthalflength=0.47f;
	public final static int CubesEachTube = 27;
	public final static int SideFacesEachTube = 6;
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
	private final static int[][] axiscoords={{1,0,0}, {0,1,0}, {0,0,1}};
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
	public final static int[] sideaxis = {
		Cube.axisX, Cube.axisX,	//left, right
		Cube.axisY, Cube.axisY,	//bottom, top
		Cube.axisZ, Cube.axisZ,	//back, front
		Cube.axisX, 			//middle
		Cube.axisY, 			//equator
		Cube.axisZ, 			//standing
	};
	public final static String[] sidestr = {
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

	public final static Color[] facecolor = {	
		Color.red, Color.orange,
		Color.white, Color.yellow,
		Color.blue, Color.green,
	};
	private final static int magicdim=3;
	private final static int base[]={1, 3, 9};
	private final static int[] standardorigin={1,1,1};
	//cubebelongtosides【i】【j】意为第i个cube绕j轴的sides
	//i为cube编号，
	//j为轴编号，x轴=0；y轴=1；z轴=2，对应于magiccube.axisX，magiccube.axisY和magiccube.axisZ
	private final int[][] cubebelongtosides=new int[CubesEachTube][3];
	
	//sides【i】【j】意为第i个side中第j个格子的cube
	//i为sides编号，j为sides中格子编号
	private final int[][] sides = new int [9][9];
	
	//visiblefaces【i】【j】意为第i个cube的第j个可见face
	//i为cube编号
	//j为第i个cube的可见face编号
	private final static int[][] visiblefaces ={
			{left, bottom, back},
			{bottom, back},
			{right, bottom, back},
			{left, back},
			{back},
			{right, back},
			{left, top, back},
			{top, back},
			{right, top, back},
			{left, bottom},
			{bottom},
			{right, bottom},
			{left},
			{},
			{right},
			{left, top},
			{top},
			{right, top},
			{left, bottom, front},
			{bottom, front},
			{right, bottom, front},
			{left, front},
			{front},
			{right, front},
			{left, top, front},
			{top, front},
			{right, top, front}			
	};
	
	//movingcube【i】【j】描述了各sides的逆(0)顺(1)时针运动规律
	private final int movingcubes[][][] = new int[][][] {
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
		}, //side		
	};
	
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
		private int[] rotateside;
		private int rotatedir;
		private long animationstarttime;


	public Cube[] getCubes(){
		return cubes;
	}
	
	public static int[] getVisibleFaces(int idxcube){
		return visiblefaces[idxcube]; 
	}
	
	public int getCubeBelongTosideBy(int whichcube, int whichaxis){
		return cubebelongtosides[whichcube][whichaxis];
	}
	
	public void enqueueRotateRequest(RotateAction action){
		mAnimationQueue.add(action);
		if(getMode()!=ANIMATIONON){
			RotateAction rAction = mAnimationQueue.remove(0);
			markRotate(rAction.getLayer(), rAction.getDir());
		}
	}
	private void markRotate(int[] sides, int direction){
		setMode(ANIMATIONON);
		rotateside = sides;
		rotatedir = direction;
		animationstarttime = System.currentTimeMillis(); 
		Log.i(TAG, "markRotate(...): " + "animationstarttime=" + animationstarttime);		
	}
	
	public void randomize(Stack<RotateAction> actions){
		Random rside = new Random(), rdir=new Random();
		
		for(int i=0;i<randomRotateN;i++){
			int[] sides={rside.nextInt(9)};
			int dir=rdir.nextInt(2);
			setRotate(sides, dir);
			actions.push(new RotateAction(sides, dir));
		}
	}
	
	public void setRotate(int[] sides, int direction){
		for(int i=0;i<sides.length;i++)
			setRotate(sides[i], direction);
	}
	public void setRotate(int side, int direction){
		int[] oldidxoncube = new int[9];
		int[] newidxoncube = new int[9];
		Color[][] colors = new Color[9][];
		int[][][] textureID = new int[9][][];
		
		for(int i=0;i<9;i++){
			oldidxoncube[i] = sides[side][i];
			int newidxonface = movingcubes[side][direction][i];
			newidxoncube[i] = sides[side][newidxonface];			
//			Log.i(TAG, "oldidxoncube=" + oldidxoncube[i]);
//			Log.i(TAG, "newidxoncube=" + newidxoncube[i]);
			
			colors[i] = cubes[newidxoncube[i]].getColorOfRotate90(sideaxis[side], direction);  //newcubes【i】做90度转动
			textureID[i] = cubes[newidxoncube[i]].getTextureIDOfRotate90(sideaxis[side], direction);
		}

		for(int i=0;i<9;i++){
			for(int j=0;j<colors[i].length;j++){
//				Log.i(TAG, "color[j]=" + colors[j].toString());
				cubes[oldidxoncube[i]].setColor(j, colors[i][j]);
				cubes[oldidxoncube[i]].setTextureID(j, textureID[i][j]);
			}
		}
	}

	private int axisOfSide(int side){
		int axis;
		
		if(side == left || side == right || side == middle){
			axis = Cube.axisX;
		} else if(side == bottom || side == top || side == equator){
			axis = Cube.axisY;
		} else {
			axis = Cube.axisZ;
		}
		return axis;
	}
		
	private void initCubeBelongToSides(){
		for(int i=0;i<sides.length;i++){
			int whichaxis = axisOfSide(i);
			for(int j=0;j<sides[i].length;j++){
				int whichcube = sides[i][j];
				cubebelongtosides[whichcube][whichaxis]=i;
			}
		}
/*		
		for(int i=0;i<27;i++){
			for(int j=0;j<3;j++){
				Log.i(TAG, "("+i+","+j+")="+cubebelongtosides[i][j]);
			}
		}
*/		
	}
	
	//Note:
	//1. define the mapping relationship 
	//              Input             Output      
	//   mapping    side cube idx     tube cub idx 
	//   range      (0,8)             (0,26)
	//2. side cube idx is also utilized in movingcubes[][][]
	// left:   24 15 6    8 5 2
	//         21 12 3    7 4 1
	//         18 9  0    6 3 0
	
	// right:  26 17 8    8 5 2
	//         23 14 5    7 4 1
	//         20 11 2    6 3 0
	
	// bottom: 0  1  2    0 1 2
	//         9  10 11   3 4 5
	//         18 19 20   6 7 8
	
	// top:    6  7  8    0 1 2
	//         15 16 17   3 4 5
	//         24 25 26   6 7 8
	
	// back:   6  7  8    6 7 8
	//         3  4  5    3 4 5
	//         0  1  2    0 1 2
	
	// front:  24 25 26   6 7 8
	//         21 22 23   3 4 5
	//         18 19 20   0 1 2
	
	// middle:
	
	// equator:3  4  5    0 1 2
	//         12 13 14   3 4 5
	//         18 19 20   6 7 8
	
	// side:
	
	private void initSides(){
		for(int i=0;i<CubesEachSide;i++){
			sides[Tube.left][i]=3*i;
			sides[Tube.right][i]=3*i+2;
			sides[Tube.bottom][i]=i/3*9+i%3;
			sides[Tube.top][i]=i/3*9+i%3+6;
			sides[Tube.back][i]=i;
			sides[Tube.front][i]=i+18;
			sides[Tube.middle][i]=i/3*9+i%3*3+1;
			sides[Tube.equator][i]=i/3*9+i%3+3;
			sides[Tube.standing][i]=i+9;
		}
		
		for(int i=0;i<6;i++) {
			for(int j=0;j<9;j++){
//				Log.i(TAG, "magiccube3(...): " + "sides[" + i + "]=" + sides[i][j]);
			}	
		}		
	}

	public void resetColor(){
		//default all 27 cubes' colre are defaultcolor
		setColor(Face.defaultcolor);
		//special color for visible faces
		for(int i=0;i<SideFacesEachTube;i++)
			setColor(i, facecolor[i]);		
	}
	
	private void resetTexture(){
		for(int i=0;i<Tube.CubesEachTube;i++)
			for(int j=0;j<Cube.FacesEachCube;j++)
				cubes[i].setTextureID(j, i, j);
	}
	
	//create a magiccube3 at center, with each cube of length len
	public Tube(float len){
		for(int i=0;i<cubes.length;i++){
//			Log.i(TAG, "magiccube3(...): "+"i="+i);
			float coords[] = getCoords(i);
//			Log.i(TAG, "magiccube3(...): "+"coords[0]="+Float.toString(coords[0]));
//			Log.i(TAG, "magiccube3(...): "+"coords[1]="+Float.toString(coords[1]));
//			Log.i(TAG, "magiccube3(...): "+"coords[2]="+Float.toString(coords[2]));

			Vertex v=new Vertex(coords[0], coords[1], coords[2]);
			cubes[i]=new Cube(v, len);
		}
		
		initSides();
	
		resetColor();
		resetTexture();
		
		//initialize the array cubebelongtosides[][]
		initCubeBelongToSides();
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

	private int isCubeBelongToSide(int c, int[] sides){
		int belong  = -1;

L0:
		for(int i=0;i<cubebelongtosides[c].length;i++){
			for(int j=0;j<sides.length;j++)
				if(cubebelongtosides[c][i] == sides[j]){
					belong = sides[j];
					break L0;
				}
		}
		return belong;
	}
	
	private void normalDraw2(GL10 gl, ITubeTexture iTexture){
		for(int i=0;i<cubes.length;i++)
			cubes[i].draw(gl, iTexture);
	}
	
	private float getAnimationAngle(){
		float angle;
		
		long currenttime = System.currentTimeMillis();
		//Log.i(TAG, "getanimationangle(...): " + "currenttime=" + currenttime);
		float v = (float)(currenttime - animationstarttime)/animationlength;
		angle = (90 * v);
		
		return angle;
	}
	
	private void drawAnimation2(GL10 gl, ITubeTexture iTexture){
		float rotateangle = getAnimationAngle();
		if(rotateangle > 90) {	//该rotateRequest完成了
			setRotate(rotateside, rotatedir);
			normalDraw2(gl, iTexture);
			if(mAnimationQueue.size()==0){	//没有其他rotate请求
				setMode(NORMAL);
			} else {						//还有其他rotate请求
				RotateAction action = mAnimationQueue.remove(0);
				markRotate(action.getLayer(), action.getDir());
			}
			return;
		}
		
		//根据rotatedir设置旋转角度
		if(rotatedir == Tube.CW)
			rotateangle = rotateangle * (-1);
		//Log.i(TAG, "drawanimation(...): " + "rotateangle=" + Float.valueOf(rotateangle).toString());			

		for(int i=0;i<cubes.length;i++){
			int side = isCubeBelongToSide(i, rotateside);
			if(side!=-1){
				//rotate draw
				int axis = sideaxis[side];
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
			gl.glRotatef(angle, axiscoords[axis][0], axiscoords[axis][1], axiscoords[axis][2]);
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
	public	void setColor(int face, Color color) {
		for(int i=0;i<CubesEachSide;i++){
//			Log.i(TAG, "setcolor(...): " + "face=" + magiccube3.sidestr[face]);
//			Log.i(TAG, "setcolor(...): " + "cube=" + sides[face][i]);
			cubes[sides[face][i]].setColor(face, color);
		}
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
	public static int reverseDir(int dir){
		int rDir;
		
		if(dir==Tube.CCW)
			rDir = Tube.CW;
		else 
			rDir = Tube.CCW;
		
		return rDir;
	}
}