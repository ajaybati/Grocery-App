package com.example.ajaybati.grocery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    LinearLayout list;
    ListView grocery;
    ArrayList<String> finalGroceryList;
    ArrayList<String> groceryList;

    interface AsyncResult
    {
        void onResult(JSONObject object);
    }

    public class groceryItem{
        private String groceryItem;
        private String amount;
        private Boolean getIt;

        public groceryItem(String item, String num){
            groceryItem=item;
            amount=num;
        }

        public String access(){
            return amount+" "+groceryItem;
        }
    }

    public class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        AsyncResult callback;
        public DownloadWebpageTask(AsyncResult callback) {
            this.callback = callback;
        }
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to download the requested page.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // remove the unnecessary parts from the response and construct a JSON
            int start = result.indexOf("{", result.indexOf("{") + 1);
            int end = result.lastIndexOf("}");
            String jsonResponse = result.substring(start, end);
            try {
                JSONObject table = new JSONObject(jsonResponse);
                callback.onResult(table);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private String downloadUrl(String urlString) throws IOException {
            InputStream is = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int responseCode = conn.getResponseCode();
                is = conn.getInputStream();
                String contentAsString = convertStreamToString(is);
                return contentAsString;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

    public void view(View view){

        list.animate().alpha(1f).setDuration(10);
        grocery.animate().alpha(0f).setDuration(10);
        for(int x=0;x<groceryList.size();x++){
            if(finalGroceryList.get(x)=="true"){
                TextView textView= (TextView) grocery.getAdapter().getView(x, null, grocery);
                list.addView(textView);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main) ;

        

        list=(LinearLayout) findViewById(R.id.list);
        grocery=(ListView)findViewById(R.id.grocery);
        groceryList=new ArrayList<String>();
        finalGroceryList=new ArrayList<String>();

        finalGroceryList.add("0");
        finalGroceryList.add("0");
        finalGroceryList.add("0");
        finalGroceryList.add("0");

        groceryItem milk=new groceryItem("Apple", "5");
        groceryItem rice=new groceryItem("Rice", "5lb");
        groceryItem oil=new groceryItem("Oil", "1 can");
        groceryItem buttermilk=new groceryItem("Buttermilk", "2");

        groceryList.add(rice.access());
        groceryList.add(oil.access());
        groceryList.add(milk.access());
        groceryList.add(buttermilk.access());

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked, groceryList);
        grocery.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        grocery.setAdapter(arrayAdapter);

        grocery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(grocery.isItemChecked(position)){
                    grocery.setItemChecked(position, true);
                    System.out.println("true");
                    finalGroceryList.set(position,"true");
                /*if(finalGroceryList.get(position)!=null){
                    finalGroceryList.set(position,"true");
                }*/
                }
                else{
                    grocery.setItemChecked(position, false);
                    System.out.println("false");
                    finalGroceryList.set(position,"false");
                /*if(finalGroceryList.get(position)!=null){
                    finalGroceryList.set(position,"false");
                }*/
                }
                System.out.println(finalGroceryList);
            }
        });





    }
}
