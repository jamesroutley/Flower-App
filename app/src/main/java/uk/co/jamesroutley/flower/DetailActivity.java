package uk.co.jamesroutley.flower;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Script;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
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
    String commonName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView capturedImageView = (ImageView) findViewById(R.id.capturedImageView);
        ImageView servedImageView = (ImageView) findViewById(R.id.servedImageView);
        TextView commonNameTextView = (TextView) findViewById(R.id.commonName);
        TextView genusTextView = (TextView) findViewById(R.id.genus);
        TextView wikiTextView = (TextView) findViewById(R.id.wiki_text);

        // Load intent info
        JSONObject jsonObject = null;
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("jsonObject");

        try {
            jsonObject = new JSONObject(jsonString);

//            Toast.makeText(DetailActivity.this, jsonObject.toString(),
//                    Toast.LENGTH_LONG).show();

            Log.v(TAG, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String filePath = intent.getStringExtra("filePath");
        final File sourceFile = new File(filePath);



        String servedImageUrl = null;

        String genus = null;
        String wiki_info = null;
        if (jsonObject != null) {
            servedImageUrl = "http://10.0.3.2:5000/img/" + jsonObject.optString("image");
            commonName = jsonObject.optString("common_name");
            genus = jsonObject.optString("genus");
            wiki_info = jsonObject.optString("wiki_info");
        }

        Picasso.with(this).load(sourceFile).placeholder(R.drawable.progress_animation).into(capturedImageView);
        Picasso.with(this).load(servedImageUrl).placeholder(R.drawable.progress_animation).into(servedImageView);

        commonNameTextView.setText(commonName);
        genusTextView.setText(genus);
        wikiTextView.setText(wiki_info);
    }

    public void searchGoogle(View view) {
//        Toast.makeText(getApplicationContext(), "Google search",
//                Toast.LENGTH_LONG).show();
        Uri.Builder googleBuilder = new Uri.Builder();
        googleBuilder.scheme("https")
                .authority("www.google.co.uk")
                .appendPath("search")
                .appendQueryParameter("q", commonName);
        Uri googleUri = googleBuilder.build();
//        Log.v(TAG, googleUrl);
        openWebPage(googleUri);
    }

    public void searchWiki(View view) {
//        Toast.makeText(getApplicationContext(), "Wiki search",
//                Toast.LENGTH_LONG).show();
//        openWebPage("http://en.m.wikipedia.org/wiki/Narcissus_(plant)");
        Uri.Builder wikiBuilder = new Uri.Builder();
        wikiBuilder.scheme("https")
                .authority("en.m.wikipedia.org")
                .appendPath("w")
                .appendPath("index.php")
                .appendQueryParameter("title", "Special%3ASearch")
                .appendQueryParameter("profile", "default")
                .appendQueryParameter("search", commonName);
        Uri wikiUri = wikiBuilder.build();
        Log.v(TAG, wikiUri.toString());
        openWebPage(wikiUri);

    }


    public void openWebPage(Uri webpage) {
//        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

//    public void openWebPage(String url) {
//        Uri webpage = Uri.parse(url);
//        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;

    }
}
