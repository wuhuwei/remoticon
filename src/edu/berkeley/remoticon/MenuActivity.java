package edu.berkeley.remoticon;

import java.util.HashMap;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

public class MenuActivity extends FragmentActivity {
	private String TAG = "MenuActivity";
	private ConnectionManager CM;
	private ConnectionListener CL;
	
	private boolean listenerRegistered = false;
	private boolean keepService = false;
	
	private final String REMOTE_TAB = "remote";
	private final String GUIDE_TAB = "guide";
	private final String FAVORITES_TAB = "favorites";
	private final String EXPLORE_TAB = "explore";

	private MenuItem btButton;
	
	private TabHost mTabHost;
	private HashMap mapTabInfo = new HashMap();
	private TabInfo mLastTab = null;
	private RoviApiHandler apiHandler;
	
	public RoviApiHandler getApiHandler() {
		return apiHandler;
	}
	private class TabInfo {
		private String tag;
		private Class clss;
		private Bundle args;
		private Fragment fragment;

		TabInfo(String tag, Class clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CM = (ConnectionManager)getApplicationContext();
        CL = new ConnectionListener();
        if (!listenerRegistered) {
            registerReceiver(CL, new IntentFilter("edu.berkeley.remoticon.statusupdate"));
            listenerRegistered = true;
        }

		
		setContentView(R.layout.tab_layout);
		setupTabs(savedInstanceState);
		apiHandler = new RoviApiHandler();
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		btButton = menu.getItem(0);
		setBTStatus(CM.getBTService().getState());
		return true;
	}
	
	public ConnectionManager getCM() {
		return CM;
	
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if (!CM.getAdapter().isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, ConnectionManager.REQUEST_ENABLE_BT);
		}
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
        if (listenerRegistered)
        {
	        listenerRegistered = false;
	        unregisterReceiver(CL);
        }
    }

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.btmanager:
	        	connectToBT();
	            return true;
	        case R.id.setup:
	        	keepService = true;
	        	Intent setupIntent = new Intent(this, SetupActivity.class);
	        	setupIntent.putExtra("force", true);
	        	startActivity(setupIntent);
	        	finish();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
    private void connectToBT()
	{
        Intent listDevicesIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(listDevicesIntent, ConnectionManager.REQUEST_CONNECT_DEVICE);
	}

	
	private void setBTStatus(int status)
	{
    	switch(status)
    	{
    	case BluetoothService.STATE_CONNECTED:
        	btButton.setIcon(R.drawable.greencheck);
    		break;
    	case BluetoothService.STATE_CONNECTING:
    		btButton.setIcon(android.R.drawable.ic_menu_rotate);
    		break;
    	case BluetoothService.STATE_NONE:
    		btButton.setIcon(R.drawable.redx);
    		break;
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
        }
    }

	
	
	public class NavTabListener<T extends Fragment> implements TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		/**
		 * Constructor used each time a new tab is created.
		 * 
		 * @param activity
		 *            The host Activity, used to instantiate the fragment
		 * @param tag
		 *            The identifier tag for the fragment
		 * @param clz
		 *            The fragment's Class, used to instantiate the fragment
		 */
		public NavTabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		/* The following are each of the ActionBar.TabListener callbacks */

		
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Fragment preInitializedFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
			if(preInitializedFragment != null && preInitializedFragment.getClass() == mClass) {
				ft.attach(preInitializedFragment);
			} else {
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
	            ft.add(R.id.realtabcontent, mFragment, mTag);
	        }	
			
	      
		}
		
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			Fragment preInitializedFragment = (Fragment) mActivity
					.getFragmentManager().findFragmentByTag(mTag);

	        if (preInitializedFragment != null) {
	            ft.detach(preInitializedFragment);
	        } else if (mFragment != null) {
	            ft.detach(mFragment);
	        }

		}


		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// User selected the already selected tab. Usually do nothing.
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		// save the selected tab
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
		super.onSaveInstanceState(outState);
	}
	
	private void setupTabs(Bundle savedInstanceState) {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab tab = actionBar.newTab().setText(REMOTE_TAB.toUpperCase()).setTabListener(new NavTabListener<RemoteFragment>(this, REMOTE_TAB, RemoteFragment.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab().setText(GUIDE_TAB.toUpperCase()).setTabListener(new NavTabListener<GuideFragment>(this, GUIDE_TAB, GuideFragment.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab().setText(FAVORITES_TAB.toUpperCase()).setTabListener(new NavTabListener<FavoritesFragment>(this, FAVORITES_TAB, FavoritesFragment.class));
        actionBar.addTab(tab);
        
        tab = actionBar.newTab().setText(EXPLORE_TAB.toUpperCase()).setTabListener(new NavTabListener<ExploreFragment>(this, EXPLORE_TAB, ExploreFragment.class));
        actionBar.addTab(tab);

        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }
	
    private class ConnectionListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("edu.berkeley.remoticon.statusupdate")) {
            	//intent has state, but may as well query it directly
            	setBTStatus(CM.getBTService().getState());
            }
        }
    }
    
    public void sendCode(String code)
    {
    	System.out.println(code);
    	if (CM.getBTService().getState() == BluetoothService.STATE_CONNECTED)
    	{
    		System.out.println("am i here?");
    		CM.getBTService().write(formatIRCode(code).getBytes());
    	}
    	else
    	{
    		connectToBT();
    	}
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

}
