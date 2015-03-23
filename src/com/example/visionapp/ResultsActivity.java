package com.example.visionapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ResultsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	protected void onResume() {
        super.onResume();
        try {
			getRtpi();
		} catch (Exception e) {
			e.printStackTrace();
		}
    } 
	
	public void getRtpi() throws Exception {
		String stopNumber = null;
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			stopNumber = extras.getString("rtpi stop");
		}
		System.out.println(stopNumber);
		
		// TODO: check if internet connection is available
		//sendRequest(stopNumber);
		//new Soap().execute();
    	Document doc = sendGet(stopNumber);
		String[][] results = parseDoc(doc);
		TableLayout table = (TableLayout) findViewById(R.id.tableLayout1);
		
		for (int i = 0; i < results.length; i++) {
			TableRow row = new TableRow(this);
			for (int j = 0; j < results[i].length; j++) {
				TextView t = new TextView(this);
				t.setText(" " + results[i][j] + " ");
				row.addView(t);
			}
			
			table.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
    }
    
	private void sendRequest(String stopNumber) throws Exception {
		final String SOAP_ACTION = "http://tempuri.org/GetInteger2";
		final String METHOD_NAME = "GetInteger2";
		final String NAMESPACE = "http://tempuri.org/";
		final String URL = "http://10.0.22:4711/Service1.asmx";
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

	    PropertyInfo pi = new PropertyInfo();
	    pi.name = "i";
	    pi.type = PropertyInfo.INTEGER_CLASS;
	    request.addProperty(pi, 123);

	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.dotNet = true;
	    envelope.setOutputSoapObject(request);

	    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
	    androidHttpTransport.call(SOAP_ACTION, envelope);

	    SoapPrimitive result = (SoapPrimitive)envelope.getResponse();
	    int requestResult = Integer.parseInt(result.toString());
	    System.out.println("SOAP response is " + requestResult);
	}
	
 	private Document sendGet(String stopNumber) throws Exception {
 		String url = "http://rtpi.ie/Text/WebDisplay.aspx?stopRef=" + ("00000" + stopNumber).substring(stopNumber.length());
  
 		URL obj;
 		StringBuffer response = null;
		obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
   		con.setRequestMethod("GET");
   		con.setRequestProperty("User-Agent", "Mozilla/5.0");
  
 		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
 		String inputLine;
 		response = new StringBuffer();
  
 		while ((inputLine = in.readLine()) != null) {
 			response.append(inputLine);
 		}
 		in.close();
 		
 
 		Document doc = Jsoup.parse(response.toString());
 		
 		return doc;
 	}
 	
 	private String[][] parseDoc(Document doc) {
 		String result = doc.getElementsByClass("webDisplayTable").toString();
		String[] parsed = result.split("<tr");
		
		if (parsed.length < 2) {
			String[][] results = new String[1][1];
			results[0][0] = "Sorry, Real Time Information is\ncurrently unavailable for this bus stop.";
			return results;
		}
		else {
			String[][] results = new String[parsed.length - 2][];
			for (int i = 1; i < parsed.length - 1; i++) {
				results[i - 1] = parsed[i].split("<td");
			}
					
			String[][] splitResults = new String[results.length-1][3];
			for (int i = 0; i < splitResults.length; i++) {
				for (int j = 0; j < 3; j++) {
					splitResults[i][j] = results[i+1][j+1];
					if (j == 2) {
						splitResults[i][j] = splitResults[i][j].substring(33, splitResults[i][j].length() - 9);
					}
					else {
						splitResults[i][j] = splitResults[i][j].substring(19, splitResults[i][j].length() - 9);
					}
				}
			}
			
			return splitResults;
		}
 	}
 	
 	public void goBack(View view) {
 		finish();
 	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_results,
					container, false);
			return rootView;
		}
	}

public class Soap extends AsyncTask<Void, Void, String> {
	ProgressDialog progress;
    String response = "";

	public void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(Void... params) {
		final String SOAP_ACTION = "http://tempuri.org/GetInteger2";
		final String METHOD_NAME = "GetInteger2";
		final String NAMESPACE = "http://tempuri.org/";
		final String URL = "http://10.0.22:4711/Service1.asmx";
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

	    PropertyInfo pi = new PropertyInfo();
	    pi.name = "i";
	    pi.type = PropertyInfo.INTEGER_CLASS;
	    request.addProperty(pi, 123);

	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.dotNet = true;
	    envelope.setOutputSoapObject(request);

	    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
	    try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
		} catch (HttpResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    SoapPrimitive result = null;
		try {
			result = (SoapPrimitive)envelope.getResponse();
		} catch (SoapFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //int requestResult = Integer.parseInt(result.toString());
	    //System.out.println("SOAP response is " + requestResult);
		String requestResult = result.toString();
		System.out.println("SOAP response is " + requestResult);
		return requestResult;
	}
	
}

}