package com.webonise.nfcwebservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class WebService extends AsyncTask<Void, Void, String> {

	MainActivity context;

	public WebService(MainActivity mainActivity) {
		context = mainActivity;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		context.onPostResultReceive(result);
	}

	@Override
	protected String doInBackground(Void... params) {
		String stringResult = null;
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://api.androidhive.info/contacts/");
		try {
			HttpResponse response = client.execute(get);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null)
					stringBuilder.append(line);
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		stringResult = stringBuilder.toString();
		Log.i(null, stringResult);
		return stringResult;
	}

}
