package oms.cj.genActions;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Stack;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class genActionsActivity extends Activity {
	int[][][] step1_1 = {
			{ {Tube.front}, {Tube.CCW}, {90}, {0},  },
			{ {Tube.top}, {Tube.CCW}, {90}, {1}, },
			{ {Tube.back}, {Tube.CCW}, {90}, {0}, },
	};
	int[][][] step1_2 = {
			{ {Tube.front}, {Tube.CCW}, {90}, {2}, },
			{ {Tube.top}, {Tube.CW}, {90}, },
			{ {Tube.right}, {Tube.CW}, {90}, },
			{ {Tube.left}, {Tube.CW}, {90}, },
	};
	int[][][] step1_3 = {
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90}, {3}, },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.left}, {Tube.CW}, {90},  },
	};
	int[][][] step1_4 = {
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90}, {4}, },
			{ {Tube.right}, {Tube.CW}, {90},  },
	};
	int[][][] step2_1 = {
			{ {Tube.top}, {Tube.CCW}, {90}, {5}, },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top,Tube.bottom,Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90}, {6}, },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top,Tube.bottom,Tube.equator}, {Tube.CW}, {90}, {7}, },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top,Tube.bottom,Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.top,Tube.bottom,Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90}, {8},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.front,Tube.standing,Tube.back}, {Tube.CW}, {90},  },
			{ {Tube.front,Tube.standing,Tube.back}, {Tube.CW}, {90},  },
			{ {Tube.top,Tube.bottom,Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.top,Tube.bottom,Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.top,Tube.bottom,Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.top,Tube.bottom,Tube.equator}, {Tube.CW}, {90},  },
	};
	
	int[][][] step3_1={
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
	};
	
	int[][][] step3_2={
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
	};
	
	int[][][] step3_3={ 
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
	};
	
	int[][][] step3_4={
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
	};
	
	int[][][] step3_5={
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
	};
	
	int[][][] step3_6={
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
	};
	
	int[][][] step4_1={
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },	
	};
	
	int[][][] step4_2={
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },				
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
	};
	
	int [][][] step4_3={
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },				
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },	
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },				
			{ {Tube.top}, {Tube.CW}, {90}, },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },			
	};

	int [][][] step5_1={
			{ {Tube.top}, {Tube.CCW}, {90},  },			//竖条一定要平行你
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },			
	};
	
	int [][][] step5_2={
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },	
			{ {Tube.top}, {Tube.CCW}, {90},  },			//竖条一定要平行你
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },				
	};
		
	int [][][] step5_3={
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },	
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },	
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },				
	};

	int [][][] step6_1={					//1次小鱼1
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
	};
	
	int [][][] step6_2={					//1次小鱼2
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
	};
	
	int [][][] step6_3={					//1次小鱼1，1次小鱼2
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CCW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
	};
	
	int [][][] step6_4={					//1次小鱼1，1次小鱼2
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
	};
	
	int [][][] step6_5={					//1次小鱼1，1次小鱼2
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },	
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
	};
	
	int [][][] step6_6={					//两次小鱼1
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },		
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },			
	};
	
	int [][][] step6_7={					//两次小鱼1
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90}, },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },		
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },		
	};
	
	int [][][] step7_1={
			{ {Tube.left, Tube.middle, Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },	
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },	
			{ {Tube.right}, {Tube.CCW}, {90},  },	
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },	
			{ {Tube.left, Tube.middle, Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },		
			{ {Tube.top}, {Tube.CW}, {90},  },	
	};

	int [][][] step7_2={
			{ {Tube.left, Tube.middle, Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },	
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },	
			{ {Tube.right}, {Tube.CCW}, {90},  },	
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW} },	
			{ {Tube.left, Tube.middle, Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },		
			{ {Tube.left, Tube.middle, Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },	
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },
			{ {Tube.bottom}, {Tube.CCW}, {90},  },	
			{ {Tube.right}, {Tube.CCW}, {90},  },	
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },	
			{ {Tube.left, Tube.middle, Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },	
	};
	
	int [][][] step8_1={
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90}, },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },			
	};

	int [][][] step8_2={
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },			
	};
	
	int [][][] step8_3={
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90}, },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
	};	

	int [][][] step8_4={
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.top}, {Tube.CCW}, {90},  },
			{ {Tube.right}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.top, Tube.bottom, Tube.equator}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90}, },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.top}, {Tube.CW}, {90},  },
			{ {Tube.front}, {Tube.CCW}, {90},  },			
	};

	int [][][] symbolrep1={
			{ {Tube.left}, {Tube.CW} },
			{ {Tube.middle}, {Tube.CW} },
			{ {Tube.right}, {Tube.CW} },
			{ {Tube.back}, {Tube.CW} },
			{ {Tube.standing}, {Tube.CW} },
			{ {Tube.front}, {Tube.CW} },
			{ {Tube.top}, {Tube.CW} },
			{ {Tube.equator}, {Tube.CW} },
			{ {Tube.bottom}, {Tube.CW} },
	};

	int [][][] symbolrep2={
			{ {Tube.left}, {Tube.CCW} },
			{ {Tube.middle}, {Tube.CCW} },
			{ {Tube.right}, {Tube.CCW} },
			{ {Tube.back}, {Tube.CCW} },
			{ {Tube.standing}, {Tube.CCW} },
			{ {Tube.front}, {Tube.CCW} },
			{ {Tube.top}, {Tube.CCW} },
			{ {Tube.equator}, {Tube.CCW} },
			{ {Tube.bottom}, {Tube.CCW} },
	};

	int [][][] symbolrep3={
			{ {Tube.left}, {Tube.CW}, {180} },
			{ {Tube.middle}, {Tube.CW}, {180} },
			{ {Tube.right}, {Tube.CW}, {180} },
			{ {Tube.back}, {Tube.CW}, {180} },
			{ {Tube.standing}, {Tube.CW}, {180} },
			{ {Tube.front}, {Tube.CW}, {180} },
			{ {Tube.top}, {Tube.CW}, {180} },
			{ {Tube.equator}, {Tube.CW}, {180} },
			{ {Tube.bottom}, {Tube.CW}, {180} },
	};

	int [][][] symbolrep4={
			{ {Tube.left, Tube.middle, Tube.right}, {Tube.CW} },
			{ {Tube.top, Tube.equator, Tube.bottom}, {Tube.CW} },
			{ {Tube.back, Tube.standing, Tube.front}, {Tube.CW} },
	};

	int [][][] symbolrep5={
			{ {Tube.left, Tube.middle}, {Tube.CW} },
			{ {Tube.right, Tube.middle}, {Tube.CW} },
			{ {Tube.top, Tube.equator}, {Tube.CW} },
			{ {Tube.bottom, Tube.equator}, {Tube.CW} },
			{ {Tube.back, Tube.standing}, {Tube.CW} },
			{ {Tube.front, Tube.standing}, {Tube.CW} },
	};

	static String[] remarks = {
			"有空位，直接翻上去",
			"没空位，旋转U，腾出空位",
			"旋转白格至E层,化为情况1",
			"此时已化为情况2",
			"此时已化为情况1",
			"红色对好",
			"橙色对好",
			"蓝色对好",
			"绿色对好",
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        try {
			saveOriginToFile("step1.1", this, step1_1);
			saveOriginToFile("step1.2", this, step1_2);
			saveOriginToFile("step1.3", this, step1_3);
			saveOriginToFile("step1.4", this, step1_4);
			saveOriginToFile("step2.1", this, step2_1);
			saveOriginToFile("step3.1", this, step3_1);
			saveOriginToFile("step3.2", this, step3_2);
			saveOriginToFile("step3.3", this, step3_3);
			saveOriginToFile("step3.4", this, step3_4);
			saveOriginToFile("step3.5", this, step3_5);
			saveOriginToFile("step3.6", this, step3_6);
			saveOriginToFile("step4.1", this, step4_1);
			saveOriginToFile("step4.2", this, step4_2);
			saveOriginToFile("step4.3", this, step4_3);
			saveOriginToFile("step5.1", this, step5_1);
			saveOriginToFile("step5.2", this, step5_2);
			saveOriginToFile("step5.3", this, step5_3);
			saveOriginToFile("step6.1", this, step6_1);
			saveOriginToFile("step6.2", this, step6_2);
			saveOriginToFile("step6.3", this, step6_3);
			saveOriginToFile("step6.4", this, step6_4);
			saveOriginToFile("step6.5", this, step6_5);
			saveOriginToFile("step6.6", this, step6_6);
			saveOriginToFile("step6.7", this, step6_7);		
			saveOriginToFile("step7.1", this, step7_1);	
			saveOriginToFile("step7.2", this, step7_2);	
			saveOriginToFile("step8.1", this, step8_1);	
			saveOriginToFile("step8.2", this, step8_2);	
			saveOriginToFile("step8.3", this, step8_3);	
			saveOriginToFile("step8.4", this, step8_4);	
			saveOriginToFile("symbolrep1", this, symbolrep1);
			saveOriginToFile("symbolrep2", this, symbolrep2);
			saveOriginToFile("symbolrep3", this, symbolrep3);
			saveOriginToFile("symbolrep4", this, symbolrep4);
			saveOriginToFile("symbolrep5", this, symbolrep5); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TelephonyManager tM = (TelephonyManager) this.getSystemService("phone");
		String sim = "sim=";              sim += tM.getSimSerialNumber(); 
		String line1no = "line1no=";      line1no += tM.getLine1Number();
		String deviceID = "deviceID=";    deviceID += tM.getDeviceId();
		
		TextView tv = (TextView) this.findViewById(R.id.helloID);
		tv.setText(sim + line1no + deviceID);
    }
    
	public static void saveOriginToFile(String strFileName, Activity act, int[][][] a) throws FileNotFoundException, IOException{
		FileOutputStream fos = act.openFileOutput(strFileName, Context.MODE_PRIVATE);
		ObjectOutputStream objectOut = new ObjectOutputStream(new BufferedOutputStream(fos));
		Stack<RotateAction> actions = new Stack<RotateAction>();
		

		for(int i=0;i<a.length;i++){
			if(a[i].length==4){
				int idx = a[i][3][0];
				actions.push(new RotateAction(a[i][0], a[i][1][0], a[i][2][0], remarks[idx]));
			} else if(a[i].length==3)
				actions.push(new RotateAction(a[i][0], a[i][1][0], a[i][2][0]));
			else
				actions.push(new RotateAction(a[i][0],a[i][1][0]));
		}
		
		objectOut.writeObject(actions);
		objectOut.close();
	}
}