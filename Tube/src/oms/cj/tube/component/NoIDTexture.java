package oms.cj.tube.component;

import javax.microedition.khronos.opengles.GL10;

import oms.cj.tube.R;

import android.content.Context;
import android.util.Log;

@SuppressWarnings("unused")
public class NoIDTexture implements ITubeTexture {
	private int[][] textureIDs = null;
	private int mCommonTextureID;
	private final static String TAG = "NoIDTexture";
	
	public NoIDTexture(int cubes, int faces, Context context, GL10 gl){
		textureIDs = new int[cubes][faces];
		mCommonTextureID = TextureUtil.getTexture(context, gl, R.drawable.tubepatch);
		InitTexture();
	}
	
	private void InitTexture() {
		for(int i=0;i<textureIDs.length;i++)
			for(int j=0;j<textureIDs[i].length;j++)
				textureIDs[i][j] = mCommonTextureID;
/*		
		for(int i=0;i<Tube.CubesEachTube;i++)
			for(int j=0;j<Cube.FacesEachCube;j++)
				Log.i(TAG, "CubeIDTexture(...): " + "TextureID = " + Integer.toString(getTextureID(i,j)));
*/				
	}

	public int getCommonTextureID(){
		return mCommonTextureID;
	}
	
	public void setTextureID(int whichCube, int whichFace, int textureID){
		textureIDs[whichCube][whichFace] = textureID;
	}
	
	@Override
	public int getTextureID(int whichCube, int whichFace) {		
		return textureIDs[whichCube][whichFace];
	}
}
