package oms.cj.tube.tutor.basic.step1;

import oms.cj.tube.R;
import oms.cj.tube.player.TubePlayer;
import oms.cj.tube.tutor.basic.step1.TubeTutorBaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class TubeTutorPlayActivity extends TubeTutorBaseActivity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Intent i = getIntent();
		int layoutID = i.getIntExtra(LAYOUTID, -1);
		if(layoutID!=-1)
			setContentView(layoutID);
		else
			throw new IllegalArgumentException("layoutID is not set correctly");
        
        setOnClickListener(R.id.previous, this);
        
		String animationLabel = i.getStringExtra(ANIMATIONLABEL);
		if(animationLabel!=null)
			setTitle(animationLabel);
		
        TubePlayer tubePlayer = (TubePlayer) this.findViewById(R.id.tutorplayer);
        setTubePlayer(tubePlayer);
    }
    
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		default:
			super.onClick(v);
		}
	}
}
