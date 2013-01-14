package oms.cj.MinMaxEgg;

import android.util.Log;

public class MinMaxEgg {
	private final static String TAG = "MinMaxEgg";
	private ResultR[][] mRTable;
	
	MinMaxEgg(){
	}
	
	public ResultR[][] getRTable(){
		return mRTable;
	}
	
	public int start(int m, int n){
		int retVal;
				
		mRTable = new ResultR[m+1][n+1];
		for(int i=0;i<m+1;i++)
			for(int j=0;j<n+1;j++){
				mRTable[i][j] = new ResultR();
				mRTable[i][j].value = -1;
				mRTable[i][j].strategy = null;
			}
		
		retVal = f(0, m, n, 0);
		
		return retVal;
	}
	
    public static String toString(int[] p, int offset){
		String str = "";
		for(int i=1; i<p.length-1;i++){
			int out = p[i] + offset;
			str += " " + out;
		}
		return str;
	}
    
    public static String prefix(int level){
    	String str = "";
    	for(int i=0;i<level;i++)
    		str += " ";
    	return str;
    }
    
    long getCombinationValue(int m, int n){
    	long retVal = 0, a=1, b=1;
    	for(int i=1;i<=n;i++){
    		a=i*a;
    		b=(m-i+1)*b;
    	}
    	retVal = b/a;
    	return retVal;
    }
    
	private int f(int offset, int m, int n, int level){
		int retVal = 0;
		String strPrefix = prefix(level);
//		Log.i(TAG+".f", strPrefix + "level = " + level);
//		Log.i(TAG+".f", strPrefix + "offset = " + offset);
		
		if(m==0)
			return 0;
		if(n==1) 
			return m;
		if(mRTable[m][n].value!=-1){
			return mRTable[m][n].value;
		}
		
		int min = Integer.MAX_VALUE;
		int[] minpI = null;
		for(int x=1;x<2;x++){
			Combination p = new Combination();
			p.start(m, x);
			long cTimes = getCombinationValue(m, x);
			int[] pI;
			int j=0;
			while( j<cTimes ){
				pI = p.getNext();
//				Log.i(TAG+".f", strPrefix + "pI = " + toString(pI, offset));
				int max = Integer.MIN_VALUE; 
				for(int i=0;i<pI.length-1;i++){
					int tl = f(pI[i], pI[i+1]-pI[i]-1, n-x+i, level+1)+x;
					if(mRTable[pI[i+1]-pI[i]-1][n-x+i].value==-1)
						mRTable[pI[i+1]-pI[i]-1][n-x+i].value = tl-x;
//					Log.i(TAG+".f", strPrefix + "tl = " + tl);
					if(tl>max){
						max = tl;
					}
				}
				if(max<min){
					min = max;
					minpI = pI.clone();
				}
				j++;
			}
		}
		retVal = min;
		mRTable[m][n].value = min;
		mRTable[m][n].strategy = minpI;
		Log.i(TAG+".f", strPrefix + "minpI = " + toString(minpI, offset));
		
		return retVal;
	}
}