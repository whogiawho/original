/*
    balance, An OPhone game to practise da ju guan

    Copyright (C) <2009>  chenjian

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    You can contact me by email ustcchenjian@gmail.com
*/

package oms.cj.balance;

import java.util.*;

import android.util.*;
import android.graphics.*;


@SuppressWarnings("unchecked")
public class plate {
	private static final int width=10;
	private static final int height=10;
	private int board[][]=new int[height][width];
	private int[][] ballres=new int[MAXBALL+1][2];	
	private ArrayList<Point>[][] nslboard=new ArrayList[height][width];
	private int[] nextballs=new int[NEXTNUM];
	private int[] nextballres=new int[MAXBALL+1];
	private Point cursor;
	private Random mrandb, mrandnb;
	private Hashtable<Integer,Integer> styleR2idx=new Hashtable<Integer,Integer>();

	private static final String TAG = "plate";
	//balls 
	private static int MINNOTEXISTLEN=5;
	private static int MAXBALL=4;
	public static final int NULL=0;
	private static final int RED=1;
	private static final int GREEN=2;
	private static final int YELLOW=3;
	private static final int BROWN=4;
	
    private static final int INITNUM=20;
    private static final int NEXTNUM=3;
    public static final int FOUND=1;
    public static final int NOTFOUND=0;
    private static final int NOCURSOR=0;
    private static final int WITHCURSOR=1;
    
	public static final Point FULLPOINT=new Point(-1,-1);
	public static final Point NULLCURSOR=FULLPOINT;
    //Directions of a point
	private static final int LEFT=0;
	private static final int RIGHT=1;
	private static final int TOP=2;
	private static final int BOTTOM=3;
	private static final int LT=4;
	private static final int RB=5;
	private static final int LB=6;
	private static final int RT=7;
	private static final int DIRNUMBERS=8;	
	private static final Point deltap[]={new Point(0,-1), new Point(0,1), new Point(-1,0), new Point(1,0),
										 new Point(-1,-1), new Point(1,1), new Point(1,-1), new Point(-1,1)};
	//Directions of a line
	public static final int LR=0;     //  =RIGHT/2
	public static final int TB=1;     //  =BOTTOM/2
	public static final int LTRB=2;   //  =RB/2
	public static final int LBRT=3;   //  =RT/2
	public static final int LINENUMBERS=4;
	
	private static int scmode=0;
	private static final int imagesize[]={24,36};
	private int easyidx=0, hardidx=0;
	private static final int[] easycycle={0,1,0,1,0};
	private static final int[] hardcycle={1,0,1,0,1};

	//image mode
	public static final int IMAGEMODEHVGA=0;
	public static final int IMAGEMODEWVGA=1;
	
	//level instance
	public static final int EASY=R.id.leveleasy;
	public static final int MEDIUM=R.id.levelmedium;
	public static final int HARD=R.id.levelhard;
	//增加一个style需要完成的事项
	//1. 准备10张png图片，添加到drawable目录中
	//2. 在layout/configapp.xml中添加一个style RadioButton
	//3. 在strings.xml中为该RadioButton起个名字
	//4. 添加那10张图片的资源ID到数组style中
	//5. 在函数initstyle()中，添加style RadioButton的资源ID和该style在数组style中的下标映射
	//   至styleR2idx
	//style index
	public static final int BALLSTYLE=R.id.ballstyle;
	public static final int ALTSTYLE=R.id.altstyle;
	public static final int ASTROSTYLE=R.id.astrostyle;
	//style instances
	private static final int[][][] style={
	  {	
		{R.drawable.hvga_background, R.drawable.hvga_red, R.drawable.hvga_green, R.drawable.hvga_yellow, R.drawable.hvga_brown,
		 R.drawable.hvga_backgroundwithcursor, R.drawable.hvga_redwithcursor, R.drawable.hvga_greenwithcursor, R.drawable.hvga_yellowwithcursor, R.drawable.hvga_brownwithcursor},
		{R.drawable.hvga_altbackground, R.drawable.hvga_che, R.drawable.hvga_ma, R.drawable.hvga_pao, R.drawable.hvga_xiang,
		 R.drawable.hvga_altbackgroundwithcursor, R.drawable.hvga_chewithcursor, R.drawable.hvga_mawithcursor, R.drawable.hvga_paowithcursor, R.drawable.hvga_xiangwithcursor},
		{R.drawable.hvga_astrobackground, R.drawable.hvga_taurus, R.drawable.hvga_lion, R.drawable.hvga_cancer, R.drawable.hvga_gemini,
		 R.drawable.hvga_astrobackgroundwithcursor, R.drawable.hvga_tauruswithcursor, R.drawable.hvga_lionwithcursor, R.drawable.hvga_cancerwithcursor, R.drawable.hvga_geminiwithcursor}
	  },		 
	  {
		{R.drawable.wvga_background, R.drawable.wvga_red, R.drawable.wvga_green, R.drawable.wvga_yellow, R.drawable.wvga_brown,
		 R.drawable.wvga_backgroundwithcursor, R.drawable.wvga_redwithcursor, R.drawable.wvga_greenwithcursor, R.drawable.wvga_yellowwithcursor, R.drawable.wvga_brownwithcursor},
		{R.drawable.wvga_altbackground, R.drawable.wvga_che, R.drawable.wvga_ma, R.drawable.wvga_pao, R.drawable.wvga_xiang,
		 R.drawable.wvga_altbackgroundwithcursor, R.drawable.wvga_chewithcursor, R.drawable.wvga_mawithcursor, R.drawable.wvga_paowithcursor, R.drawable.wvga_xiangwithcursor},
		{R.drawable.wvga_astrobackground, R.drawable.wvga_taurus, R.drawable.wvga_lion, R.drawable.wvga_cancer, R.drawable.wvga_gemini,
		 R.drawable.wvga_astrobackgroundwithcursor, R.drawable.wvga_tauruswithcursor, R.drawable.wvga_lionwithcursor, R.drawable.wvga_cancerwithcursor, R.drawable.wvga_geminiwithcursor}	  
	  }
	};
	
