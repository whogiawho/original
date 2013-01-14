package oms.cj.tube.solver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class InstallZipAssets {
	private final static String TAG = "InstallZipAssets";
	private final static String assetszip = "assets.zip";
	private Context mContext;
	
	InstallZipAssets(Context context){
		mContext = context;
	}
	
	public void initAssets(String dataDir){
		File data = new File(dataDir);
		if(!data.exists())
			data.mkdir();
		
		File finished = new File(data.getPath()+"/Finished.flag");
		if(!finished.exists()){
			InstallAssetsFromZip(dataDir+"/");
			// generate the file Finished.flag
			try {
				finished.createNewFile();
			} catch (IOException e) {
				Log.e(TAG, "copyAssets2DataDir():" + e.getMessage());
			}
		}		
	}
	
	private void InstallAssetsFromZip(String dir){
		final AssetManager mAssetManager = mContext.getResources().getAssets();
		
		ZipInputStream zs;
		try {
			zs = new ZipInputStream(mAssetManager.open(assetszip, AssetManager.ACCESS_BUFFER));
			ZipEntry item;
			while( (item = zs.getNextEntry())!=null ) {

				if( item.isDirectory() ) {
					File newdir = new File( dir + item.getName() );
					if (!newdir.exists())
						newdir.mkdir();
				}
				else {
					File newfile = new File( dir + item.getName() );
					long filesize = item.getSize();
					if (newfile.exists() && newfile.length() == filesize)
						continue;
					byte[] tempdata = new byte[(int)filesize];
					int offset = 0;
					while (offset<filesize)
						offset += zs.read(tempdata, offset, (int)filesize-offset);
					zs.closeEntry();
					newfile.createNewFile();
					FileOutputStream fo = new FileOutputStream(newfile);
					fo.write(tempdata);
					fo.close();
				}
			}
			zs.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}	
}
