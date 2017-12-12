package br.ufc.quixada.qdetective.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by cezar on 11/12/2017.
 */

public class DatePickerFragment extends DialogFragment{

    private NotificarEscutadorDoDialog escutador;

    public interface NotificarEscutadorDoDialog{
        public void onDateSelectedClick(DialogFragment dialog, int ano, int mes, int dia);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        Activity activity = getActivity();
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, confirmaData, ano, mes, dia);
        escutador = (NotificarEscutadorDoDialog) activity;
        return datePickerDialog;
    }



    DatePickerDialog.OnDateSetListener confirmaData = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
            escutador.onDateSelectedClick(DatePickerFragment.this, ano, mes + 1, dia);
        }
    };

}
