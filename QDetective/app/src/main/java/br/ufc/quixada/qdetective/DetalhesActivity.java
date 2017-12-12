package br.ufc.quixada.qdetective;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import br.ufc.quixada.qdetective.dao.DenunciaDAO;
import br.ufc.quixada.qdetective.model.Denuncia;

public class DetalhesActivity extends AppCompatActivity {

    private ViewHolder mViewHolder;
    private DenunciaDAO denunciaDAO;
    private Denuncia denuncia;
    private boolean possuiCartaoSD = false;
    private boolean dispositivoSuportaCartaoSD = false;

    private LocationManager locationManager;
    private String urlBase = "http://maps.googleapis.com/maps/api/staticmap" +
            "?size=400x400&sensor=true&markers=color:red|%s,%s";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        possuiCartaoSD = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        dispositivoSuportaCartaoSD = Environment.isExternalStorageRemovable();

        int pos = getIntent().getIntExtra("pos", 0);

        mViewHolder = new ViewHolder();
        denunciaDAO = new DenunciaDAO(this);

        mViewHolder.textDetalheDenuncia = (TextView) findViewById(R.id.detalhe_denuncia);
        mViewHolder.textDetalheDataDenuncia = (TextView) findViewById(R.id.detalhe_data_denuncia);
        mViewHolder.textDetalheDescricaoDenuncia = (TextView) findViewById(R.id.detalhe_descricao_denuncia);
        mViewHolder.textDetalheUsuarioDenuncia = (TextView) findViewById(R.id.detalhe_usuario_denuncia);
        mViewHolder.detalheContainerMidia = (LinearLayout) findViewById(R.id.detalhe_container_midia);
        mViewHolder.webView = (WebView) findViewById(R.id.mapa);

        denuncia = denunciaDAO.buscarDenunciaPorId(pos);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


        mViewHolder.textDetalheDenuncia.setText(denuncia.getCategoria());
        mViewHolder.textDetalheDataDenuncia.setText(dateFormat.format(denuncia.getData()));
        mViewHolder.textDetalheDescricaoDenuncia.setText(denuncia.getDescricao());
        mViewHolder.textDetalheUsuarioDenuncia.setText(denuncia.getUsuario());

        String[] path = denuncia.getUriMidia().split("/");

        File pathDaImagem = getDiretorioDeSalvamento(path[path.length-1]);

        Uri uri = null;

        Bitmap bitmap = null;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = this.getApplicationContext().getPackageName() + ".fileprovider";
            uri = FileProvider.getUriForFile(this, authority, pathDaImagem);
        } else {
            uri = Uri.fromFile(pathDaImagem);
        }

        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            int bmpWidth = bitmap.getWidth();
            int bmpHeight = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);

            ImageView imageView = new ImageView(this);

            imageView.setLayoutParams(new  LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 800
            ));

            imageView.setImageBitmap(resizedBitmap);

            mViewHolder.detalheContainerMidia.removeAllViews();
            mViewHolder.detalheContainerMidia.addView(imageView);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        setSettingsWebView(mViewHolder.webView);
        getLocationManager();

    }

    private File getDiretorioDeSalvamento(String nomeArquivo) {
        File diretorio = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (possuiCartaoSD && dispositivoSuportaCartaoSD) {
            diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }

        File pathDaImagem = new File(diretorio, nomeArquivo);
        return pathDaImagem;
    }

    private static class ViewHolder {
        TextView textDetalheDenuncia;
        TextView textDetalheDataDenuncia;
        TextView textDetalheDescricaoDenuncia;
        TextView textDetalheUsuarioDenuncia;
        LinearLayout detalheContainerMidia;
        WebView webView;
    }

    private void setSettingsWebView(WebView webView){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:{
                if ( grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    getLocationManager();
                } else {
                    Toast.makeText(this, "Sem permissão para uso de gps ou rede", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void getLocationManager(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET},
                    1);
            return;
        }

        String url = String.format(urlBase, denuncia.getLatitude(), denuncia.getLongitude());
        mViewHolder.webView.loadUrl(url);
    }

}
