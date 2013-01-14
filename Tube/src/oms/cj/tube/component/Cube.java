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


import javax.microedition.khronos.opengles.GL10;
import android.util.Log;

//describe a cube like below seen from y infinite
//top face               bottom face 
//v0--v1                 v4--v5
//|    |                 |    |
//v3--v2                 v7--v6
public class Cube extends Face{
	public  final static int left = 0;
	public  final static int right = 1;
	public  final static int bottom = 2;
	public  final static int top = 3;
	public  final static int back = 4;
	public  final static int front = 5;
	public final static int FacesEachCube = 6;
	public  final static String[] facestr = {
		"left",
		"right",
		"bottom",
		"top",
		"back",
		"front"
	};
	public final static int axisX=0;
	public final static int axisY=1;
	public final static int axisZ=2;
	public final static int NEGATIVE=0;
	public final static int POSITIVE=1;
	private final static String TAG="Cube";
	
	//face*direction is index; the 3rd dim is result
	private final static int[][][] facesloop = {
			{	{left, right, front, back, bottom, top},	//axisX*CCW 
				{left, right, back, front, top, bottom}		//axisX*CW
			},	
			{	{back, front, bottom, top, right, left},		//axisY*CCW
				{front, back, bottom, top, left, right}		//axisY*CW
			},
			{	{top, bottom, left, right, back, front},		//axisZ*CCW
				{bottom, top, right, left, back, front}		//axisZ*CW
			}
	};

	QuadFace[] faces = new QuadFace[FacesEachCube];
	Color color = Face.defaultcolor;

	public void printallfacescolor(){
		for(int i=0;i<FacesEachCube;i++){
			Log.i(TAG, facestr[i] + "=" + faces[i].getcolor().toString());
		}
	}
	
	public Color[] getColorOfRotate90(int axis, int direction){
		//main task is to complete the faces color loop
		int[] faceloop = facesloop[axis][direction];
		Color[] newcolor = new Color[faceloop.length];
		for(int i=0;i<faceloop.length;i++){
			newcolor[i] = this.getface(faceloop[i]).getcolor();
		}
		
		return newcolor;
	}
	
	public int[][] getTextureIDOfRotate90(int axis, int direction){
		int[] faceloop = facesloop[axis][direction];
		int[][] newTextureID = new int[faceloop.length][];
		for(int i=0;i<faceloop.length;i++){
			newTextureID[i] = this.getface(faceloop[i]).getTextureID();
		}
		
		return newTextureID;
	}
	//make sure vertex follow below style
	//      y          x         z    
	//v0   top        left      back
	//v1   top        right     back
	//v2   top        right     front
	//v3   top        left      front
	//v4   bottom     left      back
	//v5   bottom     right     back
	//v6   bottom     right     front
	//v7   bottom     left      front
	Cube(Vertex v0, Vertex v1, Vertex v2, Vertex v3,
			Vertex v4, Vertex v5, Vertex v6, Vertex v7){
		initfaces(v0, v1, v2, v3, v4, v5, v6, v7);
	}
	
	void initfaces(Vertex v0, Vertex v1, Vertex v2, Vertex v3,
			Vertex v4, Vertex v5, Vertex v6, Vertex v7){
		faces[left] = new QuadFace(v0, v4, v7, v3);
		faces[right] = new QuadFace(v2, v6, v5, v1);
		faces[bottom] = new QuadFace(v7, v4, v5, v6);
		faces[top] = new QuadFace(v0, v3, v2, v1);
		faces[back] = new QuadFace(v1, v5, v4, v0);
		faces[front] = new QuadFace(v3, v7, v6, v2);
	}
	
	public Cube(Vertex center, float halflen){
		Vertex v0, v1, v2, v3, v4, v5, v6, v7;
		float cx, cy, cz;
		
		cx = center.x;
		cy = center.y;
		cz = center.z;
		
		v0 = new Vertex(cx - halflen, cy + halflen, cz - halflen);
		v1 = new Vertex(cx + halflen, cy + halflen, cz - halflen);
		v2 = new Vertex(cx + halflen, cy + halflen, cz + halflen);
		v3 = new Vertex(cx - halflen, cy + halflen, cz + halflen);
		v4 = new Vertex(cx - halflen, cy - halflen, cz - halflen);
		v5 = new Vertex(cx + halflen, cy - halflen, cz - halflen);
		v6 = new Vertex(cx + halflen, cy - halflen, cz + halflen);
		v7 = new Vertex(cx - halflen, cy - halflen, cz + halflen);
	
		initfaces(v0, v1, v2, v3, v4, v5, v6, v7);
	}
	
	Cube(QuadFace left, QuadFace right, QuadFace bottom, 
			QuadFace top, QuadFace back,   QuadFace front){
	
		faces[Cube.left] = left;
		faces[Cube.right] = right;
		faces[Cube.front] = front;
		faces[Cube.back] = back;
		faces[Cube.top] = top;
		faces[Cube.bottom] = bottom;
	}	

	Cube(Cube c){
		for(int i=0;i<FacesEachCube;i++){
			faces[i] = new QuadFace(c.faces[i]);
		}
	}
	@Override
	public void setColor(int face, Color color){
		faces[face].setcolor(color);
	}
	
	public void setColor(int face, int color){
		float i0 = color&0x000000ff;			//b
		float i1 = (color&0x0000ff00)>>8;		//g
		float i2 = (color&0x00ff0000)>>16;		//r
		float i3 = (color&0xff000000)>>24;		//a
		Color c = new Color(i2/255f, i1/255f, i0/255f, i3/255f);
		faces[face].setcolor(c);
	}
	
	public Color getColor(int face){
		return faces[face].getcolor();
	}
	
	public QuadFace getface(int face){
		return faces[face];
	}

	//set cube's whichface face texture to this, indexed by (cubeIdx, faceIdx)
	public void setTextureID(int whichface, int cubeIdx, int faceIdx){
		faces[whichface].setTextureID(cubeIdx, faceIdx);
	}
	public void setTextureID(int whichface, int[] idx){
		setTextureID(whichface, idx[0], idx[1]);
	}
	
	@Override
	public void setColor(Color color){
		this.color = color;
		faces[left].setcolor(color);
		faces[right].setcolor(color);
		faces[front].setcolor(color);
		faces[back].setcolor(color);
		faces[top].setcolor(color);
		faces[bottom].setcolor(color);
	}
	
	@Override
	public void draw(GL10 gl, ITubeTexture iTexture){
		for(int i=0;i<faces.length;i++){
			faces[i].draw(gl, iTexture);
		}
	}
	
	public float[] getCenter(int face){
		return faces[face].getCenter();
	}
}