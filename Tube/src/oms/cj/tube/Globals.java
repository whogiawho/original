// This string is autogenerated by ChangeAppSettings.sh, do not change spaces amount
package oms.cj.tube;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.IntBuffer;
import javax.microedition.khronos.opengles.GL10;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class Globals {
	public final static String TAG = "Globals";

	public final static String ApplicationName = "tube";
    public final static String advancedAsset = "coordcube.dat";
    //kinds of switches
    public final static boolean bSaveSnapshot = false;
        
    public final static String YoumiID = "6df24ad4c3b8df87";
    public final static String YoumiPass = "c8c897ef9ca8b642";
    
    public static String assetDir = "";
    public static String sdcardDir= "";
    
	public static void write2File(String name, int[] data){
		try {
			FileOutputStream out = new FileOutputStream(Globals.sdcardDir+"/"+ name);
			PrintStream p = new PrintStream(new BufferedOutputStream(out));
			String str = "";
			for(int i=0;i<data.length;i++){
				str +=Integer.toHexString(data[i])+"\n";
			}
			p.println(str);
			p.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void init(String packageName){
    	Log.i("Globals.init", "being called!");
    	
    	assetDir = "/data/data/" + packageName + "/files";
    	sdcardDir = "/sdcard/" + packageName;
    	
		File data = new File(sdcardDir);
		if(!data.exists()){
			Log.i("Globals.init", "creating dir = " + sdcardDir);
			boolean created = data.mkdirs();
			Log.i("Globals.init", "created = " + created);
		}
	}


	public static int getWidth(Context context){
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int scwidth = display.getWidth();
        return scwidth;
	}
	
	public static int getHeight(Context context){
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int scheight = display.getHeight();		
        return scheight;
	}
    
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
	
	public static String constructAboutInfo(Activity act){
		String info = "";

		info += act.getString(R.string.fmtAuthor, act.getString(R.string.Author)); 
		info += act.getString(R.string.fmtAuthorEmail, act.getString(R.string.AuthorEmail));
		info += act.getString(R.string.fmtCopyrightPeriod, act.getString(R.string.CopyrightPeriod));
		info += act.getString(R.string.fmtLicense, act.getString(R.string.License));
		info += "\n";
		
		return info;
	}

	public static Bitmap loadRes2Bitmap(Resources rs, int id, boolean scale) {
	    Bitmap bitmap;
	    if (scale) {
	        bitmap = BitmapFactory.decodeResource(rs, id);
	    } else {
	        BitmapFactory.Options opts = new BitmapFactory.Options();
	        opts.inScaled = false; 
	        bitmap = BitmapFactory.decodeResource(rs, id, opts); 
	    }
	    return bitmap;
	} 
	
	 public static Bitmap SavePixels(int x, int y, int w, int h, GL10 gl){  
		 int b[]=new int[w*h];
	     int bt[]=new int[w*h];
	     IntBuffer ib=IntBuffer.wrap(b);
	     ib.position(0);
	     
	     gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);
	     for(int i=0; i<h; i++){
	    	 //remember, that OpenGL bitmap is incompatible with Android bitmap
	         //and so, some correction need.        
	         for(int j=0; j<w; j++) {
	        	 int pix=b[i*w+j];
                 int pb=(pix>>16)&0xff;
                 int pr=(pix<<16)&0x00ff0000;
	             int pix1=(pix&0xff00ff00) | pr | pb;
	             bt[(h-i-1)*w+j]=pix1;
	         }
	     }
	     
	     Bitmap sb=Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);

	     return sb;
	 }

	public static void saveBitmap2File(Bitmap b, String filename, Context ctx){
		try {
			FileOutputStream output = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
			b.compress(Bitmap.CompressFormat.PNG, 100, output);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Bitmap loadPNG(String filename, Context ctx){
		Bitmap b = null;
		
		File f = ctx.getFileStreamPath(filename);
		if(f!=null){
			String completePath = f.getAbsolutePath();
			String out = String.format("%s", completePath);
			Log.i(TAG+".loadPNG", out);
			b = BitmapFactory.decodeFile(completePath);
		}
		
		return b;
	}

	public static Bitmap loadPNG(int iconID, Context ctx){
		Bitmap b = null;
		
		b = BitmapFactory.decodeResource(ctx.getResources(), iconID);
		
		return b;
	}

	private static void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
	
	public static void copyAssetFileTo(Context ctx, String assetFile, String fileInDataDir){
		AssetManager assetManager = ctx.getAssets();
		
        InputStream in = null;
        OutputStream out = null;
        try {
          in = assetManager.open(assetFile);
          out = ctx.openFileOutput(fileInDataDir, Context.MODE_PRIVATE);
          copyFile(in, out);
          in.close();
          in = null;
          out.flush();
          out.close();
          out = null;		
        } catch(Exception e) {
            Log.e("tag", e.getMessage());
        }  
	}	
}
