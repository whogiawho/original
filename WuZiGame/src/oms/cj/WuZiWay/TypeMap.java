package oms.cj.WuZiWay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import oms.cj.WuZiLogic.Result;
import oms.cj.WuZiLogic.VirtualWuZiBoard;

public class TypeMap {
	@SuppressWarnings("unused")
	private final static String TAG = "TypeMap";
	private HashMap<Integer, ArrayList<Result>> rMaps;
	private int minSteps2Be5;
	@SuppressWarnings("unused")
	private int mQiZiType;
	
	public TypeMap(int qiZiType){
		mQiZiType = qiZiType;
		
		rMaps = new HashMap<Integer, ArrayList<Result>>();
	
		init(rMaps);
	}
	
	private void init(HashMap<Integer, ArrayList<Result>> maps){
		maps.put(VirtualWuZiBoard.INVALID, new ArrayList<Result>());
		maps.put(VirtualWuZiBoard.CHONG1, new ArrayList<Result>());
		maps.put(VirtualWuZiBoard.CHONG2, new ArrayList<Result>());
		maps.put(VirtualWuZiBoard.CHONG3, new ArrayList<Result>());
		maps.put(VirtualWuZiBoard.CHONG4, new ArrayList<Result>());
		maps.put(VirtualWuZiBoard.LIVE1, new ArrayList<Result>());
		maps.put(VirtualWuZiBoard.LIVE2, new ArrayList<Result>());
		maps.put(VirtualWuZiBoard.LIVE3, new ArrayList<Result>());
		maps.put(VirtualWuZiBoard.LIVE4, new ArrayList<Result>());
		maps.put(VirtualWuZiBoard.CHONG5, new ArrayList<Result>());
	}
	
	public void put(int rType, Result r){
		ArrayList<Result> rList = rMaps.get(rType);
		if(rList==null){
			String out = String.format("rType=%d, r=%s", rType, r.toString());
			throw new IllegalStateException(out);
		}
		
		int i;
		Result r1 = null;
		for(i=0;i<rList.size();i++){
			r1 = rList.get(i);
			if(r1.equal(r))
				break;
		}
		if(i!=rList.size()){ 
			int[] cPos = r.getPosList().get(0);
			r1.addPos(cPos);
			r1.addFirstQiZiSet(r.getFirstQiZiSet());
			r1.addFirstQiZiSetEx(r.getFirstQiZiSetEx());
		} else
			rList.add(r);
	}
	
	public void setMinSteps2Be5(int value){
		minSteps2Be5 = value;
	}
	
	public int getMinSteps2Be5(){
		return minSteps2Be5;
	}
	
	public HashSet<Integer> getSanSan(){
		HashSet<Integer> sansanSet = new HashSet<Integer>();
		
		ArrayList<Result> rList = rMaps.get(VirtualWuZiBoard.LIVE2);
		for(int i=0;i<rList.size();i++){
			Result r1 = rList.get(i);
			for(int j=i+1;j<rList.size();j++){
				Result r2 = rList.get(j);
				HashSet<Integer> is = Result.intersectFirstQiZiSetEx(r1, r2);
				if(is.size()!=0)
					sansanSet.addAll(is);
			}
		}
		
		return sansanSet;
	}
	
	public HashSet<Integer> getSimpleSiSi(){
		HashSet<Integer> sisiSet = new HashSet<Integer>();
		
		ArrayList<Result> rList = new ArrayList<Result>();
		ArrayList<Result> rList1 = rMaps.get(VirtualWuZiBoard.LIVE3);
		ArrayList<Result> rList2 = rMaps.get(VirtualWuZiBoard.CHONG3);
		rList.addAll(rList1);
		rList.addAll(rList2);
		
		for(int i=0;i<rList.size();i++){
			Result r1 = rList.get(i);
			for(int j=i+1;j<rList.size();j++){
				Result r2 = rList.get(j);
				HashSet<Integer> is = Result.intersectFirstQiZiSetEx(r1, r2);
				if(is.size()!=0)
					sisiSet.addAll(is);
			}
		}
		
		return sisiSet;
	}
	
	public HashSet<Integer> getC2C3C3(){
		HashSet<Integer> c2c3c3Set = new HashSet<Integer>();

		ArrayList<Result> rList1 = rMaps.get(VirtualWuZiBoard.CHONG2);
		ArrayList<Result> rList2 = rMaps.get(VirtualWuZiBoard.CHONG3);
		
		for(int i=0;i<rList1.size();i++){
			Result r1 = rList1.get(i);
			int count=0;
			c2c3c3Set.clear();
			for(int j=0;j<rList2.size();j++){
				Result r2 = rList2.get(j);
				HashSet<Integer> is = Result.intersectFirstQiZiSetEx(r1, r2);
				if(is.size()!=0){
					count++;
					c2c3c3Set.addAll(is);
				}
			}
			if(count>=2)
				break;
		}
		
		if(c2c3c3Set.size()<2)
			c2c3c3Set.clear();
		
		return c2c3c3Set;
	}

