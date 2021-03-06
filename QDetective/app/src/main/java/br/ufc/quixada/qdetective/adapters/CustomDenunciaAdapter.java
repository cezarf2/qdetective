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
import br.ufc.quixada.qdetective.model.Categoria;
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

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Denuncia denuncia = denuncias.get(i);

        if(denuncia.getCategoria() != null){
            switch (denuncia.getCategoria().toString()) {
                case "VIAS_PUBLICAS":
                    textCategoria.setText("Vias públicas de acesso");
                    break;
                case "EQUIPAMENTOS_COMUNICATARIOS":
                    textCategoria.setText("Equipamentos comunitários");
                    break;
                case "LIMPEZA_URBANA":
                    textCategoria.setText("Limpeza urbana e saneamento");
                    break;
            }
        }
        else{
            textCategoria.setText("Não existe categoria");
        }

//        textCategoria.setText(denuncia.getCategoria());
        textData.setText(dateFormat.format(denuncia.getData()));
        textDescricao.setText(denuncia.getDescricao());

        textUsuario.setText(denuncia.getUsuario());

        return view;
    }
}
