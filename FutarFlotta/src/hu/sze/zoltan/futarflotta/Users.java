package hu.sze.zoltan.futarflotta;

public class Users {
	// private variables
	int _id;
	String _username;
	String _fullname;
	String _password;

	// Empty constructor
	public Users() {

	}
	
	public Users(String username) {
		this._username = username;
	}

	// constructor
	public Users(int id, String username, String fullname, String password) {
		this._id = id;
		this._username = username;
		this._fullname = fullname;
		this._password = password;
	}

	// constructor
	public Users(String username, String fullname, String password) {
		this._username = username;
		this._fullname = fullname;
		this._password = password;
	}

	// getting ID
	public int getID() {
		return this._id;
	}

	// setting id
	public void setID(int id) {
		this._id = id;
	}

	// getting user name
	public String getUserName() {
		return this._username;
	}

	// setting user name
	public void setUserName(String username) {
		this._username = username;
	}

	// getting full name
	public String getFullName() {
		return this._fullname;
	}

	// setting full name
	public void setFullName(String fullname) {
		this._fullname = fullname;
	}

	// getting password
	public String getPassword() {
		return this._password;
	}

	// setting password
	public void setPassword(String password) {
		this._password = password;
	}
}
