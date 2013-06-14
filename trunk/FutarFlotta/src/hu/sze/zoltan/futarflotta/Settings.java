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

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);

		toggleButton = (ToggleButton) findViewById(R.id.toggleBtnGPS);
		btnGPS = (Button) findViewById(R.id.btnGPS);
		
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