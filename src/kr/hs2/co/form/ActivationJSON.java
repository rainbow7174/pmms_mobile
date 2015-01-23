package kr.hs2.co.form;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivationJSON extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activation_json);
		Button submitButton = (Button) this.findViewById(R.id.submit_btn);
		submitButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				new ReadJSONFeed()
//						.execute("http://hitag.kr/customer/data/json/activation.json.php");
						.execute("http://hitag.kr/json/activation.json");
			}
		});
	}

	private class ReadJSONFeed extends AsyncTask<String, String, String> {
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... urls) {
			HttpClient httpclient = new DefaultHttpClient();
			StringBuilder builder = new StringBuilder();
			HttpPost httppost = new HttpPost(urls[0]);
			try {
				HttpResponse response = httpclient.execute(httppost);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return builder.toString();
		}

		protected void onPostExecute(String result) {
			String state = "";
			String stateInfo = "";
			EditText stateName = (EditText) findViewById(R.id.state_name);
			String searchState = stateName.getText().toString();
			try {
				JSONArray countriesArray = new JSONArray(result);
				for (int i = 0; i < countriesArray.length(); i++) {
					JSONObject jObject = countriesArray.getJSONObject(i);
//					state = jObject.getString("state");
//					if (searchState.equalsIgnoreCase(state)) {
//						stateInfo += "Capital: " + jObject.getString("capital") + "\n";
//						stateInfo += "Latitude: " + jObject.getString("latitude") + "\n";
//						stateInfo += "Longitude: " + jObject.getString("longitude") + "\n";
//					}
					
					state = jObject.getString("records");
					JSONArray stateArray = new JSONArray(state);
					for (int j = 0; j < stateArray.length(); j++) {
						JSONObject objRec = stateArray.getJSONObject(j);
						
						if (searchState.equalsIgnoreCase(objRec.getString("product_name"))) {
						stateInfo += "tagid: "+objRec.getString("tagid")+"\n";
						stateInfo += "product_name: "+objRec.getString("product_name")+"\n";
						stateInfo += "product_no: "+objRec.getString("product_no")+"\n";
						stateInfo += "serial_no: "+objRec.getString("serial_no")+"\n";
						stateInfo += "filename: "+objRec.getString("filename")+"\n";
						stateInfo += "regdate: "+objRec.getString("regdate")+"\n";
						}
					}
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			TextView resp = (TextView) findViewById(R.id.response);
			if (stateInfo.trim().length() > 0)
				resp.setText(stateInfo);
			else
				resp.setText("Sorry no match found");
		}
	}
}