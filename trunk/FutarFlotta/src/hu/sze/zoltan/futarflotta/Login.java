package hu.sze.zoltan.futarflotta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {

	private Button btnLogin;
	private Button btnCancel;
	private EditText txtUser;
	private EditText txtPassword;

	public String userName;
	public String password;
	public UsersItem user;
	public int kiserlet = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);

		final DatabaseHandler db = new DatabaseHandler(getApplicationContext());

		db.addUser(new UsersItem("zszuri", "Szuri Zoltán", "1234"));
		db.addUser(new UsersItem("tszabo", "Szabó Tamás", "1234"));
		db.addUser(new UsersItem("lbedecs", "Bedecs László", "1234"));
		db.addUser(new UsersItem("dcsanicz", "Csánicz Dóri", "1234"));
		db.addUser(new UsersItem("teszt", "Teszt", "a"));

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtUser = (EditText) findViewById(R.id.txtUser);

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (db.isMatch(txtUser.getText().toString()) == true) {
					user = db.getUsers(txtUser.getText().toString());
					if (txtPassword.getText().toString()
							.equals(user.getPassword())) {

						kiserlet = 0;
						txtPassword.setText("");
						txtUser.setText("");
						txtUser.setFocusable(true);
						Intent myIntent = new Intent(Login.this,
								MainActivity.class);
						myIntent.putExtra("fullName", user.getFullName());
						myIntent.putExtra("userName", user.getUserName());
						startActivity(myIntent);

					} else {
						if (kiserlet < 3) {
							felbukkanoAblak("Hiba!",
									"Hibás jelszó vagy felhasználónév!", "OK");
							kiserlet++;
						} else {
							felbukkanoAblak2("Hiba!",
									"Túl sokszor próbálkozott!", "Kilépés");
						}

						// Toast.makeText(getApplicationContext(),
						// "Hibás jelszó", Toast.LENGTH_LONG).show();
					}
				} else {
					if (kiserlet < 3) {
						felbukkanoAblak("Hiba!",
								"Hibás jelszó vagy felhasználónév!", "OK");
						kiserlet++;
					} else {
						felbukkanoAblak2("Hiba!", "Túl sokszor próbálkozott!",
								"Kilépés");
					}
					// Toast.makeText(getApplicationContext(),
					// "Hibás felhasználónév", Toast.LENGTH_LONG).show();

				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				felbukkanoAblak3("Kilépés", "Biztos ki szeretne lépni?", "Igen", "Nem");
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		felbukkanoAblak3("Kilépés", "Biztos ki szeretne lépni?", "Igen", "Nem");
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
}