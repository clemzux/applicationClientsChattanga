package com.example.clemzux.applicationclientschattanga.imagepreview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.clemzux.applicationclientschattanga.R;
import com.example.clemzux.applicationclientschattanga.utilitaries.CProperties;
import com.example.clemzux.applicationclientschattanga.utilitaries.CUtilitaries;

public class CImagePreview extends Activity {


    //////// attributes ////////

    private ImageView previewImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_image_preview);

        // mon code

        initWidget();
    }

    private void initWidget() {

        initImageViewWidget();
    }

    private void initImageViewWidget() {

        previewImageView = (ImageView) findViewById(R.id.image_preview);
        previewImageView.setImageResource(CUtilitaries.imageRessourceSearcher(CProperties.CURRENT_DAYDISH.getImageIdentifier()));
    }
}
