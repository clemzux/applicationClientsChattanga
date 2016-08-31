package com.example.clemzux.applicationclientschattanga.reservation;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.clemzux.applicationclientschattanga.R;
import com.example.clemzux.applicationclientschattanga.home.CHome;
import com.example.clemzux.applicationclientschattanga.sqllite.CLocalDataBase;
import com.example.clemzux.applicationclientschattanga.utilitaries.CJsonDecoder;
import com.example.clemzux.applicationclientschattanga.utilitaries.CProperties;
import com.example.clemzux.applicationclientschattanga.utilitaries.CRestRequest;
import com.example.clemzux.applicationclientschattanga.utilitaries.CUtilitaries;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CReservation extends Activity {


    //////// attributes ////////


    private EditText nameEditText, telEditText, nbPeopleEditText, nbDayDishEditText, requestEditText, hourArriveEditText;
    private TextView dateTextView, webSiteTextView;
    private Button validateButton;


    //////// methods ////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // mon code

        // initialisation des widgets
        initWidgets();

        // initialisation des listeners
        initListeners();

        // on vérifie si le client n'a pas déja réservé
        verifyReservation();
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
            fillEditText();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillEditText() {

        nameEditText.setText(CProperties.CURRENT_RESERVATION.getName());
        telEditText.setText(CProperties.CURRENT_RESERVATION.getTel());
        nbPeopleEditText.setText(String.valueOf(CProperties.CURRENT_RESERVATION.getNumberPeople()));
        nbDayDishEditText.setText(String.valueOf(CProperties.CURRENT_RESERVATION.getNumberDayDish()));
        hourArriveEditText.setText(CProperties.CURRENT_RESERVATION.getHourArrive());
        requestEditText.setText(CProperties.CURRENT_RESERVATION.getNote());
    }

    private void initListeners() {

        initRequestEditTextListener();
        initWebSiteTextViewListener();
        initValidateButtonListener();
        initNumberDayDishEditTextListener();
    }

    private void initNumberDayDishEditTextListener() {

        nbDayDishEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (String.valueOf(nbDayDishEditText.getText()).equals("0"))
                    nbDayDishEditText.setText("");

                CUtilitaries.test(getApplicationContext(), "salut");
            }
        });
    }

    private void initValidateButtonListener() {

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initCurrentReservation();

                if (!CProperties.hasReserved) {

                    validateButtonConfiguration();
                }
                else {

                    modificationButtonConfiguration();
                }
            }
        });
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
        CProperties.CURRENT_RESERVATION.setHourArrive(String.valueOf(hourArriveEditText.getText()));
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
                String url = "http://www.la-chattanga.fr";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void initWidgets() {

        // textView
        dateTextView = (TextView) findViewById(R.id.date_textView_reservation);
        webSiteTextView = (TextView) findViewById(R.id.webSite_textView_reservation);

        // editText
        nameEditText = (EditText) findViewById(R.id.name_editText);
        telEditText = (EditText) findViewById(R.id.tel_editText);
        nbPeopleEditText = (EditText) findViewById(R.id.nbPeople_editText);
        nbDayDishEditText = (EditText) findViewById(R.id.nbDayDish_editText);
        nbDayDishEditText.setText("0");
        hourArriveEditText = (EditText) findViewById(R.id.h_arrive);
        requestEditText = (EditText) findViewById(R.id.request_editText);

        // button
        validateButton = (Button) findViewById(R.id.validate_reservation_button);
    }

    private void goToHomeIntent() {

        Intent homeIntent = new Intent(getApplicationContext(), CHome.class);
        startActivity(homeIntent);
    }
}
