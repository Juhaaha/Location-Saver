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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SavingLocPointDialog extends DialogFragment {
	
	

	public interface SavingDialogListener
	{	
		public void onSavingDialogPositiveClick(String descrp, String title, int nmb);
    	public void onSavingDialogNegativeClick();
	}
	
	
	 SavingDialogListener dlistener;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			dlistener =(SavingDialogListener)activity;		
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
		 	final View dialogView = inflater.inflate(R.layout.dialog_additem, null);
		    builder.setView(dialogView);
		    final EditText textArea =(EditText)dialogView.findViewById(R.id.etTextArea);
		    final EditText etTitle =(EditText)dialogView.findViewById(R.id.etTitle);
		    final EditText etNumb =(EditText)dialogView.findViewById(R.id.etNumber);
		    textArea.setText(MainActivity.strAddress);
		    builder.setTitle("Add details");
		    builder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialoginterface, int which) 
				{
					if(textArea.getText().toString().length() >0 && etTitle.getText().toString().length()> 0)
					{
						int nmb;
						if(etNumb.getText().toString().length()>0)
							nmb=Integer.parseInt((etNumb.getText().toString()));
						else
							nmb=1;
						dlistener.onSavingDialogPositiveClick(textArea.getText().toString(), etTitle.getText().toString(), nmb);
					}
					else
						Toast.makeText(getActivity(), "Insufficient data", Toast.LENGTH_LONG).show();
				}		    	
		    });
		    
		    builder.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dia, int which)
				{
					dlistener.onSavingDialogNegativeClick();
				}
			});		
			return builder.create(); 
	 }
}
