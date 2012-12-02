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

public class RetryGuideDialog extends DialogFragment{
	private final String TAG = "FavoritesEditDialog";
	private String initLabel = "";
	private int initChannel = -1;
	
    public interface RetryGuideListener {
        public void retry(DialogFragment dialog);
        public void cancel(DialogFragment dialog);
    }
    
    public RetryGuideDialog()
    {
    	super();
    }
    
    RetryGuideListener mListener;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (RetryGuideListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement RetryGuideListener");
        }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View layout = inflater.inflate(R.layout.retry_guide_dialog, null);
	    builder.setView(layout)
	    // Add action buttons
	           .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   mListener.retry(RetryGuideDialog.this);
	               }
	           })
	           .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   mListener.cancel(RetryGuideDialog.this);
	               }
	           });
	    Dialog retDialog = builder.create();
	    
	   
	    return retDialog;
	}
}