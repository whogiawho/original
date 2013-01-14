package oms.cj.WuZiLogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import android.util.Log;

public class Result {
	private final static String TAG = "Result";
	public final static String LEVELUNIT = "   ";
	
	//indexs
	private ArrayList<int[]> posList = new ArrayList<int[]>();
	private int dir;
	private VirtualWuZiBoard mBoard;
	
	Result(int[] pos, int dir, VirtualWuZiBoard b){
		posList.add(pos);
		this.dir = dir;
		mBoard = b;
	}
	
	public void addPos(int[] pos){
		posList.add(pos);
	}
	
	public void addFirstQiZiSet(HashSet<Integer> set){
		if(firstQiZiSet!=null)
			firstQiZiSet.addAll(set);
		else {
			//in this switch, it means that it is an INVALID Result instance
			String out = String.format("firstQiZiSet=null, posList=%s", VirtualWuZiBoard.toString(posList));
			Log.d(TAG+".Result.addFirstQiZiSet", out);
		}
	}
	public void addFirstQiZiSetEx(HashSet<Integer> set){
		if(firstQiZiSetEx!=null)
			firstQiZiSetEx.addAll(set);
		else {
			//in this switch, it means that it is an INVALID Result instance
			String out = String.format("firstQiZiSetEx=null, posList=%s", 
					VirtualWuZiBoard.toString(posList));
			Log.d(TAG+".Result.addFirstQiZiSetEx", out);
		}
	}
	public void set(int ret0, int ret1, 
			HashSet<Integer> steps, HashSet<Integer> firstQiZiSet, HashSet<Integer> firstQiZiSetEx){
		ret[0] = ret0;
		ret[1] = ret1;
		this.steps = steps; 
		this.firstQiZiSet = firstQiZiSet;
		this.firstQiZiSetEx = firstQiZiSetEx;
	}
	
	//results
	private int[] ret = new int[2];
	private HashSet<Integer> steps = null;
	private HashSet<Integer> firstQiZiSet = null;
	private HashSet<Integer> firstQiZiSetEx = null;
	public ArrayList<int[]> getPosList(){
		return posList;
	}
	public HashSet<Integer> getSteps(){
		return steps;
	}
	public HashSet<Integer> getFirstQiZiSet(){
		return firstQiZiSet;
	}
	public HashSet<Integer> getFirstQiZiSetEx(){
		HashSet<Integer> retSet = new HashSet<Integer>();
			
		retSet.addAll(firstQiZiSet);
		retSet.addAll(firstQiZiSetEx);
		
		return retSet;
	}
	public int getLevel(){
		return ret[1];
	}
	public int getAttr(){
		return ret[0];
	}
	public void setAttr(int ret0){
		ret[0] = ret0;
	}
	public int getType(){
		return VirtualWuZiBoard.getType(ret);
	}
	public int getDir(){
		return dir;
	}
	public VirtualWuZiBoard getBoard(){
		return mBoard;
	}
	
	//if dir != r.dir, equal always return false;		
	public boolean equal(Result r){
		boolean bSteps=false;
		if(steps==null&&r.steps==null||
				(steps!=null&&r.steps!=null&&steps.equals(r.steps)))
			bSteps = true;
		return dir==r.dir && ret[0]==r.ret[0] && ret[1]==r.ret[1] && bSteps;
	}
	
	public String toString(int level){
		return toString();
	}
	
	public String toString(){
		String out = "";
		
		String header = VirtualWuZiBoard.toString(posList);
		header += VirtualWuZiBoard.dirStrList[dir] + ": ";
		
		out = String.format("%s%s ", header, VirtualWuZiBoard.lcToString(ret));

		if(firstQiZiSet!=null){
			out += mBoard.toString(getFirstQiZiSet());
			out += "   |||   ";
			out += mBoard.toString(getFirstQiZiSetEx());
		}
		if(steps!=null){
			out += "   |||   ";
			out += mBoard.toString(steps);
		}
		
		return out;
	}
	
	public static HashSet<Integer> intersectFirstQiZiSetEx(Result a, Result b){
		HashSet<Integer> is = new HashSet<Integer>();
		
		HashSet<Integer> ex1 = a.getFirstQiZiSetEx();
		HashSet<Integer> ex2 = b.getFirstQiZiSetEx();
		
		is = intersection(ex1, ex2);
		
		return is;
	}
	
    public static HashSet<Integer> intersection(HashSet<Integer> a, HashSet<Integer> b) {
        HashSet<Integer> c = new HashSet<Integer>();
        for (Iterator<Integer> iter = b.iterator(); iter.hasNext(); ) {
            Integer e = iter.next();
            if (a.contains(e)) {
                c.add(e);
            }
        }
        return c;
    }
}
