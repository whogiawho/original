package oms.cj.WuZiGame;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Globals {
	public final static String YoumiID = "f8d5088542fcd19e";
	public final static String YoumiPass = "11f97e687d2ca462";
	
	public static String constructThe9RequestInfo(Activity act){
		String info = "";
		String versionName = "";
		
		try {
			String packageName = act.getPackageName();
			PackageManager mgr = act.getPackageManager();
			PackageInfo pkgInfo;
			pkgInfo = mgr.getPackageInfo(packageName, PackageManager.GET_META_DATA);
			versionName = pkgInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		info += act.getString(R.string.fmtGameName, act.getString(R.string.app_name)); 
		info += act.getString(R.string.fmtVersion, versionName);
		info += act.getString(R.string.fmtReleaser, act.getString(R.string.Releaser));
		info += act.getString(R.string.fmtDeveloper, act.getString(R.string.Developer));
		info += act.getString(R.string.fmtPhone, act.getString(R.string.Phone));
		info += act.getString(R.string.fmtEmail, act.getString(R.string.Email));
		info += "\n";
		
		return info;
	}		
}
