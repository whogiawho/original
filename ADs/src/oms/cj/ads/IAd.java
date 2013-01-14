package oms.cj.ads;

public interface IAd {
	public void setViewStub(int viewstubId);
	public int getViewStub();
	
	public void addAdContainer(int adType, int containerId);
	public int getAdContainer(int adType);
}
