package com.youtubeurs.lite2.service.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.youtubeurs.lite2.util.StreamUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by thomashenry-gomes on 31/12/2014.
 */
public class GetSearchYouTubeUsersTask extends AsyncTask<String, Void, String> {

    private Exception mException;
    private Context mContext;

    public GetSearchYouTubeUsersTask(Context context){
        mContext = context;
    }

    @Override
    protected String doInBackground(String... url) {
        try {
            this.mException = null;
            // Get a httpclient to talk to the internet
            HttpClient client = new DefaultHttpClient();
            // Perform a GET request to YouTube for a JSON list of all the videos by a specific user
            HttpUriRequest request = new HttpGet(url[0]);
            // Get the response that YouTube sends back
            HttpResponse response = client.execute(request);
            // Convert this response into a readable string
            return StreamUtils.convertToString(response.getEntity().getContent());
        } catch (IOException e) {
            this.mException = e;
            return "";
        }
    }

    protected void onPostExecute(String response) {
        if(this.mException != null){
            Toast.makeText(mContext, "Une erreur s'est produite lors de la recherche de Youtubeur", Toast.LENGTH_SHORT);
        }
        else{
            Toast.makeText(mContext, "Recherche de Youtubeur", Toast.LENGTH_SHORT);
            ArrayList<String> array = new ArrayList<>();

            response = response.substring(response.indexOf("/user/") + 1);

            int end = 0;

            while (end != -1) {
                if(response.indexOf("\"") == -1) {
                    end = -1;
                }
                else {
                    if (!array.contains(response.substring(0, response.indexOf("\""))))
                        array.add(response.substring(0, response.indexOf("\"")));
                    response = response.substring(response.indexOf("\""));
                    if(response.indexOf("/user/") == -1) {
                        end = -1;
                    }
                    else {
                        response = response.substring(response.indexOf("/user/") + 1);
                    }
                }
            }
            ;
        }
    }
}
