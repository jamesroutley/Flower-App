package uk.co.jamesroutley.flower;


    import java.util.ArrayList;
    import java.util.List;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import android.app.Activity;
    import android.app.ProgressDialog;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.Menu;
    import android.widget.ListView;

    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.VolleyLog;
    import com.android.volley.toolbox.JsonArrayRequest;



import uk.co.jamesroutley.flower.Movie.Movie;
import uk.co.jamesroutley.flower.adapter.CustomListAdapter;
import uk.co.jamesroutley.flower.app.AppController;


public class ResultsActivity extends Activity {
    // Log tag
    private static final String TAG = ResultsActivity.class.getSimpleName();

    // Movies json url
    private static final String url = "http://jamesroutley.co.uk/Flower-Classification/api.json";
    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<Movie>();
    private ListView listView;
    private CustomListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        listView = (ListView) findViewById(R.id.list1);
        adapter = new CustomListAdapter(this, movieList);
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
                                Movie movie = new Movie();
                                movie.setTitle(obj.getString("title"));
                                movie.setThumbnailUrl(obj.getString("image"));

                                movieList.add(movie);


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