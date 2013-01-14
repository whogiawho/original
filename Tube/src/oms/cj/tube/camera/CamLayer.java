package oms.cj.tube.camera;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import oms.cj.tube.Globals;
import oms.cj.tube.YUV.*;
import oms.cj.tube.camera.self.IRecognition;
import oms.cj.tube.camera.self.ISelfCameraPicture;
import oms.cj.tube.camera.self.RecognitionManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * This class handles the camera. In particular, the method setPreviewCallback
 * is used to receive camera images. The camera images are not processed in
 * this class but delivered to the GLLayer. This class itself does
 * not display the camera images.
 * 
 * @author Niels
 *
 */
public class CamLayer extends SurfaceView implements SurfaceHolder.Callback {
    private final static String TAG = "CamLayer";
    
    private Camera mCamera;
    private boolean isPreviewRunning = false;
    private ICameraPicture mICameraPicture;
    private int mRealPreviewWidth, mRealPreviewHeight;
    private SurfaceHolder mHolder;
    private byte[] mPreviewRGBData = null;
    private int[] mBitmapData = null;
    private boolean mEnablePreview = true;
    
    public void setEnablePreview(boolean bEnable){
    	mEnablePreview = bEnable;
    }
    
    private void init(Context context){
        mICameraPicture = (ICameraPicture) context;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        Log.i(TAG+".init", "being called!");
        
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public CamLayer(Context context) {
        super(context);
        init(context);
    }

    public CamLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
    	Log.i(TAG+".surfaceCreated", "being called!");

    	synchronized(this) {
	        mCamera = Camera.open();

	    	try {
				mCamera.setPreviewDisplay(holder);
				mCamera.setPreviewCallback(mPreviewCallback);
			} catch (IOException e) {
				Log.e(TAG+".surfaceCreated", "mCamera.setPreviewDisplay(holder);");
			}
    	}
	}

    public void surfaceDestroyed(SurfaceHolder holder) {
    	Log.i(TAG+".surfaceDestroyed", "being called!");
    	
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
    	synchronized(this) {
	    	try {
		    	if (mCamera!=null) {
		    		mCamera.setPreviewCallback(null);
		    		mCamera.stopPreview();  
		    		isPreviewRunning=false;
		    		mCamera.release();
		    	}
	    	} catch (Exception e) {
				Log.e(TAG+".surfaceDestroyed", e.getMessage());
	    	}
    	}
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	Log.i(TAG+".surfaceChanged", "being called!");
    	Log.i(TAG+".surfaceChanged", "w="+w); 
    	Log.i(TAG+".surfaceChanged", "h="+h); 
		if (isPreviewRunning) {
			mCamera.stopPreview();
		}
		
    	Camera.Parameters p = mCamera.getParameters();  
    	setOptimalSize(p, w, h, SIZEOFPREVIEW);
    	setOptimalSize(p, w, h, SIZEOFPICTURE);
    	mCamera.setParameters(p);

    	printCameraParams(p);
    	
    	mCamera.startPreview();
    	isPreviewRunning = true;
    }
    
    public final static int SIZEOFPICTURE = 0;
    public final static int SIZEOFPREVIEW = 1;
    private void setOptimalSize(Camera.Parameters p, int w, int h, int type){
    	double ratio = (double)w/(double)h;
    	double minDiff= Double.MAX_VALUE;
    	
    	List<Camera.Size> l = null;
    	switch(type){
    	case SIZEOFPICTURE:
    	default:
    		l = p.getSupportedPictureSizes(); 
    		break;
    	case SIZEOFPREVIEW:
    		l = p.getSupportedPreviewSizes();
    		break;
    	}
 
    	Camera.Size optimal=null;
    	
    	if(l!=null){
        	for(Camera.Size s: l){
        		if(Math.abs((double)s.width/(double)s.height-ratio)<minDiff){
        			minDiff = Math.abs((double)s.width/(double)s.height-ratio);
        			optimal = s;
        		}
        	}
        	
        	switch(type){
        	case SIZEOFPICTURE:
        	default: 
        		p.setPictureSize(optimal.width, optimal.height);
        		break;
        	case SIZEOFPREVIEW:
        		p.setPreviewSize(optimal.width, optimal.height);
        		break;
        	}    		
    	}
    }
    private void printCameraParams(Camera.Parameters p){
    	List<Camera.Size> l = p.getSupportedPictureSizes();
    	if(l!=null){
        	for(int i=0;i<l.size();i++){
        		Camera.Size s = l.get(i);
        		Log.i(TAG+".printCameraParams", "pictureSize:"+"w="+s.width+";h="+s.height);
        	}    		
    	}
    	
    	if(l!=null){
        	l = p.getSupportedPreviewSizes();
        	for(int i=0;i<l.size();i++){
        		Camera.Size s = l.get(i);
        		Log.i(TAG+".printCameraParams", "previewSize:"+"w="+s.width+";h="+s.height);
        	}    		
    	}
    	
    	
        List<Integer> li = p.getSupportedPreviewFrameRates();
        if(li!=null){
        	for(Integer i: li){
        		Log.i(TAG+".printCameraParams", "PreviewFrameRates:"+"i="+i);
        	}    		
    	}
    	
        /* Moto Defy Android 2.1 will crash here, so skip it
    	li = p.getSupportedPreviewFormats();
    	if(li!=null){
        	for(Integer i: li){
        		Log.i(TAG+".printCameraParams", "PreviewFormat:"+"i="+i);
        	}    		
    	}*/
    }

