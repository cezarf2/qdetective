package br.ufc.quixada.qdetective;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import br.ufc.quixada.qdetective.adapters.CustomDenunciaAdapter;
import br.ufc.quixada.qdetective.dao.DenunciaDAO;
import br.ufc.quixada.qdetective.view.DatePickerFragment;

public class MainActivity extends AppCompatActivity{

    private ViewHolder mViewHolder;
    private DenunciaDAO denunciaDAO;
    private CustomDenunciaAdapter customDenunciaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewHolder = new ViewHolder();
        mViewHolder.listViewDenuncias = (ListView) findViewById(R.id.list_view_denuncia);

        denunciaDAO = new DenunciaDAO(this);

        customDenunciaAdapter = new CustomDenunciaAdapter(denunciaDAO.denuncias(), this);
        mViewHolder.listViewDenuncias.setAdapter(customDenunciaAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        customDenunciaAdapter = new CustomDenunciaAdapter(denunciaDAO.denuncias(), this);
        mViewHolder.listViewDenuncias.setAdapter(customDenunciaAdapter);
        mViewHolder.listViewDenuncias.refreshDrawableState();
    }



    public void novaDenuncia(View view) {
        Intent intent = new Intent(this, CadastrarDenunciaActivity.class);
        startActivity(intent);
    }


    private static class ViewHolder {
       private ListView listViewDenuncias;
    }

}
