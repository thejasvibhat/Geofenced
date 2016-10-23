package holidayiq.com.geofenced;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anupcowkur.reservoir.Reservoir;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;

import holidayiq.com.geofenced.geofence.AddItineraray;
import holidayiq.com.geofenced.geofence.ExceptionUtils;
import holidayiq.com.geofenced.geofence.GeoFenceHelper;
import holidayiq.com.geofenced.geofence.HIQConstant;
import holidayiq.com.geofenced.geofence.HIQLocationManager;
import holidayiq.com.geofenced.geofence.HIQSharedPrefrence;
import holidayiq.com.geofenced.geofence.InAppDBHelper;
import holidayiq.com.geofenced.geofence.SearchScreen;
import holidayiq.com.geofenced.gsons.ItenaryParent;
import holidayiq.com.geofenced.gsons.IternaryList;

public class MainActivity extends AppCompatActivity {
    TwoWayView mRecyclerView;

    ArrayList<NewsStructure> newsArrayLst = new ArrayList<NewsStructure>();

    String[] strImageUrl = {"http://www.hdwallpapersos.com/wp-content/uploads/2014/08/Nature-HD-Wallpaper-1080p.jpg",
            "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQJfVFBg3wTps8lucZi1RJ_sDrNDlsWUn5v_CP3dTI-eWlqnedn",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSnrFdFpG_70zp8RFPkuM7myrc6xkIVKS8aubXAvo_t3unbWuiJ",
            "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcSuTV9Y9xYVvd0DHO7-gHht6O8tc343B5pa9kQnXMQbeyfQvwQF",
            "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcS5iTL5GeVCo2hlfCl9h1c8fBW7bF-2ZQ7hAuo6aNGzgpZgfELW",
            "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQJfVFBg3wTps8lucZi1RJ_sDrNDlsWUn5v_CP3dTI-eWlqnedn",
            "http://www.hdwallpapersos.com/wp-content/uploads/2014/08/Nature-HD-Wallpaper-1080p.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSnrFdFpG_70zp8RFPkuM7myrc6xkIVKS8aubXAvo_t3unbWuiJ",

            "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcSuTV9Y9xYVvd0DHO7-gHht6O8tc343B5pa9kQnXMQbeyfQvwQF",
            "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcS5iTL5GeVCo2hlfCl9h1c8fBW7bF-2ZQ7hAuo6aNGzgpZgfELW"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                ItenaryPreparationHelper.prepareItenary(MainActivity.this);
                return null;
            }
        }.execute();
        initView();
        triggerGeoFence();
//        LinearLayout content_layout = (LinearLayout) findViewById(R.id.content_layout);
        Gson gson = new Gson();
        ItenaryParent iternary = gson.fromJson(HIQConstant.json_obj, ItenaryParent.class);
        //Reservoir.putAsync("data",iternary,null);
        //Reservoir.getAsync("data");
//        Log.e("iternary",iternary.getIternaryList().size()+"");
//
//        for (int i=0;i<iternary.getIternaryList().size();i++){
//            IternaryList obj = iternary.getIternaryList().get(i);
//            View blanket_layout = getLayoutInflater().inflate(R.layout.blanket_itenary_object, null);
//            ImageView type_icon = (ImageView) blanket_layout.findViewById(R.id.type_icon);
//            if(obj.getType().equalsIgnoreCase("journey")){
//                type_icon.setImageResource(R.drawable.ic_map);
//            }else if(obj.getType().equalsIgnoreCase("destination")){
//                type_icon.setImageResource(R.drawable.ic_icon_city);
//            }else if(obj.getType().equalsIgnoreCase("hotel")){
//                type_icon.setImageResource(R.drawable.ic_icon_city);
//            }else if(obj.getType().equalsIgnoreCase("ss")){
//                type_icon.setImageResource(R.drawable.ic_icon_city);
//            }
//        }

    }
    private void AddCustomItinerary()
    {
        Intent search = new Intent(MainActivity.this, SearchScreen.class);
        startActivity(search);
    }
    private void initView()
    {
        FloatingActionButton oBut = (FloatingActionButton) findViewById(R.id.fab);
        if (oBut != null) {
            oBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),"came here",Toast.LENGTH_SHORT).show();
                    AddCustomItinerary();
                }
            });
        }
    }

    private void triggerGeoFence(){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                InAppDBHelper dbHelper = new InAppDBHelper(MainActivity.this);
                dbHelper.checkForUpdate();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    {
                        HIQLocationManager locmanager = new HIQLocationManager(MainActivity.this);
                        locmanager.setLocationResponseHandler(new HIQLocationManager.LocationResponseHandler() {
                            @Override
                            public void onLocationAvailable(final Location oLocation, boolean plausible) {
                                GeoFenceHelper.triggerGeoFence(MainActivity.this, oLocation.getLatitude(), oLocation.getLongitude());
                            }

                            @Override
                            public void onLocationFailed() {

                            }
                        });
                        locmanager.GetMyLocation(false, true);
                    }
                }catch (Exception e){
                    ExceptionUtils.logException(e);
                }
            }
        }.execute();

    }


}
