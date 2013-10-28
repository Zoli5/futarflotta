package hu.sze.zoltan.futarflotta;

import java.net.MalformedURLException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class PasswordChange extends Activity {

	public Button btnChange;
	public Button btnCancel;
	public EditText edOldPassword;
	public EditText edNewPassword;
	public EditText edNewPassword2;
	public String userName;
	public String password;
	public Users user;
	public ProgressDialog dialog;
	private MobileServiceClient mClient;
	private MobileServiceTable<Users> mUsersTable;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.passwordchange_activity);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			userName = extras.getString("userName");
		}

		btnChange = (Button) findViewById(R.id.btnChange);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		edOldPassword = (EditText) findViewById(R.id.edOldPassword);
		edNewPassword = (EditText) findViewById(R.id.edNewPassword);
		edNewPassword2 = (EditText) findViewById(R.id.edNewPassword2);

		dialog = new ProgressDialog(PasswordChange.this);
		dialog.setTitle("Mentés...");
		dialog.setMessage("Kérem várjon...");
		dialog.setCancelable(false);
		dialog.setIndeterminate(true);

		try {
			mClient = new MobileServiceClient(
					"https://futarflottamobile.azure-mobile.net/",
					"cMEWVBgHzMZBCrVgFByLvcgwTkfZmo87", this).withFilter(new ProgressFilter());

		} catch (MalformedURLException e) {
			createAndShowDialog(new Exception(
					"Hiba a csatlakozással, kérem ellenõrizze a kapcsolatot."),
					"Error");
		}

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnChange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				password = edOldPassword.getText().toString();
				final String newpassword = edNewPassword.getText().toString();

				mUsersTable = mClient.getTable("Users", Users.class);
				mUsersTable.where().field("username").eq(userName).and()
						.field("password").eq(password)
						.execute(new TableQueryCallback<Users>() {

							@Override
							public void onCompleted(List<Users> result,
									int count, Exception exception,
									ServiceFilterResponse response) {
								if (exception == null) {
									if (!result.isEmpty()) {
										if (checkNewPassword(edNewPassword
												.getText().toString(),
												edNewPassword2.getText()
														.toString())) {
											for (Users item : result) {
												item.setPassword(newpassword);
												mUsersTable
														.update(item,
																new TableOperationCallback<Users>() {

																	@Override
																	public void onCompleted(
																			Users entity,
																			Exception exception,
																			ServiceFilterResponse response) {
																		Toast.makeText(
																				getApplicationContext(),
																				"Sikeres jelszóváltás!",
																				Toast.LENGTH_LONG)
																				.show();
																		edNewPassword2
																				.setText("");
																		edNewPassword
																				.setText("");
																		edOldPassword
																				.setText("");

																	}
																});
											}
										} else {
											Toast.makeText(
													getApplicationContext(),
													"Az új jelszavak nem egyeznek.",
													Toast.LENGTH_LONG).show();
										}
									} else {
										Toast.makeText(getApplicationContext(),
												"A régi jelszó nem megfelelõ.",
												Toast.LENGTH_LONG).show();
									}
								} else {
									createAndShowDialog(exception, "Error");
								}
							}
						});
			}
		});
	}

	public boolean checkNewPassword(String newPass, String newPass2) {
		if (!newPass.equals(newPass2))
			return false;
		return true;
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
		public void handleRequest(ServiceFilterRequest request,
				NextServiceFilterCallback nextServiceFilterCallback,
				final ServiceFilterResponseCallback responseCallback) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (dialog != null) {
						dialog.show();
					}
				}
			});

			nextServiceFilterCallback.onNext(request,
					new ServiceFilterResponseCallback() {

						@Override
						public void onResponse(ServiceFilterResponse response,
								Exception exception) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if (dialog != null) {
										dialog.cancel();
									}
								}
							});

							if (responseCallback != null)
								responseCallback
										.onResponse(response, exception);
						}
					});
		}
	}
}