	private void initstyle(){
		styleR2idx.put(R.id.ballstyle, 0);
		styleR2idx.put(R.id.altstyle, 1);
		styleR2idx.put(R.id.astrostyle, 2);
	}
	
	private int mapstyleR2idx(int styleR){
		return styleR2idx.get(styleR);
	}
	
	public void setnextballs(int externalnextballs[]){
		int i;
		
		for(i=0;i<this.getmaxnextballs();i++){
			this.nextballs[i]=externalnextballs[i];
		}
	}
	
	public void setboard(int externalborad[][]){
		int i,j;
		
		for(i=0;i<getheight();i++)
			for(j=0;j<getwidth();j++)
				board[i][j]=externalborad[i][j];
	}
	
	private class Line{
		Point start;
		Point end;
		int direction;
		Line(Point x, Point y, int dir){
			start=x;
			end=y;
			direction=dir;
		}
		ArrayList<Point> getallpoints(){
			ArrayList<Point> lps=new ArrayList<Point>();
			Point p;
			
			for(p=start;!p.equals(end);p=deltapoint(p,direction)){
				lps.add(p);
			}
			lps.add(p);
			return lps;
		}
	}
	
	plate(int styleR){
		int i,j,k;
		
		//创建styleR到style【】【】数组的第一维idx的转换
		initstyle();
		
		//初始化棋盘为全空
		for(i=0;i<height;i++)
			for(j=0;j<width;j++)
				board[i][j]=NULL;
		//棋盘随机填充INITNUM个球
		mrandb=new Random();		
		k=0;
		while(k<INITNUM){	
			j=mrandb.nextInt(width);
			i=mrandb.nextInt(height);
			if(board[i][j]!=NULL)
				continue;
			else {
				board[i][j]=mrandb.nextInt(MAXBALL)+1;
				k++;
			}
		}
		
		//设置棋子的风格
		int idxstyle=mapstyleR2idx(styleR);
		ballres[NULL][NOCURSOR]=style[scmode][idxstyle][0];
        ballres[RED][NOCURSOR]=style[scmode][idxstyle][1];
        ballres[GREEN][NOCURSOR]=style[scmode][idxstyle][2];
        ballres[YELLOW][NOCURSOR]=style[scmode][idxstyle][3];
        ballres[BROWN][NOCURSOR]=style[scmode][idxstyle][4];
		ballres[NULL][WITHCURSOR]=style[scmode][idxstyle][5];
        ballres[RED][WITHCURSOR]=style[scmode][idxstyle][6];
        ballres[GREEN][WITHCURSOR]=style[scmode][idxstyle][7];
        ballres[YELLOW][WITHCURSOR]=style[scmode][idxstyle][8];
        ballres[BROWN][WITHCURSOR]=style[scmode][idxstyle][9];
		nextballres[NULL]=style[scmode][idxstyle][0];
		nextballres[RED]=style[scmode][idxstyle][1];
		nextballres[GREEN]=style[scmode][idxstyle][2];
		nextballres[YELLOW]=style[scmode][idxstyle][3];
		nextballres[BROWN]=style[scmode][idxstyle][4];

		//初始化nextballs
        mrandnb=new Random();
		for(k=0;k<NEXTNUM;k++){
			nextballs[k]=mrandnb.nextInt(MAXBALL)+1;
		}

		//设置缺省光标位置为棋盘中间
        cursor=new Point(getheight()/2,getwidth()/2);
        
        //初始化棋盘的每个位置的相邻位置，存在nslboard中
        gennslboard();
	}	
	
