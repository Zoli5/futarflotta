package hu.sze.zoltan.futarflotta;

import java.net.MalformedURLException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class Login extends Activity {

	private Button btnLogin;
	private Button btnCancel;
	private EditText txtUser;
	private EditText txtPassword;

	public String userName;
	public String password;
	public String fullName;
	public ProgressDialog dialog;
	// public UsersItem user;
	public int kiserlet = 0;

	private MobileServiceClient mClient;
	private MobileServiceTable<Users> mUsersTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtUser = (EditText) findViewById(R.id.txtUser);
		
		dialog = new ProgressDialog(Login.this);
		dialog.setTitle("Bet�lt�s...");
		dialog.setMessage("K�rem v�rjon...");
		dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        

		try {
			// Create the Mobile Service Client instance, using the provided
			// Mobile Service URL and key
			mClient = new MobileServiceClient(
					"https://futarflottamobile.azure-mobile.net/",
					"cMEWVBgHzMZBCrVgFByLvcgwTkfZmo87", this).withFilter(new ProgressFilter());

		} catch (MalformedURLException e) {
			createAndShowDialog(
					new Exception(
							"Hiba a csatlakoz�ssal, k�rem ellen�rizze a kapcsolatot."),
					"Error");
		}

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userName = txtUser.getText().toString();
				password = txtPassword.getText().toString();

				mUsersTable = mClient.getTable("Users", Users.class);
				mUsersTable.where().field("username").eq(userName).and()
						.field("password").eq(password)
						.execute(new TableQueryCallback<Users>() {

							public void onCompleted(List<Users> result,
									int count, Exception exception,
									ServiceFilterResponse response) {
								if (exception == null) {
									if (!result.isEmpty()) {
										for (Users item : result) {
											kiserlet = 0;
											txtPassword.setText("");
											txtUser.setText("");

											Intent myIntent = new Intent(
													Login.this,
													MainActivity.class);
											myIntent.putExtra("fullName",
													item.getFullName());
											myIntent.putExtra("userName",
													item.getUserName());
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
											felbukkanoAblak2(
													"Hiba!",
													"T�l sokszor pr�b�lkozott!",
													"Kil�p�s");
										}
									}
								} else {
									createAndShowDialog(exception, "Error");
								}
							}
						});
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
	
	private class ProgressFilter implements ServiceFilter {
		
		@Override
		public void handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback,
				final ServiceFilterResponseCallback responseCallback) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (dialog != null){
						dialog.show();
					}
				}
			});
			
			nextServiceFilterCallback.onNext(request, new ServiceFilterResponseCallback() {
				
				@Override
				public void onResponse(ServiceFilterResponse response, Exception exception) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (dialog != null){
								dialog.cancel();
							}
						}
					});
					
					if (responseCallback != null)  responseCallback.onResponse(response, exception);
				}
			});
		}
	}
}