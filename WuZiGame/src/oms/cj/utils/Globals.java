package oms.cj.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Globals {
    public static Bitmap loadRes2Bitmap(Resources rs, int id, boolean scale) {
        Bitmap bitmap;
        if (scale) {
            bitmap = BitmapFactory.decodeResource(rs, id);
        } else {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScaled = false;
            bitmap = BitmapFactory.decodeResource(rs, id, opts);
        }
        return bitmap;
    }
}
