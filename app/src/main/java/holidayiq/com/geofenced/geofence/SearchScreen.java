package holidayiq.com.geofenced.geofence;
import holidayiq.com.geofenced.R;
import holidayiq.com.geofenced.gsons.elemets;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchScreen extends AppCompatActivity implements TextWatcher{
    private ListView searchListView;
    private OfflineSearchAdapter mOfflineSearchAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_screen);
        initView();
    }
    private void initView()
    {
        searchListView = (ListView) findViewById(R.id.search_list);
        EditText oText = (EditText) findViewById(R.id.search_view);
        oText.addTextChangedListener(this);
        mOfflineSearchAdapter = new OfflineSearchAdapter(SearchScreen.this);
        searchListView.setAdapter(mOfflineSearchAdapter);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int countn, int after) {
        String que = s.toString();
        if(que.length() < 2)
            return;
        File dbFile = this.getDatabasePath("hiq_in_app.sqlite");

        SQLiteDatabase inAppDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        Cursor hotel_res = inAppDb.rawQuery(GeoDbHelper.getAllHotelsQueryKey(que), null);
        Cursor ss_res = inAppDb.rawQuery(GeoDbHelper.getAllSightseeingQueryKey(que),null);
        Cursor dd_res = inAppDb.rawQuery(GeoDbHelper.getAllDestinationQueryKey(que),null);
        ArrayList<elemets> objs = new ArrayList<>();
        try {
            if (dd_res != null) {
                while (dd_res.moveToNext()) {
                    elemets obj = new elemets();
                    obj.id = String.valueOf(dd_res.getInt(dd_res.getColumnIndex(GeoDbHelper.DESTINATION_COLUMN_ID)));
                    obj.name = dd_res.getString(dd_res.getColumnIndex(GeoDbHelper.DESTINATION_COLUMN_NAME));
                    obj.type = "destination";
                    objs.add(obj);
                }

            }

            if (hotel_res != null) {
                while (hotel_res.moveToNext()) {
                    elemets obj = new elemets();
                    obj.id = String.valueOf(hotel_res.getInt(hotel_res.getColumnIndex(GeoDbHelper.RESORT_COLUMN_ID)));
                    obj.name = hotel_res.getString(hotel_res.getColumnIndex(GeoDbHelper.RESORT_COLUMN_NAME));
                    obj.type = "hotel";
                    objs.add(obj);
                }

            }
            if (ss_res != null) {
                while (ss_res.moveToNext()) {
                    elemets obj = new elemets();
                    obj.id = String.valueOf(ss_res.getInt(ss_res.getColumnIndex(GeoDbHelper.SIGHT_SEEING_COULUMN_ID)));
                    obj.name = ss_res.getString(ss_res.getColumnIndex(GeoDbHelper.SIGHT_SEEING_COULUMN_NAME));
                    obj.type = "sightseeing";
                    objs.add(obj);
                }

            }


        }catch (Exception e){
            e.printStackTrace();
        }
        finally {

            if (hotel_res != null && !hotel_res.isClosed()) {
                hotel_res.close();
            }
            hotel_res = null;

            if (ss_res != null && !ss_res.isClosed()) {
                ss_res.close();
            }
            ss_res = null;
            if (dd_res != null && !dd_res.isClosed()) {
                dd_res.close();
            }
            dd_res = null;

            if (inAppDb != null && inAppDb.isOpen()) {
                inAppDb.close();
            }

        }

        mOfflineSearchAdapter.setData(objs);
        mOfflineSearchAdapter.notifyDataSetChanged();
    }
    public void offlineResultsClicked(String type,String id,String name)
    {
        Intent intent = new Intent(SearchScreen.this,AddItineraray.class);
        intent.putExtra("id",id);
        intent.putExtra("name",name);
        intent.putExtra("type",type);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
