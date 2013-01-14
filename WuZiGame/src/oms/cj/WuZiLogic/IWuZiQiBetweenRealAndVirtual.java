package oms.cj.WuZiLogic;

import java.util.EventListener;


public interface IWuZiQiBetweenRealAndVirtual extends EventListener {
	public void onQiZi2Position(BoardPositionEvent e);
	public int getQiZiType(int i, int j);
	public int[] getFocus();
	public void setFocus(int[] pos);
	public int getQiZiType(int[] pos);
}
