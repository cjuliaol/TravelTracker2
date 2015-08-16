package com.example.thewizard.cjuliaol.traveltracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by cjuliaol on 11-Aug-15.
 */
public class MemoryDialogFragment extends DialogFragment {

    private static final String TAG = "MemoryDialogFragment";
    private static final String MEMORY_TAG = "MEMORY";
    private Memory mMemory;
    private Listener mListener;
    private View mView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if( args != null) {
          mMemory = (Memory) args.getSerializable(MEMORY_TAG);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mView = getActivity().getLayoutInflater().inflate(R.layout.memory_dialog_fragment,null);
        TextView cityView = (TextView) mView.findViewById(R.id.city);
        cityView.setText(mMemory.city);
        TextView countryView = (TextView) mView.findViewById(R.id.country);
        countryView.setText(mMemory.country);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder .setView(mView)
                .setTitle(getString(R.string.memory_dialog_title))
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText notesView = (EditText) mView.findViewById(R.id.notes);
                        mMemory.notes = notesView.getText().toString();
                        mListener.OnSaveClicked(mMemory);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.OnCancelClicked(mMemory);
                    }
                });



        return builder.create();
    }

    public static MemoryDialogFragment newInstance(Memory memory) {
        MemoryDialogFragment fragment = new MemoryDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(MEMORY_TAG,memory);
        fragment.setArguments(args);
        return  fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
      try {
          mListener = (Listener) getActivity();
      } catch (ClassCastException e) {
          throw  new IllegalStateException("Acitivity does not implement contract");
      }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface Listener {
        public void OnSaveClicked(Memory memory);
        public void OnCancelClicked(Memory memory);
    }
}
