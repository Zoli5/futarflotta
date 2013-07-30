package hu.sze.zoltan.futarflotta;

/**
 * Represents an item in a ToDo list
 */
public class Tasks {

	@com.google.gson.annotations.SerializedName("username")
	private String mUserName;

	@com.google.gson.annotations.SerializedName("id")
	private int mId;

	@com.google.gson.annotations.SerializedName("address")
	private String mAddress;
	
	@com.google.gson.annotations.SerializedName("latitude")
	private String mLatitude;
	
	@com.google.gson.annotations.SerializedName("longitude")
	private String mLongitude;
	
	@com.google.gson.annotations.SerializedName("completed")
	private boolean mCompleted;

	public Tasks() {
	}

//	@Override
//	public String toString() {
//		return getUserName();
//	}

	public Tasks(int id, String address, String username, String latitude, String longitude) {
		this.setId(id);
		this.setAddress(address);
		this.setUserName(username);
		this.setLatitude(latitude);
		this.setLongitude(longitude);
	}

	public String getUserName() {
		return mUserName;
	}
	
	public String getAddress() {
		return mAddress;
	}
	
	public String getLatitude() {
		return mLatitude;
	}
	
	public String getLongitude() {
		return mLongitude;
	}
	
	public int getId() {
		return mId;
	}

	public final void setUserName(String username) {
		mUserName = username;
	}
	
	public final void setId(int id) {
		mId = id;
	}

	public void setAddress(String address) {
		mAddress = address;
	}
	
	public void setLatitude(String latitude) {
		mLatitude = latitude;
	}
	
	public void setLongitude(String longitude) {
		mLongitude = longitude;
	}
	
	public boolean isComplete() {
		return mCompleted;
	}
	
	public void setComplete(boolean complete) {
		mCompleted = complete;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Tasks && ((Tasks) o).mId == mId;
	}
}
