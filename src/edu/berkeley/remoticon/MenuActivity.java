package edu.berkeley.remoticon;

import java.util.HashMap;

import edu.berkeley.remoticon.ExploreFragment;
import edu.berkeley.remoticon.FavoritesFragment;
import edu.berkeley.remoticon.GuideFragment;
import edu.berkeley.remoticon.RemoteFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

public class MenuActivity extends FragmentActivity {

	private final String REMOTE_TAB = "remote";
	private final String GUIDE_TAB = "guide";
	private final String FAVORITES_TAB = "favorites";
	private final String EXPLORE_TAB = "explore";

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
		setContentView(R.layout.tab_layout);
		setupTabs(savedInstanceState);
		apiHandler = new RoviApiHandler();
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
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
}
