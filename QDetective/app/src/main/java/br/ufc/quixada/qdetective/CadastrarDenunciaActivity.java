package br.ufc.quixada.qdetective;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.ufc.quixada.qdetective.dao.DenunciaDAO;
import br.ufc.quixada.qdetective.model.Denuncia;
import br.ufc.quixada.qdetective.view.DatePickerFragment;

public class CadastrarDenunciaActivity extends AppCompatActivity implements DatePickerFragment.NotificarEscutadorDoDialog {

    private static final int CAPTURAR_IMAGEM = 1;
    private Uri uri;
    private Double latitude;
    private Double longitude;
    private boolean possuiCartaoSD = false;
    private boolean dispositivoSuportaCartaoSD = false;

    private DenunciaDAO denunciaDAO;
    private  ViewHolder mViewHolder;
    private LocationManager locationManager;
    private String urlBase = "http://maps.googleapis.com/maps/api/staticmap" +
            "?size=400x400&sensor=true&markers=color:red|%s,%s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_denuncia);

        ArrayAdapter<CharSequence> adapterCategoria = ArrayAdapter.createFromResource(this, R.array.categoria_denuncia,
                android.R.layout.simple_spinner_item);

        denunciaDAO = new DenunciaDAO(this);

        mViewHolder = new ViewHolder();

        mViewHolder.button_data = (Button) findViewById(R.id.btn_data_denuncia);
        mViewHolder.spinner_categoria = (Spinner) findViewById(R.id.categoria);
//        mViewHolder.imagem = (ImageView) findViewById(R.id.imagem);
        mViewHolder.input_descricao = (EditText) findViewById(R.id.descricao);
        mViewHolder.input_usuario = (EditText) findViewById(R.id.usuario);
        mViewHolder.latitudeText = findViewById(R.id.latitude_denuncia);
        mViewHolder.longitudeText = findViewById(R.id.longitude_denuncia);
        mViewHolder.containerMidia = (LinearLayout) findViewById(R.id.container_midia);

        getLocationManager();
        mViewHolder.spinner_categoria.setAdapter(adapterCategoria);
//        mViewHolder.imagem.setVisibility(View.INVISIBLE);

        possuiCartaoSD = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        dispositivoSuportaCartaoSD = Environment.isExternalStorageRemovable();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
    }

    @Override
    public void onDateSelectedClick(DialogFragment dialog, int ano, int mes, int dia) {
        final Calendar calendar = Calendar.getInstance();
        int horas = calendar.get(Calendar.HOUR_OF_DAY);
        int minutos = calendar.get(Calendar.MINUTE);

        String data = dia + "/" + mes + "/" + ano + " " + horas + ":" + minutos;
        this.mViewHolder.button_data.setText(data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if ( grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    iniciarCapturaDeFotos();
                } else {
                    Toast.makeText(this, "Sem permissão para uso de câmera", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case 2:{
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURAR_IMAGEM) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    int bmpWidth = bitmap.getWidth();
                    int bmpHeight = bitmap.getHeight();
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);

                    ImageView imageView = new ImageView(this);
                    imageView.setLayoutParams(new  LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 800
                    ));
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    imageView.setImageBitmap(resizedBitmap);

                    mViewHolder.containerMidia.removeAllViews();
                    mViewHolder.containerMidia.addView(imageView);

//                    mViewHolder.imagem.setImageBitmap(resizedBitmap);
//                    mViewHolder.imagem.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    Toast.makeText(this, "Imagem não encontrada!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Imagem não capturada!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void selecionarData(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "Selecione a data");
    }

    private void getPermissoes(){
        boolean camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean leitura = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean escrita = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(camera && leitura && escrita){
            iniciarCapturaDeFotos();
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }
    }


    public void capturarImagem(View view) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            getPermissoes();
        } else {
            iniciarCapturaDeFotos();
        }
    }

    private void iniciarCapturaDeFotos() {
        try {
            setArquivoImagem();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, CAPTURAR_IMAGEM);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar a câmera.", Toast.LENGTH_LONG).show();
        }
    }

    private void setArquivoImagem() {
        String nomeArquivo = System.currentTimeMillis() + ".jpg";
        File pathDaImagem = getDiretorioDeSalvamento(nomeArquivo);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = this.getApplicationContext().getPackageName() + ".fileprovider";
            uri = FileProvider.getUriForFile(this, authority, pathDaImagem);
        } else {
            uri = Uri.fromFile(pathDaImagem);
        }
    }

    private File getDiretorioDeSalvamento(String nomeArquivo) {
        File diretorio = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (possuiCartaoSD && dispositivoSuportaCartaoSD) {
            diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }

        File pathDaImagem = new File(diretorio, nomeArquivo);
        return pathDaImagem;
    }


    public void cadastrarDenuncia(View view) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Denuncia denuncia = new Denuncia();

        denuncia.setCategoria(mViewHolder.spinner_categoria.getSelectedItem().toString());

        try {
            denuncia.setData(dateFormat.parse(mViewHolder.button_data.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        denuncia.setDescricao(mViewHolder.input_descricao.getText().toString());
        denuncia.setUriMidia(uri.getPath());
        denuncia.setUsuario(mViewHolder.input_usuario.getText().toString());

        denuncia.setLatitude(latitude);
        denuncia.setLongitude(longitude);

        this.denunciaDAO.inserirDenuncia(denuncia);

        Toast toast = Toast.makeText(this, "Denuncia cadastrada com sucesso!", Toast.LENGTH_SHORT);
        toast.show();

        finish();
    }

    private static class ViewHolder {
        private EditText input_usuario;
        private EditText input_descricao;
        private Button button_data;
        private Spinner spinner_categoria;
        private ImageView imagem;
        private TextView latitudeText;
        private TextView longitudeText;
        private LinearLayout containerMidia;
    }

    private void setSettingsWebView(WebView webView){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(false);
    }

    private void getLocationManager(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET},
                    2);
            return;
        }

        Listener listener = new Listener();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        long tempoAtualizacao = 0;
        float distancia = 0;
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, tempoAtualizacao, distancia, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tempoAtualizacao, distancia, listener);
    }

    private class Listener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            String latitudeAtual = String.valueOf(latitude);
            String longitudeAtual = String.valueOf(longitude);

            mViewHolder.latitudeText.setText(latitudeAtual);
            mViewHolder.longitudeText.setText(longitudeAtual);

            //String url = String.format(urlBase, latitudeAtual, longitudeAtual);
            //mViewHolder.webView.loadUrl(url);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}
