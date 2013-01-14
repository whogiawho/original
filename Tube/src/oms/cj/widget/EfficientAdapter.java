package oms.cj.widget;

import java.io.File;
import java.util.Date;

import oms.cj.tube.Globals;
import oms.cj.tube.R;
import oms.cj.tube.TubeApplication;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EfficientAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    
    private String[] mFilenames;
    private Bitmap[] mIcons;
    private String[] mSavedTime;
    private int mLayoutID;

    public EfficientAdapter(Context context, String[] filenames, int layoutID) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        
        mFilenames = filenames;
        mIcons = new Bitmap[filenames.length];
        mSavedTime = new String[filenames.length];
        mLayoutID = layoutID;
        
        for(int i=0;i<mFilenames.length;i++){
        	String filename = mFilenames[i];
        	
        	//get file's last modified time
        	File f = context.getFileStreamPath(filename);  
        	long lTime = f.lastModified();
        	Date d = new Date(lTime); 
        	mSavedTime[i] = d.toLocaleString();
        	
        	//load the files' icon to mIcons[]
        	mIcons[i] = Globals.loadPNG(filename+TubeApplication.IconSuffix, context);
        }
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        return mFilenames.length;
    }

    /**
     * Since the data comes from an array, just returning the index is
     * sufficent to get at the data. If we were using a more complex data
     * structure, we would return whatever object represents one row in the
     * list.
     *
     * @see android.widget.ListAdapter#getItem(int)
     */
    public Object getItem(int position) {
        return mFilenames[position];
    }

    /**
     * Use the array index as a unique id.
     *
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Make a view to hold each row.
     *
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutID, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.tvCubeSavedID = (TextView) convertView.findViewById(R.id.cubesavedid);
            holder.tvCubeSavedTime = (TextView) convertView.findViewById(R.id.cubesavedtime);

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind the data efficiently with the holder.
        holder.tvCubeSavedID.setText(mFilenames[position]);
        holder.icon.setImageBitmap(mIcons[position]);
        holder.tvCubeSavedTime.setText(mSavedTime[position]);

        return convertView;
    }

    static class ViewHolder {
        ImageView icon;
        TextView tvCubeSavedID;
        TextView tvCubeSavedTime;
    }

}
