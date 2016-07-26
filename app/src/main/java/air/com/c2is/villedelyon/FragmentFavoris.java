package air.com.c2is.villedelyon;

import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentFavoris extends Fragment {
    private DataBaseHelper myDbHelper;
    public View rootView;
    public ListView mylistview;
    public ArrayList<HashMap<String, Object>> listItems;


    public FragmentFavoris() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView            = inflater.inflate(R.layout.fragment_favoris, container, false);



        TextView myAucunTexte = (TextView) rootView.findViewById(R.id.myAucunTexte);
        TextView myTitre      = (TextView) rootView.findViewById(R.id.titreFavoris);

        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");
        myTitre.setTypeface(myTypeface);
        myTitre.getPaint().setAntiAlias(true);
        myAucunTexte.setTypeface(myTypeface);
        myAucunTexte.getPaint().setAntiAlias(true);

        myDbHelper = new DataBaseHelper(Config.myHome.getBaseContext());
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

        Cursor c = myDbHelper.loadFavoris();
        listItems = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> item;
        int nbe = 0;
        try {
                if (c != null) {
                    while (c.moveToNext()) {
                        item = new HashMap<String, Object>();

                        item.put("titre"            , c.getString(0));
                        item.put("id_equipement"    , c.getInt(1));
                        item.put("xml_equipement"   , c.getString(2));

                        listItems.add(item);
                        nbe++;
                    }
                }
        }catch(SQLException sqle){
            throw sqle;
        }
        myDbHelper.close();

        if (nbe==0) {
            myAucunTexte.setVisibility(View.VISIBLE);
        }else{
            myAucunTexte.setVisibility(View.GONE);
        }

        mylistview = (ListView) rootView.findViewById(R.id.mylistview);
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Config.myContentValue   = listItems.get(position);
                Config.sql_type         = listItems.get(position).get("id_equipement").toString();


//                Log.d("myTag", listItems.get(position).get("id_equipement").toString());

                if (Config.sql_type.equals("0")) {
                    Config.flagContentEquip = 0;
                    Config.sql_type = "";
                    Config.xml_id   = listItems.get(position).get("xml_equipement").toString();
                }else {
                    Config.flagContentEquip = 1;
                    Config.xml_id = "";
                }

                Config.myFavoris.loadEquipement();

            }
        });


        SimpleAdapter mSchedule;
        mSchedule = new SimpleAdapter(Config.myHome.getBaseContext(), listItems, R.layout.itemfavoris,
                new String[]{"titre"},
                new int[]{R.id.titre});

        mSchedule.setViewBinder(new MyViewBinderFavoris());

        mylistview.setAdapter(null);
        mylistview.setAdapter(mSchedule);


        return rootView;
    }

}
