package com.anioutkazharkova.popmechreeder.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.anioutkazharkova.popmechreeder.R;

public class NetworkDialog extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setMessage(getActivity().getResources().getString(R.string.net_message));
		builder.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		return builder.create();
	}

}
