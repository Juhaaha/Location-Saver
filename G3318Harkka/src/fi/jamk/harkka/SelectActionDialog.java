package fi.jamk.harkka;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class SelectActionDialog extends DialogFragment 

{
	public interface ActionDialogListener
	{
		public void onActionDialogPositiveClick(int actPos);
    	public void onActionDialogNegativeClick();
	}
	
	ActionDialogListener dlistener;
	String selected;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			dlistener =(ActionDialogListener)activity;
			
		}
		catch(ClassCastException ex)
		{
			throw new ClassCastException(activity.toString());
			
		}
	}
	
	
	 @Override
	 public Dialog onCreateDialog(Bundle savedInstance)
	 {
		 
		 	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());		
		 	LayoutInflater inflater = getActivity().getLayoutInflater();
		 	final View dialogView = inflater.inflate(R.layout.dialog_action, null);
		    builder.setView(dialogView);
		    builder.setSingleChoiceItems(getResources().getStringArray(R.array.string_array_Actions), -1, null);
		    builder.setTitle("Choose option");
		    builder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialoginterface, int which) 
				{
					AlertDialog ad =(AlertDialog)dialoginterface;
					int pos =ad.getListView().getCheckedItemPosition();
					dlistener.onActionDialogPositiveClick(pos);
				}	    	
		    });
		    
		    builder.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dia, int which)
				{
					dlistener.onActionDialogNegativeClick();
				}
			});
			
			return builder.create();	 
	 }
}
