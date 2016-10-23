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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import holidayiq.com.geofenced.geofence.ExceptionUtils;
import holidayiq.com.geofenced.geofence.GeoFenceHelper;
import holidayiq.com.geofenced.geofence.HIQConstant;
import holidayiq.com.geofenced.geofence.HIQLocationManager;
import holidayiq.com.geofenced.geofence.InAppDBHelper;
import holidayiq.com.geofenced.gsons.IntermediateDestination;
import holidayiq.com.geofenced.gsons.ItenaryParent;
import holidayiq.com.geofenced.gsons.IternaryList;

public class EditFormActivity extends AppCompatActivity implements ItemAdapter.savePhotoObject {

    LinearLayout content_layout;
    ItenaryParent listObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Reservoir.init(this, 1004800); //in bytes
        } catch (Exception e) {
            //failure
        }
        setContentView(R.layout.scroll_render);
        content_layout = (LinearLayout) findViewById(R.id.content_layout);
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                ItenaryPreparationHelper.prepareItenary(EditFormActivity.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                reloadData();
            }
        }.execute();
        triggerGeoFence();


    }

    private void populateData(ItenaryParent iternary){
        int bottomPad = HIQConstant.dpToPx(16,this);
        int imagePad = HIQConstant.dpToPx(8,this);
        content_layout.removeAllViews();
        for (int i=0;i<iternary.getIternaryList().size();i++){
            final IternaryList obj = iternary.getIternaryList().get(i);
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
                image_layout = getLayoutInflater().inflate(R.layout.photo_selection, null);
                TwoWayView mRecyclerView = (TwoWayView) image_layout.findViewById(R.id.list);
                setPhotoRecycler(mRecyclerView,i);
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
                image_layout = getLayoutInflater().inflate(R.layout.photo_selection, null);
                TwoWayView mRecyclerView = (TwoWayView) image_layout.findViewById(R.id.list);
                setPhotoRecycler(mRecyclerView,i);
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
                setPhotoRecycler(mRecyclerView,i);
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
                image_layout = getLayoutInflater().inflate(R.layout.photo_selection, null);
                TwoWayView mRecyclerView = (TwoWayView) image_layout.findViewById(R.id.list);
                setPhotoRecycler(mRecyclerView,i);
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
                Switch toggg = (Switch) header_layout.findViewById(R.id.enable);
                if(obj.isEnable()){
                    toggg.setChecked(true);
                }else{
                    toggg.setChecked(false);
                }
                final int finalI1 = i;
                toggg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        listObj.getIternaryList().get(finalI1).setEnable(isChecked);
                        try {
                            Reservoir.put("dataObj",listObj);
                            //reloadData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                cont.addView(header_layout);
            }
            if(description_layout!=null) {
                cont.addView(description_layout);
                View add_desc = description_layout.findViewById(R.id.add_description);
                TextView description_text_1 = (TextView) description_layout.findViewById(R.id.description_text_1);
                if(obj.getDescription()==null || obj.getDescription().isEmpty()){
                    add_desc.setVisibility(View.VISIBLE);
                    description_text_1.setVisibility(View.GONE);
                }else{
                    add_desc.setVisibility(View.GONE);
                    description_text_1.setVisibility(View.VISIBLE);
                    description_text_1.setText(obj.getDescription());
                }
                final int finalI = i;
                description_text_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChangeLangDialog(finalI);
                    }
                });
                add_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChangeLangDialog(finalI);
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

    private void setPhotoRecycler(final TwoWayView mRecyclerView, final int pos){
        new AsyncTask<Void,Void,Void>(){
            ArrayList<PhotoObject> list = new ArrayList<>();

            @Override
            protected Void doInBackground(Void... params) {
                if(listObj.getIternaryList().get(pos).getPhotos()==null || listObj.getIternaryList().get(pos).getPhotos().isEmpty() ) {
                    list = ItenaryPreparationHelper.getPhotosBetweenTimeStamp(EditFormActivity.this, "1474645976", "1477224380");
                }else{
                    list = (ArrayList<PhotoObject>) listObj.getIternaryList().get(pos).getPhotos();
                }
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
                ItemAdapter mAdapter = new ItemAdapter(EditFormActivity.this, mRecyclerView, R.layout.activity_main, list,pos,EditFormActivity.this);
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

    private void reloadData(){
        Gson gson = new Gson();
        final ItenaryParent iternary = gson.fromJson(HIQConstant.json_obj, ItenaryParent.class);

        Reservoir.getAsync("dataObj", ItenaryParent.class, new ReservoirGetCallback<ItenaryParent>() {
            @Override
            public void onSuccess(ItenaryParent itenaryParent) {
                Toast.makeText(EditFormActivity.this,"data exists",Toast.LENGTH_LONG).show();
                listObj = itenaryParent;
                populateData(itenaryParent);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditFormActivity.this,"data not exists",Toast.LENGTH_LONG).show();
                Reservoir.putAsync("dataObj",iternary,null);
            }
        });
    }

    public void showChangeLangDialog(final int pos) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setMessage("Enter Description here");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String description = edt.getText().toString();
                    listObj.getIternaryList().get(pos).setDescription(description);
                    Reservoir.put("dataObj",listObj);
                    reloadData();
                }catch (Exception e){

                }
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


    @Override
    public void editPhotos(int pos, List<PhotoObject> newsArrayLst) {
        try{
            listObj.getIternaryList().get(pos).setPhotos(newsArrayLst);
            Reservoir.put("dataObj",listObj);
        }catch (Exception e){

        }
    }
}
