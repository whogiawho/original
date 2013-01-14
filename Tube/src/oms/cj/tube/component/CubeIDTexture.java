package oms.cj.tube.component;

import javax.microedition.khronos.opengles.GL10;

import oms.cj.tube.R;

import android.content.Context;
import android.util.Log;

@SuppressWarnings("unused")
public class CubeIDTexture extends NoIDTexture {
	private final static String TAG = "CubeIDTexture";
	private int[] cubeResID = new int[Tube.CubesEachTube];
	
	private void loadCubeResID(Context context, GL10 gl){
		cubeResID[0] = TextureUtil.getTexture(context, gl, R.drawable.cube0);
		cubeResID[1] = TextureUtil.getTexture(context, gl, R.drawable.cube1);
		cubeResID[2] = TextureUtil.getTexture(context, gl, R.drawable.cube2);
		cubeResID[3] = TextureUtil.getTexture(context, gl, R.drawable.cube3);
		cubeResID[4] = TextureUtil.getTexture(context, gl, R.drawable.cube4);
		cubeResID[5] = TextureUtil.getTexture(context, gl, R.drawable.cube5);
		cubeResID[6] = TextureUtil.getTexture(context, gl, R.drawable.cube6);
		cubeResID[7] = TextureUtil.getTexture(context, gl, R.drawable.cube7);
		cubeResID[8] = TextureUtil.getTexture(context, gl, R.drawable.cube8);
		cubeResID[9] = TextureUtil.getTexture(context, gl, R.drawable.cube9);
		cubeResID[10] = TextureUtil.getTexture(context, gl, R.drawable.cube10);
		cubeResID[11] = TextureUtil.getTexture(context, gl, R.drawable.cube11);
		cubeResID[12] = TextureUtil.getTexture(context, gl, R.drawable.cube12);
		cubeResID[13] = TextureUtil.getTexture(context, gl, R.drawable.cube13);
		cubeResID[14] = TextureUtil.getTexture(context, gl, R.drawable.cube14);
		cubeResID[15] = TextureUtil.getTexture(context, gl, R.drawable.cube15);
		cubeResID[16] = TextureUtil.getTexture(context, gl, R.drawable.cube16);
		cubeResID[17] = TextureUtil.getTexture(context, gl, R.drawable.cube17);
		cubeResID[18] = TextureUtil.getTexture(context, gl, R.drawable.cube18);
		cubeResID[19] = TextureUtil.getTexture(context, gl, R.drawable.cube19);
		cubeResID[20] = TextureUtil.getTexture(context, gl, R.drawable.cube20);
		cubeResID[21] = TextureUtil.getTexture(context, gl, R.drawable.cube21);
		cubeResID[22] = TextureUtil.getTexture(context, gl, R.drawable.cube22);
		cubeResID[23] = TextureUtil.getTexture(context, gl, R.drawable.cube23);
		cubeResID[24] = TextureUtil.getTexture(context, gl, R.drawable.cube24);
		cubeResID[25] = TextureUtil.getTexture(context, gl, R.drawable.cube25);
		cubeResID[26] = TextureUtil.getTexture(context, gl, R.drawable.cube26);
	}
	
	public CubeIDTexture(int cubes, int faces, Context context, GL10 gl){
		super(cubes, faces, context, gl);
		
		loadCubeResID(context, gl);
		for(int i=0;i<Tube.CubesEachTube;i++){
			int[] visiblefaces = Tube.getVisibleFaces(i);
			for(int j=0;j<visiblefaces.length;j++){
				int whichface = visiblefaces[j];
				setTextureID(i, whichface, cubeResID[i]);
			}
		}
		
/*		
		for(int i=0;i<Tube.CubesEachTube;i++)
			for(int j=0;j<Cube.FacesEachCube;j++)
				Log.i(TAG, "CubeIDTexture(...): " + "TextureID = " + Integer.toString(getTextureID(i,j)));
*/				
	}
}
