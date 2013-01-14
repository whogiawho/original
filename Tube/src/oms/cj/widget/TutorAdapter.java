package oms.cj.widget;

import oms.cj.tube.Globals;
import oms.cj.tube.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TutorAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Bitmap[] mIcons;
    private int mLayoutID;
    private String[] mChapters;

    public TutorAdapter(Context context, int[] chapters, int layoutID, int[] iconIDs) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        
        mIcons = new Bitmap[chapters.length];
        mChapters = new String[chapters.length];
        mLayoutID = layoutID;
        
        for(int i=0;i<chapters.length;i++){
        	int iconID = iconIDs[i];
        	
        	mIcons[i] = Globals.loadPNG(iconID, context);
        	mChapters[i] = context.getString(chapters[i]);
        }
    }

	@Override
	public int getCount() {
		return mChapters.length;
	}

	@Override
	public Object getItem(int position) {
		return mChapters[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
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
            holder.tvChapter = (TextView) convertView.findViewById(R.id.chapter);
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind the data efficiently with the holder.
        holder.icon.setImageBitmap(mIcons[position]);
        holder.tvChapter.setText(mChapters[position]);
        
        return convertView;
	}

    static class ViewHolder {
        ImageView icon;
        TextView tvChapter;
    }
}
