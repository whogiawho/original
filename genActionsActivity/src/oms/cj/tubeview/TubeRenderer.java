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
package oms.cj.tubeview;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.Stack;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10; 
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.CubeIDTexture;
import oms.cj.tube.component.ITubeTexture;
import oms.cj.tube.component.NoIDTexture;
import oms.cj.tube.component.QuadFace;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.TriFace;
import oms.cj.tube.component.Tube;
import oms.cj.tube.component.Vertex;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
import android.view.MotionEvent;
import com.example.android.apis.graphics.spritetext.*; 
import com.ophone.ogles.lib.Projector;
import com.ophone.ogles.lib.Quaternion;
import com.ophone.ogles.lib.Ray;
import com.ophone.ogles.lib.Vector3f;
import com.ophone.ogles.lib.Vector4f;

public class TubeRenderer implements GLSurfaceView.Renderer{
    /*viewstate状态转化图：
     * 
     * 判断函数			viewstate初始值（IN）      	其他输入（IN）	   	做什么（OUT）
     * -------------------------------------------------------------------------------------------------
     * trackcoords()	STOP					DOWN				if(和cube3相交) then viewstate->ROTATEFACEONCUBE
     * 																if(不和cube3相交) then viewstate->ROTATECUBE3 								
     * trackcoords()	ROTATEFACEONCUBE		MOVE				移动cube3上的相交face；viewstate->STOP
     * trackcoords()	ROTATECUBE3				MOVE				设置新的movePoint
     * trackcoords()	ROTATECUBE3				UP					viewstate->STOP
     *                                                              设置spinAxis，rotation，重置downPoint和movePoint
     * trackcoords()	ROTATEFACEONCUBE		UP					viewstate->STOP
     * 
     * 以下情况发生的可能性很小很小
     * trackcoords()	STOP					UP
     * trackcoords()	ROTATEFACEONCUBE		DOWN
     * trackcoords()	ROTATECUBE3				DOWN
     * trackcoords()	STOP					MOVE
     *  
     */

	// screen background color definition
	private final static float red=0.0f, green=0.0f, blue=0.0f; 
	private final static float alpha=1.0f;	
	//viewstate definition
    private final static int STOP = -1;
    private final static int ROTATEFACEONCUBE3 = 0;
    private final static int ROTATECUBE3 = 1;
    //texture types
    public final static int TEXTURENOID = 0;
    public final static int TEXTURECUBEID = 1;
    
    //const variables
	private final static String TAG = "cuberenderer";
	private final static Point invalidpt = new Point(-1, -1);
	private static final double DRAG_SLOWING = 90;
	
	private ITubeTexture mTextureInterface=null;
    private String mRestoreFromFile;
    private boolean mRandomized;
	private int mIDSwitch;
	private Context mContext;

	private Tube mTube;
	
	//matrixGrabber的最新值是在onDrawFrame()中设置的，pickray()使用它来拾取射线
	private MatrixGrabber mMatrixGrabber=null;
    //visible screen's width and height
    private int[] mProjectionView={0,0,0,0};    
    private int mViewState = STOP;
    private int getRenderState(){
    	return mViewState;
    }
    private void setRenderState(int state){
    	mViewState = state; 
    }

    //startcoords, endcoords, start and end are special variables for intersect(...)
    //当动作是ROTATEFACEONCUBE时，需要它们来确定要旋转的side和dir
	private int[] startCoord = {0,0,0}, endCoord = {0,0,0};
	private Vector4f startV, endV;
    
    //define kinds of textureIDs here
    private ITubeTexture[] iTextures = new ITubeTexture[2];
    //places to save RotateAction sequences
	private Stack<RotateAction> mActions = new Stack<RotateAction>();
	
