package com.anioutkazharkova.popmechreeder.fragments;

import com.anioutkazharkova.popmechreeder.R;
import com.anioutkazharkova.popmechreeder.R.layout;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class LoadDialogFragment extends DialogFragment {

	Dialog dialog;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new Builder(this.getActivity());
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.load_dialog, null);

		builder.setView(view);
		builder.setCancelable(false);
		dialog=builder.create();
		return dialog;
	}
public void Finish()
{
	dismiss();
}
	
}
