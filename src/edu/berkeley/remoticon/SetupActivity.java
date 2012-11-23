package edu.berkeley.remoticon;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SetupActivity extends Activity {
	private BluetoothAdapter mBTAdapter;
    
	private String mConnectedDeviceName = null;
    // Member object for the chat services
    private BluetoothService mBTService = null;
	
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int SELECT_TV_DEVICE = 3;

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new BTHandler();
    
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    private TextView btStatusText;
    private Button btConnectBtn;
    private Button deviceSelectBtn;
    private Button finishSetupBtn;
    private TextView tvSelectStatusText;
    private ProgressDialog connectingBar;
    
    private String deviceName;
    private HashMap<String, String> remoteCodes;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		
		// Check if we've already done setup. If so, don't present setup
        SharedPreferences prefs = getSharedPreferences("edu.berkeley.remoticon", Context.MODE_PRIVATE);
        if(prefs.getString("deviceName", null) != null) {
        	Intent i = new Intent(this, MenuActivity.class);
        	startActivity(i);
        	finish();		
        	return;
        }
        
		mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBTAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        mBTService = new BluetoothService(this, mHandler);
        
        connectingBar = new ProgressDialog(this);
        
        btStatusText = (TextView) findViewById(R.id.btStatusText);
        btStatusText.setVisibility(View.INVISIBLE);
        btConnectBtn = (Button) findViewById(R.id.bluetoothConnectionBtn);
        btConnectBtn.setOnClickListener(new OnClickListener()
     	{
     		public void onClick(View v)
     		{
     		     // If BT is not on, request that it be enabled.
     		        // setupChat() will then be called during onActivityResult
     		       
     		    
     			connectToBT();
     		}
     	});
        
        deviceSelectBtn = (Button) findViewById(R.id.findTVBtn);
        deviceSelectBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		selectTVDevice();
        	}
        }); 
        tvSelectStatusText = (TextView) findViewById(R.id.tvSelectStatusText);
        tvSelectStatusText.setText("Status: None selected");
        
        finishSetupBtn = (Button) findViewById(R.id.finishSetup);
        finishSetupBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if(deviceName == null) {
        			Toast.makeText(v.getContext(), R.string.tv_device_not_selected, Toast.LENGTH_SHORT).show();
        			
        		} else {
        			setupRemoteCodes();
        			
            		
        		}
        		
        	}
        });
	}
	
	@Override
	public synchronized void onStart() {
		super.onStart();
		if (!mBTAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}

	}
    @Override
    public synchronized void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBTService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBTService.getState() == BluetoothService.STATE_NONE) {
              // Start the Bluetooth chat services
              mBTService.start();
            }
        }
    }
    
	@Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mBTService != null) mBTService.stop();
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
            } else {
                // User did not enable Bluetooth or an error occurred
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        case SELECT_TV_DEVICE:
        	if (resultCode == Activity.RESULT_OK) {
        		deviceName = data.getStringExtra("deviceName");
        		remoteCodes.put("power", data.getStringExtra("power"));
        		remoteCodes.put("channelUp", data.getStringExtra("channelUp"));
        		remoteCodes.put("channelDown", data.getStringExtra("channelDown"));
        		remoteCodes.put("volumeUp", data.getStringExtra("volumeUp"));
        		remoteCodes.put("volumeDown", data.getStringExtra("volumeDown"));
        		
        		tvSelectStatusText.setText("Device: " + deviceName);
        	}
			break;
        }
    }
	
	private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBTService.connect(device);
    }
	
    private void connectToBT()
	{
        // Launch the DeviceListActivity to see devices and do scan
        Intent listDevicesIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(listDevicesIntent, REQUEST_CONNECT_DEVICE);
	}
    
    public void selectTVDevice() {
		Intent listTVDevicesIntent = new Intent(this, SelectTVDeviceActivity.class);
		startActivityForResult(listTVDevicesIntent, SELECT_TV_DEVICE);
	}
    
    public void setupRemoteCodes() {
    	// save preferences
    	
    	SharedPreferences prefs = getSharedPreferences("edu.berkeley.remoticon", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("deviceName", deviceName);
		editor.putString("power", remoteCodes.get("power"));
		editor.putString("channelUp", remoteCodes.get("channelUp"));
		editor.putString("channelDown", remoteCodes.get("channelDown"));
		editor.putString("volumeUp", remoteCodes.get("volumeUp"));
		editor.putString("volumeDown", remoteCodes.get("channelUp"));
		editor.commit();
    	Intent mainMenuIntent = new Intent(this, MenuActivity.class);
    	startActivity(mainMenuIntent);
    	finish();
    }
    
	private class BTHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                	btStatusText.setText("Status: Connected");
                	btStatusText.setVisibility(View.VISIBLE);
                	connectingBar.hide();
                    break;
                case BluetoothService.STATE_CONNECTING:
                	connectingBar.setMessage("Connecting...");
                	btStatusText.setText("Status: Trying to connect");
                	connectingBar.show();
                    break;
                case BluetoothService.STATE_NONE:
                	btStatusText.setText("Status: Not Connected");
                	btStatusText.setVisibility(View.VISIBLE);
                	connectingBar.hide();
                    break;
                }
                break;
            
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

}
