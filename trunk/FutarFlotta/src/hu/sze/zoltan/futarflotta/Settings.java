package hu.sze.zoltan.futarflotta;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class Settings extends Activity {
	public ToggleButton toggleButton;
	public Button btnGPS;
	public Button btnPasswordChange;
	public String userName;
	public Button btnAbout;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			userName = extras.getString("userName");
		}

		toggleButton = (ToggleButton) findViewById(R.id.toggleBtnGPS);
		btnGPS = (Button) findViewById(R.id.btnGPS);
		btnPasswordChange = (Button) findViewById(R.id.btnPasswordChange);
		btnAbout = (Button) findViewById(R.id.btnAbout);

		toggleButton.setChecked(false);
		toggleButton.setActivated(false);
		toggleButton.setClickable(false);

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			toggleButton.setChecked(true);
		}

		btnGPS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent callGPSSettingIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(callGPSSettingIntent);

			}
		});

		btnPasswordChange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(),
						PasswordChange.class);
				myIntent.putExtra("userName", userName);
				startActivity(myIntent);
			}
		});

		btnAbout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(),
						About.class);
				startActivity(myIntent);
			}
		});
	}

	@Override
	protected void onResume() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			toggleButton.setChecked(true);
		} else {
			toggleButton.setChecked(false);
		}
		super.onStart();
	}
}