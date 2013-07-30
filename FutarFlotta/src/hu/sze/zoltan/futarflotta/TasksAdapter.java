package hu.sze.zoltan.futarflotta;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter to bind a Users List to a view
 */
public class TasksAdapter extends ArrayAdapter<Tasks> {

	/**
	 * Adapter context
	 */
	Context mContext;

	/**
	 * Adapter View layout
	 */
	int mLayoutResourceId;

	public TasksAdapter(Context context, int layoutResourceId) {
		super(context, layoutResourceId);

		mContext = context;
		mLayoutResourceId = layoutResourceId;
	}

	/**
	 * Returns the view for a specific item on the list
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		final Tasks currentItem = getItem(position);

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
		}

		row.setTag(currentItem);
		final TextView textview = (TextView) row.findViewById(R.id.taskItem);
		textview.setText(currentItem.getAddress());

		return row;
	}
	
//	public String getItem(int position){
//		int id = position;
//		String task;
//		
//		final Tasks currentTask = getItem(id);
//		task = currentTask.getAddress();
//		
//		return task;
//	}
	
//	@Override
//	public Tasks getItem(int position) {
//		final Tasks currentItem = getItem(position);
//		
//		return currentItem;
//	}

}