	private ArrayList<Point> _getnextstep_(Point src){
		ArrayList<Point> nextsteplist=new ArrayList<Point>();
	
		if(src.x==0&&src.y==0){
			nextsteplist.add(new Point(1,0));
			nextsteplist.add(new Point(0,1));
		} else if(src.x==0&&src.y==width-1){
			nextsteplist.add(new Point(1,width-1));
			nextsteplist.add(new Point(0,width-2));
		} else if(src.x==height-1&&src.y==0){
			nextsteplist.add(new Point(height-2,0));
			nextsteplist.add(new Point(height-1,1));
		} else if(src.x==height-1&&src.y==width-1){
			nextsteplist.add(new Point(height-1,width-2));
			nextsteplist.add(new Point(height-2,width-1));
		} else if(src.x==0){
			nextsteplist.add(new Point(src.x,src.y-1));
			nextsteplist.add(new Point(src.x,src.y+1));
			nextsteplist.add(new Point(src.x+1,src.y));
		} else if(src.x==height-1){
			nextsteplist.add(new Point(src.x,src.y-1));
			nextsteplist.add(new Point(src.x,src.y+1));
			nextsteplist.add(new Point(src.x-1,src.y));
		} else if(src.y==0){
			nextsteplist.add(new Point(src.x-1,src.y));
			nextsteplist.add(new Point(src.x+1,src.y));
			nextsteplist.add(new Point(src.x,src.y+1));
		} else if(src.y==width-1){
			nextsteplist.add(new Point(src.x-1,src.y));
			nextsteplist.add(new Point(src.x+1,src.y));
			nextsteplist.add(new Point(src.x,src.y-1));
		} else {
			nextsteplist.add(new Point(src.x-1,src.y));
			nextsteplist.add(new Point(src.x+1,src.y));
			nextsteplist.add(new Point(src.x,src.y-1));
			nextsteplist.add(new Point(src.x,src.y+1));			
		}
		
		return nextsteplist;
	}
	
	public static int getimagesize(){
		return imagesize[scmode];
	}
	
	private void gennslboard(){
		int i,j;
		Point p;
		
		for(i=0;i<getheight();i++)
			for(j=0;j<getwidth();j++){
				p=new Point(i,j);
				nslboard[i][j]=_getnextstep_(p);
			}
	}
	public void setcursor(Point p){
		cursor=p;
	}
	public static void setscmode(int mode){
		scmode=mode;
	}
	public static int getscmode(){
		return scmode;
	}
	public Point getcursor(){
		return cursor;
	}
	
	public int getmaxnextballs(){
		return NEXTNUM;
	}
	public static int getwidth(){
		return width;
	}
	
	public static int getheight(){
		return height;
	}
	public void removenextballs(int i){
		nextballs[i]=NULL;
	}
	public void gennextballs(int i){
		nextballs[i]=mrandnb.nextInt(MAXBALL)+1;
	}
	public void gennextballs(){
		int k;
		
		for(k=0;k<NEXTNUM;k++){
			nextballs[k]=mrandnb.nextInt(MAXBALL)+1;
		}		
	}
	public void setballtop(Point p, int ball){
		board[p.x][p.y]=ball;
	}
	public void moveball(Point src, Point tgt){
		board[tgt.x][tgt.y]=board[src.x][src.y];
		board[src.x][src.y]=NULL;
	}
	public void removeball(Point p){
		board[p.x][p.y]=NULL;
	}
	public int ball(int x, int y){
		return board[x][y];
	}
	
	public int ball(Point p){
		return board[p.x][p.y];
	}

	public int nextball(int i){
		return nextballs[i];
	}
	public int nextballres(int i){
		Log.i(TAG,""+nextballs[i]);
		return nextballres[nextballs[i]];
	}
	
	public void offcursor(){
		setcursor(NULLCURSOR);
	}
	private boolean iscursoron(){
		return !cursor.equals(NULLCURSOR);
	}
	
	public int ballres(Point p){
		int focus=NOCURSOR;
		if(p.x==cursor.x && p.y==cursor.y && iscursoron()){
			focus=WITHCURSOR;
		}
		return ballres[board[p.x][p.y]][focus];
	}
	