    private final static int YUVQUEUEMAX =  1;
    private ArrayList<byte[]> mYUVDataQueue = new ArrayList<byte[]>();
    PreviewCallback mPreviewCallback = new PreviewCallback() {
    	
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			
			if(mEnablePreview){
				Camera.Parameters p = camera.getParameters();
				Camera.Size s = p.getPreviewSize();
				//Log.i(TAG+".mPreviewCallback.onPreviewFrame", "w="+s.width+";h="+s.height);
				mRealPreviewWidth = s.width;
				mRealPreviewHeight = s.height;
				
				if(mPreviewRGBData==null)	
					mPreviewRGBData = new byte[mRealPreviewWidth*mRealPreviewHeight*3];
				
				if(mBitmapData==null)
					mBitmapData = new int[mRealPreviewWidth*mRealPreviewHeight];

				synchronized(CamLayer.this){
					if(mYUVDataQueue.size()==YUVQUEUEMAX){
						mYUVDataQueue.remove(0);
					}
					mYUVDataQueue.add(data);
				}
				
				//below codes assume that recognition behavior happened every PreviewFrame rate
				//another way is to new a Thread separately to do recognition
				int wayType = mICameraPicture.getWayType();
				if(Snapshot.typeIsSelf(wayType)){
					IRecognition iWay = null;
					//SELF type logic came here;
					switch(wayType){
					case ICameraPicture.TYPE2:
						iWay = new oms.cj.tube.camera.self.way1.SideRecognition();
					default:
						break;
					case ICameraPicture.TYPE3:
						break;
					}
					
					Handler h = ((ISelfCameraPicture)mICameraPicture).getHandler();
					RecognitionManager m = RecognitionManager.getInstance();
					m.recognize(CamLayer.this, iWay, h);
				}				
			}
		}
    	
    };
    
    public synchronized byte[] removeElementFromQueue(){
    	byte[] element = null;
    	
    	if(mYUVDataQueue!=null&&mYUVDataQueue.size()!=0)
    		element = mYUVDataQueue.remove(0);
    	
    	return element;
    }
    
    public void restartPreview(){
		mCamera.setPreviewCallback(mPreviewCallback);
		mCamera.startPreview(); 
		isPreviewRunning=true;
    }
    public void takePicture(){
    	Log.i(TAG+".takePicture", "being called!");
    	onPictureTaken(null, null);
    	Log.i(TAG+".takePicture", "call ended!");
    }
    
    private int count = 0;
    public void onPictureTaken(byte[] data, Camera camera) {  
        Log.i(TAG+".mPictureCallback.onPictureTaken", "mRealPreviewWidth="+mRealPreviewWidth);
        Log.i(TAG+".mPictureCallback.onPictureTaken", "mRealPreviewHeight="+mRealPreviewHeight);
        
        if (mYUVDataQueue.size() != 0) {
        	byte[] previewData = null;
        	
        	synchronized(CamLayer.this){
        		previewData = mYUVDataQueue.remove(0); 
        		Decoder decoder = new Decoder1();
    			//decode data(YUV) format to Bitmap
        		decoder.decode2RGB(mBitmapData, mPreviewRGBData, 
        				previewData, mRealPreviewWidth, mRealPreviewHeight);
        		
        		if(Globals.bSaveSnapshot){
        			try {
                		//save the preview image to sdcard
                		Bitmap image = Bitmap.createBitmap(mBitmapData, mRealPreviewWidth, 
            					mRealPreviewHeight, Bitmap.Config.ARGB_8888); 
                		FileOutputStream out = new FileOutputStream(Globals.sdcardDir+"/snapshot"+count+".bmp");
        			    image.compress(Bitmap.CompressFormat.PNG, 100, out);
                		count++;
        			} catch (Exception e) {
        			       e.printStackTrace();
        			}            			
        		}
        	}
        	
        	mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewRunning=false;
            mICameraPicture.onPictureTaken(previewData, mPreviewRGBData, mRealPreviewWidth, mRealPreviewHeight);
        }
    }
}
