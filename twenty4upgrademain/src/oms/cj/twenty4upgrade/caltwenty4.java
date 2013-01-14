/*
    twenty4, An OPhone program to search expressions equal to 24 per 4 input digits

    Copyright (C) <2009>  chenjian

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    You can contact me by email ustcchenjian@gmail.com
*/

package oms.cj.twenty4upgrade;

import android.app.Activity;
import android.os.Bundle;
import java.util.*;
import oms.cj.ads.AdGlobals;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;
import android.view.*;
import android.os.*;
import android.util.*;

public class caltwenty4 extends Activity implements View.OnClickListener, Runnable{
	private static final String TAG="caltwenty4";
	public static final int NEWRESULT=1;
	public static final int COMPLETE=2;
	public static final int UPDATETITLE=3;

	private ScrollView sv;
	private TextView tv;
	private TextView searching;
	private Thread workerThread;
	private Timer timer;
	private long start;
	private String[] digitlist;
	private int mExpectedResult;
	private way way2cal24;
	
    public void onClick(View view){
    	timer.cancel();
    	workerThread.interrupt();
    	this.finish();
    }

    public void run(){
        way2cal24.run(); 
    }
    
    private void setresultviewheight(){
    	ScreenInfo screen = new ScreenInfo(this);
    	int height = screen.getHeight()-210; 
    	
    	Log.i(TAG, "setresultviewheight(...): " + "scrollview height =" + height);
    	LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height);
    	sv.setLayoutParams(params);
    }
	
    @Override
    public void onResume(){
    	super.onResume();
    	
    }
    
    @Override    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.showresult, AdGlobals.getInstance().getAdInterface());
        setContentView(view);
                
        start=new Date().getTime();
        sv = (ScrollView) findViewById(R.id.scrollview1);
        tv = (TextView) findViewById(R.id.results);
        searching=(TextView) findViewById(R.id.searching);
        Button closeresult= (Button) findViewById(R.id.closeresult);
        closeresult.setOnClickListener(this);

        //设置scrollview的height
        setresultviewheight();
        
        //读取preferences 
        Bundle bundle=getIntent().getExtras();
        digitlist=bundle.getString("digitlist").split(" ");
        mExpectedResult = bundle.getInt("expectedresult");

        //开始计算
        workerThread=new Thread(this);
        way2cal24 = new way2(digitlist, mExpectedResult, handler, workerThread);
        
        timer=new Timer();
        updatetitletask t=new updatetitletask();
        timer.schedule(t, 500, 1000);

        workerThread.start();
    }   
    
    private Handler handler=new Handler(){
    	Integer i,numberofpoints;
    	String outtxt="";

    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what){
    		case NEWRESULT:
    			String newresult = (String) msg.obj;
   				tv.append(newresult);
    			break;
    		case COMPLETE:
    			timer.cancel();
    			long end = System.currentTimeMillis();
    			if(way2cal24.getinfixexprset().size()==0)
    				outtxt="无解!";
    			else {
    				outtxt=outtxt+"共"+ new Integer(way2cal24.getinfixexprset().size()).toString() +"个解;";
    				outtxt=outtxt+"花了"+new Integer((int)(end-start)/1000).toString()+"秒";
    			}
    			searching.setText(outtxt);
    			break;
    		case UPDATETITLE:
    			String title=searching.getText().toString();
    			String[] strlist=title.split(" +");
    			String newtitle=strlist[0];
    			numberofpoints=(strlist.length)%7;
    			for(i=0;i<numberofpoints;i++)
    				newtitle=newtitle+" .";
    			searching.setText(newtitle);
    			break;
    		}
    	}
    };
    
	private class updatetitletask extends java.util.TimerTask{
		@Override
		public void run(){
			handler.sendEmptyMessage(UPDATETITLE);
		}
	}    
}