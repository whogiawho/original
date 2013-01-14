package oms.cj.tube.camera.self;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import oms.cj.tube.camera.CamLayer;

public class RecognitionManager {
	private final static String TAG = "RecognitionManager";
	private final static int ONEMATCHFOUND = 0;
	
	private static int MAXTHREADS = 5;
	private static Thread[] sThreads = new Thread[MAXTHREADS];
	private static RecognitionManager mManager = null;
	
	private static Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		switch(msg.what){
    		case ONEMATCHFOUND:
    			Log.i(TAG+".mHandler.handlMessage", "one match found!");
    			for(int i=0;i<sThreads.length;i++)
    				if(sThreads[i]!=null){
    					sThreads[i].interrupt();
    				}
    			break;
    		}
    	}
	};
	
	public static RecognitionManager getInstance(){
		if(mManager==null){
			mManager = new RecognitionManager();
			for(int i=0;i<sThreads.length;i++)
				sThreads[i]=null;
		}
		
		return mManager;
	}

	public synchronized MatchThread getEmptySlot(byte[] data, IRecognition iWay, Handler h){
		int emptySlot=-1;
		MatchThread mt = null;
		
		for(int i=0;i<sThreads.length;i++){
			if(sThreads[i]==null){
				emptySlot = i;
				break;
			}
		}
		if(emptySlot!=-1){
			mt = new MatchThread(data, iWay, h, emptySlot);
			sThreads[emptySlot] = mt;
		}
		
		return mt;
	}
	
	public void recognize(CamLayer layer, IRecognition iWay, Handler h){
		byte[] data;
		data = layer.removeElementFromQueue();
		
		MatchThread mt = getEmptySlot(data, iWay, h);
		if(mt!=null)
			mt.start();
	}
	
	private class MatchThread extends Thread {
		private Handler mFeedback;
		private IRecognition mWay;
		private byte[] mRawData;
		private int mSlot;
		
		MatchThread(byte[] data, IRecognition way, Handler h, int slot){
			mRawData = data;
			mWay = way;
			mFeedback = h;
			
			mSlot = slot;
		}
		
		@Override
		public void run() {
			boolean bMatch = mWay.match(mRawData, this);
			Message msg = new Message(); 
			if(bMatch){
				msg.what = Snapshot.SCANMATCHOK;
				int[] colors = mWay.getMatchColor();
				msg.obj = colors;
				mHandler.sendEmptyMessage(ONEMATCHFOUND);
			} else {
				msg.what = Snapshot.SCANMATCHFAIL;
			}
			mFeedback.sendMessage(msg);
			
			synchronized(RecognitionManager.this){
				sThreads[mSlot] = null;		
			}
		}
		
	}

}
