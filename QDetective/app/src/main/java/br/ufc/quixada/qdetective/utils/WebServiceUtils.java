package br.ufc.quixada.qdetective.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.leo.qdetective.model.Denuncia;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WebServiceUtils {

    private ArrayList<Denuncia> denuncias;
    private String respostaServidor;

    public WebServiceUtils() {
        this.denuncias = new ArrayList<>();
        this.respostaServidor = "";
    }

    public ArrayList<Denuncia> getDenuncias() {
        return denuncias;
    }

    public String getRespostaServidor() {
        return respostaServidor;
    }

    public List<Denuncia> getListaDenunciasJson(String url, String path, String id) {

        String uri = url + path + "/" + id;
        String json = getJSONFromAPI(uri);
        if (!json.isEmpty()) {
            try {
                GsonBuilder b = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter());
                Gson gson = b.create();
                Denuncia[] denuncia = gson.fromJson(json, Denuncia[].class);
                denuncias = new ArrayList<>(Arrays.asList(denuncia));
                respostaServidor = "Download realizado com sucesso.";
            } catch (Exception e) {
                e.printStackTrace();
                respostaServidor = "Erro ao processar JSON do servidor.";
            }
        }
        return denuncias;
    }

    private String getJSONFromAPI(String url) {
        String retorno = "";
        try {
            URL apiUrl = new URL(url);
            HttpURLConnection conexao = (HttpURLConnection) apiUrl.openConnection();
            conexao.setRequestMethod("GET");
            conexao.setReadTimeout(15000);
            conexao.setConnectTimeout(15000);
            conexao.connect();

            int codigoResposta = conexao.getResponseCode();
            InputStream is = null;
            if (codigoResposta < HttpURLConnection.HTTP_BAD_REQUEST) {
                is = conexao.getInputStream();
                retorno = converterInputStreamToString(is);
            } else {
                is = conexao.getErrorStream();
                respostaServidor = "Verifique a URL de conexão com o servidor. Erro: " + codigoResposta;
            }
            is.close();
            conexao.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            respostaServidor = "Erro ao estabelecer conexão com o servidor.";
        }
        return retorno;
    }

    private String converterInputStreamToString(InputStream is) throws IOException {
        StringBuffer buffer = new StringBuffer();
        String linha = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((linha = br.readLine()) != null) {
            buffer.append(linha);
        }
        br.close();
        return buffer.toString();
    }

    public String downloadImagemBase64(String url, String pathSalvamento, long id) {
        try {
            String imageBase64 = getJSONFromAPI(url + "/" + id);
            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap imagem = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            File file = new File(pathSalvamento);
            OutputStream fOut = new FileOutputStream(file);
            imagem.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.close();
        } catch (Exception fne) {
            fne.printStackTrace();
            respostaServidor = "Erro ao realizar o download da imagem do servidor.";
        }
        return null;
    }

    public boolean sendDenunciaJson(String url, Denuncia denuncia) {
        String retorno = null;
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
            Gson gson = builder.setPrettyPrinting().create();
            String jsonObject = gson.toJson(denuncia);

            URL apiUrl = new URL(url);
            HttpURLConnection conexao = (HttpURLConnection) apiUrl.openConnection();
            conexao.setDoOutput(true);
            conexao.setRequestMethod("POST");
            conexao.setRequestProperty("Content-Type", "application/json");
            conexao.connect();

            DataOutputStream wr = new DataOutputStream(conexao.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
            writer.write(jsonObject.toString());
            writer.close();
            wr.flush();
            wr.close();
            int codigoResposta = conexao.getResponseCode();
            InputStream is = null;
            if (codigoResposta < HttpURLConnection.HTTP_BAD_REQUEST) {
                is = conexao.getInputStream();
                respostaServidor = converterInputStreamToString(is);
            } else {
                is = conexao.getErrorStream();
                respostaServidor = "Verifique a URL de conexão com o servidor. Erro: " + codigoResposta;
            }
            is.close();
            conexao.disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            respostaServidor = "Erro ao estabelecer conexão com o servidor.";
        }
        return false;
    }

    public boolean uploadImagemBase64(String url, File foto) {
        try {
            byte[] byteArray = loadFile(foto);
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            HashMap<String, String> parametros = new HashMap<>();

            String nomeArquivo = foto.getName();
            if (foto.getName().contains("/")) {
                int beginIndex = foto.getName().lastIndexOf("/") + 1;
                nomeArquivo = foto.getName().substring(beginIndex);
            }
            parametros.put("fileName", nomeArquivo);
            parametros.put("base64", encoded);


            URL apiUrl = new URL(url);
            HttpURLConnection conexao = (HttpURLConnection) apiUrl.openConnection();
            conexao.setDoOutput(true);
            conexao.setInstanceFollowRedirects(false);
            conexao.setRequestMethod("POST");
            conexao.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conexao.setRequestProperty("charset", "UTF-8");
            conexao.setRequestProperty("Content-Length", Integer.toString(getPostDataString(parametros).length()));
            conexao.setUseCaches(false);
            conexao.connect();

            try (DataOutputStream wr = new DataOutputStream(conexao.getOutputStream())) {
                wr.write(getPostDataString(parametros).getBytes());
                wr.flush();
                wr.close();
            }
            int codigoResposta = conexao.getResponseCode();
            InputStream is = null;
            if (codigoResposta < HttpURLConnection.HTTP_BAD_REQUEST) {
                is = conexao.getInputStream();
            } else {
                is = conexao.getErrorStream();
                respostaServidor = "Resposta inválida do servidor.";
            }
            String resposta = converterInputStreamToString(is);
            if (resposta.contains("OK")) {
                respostaServidor = "Arquivo enviado com sucesso.";
            } else {
                respostaServidor = "Erro ao enviar arquivo para servidor.";
            }
            is.close();
            conexao.disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            respostaServidor = "Erro ao estabelecer conexão com o servidor.";
        }
        return false;
    }

    private byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        is.close();
        return bytes;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

        @Override
        public synchronized JsonElement serialize(Date date, Type type,
                                                  JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(date.getTime());
        }

        @Override
        public synchronized Date deserialize(JsonElement jsonElement, Type type,
                                             JsonDeserializationContext jsonDeserializationContext) {
            try {
                Date data = new Date(Long.parseLong(jsonElement.getAsString()));
                return data;
            } catch (Exception e) {
                return new Date();
            }
        }
    }
}
