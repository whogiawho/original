package oms.cj.WuZiLogic;

import oms.cj.WuZiWay.TypeMap;

public interface IGenerateTypeMap {
	public TypeMap generateTypeMap(VirtualWuZiBoard b, int qiZiType, int[][] mInvalid, int[] typeScore);
	public void updateMatrixInvalid(int[][] matrix);
}
