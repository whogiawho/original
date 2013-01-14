package oms.cj.tube.camera.self;

import android.os.Handler;
import oms.cj.tube.camera.ICameraPicture;

public interface ISelfCameraPicture extends ICameraPicture {
	public Handler getHandler();
}
