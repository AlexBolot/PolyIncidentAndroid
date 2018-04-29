package polytechnice.si3.ihm.android.Incidents;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressLint("StaticFieldLeak")
public class ThumbnailLoader extends AsyncTask<String, Void, Bitmap> {
    private ImageView image;

    public ThumbnailLoader(ImageView image) {
        this.image = image;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            URL imageURL = new URL(strings[0]);
            HttpURLConnection con = (HttpURLConnection) imageURL.openConnection();
            InputStream is = con.getInputStream();
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            Log.e("Error reading file", e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        image.setImageBitmap(result);
    }
}
