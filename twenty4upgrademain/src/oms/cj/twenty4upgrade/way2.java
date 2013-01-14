package oms.cj.twenty4upgrade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Stack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class way2 implements way{
	private final static String TAG="way2";
	private final static String[] Operators={"+", "-", "*", "/"};
	private final static int MAXPRIORITY=100;
	private final Hashtable<String,Integer> OP_PRIORITY;
	private Set<String> hashsetOperators=new HashSet<String>(Arrays.asList(Operators));
	private final static double PRECISION = 0.000000001d;
	
	ArrayList<tree> nodelist = new ArrayList<tree>();
	private final int result;
	private Handler mhandler;
	private Thread mWorkerThread;
	
	private Set<String> mInfixexprset = new HashSet<String>();

	way2(String[] digitlist, int expectedresult, Handler handler, Thread worker){
		result = expectedresult;
		mhandler=handler;
		mWorkerThread = worker;
		convert(digitlist, nodelist);
		
        OP_PRIORITY=new Hashtable<String,Integer>();
        OP_PRIORITY.put("+", 1);
        OP_PRIORITY.put("-", 1);
        OP_PRIORITY.put("*", 2);
        OP_PRIORITY.put("/", 2);
        OP_PRIORITY.put("", MAXPRIORITY);      
	}
	
	class exprresult{
		double postval=0.0;
		String postfixstring="";
	}

	//返回所有解的集合
	public Set<String> getinfixexprset() {
		return mInfixexprset;
	}

	public void run() {
		construct(0, nodelist);
        mhandler.sendEmptyMessage(caltwenty4.COMPLETE);
	}

	public String toString(ArrayList<tree> nodelist){
		String log="";
		
		for(int i=0;i<nodelist.size();i++)
			log = log + nodelist.get(i) + ", ";
		
		return log;
	}

	//static temporary variables
	private Stack<String> s2=new Stack<String>();
	private Stack<Integer> s3=new Stack<Integer>();
	public int postfix2infix(String postfixexpr, String[] val){
		String a,b,infixexpr="";
		int opa,opb;
		String[] postfixexprlist;
		int i;
		Set<String> opset=hashsetOperators;
		Stack<String> s=s2;        s.clear();
		Stack<Integer> ops=s3;     ops.clear();
		
		postfixexprlist=postfixexpr.split(" ");
		for(i=0;i<postfixexprlist.length;i++){
			if(!opset.contains(postfixexprlist[i])){
				s.push(postfixexprlist[i]);
				ops.push(MAXPRIORITY);
			} else {
				if(s.size()>0 && ops.size()>0){
					b=s.pop();
					opb=ops.pop();
				} else {
					Log.e(TAG, "syntax error!");
					return 1;
				}
				if(s.size()>0 && ops.size()>0){
					a=s.pop();
					opa=ops.pop();
				} else {
					Log.e(TAG, "syntax error!");
					return 1;
				}
				if(OP_PRIORITY.get(postfixexprlist[i])>=opb){
					b="( "+b+" )";
				}
				if(OP_PRIORITY.get(postfixexprlist[i])>opa){
					a="( "+a+" )";
				}
				infixexpr=a+" "+postfixexprlist[i]+" "+b;
				s.push(infixexpr);
				ops.push(OP_PRIORITY.get(postfixexprlist[i]));	
			}	
		}
		if(s.size()>0 && ops.size()>0){
			infixexpr=s.pop();
			ops.pop();
		}
		val[0]=infixexpr;
		if(s.size()!=0 && ops.size()!=0)
			return 1;
		return 0;
	}
	
	public void construct(int depth, ArrayList<tree> nodelist){
		String[] infixout={""};

		if(mWorkerThread.isInterrupted()){
			Log.i(TAG, "construct(...): " + "Interrupted!");
			return;
		} else {
			if(nodelist.size()==1){
				tree t=nodelist.get(0);
				t.calculate();
//				Log.i(TAG, "t.posval = " + Double.toString(t.postval));
				if(Math.abs(t.postval-result)<PRECISION){
					postfix2infix(t.postfixstring, infixout);
					if(!mInfixexprset.contains(infixout[0])){
						mInfixexprset.add(infixout[0]);					
						String infixexpr=infixout[0]+" = " + result +"\n";
						if(mhandler!=null){
							Message msg = new Message();
							msg.what = caltwenty4.NEWRESULT;
							msg.obj = infixexpr;
							mhandler.sendMessage(msg);
						}
					}
				}
			} else {
				ArrayList<int[]> list = new ArrayList<int[]>();
		
				getcombination(nodelist, list);
			
				for(int i=0;i<Operators.length;i++){
					String operator=Operators[i];
					for(int j=0;j<list.size();j++){
						int[] idxs=list.get(j);
				
						tree node1=nodelist.remove(idxs[1]); 	
						tree node2=nodelist.remove(idxs[0]);
						//2010/05/25初注
						//上述两行代码或许有问题，实际上被remove掉的不是原来的idxs[1]和idxs[0]
						//因为在第一行被执行后，第二行所要求的idxs[0]或许已经变化
						//2010/05/25再注
						//idxs【1】总是大于idxs【0】，所以不会发生初注所述问题
						tree newnode = new tree(node1, node2, operator);
						nodelist.add(newnode);
						construct(depth+1, nodelist);
						nodelist.remove(newnode);
						nodelist.add(idxs[0], node2);
						nodelist.add(idxs[1], node1);
					
						if(operator == "/" || operator == "-"){
							node1=nodelist.remove(idxs[1]); 
							node2=nodelist.remove(idxs[0]);
							newnode = new tree(node2, node1, operator);
							nodelist.add(newnode);						
							construct(depth+1, nodelist);
							nodelist.remove(newnode);
							nodelist.add(idxs[0], node2);
							nodelist.add(idxs[1], node1);		
						}
					}
				}
			}
		}	
	}
    
    private void convert(String[] strlist, ArrayList<tree> nodelist){
    	for(int i=0;i<strlist.length;i++){
    		tree t=new tree();
    		t.setnode(strlist[i]);
    		nodelist.add(t);
    	}
    }
    
    public void getcombination(ArrayList<tree> nodelist, ArrayList<int[]> list){
    	int m = nodelist.size();
    	
    	for(int i=0;i<m-1;i++)
    		for(int j=i+1;j<m;j++){
    			int[] idxs=new int[2];
    			idxs[0]=i;
    			idxs[1]=j;
    			boolean found=false;
    			for(int k=0;k<list.size();k++){
    				int[] oldidxs=list.get(k);
    				boolean b00=tree.equals(nodelist.get(idxs[0]), nodelist.get(oldidxs[0]));
    				boolean b01=tree.equals(nodelist.get(idxs[1]), nodelist.get(oldidxs[1]));
    				boolean b10=tree.equals(nodelist.get(idxs[0]), nodelist.get(oldidxs[1]));
    				boolean b11=tree.equals(nodelist.get(idxs[1]), nodelist.get(oldidxs[0]));
    				if( (b00&&b01) || (b10&&b11) )
    					found=true;
    			}
    			if(!found)
    				list.add(idxs);
    		}
    }
}