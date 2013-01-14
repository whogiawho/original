package oms.cj.WuZiWay;

import java.util.HashSet;

interface IPosition {
	HashSet<Integer> getPosition(TypeMap mapInfo, int qiZiType);
}