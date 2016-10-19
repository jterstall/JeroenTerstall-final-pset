package terstall.final_pset_jeroenterstall;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

// This class handles the retrieving of an image url and converting to a bitmap which replaces the source
// of the poster image ImageView
public class ShowImageTask extends AsyncTask<String, Void, Bitmap>
{
    ImageView imageView;

    // Constructor
    public ShowImageTask(ImageView imageView)
    {
        this.imageView = imageView;
    }

    protected Bitmap doInBackground(String... params)
    {
        // Retrieve image url
        String image_url = params[0];
        URL url;
        try
        {
            // Retrieve Bitmap from url and return
            url = new URL(image_url);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bmp;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Bitmap result)
    {
        // Set the ImageView with the bitmap created in doInBackground
        imageView.setImageBitmap(result);
    }
}

