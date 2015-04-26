package uk.co.jamesroutley.flower;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import uk.co.jamesroutley.flower.adapter.FlowerAdapter;


public class ResultsActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = ResultsActivity.class.getSimpleName();

    private String upLoadServerUri = null;
    private FlowerAdapter mFlowerAdapter;
    JSONArray jsonArray = null;
    ListView mainListView;
    String filePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        upLoadServerUri = "http://10.0.3.2:5000/androidupload";
        ImageView mImageView = (ImageView) findViewById(R.id.capturedImageView);
        mainListView = (ListView) findViewById(R.id.resultsList);
        mFlowerAdapter = new FlowerAdapter(this, getLayoutInflater());
        mainListView.setAdapter(mFlowerAdapter);
        mainListView.setOnItemClickListener(this);

        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        final File sourceFile = new File(filePath);
        Picasso.with(this).load(sourceFile).into(mImageView);


        //JSONArray jsonTemp = null;

        //mFlowerAdapter.updateData(jsonTemp);

        // UPLOAD IMAGE
        //dialog = ProgressDialog.show(ResultsActivity.this, "", "Uploading file...", true);
        new uploadFileTask().execute(sourceFile);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // create an Intent to take you over to a new DetailActivity
        Intent detailIntent = new Intent(this, DetailActivity.class);

        // pack away the data about the cover
        // into your Intent before you head out
        detailIntent.putExtra("jsonObject", jsonObject.toString());
        detailIntent.putExtra("filePath", filePath);

        // TODO: add any other data you'd like as Extras

        // start the next Activity using your prepared Intent
        startActivity(detailIntent);
    }

    @Override
    public void onClick(View v) {

    }


    public class uploadFileTask extends AsyncTask<File, Void, JSONArray> {


        @SuppressLint("LongLogTag")
        @Override
        protected JSONArray doInBackground(File... params) {
            File sourceFile = params[0];
            HttpURLConnection conn;
            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            final String fileName = sourceFile.getName();


            //Log.v(TAG, "this" + fileName);

            if (!sourceFile.isFile()) {
                //dialog.dismiss();
                Log.e(TAG, "Source File not exist :"
                        +fileName);
            }
            else
            {
                try {

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    //TODO I'm not sure what the 'name' property is for
                    dos.writeBytes("Content-Disposition: form-data; name="+fileName+";filename="
                            + fileName + "" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necessary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    int serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();
                    InputStream is = conn.getInputStream();

                    //close the streams
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                    jsonArray = convertInputStreamToJSONObject(is);
                    Log.v(TAG, "input stream is: jsonArray:" + jsonArray.toString());
                    Log.i(TAG, "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);

                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "Upload file to server error: " + ex.getMessage(), ex);
                    jsonArray = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Upload file to server Exception : "
                            + e.getMessage(), e);
                    jsonArray = null;
                }
            } // End else block
            return jsonArray;
        }


        @Override
        protected void onPostExecute (JSONArray jsonArray) {

            if (jsonArray != null) {
                mFlowerAdapter.updateData(jsonArray);
            }
            else {
                Toast.makeText(ResultsActivity.this, "Upload Error",
                        Toast.LENGTH_SHORT).show();
            }
        }


    }


    // Reads an InputStream and converts it to a JSONArray.
    public JSONArray convertInputStreamToJSONObject(InputStream stream) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        return new JSONArray(sb.toString());
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_view, menu);
        return true;
    }

}