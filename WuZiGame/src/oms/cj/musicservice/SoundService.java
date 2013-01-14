package oms.cj.musicservice;

import oms.cj.WuZiGame.R;
import android.content.Context;

public class SoundService {
	public final static int ARROWCLICKED = 0;
	public final static int ENTERCLICKED = 1;
	public final static int MENUCLICKED = 2;	
	public final static int ESCCLICKED = 3;
    
	private SoundManager s;
	
	public SoundService(Context context){
		s = new SoundManager();
		s.initSounds(context);
		s.addSound(ARROWCLICKED, R.raw.sound0);
		s.addSound(ENTERCLICKED, R.raw.sound1);
		s.addSound(MENUCLICKED, R.raw.in);
		s.addSound(ESCCLICKED, R.raw.out);
	}
	
    public void playSound(int type){
    	switch(type){
    	case ARROWCLICKED:
    	case ENTERCLICKED:
    	case MENUCLICKED:
    	case ESCCLICKED:
    		s.playSound(type);
    		break;
    	default:
    		s.playSound(MENUCLICKED);
    		break;    
    	}
    }    
}
