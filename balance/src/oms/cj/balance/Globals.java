package oms.cj.balance;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Globals {
	public final static String YoumiID = "1f32f0f2c3b2d11f"; 
	public final static String YoumiPass = "8f510c45fd6d19d7";
	
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
