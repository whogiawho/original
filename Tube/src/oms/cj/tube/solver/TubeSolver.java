package oms.cj.tube.solver;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Stack;

import org.kociemba.twophase.Search;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import oms.cj.tube.Config;
import oms.cj.tube.R;
import oms.cj.tube.component.Color;
import oms.cj.tube.flysymbol.FlySymbol;
import oms.cj.tube.player.TubePlayer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextSwitcher;

public class TubeSolver extends TubePlayer implements View.OnClickListener, IDataLoader{
	private final static String TAG = "TubeSolver";
	
	public final static boolean bTest = false;
	private SolverView mSolverView;
	
	public TubeSolver(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i(TAG+".TubeSolver", "constructor starting ...");
		
		//codes to reload color from a randomized cube if bTest==true
		if(bTest){
			Tube t = new Tube(0.47f);
			t.randomize(null);
			int[] randomColors = new int[Tube.CubesEachTube*Cube.FacesEachCube];
			t.getColor(randomColors);
			setColor(randomColors);
			try {
				saveToFile("color.xml", (Activity)this.getContext(), t);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		mSolverView = (SolverView) getPlayerView(); 
	}
	
	private static void saveToFile(String strFileName, Activity act, Tube t) throws FileNotFoundException, IOException{
		FileOutputStream fos = act.openFileOutput(strFileName, Context.MODE_PRIVATE);
		BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(fos));

		String formatStr = "app:cube%d%sColor=\"@color/%s\"\n";
		Cube[] cubes = t.getCubes();
		
    	for(int i=0;i<Tube.SidesEachTube;i++)
    		for(int j=0;j<Tube.CubesEachSide;j++){
    			int whichCube = t.getCube(i, j);
    			Color c = cubes[whichCube].getColor(i);
    			String colorStr = String.format(formatStr, whichCube, Tube.LAYERSTR_F[i], c.toString());;
    			writer.write(colorStr);
    		}
		
    	writer.close();
    	fos.close();
	}
	
	private boolean isReady(){
		return true;
	}
	
	public void solveIt(){
		SharedPreferences settings = getContext().getSharedPreferences(Config.PREFS_NAME, 0);
		int solveWay = settings.getInt(Config.ref[11], R.id.advancedSolve);
		
		if(isReady()){
			switch(solveWay){
			case R.id.basicSolve:
				mSolverView.basicSolve(mHandler);
				buttonSwitchRoutines(true);
				break;
			case R.id.fridchSolve:
				mSolverView.fridchSolve(mHandler);
				buttonSwitchRoutines(true);
				break;
			case R.id.advancedSolve:
			default:
				if(!Search.bLoaded) {
					AdvancedDataLoader dataLoader = new AdvancedDataLoader(getContext(), this);
					dataLoader.loading();
				} else {
					advancedSolve();
				}
				break;
			}
		}
	}
	
	@Override
	public void onCompleteLoadingData() {
		advancedSolve();
	}
	
	public final static int SOLVECOMPLETED = 0;
	private ProgressDialog mSolvingProgressDlg = null;
	private Handler mHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
    	public void handleMessage(Message msg){
			switch (msg.what){
			case SOLVECOMPLETED:
				//dismiss progress dialog if there is one
				if(mSolvingProgressDlg!=null){
					mSolvingProgressDlg.dismiss();
					mSolvingProgressDlg=null;
				}
				
				//update image buttons
				buttonSwitchRoutines(true);
				
				//update FlySymbol if there is one
				FlySymbol flySym = getFlySymbol();
				if(flySym!=null){
					Stack<RotateAction> commands = (Stack<RotateAction>) msg.obj;
					flySym.init(commands);
				}
				
				//update TextSwitcher
				TextSwitcher switcher = getSwitcher();
				if(switcher!=null)
					switcher.setText("");
				
				break;
			}
		}
	};
	public void advancedSolve(){
		if(isReady()){
			mSolvingProgressDlg = new ProgressDialog(getContext());
			mSolvingProgressDlg.setMessage("Please wait while solving ...");
			mSolvingProgressDlg.setIndeterminate(true);
			mSolvingProgressDlg.show();
			mSolvingProgressDlg.setCancelable(false);
            
			mSolverView.advancedSolve(mHandler);
		}
	}
}
