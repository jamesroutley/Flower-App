package uk.co.jamesroutley.flower;


    import java.io.BufferedReader;
    import java.io.DataOutputStream;
    import java.io.File;
    import java.io.FileInputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.io.Reader;
    import java.io.UnsupportedEncodingException;
    import java.net.HttpURLConnection;
    import java.net.MalformedURLException;
    import java.net.URL;
    import java.util.ArrayList;
    import java.util.List;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import android.annotation.SuppressLint;
    import android.app.Activity;
    import android.app.ProgressDialog;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.Menu;
    import android.widget.ImageView;
    import android.widget.ListView;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.VolleyLog;
    import com.android.volley.toolbox.JsonArrayRequest;
    import com.squareup.picasso.Picasso;


    import uk.co.jamesroutley.flower.FlowerResult.FlowerResult;
    import uk.co.jamesroutley.flower.adapter.CustomListAdapter;
import uk.co.jamesroutley.flower.app.AppController;


public class ResultsActivity extends Activity {
    // Log tag
    private static final String TAG = ResultsActivity.class.getSimpleName();

    // Movies json url
    private static final String url = "http://jamesroutley.co.uk/Flower-Classification/api.json";
    private ProgressDialog pDialog;
    private List<FlowerResult> flowerResultList = new ArrayList<FlowerResult>();
    private ListView listView;
    private CustomListAdapter adapter;
    private ImageView mImageView;
    private String filePath = null;
    // TODO make private?
    ProgressDialog dialog = null;
    int serverResponseCode = 0;
    String upLoadServerUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        mImageView = (ImageView)findViewById(R.id.imageView1);
        listView = (ListView) findViewById(R.id.list1);

        upLoadServerUri = "http://10.0.3.2:5000/upload";

        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        //TODO: change this name, pass the file to the Upload function
        File sourceFile = new File(filePath);
        Picasso.with(this).load(sourceFile).into(mImageView);
        /*
        if (filePath != null) {
            // bitmap factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            mImageView.setImageBitmap(bitmap);
        }
        */
        Log.v(TAG, filePath);


        // UPLOAD IMAGE
        dialog = ProgressDialog.show(ResultsActivity.this, "", "Uploading file...", true);

        // TODO use Async task?
        new Thread(new Runnable() {
            public void run() {
                uploadFile(filePath);

            }
        }).start();


        // VOLLEY DOWNLOAD FLOWER INFORMATION
        adapter = new CustomListAdapter(this, flowerResultList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                FlowerResult flowerResult = new FlowerResult();
                                flowerResult.setTitle(obj.getString("title"));
                                flowerResult.setThumbnailUrl(obj.getString("image"));

                                flowerResultList.add(flowerResult);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }


    @SuppressLint("LongLogTag")
    public int uploadFile(String sourceFileUri) {

        HttpURLConnection conn;// = null;
        DataOutputStream dos;// = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        final String fileName = sourceFile.getName();

        Log.v(TAG, "this" + fileName);

        if (!sourceFile.isFile()) {
            dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :"
                    +sourceFileUri);
            return 0;
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
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                InputStream is = conn.getInputStream();

                JSONArray jsonArray = convertInputStreamToJSONObject(is);
                Log.v("input stream is:", "jsonArray:" + jsonArray.toString());





                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);



                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ResultsActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ResultsActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ResultsActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
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
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_view, menu);
        return true;
    }

}