	public int ballres(int x, int y){
		int focus=NOCURSOR;
		
		if(x==cursor.x && y==cursor.y && iscursoron()){
			focus=WITHCURSOR;
		}
		
		return ballres[board[x][y]][focus];
	}
	
	public int getmaxball(){
		return MAXBALL;
	}
	public boolean isfull(){
		boolean full=true;
		ArrayList<Point> avposlist=getavpositions();
		if(avposlist.size()>0)
			full=false;
		return full;
	}
	public int getavpositionscount(){
		return getavpositions().size();
	}
	private ArrayList<Point> getavpositions(){
		ArrayList<Point> avposlist=new ArrayList<Point>();
		int i,j;
		
		for(i=0;i<height;i++)
			for(j=0;j<width;j++){
				if(board[i][j]==NULL)
					avposlist.add(new Point(i,j));
			}
		return avposlist;
	}

	private ArrayList<Point> getlist(int ball, int level){
		ArrayList<Point> balllist=new ArrayList<Point>();
		ArrayList<Point> retlist=new ArrayList<Point>();
		Random r=new Random();
		
		for(int i=0;i<height;i++)
			for(int j=0;j<width;j++){
				if(level==EASY && board[i][j]==ball)
					balllist.add(new Point(i,j));
				else{
					if(level==HARD && board[i][j]!=NULL && board[i][j]!=ball)
						balllist.add(new Point(i,j));
				}
			}
	
		//randomize the list
		int listlen=balllist.size();
		for(int i=0;i<listlen;i++){
			int idx=r.nextInt(listlen-i);
			retlist.add(balllist.remove(idx)); 
		}
		return retlist;
	}
	
	private Point easydistribute(int ball){
		Point p=FULLPOINT;
		ArrayList<Point> avposlist=getavpositions();
		Line[] lines;
		
		if(avposlist.size()!=0){
			if(easycycle[easyidx]==1){  //需要考虑easy
				ArrayList<Point> balllist=getlist(ball, EASY);
				boolean found=false;
				for(Point q: balllist){   //循环所有颜色为ball的球
					lines=get4lines(q);   
					for(int i=0;i<lines.length;i++){   //循环该ball的4条可能的边
						ArrayList<Point> ptlist=lines[i].getallpoints();
						if(ptlist.size()<MINNOTEXISTLEN)
							continue;
						else{
							for(Point t: ptlist){           //循环边上的每个点
								if(board[t.x][t.y]==NULL){  //如果这些边的某点有空位
									found=true;
									p=t;
									setballtop(p,ball);
									break;
								}
							}
						}
						if(found)
							break;
					}
					if(found)
						break;
				}
				//如果均无，那么随机分配
				if(!found)
					p=mediumdistribute(ball);
			} else {
				//不需要考虑easy
				p=mediumdistribute(ball);
			}
		}
		easyidx=(easyidx+1)%easycycle.length;
		
		Log.i(TAG, "easydistribute(...): "+"p="+p.toString());
		Log.i(TAG, "easydistribute(...): "+"ball="+ball);
		return p;
	}
	
	//medium is to distribute "ball" randomly
	private Point mediumdistribute(int ball){
		Point p=FULLPOINT;
		Random r=new Random();
		ArrayList<Point> avposlist=getavpositions();
		
		if(avposlist.size()!=0){
			p=avposlist.get(r.nextInt(avposlist.size()));
			setballtop(p,ball);
		}
		
		return p;		
	}
	
	//hard is to distribute "ball" as below
	private Point harddistribute(int ball){
		Point p=FULLPOINT;
		ArrayList<Point> avposlist=getavpositions();
		Line[] lines;
		
		if(avposlist.size()!=0){
			if(hardcycle[hardidx]==1){  //需要考虑hard
				ArrayList<Point> balllist=getlist(ball, HARD);
				boolean found=false;
				for(Point q: balllist){   //循环所有颜色为ball的球
					lines=get4lines(q);   
					for(int i=0;i<lines.length;i++){   //循环该ball的4条可能的边
						ArrayList<Point> ptlist=lines[i].getallpoints();
						if(ptlist.size()<MINNOTEXISTLEN)
							continue;
						else{
							for(Point t: ptlist){           //循环边上的每个点
								if(board[t.x][t.y]==NULL){  //如果这些边的某点有空位
									found=true;
									p=t;
									setballtop(p,ball);
									break;
								}
							}
						}
						if(found)
							break;
					}
					if(found)
						break;
				}
				//如果均无，那么随机分配
				if(!found)
					p=mediumdistribute(ball);
			} else {
				//不需要考虑hard
				p=mediumdistribute(ball);
			}
		}
		hardidx=(hardidx+1)%hardcycle.length;
		
		Log.i(TAG, "harddistribute(...): "+"p="+p.toString());
		Log.i(TAG, "harddistribute(...): "+"ball="+ball);
		return p;
	}
	
