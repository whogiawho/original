package oms.cj.tb;

public interface IWorkingMode {
	public final static int NORMAL = 0;
	public final static int TB = 1;
	public final static int OFFICIAL = 0;
	public final static int TRIAL =1;
	int getWorkingMode();
	int getTrial();
	boolean isAdMode();
}
