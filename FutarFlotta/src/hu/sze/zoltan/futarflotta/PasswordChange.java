package hu.sze.zoltan.futarflotta;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordChange extends Activity{
	
	public Button btnChange;
	public Button btnCancel;
	public EditText edOldPassword;
	public EditText edNewPassword;
	public EditText edNewPassword2;
	public String userName;
	public Users user;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.passwordchange_activity);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    userName = extras.getString("userName");
		}
		
		final DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
		btnChange = (Button) findViewById(R.id.btnChange);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		edOldPassword = (EditText) findViewById(R.id.edOldPassword);
		edNewPassword = (EditText) findViewById(R.id.edNewPassword);
		edNewPassword2 = (EditText) findViewById(R.id.edNewPassword2);
		
		user = db.getUsers(userName);
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btnChange.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(edOldPassword.getText().toString().equals(user.getPassword())){
					if(checkNewPassword(edNewPassword.getText().toString(), edNewPassword2.getText().toString())){
						user.setPassword(edNewPassword.getText().toString());
						db.updateContact(user);
						Toast.makeText(getApplicationContext(), "Sikeres jelszóváltás!", Toast.LENGTH_LONG).show();
						edNewPassword2.setText("");
						edNewPassword.setText("");
						edOldPassword.setText("");
					} else {
						Toast.makeText(getApplicationContext(), "Az új jelszavak nem egyeznek.", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "A régi jelszó nem megfelelõ.", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	public boolean checkNewPassword(String newPass, String newPass2){
		if(!newPass.equals(newPass2))
			return false;
	return true;
	}
}