	private static String toString(int[] sides){
		String s = "[";
		for(int i=0;i<sides.length;i++){
			s = s + sides[i] + ",";
		}
		s = s + "]";
		return s;
	}
	private void onRotate(int[] sides, int direction){
		RotateAction action=new RotateAction(sides, direction);
		mActions.push(action);
		Log.i(TAG+".onRotate", "stack size=" + mActions.size());
	}
    private void rotate(int[] sides, int dir){
    	Log.i(TAG+".rotate", "face=" + toString(sides));
    	Log.i(TAG+".rotate", "dir=" + dir);
		//call onSetRotateColor() to save this actions to mActions
		onRotate(sides, dir);
		mTube.enqueueRotateRequest(new RotateAction(sides, dir));
    }
	public void undoRotate(){
		if(mActions.size()!=0){
			RotateAction lastAction=mActions.pop(); 
			Log.i(TAG, "undoRotate(...): " + "stack size=" + mActions.size());
			mTube.enqueueRotateRequest(new RotateAction(lastAction.getLayer(), reverseDir(lastAction.getDir())));
		}
	}
	private int reverseDir(int dir){
		int rDir;
		
		if(dir==Tube.CCW)
			rDir = Tube.CW;
		else 
			rDir = Tube.CCW;
		
		return rDir;
	}
	@SuppressWarnings("unchecked")
	public void restoreFromFile(String strFileName, Activity act) throws OptionalDataException, ClassNotFoundException, IOException{
		FileInputStream fis = act.openFileInput(strFileName);
		ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(fis));
		
		mActions = (Stack<RotateAction>) objectIn.readObject();
		objectIn.close();
		
