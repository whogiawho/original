package oms.cj.MinMaxEgg;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MinMaxEggActivity extends Activity {
	private final static String TAG = "MinMaxEggActivity";
	
	private EditText mFloors, mEggs;
	private Button mMinMaxExample;
	private TextView results;
	private TextView example;
	
	private MinMaxEgg mMinMaxEgg;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mFloors = (EditText)this.findViewById(R.id.floors);
        mEggs = (EditText)this.findViewById(R.id.eggs);
        results = (TextView)this.findViewById(R.id.results);
        example = (TextView)this.findViewById(R.id.example);
        
        Button submit = (Button)this.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Integer floors = new Integer(mFloors.getText().toString());
				Integer eggs = new Integer(mEggs.getText().toString());
				Log.i(TAG+".onCreate", "floors = " + floors);
				Log.i(TAG+".onCreate", "eggs = " + eggs);
				
				mMinMaxEgg = new MinMaxEgg();
		        Integer result = mMinMaxEgg.start(floors, eggs);
		        Log.i(TAG+".onCreate", "result = " + result);
		        results.setText(result.toString());
		        
		        ResultR[][] rTable = mMinMaxEgg.getRTable();
		        for(int j=0;j<rTable.length;j++){
		        	for(int k=0;k<rTable[j].length;k++){
		        		Log.i(TAG+".onCreate", "("+j+","+k+"):" + "value = " + rTable[j][k].value);
		        	}
		        }
		        
		        mMinMaxExample.setEnabled(true);
			}
		});
        
        mMinMaxExample = (Button)this.findViewById(R.id.minmaxexample);
        mMinMaxExample.setOnClickListener(new View.OnClickListener() {
			
        	private String tryPoint(int[] pI, int offset, int type){
        		String strA = "";
        		int start, end;
        		
        		if(type == 0){
        			start = 0;
        			end = pI.length;
        		} else {
        			start = 1;
        			end = pI.length - 1;
        		}
        		
        		for(int i=start;i<end;i++) {
        			int floor = pI[i] + offset;
        			strA = strA + " " + floor;
        		}
        		return strA;
        	}
        	
        	private String crashedPoint(int p, int offset){
        		String strA = "actual crashed point：     " ;
        		int floor = p + offset;
        		strA = strA + floor;
        		
        		return strA;
        	}
        	
        	private void emulateMinMax(int crashedPt, ResultR[][] rTable, int floors, int eggs, int offset, int level){
        		Log.i(TAG+".emulateMinMax", MinMaxEgg.prefix(level) + "floors = " + floors);
        		Log.i(TAG+".emulateMinMax", MinMaxEgg.prefix(level) + "eggs = " + eggs);
        		if(floors==0)
        			return;
        		
        		if(eggs==1)
        			return;
        		
        		int[] pI = rTable[floors][eggs].strategy; 
        		String tries = "suggested try points：" + tryPoint(pI, offset, 0);
        		Log.i(TAG+".emulateMinMax", MinMaxEgg.prefix(level) + "tries = " + tries);
        		String out = "第" + level + "次尝试：";
        		example.append(out + tryPoint(pI,offset,1) + "\n");
        		
//        		Log.i(TAG+".emulateMinMax", MinMaxEgg.prefix(level) + out + tryPoint(pI,offset) + "\n");
        		
        		int i;
        		for(i=0;i<pI.length-2;i++){
        			Log.i(TAG+".emulateMinMax", MinMaxEgg.prefix(level) + "pI[i] = " + pI[i]);
        			if(pI[i]<=crashedPt-offset && pI[i+1]>crashedPt-offset)
        				break;
        		}

        		Log.i(TAG+".emulateMinMax", MinMaxEgg.prefix(level) + "i = " + i);
        		emulateMinMax(crashedPt, rTable, pI[i+1]-pI[i]-1, eggs-(pI.length-2)+i, offset+pI[i], level+1);
        	}
        	
			@Override
			public void onClick(View v) {
				Integer floors = new Integer(mFloors.getText().toString());
				Integer eggs = new Integer(mEggs.getText().toString());
		        ResultR[][] rTable = mMinMaxEgg.getRTable();
		        
		        Random r = new Random();
		        int crashedPt = r.nextInt(floors+2); 
		        Log.i(TAG+".onClick", crashedPoint(crashedPt,0));

		        example.setText("");
		        example.setText("假设临界点为" + crashedPt + "\n");
		        emulateMinMax(crashedPt, rTable, floors, eggs, 0, 1);
			}
		});
        mMinMaxExample.setEnabled(false);
    }
}