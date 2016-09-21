package com.example.clemzux.applicationclientschattanga.update;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.clemzux.applicationclientschattanga.R;
import com.example.clemzux.applicationclientschattanga.utilitaries.CProperties;
import com.example.clemzux.applicationclientschattanga.utilitaries.CUtilitaries;

public class CUpdate extends Activity {

    private TextView webSiteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        //////// mon code ////////

        initWidgets();
        initListeners();
    }

    private void initListeners() {

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

        webSiteTextView = (TextView) findViewById(R.id.webSite_textView_update);
    }
}
