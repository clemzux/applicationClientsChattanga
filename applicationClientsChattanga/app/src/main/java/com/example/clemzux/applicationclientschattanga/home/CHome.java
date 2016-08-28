package com.example.clemzux.applicationclientschattanga.home;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clemzux.applicationclientschattanga.R;
import com.example.clemzux.applicationclientschattanga.reservation.CReservation;
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


    //////// methods ////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        // mon code

        // initialisation de la date
        initCurrentDate();

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

    private void initCurrentDate() {

        String day, month, year;
        int hour;
        Calendar calendar = Calendar.getInstance();

        hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour > CProperties.HOUR_RESET_DAYDISH)
            day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH) + 1);
        else
            day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        year = String.valueOf(calendar.get(Calendar.YEAR));

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

            // on l'affiche dans la textView concernée
            dayDishTextView.setText(currentDayDish.getDayDish());
            // on change l'appercu du plat du jour
            dayDishImageView.setImageResource(CUtilitaries.imageRessourceSearcher(currentDayDish.getImageIdentifier()));
        }
        catch (Exception e) {}
    }

    private void initListeners() {

        initReservationButtonListener();
        initWebSiteTextViewListener();
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
                Intent reservationIntent = new Intent(getApplicationContext(), CReservation.class);
                startActivity(reservationIntent);
            }
        });
    }

    private void initWidgets() {

        dayDishTextView = (TextView) findViewById(R.id.dayDish_textView);
        webSiteTextView = (TextView) findViewById(R.id.webSite_textView_home);
        dateTextView = (TextView) findViewById(R.id.date_textView_home);

        dayDishImageView = (ImageView) findViewById(R.id.dayDish_imageView);

        reservationButton = (Button) findViewById(R.id.reservation_Button);
    }
}
