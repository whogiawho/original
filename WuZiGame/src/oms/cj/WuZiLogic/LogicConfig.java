package oms.cj.WuZiLogic;

public class LogicConfig {
	//direction ID
	public final static int LEFT = 0;
	public final static int RIGHT = 1;
	public final static int UP = 2;
	public final static int DOWN = 3;
	public final static int UPLEFT = 4;
	public final static int UPRIGHT = 5;
	public final static int DOWNLEFT = 6;
	public final static int DOWNRIGHT = 7;
	public final static int STILL = 8;
	
	//music switch
	public final static int VOICEOFF=0;
	public final static int VOICEON=1;
	
	//focus moving method
	public final static int MOVEFOCUSONLY = 0;
	public final static int MOVEFOCUSANDLUOZI = 1;
	
	//xian hou shou
	public final static int XianShou = 0;
	public final static int HouShou = 1;
	//difficulty
	public final static int EASY=0;
	public final static int MEDIUM=1;
	public final static int LITTLEHARD=2;
	public final static int HARD=3;
	public final static int IMPOSSIBLE=4;
	//strategy
	public final static int ATTACK=0;
	public final static int DEFEND=1;
	public final static int ADBALANCE=2;
	//sound effects
	public final static int SOUNDON=0;
	public final static int SOUNDOFF=1;
	
	//core members
	public int mXianHouShou;
	public int mDifficulty;
	public int mStrategy;
	public boolean mSanSan;
	public boolean mSiSi;
	public boolean mChangLian;

	public LogicConfig(int x, int d, int s, boolean SanSan, boolean SiSi, boolean ChangLian){
		mXianHouShou=x;
		mDifficulty=d;
		mStrategy=s;
		mSanSan=SanSan;
		mSiSi=SiSi;
		mChangLian=ChangLian;
	}
	
	public LogicConfig(boolean SanSan, boolean SiSi, boolean ChangLian){
		this(XianShou, EASY, ATTACK, SanSan, SiSi, ChangLian);
	}
	
	public LogicConfig(LogicConfig config){
		this(config.mXianHouShou, config.mDifficulty, config.mStrategy, 
				config.mSanSan, config.mSiSi, config.mChangLian);
	}
}
