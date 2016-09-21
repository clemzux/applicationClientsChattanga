package com.example.clemzux.applicationclientschattanga.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clemzux.applicationclientschattanga.R;
import com.example.clemzux.applicationclientschattanga.imagepreview.CImagePreview;
import com.example.clemzux.applicationclientschattanga.reservation.CReservation;
import com.example.clemzux.applicationclientschattanga.sqllite.CLocalDataBase;
import com.example.clemzux.applicationclientschattanga.update.CUpdate;
import com.example.clemzux.applicationclientschattanga.utilitaries.CJsonDecoder;
import com.example.clemzux.applicationclientschattanga.utilitaries.CProperties;
import com.example.clemzux.applicationclientschattanga.utilitaries.CRestRequest;
import com.example.clemzux.applicationclientschattanga.utilitaries.CUtilitaries;

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import chattanga.classes.CAppVersion;
import chattanga.classes.CDate;

public class CHome extends AppCompatActivity {


    //////// attributes ////////


    private Button reservationButton;
    private ImageView dayDishImageView;
    private TextView dayDishTextView, webSiteTextView, dateTextView, telNumber;

    private String currentdate;

    private CDate currentDayDish = null;

    private Boolean loaded = false;

    private final int PERMISSION_CALL = 1;


    //////// methods ////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        // mon code

        // on vérifie que le numéro de version de l'application est le bon
        appVersionVerification();

        // on initialise l'activité
        generalInitialisation();
    }

    private void appVersionVerification() {

        try {
            CAppVersion appVersion = new CJsonDecoder<CAppVersion>().Decoder(CRestRequest.get_appVersion(), CAppVersion.class);

            if (CProperties.APP_VERSION != appVersion.getVersionNumber()) {

                Intent updateIntent = new Intent(getApplicationContext(), CUpdate.class);
                startActivity(updateIntent);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void generalInitialisation() {

        // initialisation de la date
        initCurrentDate();

        // on vérifie que le client n'a pas deja réservé
        reservationVerification();

        // initialisation widgets
        initWidgets();

        // initialisation listeners
        initListeners();

        // chargement des informations de plat du jour en fonction de la journée et de l'heure
        try {
            dayDishLoader();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void reservationVerification() {

        SQLiteDatabase db = new CLocalDataBase(getApplicationContext(), CProperties.DB_NAME, null, 2).getWritableDatabase();

        Cursor c = null;

        try {

            c = db.rawQuery("SELECT date, name, hasreserved FROM " + CProperties.DB_NAME + " WHERE date='" + currentdate + "';", null);
            c.moveToFirst();

            if (c.getString(2).equals("1")) {

                setHasRerserved(c);

                if (CProperties.hasReserved && CUtilitaries.notToLate())
                    CUtilitaries.messageLong(getApplicationContext(),
                        "Le système a détecté que vous avez déja réservé, vous pouvez modifier votre réservation");
                else
                    CUtilitaries.messageLong(getApplicationContext(), "Vous ne pouvez pas modifier votre réservation après 11h30 " +
                            "sur l'application, veuillez contacter directement le restaurant !");
            }
        }
        catch (Exception e) {}
    }

    private void setHasRerserved(Cursor pActualReservation) {

        CProperties.idCurrentReservation = pActualReservation.getInt(0);
        CProperties.nameCurrentReservation = pActualReservation.getString(1);
        CProperties.hasReserved = true;
    }

    private void initCurrentDate() {

        String day, month, year;
        int hour;
        Calendar calendar = Calendar.getInstance();

        hour = calendar.get(Calendar.HOUR_OF_DAY);

        year = String.valueOf(calendar.get(Calendar.YEAR));
        month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        if (hour >= CProperties.HOUR_RESET_DAYDISH) {

            day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH) + 1);

            if (Integer.valueOf(day) > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {

                day = "01";
                month = String.valueOf(calendar.get(Calendar.MONTH) + 2);

                if (month.equals("13")) {

                    month = "01";
                    year = String.valueOf(calendar.get(Calendar.YEAR) + 1);
                }
            }
        }

        if (month.length() == 1)
            month = "0" + month;

        if (day.length() == 1)
            day = "0" + day;

        currentdate = day + "-" + month + "-" + year;
    }

    private void dayDishLoader() throws ExecutionException, InterruptedException, IOException, JSONException {

        dateTextView.setText("Aujourd'hui (" + currentdate + ") nous vous proposons en plat du jour");

        try {

            // on demande au server le plat du jour
            currentDayDish = new CJsonDecoder<CDate>().Decoder(CRestRequest.get_dateByDate(currentdate), CDate.class);
            CProperties.CURRENT_DAYDISH = currentDayDish;

            // on l'affiche dans la textView concernée
            dayDishTextView.setText(currentDayDish.getDayDish());
            // on change l'appercu du plat du jour
            dayDishImageView.setImageResource(CUtilitaries.imageRessourceSearcher(currentDayDish.getImageIdentifier()));

            // on indique que le plat du jour a bien été récupéré
            loaded = true;
        }
        catch (Exception e) {}
    }

    private void initListeners() {

        initReservationButtonListener();
        initWebSiteTextViewListener();
        initImageViewListener();
        initTelNumberTextViewListener();
    }

    private void initTelNumberTextViewListener() {

        telNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                call();
            }
        });
    }

    private void call() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callItent = new Intent(Intent.ACTION_CALL);
            callItent.setData(Uri.parse("tel:" + "+33763110707"));
            startActivity(callItent);
        }
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {

            case PERMISSION_CALL: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    call();

                } else {

                    // do nothing
                }
                return;
            }
        }
    }

    private void initImageViewListener() {

        dayDishImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loaded){
                    Intent imagePreviewIntent = new Intent(getApplicationContext(), CImagePreview.class);
                    startActivity(imagePreviewIntent);
                }
                else {

                    try {
                        dayDishLoader();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    CUtilitaries.test(getApplicationContext(), CProperties.DAYDISH_NOT_LOADED);
                }
            }
        });
    }

    private void initWebSiteTextViewListener() {

        webSiteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = CProperties.WEBSITE_URL;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void initReservationButtonListener() {

        reservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CUtilitaries.notToLate()) {

                    if (loaded) {

                        Intent reservationIntent = new Intent(getApplicationContext(), CReservation.class);
                        startActivity(reservationIntent);
                    } else {

                        try {
                            dayDishLoader();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        CUtilitaries.test(getApplicationContext(), CProperties.DAYDISH_NOT_LOADED);
                    }
                }
                else
                    CUtilitaries.messageLong(getApplicationContext(), "Vous ne pouvez pas modifier votre réservation après 11h30 " +
                            "sur l'application, veuillez contacter directement le restaurant !");
            }
        });
    }

    private void initWidgets() {

        dayDishTextView = (TextView) findViewById(R.id.dayDish_textView);
        webSiteTextView = (TextView) findViewById(R.id.webSite_textView_home);
        dateTextView = (TextView) findViewById(R.id.date_textView_home);
        telNumber = (TextView) findViewById(R.id.telNumber_home_activity);

        dayDishImageView = (ImageView) findViewById(R.id.dayDish_imageView);

        reservationButton = (Button) findViewById(R.id.reservation_Button);

        // si le client a deja réservé pour aujourd'hui, l'intitulé du bouton change
        if (CProperties.hasReserved)
            reservationButton.setText("Modifier réservation");
    }

    @Override
    protected void onPause() {
        super.onPause();
        generalInitialisation();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        generalInitialisation();
    }
}
