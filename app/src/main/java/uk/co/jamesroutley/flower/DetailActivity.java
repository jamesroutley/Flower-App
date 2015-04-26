package uk.co.jamesroutley.flower;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by admin on 29/03/15.
 */
public class DetailActivity extends ActionBarActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView capturedImageView = (ImageView) findViewById(R.id.capturedImageView);
        ImageView servedImageView = (ImageView) findViewById(R.id.servedImageView);
        TextView commonNameTextView = (TextView) findViewById(R.id.commonName);
        TextView genusTextView = (TextView) findViewById(R.id.genus);

        // Load intent info
        JSONObject jsonObject = null;
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("jsonObject");

        try {
            jsonObject = new JSONObject(jsonString);

            Toast.makeText(DetailActivity.this, jsonObject.toString(),
                    Toast.LENGTH_LONG).show();

            Log.v(TAG, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String filePath = intent.getStringExtra("filePath");
        final File sourceFile = new File(filePath);



        String servedImageUrl = null;
        String commonName = null;
        String genus = null;
        if (jsonObject != null) {
            servedImageUrl = "http://10.0.3.2:5000/img/" + jsonObject.optString("image");
            commonName = jsonObject.optString("common_name");
            genus = jsonObject.optString("genus");
        }

        Picasso.with(this).load(sourceFile).placeholder(R.drawable.progress_animation).into(capturedImageView);
        Picasso.with(this).load(servedImageUrl).placeholder(R.drawable.progress_animation).into(servedImageView);

        commonNameTextView.setText(commonName);
        genusTextView.setText(genus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;

    }
}
