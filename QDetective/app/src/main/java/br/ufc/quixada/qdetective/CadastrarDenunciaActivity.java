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
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.ufc.quixada.qdetective.dao.DenunciaDAO;
import br.ufc.quixada.qdetective.model.Categoria;
import br.ufc.quixada.qdetective.model.Denuncia;
import br.ufc.quixada.qdetective.view.DatePickerFragment;

public class CadastrarDenunciaActivity extends AppCompatActivity implements DatePickerFragment.NotificarEscutadorDoDialog {

    private static final int CAPTURAR_IMAGEM = 1;
    private static final int CAPTURAR_VIDEO = 3;

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
        mViewHolder.input_descricao = (EditText) findViewById(R.id.descricao);
        mViewHolder.input_usuario = (EditText) findViewById(R.id.usuario);
        mViewHolder.latitudeText = findViewById(R.id.latitude_denuncia);
        mViewHolder.longitudeText = findViewById(R.id.longitude_denuncia);
        mViewHolder.containerMidia = (LinearLayout) findViewById(R.id.container_midia);

        getLocationManager();
        mViewHolder.spinner_categoria.setAdapter(adapterCategoria);

        possuiCartaoSD = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        dispositivoSuportaCartaoSD = Environment.isExternalStorageRemovable();


//        caso seja pra editar
        if(getIntent().getBooleanExtra("flag", false)) {
            int id = getIntent().getIntExtra("id", -1);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            Denuncia denuncia = denunciaDAO.buscarDenunciaPorId(id);

            mViewHolder.button_data.setText(dateFormat.format(denuncia.getData()));

            // marcando spiner
            if(denuncia.getCategoria().toString().equals(Categoria.VIAS_PUBLICAS.toString())) {
                mViewHolder.spinner_categoria.setSelection(0,true);
            }
            if(denuncia.getCategoria().toString().equals(Categoria.EQUIPAMENTOS_COMUNICATARIOS.toString())) {
                mViewHolder.spinner_categoria.setSelection(1, true);
            }
            if(denuncia.getCategoria().toString().equals(Categoria.LIMPEZA_URBANA.toString())) {
                mViewHolder.spinner_categoria.setSelection(2, true);
            }

            mViewHolder.input_usuario.setText(denuncia.getUsuario());
            mViewHolder.input_descricao.setText(denuncia.getDescricao());

            ((TableLayout)findViewById(R.id.hack)).setVisibility(View.GONE);


            String[] path = denuncia.getUriMidia().split("/");

            File pathDaMidia = getDiretorioDeSalvamento(path[path.length-1]);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String authority = this.getApplicationContext().getPackageName() + ".fileprovider";
                uri = FileProvider.getUriForFile(this, authority, pathDaMidia);
            } else {
                uri = Uri.fromFile(pathDaMidia);
            }

