package oms.cj.tube.component;

import javax.microedition.khronos.opengles.GL10;

import oms.cj.tube.R;
import android.content.Context;

public class TransparentTexture extends NoIDTexture {
	private int[] cubeResID = new int[Tube.SidesEachTube];
	
	private void loadCubeResID(Context context, GL10 gl){
		cubeResID[0] = TextureUtil.getTexture(context, gl, R.drawable.transparentleft);
		cubeResID[1] = TextureUtil.getTexture(context, gl, R.drawable.transparentright);
		cubeResID[2] = TextureUtil.getTexture(context, gl, R.drawable.transparentbottom);
		cubeResID[3] = TextureUtil.getTexture(context, gl, R.drawable.transparenttop);
		cubeResID[4] = TextureUtil.getTexture(context, gl, R.drawable.transparentback);
		cubeResID[5] = TextureUtil.getTexture(context, gl, R.drawable.transparentfront);
	}
	
	public TransparentTexture(int cubes, int faces, Context context, GL10 gl) {
		super(cubes, faces, context, gl);
		
		int textureID = TextureUtil.getTexture(context, gl, R.drawable.transparent);
		for(int i=0;i<cubes;i++)
			for(int j=0;j<faces;j++)
				setTextureID(i, j, textureID);
		
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
