package oms.cj.ads;

import com.waps.AppConnect;
import com.waps.UpdatePointsNotifier;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

public class WapsNode implements UpdatePointsNotifier{
	private final static String TAG = "WapsNode";
	
	private final static String PREFNAME = "TRIALLIMIT";
	private final static String ITEM_UNLOCKED = "UNLOCKED";
	private final static String ITEM_TRYCOUNT = "TRYCOUNT";
	private final static int TRIALLIMIT = 5;
	private final static int UNLOCKTHRESHOLD = 300;
	
    Handler mHandler = null;
    final DirectToOfferDlg mOfferDlg = new DirectToOfferDlg();
    final GetPointsErrorDlg mErrorDlg = new GetPointsErrorDlg();
	private Activity mAct;
	private IHackedActions mAction;
	
	public WapsNode(Activity act, IHackedActions action, Handler h){
		mAct = act;
		mAction = action;
		mHandler =  h;
	}
	
	public WapsNode(Activity act, IHackedActions action){
		mAct = act;
		mAction = action;
		mHandler = new Handler();
	}
	
    class DirectToOfferDlg implements Runnable {
    	public String msg="";
    	
		@Override
		public void run() {
        	Dialog dialog=new AlertDialog.Builder(mAct)
            .setMessage(msg)
            .setPositiveButton(R.string.getCoinsNow, new DialogInterface.OnClickListener(){
				@Override
            	public void onClick(DialogInterface dialog, int whichButton){
            		AdGlobals.getInstance().showWapsOffer(mAct);
            	}
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
            .create();
        	dialog.show();	
		}
    }
    
    class GetPointsErrorDlg implements Runnable {
    	public String msg = "";
    	
		@Override
		public void run() {
        	Dialog dialog=new AlertDialog.Builder(mAct)
            .setMessage(msg)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
				@Override
            	public void onClick(DialogInterface dialog, int whichButton){
            	}
            })
            .create(); 
        	dialog.show();		
		}
    }
    
    class Spend implements UpdatePointsNotifier {
		@Override
		public void getUpdatePoints(String arg0, int arg1) {
			String out = String.format("%d%s is spent", arg1, arg0);
			Log.i(TAG+".Spend.getUpdatePoints", out);
		}

		@Override
		public void getUpdatePointsFailed(String arg0) {
			String out = String.format("%s", arg0);
			Log.e(TAG+".Spend.getUpdatePointsFailed", out);			
		}
    }
    
	@Override
	public void getUpdatePoints(String arg0, int arg1) {
		String out = String.format("arg0=%s, arg1=%d", arg0, arg1);
		Log.i(TAG+".getUpdatePoints", out);
		
		// check if there are enough coins
		// only allow to play if there are coins larger than limit
		if(arg1>=UNLOCKTHRESHOLD){
			SharedPreferences settings = mAct.getSharedPreferences(PREFNAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(ITEM_UNLOCKED, true);
			editor.commit();
			AppConnect.getInstance(mAct).spendPoints(UNLOCKTHRESHOLD, new Spend());
			
			mAction.playAfterPassCheck();
		} else
			directToOfferList(arg1);
	}

	//show dialog: no enough coins, hint to offer list
	public void directToOfferList(int currentCoins){
		Resources res = mAct.getResources();
		String offerMsg = res.getString(R.string.directToOfferList, UNLOCKTHRESHOLD, currentCoins);
		mOfferDlg.msg = offerMsg;
		if(mHandler!=null)
			mHandler.post(mOfferDlg);
	}

	@Override
	public void getUpdatePointsFailed(String arg0) {
		String out = String.format("arg0=%s", arg0);
		Log.i(TAG+".getUpdatePoints", out);

		mErrorDlg.msg = arg0;
		if(mHandler!=null)
			mHandler.post(mErrorDlg);
	}
	
	public boolean checkQualificationToContinue(){
		boolean bContinue = false;
		
		SharedPreferences settings = mAct.getSharedPreferences(PREFNAME, 0);
		boolean bUnlocked = settings.getBoolean(ITEM_UNLOCKED, false);
		int count = settings.getInt(ITEM_TRYCOUNT, 0);
		if(bUnlocked) {
			bContinue = true;
		} else if(count<TRIALLIMIT) {
			bContinue = true;
			count++;
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(ITEM_TRYCOUNT, count);
			editor.commit();
		} else {
			bContinue = false;
		}
		
		return bContinue;
	}
	
}
