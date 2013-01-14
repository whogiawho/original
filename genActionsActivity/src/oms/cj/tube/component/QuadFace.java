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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.opengles.GL10;


//describe a squre like below seen from z infinite, 
//    v0--v3
//    |    |
//    v1--v2
//(v0,v1,v2) form one triface; (v0,v2,v3) form the other
public class QuadFace {
	FloatBuffer mTextureBuffer;
	private FloatBuffer mVertexBuffer;
	private ShortBuffer mIndexBuffer;
	private short indexs[]={0, 1, 2, 0, 2, 3};
	private float[] center = new float[3];
	
	//从(0,0)出发，顺时针旋转？
	private float[] textureVertexs = { 
			0.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,
	};
	
	private Color color;
	private int mTextureWhichCube, mTextureWhichFace;
	private TriFace[] tf=new TriFace[2];
	public TriFace getface(int k){
		return tf[k];
	}
	
	public QuadFace(QuadFace q){
		color = q.getcolor();
		
	}
	//make sure (v0,v1,v2) and (v1,v2,v3) follow CCW direction
	public QuadFace(Vertex v0, Vertex v1, Vertex v2, Vertex v3){
		center[0] = (v0.x+v2.x)/2;
		center[1] = (v0.y+v2.y)/2;
		center[2] = (v0.z+v2.z)/2;
		
		tf[0]=new TriFace(v0, v1, v2);
		tf[1]=new TriFace(v0, v2, v3);
		
		float fvertexs[] = new float[4*3];
		fvertexs[0]=v0.x;
		fvertexs[1]=v0.y;
		fvertexs[2]=v0.z;
		fvertexs[3]=v1.x;
		fvertexs[4]=v1.y; 
		fvertexs[5]=v1.z;
		fvertexs[6]=v2.x;
		fvertexs[7]=v2.y;
		fvertexs[8]=v2.z;
		fvertexs[9]=v3.x;
		fvertexs[10]=v3.y;
		fvertexs[11]=v3.z;

    	ByteBuffer vbb = ByteBuffer.allocateDirect(fvertexs.length*4);
    	vbb.order(ByteOrder.nativeOrder());
    	mVertexBuffer=vbb.asFloatBuffer();
    	mVertexBuffer.put(fvertexs);
    	mVertexBuffer.position(0);
    	
    	ByteBuffer ibb = ByteBuffer.allocateDirect(indexs.length*2);
    	ibb.order(ByteOrder.nativeOrder());
    	mIndexBuffer=ibb.asShortBuffer();
    	mIndexBuffer.put(indexs);
    	mIndexBuffer.position(0);
    	
		ByteBuffer tbb = ByteBuffer.allocateDirect(4*2*4); //4 vertext, 2 coords per vertex, 4 bytes per coord
		tbb.order(ByteOrder.nativeOrder());
		mTextureBuffer = tbb.asFloatBuffer();
		mTextureBuffer.put(textureVertexs);
		mTextureBuffer.position(0);
		
    	color = Face.defaultcolor;
	}
		
	public	void setcolor(Color color){
		this.color=color;
	}
	public Color getcolor(){
		return color;
	}

	public void draw(GL10 gl, ITubeTexture iTexture){
		//启用纹理贴图
		gl.glEnable(GL10.GL_TEXTURE_2D);
		//绑定纹理坐标数据
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glActiveTexture(GL10.GL_TEXTURE0);
		int textureID = iTexture.getTextureID(mTextureWhichCube, mTextureWhichFace);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);	
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
	    
		gl.glColor4f(color._red, color._green, color._blue, 0.7f);		
    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
    	gl.glDrawElements(GL10.GL_TRIANGLES, indexs.length, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);		
	}

	//set this face's textureID to this, indexed by (whichCube, whichFace)
	//A face's textureID is derived indirectly from below variables:
	//1. whichCube
	//2. whichFace
	//3. iTextrue
	public void setTextureID(int whichCube, int whichFace){
		mTextureWhichCube = whichCube;
		mTextureWhichFace = whichFace;
	}	
	
	public int[] getTextureID(){
		int[] textureID = new int[2];
		textureID[0] = mTextureWhichCube;
		textureID[1] = mTextureWhichFace;
		return textureID;
	}
	
	public float[] getCenter() {
		return center;
	}
}