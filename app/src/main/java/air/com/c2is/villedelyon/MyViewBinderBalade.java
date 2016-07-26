package air.com.c2is.villedelyon;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;


public class MyViewBinderBalade implements ViewBinder {
	public boolean setViewValue(View view, Object data,String textRepresentation) {

		if (view.getId() == R.id.description) {
			String myData2	 = (String) data;

			TextView myTexte = (TextView) view;

			myTexte.setText(Config.killHtml(myData2));

			return true;
		}
		
		if (view.getId() == R.id.titre) {
			String myData2	 	= (String) data;
			TextView myTexte 	= (TextView) view;
			Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

			myTexte.setTypeface(myTypeface);
			myTexte.getPaint().setAntiAlias(true);

			myTexte.setText(myData2);

			return true;
		}


		if( (view instanceof ImageView) & (data instanceof String) ) {
			ImageView iv	 = (ImageView) view;
			String urlImg 	 = (String) data;

		//	if (!iv.getTag().equals(urlImg)) {
//				Log.d("myTag", ">> " + iv.getTag() + "/" + urlImg);
				//iv.setImageResource(R.drawable.neutre);

				BitmapDownloaderTask task = new BitmapDownloaderTask(iv);
				task.execute(urlImg);
				iv.setTag(urlImg);
		//	}

			return true;
		}
		
		return false;
	}
}