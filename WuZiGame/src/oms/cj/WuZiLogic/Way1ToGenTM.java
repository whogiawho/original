package oms.cj.WuZiLogic;

import java.util.ArrayList;
import android.util.Log;
import oms.cj.WuZiWay.TypeMap;
import oms.cj.WuZiWay.Way4;

@SuppressWarnings("unused")
public class Way1ToGenTM implements IGenerateTypeMap, ILiveOrChong {
	private final static String TAG = "Way1ToGenTM";
	
	private VirtualWuZiBoard mBoard;
	private ILiveOrChong mILC;
	
	public Way1ToGenTM(VirtualWuZiBoard b){
		mBoard = b;
		
		mILC = new Way1ToLC();
	}
	
	@Override
	public void updateMatrixInvalid(int[][] matrix){
		for(int i=0;i<mBoard.getRows()*mBoard.getCols();i++)
			for(int j=0;j<VirtualWuZiBoard.LINEDIRTOTAL;j++){
				int[] pos = mBoard.oneDim2twoDim(i);
				if(mBoard.getQiZiType(pos)==VirtualWuZiBoard.EMPTY)
					continue;
				Result r = getLiveOrChong(mBoard, pos, j);
				if(r.getType()==VirtualWuZiBoard.INVALID)
					matrix[i][j]=Way4.INVALID;
			}
	}
	
	@Override
	public TypeMap generateTypeMap(VirtualWuZiBoard b, int qiZiType, int[][] mInvalid, int[] typeScore){
		TypeMap tMap = new TypeMap(qiZiType);
		int max=Integer.MIN_VALUE;
		
		//long start = System.currentTimeMillis();

		ArrayList<Integer> posList = b.getQiZiList(qiZiType);
		for(int j=0;j<posList.size();j++){
			int idx = posList.get(j);
			int[] cPos = b.oneDim2twoDim(idx);
			for(int i=0;i<VirtualWuZiBoard.LINEDIRTOTAL;i++){
				if(mInvalid!=null&&mInvalid[idx][i]==Way4.INVALID)
					continue;
				Result r = getLiveOrChong(b, cPos, i);
				int rType = r.getType();
				if(typeScore!=null&&typeScore[rType]==0)
					continue;
				if(rType!=VirtualWuZiBoard.INVALID){
					tMap.put(rType, r);
					int level = r.getLevel();
					if(level>max)
						max = level;
				}
			}					
		}

		tMap.setMinSteps2Be5(max);
		
		//long end = System.currentTimeMillis();
		//Log.i(TAG+".generateTypeMap", "time used ="+(end-start));
		
		return tMap;
	}
	
	@Override
	public Result getLiveOrChong(VirtualWuZiBoard b, int[] pos, int dir){
		return mILC.getLiveOrChong(b, pos, dir);
	}
}
