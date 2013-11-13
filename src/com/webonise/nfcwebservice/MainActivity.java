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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private NfcAdapter nfcAdapter;
	private Context context;
	private PendingIntent pendingIntent;
	private IntentFilter[] mReadTagFilters;
	TextView textViewMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = getApplicationContext();
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		ndefDetected.addDataScheme("http");
		IntentFilter techDetected = new IntentFilter(
				NfcAdapter.ACTION_TECH_DISCOVERED);
		mReadTagFilters = new IntentFilter[] { ndefDetected, techDetected };
	}

	public void onClickReadTag(View view) {
		if (nfcAdapter != null) {
			if (!nfcAdapter.isEnabled()) {
				new AlertDialog.Builder(this)
						.setTitle("NFC Dialog")
						.setMessage("Sample message")
						.setPositiveButton("Update Settings",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0,
											int arg1) {
										Intent setnfc = new Intent(
												Settings.ACTION_WIRELESS_SETTINGS);
										startActivity(setnfc);

									}
								})
						.setOnCancelListener(
								new DialogInterface.OnCancelListener() {
									public void onCancel(DialogInterface dialog) {
										dialog.dismiss();
									}
								}).create().show();
			}
			nfcAdapter.enableForegroundDispatch(this, pendingIntent,
					mReadTagFilters, null);

		} else {
			Toast.makeText(context,
					"Sorry, NFC adapter not available on your device.",
					Toast.LENGTH_SHORT).show();

		}
	}

	private void onWebServiceCall() {
		WebService webService = new WebService(this);
		webService.execute();
	}

	public void onPostResultReceive(String result) {
		textViewMessage = (TextView) findViewById(R.id.textViewMessage);
		String stringResult = null;
		Log.v(null, result);
		textViewMessage.setText(result);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		NdefMessage[] messages = null;
		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (rawMsgs != null) {
			messages = new NdefMessage[rawMsgs.length];
			for (int i = 0; i < rawMsgs.length; i++) {
				messages[i] = (NdefMessage) rawMsgs[i];
			}
		}
		if (messages[0] != null) {
			String result = "";
			byte[] payload = messages[0].getRecords()[0].getPayload();

			for (int b = 1; b < payload.length; b++) {
				result += (char) payload[b];
			}
			Toast.makeText(getApplicationContext(), "Tag has : " + result,
					Toast.LENGTH_LONG).show();
			onWebServiceCall();

		}

	}
}
