/**
 *
 */
package holidayiq.com.geofenced.geofence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import holidayiq.com.geofenced.R;
import holidayiq.com.geofenced.gsons.elemets;

import java.util.ArrayList;

/**
 * @author charan
 */
public class OfflineSearchAdapter extends BaseAdapter {

    private Context mContext = null;
    private final OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            //int resultId = Integer.valueOf(String.valueOf(v.getTag(R.id.dest_name)));
            String type = (String) v.getTag(R.id.search_type);
            String name = (String) v.getTag(R.id.search_name);
            String id = (String) v.getTag(R.id.search_header);
            String parent_id = (String) v.getTag(R.id.seach_sep);



            ((SearchScreen) mContext).offlineResultsClicked(type, id,name,parent_id);

        }
    };
    private ArrayList<elemets> mOfflineSearchResults = null;

    public OfflineSearchAdapter(Context context) {
        mContext = context;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        if (mOfflineSearchResults != null) {
            return mOfflineSearchResults.size();
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int arg0) {
        return mOfflineSearchResults.get(arg0);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SearchHolder holder;
        elemets osr = mOfflineSearchResults.get(position);
        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(mContext);
            convertView = li.inflate(R.layout.search_items, parent, false);
            holder = new SearchHolder();
            holder.searchName = (TextView) convertView.findViewById(R.id.search_name);
            holder.searchCity = (TextView) convertView.findViewById(R.id.search_city);
            holder.searchImage = (ImageView) convertView.findViewById(R.id.search_image);
            convertView.findViewById(R.id.time_diff_text).setVisibility(View.INVISIBLE);
            convertView.setTag(holder);

        } else {
            holder = (SearchHolder) convertView.getTag();
        }

        if (osr.type == "destination") {
            holder.searchImage.setImageResource(R.drawable.destination_search_icon);
        } else if (osr.type == "hotel") {
            holder.searchImage.setImageResource(R.drawable.hotel_search_icon);
        } else {
            holder.searchImage.setImageResource(R.drawable.sight_seeing_search_icon);
        }

        holder.searchName.setText(osr.name);
        holder.searchCity.setText("India");
        convertView.setTag(R.id.search_name, osr.name);
        convertView.setTag(R.id.search_header, osr.id);
        convertView.setTag(R.id.search_type, osr.type);
        convertView.setTag(R.id.seach_sep, osr.parent_id);

        convertView.setOnClickListener(clickListener);

        return convertView;
    }

    public void setData(ArrayList<elemets> offlineSearchResults) {

        if (mOfflineSearchResults == null) {
            mOfflineSearchResults = new ArrayList<>();

        } else {
            mOfflineSearchResults.clear();
        }
        mOfflineSearchResults.addAll(offlineSearchResults);
    }

    public class SearchHolder {
        private TextView searchName, searchCity;
        private ImageView searchImage;

    }

}
