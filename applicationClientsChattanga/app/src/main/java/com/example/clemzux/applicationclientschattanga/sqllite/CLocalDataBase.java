package com.example.clemzux.applicationclientschattanga.sqllite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.clemzux.applicationclientschattanga.utilitaries.CProperties;

/**
 * Created by clemzux on 29/08/16.
 */
public class CLocalDataBase extends SQLiteOpenHelper {


    //////// attributes ////////


    private static final String CREATE_TABLE_INFORMATIONS = "CREATE TABLE " + CProperties.DB_NAME + "(" +
            "date TEXT PRIMARY KEY," +
            "name TEXT," +
            "hasreserved INTEGER DEFAULT 0);";

    public static final String DROP_TABLE_INFORMATIONS = "DROP TABLE IF EXISTS " + CProperties.DB_NAME + ";";


    //////// builder ////////


    public CLocalDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    //////// methods ////////


    @Override
    public void onCreate(SQLiteDatabase pDataBase) {

        pDataBase.execSQL(CREATE_TABLE_INFORMATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase pDataBase, int i, int i1) {

        pDataBase.execSQL(DROP_TABLE_INFORMATIONS);
        onCreate(pDataBase);
    }
}
