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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class InitialActivity extends FragmentActivity {
	private BluetoothAdapter mBTAdapter;
    
	private String mConnectedDeviceName = null;
    // Member object for the chat services
    private BluetoothService mBTService = null;
	
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

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
    private ProgressDialog connectingBar;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		
		mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBTAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        mBTService = new BluetoothService(this, mHandler);
        
        btStatusText = (TextView) findViewById(R.id.btStatusText);
        btStatusText.setVisibility(View.INVISIBLE);
        btConnectBtn = (Button) findViewById(R.id.bluetoothConnectionBtn);
        deviceSelectBtn = (Button) findViewById(R.id.findTVBtn);
        
        btConnectBtn.setOnClickListener(new OnClickListener()
     	{
     		public void onClick(View v)
     		{
     		     // If BT is not on, request that it be enabled.
     		        // setupChat() will then be called during onActivityResult
     		       
     		    
     			connectToBT();
     		}
     	});
        
        connectingBar = new ProgressDialog(this);
        
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
