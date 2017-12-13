package br.ufc.quixada.qdetective;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Map;

import br.ufc.quixada.qdetective.adapters.CustomDenunciaAdapter;
import br.ufc.quixada.qdetective.fragments.DetalhesDialogFragment;
import br.ufc.quixada.qdetective.model.Denuncia;
import br.ufc.quixada.qdetective.utils.WebServiceUtils;

public class DenunciasCompartilhadasActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener, View.OnClickListener {

    private ViewHolder viewHolder;
    private CustomDenunciaAdapter customDenunciaAdapter;
    private WebServiceUtils webServiceUtils;
    private ProgressDialog load;
    private List<Map<String, Object>> mapList;

    private boolean possuiCartaoSD = false;
    private boolean dispositivoSuportaCartaoSD = false;

    private final String url = "http://35.193.98.124/QDetective/rest/";
    private boolean permisaoInternet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncias_compartilhadas);

        webServiceUtils = new WebServiceUtils();

        viewHolder = new ViewHolder();
        viewHolder.listViewDenunciasCompartilhadas = (ListView) findViewById(R.id.list_view_denuncias_compartilhadas);

        customDenunciaAdapter = new CustomDenunciaAdapter(webServiceUtils.getDenunciasJson(url, "denuncias"), this);
        List<Denuncia> denuncias = webServiceUtils.getDenunciasJson(url, "denuncias");
        System.out.println(webServiceUtils.getRespostaServidor());
        System.out.println(denuncias.size());
        System.out.println(denuncias.get(0).toString());
        //viewHolder.listViewDenunciasCompartilhadas.setAdapter(customDenunciaAdapter);

        //viewHolder.listViewDenunciasCompartilhadas.setOnClickListener(this);

        possuiCartaoSD = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        dispositivoSuportaCartaoSD = Environment.isExternalStorageRemovable();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DetalhesDialogFragment detalhesDialogFragment = new DetalhesDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("id", ((Denuncia) customDenunciaAdapter.getItem(i)).getId());
        detalhesDialogFragment.setArguments(bundle);

        detalhesDialogFragment.show(this.getFragmentManager(), "Opções");
    }

    @Override
    public void onClick(View view) {

    }

    private static class ViewHolder{
        private ListView listViewDenunciasCompartilhadas;
    }

    public void carregarDados(){
        mapList = webServiceUtils.get
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (isOnline()) {
                        permisaoInternet = true;
                        return;
                    } else {
                        permisaoInternet = false;
                        Toast.makeText(this, "Sem conexão de internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    permisaoInternet = false;
                    Toast.makeText(this, "Sem permissão para uso de internet", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void getPermissaoInternet(){
        String internet = Manifest.permission.INTERNET;
        String redeStatus = Manifest.permission.ACCESS_NETWORK_STATE;
        int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

        boolean permissaoInternet = ContextCompat.checkSelfPermission(this, internet) == PERMISSION_GRANTED;
        boolean permissaoRedeStatus = ContextCompat.checkSelfPermission(this, redeStatus) == PERMISSION_GRANTED;

        if (permissaoInternet && permissaoRedeStatus) {
            if (isOnline()) {
                permisaoInternet = true;
                return;
            } else {
                permisaoInternet = false;
                Toast.makeText(this, "Sem conexão de internet", Toast.LENGTH_LONG).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE},
                    1);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private File getDiretorioDeSalvamento(String nomeArquivo) {
        File diretorio = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (possuiCartaoSD && dispositivoSuportaCartaoSD) {
            diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }

        File pathDaMidia = new File(diretorio, nomeArquivo);
        return pathDaMidia;
    }

    private class DownloadDenuncias extends AsyncTask<Long, Void, WebServiceUtils>{

        @Override
        protected void onPreExecute(){
            load = ProgressDialog.show(DenunciasCompartilhadasActivity.class, "Por favor aguarde",
                    "Recuperando informações do servidor");
        }

        @Override
        protected WebServiceUtils doInBackground(Long... ids) {
            WebServiceUtils webService = new WebServiceUtils();
            String id = (ids != null && ids.length == 1) ? ids[0].toString() : "";
            List<Denuncia> denuncias = webService.getListaDenunciasJson(url, "denuncias", id);
            for (Denuncia denuncia : denuncias) {
                String path = getDiretorioDeSalvamento(denuncia.getUriMidia()).getPath();
                webService.downloadImagemBase64(url + "arquivos", path, denuncia.getId());
                denuncia.setUriMidia(path);
            }
            return webService;
        }
    }
}
