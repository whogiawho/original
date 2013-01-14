package oms.cj.MinMaxEgg;

public class Combination {
	private int mM;
	private int mN;
	private PThread pT;
	Combination(){
	}
	
	private class PThread extends Thread implements Runnable {
		private int[] pList;
		private boolean[] bSelected;
		private volatile boolean bResultReady;
		
		PThread(){
			bSelected = new boolean[mM];
			for(int i=0;i<bSelected.length;i++)
				bSelected[i] = false;
			
			pList = new int[mN+2];
			pList[0] = 0;
			pList[pList.length-1] = mM+1;
			
			bResultReady = false;
		}
				
		@Override
		public void run() {
			perm(0);
		}
		
		void perm(int level){
			if(level == mN){		
				try {
					synchronized(this){
						bResultReady = true;
						wait();
 					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}else {
				for(int i=mM-1;i>=0;i--){
					if(bSelected[i] != true){
						bSelected[i] = true;
						pList[level+1] = i+1;
						perm(level+1);
						bSelected[i] = false;
					} else
						break;
				}
			}
		}
		
		public int[] getNext(){
			int[] p;

			while(bResultReady==false);
			synchronized(this){
				p = pList.clone();
				bResultReady = false;
				notifyAll();
			}
			return p;
		}
	}
	
	public void start(int m, int n){
		mM = m;
		mN = n;
		
		pT = new PThread();
		pT.start();
	}
		
	public int[] getNext(){
		if(pT.isAlive()) 
			return pT.getNext();
        else
			return null;
	}
	
	public void end(){
		
	}
}