            if(path[path.length-1].contains(".jpg")) {

                ImageView imageView = new ImageView(this);

                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 800
                ));

                imageView.setImageURI(uri);

                mViewHolder.containerMidia.removeAllViews();
                mViewHolder.containerMidia.addView(imageView);


            }else {
                VideoView videoView = new VideoView(this);
                videoView.setLayoutParams(new  LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 800
                ));
                videoView.setVideoPath(denuncia.getUriMidia());
                MediaController mc = new MediaController(this);
                videoView.setMediaController(mc);

                mViewHolder.containerMidia.removeAllViews();
                mViewHolder.containerMidia.addView(videoView);
            }
        }

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
            case 3: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    iniciarGravacaoDeVideo();
                } else {
                    Toast.makeText(this, "Sem permissão para uso de câmera.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == CAPTURAR_VIDEO) {
            if (resultCode == RESULT_OK) {

                VideoView videoView = new VideoView(this);
                videoView.setLayoutParams(new  LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 800
                ));

                videoView.setVideoURI(uri);
                MediaController mc = new MediaController(this);
                videoView.setMediaController(mc);

                mViewHolder.containerMidia.removeAllViews();
                mViewHolder.containerMidia.addView(videoView);
            } else {
                Toast.makeText(this, "Video não gravado!", Toast.LENGTH_LONG).show();
            }
        }


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

    private void getPermissoes(boolean flag){
        boolean camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean leitura = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean escrita = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(flag) {
            if (camera && leitura && escrita) {
                iniciarCapturaDeFotos();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, CAPTURAR_IMAGEM);
            }
        }else{
            if (camera && leitura && escrita) {
                iniciarGravacaoDeVideo();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, CAPTURAR_VIDEO);
            }
        }
    }


    private File getDiretorioDeSalvamento(String nomeArquivo) {
        File diretorio = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (possuiCartaoSD && dispositivoSuportaCartaoSD) {
            diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }

        File pathDaMidia = new File(diretorio, nomeArquivo);
        return pathDaMidia;
    }

    public void capturarVideo(View v) {
        getPermissoes(false);
    }

    private void iniciarGravacaoDeVideo() {
        try {
            setArquivo(false);
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            startActivityForResult(intent, CAPTURAR_VIDEO);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar a câmera.", Toast.LENGTH_LONG).show();
        }
    }


    public void capturarImagem(View view) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            getPermissoes(true);
        } else {
            iniciarCapturaDeFotos();
        }
    }

    private void iniciarCapturaDeFotos() {
        try {
            setArquivo(true);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, CAPTURAR_IMAGEM);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar a câmera.", Toast.LENGTH_LONG).show();
        }
    }

    private void setArquivo(boolean flag) {
        String nomeArquivo = "";
        if(flag) {
            nomeArquivo = System.currentTimeMillis() + ".jpg";
        }
        else {
            nomeArquivo = System.currentTimeMillis() + ".mp4";
        }
        File pathDaMidia = getDiretorioDeSalvamento(nomeArquivo);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = this.getApplicationContext().getPackageName() + ".fileprovider";
            uri = FileProvider.getUriForFile(this, authority, pathDaMidia);
        } else {
            uri = Uri.fromFile(pathDaMidia);
        }
    }



    public void cadastrarDenuncia(View view) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            Denuncia denuncia = new Denuncia();

            if (getIntent().getBooleanExtra("flag", false)) {
                denuncia = denunciaDAO.buscarDenunciaPorId(getIntent().getIntExtra("id", -1));
            }


            switch ((int) mViewHolder.spinner_categoria.getSelectedItemId()) {
                case 0:
                    denuncia.setCategoria(Categoria.VIAS_PUBLICAS);
                    break;
                case 1:
                    denuncia.setCategoria(Categoria.EQUIPAMENTOS_COMUNICATARIOS);
                    break;
                case 2:
                    denuncia.setCategoria(Categoria.LIMPEZA_URBANA);
                    break;
            }

            try {
                denuncia.setData(dateFormat.parse(mViewHolder.button_data.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            denuncia.setDescricao(mViewHolder.input_descricao.getText().toString());
            denuncia.setUriMidia(uri.getPath());
            denuncia.setUsuario(mViewHolder.input_usuario.getText().toString());

            if (getIntent().getBooleanExtra("flag", false) == false) {
                denuncia.setLatitude(latitude);
                denuncia.setLongitude(longitude);
            }

            if (getIntent().getBooleanExtra("flag", false)) {
                this.denunciaDAO.atualizarDenuncia(denuncia);
            } else {
                this.denunciaDAO.inserirDenuncia(denuncia);
            }
            
            if (getIntent().getBooleanExtra("flag", false)) {
                Toast toast = Toast.makeText(this, "Denuncia atualizada com sucesso!", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(this, "Denuncia cadastrada com sucesso!", Toast.LENGTH_SHORT);
                toast.show();
            }

            uri = null;

            finish();
        } catch (Exception e) {
            Toast toast = Toast.makeText(this, "Por favor preencha todos os campos corretamente!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private static class ViewHolder {
        private EditText input_usuario;
        private EditText input_descricao;
        private Button button_data;
        private Spinner spinner_categoria;
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
