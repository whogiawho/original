package oms.cj.WuZiGame;

import oms.cj.WuZiGame.R;
import oms.cj.ads.AdGlobals;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class help extends Activity {
	private static final String TAG="help";
	LinearLayout mhelp;
	private TextView mhelpsv;
	private static final String[] sHelpText=
	    {
		 "\n",
		 "0.一个15x15的棋盘，共有2种不同颜色的棋子：",
		 "  黑白\n",
		 "1.玩家可以选择其中的任意一种；",
		 "  程序则选择另外一种, 双方轮流下子\n",
	     "2.如果在棋盘上的直线上出现相邻的某方的棋子达",
	     "  到5个或5个以上, 该方获胜，游戏结束",
	     "  这些直线必须是下列四种情况之一:",
	     "      任一水平线",
	     "      任一垂直线",
	     "      任一45度直线",
	     "      任一135度直线",
	     "  但黑方必须排除掉规则3中列出的3种禁手\n",
	     "3.玩家可以自定是否限制黑方走出下列3种禁手：",
	     "  三三禁手",
	     "  四四禁手",
	     "  长连禁手",
	     "  关于禁手，请参考《中国五子棋竞赛规则》\n",
	    };
	
    @Override
    public void onResume(){
    	super.onResume();
    	
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// fullscreen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.help, AdGlobals.getInstance().getAdInterface());
        setContentView(view);

    	mhelpsv=(TextView)findViewById(R.id.helptext);
    	String helpstring="";
    	for(int i=0;i<sHelpText.length;i++)
    		helpstring=helpstring+sHelpText[i]+"\n";
    	helpstring += Globals.constructThe9RequestInfo(this);
    	Log.i(TAG,"onCreate(saveInstancesTate): "+"helpstring="+helpstring);
    	mhelpsv.setText(helpstring);

        mhelp=(LinearLayout) findViewById(R.id.help);
        mhelp.setBackgroundResource(R.drawable.background);
    }   
    
    @Override
    public void onConfigurationChanged(Configuration newConfig){
    	Log.i(TAG, "onConfigurationChanged() is called!");
    	super.onConfigurationChanged(newConfig);
    }
}