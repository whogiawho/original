package oms.cj.twenty4upgrade;

import android.util.Log;

public class tree{
	private static final String TAG="tree";
	public final static int left=0;
	public final static int right=1;
		
	private String melement;
	private tree mleft, mright;
		
	private boolean already;
	double postval=0.0;
	String postfixstring="";
	
	tree(){
		mleft=null;
		mright=null;
		melement=null;
		already=false;
	}

	tree(tree left, tree right, String element){
		mleft = left;
		mright = right;
		melement = element;
		already = false;
	}
	
	public void calculate(){
		if(already){
			return;
		} else if(isleaf()){
			postval = Double.valueOf(getnode());
			postfixstring = getnode();
			already=true;
		} else {
			tree t1 =getsubtree(tree.left);
			tree t2 =getsubtree(right);
			t1.calculate();
			t2.calculate();
			postval = calbin(t1.postval, t2.postval, getnode());
			postfixstring = t1.postfixstring + " " + t2.postfixstring + " " + getnode();
			already=true;
		}
	}

    private double calbin(double fa, double fb, String operator){
		double temp=Double.MAX_VALUE;
		
		if(fb==0 && operator.equals("/"))
			return Double.MAX_VALUE;
		//fix the bug of treating forms like "25 - 1/(2/0) = 25" to be a reasonable answer
		if(fa == Double.MAX_VALUE || fb == Double.MAX_VALUE)
			return Double.MAX_VALUE;
		
		switch (operator.charAt(0)) {
		case '+':
			temp=fa+fb;
			break;
		case '-':
			temp=fa-fb;
			break;
		case '*':
			temp=fa*fb;
			break;
		case '/':
			temp=fa/fb;
			break;
		default:
			Log.e(TAG, "calexpr(...): " + "exception!!!" + "operator=" + operator);
		}	
		
		return temp;
    }

	public void setnode(String element){
		melement=element;
	}
		
	public void setsubtree(int sub, tree subtree){
		if(sub==left){
			mleft = subtree;
		} else {
			mright = subtree;
		}
	}
		
	public tree getsubtree(int sub){
		tree subtree;
		if(sub==left){
			subtree = mleft;
		} else {
			subtree = mright;	
		}
		return subtree;
	}
		
	public String getnode(){
		return melement;
	}
	
	public boolean isleaf(){
		if(mleft==null && mright==null)
			return true;
		else 
			return false;
	}
	
	public String toString(){
		if(isleaf()){
			return getnode();
		} else {
			return mleft.toString() + mright.toString() + melement;
		}
	}
	
	public static boolean equals(tree t1, tree t2){
		if(t1==null && t2==null)
			return false;
		else if((t1==null && t2!=null) ||(t1!=null && t2==null))
			return false;
		else {
			if(t1.getnode()!=t2.getnode())
				return false;
			boolean b1 = equals(t1.getsubtree(left), t2.getsubtree(left));
			boolean b2 = equals(t1.getsubtree(right), t2.getsubtree(right));
			return b1&&b2;
		}
	}
}
