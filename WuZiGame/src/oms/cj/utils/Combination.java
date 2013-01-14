package oms.cj.utils;

public class Combination {
	public final static int ZEROSTART = 0;
	public final static int ONESTART = 1;
	
	private int mM;
	private int mN;
	private int mType;
	private CThread pT;
	
	public Combination(int type){
		mType = ZEROSTART;
		if(type == ONESTART)
			mType = ONESTART;
	}
	
    public static String intArray2String(int[] p, int offset){
		String str = "";
		for(int i=0; i<p.length;i++){
			int out = p[i] + offset;
			str += " " + out;
		}
		return str;
	}

    public long totalValues(){
    	long retVal = 0, a=1, b=1;
    	for(int i=1;i<=mN;i++){
    		a=i*a;
    		b=(mM-i+1)*b;
    	}
    	retVal = b/a;
    	return retVal;
    }

	private class CThread extends Thread implements Runnable {
		private int[] pList;
		private boolean[] bSelected;
		private volatile boolean bResultReady;
		private boolean mIsInterrupted;
		
		CThread(){
			bSelected = new boolean[mM];
			for(int i=0;i<bSelected.length;i++)
				bSelected[i] = false;
			
			pList = new int[mN];
			
			bResultReady = false;
			mIsInterrupted = false;
		}
				
		@Override
		public void run() {
			combination(0);
		}
		
		void combination(int level){
			if(mIsInterrupted)
				return;
			if(this.isInterrupted()){
				mIsInterrupted = true;
				return;
			}
			
			if(level == mN){		
				try {
					synchronized(this){
						bResultReady = true;
						wait();
 					}
				} catch (InterruptedException e) {
					mIsInterrupted = true;
					e.printStackTrace();
				}
				return;
			}else {
				for(int i=mM-1;i>=0;i--){
					if(bSelected[i] != true){
						bSelected[i] = true;
						if(mType == ZEROSTART)
							pList[level] = i;
						else
							pList[level] = i+1;
						combination(level+1);
						bSelected[i] = false;
					} else
						break;
				}
			}
		}
		
		public int[] getNext() throws InterruptedException{
			int[] p=null;
			
			while(bResultReady==false){
				if(mIsInterrupted){
					throw new java.lang.InterruptedException();
				}
			}
			
			synchronized(this){
				p = pList.clone();
				bResultReady = false;
				notifyAll();
			}
			return p;
		}
	}
	
	public void open(int m, int n){
		mM = m;
		mN = n;
		
		pT = new CThread();
		pT.start();
	}
		
	public int[] getNext() throws InterruptedException{
		if(pT.isAlive()) 
			return pT.getNext();
        else
			return null;
	}
	
	public void close(){
		pT.interrupt();
	}
}