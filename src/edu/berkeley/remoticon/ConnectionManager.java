package edu.berkeley.remoticon;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class ConnectionManager extends Application{
	private String TAG = "ConnectionManager";
	private static BluetoothAdapter mBTAdapter;
	private static BluetoothService mBTService = null;
	private static String mConnectedBTName = null;
	private static String mConnectedTVName = null;
	
    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final int SELECT_TV_DEVICE = 3;
    
    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new BTHandler();
    
    // Message types sent from the BluetoothService Handler
    private static final int MESSAGE_STATE_CHANGE = 1;
    private static final int MESSAGE_READ = 2;
    private static final int MESSAGE_WRITE = 3;
    private static final int MESSAGE_DEVICE_NAME = 4;
    private static final int MESSAGE_TOAST = 5;
    
    // Key names received from the BluetoothService Handler
    private static final String DEVICE_NAME = "device_name";
    private static final String TOAST = "toast";
        
	public void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

		Log.e(TAG, "connectDevice: " + address);
        // Attempt to connect to the device
        mBTService.connect(device);
    }

    
    public void setupService()
    {
    	Log.e(TAG, "setupService");
        if (mBTService != null) {
            if (mBTService.getState() == BluetoothService.STATE_NONE) {
            	mConnectedBTName = null;
            	mBTService.start();
            }
        }
    }
    
    public void stopService()
    {
    	Log.e(TAG, "stopService");
    	mConnectedBTName = null;
    	if (mBTService != null)
    	{
        	mBTService.stop();
    	}
    }
    
    public void setBTDeviceName(String bt)
    {
    	mConnectedBTName = bt;
    }
    
    public void setTVName(String tv)
    {
    	mConnectedTVName = tv;
    }
    
    public BluetoothAdapter getAdapter()
    {
    	if (mBTAdapter == null)
    	{
    		mBTAdapter = BluetoothAdapter.getDefaultAdapter();
    	}
    	return mBTAdapter;
    }
    
    public BluetoothService getBTService()
    {
    	if (mBTService == null)
    	{
    		mBTService = new BluetoothService(this, mHandler);
    	}
    	return mBTService;
    }
    
    public String getBTDeviceName()
    {
    	return mConnectedBTName;
    }
    
    public String getTVName()
    {
    	return mConnectedTVName;
    }
    
    
	private class BTHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
            	Intent statusChangedIntent = new Intent();
            	statusChangedIntent.setAction("edu.berkeley.remoticon.statusupdate");
            	String newStatus = "error";
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                	newStatus = "connected";
                    break;
                case BluetoothService.STATE_CONNECTING:
                	newStatus = "connecting";
                    break;
                case BluetoothService.STATE_NONE:
                	newStatus = "none";
                    break;
                }
                statusChangedIntent.putExtra("status", newStatus);
                Log.e(TAG, "broadcasting status change: " + newStatus);
                sendBroadcast(statusChangedIntent);
                break;
            
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
            	mConnectedBTName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedBTName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
