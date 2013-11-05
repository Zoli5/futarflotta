package hu.sze.zoltan.futarflotta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class MapV2 extends FragmentActivity implements
		OnMyLocationChangeListener, GoogleMap.OnMarkerClickListener {

	public GoogleMap googleMap;
	public String address;
	public String latitude = "";
	public String longitude = "";
	public double lon = 0.0;
	public double lat = 0.0;
	public double longitudeDB = 0.0;
	public double latitudeDB = 0.0;
	public boolean isEnd = false;
	public MarkerOptions start_options;
	public MarkerOptions des_options;
	public MarkerOptions des_options_db;
	public String userName;
	public boolean elsoInditas = true;
	public static Activity MapV2;
	public boolean kesze = false;
	public boolean main;

	public ProgressDialog dialog;

	public LocationManager locationManager;
	public PendingIntent pendingIntent;

	private MobileServiceClient mClient;
	private MobileServiceTable<Tasks> mTasksTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity2);

		MapV2 = this;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			
			main = extras.getBoolean("main");
			
			if (main){
				userName = extras.getString("userName");
			} else {
				latitude = extras.getString("lat");
				longitude = extras.getString("lon");
				lat = Double.parseDouble(latitude);
				lon = Double.parseDouble(longitude);
				userName = extras.getString("userName");
				
				isEnd = true;
			}
			
		}

		// Getting LocationManager object from System Service
		// LOCATION_SERVICE
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		dialog = new ProgressDialog(MapV2.this);
		dialog.setTitle("Cimek betöltése...");
		dialog.setMessage("Kérem várjon...");
		dialog.setCancelable(false);
		dialog.setIndeterminate(true);

		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		if (status != ConnectionResult.SUCCESS) {
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();
		} else {
			// Getting reference to the SupportMapFragment of activity_main.xml
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);

			// Getting GoogleMap object from the fragment
			googleMap = fm.getMap();

			// Enabling MyLocation Layer of Google Map
			googleMap.setMyLocationEnabled(true);

			// Setting event handler for location change
			googleMap.setOnMyLocationChangeListener(this);

			if (isEnd) {
				boolean proximity_entering = getIntent().getBooleanExtra(
						LocationManager.KEY_PROXIMITY_ENTERING, true);

				if (proximity_entering) {
					// This intent will call the activity ProximityActivity
					Intent proximityIntent = new Intent(
							"hu.sze.zoltan.futarflotta.proximity");
					proximityIntent.putExtra("latitude", latitude);
					proximityIntent.putExtra("longitude", longitude);
					proximityIntent.putExtra("userName", userName);

					// Creating a pending intent which will be invoked by
					// LocationManager when the specified region is
					// entered or exited
					pendingIntent = PendingIntent.getActivity(getBaseContext(),
							0, proximityIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

					// Setting proximity alert
					// The pending intent will be invoked when the device enters
					// or
					// exits the region 20 meters
					// away from the marked point
					// The -1 indicates that, the monitor will not be expired
					locationManager.addProximityAlert(lat, lon, 20, -1,
							pendingIntent);
				}
			} else {
				try {
					mClient = new MobileServiceClient(
							"https://futarflottamobile.azure-mobile.net/",
							"cMEWVBgHzMZBCrVgFByLvcgwTkfZmo87", this)
							.withFilter(new ProgressFilter());

				} catch (MalformedURLException e) {
					createAndShowDialog(
							new Exception(
									"Hiba a csatlakozással, kérem ellenõrizze a kapcsolatot."),
							"Error");
				}

				googleMap.clear();

				mTasksTable = mClient.getTable(Tasks.class);
				mTasksTable.where().field("username").eq(userName).and()
						.field("completed").eq(kesze)
						.execute(new TableQueryCallback<Tasks>() {

							@Override
							public void onCompleted(List<Tasks> result, int count, Exception exception, ServiceFilterResponse response) {
								if (exception == null) {
										for (Tasks item : result) {
											des_options_db = new MarkerOptions();

											String lat_db = item.getLatitude();
											String lon_db = item.getLongitude();

											latitudeDB = Double
													.parseDouble(lat_db);
											longitudeDB = Double
													.parseDouble(lon_db);
											LatLng latLngDesDB = new LatLng(
													latitudeDB, longitudeDB);
											des_options_db
													.position(latLngDesDB).title(item.getAddress());

											des_options_db
													.icon(BitmapDescriptorFactory
															.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

											googleMap.addMarker(des_options_db);
										}							
								} else {
									createAndShowDialog(exception, "Error");
								}

							}

						});
			}
		}
	}

	@Override
	public void onMyLocationChange(Location location) {

		// Getting latitude of the current location
		double latitude = location.getLatitude();

		// Getting longitude of the current location
		double longitude = location.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);
		LatLng latLngDes = new LatLng(lat, lon);

		// Instantiating MarkerOptions class
		start_options = new MarkerOptions();
		des_options = new MarkerOptions();

		// Setting position for the MarkerOptions
		start_options.position(latLng);
		des_options.position(latLngDes);

		start_options.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_RED));

		des_options.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

		// Getting Reference to SupportMapFragment of activity_map.xml
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		googleMap = fm.getMap();

		googleMap.addMarker(start_options);

		if (elsoInditas) {
			elsoInditas = false;
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(latLng).zoom(16).build();

			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		}

		if (isEnd) {
			googleMap.addMarker(des_options);
			String url = getDirectionsUrl(latLng, latLngDes);
			DownloadTask downloadTask = new DownloadTask();
			downloadTask.execute(url);
		}

	}

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	/** A class to download data from Google Directions URL */
	private class DownloadTask extends AsyncTask<String, Void, String> {

		// private ProgressDialog Dialog = new ProgressDialog(MapV2.this);
		//
		// protected void onPreExecute() {
		// Dialog.setMessage("Útvonal betöltése...");
		// Dialog.show();
		// }

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Dialog.dismiss();

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);

		}
	}

	/** A class to parse the Google Directions in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionJSONParser parser = new DirectionJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(3);
				lineOptions.color(Color.BLUE);

			}
			googleMap.clear();
			LatLng latLngDes = new LatLng(lat, lon);
			drawCircle(latLngDes);

			googleMap.addMarker(start_options);
			googleMap.addMarker(des_options);

			// Drawing polyline in the Google Map for the i-th route
			googleMap.addPolyline(lineOptions);
		}
	}

	private void drawCircle(LatLng point) {

		// Instantiating CircleOptions to draw a circle around the marker
		CircleOptions circleOptions = new CircleOptions();

		// Specifying the center of the circle
		circleOptions.center(point);

		// Radius of the circle
		circleOptions.radius(20);

		// Border color of the circle
		circleOptions.strokeColor(Color.BLACK);

		// Fill color of the circle
		circleOptions.fillColor(0x30ff0000);

		// Border width of the circle
		circleOptions.strokeWidth(2);

		// Adding the circle to the GoogleMap
		googleMap.addCircle(circleOptions);

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

	@Override
	public boolean onMarkerClick(Marker marker) {
		if(marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.showInfoWindow();
        }
        return true;
	}
}
