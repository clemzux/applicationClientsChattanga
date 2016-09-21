package com.example.clemzux.applicationclientschattanga.utilitaries;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.clemzux.applicationclientschattanga.R;

import java.util.Date;

/**
 * Created by clemzux on 28/08/16.
 */
public class CUtilitaries extends AppCompatActivity{
    private static CUtilitaries ourInstance = new CUtilitaries();

    public static CUtilitaries getInstance() {
        return ourInstance;
    }

    private CUtilitaries() {
    }

    public static void test(Context pContext, String pVar) {

        Toast t = Toast.makeText(pContext, pVar, Toast.LENGTH_SHORT);
        t.show();
    }

    public static void messageLong(Context pContext, String pVar) {

        Toast t = Toast.makeText(pContext, pVar, Toast.LENGTH_LONG);
        t.show();
    }

    public static int imageRessourceSearcher(String pImageName) {

        switch (pImageName) {
            case "hotdog":
                return R.drawable.hotdog;

            case "pizza":
                return R.drawable.pizzafromage;

            case "pouletfrites":
                return R.drawable.pouletfrites;
        }

        return R.drawable.wrongimage;
    }

    public static Boolean notToLate() {

        if (CProperties.hasReserved) {

            Date date = new Date();

            if (date.getHours() >= 11) {

                if (date.getMinutes() > 30)
                    return false;
            }
        }

        return true;
    }
}
