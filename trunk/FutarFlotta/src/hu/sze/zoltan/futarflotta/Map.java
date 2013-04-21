package hu.sze.zoltan.futarflotta;

//47692707,17627381

import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity implements LocationListener {

	private MapView mapView;
	private LocationManager locationManager;
	private double myLatitude;
	private double myLongitude;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);

		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);

		if (location != null) {
			onLocationChanged(location);
		}

		locationManager.requestLocationUpdates(provider, 20000, 0, this);

	}

	@Override
	protected void onStop() {
		super.onStop();

		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onLocationChanged(Location l) {
		myLatitude = l.getLatitude();
		myLongitude = l.getLongitude();
		
		GeoPoint point = new GeoPoint((int)(myLatitude * 1E6), (int)(myLongitude*1E6));
		MapController mapController = mapView.getController();
		mapController.animateTo(point);
		mapController.setZoom(15);
		mapView.invalidate();
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.current_position);
		MyItemizedOverlay currentLocationOverlay = new MyItemizedOverlay(drawable);
		OverlayItem currentLocation = new OverlayItem(point, "Current Location", "Latitude : " + myLatitude + ", Longitude:" + myLongitude);
		currentLocationOverlay.addOverlay(currentLocation);
		mapOverlays.clear();
		mapOverlays.add(currentLocationOverlay);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "disabled", Toast.LENGTH_LONG).show();

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "enabled", Toast.LENGTH_LONG).show();

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(this, provider + ", status: " + status,
				Toast.LENGTH_LONG).show();

	}
}