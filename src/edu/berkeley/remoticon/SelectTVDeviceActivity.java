package edu.berkeley.remoticon;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectTVDeviceActivity extends Activity {
    // Return Intent extra
    public static String DEVICE_NAME = "deviceName";
    
    private ListView devicesList;
    private ArrayAdapter<String> devicesListAdapter;
    
    // hardcoded list of devices currently
    String[] DEVICES = { "Sony", "Samsung", "Panasonic" };
	String[] SONY_CODES = {
			"35560,2380,560,1200,580,600,560,1200,580,600,560,1200,580,620,560,600,560,1220,560,620,560,600,560,620,560,620,25300,2380,560,1200,580,600,560,1220,560,600,580,1200,560,600,580,600,580,1200,560,600,580,600,580,600,560,620,25300,2400,540,1200,580,600,580,1180,580,620,560,1200,560,620,560,620,560,1200,560,620,560,620,560,600,580,600", // power button
			"6088,2380,580,1200,560,1200,560,620,560,620,560,1200,560,620,560,600,580,1200,560,620,560,620,560,620,560,600,25300,2380,600,1180,580,1180,600,580,580,600,580,1180,600,580,580,600,580,1180,600,580,580,600,580,580,600,580,25300,2400,580,1180,580,1200,580,580,600,580,580,1180,600,580,600,580,580,1180,600,580,600,580,580,600,580,600,25300,2380,560,1200,560,1200,580,600,560,620,560,1200,580,600,560,620,560,1200,580,600,560,620,560,620,560,620,25300,2380,560,1200,560,1220,560,600,580,600,560,1200,580,600,580,600,560,1200,580,600,580,600,560,620,560,620,25300,2380,560,1200,580,1200,560,600,580,600,580,1200,560,600,580,600,560,1200,580,600,580,600,560,620,560,620", // volume down
			"1984,2400,560,600,600,580,600,580,600,580,580,1180,580,600,560,620,560,1200,580,600,560,620,560,620,560,600,26500,2380,560,620,560,600,580,600,560,620,560,1200,580,600,580,600,560,1200,580,600,560,620,560,620,560,600,26500,2380,560,620,560,600,580,600,580,600,560,1200,580,600,580,600,560,1200,580,600,560,620,560,620,560,620,26480,2380,560,620,560,600,580,600,580,600,560,1200,580,600,560,620,560,1220,560,600,560,620,560,620,560,620,26480,2380,560,620,560,620,560,620,560,600,560,1220,560,600,580,600,560,1200,580,600,580,600,560,620,560,620" // channel up
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_tv);

       
        devicesList = (ListView) findViewById(R.id.devicesList);
        
        String[] supportedDevices = getSupportedDevices();
        devicesListAdapter = new ArrayAdapter<String>(this,R.layout.device_name, supportedDevices);
        devicesList.setAdapter(devicesListAdapter);
        devicesList.setOnItemClickListener(new OnItemClickListener() {
        	
        	@Override
			public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
				// Get the device MAC address, which is the last 17 chars in the View
	            String deviceName = ((TextView) v).getText().toString();
	            HashMap<String, String> remoteCodes = getRemoteCodes(deviceName);
	            // Create the result Intent and include the MAC address
	            Intent intent = new Intent();
	            intent.putExtra(DEVICE_NAME, deviceName);
	            intent.putExtra("power", remoteCodes.get("power"));
	            intent.putExtra("channelUp", remoteCodes.get("channelUp"));
	            intent.putExtra("channelDown", remoteCodes.get("channelDown"));
	            intent.putExtra("volumeUp", remoteCodes.get("volumeUp"));
	            intent.putExtra("volumeDown", remoteCodes.get("volumeDown"));
	            
	            // Set result and finish this Activity
	            setResult(Activity.RESULT_OK, intent);
	            finish();
				
			}
        	
        });
        
	}
	
	// this is where we'd fetch remote codes from the server
	// we'll hard code this for now
	private HashMap<String, String> getRemoteCodes(String device) {
		HashMap<String, String> codes = new HashMap<String, String>();
		if(device == "Sony") {
			codes.put("power", SONY_CODES[0]);
			codes.put("volumeDown", SONY_CODES[1]);
			codes.put("channelUp", SONY_CODES[2]);
		}
		return codes;
	}
	
	
	
	// TODO: fill in this function to support fetching list of devices
	private String[] getSupportedDevices() {
		return DEVICES;
	}

}
