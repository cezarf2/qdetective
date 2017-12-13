package br.ufc.quixada.qdetective.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by cezar on 13/12/2017.
 */

public class DetalhesDialogFragment extends DialogFragment{

    private DialogListener dialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final CharSequence[] items = {"Detalhes"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Opções").setItems(items, itemClick);
        dialogListener = (DialogListener) getActivity();

        return builder.create();
    }

    public interface DialogListener{
        public void onDialogDetalhesClick(int id);
    }

    DialogInterface.OnClickListener itemClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            int id = DetalhesDialogFragment.this.getArguments().getInt("id");

            if(which == 0){
                dialogListener.onDialogDetalhesClick(id);
            }
        }
    };
}
