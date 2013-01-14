package oms.cj.testc;

import oms.cj.utils.Combination;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class TestCActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Combination c = new Combination(Combination.ZEROSTART);
        c.open(10000, 3); 
        Try tc = new Try(c);
        Thread t = new Thread(tc);
        t.start();
        
        Button submit = (Button)this.findViewById(R.id.stop);
        submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				c.close();
				
			}
		});
    }
    
    private class Try implements Runnable {
    	Combination mC;
    	Try(Combination c){
    		mC = c;
    	}
		@Override
		public void run() {
	        for(int i=0;i<mC.totalValues();i++){
	        	int[] pI;
				try {
					pI = mC.getNext();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i("Try.run", "combination thread is interrupted!!");
					break;
				}
	        	Log.i("TestActivity.onCreate", "length = " + pI.length);
	        	Log.i("TestActivity.onCreate", Combination.intArray2String(pI, 0));
	        }
	        mC.close();			
		}
    }
}