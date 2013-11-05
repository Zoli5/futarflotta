package hu.sze.zoltan.futarflotta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button btnLogOut;
	private Button btnMap;
	private Button btnTasks;
	private Button btnSettings;
	public TextView txtViewUser;
	public String fullName;
	public String userName;
	public String ures = "main";
	public LocationManager locationManager;
	public PendingIntent pendingIntent;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		btnMap = (Button) findViewById(R.id.btnMap);
		btnLogOut = (Button) findViewById(R.id.btnLogOut);
		btnTasks = (Button) findViewById(R.id.btnTasks);
		btnSettings = (Button) findViewById(R.id.btnSettings);

		txtViewUser = (TextView) findViewById(R.id.txtFelhasznalo);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			fullName = extras.getString("fullName");
			userName = extras.getString("userName");
		}

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		txtViewUser.setText(fullName);

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			new AlertDialog.Builder(this)
					.setMessage("GPS kötelezõ.")
					.setCancelable(false)
					.setPositiveButton("GPS",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent callGPSSettingIntent = new Intent(
											android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									startActivity(callGPSSettingIntent);
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									Toast.makeText(getApplicationContext(),
											"GPS nincs bekapcsolva",
											Toast.LENGTH_LONG).show();
								}
							}).create().show();
		}

		btnMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(MainActivity.this, MapV2.class);
				myIntent.putExtra("userName", userName);
				myIntent.putExtra("main", true);
				startActivity(myIntent);
			}
		});

		btnLogOut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent proximityIntent = new Intent("hu.sze.zoltan.futarflotta.proximity");
				
				pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent,Intent.FLAG_ACTIVITY_NEW_TASK);
				
				// Removing the proximity alert					
				locationManager.removeProximityAlert(pendingIntent);
				finish();
			}
		});

		btnTasks.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(MainActivity.this,
						TasksActivity.class);
				myIntent.putExtra("userName", userName);
				myIntent.putExtra("fullName", fullName);
				startActivity(myIntent);
			}
		});

		btnSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(MainActivity.this, Settings.class);
				myIntent.putExtra("userName", userName);
				startActivity(myIntent);
			}
		});

	}

	@Override
	public void onBackPressed() {
		// do nothing.
	}
}