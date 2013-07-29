package hu.sze.zoltan.futarflotta;

import java.net.MalformedURLException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class Login extends Activity {

	private Button btnLogin;
	private Button btnCancel;
	private EditText txtUser;
	private EditText txtPassword;

	public String userName;
	public String password;
	public String fullName;
	public UsersItem user;
	public int kiserlet = 0;

	private MobileServiceClient mClient;
	private MobileServiceTable<Users> mUsersTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);

		// final DatabaseHandler db = new
		// DatabaseHandler(getApplicationContext());
		//
		// db.addUser(new UsersItem("zszuri", "Szuri Zolt�n", "1234"));
		// db.addUser(new UsersItem("tszabo", "Szab� Tam�s", "1234"));
		// db.addUser(new UsersItem("lbedecs", "Bedecs L�szl�", "1234"));
		// db.addUser(new UsersItem("dcsanicz", "Cs�nicz D�ri", "1234"));
		// db.addUser(new UsersItem("teszt", "Teszt", "a"));

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtUser = (EditText) findViewById(R.id.txtUser);

		

		try {
			// Create the Mobile Service Client instance, using the provided
			// Mobile Service URL and key
			mClient = new MobileServiceClient(
					"https://futarflottamobile.azure-mobile.net/",
					"cMEWVBgHzMZBCrVgFByLvcgwTkfZmo87", this);

		} catch (MalformedURLException e) {
			createAndShowDialog(
					new Exception(
							"There was an error creating the Mobile Service. Verify the URL"),
					"Error");
		}

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userName = txtUser.getText().toString();
				password = txtPassword.getText().toString();
				
				Log.d("USERNAME", "Felhaszn�l�: " + userName);  
				Log.d("PASSWORD", "Jelsz�: " + password);  
				mUsersTable = mClient.getTable("Users", Users.class);
				mUsersTable.where().field("username").eq(userName).and()
						.field("password").eq(password)
						.execute(new TableQueryCallback<Users>() {

							public void onCompleted(List<Users> result,
									int count, Exception exception,
									ServiceFilterResponse response) {
								if (exception == null) {
									Log.d("COUNT", "Valamit sz�mol: " + count);  
									if(!result.isEmpty()){
										for (Users item : result) {
											kiserlet = 0;
											txtPassword.setText("");
											txtUser.setText("");

											Log.d("FULNAME", "Felhaszn�l�: " + item.getFullName());  
											Intent myIntent = new Intent(
													Login.this, MainActivity.class);
											myIntent.putExtra("fullName", item.getFullName());
											myIntent.putExtra("userName", item.getUserName());
											startActivity(myIntent);
										}
									} else {
										if (kiserlet < 3) {
											felbukkanoAblak(
													"Hiba!",
													"Hib�s jelsz� vagy felhaszn�l�n�v!",
													"OK");
											kiserlet++;
										} else {
											felbukkanoAblak2("Hiba!",
													"T�l sokszor pr�b�lkozott!",
													"Kil�p�s");
										}
									}					
								} else {	
									createAndShowDialog(exception, "Error");
								}
							}
						});

				// if (db.isMatch(txtUser.getText().toString()) == true) {
				// user = db.getUsers(txtUser.getText().toString());
				// if (txtPassword.getText().toString()
				// .equals(user.getPassword())) {
				//
				// kiserlet = 0;
				// txtPassword.setText("");
				// txtUser.setText("");
				// txtUser.setFocusable(true);
				// Intent myIntent = new Intent(Login.this,
				// MainActivity.class);
				// myIntent.putExtra("fullName", user.getFullName());
				// myIntent.putExtra("userName", user.getUserName());
				// startActivity(myIntent);
				//
				// } else {
				// if (kiserlet < 3) {
				// felbukkanoAblak("Hiba!",
				// "Hib�s jelsz� vagy felhaszn�l�n�v!", "OK");
				// kiserlet++;
				// } else {
				// felbukkanoAblak2("Hiba!",
				// "T�l sokszor pr�b�lkozott!", "Kil�p�s");
				// }
				//
				// // Toast.makeText(getApplicationContext(),
				// // "Hib�s jelsz�", Toast.LENGTH_LONG).show();
				// }
				// } else {
				// if (kiserlet < 3) {
				// felbukkanoAblak("Hiba!",
				// "Hib�s jelsz� vagy felhaszn�l�n�v!", "OK");
				// kiserlet++;
				// } else {
				// felbukkanoAblak2("Hiba!", "T�l sokszor pr�b�lkozott!",
				// "Kil�p�s");
				// }
				// // Toast.makeText(getApplicationContext(),
				// // "Hib�s felhaszn�l�n�v", Toast.LENGTH_LONG).show();
				//
				// }
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				felbukkanoAblak3("Kil�p�s", "Biztos ki szeretne l�pni?",
						"Igen", "Nem");
			}
		});
	}

	@Override
	public void onBackPressed() {
		felbukkanoAblak3("Kil�p�s", "Biztos ki szeretne l�pni?", "Igen", "Nem");
	}

	private void felbukkanoAblak(String cim, String szoveg, String gombfelirat) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setTitle(cim);
		alertDialogBuilder.setMessage(szoveg);
		alertDialogBuilder.setCancelable(false);

		alertDialogBuilder.setPositiveButton(gombfelirat,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		alertDialogBuilder.create().show();
	}

	private void felbukkanoAblak2(String cim, String szoveg, String gombfelirat) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setTitle(cim);
		alertDialogBuilder.setMessage(szoveg);
		alertDialogBuilder.setCancelable(false);

		alertDialogBuilder.setPositiveButton(gombfelirat,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alertDialogBuilder.create().show();
	}

	private void felbukkanoAblak3(String cim, String szoveg,
			String pozGombfelirat, String negGombfelirat) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setTitle(cim);
		alertDialogBuilder.setMessage(szoveg);
		alertDialogBuilder.setCancelable(false);

		alertDialogBuilder.setPositiveButton(pozGombfelirat,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});

		alertDialogBuilder.setNegativeButton(negGombfelirat,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		alertDialogBuilder.create().show();
	}

	private void createAndShowDialog(Exception exception, String title) {
		createAndShowDialog(exception.toString(), title);
	}

	private void createAndShowDialog(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(message);
		builder.setTitle(title);
		builder.create().show();
	}
}