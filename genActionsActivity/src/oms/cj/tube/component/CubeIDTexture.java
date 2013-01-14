package oms.cj.tube.component;

import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.util.Log;
import oms.cj.genActions.R;

@SuppressWarnings("unused")
public class CubeIDTexture extends NoIDTexture {
	private final static String TAG = "CubeIDTexture";
	private int[] cubeResID = new int[Tube.CubesEachTube];
	
	private void loadCubeResID(Context context, GL10 gl){
		cubeResID[0] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[1] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[2] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[3] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[4] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[5] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[6] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[7] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[8] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[9] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[10] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[11] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[12] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[13] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[14] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[15] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[16] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[17] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[18] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[19] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[20] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[21] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[22] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[23] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[24] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[25] = TextureUtil.getTexture(context, gl, R.drawable.icon);
		cubeResID[26] = TextureUtil.getTexture(context, gl, R.drawable.icon);
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
