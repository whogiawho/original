package oms.cj.ads;

import com.rlm.client.android.MMApp;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class MMInterstitialActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
       
    	super.onCreate(savedInstanceState);

    	setContentView(R.layout.mmfullscreen);
    	
    	MMApp app = new MMApp("match1.mmarket.com", // Delivery Server
    			this.getApplicationContext(),
    			"MobileApps.TestUser.DefaultApp.DefaultPage", // Context Full Name
    			"MOBILE_APP_INTERSTITIAL_IMAGE", // Place Holder Name
    			new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, Gravity.TOP));

    	FrameLayout frame = (FrameLayout)findViewById(R.id.frame);
    	
    	app.showFirstPageAd(this, frame);
    }

}
