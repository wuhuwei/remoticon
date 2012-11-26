package edu.berkeley.remoticon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class FavoritesEditDialog extends DialogFragment{
	private final String TAG = "FavoritesEditDialog";
	private String initLabel = "";
	private int initChannel = -1;
	
    public interface FavoritesEditListener {
        public void EditFavoriteSave(DialogFragment dialog, String label, int channel);
        public void EditFavoriteDelete(DialogFragment dialog);
        public void EditFavoriteCancel(DialogFragment dialog);
    }
    
    public FavoritesEditDialog()
    {
    	super();
    }
    
    public FavoritesEditDialog(String l, int c)
    {
    	super();
    	if (l != null)
    	{
    		initLabel = l;
    	}
    	initChannel = c;
    }
    
    FavoritesEditListener mListener;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (FavoritesEditListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement FavoritesEditListener");
        }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View layout = inflater.inflate(R.layout.favorites_edit_dialog, null);
	    builder.setView(layout)
	    // Add action buttons
	           .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
		           	   final EditText label = (EditText)((Dialog)dialog).findViewById(R.id.label);
		           	   final EditText channel = (EditText)((Dialog)dialog).findViewById(R.id.channel);
		           	   try
		           	   {
		           		   mListener.EditFavoriteSave(FavoritesEditDialog.this, label.getText().toString(), Integer.parseInt(channel.getText().toString()));
		           	   }
		           	   catch (NumberFormatException e)
		           	   {
		           		   Toast.makeText(getActivity(), "Invalid Channel", Toast.LENGTH_SHORT).show();
		           		   mListener.EditFavoriteCancel(FavoritesEditDialog.this);
		           	   }
	               }
	           })
	           .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
	        	   public void onClick(DialogInterface dialog, int id) {
	        		   mListener.EditFavoriteDelete(FavoritesEditDialog.this);
	        	   }
	           })
	           .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   mListener.EditFavoriteCancel(FavoritesEditDialog.this);
	               }
	           });
	    Dialog retDialog = builder.create();
	    
	    if (initLabel != null && initLabel != "")
	    {
	    	Log.e(TAG, ""+initLabel.length());
	    	((EditText)layout.findViewById(R.id.label)).setText(initLabel);
	    }
	    if (initChannel != -1)
	    {
	    	((EditText)layout.findViewById(R.id.channel)).setText("" + initChannel);
	    }
	    return retDialog;
	}
}