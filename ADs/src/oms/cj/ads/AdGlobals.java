package oms.cj.ads;

import java.util.Random;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;
import com.rlm.client.android.MMApp;
import com.rlm.client.android.MMPage;
import com.rlm.client.android.MMPlaceHolder;
import com.waps.AppConnect;
import com.waps.AppLog;
import com.waps.MiniAdView;
import com.wooboo.adlib_android.ImpressionAdView;
import net.youmi.android.AdManager;
import net.youmi.android.AdView;

public class AdGlobals {
	public final static String TAG = "AdGlobals";

    public final static int WOOBOO = 0;
	public final static int YOUMI = 1;
	public final static int WOSTORE = 2;
	public final static int MM = 3;
	public final static int WAPS = 4;
	public final static String[] adTypeString = {
		"WOOBOO",
		"YOUMI",
		"WOSTORE",
		"MM",
		"WAPS",
	};
	
	//single instance 
	private static AdGlobals mSelf = null;
	public static AdGlobals getInstance(){
		if(mSelf==null)
			mSelf = new AdGlobals();
			
		return mSelf; 
	}
	
    private final Random mRandom;
    private IAd mAd;
	//2 switches to decide:
	//1. show ad or not            			(adSwitch)
	//2. show which type of ad        		(adType)
    //   adType is decided from adPercents
    public final boolean adSwitch = true;
    public final int[] adPercents = {
    	WOOBOO,
    	WOOBOO,
    	WOOBOO,
    	WOOBOO,
    	WOOBOO,
    	WOOBOO,
    	WOOBOO,
    	WOOBOO,
    	WOOBOO,
//    	WAPS,
//      YOUMI,    	
//    	WOSTORE,
//	MM,
    };
	public final boolean wapsAdSwitch = true;
	public final boolean the9Switch = false;
    
	protected AdGlobals() {
		mRandom = new Random();
	}
	public void init(String id, String pass, Context ctx){
    	if(adSwitch) {
    		//youmi
    		AdManager.init(id, pass, 31, false);
    		
    		//waps 
            AppLog.enableLogging(true);
            
            //adInterface initialized
            mAd = new AdInterface();
            mAd.setViewStub(R.id.adviewstub);
            mAd.addAdContainer(AdGlobals.WOSTORE, R.id.wostoreAdContainer);
            mAd.addAdContainer(AdGlobals.MM, R.id.mmAdContainer);
            mAd.addAdContainer(AdGlobals.WAPS, R.id.wapsAdContainer);
            String out = String.format("R.id.wostoreAdContainer=0x%x\nR.id.mmAdContainer=0x%x\nR.id.wapsAdContainer=0x%x\n", 
            		R.id.wostoreAdContainer,
            		R.id.mmAdContainer,
            		R.id.wapsAdContainer);
            Log.i(TAG+".init", out);
            //MM
            initMM(ctx);
    	}
	}

    public IAd getAdInterface(){
    	return mAd;
    }
    
    public int getAdType(){
    	int idx = mRandom.nextInt(adPercents.length);
    	int adType = adPercents[idx];
    	
    	return adType;
    }
    public int getStubLayout(int adType){
		switch(adType){
		case WOOBOO:
		default:
			return R.layout.woobooadview;
		case YOUMI:
			return R.layout.youmiadview;
		case WAPS:
			return R.layout.wapsadview;
		}    	
    }
    public int getAdLayoutID(){
    	int adType = getAdType();
    	return getStubLayout(adType);
    } 

    public View inflateContentView(Context context, int layoutId, int viewStubId){
    	return inflateContentView(context, layoutId, viewStubId, R.id.nullID, R.id.nullID, R.id.nullID);
    }
    public View inflateContentView(Context context, int layoutId, IAd adInterface){
    	return inflateContentView(context, layoutId, 
    			adInterface.getViewStub(),
    			adInterface.getAdContainer(AdGlobals.WOSTORE),
    			adInterface.getAdContainer(AdGlobals.MM),
    			adInterface.getAdContainer(AdGlobals.WAPS));
    }
    public void inflateAdView(Context context, View view, IAd adInterface){
    	inflateAdView(context, view, 
    			adInterface.getViewStub(),
    			adInterface.getAdContainer(AdGlobals.WOSTORE),
    			adInterface.getAdContainer(AdGlobals.MM),
    			adInterface.getAdContainer(AdGlobals.WAPS));
    }

