package hu.sze.zoltan.futarflotta;

import java.net.MalformedURLException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class NotificationView extends Activity {

	private Button btnYes;
	private Button btnNo;
	public String latitude = "";
	public String longitude = "";
	private ProgressBar pbLoading;
	private LinearLayout llDelivered;

	private MobileServiceClient mClient;
	private MobileServiceTable<Tasks> mTasksTable;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification);

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

		btnNo = (Button) findViewById(R.id.btnNo);
		btnYes = (Button) findViewById(R.id.btnYes);
		pbLoading = (ProgressBar) findViewById(R.id.progressBar1);
		llDelivered = (LinearLayout) findViewById(R.id.ll_delivered);
		TextView tv = (TextView) findViewById(R.id.tv_notification);

		Bundle data = getIntent().getExtras();
		tv.setText(data.getString("content"));
		latitude = data.getString("latitude");
		longitude = data.getString("longitude");

		pbLoading.setVisibility(ProgressBar.GONE);

		mTasksTable = mClient.getTable("Tasks", Tasks.class);
		mTasksTable.where().field("latitude").eq(latitude).and()
				.field("longitude").eq(longitude)
				.execute(new TableQueryCallback<Tasks>() {

					@Override
					public void onCompleted(List<Tasks> result, int count,
							Exception exception, ServiceFilterResponse response) {
						if (exception == null) {
							if (!result.isEmpty()) {
								for (Tasks item : result) {
									if(item.isComplete() == true){
										llDelivered.setVisibility(LinearLayout.GONE);
									} else {
										llDelivered.setVisibility(LinearLayout.VISIBLE);
									}
								}
							}
						}

					}
				});

		btnNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		btnYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pbLoading.setVisibility(ProgressBar.VISIBLE);
				mTasksTable = mClient.getTable("Tasks", Tasks.class);
				mTasksTable.where().field("latitude").eq(latitude).and()
						.field("longitude").eq(longitude)
						.execute(new TableQueryCallback<Tasks>() {

							@Override
							public void onCompleted(List<Tasks> result,
									int count, Exception exception,
									ServiceFilterResponse response) {
								if (exception == null) {
									if (!result.isEmpty()) {
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
																	pbLoading
																			.setVisibility(ProgressBar.GONE);
																	MapV2.MapV2
																			.finish();
																	finish();
																}
															});
										}
									}
								}
							}
						});
			}
		});

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