		mTube.resetColor();
		for(int i=0;i<mActions.size();i++){
			RotateAction action = mActions.get(i);
			mTube.setRotate(action.getLayer(), action.getDir());
		}
	}

    public TubeRenderer( String fileName, boolean randomized, int IDSwitch, Activity act){
    	mRestoreFromFile = fileName;
    	mRandomized = randomized;
    	mIDSwitch = IDSwitch;
    	mContext = act;
    	
    	initTube();
    }

    public void setCubesColor(int[][] colors){
    	Cube[] cubes = mTube.getCubes();
    	for(int i=0;i<Tube.CubesEachTube;i++)
    		for(int j=0;j<Cube.FacesEachCube;j++){
    			cubes[i].setColor(j, colors[i][j]);
    		}
    }
    
    private void initTube(){
        if(mMatrixGrabber == null)
        	mMatrixGrabber = new MatrixGrabber();
    	
        startV = new Vector4f();
        endV = new Vector4f();
        
        mTube=new Tube(Tube.defaulthalflength);
        
    	//firstly initialize the mCube3 from fileName
    	if(mRestoreFromFile!=null){
			try {
				restoreFromFile(mRestoreFromFile, (Activity) mContext);
			} catch (OptionalDataException e) {
				Log.e(TAG+"initTube", "; OptionalDataException");
			} catch (ClassNotFoundException e) {
				Log.e(TAG+"initTube", "; ClassNotFoundException");
			} catch (IOException e) {
				Log.e(TAG+"initTube", "; IOException");
			}
    	}
    	
    	//secondly decide to whether to randomize mCube3
    	if(mRandomized)
    		mTube.randomize(mActions);
    }
    
    private void initTextureIDs(GL10 gl){
    	iTextures[0] = new NoIDTexture(Tube.CubesEachTube, Cube.FacesEachCube, mContext, gl);
    	iTextures[1] = new CubeIDTexture(Tube.CubesEachTube, Cube.FacesEachCube, mContext, gl);
    }
    
    private ITubeTexture getTextureIDs(int idSwitch){
    	ITubeTexture iTexture;
    	
    	switch(idSwitch){
    	case TEXTURENOID:
    	default:
    		iTexture = iTextures[0];
    		break;
    	case TEXTURECUBEID:
    		Log.i(TAG, "CubeIDTexture is selected!");
    		iTexture = iTextures[1];
    		break;
    	}
    	
    	return iTexture;
    }
    @SuppressWarnings("unused")
    private void setTubeTexture(int idSwitch){
    	mTextureInterface = getTextureIDs(idSwitch);
	}
    private void initTexture(GL10 gl){

    	initTextureIDs(gl);
        mTextureInterface = getTextureIDs(mIDSwitch);
    }
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	Log.i(TAG+".onSurfaceCreated", "being called!");
    	
        // preparation
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // enable the differentiation of which side may be visible 
        gl.glEnable(GL10.GL_CULL_FACE);
        // which is the front? the one which is drawn counter clockwise
        gl.glFrontFace(GL10.GL_CCW);
        // which one should NOT be drawn
        gl.glCullFace(GL10.GL_BACK);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        
        initTexture(gl);
    }
    
    private Point translate(Point pt){
    	Point newPoint = new Point(pt);
    	newPoint.y = mProjectionView[3] - newPoint.y;
    	return newPoint;
    }

    private Stack<RotateAction> mCommands = new Stack<RotateAction>();
    private int mCurrentPosition = 0;
    @SuppressWarnings("unchecked")
	public void getActionsFrom(String commandFile) throws StreamCorruptedException, IOException, ClassNotFoundException {
    	mCommands.clear();
		mCurrentPosition = 0;
		
    	final AssetManager mAssetManager = mContext.getResources().getAssets();
		ObjectInputStream objectIn = new ObjectInputStream(mAssetManager.open(commandFile, AssetManager.ACCESS_BUFFER));
		
		mCommands = (Stack<RotateAction>) objectIn.readObject();
		objectIn.close();
    }
    public void forwardRotate(){
    	if(mCurrentPosition<mCommands.size()){
    		RotateAction r = mCommands.get(mCurrentPosition);
    		rotate(r.getLayer(), r.getDir());
    		mCurrentPosition++;
    	}
    }
    public void backwardRotate(){
    	if(mCurrentPosition>0){
    		mCurrentPosition--;
    		RotateAction r = mCommands.get(mCurrentPosition);
    		rotate(r.getLayer(), reverseDir(r.getDir()));
    	}
    }
    public void play(){
    	for(;mCurrentPosition<mCommands.size();){
    		forwardRotate();
    	}
    }
    public void reset(){
    	for(;mCurrentPosition>0;){
    		backwardRotate();
    	}
    }
    
    private int mRotateFaceCnt=0;
    private void incRFCnt(){
    	mRotateFaceCnt++;
    }
    private void resetRFCnt(){
    	mRotateFaceCnt=0;
    }
    private int getRFCnt(){
    	return mRotateFaceCnt;
    }
    private final static int ROTATEFACEUPLIMIT = 3;
    //members of logging coordinates of keys being down
	Point downPoint = invalidpt, movePoint = invalidpt;	
	// Base rotation, before drag
	private Quaternion rotation = new Quaternion(-0.173f, 0.381f, 0.072f, 0.905f);
	// The amount of rotation to add as part of drag
	private Quaternion dragRotation = new Quaternion(-0.173f, 0.381f, 0.072f, 0.905f);
	// Equal to rotation*dragRotation.
	private Quaternion intermediateRotation = new Quaternion(-0.173f, 0.381f, 0.072f, 0.905f);	
	// The current axis about which the object is being rotated
	private Vector3f spinAxis = new Vector3f(0,0,0);
	
	public final static int FEATURE_ROTATEFACE = 0x00000001;
	public final static int FEATURE_ROTATECUBE = 0x00000002;
	private int featureFlag = FEATURE_ROTATEFACE|FEATURE_ROTATECUBE;
	public void enableFeature(int feature, boolean bSwitch){
		if(bSwitch)
			featureFlag |= feature;
		else
			featureFlag &= ~feature;
	}
	public boolean isFeatureEnable(int feature){
		if((feature&featureFlag)!=0)
			return true;
		else 
			return false;
	}
	
    public void trackCoords(Point pt, int motionstate){
    	switch(motionstate){
    	case MotionEvent.ACTION_DOWN:
    		if(getRenderState() == STOP){
    			downPoint = translate(pt);
    			Log.i(TAG, "downPoint = (" + downPoint.x +"," + downPoint.y + ")");
    			if(mMatrixGrabber!=null){
    				//仅当matrixGrabber的ModelView和Projection Matrix被赋值后
    				//viewstate才可能从STOP转换到ROTATEFACEONCUBE3 或 ROTATECUBE3
    				Ray ray = pickRay(downPoint, mMatrixGrabber);
    				if(intersect(ray, startCoord, startV)&&isFeatureEnable(FEATURE_ROTATEFACE)) {
    					setRenderState(ROTATEFACEONCUBE3);
    					resetRFCnt();
    					Log.i(TAG, "start.x =" + startV.x);
    					Log.i(TAG, "start.y =" + startV.y);
    					Log.i(TAG, "start.z =" + startV.z);
    					Log.i(TAG, "startCoord[0]=" + startCoord[0]);
    					Log.i(TAG, "startCoord[1]=" + startCoord[1]);
    					Log.i(TAG, "startCoord[2]=" + startCoord[2]);
    				} else if(isFeatureEnable(FEATURE_ROTATECUBE)){
    					movePoint = translate(pt);
    					setRenderState(ROTATECUBE3);
    				}
    			} else {
    				//nothing will be done
    			}
    		} else {
    			//nothing will be done
    		}
    		break;
    	case MotionEvent.ACTION_MOVE:
   			if(getRenderState() == ROTATEFACEONCUBE3 && isFeatureEnable(FEATURE_ROTATEFACE)) {
   				if(getRFCnt()==ROTATEFACEUPLIMIT) {
   					movePoint = translate(pt);
   					Log.i(TAG, "movePoint = (" + movePoint.x +"," + movePoint.y + ")");
   					Ray ray = pickRay(movePoint, mMatrixGrabber);
   					boolean sect = intersect(ray, endCoord, endV);
   					Log.i(TAG, "trackcoords(...): " + "startcoords[1]=" + startCoord[1]);
   					Log.i(TAG, "trackcoords(...): " + "endCoord[1]=" + endCoord[1]);
   					if( sect && startCoord[1] == endCoord[1]){
   						//仅当end点的face和start点的face相同时，才进入此分支，即第二个判断
   						Log.i(TAG, "end.x =" + endV.x);
   						Log.i(TAG, "end.y =" + endV.y);
   						Log.i(TAG, "end.z =" + endV.z);
   						//determine the vector downPoint-->movePoint, and final dir
   						int[] sideanddir = new int[2];
   						getSideAndDir(endCoord[0], startV, endV, sideanddir);
   						int[] a = {sideanddir[0]};
   						rotate(a, sideanddir[1]);
   					}
   					resetRFCnt();
    				setRenderState(STOP);
   				} else {
   					incRFCnt();
   				}
   			} else if(getRenderState() == STOP) {
   				//nothing will be done   				
   			} else if(getRenderState() == ROTATECUBE3 && isFeatureEnable(FEATURE_ROTATECUBE)) {
   				movePoint = translate(pt);
   			} 
    		break;
    	case MotionEvent.ACTION_UP:
    		resetRFCnt();
    		
    		if(getRenderState() == ROTATECUBE3 && isFeatureEnable(FEATURE_ROTATECUBE)){
    			float rotateX = movePoint.x - downPoint.x;		        
    			float rotateY = movePoint.y - downPoint.y;
        
    			if (rotateX != 0 || rotateY != 0) {
    				spinAxis = new Vector3f(rotateY, -rotateX, 0);
    				double mag = spinAxis.magnitude();
    				spinAxis.normalize();

    				intermediateRotation.set(spinAxis, mag/90);
    				rotation.mulThis(intermediateRotation);
    			}
		
    			movePoint.x = downPoint.x = 0;
    			movePoint.y = movePoint.y = 0;
    		}
    		
    		setRenderState(STOP);
    		break;
    	}
    }
    
	private Quaternion currentRotation() {
		float rotateX = movePoint.x - downPoint.x;		        
        float rotateY = movePoint.y - downPoint.y;
        
        if (getRenderState()==ROTATECUBE3 && (rotateX != 0 || rotateY != 0) && isFeatureEnable(FEATURE_ROTATECUBE)) {
        	//Log.i(TAG+".currentRotation", "branch 0");
	       	spinAxis.set(rotateY, -rotateX, 0);
			double mag = spinAxis.magnitude();
			spinAxis.normalize();

	        intermediateRotation.set(spinAxis, mag/DRAG_SLOWING);
	        dragRotation.set(rotation);
	        dragRotation.mulThis(intermediateRotation);

	        return dragRotation;
        } else {
        	//Log.i(TAG+".currentRotation", "branch 1");
        	return rotation;
        }
	}
	
    private final static float vdifference=(float) 5.0E-5;
    //取得side and dir
    //Input：
    //1. whichcube  -  cube idx
    //2. start  -  whichcube上的起始点坐标
    //3. end  -  whichcube上的终止点坐标
    //Output：
    //1. ret
    //   ret【0】  -  side
    //   ret【1】  -  dir
    //说明：
    //1. whichcube上的start->end向量决定的side
    //2. 取得start->end向量的顺逆时针方向
    private void getSideAndDir(int whichcube, Vector4f start, Vector4f end, int[] ret){
    	int count=0, whichaxis=0; 
    	float[] absstart2end={Math.abs(end.x-start.x), Math.abs(end.y-start.y), Math.abs(end.z-start.z)};
    	float[] compared ={0.0f, 0.0f};

    	//判断是该tube的哪个side的逻辑
    	for(int i=0;i<absstart2end.length;i++){
    		Log.i(TAG, "getsideanddir(...): " + "absstart2end[" + i + "]= " +absstart2end[i]);
    		if(absstart2end[i]>vdifference){ //absstart2end[i]不是0
    			compared[count]=absstart2end[i];
    			count++;
    		}
    	}
    	float min=Math.min(compared[0], compared[1]);
    	for(int i=0;i<absstart2end.length;i++){
    		if(absstart2end[i]==min){
    			whichaxis = i;
    			break;
    		}
    	}
    	ret[0] = mTube.getCubeBelongTosideBy(whichcube,whichaxis);

    	//判断从某轴的正向来看的顺逆时针逻辑
    	float[] vstart={start.x, start.y, start.z};
    	float[] vend={end.x, end.y, end.z};
    	float cross;
    	if(whichaxis == Cube.axisX){
    		cross = vstart[1]*vend[2] - vend[1]*vstart[2];
    	} else if(whichaxis == Cube.axisY){
    		cross = vstart[2]*vend[0] - vend[2]*vstart[0];
    	} else {
    		cross = vstart[0]*vend[1] - vend[0]*vstart[1];
    	}
    	if(cross > 0.0f)
    		ret[1] = Tube.CCW;
    	else
    		ret[1] = Tube.CW;
    }
    
    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
    	mProjectionView[0] = 0;
    	mProjectionView[1] = 0;
    	mProjectionView[2] = w;
    	mProjectionView[3] = h;
    	Log.i(TAG+"::onSurfaceChanged", "width = " + w);
    	Log.i(TAG+"::onSurfaceChanged", "height = " + h);
        gl.glViewport(0, 0, w, h);
        /*
         * Set our projection matrix. This doesn't have to be done
         * each time we draw, but usually a new projection needs to
         * be set when the viewport is resized.
         */
        float ratio = (float) w / h;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        
        //there are 2 ways to configure projection matrix
        //1. gl.glFrustumf
        //2. GLU.gluPerspective
        //首选第2种方法
        
		// Calculate the aspect ratio of the window
		GLU.gluPerspective(gl, 45.0f, ratio, 3.0f, 100.0f);
		//gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
	}
    
	private void setUpCamera(GL10 gl) {
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		//GLU.gluLookAt(gl, mfEyeX, mfEyeY, mfEyeZ, mfCenterX, mfCenterY, mfCenterZ, 0, 1, 0);//系统提供
		GLU.gluLookAt(gl, 0,0,Tube.size2distance(Tube.size), 0,0,0, 0,1,0);

	}
	
    @Override
    public void onDrawFrame(GL10 gl) {
    	gl.glClearColor(red, green, blue, alpha);  //定义buffer颜色
    	gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);        //使用上述定义
    
    	setUpCamera(gl);
    	
		//做model变换
    	Quaternion q = currentRotation(); 
    	//Log.i(TAG+".onDrawFrame", ""+q.getX()+";"+q.getY()+";"+q.getZ()+";"+q.getW());
    	float[] m = q.toMatrix();
    	gl.glMultMatrixf(m, 0);
                
        // draw the cube3
        mTube.draw(gl, mTextureInterface);
        
        mMatrixGrabber.getCurrentProjection(gl);	//设置matrixGrabber.mProjection
        mMatrixGrabber.getCurrentModelView(gl);	//设置matrixGrabber.mModelView
    }
    
	private Ray pickRay(Point pt, MatrixGrabber grabber){
	    float[] obj=new float[3];
		Projector gProjector = new Projector();
		Ray pickRay=new Ray();
		
	    //取得Ray的前点
    	gProjector.gluUnProject(pt.x, pt.y, 0.0f, 
    					grabber.mModelView,0, grabber.mProjection,0, 
    					mProjectionView, 0, obj, 0);
/*        	
    	Log.i(TAG, "ray origin = (" + Float.valueOf(obj[0]).toString() + "," + 
    								  Float.valueOf(obj[1]).toString() + "," +
    								  Float.valueOf(obj[2]).toString() + ")" );
*/        								  
    	pickRay.mvOrigin.set(obj[0], obj[1], obj[2]);

		//取得Ray的后点
		gProjector.gluUnProject(pt.x, pt.y, 1.0f, 
						 grabber.mModelView, 0,grabber.mProjection, 0, 
						 mProjectionView, 0, obj, 0);
/*    		
    	Log.i(TAG, "ray end = (" + Float.valueOf(obj[0]).toString() + "," + 
				  				   Float.valueOf(obj[1]).toString() + "," +
				  				   Float.valueOf(obj[2]).toString() + ")" );
*/					  				   
		pickRay.mvDirection.set(obj[0], obj[1], obj[2]);
		
		//后点-前点，即得gPickRay的向量
		pickRay.mvDirection.sub(pickRay.mvOrigin);
		//最后做成范式
		pickRay.mvDirection.normalize();
		
		return pickRay;
	}
	
    //intersect() returns 2 variables
    //1. cubecoords
    //   cubecoords[0]  -  whichcube
    //   cubecoords[1]  -  whichcube's whichvisibleface
    //   cubecoords[2]  -  whichcube's whichvisibleface's whichtriface
    //2. sectpt
    //   sectpt  -  相交点的坐标
    static Vector3f[] v = {new Vector3f(), new Vector3f(), new Vector3f(),};
    static Vector4f location = new Vector4f();
    boolean intersect(Ray ray, int[] cubecoords, Vector4f sectpt){
    	Vertex[] vertexs;
		float closeDis = 10000.0f;
		int count=0;
    	
		Cube[] cubes = mTube.getCubes();
    	for(int i=0;i<cubes.length;i++){
    		Cube cube = cubes[i];
    		int[] visiblefaces = Tube.getVisibleFaces(i);
    		for(int j=0;j<visiblefaces.length;j++){
    			QuadFace quadface = cube.getface(visiblefaces[j]);
    			for(int k=0;k<2;k++){
    				TriFace tface = quadface.getface(k);
    				vertexs = tface.getvertex();
    				v[0].set(vertexs[0].x, vertexs[0].y, vertexs[0].z);
    				v[1].set(vertexs[1].x, vertexs[1].y, vertexs[1].z);
    				v[2].set(vertexs[2].x, vertexs[2].y, vertexs[2].z);
    				if(ray.intersectoneside(v[0], v[1], v[2], location)){
						count++;
		    			Log.i(TAG, "intersect(...): " + "location.w=" + location.w);
						if(closeDis > location.w) {
    						closeDis = location.w;
    						cubecoords[0]=i;
    						cubecoords[1]=visiblefaces[j];
    						cubecoords[2]=k;
    						sectpt.set(location);
    					}
    				}
    			}
    		}
    	}
    	Log.i(TAG, "intersect(...): " + "count =" + count);
    	if(count > 0)
    		return true;
    	else
    		return false;
    }
}
