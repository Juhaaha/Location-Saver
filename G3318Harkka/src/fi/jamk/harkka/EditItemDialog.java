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
import android.widget.Toast;

public class EditItemDialog extends DialogFragment 
{
	
	
	
	public interface editDialogListener
	{	
		public void onEditDialogPositiveClick(String descrp, String title, int numb);
    	public void onEditDialogNegativeClick();

	}
	editDialogListener dlistener;
	

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			dlistener =(editDialogListener)activity;
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
		    final EditText etNumber=(EditText)dialogView.findViewById(R.id.etNumber);
		    builder.setTitle("Edit details of selected location");
		    etTitle.setText(MainActivity.locTitle);
		    textArea.setText(MainActivity.locDesc);
		    etNumber.setText(""+MainActivity.locNum);
		    builder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialoginterface, int which) 
				{
					if(textArea.getText().toString().length() >0 && etTitle.getText().toString().length()> 0 && etNumber.getText().toString().length()>0)
					{
					AlertDialog ad =(AlertDialog)dialoginterface;
					 int nmb =Integer.parseInt(etNumber.getText().toString());
						
					dlistener.onEditDialogPositiveClick(textArea.getText().toString(), etTitle.getText().toString(), nmb);
					}
					else
						Toast.makeText(getActivity(), "Insufficient data", Toast.LENGTH_LONG).show();
				}
		    	
		    });
		    
		    builder.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dia, int which)
				{
					dlistener.onEditDialogNegativeClick();
				}
			});
			
			return builder.create();
	 }

}
