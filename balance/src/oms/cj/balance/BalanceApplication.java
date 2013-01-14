package oms.cj.balance;

import java.util.HashMap;

import oms.cj.ads.AdGlobals;

import com.openfeint.api.OpenFeint;
import com.openfeint.api.OpenFeintDelegate;
import com.openfeint.api.OpenFeintSettings;

import android.app.Application;
import android.util.Log;

public class BalanceApplication extends Application {
	private final static String TAG = "BalanceApplication";
	
	static final String gameName = "快乐魔球";

	static final String gameID = "1060316192";
	static final String gameKey = "Gw9Uyp0nNaTHKJbDjd9sw";
	static final String gameSecret = "i7ko4vvt1sYooEzQ4SqPuPLuPZBSpwCI";
	
    private HashMap<Integer, String> mLevel2The9LeaderBoard = new HashMap<Integer, String>();    

    private void initThe9Map(){
        mLevel2The9LeaderBoard.put(plate.EASY, "916956372");
        //mLevel2The9LeaderBoard.put(plate.MEDIUM, "916956142");  
        mLevel2The9LeaderBoard.put(plate.MEDIUM, "916956612");
        mLevel2The9LeaderBoard.put(plate.HARD, "916956382");
    }
    
    public String getLeaderboardID(int level){
    	return mLevel2The9LeaderBoard.get(level);
    }
    
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
            initThe9Map();
            OpenFeintSettings settings = new OpenFeintSettings(gameName,
            		gameKey, gameSecret, gameID);
        	OpenFeint.initialize(this, settings, new OpenFeintDelegate() {});        	
        }        
    }
}
