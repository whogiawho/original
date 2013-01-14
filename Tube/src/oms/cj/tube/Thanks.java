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

package oms.cj.tube;

import java.io.IOException;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.*;

@SuppressWarnings("unused")
public class Thanks extends Activity {
	public static final int WC=TableLayout.LayoutParams.WRAP_CONTENT;
	private static final String TAG="help";

	private static final String[] sThanksText={
		 "\n",
		 "0. Special thanks to Herbert Kociemba for his\n",
		 "1. excellent two-phase algorithm, which made \n",
		 "2. advanced solver here!\n",
		 "3. 特别感谢薛永(EL)\n",
		 "4. 因为有了他的文章“OPhone 3D开发之射线拾取”\n",
		 "5. 快乐魔方才能旋转起来\n\n",
	};
	
	private static final String[] sHelpText={
		 "\n",
	     "0.在九城游戏中心主界面里，点击排行，您可查看",
	     "  自己的得分在所有玩家中的排行。点击成就，您",
	     "  可查看本游戏成就的完成状况和达成条件。点击",
	     "  好友，您可查看好友的个性化界面和好友的游戏。\n\n",
	};
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {  		
    	super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
    	setContentView(R.layout.thanks);
    	
    	TextView tvThanks=(TextView)findViewById(R.id.thanks);
    	String strThanks="";
    	for(int i=0;i<sThanksText.length;i++)
    		strThanks += sThanksText[i]+"\n";
    	String out = String.format("strThanks=%s", strThanks);
    	Log.i(TAG+".onCreate", out);
    	tvThanks.setText(strThanks);
    	
    	TextView tvHelp=(TextView)findViewById(R.id.help);
    	String strHelp = "";
    	for(int i=0;i<sHelpText.length;i++)
    		strHelp += sHelpText[i]+"\n";
    	strHelp += Globals.constructThe9RequestInfo(this);
    	tvHelp.setText(strHelp);
    	
    	TextView tvAbout=(TextView)findViewById(R.id.about);
    	String strAbout = "";
    	strAbout += Globals.constructAboutInfo(this);
    	tvAbout.setText(strAbout);    	
    }
}
