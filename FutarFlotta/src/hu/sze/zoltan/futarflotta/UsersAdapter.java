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
public class UsersAdapter extends ArrayAdapter<Users> {

	/**
	 * Adapter context
	 */
	Context mContext;

	/**
	 * Adapter View layout
	 */
	int mLayoutResourceId;

	public UsersAdapter(Context context, int layoutResourceId) {
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

		final Users currentItem = getItem(position);

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
		}

		row.setTag(currentItem);
		final TextView textview = (TextView) row.findViewById(R.id.txtUsers);
		textview.setText(currentItem.getUserName());

		return row;
	}
	
	public String getUser(int position){
		int id = position;
		String user;
		
		final Users currentUser = getItem(id);
		user = currentUser.getFullName();
		
		return user;
	}

}
