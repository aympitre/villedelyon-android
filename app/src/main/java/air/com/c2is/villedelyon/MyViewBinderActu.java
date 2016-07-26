package air.com.c2is.villedelyon;

import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class MyViewBinderActu implements ViewBinder {
		
	public boolean setViewValue(View view, Object data,String textRepresentation) {
		if (view.getId() == R.id.texte) {
			String myData2	 = (String) data;
			WebView myTexte  = (WebView) view;
			myTexte.loadDataWithBaseURL		(null, myData2, "text/html", "UTF-8", null);

			return true;
		}
		
		if ((view.getId() == R.id.titre)||(view.getId() == R.id.titreActualite)) {
			String myData2 		= (String) data;
			TextView myTexte 	= (TextView) view;

			Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");
			myTexte.setTypeface(myTypeface);
			myTexte.getPaint().setAntiAlias(true);

			myTexte.setText(myData2);
			return true;
		}

		if( (view instanceof ImageView) & (data instanceof String) ) {
			ImageView iv  = (ImageView) view;
			String urlImg = (String) data;

			BitmapDownloaderTask task = new BitmapDownloaderTask(iv);
			task.execute(urlImg);
			iv.setTag(urlImg);

			return true;
		}


		return false;
	}
}