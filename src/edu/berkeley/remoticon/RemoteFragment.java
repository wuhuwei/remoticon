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
	Button powerBtn;
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
	
	TextView channelInput;
	Button enterBtn;

	SeekBar volumeControl;
	ImageView powerButton;
	
	MenuActivity activity;

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

		activity = (MenuActivity)getActivity();

		if (activity != null) {
			powerBtn = (Button) activity.findViewById(R.id.powerBtn);
			powerBtn.setOnClickListener(new RemoteBtnClickListener("power"));
			channelUp = (ImageView) activity.findViewById(R.id.channelUp);
			channelUp.setOnClickListener(new RemoteBtnClickListener("channelUp"));
			channelDown = (ImageView) activity.findViewById(R.id.channelDown);
			channelDown.setOnClickListener(new RemoteBtnClickListener("channelDown"));

			volumeUp = (Button) activity.findViewById(R.id.volumeUp);
			volumeUp.setOnClickListener(new RemoteBtnClickListener("volumeUp"));
			volumeDown = (Button) activity.findViewById(R.id.volumeDown);
			volumeDown.setOnClickListener(new RemoteBtnClickListener("volumeDown"));
			
			NumPadClickListener numPadListener = new NumPadClickListener();
			btnOne = (Button) activity.findViewById(R.id.Button01);
			btnOne.setOnClickListener(numPadListener);
			btnTwo = (Button) activity.findViewById(R.id.Button02);
			btnTwo.setOnClickListener(numPadListener);
			btnThree = (Button) activity.findViewById(R.id.Button03);
			btnThree.setOnClickListener(numPadListener);
			btnFour = (Button) activity.findViewById(R.id.Button04);
			btnFour.setOnClickListener(numPadListener);
			btnFive = (Button) activity.findViewById(R.id.Button05);
			btnFive.setOnClickListener(numPadListener);
			btnSix = (Button) activity.findViewById(R.id.Button06);
			btnSix.setOnClickListener(numPadListener);
			btnSeven = (Button) activity.findViewById(R.id.Button07);
			btnSeven.setOnClickListener(numPadListener);
			btnEight = (Button) activity.findViewById(R.id.Button08);
			btnEight.setOnClickListener(numPadListener);
			btnNine = (Button) activity.findViewById(R.id.Button09);
			btnNine.setOnClickListener(numPadListener);
			btnZero = (Button) activity.findViewById(R.id.Button00);
			btnZero.setOnClickListener(numPadListener);
			
			channelInput = (TextView) activity.findViewById(R.id.channelInput);
			channelInput.setText("");
			enterBtn = (Button) activity.findViewById(R.id.enterBtn);
			enterBtn.setOnClickListener(new ChannelClickListener());
			fillRemoteCodes();
		}
	}
	
	private class NumPadClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Button b = (Button) v;
			channelInput.append(b.getText());
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
			//System.out.println(signal);
			if(signal != null) {
				activity.sendCode(signal);
			}
			
		}
	}
	
	private class ChannelClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String channel = (String) channelInput.getText();
			String signal = "";
			for(int i = 0; i < channel.length(); i++) {
				signal += remoteCodes.get(channel.charAt(i));
			}
			activity.sendCode(signal);
			
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
		remoteCodes.put("1", prefs.getString("1", null));
		remoteCodes.put("2", prefs.getString("2", null));
		remoteCodes.put("3", prefs.getString("3", null));
		remoteCodes.put("4", prefs.getString("4", null));
		remoteCodes.put("5", prefs.getString("5", null));
		remoteCodes.put("6", prefs.getString("6", null));
		remoteCodes.put("7", prefs.getString("7", null));
		remoteCodes.put("8", prefs.getString("8", null));
		remoteCodes.put("9", prefs.getString("9", null));
		remoteCodes.put("0", prefs.getString("0", null));
		System.out.println(remoteCodes);
	}
	

}
