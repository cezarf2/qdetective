package br.ufc.quixada.qdetective;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import br.ufc.quixada.qdetective.view.DatePickerFragment;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void novaDenuncia(View view) {
        Intent intent = new Intent(this, CadastrarDenunciaActivity.class);
        startActivity(intent);
    }


}
