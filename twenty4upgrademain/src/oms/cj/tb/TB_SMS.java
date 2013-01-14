package oms.cj.tb;

import oms.cj.twenty4upgrade.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TB_SMS implements View.OnClickListener{
	private final static String smsport = "1065880004";
	private final static String TAG = "TB_SMS";
    private final static String SENT = "SMS_SENT";
    private final static String DELIVERED = "SMS_DELIVERED";
	private final static int HITLIMIT = 10;
	
	private Activity mActivity;
	private IBetweenMainAndGlobals mI;
	private String mTBid, mLoginId, mPayCode;
	
	public TB_SMS(Activity act, IBetweenMainAndGlobals i, String TBid, String LoginId, String PayCode){
		mActivity = act;
		mI = i;
		mTBid = TBid;
		mLoginId = LoginId;
		mPayCode = PayCode;
	}
	
	//helper 1 to support T&B
	public void promptToBuy(Double price){
		mActivity.setContentView(R.layout.tandb);
    	Button button;
    	button = (Button) mActivity.findViewById(R.id.buy);
    	button.setOnClickListener(this);
    	button = (Button) mActivity.findViewById(R.id.quitapp);
    	button.setOnClickListener(this);
    	//set the work's price
    	TextView tvPrice = (TextView) mActivity.findViewById(R.id.price);
    	tvPrice.setText("本次计费" + price + "元!");
    	
    	//register sms broadcast handler
        //---when the SMS has been sent---
    	mActivity.registerReceiver(new SendHandler(), new IntentFilter(SENT));
        //---when the SMS has been delivered---
    	mActivity.registerReceiver(new DeliveredHandler(), new IntentFilter(DELIVERED));        
	}
	
	//helper 2 to support T&B
	public boolean checkQualificationToContinue(){
		boolean bContinue = false;
		
		SharedPreferences settings = mActivity.getSharedPreferences("HitLimit", 0);
		boolean bBought = settings.getBoolean("Bought", false);
		int count = settings.getInt("TryCount", 0);
		if(bBought) {
			bContinue = true;
		} else if(count<HITLIMIT) {
			bContinue = true;
			count++;
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("TryCount", count);
			editor.commit();
		} else {
			bContinue = false;
		}
		
		return bContinue;
	}
	
	//helper 3 to support T&B
	private void exitApp(){
		android.os.Process.killProcess(android.os.Process.myPid());	
	}
	
	//helper 4 to support T&B
	private void reqeustToBuy(){
		
        //String smsport = "5556"; 
		String smsrule = mTBid + "#" + mLoginId + "#" + mPayCode;
		
        //send the buy request to MM 
		Intent i1 = new Intent(SENT); 
        PendingIntent sendintent = PendingIntent.getBroadcast(mActivity, 0, i1, 0);
		Intent i2 = new Intent(DELIVERED); 
        PendingIntent deliveryintent = PendingIntent.getBroadcast(mActivity, 0, i2, 0);
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(smsport, null, smsrule, sendintent, deliveryintent); 
	}
	
	//helper 5 to support T&B
	private void onBuySuccess(){
		SharedPreferences settings = mActivity.getSharedPreferences("HitLimit", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("Bought", true);
		editor.commit();
		mI.normalFlow();
	}
	
    //helper 6 to support T&B
	class SendHandler extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
        	int r = getResultCode();
        	Log.i(TAG, "requestToBuy::SMS_SENT::onReceive():" + "resultCode =" + r);
            switch (r) {
                case Activity.RESULT_OK:
//                	onBuySuccess(); setTitle();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                case SmsManager.RESULT_ERROR_NULL_PDU:
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                default:
//                	onBuyFailure();
                    break;
            }
		}
	}
	
	//helper 7 to support T&B
	class DeliveredHandler extends BroadcastReceiver{
        @Override
        public void onReceive(Context arg0, Intent arg1) {
        	int r = getResultCode();
        	Log.i(TAG, "requestToBuy::SMS_DELIVERED::onReceive():" + "resultCode =" + r);
            switch (r) {
                case Activity.RESULT_OK:
                	onBuySuccess(); setTitle();
                    break;
                case Activity.RESULT_CANCELED: 
                	onBuyFailure();
                default:
                    break;                        
            }
        }
	} 
	
	//helper 8 to support T&B
	private void onBuyFailure(){
		showMessage("购买失败，请重试！");
	}
	
	//helper 9 to support T&B
	private boolean checkBought(){
		SharedPreferences settings = mActivity.getSharedPreferences("HitLimit", 0);
		boolean bBought = settings.getBoolean("Bought", false);
		return bBought;
	}
	
	//helper 10 to support T&B
	private void showMessage(String message){
    	Dialog dialog=new AlertDialog.Builder(mActivity)
			.setTitle("提示对话框")
			.setMessage(message)
			.setPositiveButton("OK", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				return;
			}
		})
		.create();
    	dialog.show();	
	}
	
	//helper 11 to support T&B
	public void setTitle(){
		String title = mActivity.getString(R.string.app_name);
		
		SharedPreferences settings = mActivity.getSharedPreferences("HitLimit", 0);
		boolean bBought = settings.getBoolean("Bought", false);
		int tryCount = settings.getInt("TryCount", 0);
		if(!bBought){
			title += String.format("-还可试用%d次", HITLIMIT-tryCount);
		}
		mActivity.setTitle(title);
	}

	//helper 12 to support T&B
	@Override
	public void onClick(View v) {
		int id = v.getId();
		Log.i(TAG, "onClick():" + "id =" + id);
		switch(id){
		case R.id.buy:
			if(!checkBought())
				reqeustToBuy();
			else 
				showMessage("您已经购买了该应用！");
			return;
		case R.id.quitapp:
			Log.i(TAG, "onClick():" + "cancel is clicked!");
			exitApp();
			return;
		}		
	}
}
