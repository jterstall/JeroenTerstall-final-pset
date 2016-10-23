package terstall.final_pset_jeroenterstall;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// This class handles the retrieving of json from the API

class RetrieveApiInformationTask extends AsyncTask<URL, Void, JSONObject>
{
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
        super.onPostExecute(result);
    }
}