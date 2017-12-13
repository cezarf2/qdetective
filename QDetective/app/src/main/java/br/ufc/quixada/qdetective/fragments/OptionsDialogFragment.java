package br.ufc.quixada.qdetective.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by leo on 12/12/17.
 */

public class OptionsDialogFragment extends DialogFragment {

    private DialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CharSequence[] items = {"Editar", "Detalhes", "Remover"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Opções").setItems(items, itemClick);
        listener = (DialogListener) getActivity();

        return builder.create();
    }

    public interface DialogListener {
        public void onDialogEditarClik(int id);
        public void onDialogDetalhesClick(int id);
        public void onDialogRemoverClick(int id);
    };

    DialogInterface.OnClickListener itemClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            int id = OptionsDialogFragment.this.getArguments().getInt("id");

            switch (which) {
                case 0:
                    listener.onDialogEditarClik(id);
                    break;
                case 1:
                    listener.onDialogDetalhesClick(id);
                    break;
                case 2:
                    listener.onDialogRemoverClick(id);
                    break;
            }
        }
    };
}
