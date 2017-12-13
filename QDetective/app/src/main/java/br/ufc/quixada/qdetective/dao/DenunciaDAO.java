package br.ufc.quixada.qdetective.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufc.quixada.qdetective.model.Categoria;
import br.ufc.quixada.qdetective.model.Denuncia;
import br.ufc.quixada.qdetective.utils.DatabaseHelper;

/**
 * Created by leo on 11/12/17.
 */

public class DenunciaDAO {
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

    public DenunciaDAO(Context context) {
        this.helper = new DatabaseHelper(context);
    }

    private Denuncia criarDenuncia(Cursor cursor) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        Integer id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Denuncia._ID));
        String descricao = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Denuncia.DESCRICAO));

        Date data  = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Denuncia.DATA)));

        Double longitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.Denuncia.LONGITUDE));
        Double latitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.Denuncia.LATITUDE));
        String uriMidia = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Denuncia.URIMIDIA));
        String usuario = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Denuncia.USUARIO));
        String categoria = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Denuncia.CATEGORIA));

        Denuncia denuncia = new Denuncia();

        denuncia.setId(id);
        denuncia.setDescricao(descricao);
        denuncia.setData(data);
        denuncia.setLongitude(longitude);
        denuncia.setLatitude(latitude);
        denuncia.setUriMidia(uriMidia);
        denuncia.setUsuario(usuario);

        switch (categoria) {
            case "VIAS_PUBLICAS":
                denuncia.setCategoria(Categoria.VIAS_PUBLICAS);
                break;
            case "EQUIPAMENTOS_COMUNICATARIOS":
                denuncia.setCategoria(Categoria.EQUIPAMENTOS_COMUNICATARIOS);
                break;
            case "LIMPEZA_URBANA":
                denuncia.setCategoria(Categoria.LIMPEZA_URBANA);
                break;
        }

//        System.out.println(categoria);

//        denuncia.setCategoria(Categoria.LIMPEZA_URBANA);

        return denuncia;
    }

    List<Map<String, Object>> listarDenuncias() {
        db = helper.getReadableDatabase();
        cursor = db.query(DatabaseHelper.Denuncia.TABELA, DatabaseHelper.Denuncia.COLUNAS, null, null, null, null, null);

        List<Map<String, Object>> denuncias = new ArrayList<>();

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        while (cursor.moveToNext()) {
            Integer id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Denuncia._ID));
            String descricao = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Denuncia.DESCRICAO));

            Date data  = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Denuncia.DATA)));

            Double longitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.Denuncia.LONGITUDE));
            Double latitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.Denuncia.LATITUDE));
            String uriMidia = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Denuncia.URIMIDIA));
            String usuario = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Denuncia.USUARIO));
            String categoria = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Denuncia.CATEGORIA));

            Map<String, Object> denuncia = new HashMap<>();

            denuncia.put(DatabaseHelper.Denuncia._ID, id);
            denuncia.put(DatabaseHelper.Denuncia.DESCRICAO, descricao);
            denuncia.put(DatabaseHelper.Denuncia.DATA, data);
            denuncia.put(DatabaseHelper.Denuncia.LONGITUDE, longitude);
            denuncia.put(DatabaseHelper.Denuncia.LATITUDE, latitude);
            denuncia.put(DatabaseHelper.Denuncia.URIMIDIA, uriMidia);
            denuncia.put(DatabaseHelper.Denuncia.USUARIO, usuario);

            switch (categoria) {
                case "VIAS_PUBLICAS":
                    denuncia.put(DatabaseHelper.Denuncia.CATEGORIA, Categoria.VIAS_PUBLICAS);
                    break;
                case "EQUIPAMENTOS_COMUNICATARIOS":
                    denuncia.put(DatabaseHelper.Denuncia.CATEGORIA, Categoria.EQUIPAMENTOS_COMUNICATARIOS);
                    break;
                case "LIMPEZA_URBANA":
                    denuncia.put(DatabaseHelper.Denuncia.CATEGORIA, Categoria.LIMPEZA_URBANA);
                    break;
            }
            denuncia.put(DatabaseHelper.Denuncia.CATEGORIA, categoria);
            denuncias.add(denuncia);
        }
        cursor.close();
        return denuncias;
    }

    public List<Denuncia> denuncias() {
        db = helper.getReadableDatabase();
        cursor = db.query(DatabaseHelper.Denuncia.TABELA, DatabaseHelper.Denuncia.COLUNAS, null, null, null, null, null);

        List<Denuncia> denuncias = new ArrayList<>();

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        while (cursor.moveToNext()) {
            Integer id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Denuncia._ID));
            String descricao = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Denuncia.DESCRICAO));

            Date data  = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Denuncia.DATA)));

            Double longitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.Denuncia.LONGITUDE));
            Double latitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.Denuncia.LATITUDE));
            String uriMidia = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Denuncia.URIMIDIA));
            String usuario = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Denuncia.USUARIO));
