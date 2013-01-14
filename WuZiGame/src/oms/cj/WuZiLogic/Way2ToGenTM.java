package oms.cj.WuZiLogic;

import java.util.ArrayList;
import oms.cj.WuZiWay.EmptyTypeMap;
import oms.cj.WuZiWay.Way4;

public class Way2ToGenTM implements IGenerateTypeMap, ILiveOrChong {
	private VirtualWuZiBoard mBoard;
	private ILiveOrChong mILC;
	
	Way2ToGenTM(VirtualWuZiBoard b){
		mBoard = b;
		mILC = new Way2ToLC();
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
	public EmptyTypeMap generateTypeMap(VirtualWuZiBoard b, int qiZiType, int[][] mInvalid, int[] typeScore){
		EmptyTypeMap tMap = new EmptyTypeMap(qiZiType, VirtualWuZiBoard.LINEDIRTOTAL, b);
		int max=Integer.MIN_VALUE;
		Params p = new Params();
		
		//long start = System.currentTimeMillis();

		ArrayList<Integer> posList = b.getQiZiList(qiZiType);
		for(int j=0;j<posList.size();j++){
			int idx = posList.get(j);
			int[] cPos = b.oneDim2twoDim(idx);
			for(int i=0;i<VirtualWuZiBoard.LINEDIRTOTAL;i++){
				if(mInvalid!=null&&mInvalid[idx][i]==Way4.INVALID)
					continue;
				
				Result r = new Result(cPos,i, b);
				((Way2ToLC)mILC).getLiveOrChong(b, qiZiType, cPos, i, false, p, r, tMap);
				
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
