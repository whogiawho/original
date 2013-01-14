package oms.cj.tube;

import oms.cj.ads.AdGlobals;

import com.openfeint.api.OpenFeint;
import com.openfeint.api.OpenFeintDelegate;
import com.openfeint.api.OpenFeintSettings;

import android.app.Application;
import android.util.Log;

public class TubeApplication extends Application {
	private final static String TAG = "TubeApplication";

	public static final String cjLeaderBoardID = "916956642";
	
	static final String gameName = "快乐魔方";
	static final String gameID = "1060316632";
	static final String gameKey = "vDIwFs07jOfixnOZuYtrZA";
	static final String gameSecret = "4ROrHLaMq8a5AZeopSSPJE8k8iERjzkc";

	public final static int DOU_MU_GONG = 0;
	public final static int JING_SHI_YU = 1;
	public final static int HU_TIAN_GE = 2;
	public final static int YAO_WANG_DIAN = 3;
	public final static int ZHONG_TIAN_MEN = 4;
	public final static int ZAN_YUN_JIAN = 5;
	public final static int SHI_BA_PAN = 6;
	public final static int NAN_TIAN_MEN = 7;
	public final static int YU_HUANG_DING = 8;
	
	public final static String cjAchievements[] = {
		"1073563092",
		"1073563102",
		"1073563112",
		"1073563122",
		"1073563132",
		"1073563142",
		"1073563152",
		"1073563162",
		"1073563172",
	};
	
	public final static String OriginCubeIcon = "origincubeicon.png";
	public final static String IconSuffix = ".png";

	@Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG+".onCreate", "is called!");
        
        /*//如果你需要自定义一些OpenFeintSettings的参数		
        Map<String, Object> options = new HashMap<String, Object>();
		options.put(OpenFeintSettings.SettingCloudStorageCompressionStrategy,
				OpenFeintSettings.CloudStorageCompressionStrategyDefault);
        OpenFeintSettings settings = new OpenFeintSettings(AppName,
				Product_Key, Product_Secret, Client_Application_ID, options);
         */
        if(AdGlobals.getInstance().the9Switch){
            OpenFeintSettings settings = new OpenFeintSettings(gameName,
            		gameKey, gameSecret, gameID);
        	OpenFeint.initialize(this, settings, new OpenFeintDelegate() {});       	
        }
        
    }
}
