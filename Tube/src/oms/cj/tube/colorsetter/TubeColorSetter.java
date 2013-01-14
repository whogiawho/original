package oms.cj.tube.colorsetter;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import org.kociemba.twophase.Tools;

import com.waps.AppConnect;

import oms.cj.ads.AdGlobals;
import oms.cj.ads.IHackedActions;
import oms.cj.ads.WapsNode;
import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.Quaternion;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import oms.cj.tube.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

public class TubeColorSetter extends LinearLayout implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, IExternal{
	private final static String TAG = "TubeColorSetter";
	private ColorSetterView mColorSetterView;
	private Button[] mButtons = new Button[4];
	private RadioGroup mRadioGroup;
	private Color mCurrentColor;
	private TextView mShowColor;
	private IComplete mIComplete;
	
	public TubeColorSetter(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=layoutInflater.inflate(R.layout.tubecolorsetter, this); 
		
		Button button = (Button) view.findViewById(R.id.complete);
		button.setOnClickListener(this); button.setEnabled(true); mButtons[0]=button;
		button = (Button) view.findViewById(R.id.camera);
		button.setOnClickListener(this); mButtons[1]=button;
		button = (Button) view.findViewById(R.id.save);
		button.setOnClickListener(this); mButtons[2]=button;
		button = (Button) view.findViewById(R.id.open);
		button.setOnClickListener(this); mButtons[3]=button;
		
		mColorSetterView = (ColorSetterView) view.findViewById(R.id.colorsetterview);
		if(mColorSetterView!=null)
			mColorSetterView.setExternalInterface(this);
		
        mRadioGroup = (RadioGroup) findViewById(R.id.selectColor);
        mRadioGroup.setOnCheckedChangeListener(this);
        mRadioGroup.check(R.id.red);
        mCurrentColor = Color.red;
        
        mShowColor = (TextView)view.findViewById(R.id.showColor);
        mShowColor.setBackgroundColor(mCurrentColor.toInt());
        
        mIComplete = null;
        
        errFormatString = getResources().getStringArray(R.array.TubeCheckError);
	}

