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
import android.widget.Toast;

public class LocationsDialog extends DialogFragment {
	

	public interface LocationsDialogListener
	{	
		public void onLocationsDialogPositiveClick(int position);
		public void onLocationsDialogNegativeClick();
    	
	}
	
	
	 LocationsDialogListener dlistener;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			dlistener =(LocationsDialogListener)activity;		
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
		    CharSequence[] cs = MainActivity.loclist.toArray(new CharSequence [MainActivity.loclist.size()]);
		    builder.setSingleChoiceItems(cs, -1, null);//setItems(cs, null);
		    builder.setTitle("Saved Locations in database");
		    builder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialoginterface, int which) 
				{					
					
						AlertDialog ad =(AlertDialog)dialoginterface;
						int pos =ad.getListView().getCheckedItemPosition();
						if(pos > -1)
						dlistener.onLocationsDialogPositiveClick(pos);
						else
							Toast.makeText(getActivity(), "Error occured", Toast.LENGTH_LONG).show();
				}						    	
		    });		    	
		    builder.setNegativeButton("Cancel", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dia, int which)
						{
							dlistener.onLocationsDialogNegativeClick();
						}
					});
			return builder.create(); 
	 }

}
