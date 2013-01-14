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

import oms.cj.ads.AdGlobals;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

@SuppressWarnings("unused")
public class help extends Activity {
	public static final int WC=TableLayout.LayoutParams.WRAP_CONTENT;
	private static final String TAG="help";
	private TextView mhelpsv;
	private static final String[] sHelpText=
	    {
		 "\n",
		 "0.一个10x10的棋盘，共有4种不同颜色的球：",
		 "  红绿黄棕\n",
		 "1.点击任一球，然后点击你希望它被移到的位置；", 
		 "  如果源和目的之间有路，移动成功，否则失败\n",
	     "2.每一次球移动后，棋盘中会被添加三颗球，",
	     "  棋盘左上方给出了这三颗球的颜色\n",
	     "3.如果在棋盘上的直线上出现相邻的同颜色的球达",
	     "  到5或5个以上, 这些球就会被消掉，腾出新空位",
	     "  这些直线必须是下列四种情况之一:",
	     "      任一水平线",
	     "      任一垂直线",
	     "      任一45度直线",
	     "      任一135度直线\n",
	     "4.消掉一个小球，加5分，累积在棋盘的右上方\n",
	     "5.棋盘上，如果再也无空位，游戏结束\n",
	     "6.在九城游戏中心主界面里，点击排行，您可查看",
	     "  自己的得分在所有玩家中的排行。点击成就，您",
	     "  可查看本游戏成就的完成状况和达成条件。点击",
	     "  好友，您可查看好友的个性化界面和好友的游戏。\n\n\n",
	    };
	
    @Override
    public void onResume(){
    	super.onResume();
    	
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {  		
    	super.onCreate(savedInstanceState);
        
		View view = AdGlobals.getInstance().inflateContentView(this, R.layout.help, AdGlobals.getInstance().getAdInterface());
        setContentView(view);
    	
    	mhelpsv=(TextView)findViewById(R.id.helptext);
    	String helpstring="";
    	for(int i=0;i<sHelpText.length;i++)
    		helpstring=helpstring+sHelpText[i]+"\n";
    	
    	helpstring += Globals.constructThe9RequestInfo(this);
    	
    	Log.i(TAG,"onCreate(saveInstancesTate): "+"helpstring="+helpstring);
    	mhelpsv.setText(helpstring);
    }
}
