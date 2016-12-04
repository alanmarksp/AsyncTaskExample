package com.alanmarksp.asynctaskexample;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alanmarksp.asynctaskexample.models.Repo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String URL_STRING = "https://api.github.com/users/{user}/repos";

    private ArrayAdapter<Repo> repoArrayAdapter;
    private List<Repo> repos;

    private EditText gitHubUserEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repos = new ArrayList<>();

        repoArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, repos);

        ListView repositoriesListView = (ListView) findViewById(R.id.repositories_list_view);

        repositoriesListView.setAdapter(repoArrayAdapter);

        gitHubUserEditText = (EditText) findViewById(R.id.github_user_edit_text);

        Button repositoriesSearchButton = (Button) findViewById(R.id.repositories_search_button);

        repositoriesSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRepositoriesSearchButton();
            }
        });
    }

    private void onClickRepositoriesSearchButton() {
        String user = gitHubUserEditText.getText().toString();
        if (!user.equals("")) {
            String urlString = URL_STRING.replace("{user}", user);
            new fetchGitHubRepositories().execute(urlString);
        }
        else {
            Toast.makeText(this, "Debe indicar un usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRepositories(ArrayList<Repo> repos) {
        if (repos != null) {
            this.repos.clear();
            this.repos.addAll(repos);
            repoArrayAdapter.notifyDataSetChanged();
        }
        else {
            Toast.makeText(this, "No se han encontrado repositorios", Toast.LENGTH_SHORT).show();
        }
    }

    private class fetchGitHubRepositories extends AsyncTask<String, Void, ArrayList<Repo>> {

        @Override
        protected ArrayList<Repo> doInBackground(String... strings) {
            HttpURLConnection con = null;
            try {
                URL mUrl = new URL(strings[0]);
                con = (HttpURLConnection) mUrl.openConnection();
                InputStream is = con.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                Gson gson = new Gson();
                ArrayList<Repo> repos = gson.fromJson(isr, new TypeToken<List<Repo>>(){}.getType());
                is.close();
                return repos;
            }
            catch (Exception e) {
                Log.d("Debug", "doInBackground: " + e.getMessage());
                return null;
            } finally {
                if (con != null) con.disconnect();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Repo> repos) {
            updateRepositories(repos);
        }
    }
}
