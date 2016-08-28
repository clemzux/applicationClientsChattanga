package com.example.clemzux.applicationclientschattanga.reservation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.clemzux.applicationclientschattanga.R;

public class CReservation extends Activity {


    //////// attributes ////////


    private EditText nameEditText, telEditText, nbPeopleEditText, nbDayDishEditText, requestEditText;
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
    }

    private void initListeners() {

        initRequestEditTextListener();
        initWebSiteTextViewListener();
    }

    private void initRequestEditTextListener() {

        // listener qui efface le android hint
        requestEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestEditText.setText("");
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
        requestEditText = (EditText) findViewById(R.id.request_editText);
    }
}
