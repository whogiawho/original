package oms.cj.ads;

import java.util.HashMap;

public class AdInterface implements IAd {
	private int mAdStub;
	private HashMap<Integer, Integer> mContainers;
	
	AdInterface(){
		mAdStub = R.id.nullID;
		mContainers = new HashMap<Integer, Integer>();
	}

	@Override
	public void addAdContainer(int adType, int containerId) {
		mContainers.put(adType, containerId);
	}

	@Override
	public int getAdContainer(int adType) {
		return mContainers.get(adType);
	}

	@Override
	public int getViewStub() {
		return mAdStub;
	}

	@Override
	public void setViewStub(int viewstubId) {
		mAdStub = viewstubId;
	}
}
