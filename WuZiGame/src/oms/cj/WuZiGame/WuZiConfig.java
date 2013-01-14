package oms.cj.WuZiGame;

import oms.cj.WuZiGame.R;
import oms.cj.WuZiLogic.LogicConfig;
import oms.cj.view.SimArrowsView;

public class WuZiConfig{
	public final static int VOICEOFF=R.id.voiceoff;
	public final static int VOICEON=R.id.voiceon;
	public final static int MOVEFOCUSONLY = R.id.movefocusonly;
	public final static int MOVEFOCUSANDLUOZI = R.id.movefocusandluozi;
	//xian hou shou
	public final static int XianShou = R.id.blackxianrun;
	public final static int HouShou = R.id.whitehourun;
	//difficulty
	public final static int EASY=R.id.easy;
	public final static int MEDIUM=R.id.medium;
	public final static int LITTLEHARD=R.id.littlehard;
	public final static int HARD=R.id.hard;
	public final static int IMPOSSIBLE=R.id.impossible;
	//strategy
	public final static int ATTACK=R.id.attack;
	public final static int DEFEND=R.id.defend;
	public final static int ADBALANCE=R.id.adbalance;
	//sound effects
	public final static int SOUNDON=R.id.soundon;
	public final static int SOUNDOFF=R.id.soundoff;
	
	public int mXianHouShou;
	public int mDifficulty;
	public int mStrategy;
	public boolean mSanSan;
	public boolean mSiSi;
	public boolean mChangLian;
	
	public static int mapDirID(int id){
		int retVal;
		
		switch(id){
		case SimArrowsView.LEFT:
			retVal = LogicConfig.LEFT;
			break;
		case SimArrowsView.RIGHT:
			retVal = LogicConfig.RIGHT;
			break;
		case SimArrowsView.UP:
			retVal = LogicConfig.UP;
			break;
		case SimArrowsView.DOWN:
			retVal = LogicConfig.DOWN;
			break;
		case SimArrowsView.UPLEFT:
			retVal = LogicConfig.UPLEFT;
			break;
		case SimArrowsView.UPRIGHT:
			retVal = LogicConfig.UPRIGHT;
			break;
		case SimArrowsView.DOWNLEFT:
			retVal = LogicConfig.DOWNLEFT;
			break;
		case SimArrowsView.DOWNRIGHT:
			retVal = LogicConfig.DOWNRIGHT;
			break;
		case SimArrowsView.STILL:
		default:
			retVal = LogicConfig.STILL;
			break;
		}
		
		return retVal;		
	}
	
	public static int mapFocusMovingMethod(int m){
		int retVal;
		
		switch(m){
		case R.id.movefocusonly:
			retVal = LogicConfig.MOVEFOCUSONLY;
			break;
		case R.id.movefocusandluozi:
			retVal = LogicConfig.MOVEFOCUSANDLUOZI;
			break;
		default:
			retVal = LogicConfig.MOVEFOCUSONLY;
			break;
		}
		
		return retVal;
	}
	
	private static int mapXianHouShou(int x){
		int retVal;
		
		switch(x){
		case R.id.blackxianrun:
			retVal = LogicConfig.XianShou;
			break;
		case R.id.whitehourun:
			retVal = LogicConfig.HouShou;
			break;
		default:
			retVal = LogicConfig.XianShou;
			break;
		}
		
		return retVal;
	}
	
	private static int mapDifficulty(int d){
		int retVal;
		
		switch(d){
		case R.id.easy:
			retVal = LogicConfig.EASY;
			break;
		case R.id.medium:
			retVal = LogicConfig.MEDIUM;
			break;
		case R.id.littlehard:
			retVal = LogicConfig.LITTLEHARD;
			break;
		case R.id.hard:
			retVal = LogicConfig.HARD;
			break;
		case R.id.impossible:
			retVal = LogicConfig.IMPOSSIBLE;
			break;
		default:
			retVal = LogicConfig.EASY;
			break;
		}
		
		return retVal;
	}
	
	private static int mapStrategy(int s){
		int retVal;
		
		switch(s){
		case R.id.attack:
			retVal = LogicConfig.ATTACK;
			break;
		case R.id.defend:
			retVal = LogicConfig.DEFEND;
			break;
		case R.id.adbalance:
			retVal = LogicConfig.ADBALANCE;
			break;
		default:
			retVal = LogicConfig.ATTACK;
			break;
		}
		
		return retVal;
	}

	public static int mapSoundEffects(int se){
		int retVal;
		
		switch(se){
		case R.id.soundon:
		default:
			retVal = LogicConfig.SOUNDON;
			break;
		case R.id.soundoff:
			retVal = LogicConfig.SOUNDOFF;
			break;
		}
		
		return retVal;		
	}
	
	public WuZiConfig(int x, int d, int s, boolean SanSan, boolean SiSi, boolean ChangLian){
		mXianHouShou=x;
		mDifficulty=d;
		mStrategy=s;
		mSanSan=SanSan;
		mSiSi=SiSi;
		mChangLian=ChangLian;
	}
	
	public LogicConfig convert2LogicConfig(){
		return new LogicConfig(mapXianHouShou(mXianHouShou), 
				mapDifficulty(mDifficulty), 
				mapStrategy(mStrategy), 
				mSanSan, mSiSi, mChangLian);
	}
	
	public String toString(){
		String str="";
		
		str="mSanSan=" + mSanSan + "\n";
		str+="mSiSi=" + mSiSi + "\n";
		str+="mChangLian=" + mChangLian + "\n";
		
		return str;
	}
}
