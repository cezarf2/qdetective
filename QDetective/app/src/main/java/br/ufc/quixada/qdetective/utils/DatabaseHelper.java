package br.ufc.quixada.qdetective.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String BANCO_DADOS = "qdetective";
    private static final int VERSAO = 1;

    private static final String CREATE_TABLE_DENUNCIA = "CREATE TABLE denuncia (_id INTEGER PRIMARY KEY, descricao TEXT, data DATE, longitude DOUBLE, latitude DOUBLE, urimidia TEXT, usuario TEXT, categoria TEXT);";
    private static final String DROP_TABLE_DENUNCIA = "DROP TABLE denuncia;";


    public static class Denuncia {
        public static final String TABELA = "denuncia";


        public static final String _ID = "_id";
        public static final String DESCRICAO = "descricao";
        public static final String DATA = "data";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        public static final String URIMIDIA = "urimidia";
        public static final String USUARIO = "usuario";
        public static final String CATEGORIA = "categoria";

        public static final String[] COLUNAS =
                new String[]{_ID, DESCRICAO, DATA, LONGITUDE, LATITUDE, URIMIDIA, USUARIO, CATEGORIA};
    }

    public DatabaseHelper(Context context) {
        super(context, BANCO_DADOS, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DENUNCIA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_DENUNCIA);
        onCreate(db);
    }
}
