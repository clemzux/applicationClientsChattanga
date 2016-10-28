package com.example.clemzux.applicationclientschattanga.reservation;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.clemzux.applicationclientschattanga.R;
import com.example.clemzux.applicationclientschattanga.home.CHome;
import com.example.clemzux.applicationclientschattanga.sqllite.CLocalDataBase;
import com.example.clemzux.applicationclientschattanga.utilitaries.CJsonDecoder;
import com.example.clemzux.applicationclientschattanga.utilitaries.CProperties;
import com.example.clemzux.applicationclientschattanga.utilitaries.CRestRequest;
import com.example.clemzux.applicationclientschattanga.utilitaries.CUtilitaries;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CReservation extends Activity {


    //////// attributes ////////


    private EditText nameEditText, telEditText, nbPeopleEditText, nbDayDishEditText, requestEditText;
    private Spinner hourArriveSpinner, minuteArriveSpinner;
    private TextView dateTextView, webSiteTextView, telNumber;
    private Button validateButton;

    private final int PERMISSION_CALL = 1;
    private Boolean callInProgress = false;


    //////// methods ////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // mon code

        if (callInProgress)
            goToHomeIntent();

        else {

            // initialisation des widgets
            initWidgets();

            // initialisation des listeners
            initListeners();

            // on vérifie si le client n'a pas déja réservé
            verifyReservation();
        }
    }

    private void verifyReservation() {

        if (CProperties.hasReserved) {

            getActualReservation();

            validateButton.setText("Modifier");
        }
    }

    private void getActualReservation() {

        chattanga.classes.CReservation reservation;

        try {

            String reservationsList = CRestRequest.get_reservationByDateAndName(CProperties.CURRENT_DAYDISH.getId(), CProperties.nameCurrentReservation);

            CJsonDecoder<chattanga.classes.CReservation> reservationCJsonDecoder = new CJsonDecoder<>();
            reservation = reservationCJsonDecoder.Decoder(reservationsList, chattanga.classes.CReservation.class);

            CProperties.CURRENT_RESERVATION = reservation;

            // on remplit les editText avec la reservation trouvée
            fillReservation();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillReservation() {

        nameEditText.setText(CProperties.CURRENT_RESERVATION.getName());
        telEditText.setText(CProperties.CURRENT_RESERVATION.getTel());
        nbPeopleEditText.setText(String.valueOf(CProperties.CURRENT_RESERVATION.getNumberPeople()));
        nbDayDishEditText.setText(String.valueOf(CProperties.CURRENT_RESERVATION.getNumberDayDish()));
        requestEditText.setText(CProperties.CURRENT_RESERVATION.getNote());

        String hour = CProperties.CURRENT_RESERVATION.getHourArrive();

        switch (hour.split("h")[0]) {

            case "11":
                hourArriveSpinner.setSelection(0);
                break;

            case "12":
                hourArriveSpinner.setSelection(1);
                break;

            case "13":
                hourArriveSpinner.setSelection(2);
                break;
        }

        minuteArriveSpinner.setSelection(Integer.valueOf(hour.split("h")[1]));
    }

    private void initListeners() {

        initRequestEditTextListener();
        initWebSiteTextViewListener();
        initValidateButtonListener();
        initNumberDayDishEditTextListener();
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

    private void initNumberDayDishEditTextListener() {

        nbDayDishEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (String.valueOf(nbDayDishEditText.getText()).equals("0"))
                    nbDayDishEditText.setText("");
            }
        });
    }

    private void initValidateButtonListener() {

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (acceptForm()) {

                    initCurrentReservation();

                    if (!CProperties.hasReserved) {

                        validateButtonConfiguration();
                    } else {

                        modificationButtonConfiguration();
                    }
                }
            }
        });
    }

    private Boolean acceptForm() {

        if (String.valueOf(nameEditText.getText()).equals("")) {

            CUtilitaries.messageLong(getApplicationContext(), "Vous devez entrer un nom pour réserver !");
            return false;
        }
        else if (String.valueOf(telEditText.getText()).length() > 10) {

            CUtilitaries.messageLong(getApplicationContext(), "Le numéro de téléphone comporte plus de 10 chiffres !");
            return false;
        }

        int nbPeople = 0, nbDayDish = 0;

        try {

            nbPeople = Integer.valueOf(String.valueOf(nbPeopleEditText.getText()));
        }catch (Exception e) {

            CUtilitaries.messageLong(getApplicationContext(), "Le nombre de personnes n'a pas de format correct !");
            return false;
        }

        try {

            nbDayDish = Integer.valueOf(String.valueOf(nbDayDishEditText.getText()));
        }catch (Exception e) {

            CUtilitaries.messageLong(getApplicationContext(), "Le nombre de plats du jour n'a pas de format correct !");
            return false;
        }

        if (nbDayDish > nbPeople) {

            CUtilitaries.messageLong(getApplicationContext(), "Le nombre de plats du jour est supérieur au nombre de réservation !");
            return false;
        }
        else
            return true;
    }

    private void modificationButtonConfiguration() {

        try {

            CProperties.CURRENT_RESERVATION.setDate(CProperties.CURRENT_DAYDISH);

            CRestRequest.post(CProperties.CURRENT_RESERVATION, "reservations");

            SQLiteDatabase db = new CLocalDataBase(getApplicationContext(), CProperties.DB_NAME, null, 2).getWritableDatabase();

            db.beginTransaction();

            ContentValues cv = new ContentValues();
            cv.put("name", CProperties.CURRENT_RESERVATION.getName());

            db.update(CProperties.DB_NAME, cv, "date=?",  new String[]{CProperties.CURRENT_DAYDISH.getDate()});

            db.setTransactionSuccessful();
            db.endTransaction();

            CUtilitaries.messageLong(getApplicationContext(), "Votre modification à bien été prise en compte !");

            goToHomeIntent();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void validateButtonConfiguration() {

        try {

            CRestRequest.put(CProperties.CURRENT_RESERVATION, "reservations");

            SQLiteDatabase db = new CLocalDataBase(getApplicationContext(), CProperties.DB_NAME, null, 2).getWritableDatabase();

            db.beginTransaction();

            db.execSQL("INSERT INTO " + CProperties.DB_NAME +
                    "(date,name,hasreserved)" +
                    "values('" + CProperties.CURRENT_RESERVATION.getDate().getDate() + "','" + CProperties.CURRENT_RESERVATION.getName() + "',1)");

            db.setTransactionSuccessful();
            db.endTransaction();

            CUtilitaries.test(getApplicationContext(), CProperties.RESERVATION_ACCEPTED);

            goToHomeIntent();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initCurrentReservation() {

        CProperties.CURRENT_RESERVATION.setDate(CProperties.CURRENT_DAYDISH);
        CProperties.CURRENT_RESERVATION.setName(String.valueOf(nameEditText.getText()));
        CProperties.CURRENT_RESERVATION.setTel(String.valueOf(telEditText.getText()));
        CProperties.CURRENT_RESERVATION.setNumberPeople(Integer.valueOf(String.valueOf(nbPeopleEditText.getText())));
        CProperties.CURRENT_RESERVATION.setNumberDayDish(Integer.valueOf(String.valueOf(nbDayDishEditText.getText())));
        CProperties.CURRENT_RESERVATION.setHourArrive(
                String.valueOf((String)hourArriveSpinner.getSelectedItem() + "h" + (String)minuteArriveSpinner.getSelectedItem()));
        CProperties.CURRENT_RESERVATION.setNote(String.valueOf(requestEditText.getText()));
    }

    private void initRequestEditTextListener() {

        // listener qui efface le android hint
        requestEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestEditText.setHint("");
            }
        });

        // listener qui remet le android hint
        requestEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (requestEditText.getText().equals(""))
                    requestEditText.setHint("Vous pouvez demander une table en particulier, une heure d'arrivée ...");
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

    private void initWidgets() {

        // textView
        dateTextView = (TextView) findViewById(R.id.date_textView_reservation);
        dateTextView.setText(CProperties.CURRENT_DAYDISH.getDate());
        webSiteTextView = (TextView) findViewById(R.id.webSite_textView_reservation);

        // editText
        nameEditText = (EditText) findViewById(R.id.name_editText);
        telEditText = (EditText) findViewById(R.id.tel_editText);
        nbPeopleEditText = (EditText) findViewById(R.id.nbPeople_editText);
        nbDayDishEditText = (EditText) findViewById(R.id.nbDayDish_editText);
        nbDayDishEditText.setText("0");
        requestEditText = (EditText) findViewById(R.id.request_editText);
        telNumber = (TextView) findViewById(R.id.telNumber_reservation_activity);

        // spinner
        hourArriveSpinner = (Spinner) findViewById(R.id.h_arrive);
        minuteArriveSpinner = (Spinner) findViewById(R.id.m_arrive);

        ArrayAdapter<CharSequence> adapterH = ArrayAdapter.createFromResource(this, R.array.hours, android.R.layout.simple_spinner_item);
        adapterH.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourArriveSpinner.setAdapter(adapterH);

        ArrayAdapter<CharSequence> adapterM = ArrayAdapter.createFromResource(this, R.array.minutes, android.R.layout.simple_spinner_item);
        adapterM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minuteArriveSpinner.setAdapter(adapterM);

        // button
        validateButton = (Button) findViewById(R.id.validate_reservation_button);
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

                    callInProgress = true;
                    call();
                }

                return;
            }
        }
    }

    private void goToHomeIntent() {

        Intent homeIntent = new Intent(getApplicationContext(), CHome.class);
        startActivity(homeIntent);
    }

    @Override
    protected void onRestart() {

        super.onRestart();

        goToHomeIntent();
    }
}
