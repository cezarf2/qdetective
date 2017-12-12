package br.ufc.quixada.qdetective.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import br.ufc.quixada.qdetective.R;
import br.ufc.quixada.qdetective.model.Denuncia;

/**
 * Created by cezar on 11/12/2017.
 */

public class CustomDenunciaAdapter extends BaseAdapter{

    private List<Denuncia> denuncias;
    private LayoutInflater layoutInflater;

    public CustomDenunciaAdapter(List<Denuncia> denuncias, Context context) {
        this.denuncias = denuncias;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.denuncias.size();
    }

    @Override
    public Object getItem(int i) {
        return this.denuncias.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.denuncias.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.item_denuncia, null);

        TextView textCategoria = view.findViewById(R.id.categoria_denuncia);
        TextView textData = view.findViewById(R.id.data_denuncia);
        TextView textDescricao = view.findViewById(R.id.descricao_denuncia);
        TextView textUsuario = view.findViewById(R.id.usuario_denuncia);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Denuncia denuncia = denuncias.get(i);

        textCategoria.setText(denuncia.getCategoria());
        String teste = (denuncia.getData() != null) ? "sim" : "não";
        textData.setText(teste);
//        textData.setText("sas");
        textDescricao.setText(denuncia.getDescricao());

        textUsuario.setText(denuncia.getUsuario());

        return view;
    }
}
