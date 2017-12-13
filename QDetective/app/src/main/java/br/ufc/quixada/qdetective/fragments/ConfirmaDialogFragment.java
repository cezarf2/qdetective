package br.ufc.quixada.qdetective.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by leo on 12/12/17.
 */

public class ConfirmaDialogFragment extends DialogFragment {

    private DialogConfirmListener listener;

    public interface DialogConfirmListener {
        public void onDialogSimClick(DialogFragment dialog, int id);
        public void onDialogCancelarClick(DialogFragment dialog, int id);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Deseja realmente excluir essa denúncia?");
        builder.setPositiveButton("Sim", confirmaSim);
        builder.setNegativeButton("Cancelar", confirmaCancelar);
        listener = (DialogConfirmListener) getActivity();

        return builder.create();
    }

    DialogInterface.OnClickListener confirmaSim = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            int id = ConfirmaDialogFragment.this.getArguments().getInt("id");
            listener.onDialogSimClick(ConfirmaDialogFragment.this, id);
        }
    };

    DialogInterface.OnClickListener confirmaCancelar = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            int id = ConfirmaDialogFragment.this.getArguments().getInt("id");
            listener.onDialogCancelarClick(ConfirmaDialogFragment.this, id);
        }
    };

}
