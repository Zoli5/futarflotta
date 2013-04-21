package hu.sze.zoltan.futarflotta;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "futarflotta";
	private static final String TABLE_USERS = "users";

	private static final String KEY_ID = "id";
	private static final String KEY_USERNAME = "username";
	private static final String KEY_FULLNAME = "fullname";
	private static final String KEY_PASSWORD = "password";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
		// + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERNAME + " TEXT,"
		// + KEY_FULLNAME + " TEXT " + KEY_PASSWORD + " TEXT" +")";
		db.execSQL("CREATE TABLE "
				+ TABLE_USERS
				+ " (id INTEGER PRIMARY KEY, username TEXT, fullname TEXT, password TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

		// Create tables again
		onCreate(db);
	}

	void addUser(Users users) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USERNAME, users.getUserName()); // Contact user Name
		values.put(KEY_FULLNAME, users.getFullName()); // Contact full name
		values.put(KEY_PASSWORD, users.getPassword()); // Contact password

		// Inserting Row
		db.insert(TABLE_USERS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single user
	Users getUsers(String username) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_USERS, new String[] { KEY_ID,
				KEY_USERNAME, KEY_FULLNAME, KEY_PASSWORD },
				KEY_USERNAME + "=?", new String[] { String.valueOf(username) },
				null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Users contact = new Users(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3));
		// return contact
		db.close();
		cursor.close();
		return contact;
	}

	// Getting All Contacts
	public List<Users> getAllContacts() {
		List<Users> usersList = new ArrayList<Users>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_USERS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Users users = new Users();
				// users.setID(Integer.parseInt(cursor.getString(0)));
				users.setUserName(cursor.getString(1));
				// users.setFullName(cursor.getString(2));
				// users.setPassword(cursor.getString(3));
				// Adding contact to list
				usersList.add(users);
			} while (cursor.moveToNext());
		}

		// return contact list
		return usersList;
	}

	public boolean isMatch(String userName) {
		Cursor cursor = null;
		boolean match = false;
		SQLiteDatabase db = this.getReadableDatabase();

		try {
			cursor = db.query(TABLE_USERS, null, KEY_USERNAME + "=?",
					new String[] { String.valueOf(userName) }, null, null, null);
		} catch (CursorIndexOutOfBoundsException e) {
			e.printStackTrace();
			match = false;
		}

		if (cursor != null) {
			if (cursor.moveToFirst()){
				match = true;
			}
		}

		db.close();
		cursor.close();
		return match;
	}

	public int getUsersCount() {
		String countQuery = "SELECT  * FROM " + TABLE_USERS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		db.close();
		return cursor.getCount();
	}

}
