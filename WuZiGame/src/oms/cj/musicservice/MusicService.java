package oms.cj.musicservice;

import oms.cj.WuZiGame.R;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {

    public class MusicBinder extends Binder {
    	public MusicService getService() {
            return MusicService.this;
        }
    }
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

    private final IBinder mBinder = new MusicBinder();
    private MediaPlayer mMediaPlayer;
    
    @Override
    public void onCreate() {
    }
    
    public void playBackgroundMusic(){
    	mMediaPlayer = MediaPlayer.create(this, R.raw.yuzhouchangwan);
    	mMediaPlayer.setLooping(true);
    	mMediaPlayer.start();
    }
    
    public void stopBackgroundMusic(){
    	mMediaPlayer.stop();
    }
}
