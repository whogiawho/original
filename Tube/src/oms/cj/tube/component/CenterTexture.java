package oms.cj.tube.component;

import javax.microedition.khronos.opengles.GL10;

import oms.cj.tube.R;
import android.content.Context;

public class CenterTexture extends NoIDTexture {
	private int[] cubeResID = new int[Tube.SidesEachTube];
	
	private void loadCubeResID(Context context, GL10 gl){
		cubeResID[0] = TextureUtil.getTexture(context, gl, R.drawable.left);
		cubeResID[1] = TextureUtil.getTexture(context, gl, R.drawable.right);
		cubeResID[2] = TextureUtil.getTexture(context, gl, R.drawable.bottom);
		cubeResID[3] = TextureUtil.getTexture(context, gl, R.drawable.top);
		cubeResID[4] = TextureUtil.getTexture(context, gl, R.drawable.back);
		cubeResID[5] = TextureUtil.getTexture(context, gl, R.drawable.front);
	}
	
	public CenterTexture(int cubes, int faces, Context context, GL10 gl) {
		super(cubes, faces, context, gl);
		
		int[][] centerMapping= {
				{12, Tube.left},
				{14, Tube.right},
				{10, Tube.bottom},
				{16, Tube.top},
				{4,  Tube.back},
				{22, Tube.front},
		};
		
		loadCubeResID(context, gl);
		for(int i=0;i<centerMapping.length;i++){
			int whichCube = centerMapping[i][0];
			int whichFace = centerMapping[i][1];
			setTextureID(whichCube, whichFace, cubeResID[i]);
		}
	}
}
