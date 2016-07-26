package air.com.c2is.villedelyon;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;


/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentVide extends Fragment {
    public View rootView;
    private SupportMapFragment mMapFragment;

    public FragmentVide() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView            = inflater.inflate(R.layout.fragment_vide, container, false);

        TextView myLoad    = (TextView) rootView.findViewById(R.id.titreChargement);
        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");
        myLoad.setTypeface(myTypeface);

        return rootView;
    }
}