	public Point distribute(int ball, int level){
		Point p;
		
		switch(level){
		case EASY:
			p=easydistribute(ball);
			break;
		case MEDIUM:
			p=mediumdistribute(ball);
			break;
		case HARD:
			p=harddistribute(ball);
			break;
		default:
			p=mediumdistribute(ball);
			break;
		}
		return p;
	}
	
	public void getconnectedpoints(Point p, ArrayList<Point> plist){
		ArrayList<Point> nextlist=new ArrayList<Point>();
		Point q;
		int i;
		
		nextlist=nslboard[p.x][p.y];
		for(i=0;i<nextlist.size();i++){
			q=nextlist.get(i);
			if(plist.indexOf(q)!=-1)
				continue;
			else{
				plist.add(q);
				if(board[q.x][q.y]==NULL)
					getconnectedpoints(q,plist);
			}
		}
	}
	
	private Point deltapoint(Point p, int direction){
		Point q=new Point(p);
		
		q.x=q.x+deltap[direction].x;
		q.y=q.y+deltap[direction].y;
		return q;
	}
	
	private Point borderpointof(Point p, int direction){
		Point q=new Point(p);
		
		switch(direction){
		case LEFT:
			q.y=0;
			break;
		case RIGHT:
			q.y=width-1;
			break;
		case TOP:
			q.x=0;
			break;
		case BOTTOM:
			q.x=height-1;
			break;
		case LT:
			while(q.x>0&&q.y>0){
				q=deltapoint(q,direction);
			}
			break;
		case RB:
			while(q.x<height-1&&q.y<width-1){
				q=deltapoint(q,direction);
			}
			break;
		case LB:
			while(q.x<height-1&&q.y>0){
				q=deltapoint(q,direction);
			}
			break;
		case RT:
			while(q.x>0&&q.y<width-1){
				q=deltapoint(q,direction);
			}
			break;
		}
		return q;
	}
	
	private Line[] get4lines(Point p){
		Line[] lines=new Line[LINENUMBERS];
		Integer i;
		Point[] borderp=new Point[DIRNUMBERS];
		
		for(i=0;i<DIRNUMBERS;i++){
			borderp[i]=borderpointof(p,i);
			//Log.i(TAG,"get4lines(p): "+"borderp"+i+"="+borderp[i].toString());
		}
		for(i=0;i<LINENUMBERS;i++){
			lines[i]=new Line(borderp[2*i],borderp[2*i+1],2*i+1);
		}
		return lines;
	}
	
	public void check(Point p, ArrayList<Point> removedpoints){
		Line[] lines;
		int start[]=new int[LINENUMBERS];
		int end[]=new int[LINENUMBERS];
		boolean found[]=new boolean[LINENUMBERS];
		int i,j,k,firstball;
		ArrayList<Point> points;
		
		//initialize found, start and end
		for(i=0;i<LINENUMBERS;i++){
			start[i]=-1;
			end[i]=-1;
			found[i]=false;
		}
		
		lines=get4lines(p);
		for(i=0;i<lines.length;i++){
			points=lines[i].getallpoints();
			//Log.i(TAG,"check(p): "+"line[i]="+points.toString());
			if(points.size()<MINNOTEXISTLEN)
				continue;
			else{
				j=0;k=0; 
				while(j<points.size()-MINNOTEXISTLEN+1){
					firstball=ball(points.get(j));
					if(firstball==NULL){
						j++;
						continue;
					}
					k=j;
					while(k<points.size()&&ball(points.get(k))==firstball)
						k++;
					//Log.i(TAG,"check(p): "+"j="+j+";k="+k);
					if(k-j>=MINNOTEXISTLEN){
						found[i]=true;
						break;
					} else{
						j=k;
					}
				}
				if(found[i]){
					start[i]=j;
					end[i]=k;
				}
			}
		}
		//remove the balls if there are balls whose continuous length is greater than MINNOTEXISTLEN
		for(i=0;i<lines.length;i++){
			if(found[i]){
				points=lines[i].getallpoints();
				for(j=start[i];j<end[i];j++){
					//Log.i(TAG,"check(p,removedpoints): "+"point="+points.get(j).toString()+";color="+ball(points.get(j)));
					removeball(points.get(j));
					removedpoints.add(points.get(j));
				}
			}
		}
	}
}