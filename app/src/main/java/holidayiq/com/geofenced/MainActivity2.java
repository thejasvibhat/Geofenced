package holidayiq.com.geofenced;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Window;
import android.widget.RelativeLayout;

/**
 * Created by Manoj on 23/10/2016.
 */

public class MainActivity2 extends AppCompatActivity {
    private Context mContext;

    RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Request window feature action bar
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);

        // Get the application context
        mContext = getApplicationContext();

        // Change the action bar color
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.RED));

        // Get the widgets reference from XML layout
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Initialize a new String array
        String[] colors = {
                "Red","Green","Blue","Yellow","Magenta","Cyan","Orange",
                "Aqua","Azure","Beige","Bisque","Brown","Coral","Crimson"
        };

        /*
            StaggeredGridLayoutManager
                A LayoutManager that lays out children in a staggered grid formation. It supports
                horizontal & vertical layout as well as an ability to layout children in reverse.

                Staggered grids are likely to have gaps at the edges of the layout. To avoid these
                gaps, StaggeredGridLayoutManager can offset spans independently or move items
                between spans. You can control this behavior via setGapStrategy(int).
        */
        /*
            public StaggeredGridLayoutManager (int spanCount, int orientation)
                Creates a StaggeredGridLayoutManager with given parameters.

            Parameters
                spanCount : If orientation is vertical, spanCount is number of columns.
                    If orientation is horizontal, spanCount is number of rows.
                orientation : VERTICAL or HORIZONTAL
        */
        // Define a layout for RecyclerView
        mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Initialize a new instance of RecyclerView Adapter instance
        mAdapter = new ColorAdapter(mContext,colors);

        // Set the adapter for RecyclerView
        mRecyclerView.setAdapter(mAdapter);
    }
}

