package oms.cj.tube.solver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import oms.cj.tube.Globals;
import oms.cj.tube.R;
import org.kociemba.twophase.Search;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AdvancedDataLoader {
	private final static String TAG = "AdvancedDataLoader";
	private final static int MAX_PROGRESS = 100;
    public final static int LOADINGCOMPLETE = 0;
	public final static int LOADING = 1;
	
	private Context mContext;
	private IDataLoader mLoader;
	
	AdvancedDataLoader(Context context, IDataLoader loader){
		mLoader = loader;
		mContext = context;
	}

	private void loadAssets(){
		InstallZipAssets cpAssets = new InstallZipAssets(mContext);
		cpAssets.initAssets(Globals.assetDir);
	}
	
	
    public void loading(){
		// show a dialog with R.layout.loading
    	final ProgressDialog progressDlg = new ProgressDialog(mContext);
    	progressDlg.setTitle(R.string.loading);
    	progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	progressDlg.setMax(MAX_PROGRESS);
    	progressDlg.setCancelable(false);
    	progressDlg.show();
    	progressDlg.setProgress(0);
    	
    	Handler handler = new Handler(){
        	@Override
        	public void handleMessage(Message msg){
        		switch (msg.what){
        		case LOADINGCOMPLETE:
        			//cancel the dialog and call advancedSolve()
        			progressDlg.dismiss(); 
        			mLoader.onCompleteLoadingData();
        			break;
        		case LOADING:
        			Log.i(TAG+".handler.handleMessage", "LOADING is received!");
        			progressDlg.setProgress(msg.arg1);
        			break;
        		}
        	}
    	};
		LoadingThread t = new LoadingThread(handler);
		t.start();
    }

	private class LoadingThread extends Thread {
		Handler mHandler;
		LoadingThread(Handler handler){
			mHandler = handler;
		}
		
		private void sendMessage(int type, int progress){
			Message msg = new Message();
			msg.what = type;
			msg.arg1 = progress;
			mHandler.sendMessage(msg);
		}
		
		@Override
		public void run() {
			try {
		    	loadAssets();
		    	sendMessage(LOADING, 5);
				if(!Search.bLoaded){
					FileInputStream fis = mContext.openFileInput(Globals.advancedAsset);
					ObjectInputStream objectIn = new ObjectInputStream(fis);
					Search.restoreFromFile(objectIn, mHandler);
					Search.bLoaded=true;
				}
				Log.i(TAG+".run", "complete loading coordcube!");
				mHandler.sendEmptyMessage(LOADINGCOMPLETE);
			} catch (OptionalDataException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