	private HashSet<Integer> filterSetForL1C3C3(VirtualWuZiBoard b, HashSet<Integer> l1c3c3Set){
		Integer[] a = new Integer[l1c3c3Set.size()];
		HashSet<Integer> set = new HashSet<Integer>();
		l1c3c3Set.toArray(a);
		
_L0:
		for(int i=0;i<a.length;i++){
			for(int j=i+1;j<a.length;j++){
				if(b.getDistance(a[i], a[j])<=3){
					set.add(a[i]);
					set.add(a[j]);
					break _L0;
				}
			}
		}
		
		return set;
	}
	
	public HashSet<Integer> getL1C3C3(VirtualWuZiBoard b){
		HashSet<Integer> l1c3c3Set = new HashSet<Integer>();
		ArrayList<Result> rList1 = rMaps.get(VirtualWuZiBoard.LIVE1);
		ArrayList<Result> rList2 = rMaps.get(VirtualWuZiBoard.CHONG3);
				
		for(int i=0;i<rList1.size();i++){
			Result r1 = rList1.get(i);
			int count=0;
			l1c3c3Set.clear();
			for(int j=0;j<rList2.size();j++){
				Result r2 = rList2.get(j);
				HashSet<Integer> is = Result.intersectFirstQiZiSetEx(r1, r2); 
				if(is.size()!=0){
					count++;
					l1c3c3Set.addAll(is);
				}
			}
			if(count>=2){
				l1c3c3Set = filterSetForL1C3C3(b, l1c3c3Set);
				if(l1c3c3Set.size()==2)
					break;
			}
		}
		
		if(l1c3c3Set.size()<2)
			l1c3c3Set.clear();
		
		return l1c3c3Set;
	}

	public HashSet<Integer> getSiSan(){
		HashSet<Integer> sisanSet = new HashSet<Integer>();

		ArrayList<Result> rList1 = rMaps.get(VirtualWuZiBoard.CHONG3);		
		ArrayList<Result> rList2 = rMaps.get(VirtualWuZiBoard.LIVE2);
		
		for(int i=0;i<rList1.size();i++){
			Result r1 = rList1.get(i);
			for(int j=0;j<rList2.size();j++){
				Result r2 = rList2.get(j);
				HashSet<Integer> is = Result.intersectFirstQiZiSetEx(r1, r2);
				if(is.size()!=0)
					sisanSet.addAll(is);
			}
		}
		
		return sisanSet;
	}

    public String toString(){
    	String out = "";
    	
    	Iterator<ArrayList<Result>> it; 
	    for (it = rMaps.values().iterator(); it.hasNext();) {
	        ArrayList<Result> rList = it.next();
	        out += VirtualWuZiBoard.ResultListToString(rList);
	      }
	    
    	return out;
    }
    
    public HashSet<Integer> getCandidates(int type){
    	HashSet<Integer> sets = new HashSet<Integer>();
    	
    	ArrayList<Result> rList = rMaps.get(type);	
    	for(int i=0;i<rList.size();i++){
    		Result r = rList.get(i);
    		HashSet<Integer> ex = r.getFirstQiZiSetEx();
    		
    		sets.addAll(ex);
    	}
    	
    	return sets;
    }
    
    public ArrayList<Result> getMaps(int type){
    	return rMaps.get(type);
    }
    
    public boolean C4L3C3L2C2Empty(){
    	boolean bEmpty = false;
    	
    	int c4Size = rMaps.get(VirtualWuZiBoard.CHONG4).size();
    	int c3Size = rMaps.get(VirtualWuZiBoard.CHONG3).size();
    	int c2Size = rMaps.get(VirtualWuZiBoard.CHONG2).size();
    	int l3Size = rMaps.get(VirtualWuZiBoard.LIVE3).size();
    	int l2Size = rMaps.get(VirtualWuZiBoard.LIVE2).size();
    	if(c4Size==0&&c3Size==0&&c2Size==0&&l3Size==0&&l2Size==0)
    		bEmpty = true;
    	
    	return bEmpty;
    }
    
	public boolean searchIn(int pos, int exDir, int rType, HashSet<Integer> others){
		boolean bFound = false;
	
		ArrayList<Result> rList = rMaps.get(rType);
		for(int i=0;i<rList.size();i++){
			Result r = rList.get(i);
			if(r.getDir()==exDir)
				continue;
			if(r.getFirstQiZiSetEx().contains(pos)){
				bFound = true;
				if(others!=null){
					others.addAll(r.getFirstQiZiSetEx());
					others.remove(pos);
				}
				break;
			}
		}
		
		return bFound;
	}
	
	public boolean searchIn(VirtualWuZiBoard b, int pos, int exDir, 
			int rType, HashSet<Integer> others){
		boolean bFound = false;
	
		ArrayList<Result> rList = rMaps.get(rType);
		for(int i=0;i<rList.size();i++){
			Result r = rList.get(i);
			if(r.getDir()==exDir)
				continue;
			HashSet<Integer> exSet = r.getFirstQiZiSetEx(); 
			if(exSet.contains(pos)){
				bFound = true;
				if(others!=null){
					if(rType!=VirtualWuZiBoard.LIVE1)
						others.addAll(exSet);
					else {
						Iterator<Integer> itr = exSet.iterator();
						while(itr.hasNext()){
							int idx = itr.next();
							if(b.getDistance(pos, idx)<=3)
								others.add(idx);
						}
					}
					others.remove(pos);
				}
				break;
			}
		}
		
		return bFound;
	}
}
