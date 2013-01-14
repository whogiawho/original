/*
    balance, An OPhone game to practise da ju guan

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

package oms.cj.balance;

import java.io.IOException;
import java.util.ArrayList;

import oms.cj.ads.AdGlobals;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

@SuppressWarnings("unused")
public class records extends Activity implements View.OnClickListener{
	private final static String TAG="records";
	private final static String PREFS_NAME = configapp.PREFS_NAME;
	private final static int PaddingLeft = 30;
	private final static int PaddingTop = 20;
	public final static int WC=TableLayout.LayoutParams.WRAP_CONTENT;
	public final static String DefaultLevelFile = "balancerecords.txt";
	public final static String EasyLevelFile = "balancerecords_easy.txt";
	public final static String HardLevelFile = "balancerecords_hard.txt";
	private final static ArrayList<Integer> sLevelIdList = new ArrayList<Integer>();
	private final static ArrayList<String> sLevelFileList = new ArrayList<String>();
	static {
		sLevelIdList.add(plate.EASY);
		sLevelIdList.add(plate.MEDIUM);
		sLevelIdList.add(plate.HARD);
	
		sLevelFileList.add(EasyLevelFile);
		sLevelFileList.add(DefaultLevelFile);
		sLevelFileList.add(HardLevelFile);
	}

	private TableLayout[] mtablayout = new TableLayout[3];
	

	private void resetrecords(int level){
		String levelFile = getLevelFile(level);
    	recordfile recfile=new recordfile(this, levelFile);
    	recfile.resetrecords();
    	Dialog dialog=new AlertDialog.Builder(this)
        .setTitle("重设记录")
        .setMessage("记录成功恢复到缺省值！")
        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
        	public void onClick(DialogInterface dialog, int whichButton){
        		return;
        	}
        })
        .create();
    	dialog.show();
	}
	
	public void onClick(View view){
		int viewid=view.getId();
		int level;
		switch(viewid){
		case R.id.resetrecord:
		default:
			level = plate.MEDIUM;
		   	break;
		case R.id.resetrecord_easy:
			level = plate.EASY;
		   	break;
		case R.id.resetrecord_hard:
			level = plate.HARD;
			break;
		}
		
		resetrecords(level);	//reset records
		//then show then in mtablayout
		int idx = getLevelIndex(level);
		String levelFile = getLevelFile(level);
		recordfile f = new recordfile(this, levelFile);
		f.getrecords();
		Log.i(TAG+".onClick", "size="+f.mrecords.size());
		for(int i=0;i<f.mrecords.size();i++){
			TableRow row=(TableRow)mtablayout[idx].getChildAt(i);
			Log.i(TAG+".onClick", "i="+i);
			for(int j=0;j<recordfile.col.length;j++){
				TextView tv=(TextView)row.getChildAt(j);
				tv.setText(f.mrecords.get(i).get(recordfile.col[j]));
			}			
		}	   
   	}
   
	private void initLevel(int level, int resHeaderOrder, int resRecordLayout, int resResetButton){
		int idx = getLevelIndex(level);
    	TextView tv=(TextView)findViewById(resHeaderOrder);
    	tv.setPadding(PaddingLeft, 0, PaddingTop, 0);
    	mtablayout[idx]=(TableLayout)findViewById(resRecordLayout);
    	Button reset=(Button)findViewById(resResetButton);
    	reset.setOnClickListener(this);
    
    	String levelFile = getLevelFile(level);
    	recordfile f = new recordfile(this, levelFile);
		f.getrecords();
		for(int i=0;i<f.mrecords.size();i++){
			TableRow row=new TableRow(this);
        	mtablayout[idx].addView(row, new TableLayout.LayoutParams(WC,WC));
        	
        	for(int j=0;j<recordfile.col.length;j++){
        		tv=new TextView(this);
        		if(j==0)
        			tv.setPadding(PaddingLeft, 0, 0, 0);
        		tv.setText(f.mrecords.get(i).get(recordfile.col[j]));
        		row.addView(tv, new TableRow.LayoutParams(120,WC));
        	}			
		}
		
	}
	
    @Override
    public void onResume(){
    	super.onResume();
    	
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {  		
    	super.onCreate(savedInstanceState);
        
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.records, AdGlobals.getInstance().getAdInterface());
        setContentView(view);
    	
        initLevel(plate.MEDIUM, R.id.headerorder, R.id.recordlayout, R.id.resetrecord);
        initLevel(plate.EASY, R.id.headerorder_easy, R.id.recordlayout_easy, R.id.resetrecord_easy);
        initLevel(plate.HARD, R.id.headerorder_hard, R.id.recordlayout_hard, R.id.resetrecord_hard);		
    }
    
    //out: [0-2]
    private static int getLevelIndex(int level){
    	int idx = sLevelIdList.indexOf(level);
    	if(idx==-1)
    		idx = 1;
    	return idx;
    }
    public static String getLevelFile(int level){
    	int idx = getLevelIndex(level);
    	String levelFile = sLevelFileList.get(idx);
        return levelFile;
    }
    private String getCurrentLevelFile(){    	
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int level = settings.getInt(configapp.ref[0], plate.MEDIUM); 
        
        return getLevelFile(level);
    }
}
