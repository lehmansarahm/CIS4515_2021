package edu.temple.convoy.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import edu.temple.convoy.R;

public class JoinConvoyDialogFragment extends DialogFragment {

    public interface JoinConvoyDialogListener {
        void onDialogPositiveClick(String convoyID);
        void onDialogNegativeClick();
    }

    JoinConvoyDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (JoinConvoyDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Parent activity must implement JoinConvoyDialogListener!");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_join_convoy, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton(R.string.button_submit, (dialog, id) -> {
                    EditText convoyIdInput = view.findViewById(R.id.dialog_convoy_id);
                    listener.onDialogPositiveClick(String.valueOf(convoyIdInput.getText()));
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.button_cancel, (dialog, id) -> {
                    listener.onDialogNegativeClick();
                    dialog.cancel();
                });
        return builder.create();
    }

}