//            Categoria categoria = Categoria.EQUIPAMENTOS_COMUNICATARIOS;

            String categoria = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Denuncia.CATEGORIA));

//            Map<String, Object> denuncia = new HashMap<>();

            Denuncia denuncia = new Denuncia();


            denuncia.setId(id);
            denuncia.setDescricao( descricao);
            denuncia.setData(data);
            denuncia.setLongitude(longitude);
            denuncia.setLatitude(latitude);
            denuncia.setUriMidia(uriMidia);
            denuncia.setUsuario(usuario);

            switch (categoria) {
                case "VIAS_PUBLICAS":
                    denuncia.setCategoria(Categoria.VIAS_PUBLICAS);
                    break;
                case "EQUIPAMENTOS_COMUNICATARIOS":
                    denuncia.setCategoria(Categoria.EQUIPAMENTOS_COMUNICATARIOS);
                    break;
                case "LIMPEZA_URBANA":
                    denuncia.setCategoria(Categoria.LIMPEZA_URBANA);
                    break;
            }

//            denuncia.setCategoria(categoria);
            denuncias.add(denuncia);
        }
        cursor.close();
        return denuncias;
    }

    public long inserirDenuncia(Denuncia denuncia) {
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.Denuncia.DESCRICAO, denuncia.getDescricao());
        values.put(DatabaseHelper.Denuncia.DATA, denuncia.getData().getTime());
        values.put(DatabaseHelper.Denuncia.LONGITUDE, denuncia.getLongitude());
        values.put(DatabaseHelper.Denuncia.LATITUDE, denuncia.getLatitude());
        values.put(DatabaseHelper.Denuncia.URIMIDIA, denuncia.getUriMidia());
        values.put(DatabaseHelper.Denuncia.USUARIO, denuncia.getUsuario());
        values.put(DatabaseHelper.Denuncia.CATEGORIA, denuncia.getCategoria().toString());

        db = helper.getWritableDatabase();
        long qtdInseridos = db.insert(DatabaseHelper.Denuncia.TABELA, null, values);

        return qtdInseridos;
    }

    public boolean removerDenuncia(Integer id) {
        db = helper.getWritableDatabase();
        String where[] = new String[]{id.toString()};
        int removidos = db.delete(DatabaseHelper.Denuncia.TABELA, "_id = ?", where);
        return removidos > 0;
    }

    public Denuncia buscarDenunciaPorId(Integer id) {
        db = helper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM denuncia WHERE _id = " + id.toString(), null );
        cursor.moveToFirst();
        Denuncia denuncia = criarDenuncia(cursor);
        cursor.close();
        return denuncia;
    }

    public long atualizarDenuncia(Denuncia denuncia) {
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.Denuncia.DESCRICAO, denuncia.getDescricao());
        values.put(DatabaseHelper.Denuncia.DATA, denuncia.getData().getTime());
        values.put(DatabaseHelper.Denuncia.LONGITUDE, denuncia.getLongitude());
        values.put(DatabaseHelper.Denuncia.LATITUDE, denuncia.getLatitude());
        values.put(DatabaseHelper.Denuncia.URIMIDIA, denuncia.getUriMidia());
        values.put(DatabaseHelper.Denuncia.USUARIO, denuncia.getUsuario());
        values.put(DatabaseHelper.Denuncia.CATEGORIA, denuncia.getCategoria().toString());

        db = helper.getWritableDatabase();
        long qtdAtualizados = db.update(DatabaseHelper.Denuncia.TABELA,
                values,
                DatabaseHelper.Denuncia._ID + " = ?",
                new String[]{denuncia.getId().toString()});
        return qtdAtualizados;
    }

    public void close() {
        helper.close();
        db = null;
    }
}
