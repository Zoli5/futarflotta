package hu.sze.zoltan.futarflotta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ToDoJSONParser {
	public List<HashMap<String, Object>> parse(JSONObject jObject) {

		JSONArray jTasks = null;
		try {
			// Retrieves all the elements in the 'countries' array
			jTasks = jObject.getJSONArray("tasks");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Invoking getCountries with the array of json object
		// where each json object represent a country
		return getTasks(jTasks);
	}

	private List<HashMap<String, Object>> getTasks(JSONArray jTasks) {
		int taskCount = jTasks.length();
		List<HashMap<String, Object>> taskList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> task = null;

		// Taking each country, parses and adds to list object
		for (int i = 0; i < taskCount; i++) {
			try {
				// Call getCountry with country JSON object to parse the country
				task = getTask((JSONObject) jTasks.get(i));
				taskList.add(task);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return taskList;
	}
	
	private HashMap<String, Object> getTask(JSONObject jTasks){

		HashMap<String, Object> task = new HashMap<String, Object>();
		String name = "";
		String username="";
		String latitude="";
		String longitude="";

		
		try {
			name = jTasks.getString("name");
			username = jTasks.getString("username");
			latitude = jTasks.getString("latitude");
			longitude = jTasks.getString("longitude");
			
			String cim = "Latitude: " + latitude + ", longitude: " + longitude;
						
			task.put("name", name);
			task.put("username", username);
			task.put("latitude", latitude);
			task.put("longitude", longitude);
			task.put("cim", cim);
			
		} catch (JSONException e) {			
			e.printStackTrace();
		}		
		return task;
	}
}
