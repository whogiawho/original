package oms.cj.tube.tutor.basic.step1;

import java.util.Arrays;
import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.Tube;
import oms.cj.tube.player.TubePlayer;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public abstract class TubeTutorBaseActivity extends Activity implements View.OnClickListener{
	public final static int RESULT_COMPLETE = Activity.RESULT_FIRST_USER;
	public final static String LAYOUTID = "layoutResID";
	public final static String ANIMATIONLABEL = "animationLabel";
	public final static String TUBETUTORPLAYACTIVITY="oms.cj.tube.tutor.basic.step1.TubeTutorPlayActivity";

	private final static String TAG = "TubeTutorBaseActivity";
	private TubePlayer mPlayer = null;
	
	public void setTubePlayer(TubePlayer player){
		mPlayer = player;
	}
	public void setOnClickListener(int buttonResID, View.OnClickListener listener){
        Button button = (Button)findViewById(buttonResID);
        button.setOnClickListener(listener);
	}
	
	protected void prepareBrowseButtonOnClickListener(int nextID, int previousID, int tocID, View.OnClickListener listener){
		setOnClickListener(nextID, listener);
		setOnClickListener(previousID, listener);
		setOnClickListener(tocID, listener);
	}
	
	protected void prepareESButtonOnClickListener(int enlargeID, int shrinkID, View.OnClickListener listener){
		setOnClickListener(enlargeID, listener);
		setOnClickListener(shrinkID, listener);
	}
	
	protected void setButtonText(int buttonResID, String text){
		Button button = (Button)findViewById(buttonResID);
		button.setText(text);
	}
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(resultCode == RESULT_COMPLETE){
    		Log.i(TAG+".onActivityResult", "RESULT_COMPLETE!");
    		setResult(RESULT_COMPLETE);
    		finish();
    	} else {
    		Log.i(TAG+".onActivityResult", "resultCode = " + resultCode);
    	}
    }

    public void nextSection(String activityName, int layoutID, String animationLabel){
        Intent intent=new Intent();
        intent.putExtra(LAYOUTID, layoutID);
        String _animationLabelPrefix=getResources().getString(R.string.animation);
        intent.putExtra(ANIMATIONLABEL, _animationLabelPrefix+animationLabel);
        
        intent.setClassName(this, activityName); 
        startActivityForResult(intent, 0); 
    }

    public void nextSection(String activityName, int layoutID){
        nextSection(activityName, layoutID, "");
    }
    
    public void nextSection(String activityName){
        Intent intent=new Intent();
        
        intent.setClassName(this, activityName); 
        startActivityForResult(intent, 0); 
    }
    
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.previous:
			finish();
			break;
		case R.id.toc:
			setResult(TubeTutor1Activity1.RESULT_COMPLETE);
			finish();
			break;
		}
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig){
    	Log.i(TAG+"onConfigurationChanged", "being called!");
    	super.onConfigurationChanged(newConfig);
    }
    
	@Override
	public void onPause(){
		super.onPause();
		
		if(mPlayer!=null)
			mPlayer.onPause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		if(mPlayer!=null)
			mPlayer.onResume();
	}	
		
	//tutorstep2: top layer Flower
	protected boolean isFlowerInPlace(Tube t, Color centerColor, Color crossColor){
        int[] pos = t.searchCenterColor(centerColor);

        
        boolean bInPlace = true;
        for(int i=0;i<Tube.idxsOfCrossCube.length;i++){
        	int idx = Tube.idxsOfCrossCube[i];
        	Color c = t.getVisibleFaceColor(pos[Tube.CENTERBYSIDE], idx); 
        	if(!c.equals(crossColor)){
        		bInPlace = false;
        		break;
        	}
        }

		return bInPlace;
	}
	
	//tutorstep3: top layer cross
	protected boolean isTutorStep3InPlace(Tube t) {
		boolean b1 = isFlowerInPlace(t, Color.white, Color.white);

		//the middle 2 cube visible face color should be identical
		int[] pos = t.searchCenterColor(Color.white);
		boolean b2 = true;
		Color[] cList = new Color[4];
		int[] layerList = new int[4];
		t.getCrossOtherSideColorOfCenter(pos, cList, layerList);
		for(int i=0;i<cList.length;i++){
			Color c1 = cList[i];
			Color c2 = t.getVisibleFaceColor(layerList[i], 4);
			if(!c1.equals(c2)){
				b2 = false;
				break;
			}
		}
		
		return b1&&b2;
	}
	
	//tutorstep4: bottom layer identical, T
	protected boolean isTutorStep4InPlace(Tube t){
		boolean b1 = isTutorStep3InPlace(t);
		
		boolean b2 = true;
		//the corner cube shoudl also be in place
		Cube[] cubes = t.getCubes();
		int[] pos = t.searchCenterColor(Color.white);
		int side = pos[Tube.CENTERBYSIDE];
		int[] cornerCubes = t.getCornerCube(side);
_L0:
		for(int i=0;i<cornerCubes.length;i++){
			int idx = cornerCubes[i];
			Color c = cubes[idx].getColor(side);
			if(!c.equals(Color.white)){
				b2 = false;
				break;
			}
			
			int[] otherSides = t.getOther2SidesOfCorner(idx, side);
			for(int j=0;j<otherSides.length;j++){
				Color c1 = cubes[idx].getColor(otherSides[j]);
				Color c2 = t.getVisibleFaceColor(otherSides[j], 4);
				if(!c1.equals(c2)){
					b2 = false;
					break _L0;
				}				
			}		
		}
		
		return b1&&b2;
	}
	
	//tutorstep5: bottom 2 layers
	protected boolean isTutorStep5InPlace(Tube t){
		boolean b1 = isTutorStep4InPlace(t);
		
		boolean b2 = true;
		//the edge cube which is connected with the corner cube should also be in place
		Cube[] cubes = t.getCubes();
		int[] pos = t.searchCenterColor(Color.white);
		int side = pos[Tube.CENTERBYSIDE];
		int[] cornerCubes = t.getCornerCube(side);
_L0:		
		for(int i=0;i<cornerCubes.length;i++){
			int idx = cornerCubes[i];
			int[] otherSides = t.getOther2SidesOfCorner(idx, side);
			int otherCube = t.getCube(otherSides);
			if(otherCube!=-1){
				for(int j=0;j<otherSides.length;j++){
					int otherSide = otherSides[j];
					Color c1 = cubes[idx].getColor(otherSide);
					Color c2 = cubes[otherCube].getColor(otherSide);
					if(!c1.equals(c2)){
						b2 = false;
						break _L0;
					}
				}
			} else {
				String out = String.format("otherSides=%s", Arrays.toString(otherSides));
				throw new IllegalStateException(out);
			}
		}
		
		return b1&&b2;
	}
	
	//tutorstep6: bottom 2 layers, top layer cross
	protected boolean isTutorStep6InPlace(Tube t){
		boolean b1 = isTutorStep5InPlace(t);
		
		boolean b2 = isFlowerInPlace(t, Color.yellow, Color.yellow);
		
		return b1&&b2;
	}
	
	//tutorstep7: bottom 2layers, top layer side
	protected boolean isTutorStep7InPlace(Tube t){
		boolean b1 = isTutorStep6InPlace(t);
		
		boolean b2 = true;
		Cube[] cubes = t.getCubes();
		int[] pos = t.searchCenterColor(Color.yellow);
		int side = pos[Tube.CENTERBYSIDE];
		int[] cornerCubes = t.getCornerCube(side);
		for(int i=0;i<cornerCubes.length;i++){
			int idx = cornerCubes[i];
			Color c = cubes[idx].getColor(side);
			if(!c.equals(Color.yellow)){
				b2 = false;
				break;
			}
		}
		
		return b1&&b2;
	}
	
	//tutorstep8: bottom 2 layer, top layer side, top layer corner 
	protected boolean isTutorStep8InPlace(Tube t){
		boolean b1 = isTutorStep7InPlace(t);
		
		boolean b2 = true;
		Cube[] cubes = t.getCubes();
		int[] pos = t.searchCenterColor(Color.yellow);
		int side = pos[Tube.CENTERBYSIDE];
		int[] cornerCubes = t.getCornerCube(side);
_L0:		
		for(int i=0;i<cornerCubes.length;i++){
			int idx = cornerCubes[i];
			int[] otherSides = t.getOther2SidesOfCorner(idx, side);
			for(int j=0;j<otherSides.length;j++){
				Color c1 = cubes[idx].getColor(otherSides[j]);
				Color c2 = t.getVisibleFaceColor(otherSides[j], 4);
				if(!c1.equals(c2)){
					b2 = false;
					break _L0;
				}				
			}		
		}
		
		return b1&&b2;
	}
	
	//tutorstep9: in origin state
	protected boolean isTutorStep9InPlace(Tube t){
		return t.inOriginState();
	}
}