	private void onCameraClick(){
		if(mIComplete!=null)
			mIComplete.onCameraClicked(mColorSetterView.getTube());
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.open:
			if(mIComplete!=null)
				mIComplete.onOpenClicked(mColorSetterView.getTube());
			break;
		case R.id.complete:
			int errCode = isTubeValid(mColorSetterView);
			String title = getContext().getString(R.string.title);
			
			if(errCode==0){
				if(mIComplete!=null)
					mIComplete.onCompleteClicked(mColorSetterView.getTube());
			} else {
				showMessage(title, errorString);
			}
			break;
		case R.id.camera:
			if(AdGlobals.getInstance().wapsAdSwitch){
				//define hacked actions here
				IHackedActions action = new IHackedActions(){
					@Override
					public void playAfterPassCheck() {
						onCameraClick();
					}
				};
				//pass node to WapsNode so that it can be called by getPoints's callback function
				WapsNode node = new WapsNode((Activity) this.getContext(), action);
				if(!node.checkQualificationToContinue()){
					Log.i(TAG+".onClick", "calling AppConnect.getInstance(this).getPoints(node)!");
					AppConnect.getInstance(this.getContext()).getPoints(node);
					return;
				}
			}
			onCameraClick();
			break;
		case R.id.save:
			errCode = isTubeValid(mColorSetterView);
			title = getContext().getString(R.string.title);
			
			if(errCode==0){
				if(mIComplete!=null)
					mIComplete.onSaveClicked(mColorSetterView.getTube());
			} else {
				showMessage(title, errorString);
			}		
			break;
		default:
			break;
		}
	}

	public void setOnCompleteListener(IComplete i){
		mIComplete = i;
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId){
		case R.id.red:
			mCurrentColor = Color.red;
			break;
		case R.id.orange:
			mCurrentColor = Color.orange;
			break;
		case R.id.blue:
			mCurrentColor = Color.blue;
			break;
		case R.id.green:
			mCurrentColor = Color.green;
			break;
		case R.id.yellow:
			mCurrentColor = Color.yellow;
			break;
		case R.id.white:
		default:
			mCurrentColor = Color.white;
			break;
		}
		if(mShowColor!=null){
			mShowColor.setBackgroundColor(mCurrentColor.toInt());
		}
	}

	@Override
	public Color getCurrentColor() {
		return mCurrentColor;
	}
	public void setCubesColor(Tube t){
		mColorSetterView.setCubesColor(t);
	}
	
	private int[][] oppositePos = {
			{Tube.left, Tube.right},
			{Tube.bottom, Tube.top},
			{Tube.back, Tube.front},
	};
	private String errorString = "";
	private String[] errFormatString = {
	};	//errFormatString is initialized at onCreate();
	
	//there are exactly 9 faces for red, orange, blue, green, yellow and white
	private int check9Faces(Tube tube){
		int errCode=0;
		Hashtable<String, Integer> cTable = new Hashtable<String, Integer>();
		
		cTable.put(Color.red.toString(), 0); 	cTable.put(Color.orange.toString(), 0); 
		cTable.put(Color.blue.toString(), 0);	cTable.put(Color.green.toString(), 0); 
		cTable.put(Color.yellow.toString(), 0); cTable.put(Color.white.toString(), 0);
		for(int i=0;i<Tube.SidesEachTube;i++)
			for(int j=0;j<Tube.CubesEachSide;j++){
				Color c = tube.getVisibleFaceColor(i, j);
				if(!c.equals(Color.gray)){
					Log.i(TAG+".check9Faces", "c = " + c.toString());
					int count = cTable.get(c.toString());
					cTable.put(c.toString(), count+1);
				}
			}
		Enumeration<String> e = cTable.keys();
		while(e.hasMoreElements()){
			String c = (String) e.nextElement();
			if(cTable.get(c)!=9){
				errCode = 1;
				errorString = String.format(errFormatString[errCode], cTable.get(c), c);
				return errCode;
			}
		}
		
		errorString = errFormatString[errCode];
		return errCode;
	}
	//check yellow and white must be set to opposite face
	private int checkOppositeFaces(Tube tube){
		int errCode = 0;
		
		int i;
		for(i=0;i<oppositePos.length;i++){
			int face1 = oppositePos[i][0];
			Color c1 = tube.getVisibleFaceColor(face1, 4);
			int face2 = oppositePos[i][1];
			Color c2 = tube.getVisibleFaceColor(face2, 4);
			if(c1.equals(Color.yellow)&&c2.equals(Color.white)||
			   c1.equals(Color.white)&&c2.equals(Color.yellow)||
			   c1.equals(Color.red)&&c2.equals(Color.orange)||
			   c1.equals(Color.orange)&&c2.equals(Color.red)||
			   c1.equals(Color.blue)&&c2.equals(Color.green)||
			   c1.equals(Color.green)&&c2.equals(Color.blue)){
				continue;
			} else {
				break;
			}
		}
		if(i!=oppositePos.length){
			errCode = 2;
			errorString = errFormatString[errCode];
			return errCode;
		}
		
		errorString = errFormatString[errCode];
		return errCode;
	}
	//check there is not a cube which have identical color faces
	private int checkIdenticalFaces(Tube tube){
		int errCode=0;
		Cube[] cubes = tube.getCubes();
		
		boolean bExistIdenticalFace = false;
_L0:
		for(int i=0;i<Tube.CubesEachTube;i++){
			int[] visibleFaces = Tube.getVisibleFaces(i);
			for(int j=0;j<visibleFaces.length-1;j++){
				Color c1 = cubes[i].getColor(visibleFaces[j]);
				for(int k=j+1;k<visibleFaces.length;k++){
					Color c2 = cubes[i].getColor(visibleFaces[k]);
					if(c1.equals(c2)){
						Log.i(TAG+".checkIdenticalFaces", "c1 = " + c1.toString());
						Log.i(TAG+".checkIdenticalFaces", "i = " + i);
						Log.i(TAG+".checkIdenticalFaces", "visibleFaces[j] = " + visibleFaces[j]);
						Log.i(TAG+".checkIdenticalFaces", "visibleFaces[k] = " + visibleFaces[k]);
						bExistIdenticalFace = true;
						break _L0;
					}
				}
			}
		}
		if(bExistIdenticalFace){
			errCode = 3;
			errorString = errFormatString[errCode];
			return errCode;			
		}	
		
		errorString = errFormatString[errCode];
		return errCode;
	}
	private int checkCenters(Tube tube){
		int errCode=0;
		Hashtable<String, Integer> cTable = new Hashtable<String, Integer>();
		
		cTable.put(Color.red.toString(), 0); 	cTable.put(Color.orange.toString(), 0); 
		cTable.put(Color.blue.toString(), 0);	cTable.put(Color.green.toString(), 0); 
		cTable.put(Color.yellow.toString(), 0); cTable.put(Color.white.toString(), 0);
		for(int i=0;i<Tube.SidesEachTube;i++){
			Color c = tube.getVisibleFaceColor(i, 4);
			if((!c.equals(Color.gray)))
				cTable.put(c.toString(), cTable.get(c.toString())+1);
		}
		Enumeration<String> e = cTable.keys();
		while(e.hasMoreElements()){
			String c = (String) e.nextElement();
			if(cTable.get(c)!=1){
				errCode = 4;
				errorString = errFormatString[errCode];
				return errCode;
			}
		}
		
		errorString = errFormatString[errCode];
		return errCode;
	}

	private int isTubeValid(ColorSetterView tView){
		int errCode=0;
		Tube tube = tView.getTube();
		
		errCode = check9Faces(tube);
		if(errCode!=0)
			return errCode;	

		errCode = checkOppositeFaces(tube);
		if(errCode!=0)
			return errCode;
		
		errCode = checkIdenticalFaces(tube);
		if(errCode!=0)
			return errCode;
		
		errCode = checkCenters(tube);
		if(errCode!=0)
			return errCode;

		errCode = Tools.verify(tube.toKociembaFacelet());
		switch(errCode){
		case 0:
			break;
		case -1:
		case -2:
		case -3:
		case -4:
		case -5:
		case -6:
			errCode = Math.abs(errCode)+4;
			break;
		}
		
		errorString = errFormatString[errCode];
		return errCode;
	}
	private void showMessage(String title, String message){
    	Dialog dialog=new AlertDialog.Builder(this.getContext())
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton("OK", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				return;
			}
		})
		.create();
    	dialog.show();	
	}

	public void onPause(){
		if(mColorSetterView!=null)
			mColorSetterView.onPause();
	}
	public void onResume(){
		if(mColorSetterView!=null)
			mColorSetterView.onResume();
	}
	public void restoreFromFile(Activity act, String strFileName) {
		mColorSetterView.restoreFromFile(act, strFileName);
	}
	public void saveToFile(Activity act, String strFileName, Stack<RotateAction> commands, Quaternion q, Color[] faceColor){
		mColorSetterView.saveToFile(act, strFileName, commands, q, faceColor);
	}
	public Quaternion getCurrentQuaternion(){
		return mColorSetterView.getCurrentQuaternion();
	}
}
