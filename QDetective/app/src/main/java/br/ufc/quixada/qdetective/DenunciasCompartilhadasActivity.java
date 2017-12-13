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
import android.widget.Toast;

import java.io.File;
import java.util.List;

import br.ufc.quixada.qdetective.adapters.CustomDenunciaAdapter;
import br.ufc.quixada.qdetective.fragments.DetalhesDialogFragment;
import br.ufc.quixada.qdetective.model.Denuncia;
import br.ufc.quixada.qdetective.utils.WebServiceUtils;

public class DenunciasCompartilhadasActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener, View.OnClickListener {

    private ViewHolder viewHolder;
    private CustomDenunciaAdapter customDenunciaAdapter;
    private ProgressDialog load;

    private boolean possuiCartaoSD = false;
    private boolean dispositivoSuportaCartaoSD = false;

    private final String url = "http://35.193.98.124/QDetective/rest";
    private boolean permisaoInternet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncias_compartilhadas);

        viewHolder = new ViewHolder();
        viewHolder.listViewDenunciasCompartilhadas = (ListView) findViewById(R.id.list_view_denuncias_compartilhadas);

        iniciarDownload();

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

    public void iniciarDownload(){
        getPermissaoInternet();
        if(permisaoInternet == true){
            DownloadDenuncias downloadDenuncias = new DownloadDenuncias();
            downloadDenuncias.execute();

        }
    }

    private class DownloadDenuncias extends AsyncTask<Void, Void, List<Denuncia>> {


        @Override
        protected void onPreExecute(){
            load = ProgressDialog.show(DenunciasCompartilhadasActivity.this, "Por favor aguarde",
                    "Recuperando informações do servidor");
        }

        @Override
        protected List<Denuncia> doInBackground(Void... voids) {
            WebServiceUtils webService = new WebServiceUtils();
            List<Denuncia> denuncias = webService.getDenunciasJson(url, "denuncias");
            return denuncias;
        }

        @Override
        protected void onPostExecute(List<Denuncia> denuncias) {
            customDenunciaAdapter = new CustomDenunciaAdapter(denuncias, DenunciasCompartilhadasActivity.this);
            viewHolder.listViewDenunciasCompartilhadas.setAdapter(customDenunciaAdapter);
            load.dismiss();
        }
    }
}
