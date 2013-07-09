package hu.sze.zoltan.futarflotta;

/**
 * Represents an item in a ToDo list
 */
public class Users {

	@com.google.gson.annotations.SerializedName("username")
	private String mUserName;

	@com.google.gson.annotations.SerializedName("id")
	private int mId;

	@com.google.gson.annotations.SerializedName("fullname")
	private String mFullName;
	
	@com.google.gson.annotations.SerializedName("password")
	private String mPassword;

	public Users() {
	}

	@Override
	public String toString() {
		return getUserName();
	}

	public Users(int id, String username, String fullname, String password) {
		this.setId(id);
		this.setUserName(username);
		this.setFullName(fullname);
		this.setPassword(password);
	}

	public String getUserName() {
		return mUserName;
	}
	
	public String getFullName() {
		return mFullName;
	}
	
	public String getPassword() {
		return mPassword;
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

	public void setFullName(String fullname) {
		mFullName = fullname;
	}
	
	public void setPassword(String password) {
		mPassword = password;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Users && ((Users) o).mId == mId;
	}
}
