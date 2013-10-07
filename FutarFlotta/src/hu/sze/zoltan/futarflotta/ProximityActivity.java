package hu.sze.zoltan.futarflotta;

import java.net.MalformedURLException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class ProximityActivity extends Activity {

	String notificationTitle;
	String notificationContent;
	String tickerMessage;
	public String latitude = "";
	public String longitude = "";
	public String userName = "";

	public LocationManager locationManager;
	public PendingIntent pendingIntent;
	
	private MobileServiceClient mClient;
	private MobileServiceTable<Tasks> mTasksTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.proximity_activity);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			latitude = extras.getString("latitude");
			longitude = extras.getString("longitude");
			userName = extras.getString("userName");
		}
		

		boolean proximity_entering = getIntent().getBooleanExtra(
				LocationManager.KEY_PROXIMITY_ENTERING, false);

		try {
			mClient = new MobileServiceClient(
					"https://futarflottamobile.azure-mobile.net/",
					"cMEWVBgHzMZBCrVgFByLvcgwTkfZmo87", this);

		} catch (MalformedURLException e) {
			createAndShowDialog(
					new Exception(
							"There was an error creating the Mobile Service. Verify the URL"),
					"Error");
		}

		if (proximity_entering) {
			Toast.makeText(getBaseContext(), "Entering the region",
					Toast.LENGTH_LONG).show();
			notificationTitle = "Proximity - Belépés";
			notificationContent = "Beléptél a régióba";
			tickerMessage = "Beléptél a régióba";
		} else {
//			Toast.makeText(getBaseContext(), "Exiting the region",
//					Toast.LENGTH_LONG).show();
//			notificationTitle = "Proximity - Kilépés";
//			notificationContent = "Elhagyta a régiót";
//			tickerMessage = "Elhagyta a régiót";
			this.finish();
		}

		Intent notificationIntent = new Intent(getApplicationContext(),
				NotificationView.class);
		notificationIntent.putExtra("content", notificationContent);
		notificationIntent.putExtra("latitude", latitude);
		notificationIntent.putExtra("longitude", longitude);

		/**
		 * This is needed to make this intent different from its previous
		 * intents
		 */
//		notificationIntent.setData(Uri.parse("tel:/"
//				+ (int) System.currentTimeMillis()));

		/**
		 * Creating different tasks for each notification. See the flag
		 * Intent.FLAG_ACTIVITY_NEW_TASK
		 */
		pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent,
				Intent.FLAG_ACTIVITY_NEW_TASK);

		/** Getting the System service NotificationManager */
		NotificationManager nManager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);

		/** Configuring notification builder to create a notification */
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				getApplicationContext())
				.setContentText(notificationContent)
				.setContentTitle(notificationTitle)
				.setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
				.setTicker(tickerMessage).setContentIntent(pendingIntent);

		new AlertDialog.Builder(this)
				.setTitle("Megérkezett!")
				.setMessage(
						"Megérkezett a kézbesítési címhez. Kézbesítette a csomagot?")
				.setCancelable(false)
				.setPositiveButton("Igen",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mTasksTable = mClient.getTable("Tasks",
										Tasks.class);
								mTasksTable
										.where()
										.field("latitude")
										.eq(latitude)
										.and()
										.field("longitude")
										.eq(longitude)
										.execute(
												new TableQueryCallback<Tasks>() {

													@Override
													public void onCompleted(
															List<Tasks> result,
															int count,
															Exception exception,
															ServiceFilterResponse response) {
														if (exception == null) {
															if (!result
																	.isEmpty()) {
																for (Tasks item : result) {
																	item.setComplete(true);
																	mTasksTable
																			.update(item,
																					new TableOperationCallback<Tasks>() {

																						@Override
																						public void onCompleted(
																								Tasks entity,
																								Exception exception,
																								ServiceFilterResponse response) {
																							Toast.makeText(
																									getApplicationContext(),
																									"Csomag kézbesítve",
																									Toast.LENGTH_LONG)
																									.show();
//																							Intent myIntent = new Intent(
//																									ProximityActivity.this,
//																									TasksActivity.class);
//																							myIntent.putExtra(
//																									"userName",
//																									userName);
//																							startActivity(myIntent);
																							MapV2.MapV2.finish();
																							finish();
																						}
																					});
																}
															}
														}
													}
												});
							}
						})
				.setNegativeButton("Nem",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								finish();
							}
						}).create().show();

		/** Creating a notification from the notification builder */
		Notification notification = notificationBuilder.build();

		/**
		 * Sending the notification to system. The first argument ensures that
		 * each notification is having a unique id If two notifications share
		 * same notification id, then the last notification replaces the first
		 * notification
		 * */
		nManager.notify((int) System.currentTimeMillis(), notification);
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
}