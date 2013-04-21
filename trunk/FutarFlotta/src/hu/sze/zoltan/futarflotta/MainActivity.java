package hu.sze.zoltan.futarflotta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private Button btnLogOut;
	private Button btnMap;
	private Button btnTasks;
	public TextView txtViewUser;
	public String value;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		btnMap = (Button) findViewById(R.id.btnMap);
		btnLogOut = (Button) findViewById(R.id.btnLogOut);
		btnTasks = (Button) findViewById(R.id.btnTasks);
		txtViewUser = (TextView) findViewById(R.id.txtFelhasznalo);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    value = extras.getString("fullName");
		}
		
		txtViewUser.setText(value);
		
		btnMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(MainActivity.this,MapV2.class);
				startActivity(myIntent);
			}
		});
		
		btnLogOut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();		
			}
		});
		
		btnTasks.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(MainActivity.this,ToDoTasks.class);
				startActivity(myIntent);
			}
		});
	}
	
	@Override
	public void onBackPressed() {
	    // do nothing.
	}
}