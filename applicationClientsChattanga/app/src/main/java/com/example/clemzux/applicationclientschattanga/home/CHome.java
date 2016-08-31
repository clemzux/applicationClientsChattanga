package com.example.clemzux.applicationclientschattanga.home;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import com.example.clemzux.applicationclientschattanga.utilitaries.CJsonDecoder;
import com.example.clemzux.applicationclientschattanga.utilitaries.CProperties;
import com.example.clemzux.applicationclientschattanga.utilitaries.CRestRequest;
import com.example.clemzux.applicationclientschattanga.utilitaries.CUtilitaries;

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import chattanga.classes.CDate;

public class CHome extends AppCompatActivity {


    //////// attributes ////////


    private Button reservationButton;
    private ImageView dayDishImageView;
    private TextView dayDishTextView, webSiteTextView, dateTextView;

    private String currentdate;

    private CDate currentDayDish = null;

    private Boolean loaded = false;


    //////// methods ////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        // mon code

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
                CUtilitaries.messageLong(getApplicationContext(),
                        "Le système a détecté que vous avez déja réservé, vous pouvez modifier votre réservation");
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

        if (hour > CProperties.HOUR_RESET_DAYDISH) {

            day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH) + 1);

            if (day.equals("32")) {

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

        dateTextView.setText("Aujourd'hui (" + currentdate + ")on vous propose en plat du jour");

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
                String url = "http://www.la-chattanga.fr";
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

                if (loaded) {

                    Intent reservationIntent = new Intent(getApplicationContext(), CReservation.class);
                    startActivity(reservationIntent);
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

    private void initWidgets() {

        dayDishTextView = (TextView) findViewById(R.id.dayDish_textView);
        webSiteTextView = (TextView) findViewById(R.id.webSite_textView_home);
        dateTextView = (TextView) findViewById(R.id.date_textView_home);

        dayDishImageView = (ImageView) findViewById(R.id.dayDish_imageView);

        reservationButton = (Button) findViewById(R.id.reservation_Button);

        // si le client a deja réservé pour aujourd'hui, l'intitulé du bouton change
        if (CProperties.hasReserved)
            reservationButton.setText("Modifier réservation");
    }
}
