package edu.berkeley.remoticon;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class RemoteFragment extends Fragment {
	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */

	ImageView channelUp;
	ImageView channelDown;

	Button volumeUp;
	Button volumeDown;

	Button btnOne;
	Button btnTwo;
	Button btnThree;
	Button btnFour;
	Button btnFive;
	Button btnSix;
	Button btnSeven;
	Button btnEight;
	Button btnNine;
	Button btnZero;

	SeekBar volumeControl;
	ImageView powerButton;
	
	Activity activity;

	HashMap<String, String> remoteCodes;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		return (LinearLayout) inflater.inflate(R.layout.remote_layout,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		activity = getActivity();

		if (activity != null) {
			channelUp = (ImageView) activity.findViewById(R.id.channelUp);
			channelUp.setOnClickListener(new RemoteBtnClickListener("channelUp"));
			channelDown = (ImageView) activity.findViewById(R.id.channelDown);
			channelDown.setOnClickListener(new RemoteBtnClickListener("channelDown"));
			
			volumeControl = (SeekBar) activity.findViewById(R.id.volumeControl);

			volumeUp = (Button) activity.findViewById(R.id.volumeUp);
			volumeUp.setOnClickListener(new RemoteBtnClickListener("volumeUp"));
			volumeDown = (Button) activity.findViewById(R.id.volumeDown);
			volumeDown.setOnClickListener(new RemoteBtnClickListener("volumeDown"));
			
			// TODO: add these signals
			btnOne = (Button) activity.findViewById(R.id.Button01);
			btnTwo = (Button) activity.findViewById(R.id.Button02);
			btnThree = (Button) activity.findViewById(R.id.Button03);
			btnFour = (Button) activity.findViewById(R.id.Button04);
			btnFive = (Button) activity.findViewById(R.id.Button05);
			btnSix = (Button) activity.findViewById(R.id.Button06);
			btnSeven = (Button) activity.findViewById(R.id.Button07);
			btnEight = (Button) activity.findViewById(R.id.Button08);
			btnNine = (Button) activity.findViewById(R.id.Button09);
			btnZero = (Button) activity.findViewById(R.id.Button00);

			fillRemoteCodes();
		}
	}
	
	private class RemoteBtnClickListener implements OnClickListener {
		String key;
		public RemoteBtnClickListener(String key) {
			this.key = key;
		}
		
		@Override
		public void onClick(View v) {
			String signal = remoteCodes.get(key);
			// TODO
			// send bluetooth signal here
			
		}
		
	}
	
	private void fillRemoteCodes() {
		remoteCodes = new HashMap<String, String>();
		SharedPreferences prefs = activity.getSharedPreferences(
				"edu.berkeley.remoticon", Context.MODE_PRIVATE);
		remoteCodes.put("power", prefs.getString("power", null));
		remoteCodes.put("channelUp", prefs.getString("channelUp", null));
		remoteCodes.put("channelDown", prefs.getString("channelDown", null));
		remoteCodes.put("volumeUp", prefs.getString("volumeUp", null));
		remoteCodes.put("volumeDown", prefs.getString("volumeDown", null));
	}
	

}
