package com.youtubeurs.lite2.service.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.youtubeurs.lite2.R;
import com.youtubeurs.lite2.VideosActivity;
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

    final String VIDEO_AUTHOR = "video_author";

    public GetSearchYouTubeUsersTask(Context context){
        mContext = context;
    }

    @Override
    protected String doInBackground(String... url) {
        try {
            this.mException = null;

            url[0] = url[0].replaceAll(" ", "+");

            // Get a httpclient to talk to the internet
            HttpClient client = new DefaultHttpClient();
            // Perform a GET request
            HttpUriRequest request = new HttpGet(url[0]);
            // Get the response that YouTube sends back
            HttpResponse response = client.execute(request);
            // Convert this response into a readable string
            return StreamUtils.convertToString(response.getEntity().getContent());
        } catch (IOException e) {
            this.mException = e;
            return "";
        } catch (Exception e) {
            this.mException = e;
            return "";
        }
    }

    protected void onPostExecute(String response) {
        if(this.mException != null){
            Toast.makeText(mContext, "Une erreur s'est produite lors de la recherche du Youtubeur !!!", Toast.LENGTH_SHORT).show();
        }
        else{
            ArrayList<String> array = new ArrayList<>();

            if (response.indexOf("/user/") != -1) {
                response = response.substring(response.indexOf("/user/") + 1);

                int end = 0;

                while (end != -1) {
                    if(response.indexOf("\"") == -1) {
                        end = -1;
                    }
                    else {
                        if (!array.contains(response.substring(0, response.indexOf("\"")).replace("user/", "")))
                            array.add(response.substring(0, response.indexOf("\"")).replace("user/", ""));
                        response = response.substring(response.indexOf("\""));
                        if(response.indexOf("/user/") == -1) {
                            end = -1;
                        }
                        else {
                            response = response.substring(response.indexOf("/user/") + 1);
                        }
                    }
                }
            }

            final AlertDialog.Builder adb2 = new AlertDialog.Builder(mContext);

            TextView title2 = new TextView(mContext);
            title2.setText("Rechercher un Youtubeur");
            title2.setPadding(15, 15, 15, 15);
            title2.setGravity(Gravity.CENTER);
            title2.setTextColor(mContext.getResources().getColor(R.color.material_textColorPrimary));
            title2.setTextSize(19);
            adb2.setCustomTitle(title2);

            adb2.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            final AlertDialog ad2 = adb2.create();

            if (array.size() > 0) {
                ListView list = new ListView(mContext);
                list.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, array));
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String s = (String) parent.getItemAtPosition(position);
                        ad2.cancel();
                        Intent intent = new Intent(mContext, VideosActivity.class);
                        intent.putExtra(VIDEO_AUTHOR, s);
                        mContext.startActivity(intent);
                    }
                });
                ad2.setView(list);
            }
            else {
                ad2.setMessage("Aucun Youtubeur n'a été trouvé.");
            }

            ad2.show();

            TextView messageText2 = (TextView) ad2.findViewById(android.R.id.message);
            if (messageText2 != null) {
                messageText2.setPadding(15, 15, 15, 15);
                messageText2.setGravity(Gravity.CENTER);
                messageText2.setTextColor(mContext.getResources().getColor(R.color.material_textColorPrimary));
                messageText2.setTextSize(18);
            }

        }
    }

}
