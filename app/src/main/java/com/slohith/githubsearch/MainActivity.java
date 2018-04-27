package com.slohith.githubsearch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchBoxEditText;

    private ProgressBar mLoadingIndicator;
    private ListView lv;
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        contactList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);
    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the github user you'd like to find,
     * and finally fires off an AsyncTask to perform the GET request using
     * our {@link GithubQueryTask}
     */
    private void makeGithubSearchQuery() {
        String githubQuery = mSearchBoxEditText.getText().toString();
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        new GithubQueryTask().execute(githubSearchUrl);
    }


    public class GithubQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                JSONObject jsonObj = new JSONObject(githubSearchResults);

                // Getting JSON Array node
                JSONArray contacts = jsonObj.getJSONArray("items");
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);

                    String id = c.getString("id");
                    String name = c.getString("name");
                    String full_name = c.getString("full_name");
                    String stars = c.getString("stargazers_count");

                    HashMap<String, String> contact = new HashMap<>();

                    contact.put("id", id);
                    contact.put("name", name);
                    contact.put("full_name", full_name);
                    contact.put("stars", stars);

                    contactList.add(contact);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

            @Override
        protected void onPostExecute(String githubSearchResults) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
                ListAdapter adapter = new SimpleAdapter(
                        MainActivity.this, contactList,
                        R.layout.list_item_xml, new String[]{"name", "stars",
                        }, new int[]{R.id.full_name , R.id.stars });

                lv.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search) {
            makeGithubSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);




    }
}

