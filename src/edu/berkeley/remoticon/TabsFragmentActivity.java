package edu.berkeley.remoticon;

import java.util.HashMap;

import edu.berkeley.remoticon.ExploreFragment;
import edu.berkeley.remoticon.FavoritesFragment;
import edu.berkeley.remoticon.GuideFragment;
import edu.berkeley.remoticon.RemoteFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

public class TabsFragmentActivity extends FragmentActivity implements
		TabHost.OnTabChangeListener {

	private TabHost mTabHost;
	private HashMap mapTabInfo = new HashMap();
	private TabInfo mLastTab = null;
	private final String REMOTE_TAB = "remote";
	private final String GUIDE_TAB = "guide";
	private final String FAVORITES_TAB = "favorites";
	private final String EXPLORE_TAB = "explore";

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

	class TabFactory implements TabContentFactory {

		private final Context mContext;

		/**
		 * @param context
		 */
		public TabFactory(Context context) {
			mContext = context;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
		 */
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Step 1: Inflate layout
		setContentView(R.layout.tab_layout);
		// Step 2: Setup TabHost
		initializeTabHost(savedInstanceState);
		if (savedInstanceState != null) {
			// set the tab as per the saved state
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); 
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		// save the selected tab
		outState.putString("tab", mTabHost.getCurrentTabTag()); 
		super.onSaveInstanceState(outState);
	}

	/**
	 * Step 2: Setup TabHost
	 */
	private void initializeTabHost(Bundle args) {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost
				.newTabSpec(REMOTE_TAB).setIndicator(getString(R.string.remote_label)),
				(tabInfo = new TabInfo(REMOTE_TAB, RemoteFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost
				.newTabSpec(GUIDE_TAB).setIndicator(getString(R.string.guide_label)),
				(tabInfo = new TabInfo(GUIDE_TAB, GuideFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost
				.newTabSpec(FAVORITES_TAB).setIndicator(getString(R.string.favorites_label)),
				(tabInfo = new TabInfo(FAVORITES_TAB, FavoritesFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost
				.newTabSpec(EXPLORE_TAB).setIndicator(getString(R.string.explore_label)),
				(tabInfo = new TabInfo(EXPLORE_TAB, ExploreFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		// Default to first tab
		this.onTabChanged(REMOTE_TAB);
		//
		mTabHost.setOnTabChangedListener(this);
	}

	/**
	 * @param activity
	 * @param tabHost
	 * @param tabSpec
	 * @param clss
	 * @param args
	 */
	private static void addTab(TabsFragmentActivity activity, TabHost tabHost,
			TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
		String tag = tabSpec.getTag();

		// Check to see if we already have a fragment for this tab, probably
		// from a previously saved state. If so, deactivate it, because our
		// initial state is that a tab isn't shown.
		tabInfo.fragment = activity.getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
			FragmentTransaction ft = activity.getSupportFragmentManager()
					.beginTransaction();
			ft.detach(tabInfo.fragment);
			ft.commit();
			activity.getSupportFragmentManager().executePendingTransactions();
		}

		tabHost.addTab(tabSpec);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	public void onTabChanged(String tag) {
		TabInfo newTab = (TabInfo) this.mapTabInfo.get(tag);
		if (mLastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager()
					.beginTransaction();
			if (mLastTab != null) {
				if (mLastTab.fragment != null) {
					ft.detach(mLastTab.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this,
							newTab.clss.getName(), newTab.args);
					ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
				} else {
					ft.attach(newTab.fragment);
				}
			}

			mLastTab = newTab;
			ft.commit();
			this.getSupportFragmentManager().executePendingTransactions();
		}
	}

}
