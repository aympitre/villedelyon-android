package air.com.c2is.villedelyon;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;

    public BitmapDownloaderTask(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
         // params comes from the execute() call: params[0] is the url.
    	
    	  try {
	        	URL pictureURL = new URL(params[0]);
                Bitmap bitmap = BitmapFactory.decodeStream(pictureURL.openStream());

	        	return bitmap;
	             
			} catch (MalformedURLException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				e.printStackTrace();
			}   	
    	
         return null;
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

//        Config.numDebug = Config.numDebug + 1;
        
        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}