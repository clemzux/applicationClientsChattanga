package com.example.clemzux.applicationclientschattanga.home;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clemzux.applicationclientschattanga.R;
import com.example.clemzux.applicationclientschattanga.reservation.CReservation;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import chattanga.classes.CDate;

public class CHome extends AppCompatActivity {


    //////// attributes ////////


    private Button reservationButton;
    private ImageView dayDishImageView;
    private TextView dayDishTextView, webSiteTextView, dateTextView;

    private String currentCate;

    private CDate currentDayDish;


    //////// methods ////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        // mon code

        // initialisation de la date
        //initDate();

        // initialisation widgets
        initWidgets();

        // initialisation listeners
        initListeners();

        // chargement des informations de plat du jour en fonction de la journée et de l'heure
        dayDishLoader();
    }

    private void dayDishLoader() throws ExecutionException, InterruptedException, IOException, JSONException {

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
