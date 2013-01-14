package oms.cj.tube;

import oms.cj.ads.AdGlobals;
import oms.cj.widget.EfficientAdapter;

import com.wooboo.adlib_android.ImpressionAdView;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ChooseFileActivity extends ListActivity {
	private final static String TAG = "SerializeCubeActivity";
	public final static String REQUESTCODE = "RequestCode";
    public final static int CHOOSE_FILE_TO_OPEN = 0;
    public final static int CHOOSE_FILE_TO_SAVE = 1;
    public final static String FILE2SERIALIZE = "filetoserialize";
    
	protected Object[] mObjects = MainActivity.getPredefinedNames();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		String title = getResources().getString(R.string.app_name) + "-";
		int requestCode = getIntent().getIntExtra(REQUESTCODE, CHOOSE_FILE_TO_OPEN);
		switch(requestCode){
		case CHOOSE_FILE_TO_OPEN:
		default:
			title += getResources().getString(R.string.restore);
			break;
		case CHOOSE_FILE_TO_SAVE:
			title += getResources().getString(R.string.save);
			break;
		}
		this.setTitle(title);
		
        //读取预定义的文件名
    	String[] strings = new String[mObjects.length];
    	for(int i=0;i<strings.length;i++){
    		strings[i]=(String) mObjects[i];
    	}

        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        ListAdapter adapter = new EfficientAdapter(this, strings, R.layout.cubesavedlayout);
        setListAdapter(adapter);
        getListView().setTextFilterEnabled(true);
        
        AdGlobals.getInstance().displayWoobooDynamicAd(getListView(), this);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {    
    	String fileName = (String) l.getItemAtPosition(position);
    	Log.i(TAG, "onListItemClick(...): " + "fileName=" + fileName);
    	Intent data = new Intent();
    	data.putExtra(FILE2SERIALIZE, fileName);
    	setResult(RESULT_OK, data);
    	finish();
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭 渐入式 广告
		ImpressionAdView.close();
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onResume(){
		super.onPause();
	}
}
