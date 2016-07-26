package air.com.c2is.villedelyon;

import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class MyViewBinderUrgence implements ViewBinder {
		
	public boolean setViewValue(View view, Object data,String textRepresentation) {

		if (view.getId() == R.id.titre) {
			String myData2	 = (String) data;
			TextView myTexte = (TextView) view;

			Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");
			myTexte.setTypeface(myTypeface);
			myTexte.getPaint().setAntiAlias(true);

			myTexte.setText(myData2);

			return true;
		}

		if (view.getId() == R.id.numero) {
			String myData2	 = (String) data;
			Button myBouton = (Button) view;

			Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");
			myBouton.setTypeface(myTypeface);
			myBouton.getPaint().setAntiAlias(true);

			myBouton.setText(myData2);

			return true;
		}

		return false;
	}
}