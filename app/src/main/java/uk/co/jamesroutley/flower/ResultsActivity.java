package uk.co.jamesroutley.flower;
// TODO use Volley
// look at json (?)
// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
// TODO send photo, get 'data' back. Greymotion. Google developer interface


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;




public class ResultsActivity extends Activity {

    ListView mainListView;

    public class Flower {
        public String name;
        public String latinname;

        public Flower(String name, String latinname) {
            this.name = name;
            this.latinname = latinname;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        mainListView = (ListView) findViewById(R.id.main_listview);


        // Construct the data source
        ArrayList<Flower> arrayOfFlowers = new ArrayList<Flower>();
        // Create the adapter to convert the array to views
        FlowersAdapter adapter = new FlowersAdapter(this, arrayOfFlowers);
        // Attach the adapter to a ListView
        //ListView listView = (ListView) findViewById(R.id.lvItems);
        mainListView.setAdapter(adapter);

        // Add item to adapter
        Flower newFlower1 = new Flower("Daffodil", "Narcissus");
        adapter.add(newFlower1);
        /*
        Flower newFlower = new Flower("Rose", "Rosa");
        adapter.add(newFlower);
        Flower newFlower2 = new Flower("Lilly", "Lilium");
        adapter.add(newFlower2);
        */


        /*
        JSONObject rose = new JSONObject();
        try {
            rose.put("name", "Rose");
            rose.put("latinname", "Rosa");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(rose);
        ArrayList<Flower> newFlowers = Flower.fromJson(jsonArray)
        adapter.addAll(newFlowers);
        */
    }


    //TODO ArrayAdapter class should maybe not be static (made static to allow ViewHolder to be static)
    public static class FlowersAdapter extends ArrayAdapter<Flower> {
        // View lookup cache
        private static class ViewHolder {
            TextView name;
            TextView latinname;
        }

        public FlowersAdapter(Context context, ArrayList<Flower> flowers) {
            super(context, R.layout.item_flower, flowers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Flower flower = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_flower, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.latinname = (TextView) convertView.findViewById(R.id.tvLatinName);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.name.setText(flower.name);
            viewHolder.latinname.setText(flower.latinname);
            // Return the completed view to render on screen
            return convertView;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}