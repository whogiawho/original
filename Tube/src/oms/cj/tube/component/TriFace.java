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

public class TriFace extends Face{
	private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
	private Vertex[] vertexs= new Vertex[3];
	private short indexs[]={0, 1, 2};
	Color color=Face.defaultcolor;
	
	//make sure (v0,v1,v2) followed CCW direction
	public TriFace(Vertex v0, Vertex v1, Vertex v2){
		vertexs[0]=v0; vertexs[1]=v1; vertexs[2]=v2;
		
		float fvertexs[] = new float[3*3];
		fvertexs[0]=v0.x;
		fvertexs[1]=v0.y;
		fvertexs[2]=v0.z;
		fvertexs[3]=v1.x;
		fvertexs[4]=v1.y; 
		fvertexs[5]=v1.z;
		fvertexs[6]=v2.x;
		fvertexs[7]=v2.y;
		fvertexs[8]=v2.z;
		
    	ByteBuffer vbb = ByteBuffer.allocateDirect(fvertexs.length*4);
    	vbb.order(ByteOrder.nativeOrder());
    	vertexBuffer=vbb.asFloatBuffer();
    	vertexBuffer.put(fvertexs);
    	vertexBuffer.position(0);
    	
    	ByteBuffer ibb = ByteBuffer.allocateDirect(indexs.length*2);
    	ibb.order(ByteOrder.nativeOrder());
    	indexBuffer=ibb.asShortBuffer();
    	indexBuffer.put(indexs);
    	indexBuffer.position(0);
	}
	
	public Vertex[] getvertex(){ 
		return vertexs;
	}
	public FloatBuffer getvertexbuffer(){
		return vertexBuffer;
	}
	public ShortBuffer getindexbuffer(){
		return indexBuffer;
	}
	
	@Override
	public	void setColor(Color color){
		this.color=color;
	}
	
	@Override
	public	void draw(GL10 gl, ITubeTexture iTexture){
		gl.glColor4f(color._red, color._green, color._blue, color._alpha);
    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
    	gl.glDrawElements(GL10.GL_TRIANGLES, indexs.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);		
	}

	@Override
	public	void setColor(int face, Color color) {
		setColor(color);
	}
}
