package oms.cj.WuZiWay;

import java.util.ArrayList;
import java.util.Random;

import oms.cj.WuZiLogic.ISuggest;
import oms.cj.WuZiLogic.VirtualWuZi;

public class RandomWay implements ISuggest{
	VirtualWuZi mV;
	
	RandomWay(VirtualWuZi v){
		mV=v;
	}
	
	@Override
	public int[] suggestPosition() {
		Random r = new Random();

		ArrayList<Integer> emptyList=mV.getQiZiList(VirtualWuZi.EMPTY);
		int size=emptyList.size();
		if(size==0)
			return null;
		else {
			int randomNum = r.nextInt(emptyList.size());
			int pos = emptyList.get(randomNum);
			return mV.oneDim2twoDim(pos);
		}
	}
}