    private View inflateContentView(Context context, int layoutId, int viewStubId, 
    		int wostoreContainer, int mmContainer, int wapsContainer){
    	View view;

		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflator.inflate(layoutId, null);
		
		inflateAdView(context, view, viewStubId, wostoreContainer, mmContainer, wapsContainer);
				
    	return view;
    }
    //               ad container        stub layout
    //WOOBOO           no                   yes
    //YOUMI            no                   yes
    //WAPS             yes                  yes
    //MM               yes                  no
    //WOSTORE          yes                  no
    private void inflateAdView(Context context, View view,
    		int viewStubId, int wostoreContainer, int mmContainer, int wapsContainer){
		if(adSwitch){
			int adType = getAdType();
			String out = String.format("adType=%s", AdGlobals.adTypeString[adType]);
			Log.i(TAG+".inflateAdView", out);
			switch(adType){
			case WOOBOO:
			case YOUMI:
			case WAPS:
				ViewStub adStub = (ViewStub) view.findViewById(viewStubId);
				if(adStub!=null){
					int layout = getStubLayout(adType);
					adStub.setLayoutResource(layout);
					adStub.setVisibility(View.VISIBLE);
				}
				//special codes for WAPS
				if(adType==WAPS){
			        LinearLayout miniLayout =(LinearLayout)view.findViewById(wapsContainer);
			        new MiniAdView(context, miniLayout).DisplayAd(10);//10秒刷新一次
				}
				break;
			case MM:
				//mm
				FrameLayout frame = (FrameLayout) view.findViewById(mmContainer);
				out = String.format("mmContainer=0x%x", mmContainer);
				Log.i(TAG+".inflateAdView", out);
				if(frame!=null){
					Log.i(TAG+".inflateAdView", "display MM AD!");
			    	MMPlaceHolder placeHolder =	mPage.createPlaceHolder("MOBILE_APP_IMAGE", mmAdLayoutParams);
			    	placeHolder.setMultiMatch(3);
			   		placeHolder.showAd((Activity)context, frame);
					frame.setVisibility(View.VISIBLE);
				}				
				break;
			case WOSTORE:
				//wostore
				View v = view.findViewById(wostoreContainer);
				out = String.format("wostoreContainer=0x%x", wostoreContainer);
				Log.i(TAG+".inflateAdView", out);
				if(v!=null){	
					Log.i(TAG+".inflateAdView", "display wostore AD!");
			    	v.setVisibility(View.VISIBLE);
				}				
				break;
			}
		}    	
    }

    private void wapsAdFlow(Activity act, int gravity){
    	LinearLayout layout = new LinearLayout(act);
    	FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
    			FrameLayout.LayoutParams.FILL_PARENT, 
    			FrameLayout.LayoutParams.WRAP_CONTENT
    	);
    	params.gravity = gravity;
    	MiniAdView miniV = new MiniAdView(act, layout);
    	miniV.DisplayAd(10);
    	
    	act.addContentView(layout, params);
    }
    
    private void youmiAdFlow(Activity act, int gravity){
    	
    	//初始化广告视图
    	AdView adView = new AdView(act);
    	FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
    			FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    	
    	//设置广告出现的位置(悬浮于屏幕右下角)
    	params.gravity=gravity;
    	
    	//将广告视图加入Activity中
    	act.addContentView(adView, params);
    }
    
    public void displayWoobooDynamicAd(View container, Context ctx){
    	if(adSwitch){
        	int adType = getAdType();
        	if(adType==AdGlobals.WOOBOO){
        		woobooAdFlow(container, ctx);
        	}
    	}
    }
    
    private ImpressionAdView woobooAdFlow(View container, Context context){
    	ImpressionAdView ad = null;
    	
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		double density = dm.density;
		int adWidth = (int) (density * 320); // 广告宽度
		int adHeight = (int) (density * 48); // 广告高度
		int x = (dm.widthPixels - adWidth) >> 1;
		int y = dm.heightPixels - adHeight;
		
		ad = new ImpressionAdView(context,
				container,
				x,
				y,
				android.graphics.Color.WHITE,
				false,
				null);		
	    ad.show(60); //60表示广告刷新频率
		
		return ad;
    }
    
    public void adDynamicFlow(View woobooAdViewContainer, Activity act, int youmiAdViewGravity){
        if(adSwitch){
        	int adType = getAdType();
        	String out = String.format("dynamic ad, type=%s", adTypeString[adType]);
        	Log.i(TAG+".adDynamicFlow", out);
        	switch(adType){
        	case AdGlobals.WOOBOO:
            	woobooAdFlow(woobooAdViewContainer, act);
        		break;
        	case AdGlobals.YOUMI:
            	youmiAdFlow(act, youmiAdViewGravity);
        		break;
        	case AdGlobals.WAPS:
        		wapsAdFlow(act, youmiAdViewGravity);
        		break;
        	default:
        		out = String.format(
        				"no dynamic ad will be displayed for adType=%s!!",
        				adTypeString[adType]);
        		Log.i(TAG+".adDynamicFlow", out);
        		break;
        	}
        }
    }
    
    //MM
    private final FrameLayout.LayoutParams mmAdLayoutParams = new FrameLayout.LayoutParams(
    		LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
    private MMApp mApp;
    private MMPage mPage;
    private void initMM(Context ctx){
       	mApp = new MMApp("match1.mmarket.com", ctx.getApplicationContext());
    	mPage = mApp.createPage("MobileApps.TestUser.DefaultApp.DefaultPage"); 
    }
    public MMApp getMMAppInstance(){
    	return mApp;
    }
    public void playMMInterstitialAd(Activity act){
    	if(adSwitch){
            Intent intent = new Intent();
            intent.setClass(act, MMInterstitialActivity.class);
            act.startActivity(intent);    		
    	}
    }
    
    //waps
    public void showWapsOffer(Context ctx){
    	AppConnect.getInstance(ctx).showOffers(ctx);
    }
    
    public void addRecommendButton(final Activity act, int gravity){
    	Button btn = new Button(act);
    	btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AdGlobals.getInstance().showWapsOffer(act);
			}
		});
    	
    	ViewGroup.LayoutParams btnParams = new ViewGroup.LayoutParams(
    			ViewGroup.LayoutParams.WRAP_CONTENT, 
    			ViewGroup.LayoutParams.WRAP_CONTENT
    	);
    	btn.setLayoutParams(btnParams);
    	btn.setText(R.string.recommendByWaps);
    	
    	FrameLayout.LayoutParams posParams = new FrameLayout.LayoutParams(
    			FrameLayout.LayoutParams.WRAP_CONTENT,
    			FrameLayout.LayoutParams.WRAP_CONTENT
    	);
    	posParams.gravity = gravity;
    	act.addContentView(btn, posParams);
    }    
}