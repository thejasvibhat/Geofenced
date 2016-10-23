package holidayiq.com.geofenced.geofence;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.squareup.picasso.Picasso;

import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import holidayiq.com.geofenced.ItemAdapter;
import holidayiq.com.geofenced.ItenaryPreparationHelper;
import holidayiq.com.geofenced.PhotoObject;
import holidayiq.com.geofenced.R;
import holidayiq.com.geofenced.gsons.IternaryList;

public class AddItineraray extends AppCompatActivity {
    private static long mEnterTime = 0;
    private static Calendar oDate;
    private static int mDuration = 0;
    private static AddItineraray mActivity;
    private TwoWayView oListView;
    private static int mYear,mMonth,mDay,mHours,mMinutes;
    private ItemAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_itineraray);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        mActivity = this;
    }
    private void Submit()
    {
        IternaryList oList = new IternaryList();
        oList.setEnterTime(mEnterTime);
        oList.setExitTime(mEnterTime + mDuration);
        oList.setObjectId(Integer.valueOf(getIntent().getStringExtra("id")));
        oList.setObjectName(getIntent().getStringExtra("name"));
        if(mAdapter != null)
        {
            List<PhotoObject> oArr = mAdapter.getNewsArrayLst();
            ArrayList<PhotoObject> oAcceptObjArr = new ArrayList<>();
            if(oArr.size() > 0)
            {
                for (PhotoObject obj:oArr
                     ) {
                    if(obj.isSelected)
                    {
                        oAcceptObjArr.add(obj);
                    }
                }
            }
            oList.setPhotos(oAcceptObjArr);
        }
        
    }
    private void initViews()
    {
        Button oBut = (Button) findViewById(R.id.submit);
        oBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit();
            }
        });

        oListView = (TwoWayView) findViewById(R.id.list);
        TextView oName = (TextView) findViewById(R.id.placeName);
        oName.setText(getIntent().getStringExtra("name"));

        Button enterTime = (Button) findViewById(R.id.timeEnterBut);
        enterTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "Enter");

            }
        });
        Spinner spinner = (Spinner) findViewById(R.id.duration_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.durations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(Color.BLACK);
                mDuration = (int)(1 + (position)*0.5)*30*60*1000;
                populatePhotos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public void populatePhotos()
    {
        if(mEnterTime != 0 && mDuration != 0)
        {
            setPhotoRecycler(oListView,String.valueOf(mEnterTime/1000), String.valueOf((mEnterTime + mDuration)/1000));
        }
    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(), getTag());
            mYear = year;
            mMonth = month;
            mDay = day;

        }
    }
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHours = hourOfDay;
            mMinutes = minute;
            Calendar oDate = Calendar.getInstance();
            oDate.set(mYear,mMonth,mDay,mHours,mMinutes);
            if(getTag().equals("Enter"))
            {
                Date time = oDate.getTime();
                Button enterTime = (Button) mActivity.findViewById(R.id.timeEnterBut);
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd hh:mm");

                enterTime.setText(simpleDateFormat.format(time));
                mEnterTime = time.getTime();
                mActivity.populatePhotos();
            }


        }
    }
    private void setPhotoRecycler(final TwoWayView mRecyclerView,final String startTime , final String endTime){
        new AsyncTask<Void,Void,Void>(){
            ArrayList<PhotoObject> list = new ArrayList<>();

            @Override
            protected Void doInBackground(Void... params) {
                list= ItenaryPreparationHelper.getPhotosBetweenTimeStamp(AddItineraray.this,startTime,endTime);
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
                mAdapter = new ItemAdapter(AddItineraray.this, mRecyclerView, R.layout.activity_main, list,0,null);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @SuppressLint("NewApi") @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                        boolean pauseOnScroll = false; // or true
                        boolean pauseOnFling = false; // or false
                        final Picasso picasso = Picasso.with(AddItineraray.this);
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
}

