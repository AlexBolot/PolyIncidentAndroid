package polytechnice.si3.ihm.android.Incidents;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressLint("StaticFieldLeak")
public class ThumbnailLoader extends AsyncTask<String, Void, Bitmap> {
    private ImageView image;
    private Context context;
    private String arg = "";

    public ThumbnailLoader(ImageView image, Context context) {
        this.image = image;
        this.context = context;
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

    private void loadImage() {
        Uri file = Uri.parse(arg);

        try (InputStream inputStream = context.getContentResolver().openInputStream(file)) {
            Bitmap result = BitmapFactory.decodeStream(inputStream);
            image.setImageBitmap(result);
        } catch (Exception e) {
            Log.d("Debug.catch", e.getMessage());
        }
    }
}
