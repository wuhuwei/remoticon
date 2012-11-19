package edu.berkeley.remoticon;

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

	            // Create the result Intent and include the MAC address
	            Intent intent = new Intent();
	            intent.putExtra(DEVICE_NAME, deviceName);

	            // Set result and finish this Activity
	            setResult(Activity.RESULT_OK, intent);
	            finish();
				
			}
        	
        });
        
	}
	
	
	
	// TODO: fill in this function to support fetching list of devices
	private String[] getSupportedDevices() {
		return DEVICES;
	}

}
