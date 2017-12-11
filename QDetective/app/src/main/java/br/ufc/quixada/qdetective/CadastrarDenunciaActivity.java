package br.ufc.quixada.qdetective;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;

import br.ufc.quixada.qdetective.model.Denuncia;
import br.ufc.quixada.qdetective.view.DatePickerFragment;

public class CadastrarDenunciaActivity extends AppCompatActivity implements DatePickerFragment.NotificarEscutadorDoDialog{

    private static final int CAPTURAR_IMAGEM = 1;
    private Uri uri;
    private boolean possuiCartaoSD = false;
    private boolean dispositivoSuportaCartaoSD = false;

    private Button dataButton;
    private Spinner categoria;
    private ImageView imagem;
    private EditText usuarioEditText;
    private EditText descricaoEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_denuncia);

        dataButton = (Button) findViewById(R.id.btn_data_denuncia);

        ArrayAdapter<CharSequence> adapterCategoria = ArrayAdapter.createFromResource(this, R.array.categoria_denuncia,
                android.R.layout.simple_spinner_item);
        categoria = (Spinner) findViewById(R.id.categoria);
        categoria.setAdapter(adapterCategoria);
        imagem = (ImageView) findViewById(R.id.imagem);
        imagem.setVisibility(View.INVISIBLE);

        possuiCartaoSD = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        dispositivoSuportaCartaoSD = Environment.isExternalStorageRemovable();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
    }

    @Override
    public void onDateSelectedClick(DialogFragment dialog, int ano, int mes, int dia) {
        String data = dia + "/" + mes + "/" + ano;
        this.dataButton.setText(data);
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
                    imagem.setImageBitmap(resizedBitmap);
                    imagem.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    Toast.makeText(this, "Imagem não encontrada!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Imagem não capturada!", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void cadastrarDenuncia(View view) {

        Denuncia denuncia = new Denuncia();

    }
}
