package hu.sze.zoltan.futarflotta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ToDoTasks extends Activity {
	
	ListView mListView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todotasks_activity);
		
		// URL to the JSON data         
        String strUrl = "http://futarflotta.azurewebsites.net";
        
     // Creating a new non-ui thread task to download json data 
        DownloadTask downloadTask = new DownloadTask();

        // Starting the download process
        downloadTask.execute(strUrl);
        
        // Getting a reference to ListView of activity_main
        mListView = (ListView) findViewById(R.id.lv_tasks);
	}
	
	/** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        try{
                URL url = new URL(strUrl);
                
                // Creating an http connection to communicate with url 
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url 
                urlConnection.connect();

                // Reading data from url 
                iStream = urlConnection.getInputStream();
                
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                
                StringBuffer sb  = new StringBuffer();
                
                String line = "";
                while( ( line = br.readLine())  != null){
                	sb.append(line);
                }
                
                data = sb.toString();
                
                br.close();

        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
        }

        return data;
    }
    
    /** AsyncTask to download json data */
    private class DownloadTask extends AsyncTask<String, Integer, String>{
        String data = null;
                @Override
                protected String doInBackground(String... url) {
                        try{
                            data = downloadUrl(url[0]);
                                
                        }catch(Exception e){
                        	Log.d("Background Task",e.toString());
                        }
                        return data;
                }

                @Override
                protected void onPostExecute(String result) {
                	
                        // The parsing of the xml data is done in a non-ui thread 
                        ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();
                        
                        // Start parsing xml data
                        listViewLoaderTask.execute(result);                        
                        
                }
    }
    
    /** AsyncTask to parse json data and load ListView */
    private class ListViewLoaderTask extends AsyncTask<String, Void, SimpleAdapter>{

    	JSONObject jObject;
    	// Doing the parsing of xml data in a non-ui thread 
		@Override
		protected SimpleAdapter doInBackground(String... strJson) {
			try{
	        	jObject = new JSONObject(strJson[0]);
	        	ToDoJSONParser taskJsonParser = new ToDoJSONParser();
	        	taskJsonParser.parse(jObject);
	        }catch(Exception e){
	        	Log.d("JSON Exception1",e.toString());
	        }
			
			// Instantiating json parser class
			ToDoJSONParser taskJsonParser = new ToDoJSONParser();
			
			// A list object to store the parsed countries list
	        List<HashMap<String, Object>> tasks = null;
	        
	        try{
	        	// Getting the parsed data as a List construct
	        	tasks = taskJsonParser.parse(jObject);
	        }catch(Exception e){
	        	Log.d("Exception",e.toString());
	        }	       

	        // Keys used in Hashmap 
	        String[] from = { "name","cim"};

	        // Ids of views in listview_layout
	        int[] to = { R.id.tvNev,R.id.tvCim};

	        // Instantiating an adapter to store each items
	        // R.layout.listview_layout defines the layout of each item	        
	        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), tasks, R.layout.listview_layout, from, to);  
	        
			return adapter;
		}
    }
}
