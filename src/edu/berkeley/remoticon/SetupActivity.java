package edu.berkeley.remoticon;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SetupActivity extends Activity {
	private static String TAG = "SetupActivity";
	
	private ConnectionManager CM;
	private ConnectionListener CL;
	
	private boolean listenerRegistered = false;
        
    private TextView btStatusText;
    private Button btConnectBtn;
    private Button tvSelectBtn;
    private Button finishSetupBtn;
    private TextView tvSelectStatusText;
    private ProgressDialog connectingBar;
    
    private boolean keepService = false;    	
    private HashMap<String, String> remoteCodes;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		
		CM = (ConnectionManager)getApplicationContext();
		
		// Check if we've already done setup. If so, don't present setup
        SharedPreferences prefs = getSharedPreferences("edu.berkeley.remoticon", Context.MODE_PRIVATE);
        boolean forced = getIntent().getBooleanExtra("force", false);
        if(!forced && prefs.getString("tvName", null) != null) {
        	Intent i = new Intent(this, MenuActivity.class);
        	startActivity(i);
        	finish();		
        	return;
        }

        // If the adapter is null, then Bluetooth is not supported
        if (CM.getAdapter() == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        CM.getBTService();
        
        CL = new ConnectionListener();
        if (!listenerRegistered) {
            registerReceiver(CL, new IntentFilter("edu.berkeley.remoticon.statusupdate"));
            listenerRegistered = true;
        }
        
        connectingBar = new ProgressDialog(this);
        
        btStatusText = (TextView) findViewById(R.id.btStatusText);
        if (CM.getBTDeviceName() != null)
        {
        	btStatusText.setText("Connected to: " + CM.getBTDeviceName());
        	btStatusText.setVisibility(View.VISIBLE);
        }
        else
        {
        	btStatusText.setVisibility(View.INVISIBLE);
        }
        btConnectBtn = (Button) findViewById(R.id.bluetoothConnectionBtn);
        btConnectBtn.setOnClickListener(new OnClickListener()
     	{
     		public void onClick(View v)
     		{
     		     // If BT is not on, request that it be enabled.
     			connectToBT();
     		}
     	});
        
        tvSelectBtn = (Button) findViewById(R.id.findTVBtn);
        tvSelectBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		selectTVDevice();
        	}
        }); 
        tvSelectStatusText = (TextView) findViewById(R.id.tvSelectStatusText);
        if (CM.getTVName() == null)
        {
        	tvSelectStatusText.setText("None selected");
        }
        else
        {
        	tvSelectStatusText.setText(CM.getTVName());
        }
        
        finishSetupBtn = (Button) findViewById(R.id.finishSetup);
        finishSetupBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if(CM.getTVName() == null) {
        			Toast.makeText(v.getContext(), R.string.tv_device_not_selected, Toast.LENGTH_SHORT).show();
        			
        		} else {
        			keepService = true;
        			SharedPreferences prefs = v.getContext().getSharedPreferences("edu.berkeley.remoticon", Context.MODE_PRIVATE);
            		SharedPreferences.Editor editor = prefs.edit();
            		editor.putString("tvName", CM.getTVName());
            		editor.commit();
        			setupRemoteCodes();
            		Intent i = new Intent(v.getContext(), MenuActivity.class);
            		startActivity(i);
            		SetupActivity.this.finish();
        			
            		
        		}
        		
        	}
        });
	}
	
	@Override
	public synchronized void onStart() {
		Log.e(TAG, "onStart");
		super.onStart();
		if (!CM.getAdapter().isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, ConnectionManager.REQUEST_ENABLE_BT);
		}

	}
    @Override
    public synchronized void onResume() {
    	Log.e(TAG, "onResume");
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        CM.setupService();
    }
    
	@Override
    public void onDestroy() {
		Log.e(TAG, "onDestroy");
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (!keepService)
        {
        	CM.stopService();
       	}
        keepService = false;
        if (connectingBar != null)
        {
        	connectingBar.dismiss();
        }
        if (listenerRegistered)
        {
	        listenerRegistered = false;
	        unregisterReceiver(CL);
        }
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case ConnectionManager.REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                CM.connectDevice(data);
            }
            break;
        case ConnectionManager.REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
            } else {
                // User did not enable Bluetooth or an error occurred
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        case ConnectionManager.SELECT_TV_DEVICE:
        	if (resultCode == Activity.RESULT_OK) {
        		CM.setTVName(data.getStringExtra("deviceName"));
        		tvSelectStatusText.setText(CM.getTVName());

        		remoteCodes.put("power", data.getStringExtra("power"));
        		remoteCodes.put("channelUp", data.getStringExtra("channelUp"));
        		remoteCodes.put("channelDown", data.getStringExtra("channelDown"));
        		remoteCodes.put("volumeUp", data.getStringExtra("volumeUp"));
        		remoteCodes.put("volumeDown", data.getStringExtra("volumeDown"));        		
        	}
			break;
        }
    }
	
	
    private void connectToBT()
	{
        // Launch the DeviceListActivity to see devices and do scan
        Intent listDevicesIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(listDevicesIntent, ConnectionManager.REQUEST_CONNECT_DEVICE);
	}
    
    public void selectTVDevice() {
		Intent listTVDevicesIntent = new Intent(this, SelectTVDeviceActivity.class);
		startActivityForResult(listTVDevicesIntent, ConnectionManager.SELECT_TV_DEVICE);
	}
    
    public void setupRemoteCodes() {
    	// save preferences
    	
    	SharedPreferences prefs = getSharedPreferences("edu.berkeley.remoticon", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("deviceName", CM.getTVName());
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
    
    private class ConnectionListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("edu.berkeley.remoticon.statusupdate")) {
            	//intent has state, but may as well query it directly
            	switch(CM.getBTService().getState())
            	{
            	case BluetoothService.STATE_CONNECTED:
                	btStatusText.setText("Connected to: " + CM.getBTDeviceName());
                	btStatusText.setVisibility(View.VISIBLE);
                	connectingBar.hide();
            		break;
            	case BluetoothService.STATE_CONNECTING:
                	connectingBar.setMessage("Connecting...");
                	btStatusText.setText("Connecting...");
                	connectingBar.show();
            		break;
            	case BluetoothService.STATE_NONE:
                	btStatusText.setText("Not Connected");
                	btStatusText.setVisibility(View.VISIBLE);
                	connectingBar.hide();
            		break;
            	}
            }
        }
    }
}
