package holidayiq.com.geofenced;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;

import holidayiq.com.geofenced.geofence.ExceptionUtils;
import holidayiq.com.geofenced.geofence.GeoFenceHelper;
import holidayiq.com.geofenced.geofence.HIQConstant;
import holidayiq.com.geofenced.geofence.HIQLocationManager;
import holidayiq.com.geofenced.geofence.InAppDBHelper;
import holidayiq.com.geofenced.gsons.IntermediateDestination;
import holidayiq.com.geofenced.gsons.ItenaryParent;
import holidayiq.com.geofenced.gsons.IternaryList;

public class EditFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_render);
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                ItenaryPreparationHelper.prepareItenary(EditFormActivity.this);
                return null;
            }
        }.execute();
        triggerGeoFence();
        LinearLayout content_layout = (LinearLayout) findViewById(R.id.content_layout);
        Gson gson = new Gson();
        ItenaryParent iternary = gson.fromJson(HIQConstant.json_obj, ItenaryParent.class);

        int bottomPad = HIQConstant.dpToPx(16,this);
        int imagePad = HIQConstant.dpToPx(8,this);
        for (int i=0;i<iternary.getIternaryList().size();i++){
            IternaryList obj = iternary.getIternaryList().get(i);
            View blanket_layout = getLayoutInflater().inflate(R.layout.blanket_itenary_object, null);
            ImageView type_icon = (ImageView) blanket_layout.findViewById(R.id.type_icon);
            LinearLayout cont = (LinearLayout) blanket_layout.findViewById(R.id.content_layout);
            View header_layout = null;
            View description_layout = null;
            View image_layout =null;
            if(obj.getType().equalsIgnoreCase("journey")){
                header_layout = getLayoutInflater().inflate(R.layout.header_info, null);
                TextView ObjName = (TextView) header_layout.findViewById(R.id.header_text_1);
                TextView time = (TextView) header_layout.findViewById(R.id.header_text_2);
                time.setText(HIQConstant.getAmPmTime(obj.getStartTime()));
                ObjName.setText(obj.getStartDestination()+" ");
                description_layout = getLayoutInflater().inflate(R.layout.description_text, null);
                image_layout = getLayoutInflater().inflate(R.layout.single_image_layput, null);
                type_icon.setImageResource(R.drawable.ic_p_flight_icon);

            }else if(obj.getType().equalsIgnoreCase("destination")){
                header_layout = getLayoutInflater().inflate(R.layout.header_info, null);
                TextView ObjName = (TextView) header_layout.findViewById(R.id.header_text_1);
                ObjName.setText(obj.getObjectName()+" ");
                TextView time = (TextView) header_layout.findViewById(R.id.header_text_2);
                if(obj.getEnterTime()!=0) {
                    time.setText(HIQConstant.getAmPmTime(obj.getEnterTime()));
                }else{
                    time.setText(HIQConstant.getAmPmTime(obj.getExitTime()));
                }
                description_layout = getLayoutInflater().inflate(R.layout.description_text, null);
                image_layout = getLayoutInflater().inflate(R.layout.three_image_layout, null);
                type_icon.setImageResource(R.drawable.destination_search_icon);
            }else if(obj.getType().equalsIgnoreCase("hotel")){
                header_layout = getLayoutInflater().inflate(R.layout.header_info, null);
                TextView ObjName = (TextView) header_layout.findViewById(R.id.header_text_1);
                ObjName.setText(obj.getObjectName()+" ");
                TextView time = (TextView) header_layout.findViewById(R.id.header_text_2);
                if(obj.getEnterTime()!=0) {
                    time.setText(HIQConstant.getAmPmTime(obj.getEnterTime()));
                }else{
                    time.setText(HIQConstant.getAmPmTime(obj.getExitTime()));
                }
                description_layout = getLayoutInflater().inflate(R.layout.description_text, null);
                image_layout = getLayoutInflater().inflate(R.layout.photo_selection, null);
                TwoWayView mRecyclerView = (TwoWayView) image_layout.findViewById(R.id.list);
                setPhotoRecycler(mRecyclerView);
                type_icon.setImageResource(R.drawable.hotel_search_icon);
            }else if(obj.getType().equalsIgnoreCase("ss")){
                header_layout = getLayoutInflater().inflate(R.layout.header_info, null);
                TextView ObjName = (TextView) header_layout.findViewById(R.id.header_text_1);
                ObjName.setText(obj.getObjectName()+" ");
                TextView time = (TextView) header_layout.findViewById(R.id.header_text_2);
                if(obj.getEnterTime()!=0) {
                    time.setText(HIQConstant.getAmPmTime(obj.getEnterTime()));
                }else{
                    time.setText(HIQConstant.getAmPmTime(obj.getExitTime()));
                }
                description_layout = getLayoutInflater().inflate(R.layout.description_text, null);
                image_layout = getLayoutInflater().inflate(R.layout.three_image_layout, null);
                type_icon.setImageResource(R.drawable.sight_seeing_search_icon);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10,0,10,bottomPad);
            blanket_layout.setLayoutParams(params);
            if(content_layout==null){
                return;
            }
            content_layout.addView(blanket_layout);
            if(header_layout!=null) {
                cont.addView(header_layout);
            }
            if(description_layout!=null) {
                cont.addView(description_layout);
                View add_desc = description_layout.findViewById(R.id.add_description);
                add_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChangeLangDialog();
                    }
                });
            }
            if(image_layout!=null) {
                LinearLayout.LayoutParams params_img = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params_img.setMargins(0,imagePad,0,0);
                image_layout.setLayoutParams(params_img);
                cont.addView(image_layout);
            }
        }



    }

    private void setPhotoRecycler(final TwoWayView mRecyclerView){
        new AsyncTask<Void,Void,Void>(){
            ArrayList<PhotoObject> list = new ArrayList<>();

            @Override
            protected Void doInBackground(Void... params) {
                list= ItenaryPreparationHelper.getPhotosBetweenTimeStamp(EditFormActivity.this,"1414065979","1477224380");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLongClickable(false);
                mRecyclerView.setOrientation(org.lucasr.twowayview.TwoWayLayoutManager.Orientation.HORIZONTAL);
                final Drawable divider = getResources().getDrawable(R.drawable.divider);
                mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));
                ItemAdapter mAdapter = new ItemAdapter(EditFormActivity.this, mRecyclerView, R.layout.activity_main, list);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @SuppressLint("NewApi") @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                        boolean pauseOnScroll = false; // or true
                        boolean pauseOnFling = false; // or false
                        final Picasso picasso = Picasso.with(EditFormActivity.this);
                        switch (scrollState) {
                            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                                picasso.resumeTag("mylist");
                                break;
                            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                                if (pauseOnScroll) {
                                    picasso.pauseTag("mylist");

                                }
                                break;
                            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                                if (pauseOnFling) {
                                    picasso.pauseTag("mylist");

                                }
                                break;
                        }


                    }

                    @SuppressLint("NewApi") @Override
                    public void onScrolled(RecyclerView recyclerView, int i, int i2) {

                    }
                });
            }
        }.execute();

    }

    private void triggerGeoFence(){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                InAppDBHelper dbHelper = new InAppDBHelper(EditFormActivity.this);
                dbHelper.checkForUpdate();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    {
                        HIQLocationManager locmanager = new HIQLocationManager(EditFormActivity.this);
                        locmanager.setLocationResponseHandler(new HIQLocationManager.LocationResponseHandler() {
                            @Override
                            public void onLocationAvailable(final Location oLocation, boolean plausible) {
                                GeoFenceHelper.triggerGeoFence(EditFormActivity.this, oLocation.getLatitude(), oLocation.getLongitude());
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

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setMessage("Enter Description here");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


}
