package com.feigdev.okhttpexample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

public class BackgroundWebRunner extends AsyncTask<Void, Void, String> {
	private static final String TAG = "BackgroundWebRunner";
	private static final String ENDPOINT = "http://www.feigdev.com";

	@Override
	protected String doInBackground(Void... arg0) {
		String response = null;

		OkHttpClient client = new OkHttpClient();

		// Ignore invalid SSL endpoints.
		client.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String s, SSLSession sslSession) {
				return true;
			}
		});

		// Create request for remote resource.
		HttpURLConnection connection;
		try {
			connection = client.open(new URL(ENDPOINT));
			InputStream is = connection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			// from StackOverflow: http://stackoverflow.com/a/2549222
			BufferedReader r = new BufferedReader(isr);
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
			    total.append(line);
			}

			response = total.toString();
			Log.d(TAG,"response: " + response);
		} catch (MalformedURLException e) {
			Log.e(TAG,"",e);
		} catch (IOException e) {
			Log.e(TAG,"",e);
		}

		return response;
	}

	@Override
	protected void onPostExecute(String result) {
		BusProvider.getInstance().register(this);
		BusProvider.getInstance().post(result);
		BusProvider.getInstance().unregister(this);
	}
}
