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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import android.app.*;
import android.content.Context;
import java.util.*;
import android.util.Log;

public class recordfile {
	private static final String TAG="recordfile";
	static final String col[]={"order","name","grade"};
	
	private String recordfile=records.DefaultLevelFile;
	private Activity mact;
	ArrayList<Map<String, String>> mrecords;
	
	recordfile(Activity act, String filename){
		mact=act;
		recordfile=filename;
		mrecords=new ArrayList<Map<String,String>>();
	}
	
	public int findrecordlocation(int score){
		int highscore=0,i;
		
		for(i=mrecords.size();i>0;i--){
			highscore=new Integer(mrecords.get(i-1).get(col[2]));
			if(highscore>=score)
				break;
		}
		return i;
	}
	
    //将mrecords的新纪录存入recordfile
    public void saverecords(){
    	FileOutputStream fos;
    	int i;
    	
    	try {
			fos = mact.openFileOutput(recordfile, Context.MODE_PRIVATE);
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(fos));
			Log.i(TAG,"saverecords(): "+"mrecords.size="+mrecords.size());
			for(i=0;i<mrecords.size();i++){
				writer.write(mrecords.get(i).get(col[0])+"\t");
				writer.write(mrecords.get(i).get(col[1])+"\t");
				writer.write(mrecords.get(i).get(col[2])+"\n");
				writer.flush();
				Log.i(TAG,"saverecords(): "+"writing "+i+" successfully!");
			}
			writer.close();
			fos.close();
			Log.i(TAG,"saverecords(): "+"fos.close() completed!!!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
    
    public void resetrecords(){
    	mrecords=new ArrayList<Map<String,String>>();
		addonerecord("1","张三","0");
		addonerecord("2","李四","0");
		addonerecord("3","王五","0");
		addonerecord("4","赵六","0");
		addonerecord("5","钱七","0");
		saverecords();    	
		mrecords=new ArrayList<Map<String,String>>();
    }
    
    //从recordfile文件中读取记录置入mrecords
    public void getrecords(){
    	HashMap<String, String>m=new HashMap<String,String>();    	
    	FileInputStream fis;

    	//resetrecords();
		try {
			fis = mact.openFileInput(recordfile);
			BufferedReader reader=new BufferedReader(new InputStreamReader(fis));
	    	String line=null;
	    	String[] elementofline;  	

			while((line=reader.readLine())!=null){
				Log.i(TAG,"getrecords(): " + "line= " + line);				
				//split line and add it to mrecords
				elementofline=line.split("\t+");
				m=new HashMap<String,String>();
				m.put(col[0], elementofline[0]);
				m.put(col[1], elementofline[1]);
				m.put(col[2], elementofline[2]);
				mrecords.add(m);
				Log.i(TAG,"getrecords(): " + "item: " + m.toString());
			}
			reader.close();
			fis.close();	
			Log.i(TAG,"getrecords(): " + "fis.close() completed!!!");			
		} catch (FileNotFoundException e) {
			//如果读取文件recordfile不存在，那么产生一个新的recordfile
			Log.i(TAG,"getrecords(): "+recordfile+"does not exist!!");
			addonerecord("1","张三","0");
			addonerecord("2","李四","0");
			addonerecord("3","王五","0");
			addonerecord("4","赵六","0");
			addonerecord("5","钱七","0");
			saverecords();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
    
    private void addonerecord(String order, String name, String grade){
    	HashMap<String, String>m=new HashMap<String,String>();
    	
		m.put(col[0], order);
		m.put(col[1], name);
		m.put(col[2], grade);
		mrecords.add(m);
    }
}
