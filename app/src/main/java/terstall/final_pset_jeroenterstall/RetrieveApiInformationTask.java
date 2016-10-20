package terstall.final_pset_jeroenterstall;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// This class handles the retrieving of json from the API
// Also stores the JSON tags
class RetrieveApiInformationTask extends AsyncTask<URL, Void, JSONObject>
{
    // Json names
    static final String JSON_RESULT = "results";
    static final String JSON_TRACK_MATCH = "trackmatches";
    static final String JSON_ALBUM_MATCH = "albummatches";
    static final String JSON_ARTIST_MATCH = "artistmatches";
    static final String JSON_TRACK = "track";
    static final String JSON_ALBUM = "album";
    static final String JSON_ARTIST = "artist";
    static final String JSON_NAME = "name";
    static final String JSON_IMAGE = "image";
    static final String JSON_IMAGE_URL = "#text";
    static final String JSON_TRACKS = "tracks";
    static final String JSON_SUMMARY = "summary";
    static final String JSON_WIKI = "wiki";
    static final String JSON_TOPTAGS = "toptags";
    static final String JSON_TAGS = "tags";
    static final String JSON_TAG = "tag";
    static final String JSON_BIO = "bio";
    static final int JSON_IMAGE_SIZE = 2; // 0 = small, 1 = medium, 2 = large, 3 = extra large

    @Override
    protected JSONObject doInBackground(URL... params)
    {
        try
        {
            // Retrieve url from arguments passed
            URL url = params[0];
            // Open a connection to api with url
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try
            {
                // Create a buffered reader to read in json
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                // While there are still lines to read, append to stringbuilder
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line).append("\n");
                }
                reader.close();
                // Return the json
                String response = sb.toString();
                return new JSONObject(response);

            }
            finally
            {
                // At the end make sure to disconnect
                urlConnection.disconnect();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onPostExecute(JSONObject result)
    {
        // Return result
        super.onPostExecute(result);
    }
}