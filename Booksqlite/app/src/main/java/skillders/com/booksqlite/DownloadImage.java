package skillders.com.booksqlite;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Naisargi on 28-Dec-17.
 */

public class DownloadImage extends AsyncTask<String,Integer,Bitmap> {

    ImageView iv;
    Bitmap bmp=null;

    public DownloadImage(ImageView iv) {
        this.iv = iv;
    }

    @Override
    protected Bitmap doInBackground(String... url) {
        String displayurl= url[0];

        try {
            InputStream is=new java.net.URL(displayurl).openStream();
            bmp= BitmapFactory.decodeStream(is);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        iv.setImageBitmap(bmp);
        super.onPostExecute(bitmap);
    }
}
