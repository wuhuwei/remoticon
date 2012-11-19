package edu.berkeley.remoticon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ArduinoBT extends Activity {
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

    private static final String powerCode = "35560,2380,560,1200,580,600,560,1200,580,600,560,1200,580,620,560,600,560,1220,560,620,560,600,560,620,560,620,25300,2380,560,1200,580,600,560,1220,560,600,580,1200,560,600,580,600,580,1200,560,600,580,600,580,600,560,620,25300,2400,540,1200,580,600,580,1180,580,620,560,1200,560,620,560,620,560,1200,560,620,560,620,560,600,580,600\n";

	
	
	private TextView statusText;
	private Button button1;
	private Button button2;
	private Button button3;
	private Button connect_button;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_bt);
        
        statusText = (TextView)findViewById(R.id.status);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
     	button3 = (Button)findViewById(R.id.button3);
     	connect_button = (Button)findViewById(R.id.connect_button);
     	
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBTAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        mBTService = new BluetoothService(this, mHandler);

     	
     	button1.setOnClickListener(new OnClickListener()
     	{
     		public void onClick(View v)
     		{
     			sendCode(powerCode);
     		}     		
     	});
     	button2.setOnClickListener(new OnClickListener()
     	{
     		public void onClick(View v)
     		{
     			sendCode(powerCode);
     		}     		
     	});
     	button3.setOnClickListener(new OnClickListener()
     	{
     		public void onClick(View v)
     		{
     			sendCode(powerCode);
     		}     		
     	});
     	connect_button.setOnClickListener(new OnClickListener()
     	{
     		public void onClick(View v)
     		{
     			connectToBT();
     		}
     	});
    }
    
    @Override
    public void onStart() {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
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

    
    public String formatIRCode(String code)
    {
    	int numNums = 0;
    	for (int j = 0; j < code.length(); ++j)
    	{
    		if (code.charAt(j) == ',')
    		{
    			++numNums;
    		}
    	}
    	return "" + (numNums+1) + "," + code;
    }
    public void sendCode(String code)
    {
    	if (mBTService.getState() == BluetoothService.STATE_CONNECTED)
    	{
    		mBTService.write(formatIRCode(code).getBytes());
    	}
    	else
    	{
    		connectToBT();
    	}
    }
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_arduino_bt, menu);
//        return true;
//    }
//        
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
                	statusText.setText("Connected");
                    break;
                case BluetoothService.STATE_CONNECTING:
                	statusText.setText("Connecting...");
                    break;
                case BluetoothService.STATE_NONE:
                	statusText.setText("Not Connected");
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
    };  
}
