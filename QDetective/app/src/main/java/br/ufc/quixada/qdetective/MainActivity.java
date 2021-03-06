package br.ufc.quixada.qdetective;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import br.ufc.quixada.qdetective.adapters.CustomDenunciaAdapter;
import br.ufc.quixada.qdetective.dao.DenunciaDAO;
import br.ufc.quixada.qdetective.fragments.ConfirmaDialogFragment;
import br.ufc.quixada.qdetective.fragments.OptionsDialogFragment;
import br.ufc.quixada.qdetective.model.Denuncia;
import br.ufc.quixada.qdetective.utils.DatabaseHelper;
import br.ufc.quixada.qdetective.view.DatePickerFragment;

public class MainActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener, OptionsDialogFragment.DialogListener, ConfirmaDialogFragment.DialogConfirmListener{

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

        mViewHolder.listViewDenuncias.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        OptionsDialogFragment fragment = new OptionsDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("id", ((Denuncia)customDenunciaAdapter.getItem(i)).getId());
        fragment.setArguments(bundle);

        fragment.show(this.getFragmentManager(), "Opções");
    }

    @Override
    public void onDialogEditarClik(int id) {
        Intent intent = new Intent(this, CadastrarDenunciaActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("flag", true);
        startActivity(intent);
    }

    @Override
    public void onDialogDetalhesClick(int id) {
        Intent intent = new Intent(this, DetalhesActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onDialogRemoverClick(int id) {

        DialogFragment fragment = new ConfirmaDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        fragment.setArguments(bundle);

        fragment.show(this.getFragmentManager(), "confirma");

    }

    @Override
    public void onDialogSimClick(DialogFragment dialog, int id) {
        if(denunciaDAO.removerDenuncia(id)) {
            Toast.makeText(this, "Denúncia removida com sucesso!", Toast.LENGTH_LONG).show();
            this.onResume();
        }
    }

    @Override
    public void onDialogCancelarClick(DialogFragment dialog, int id) {
        Toast.makeText(this, "Operação cancelada!", Toast.LENGTH_LONG).show();
    }

    public void listarDenunciasCompartilhadas(View view) {
        Intent intent = new Intent(this, DenunciasCompartilhadasActivity.class);
        startActivity(intent);
    }

    private static class ViewHolder {
       private ListView listViewDenuncias;
    }

}
