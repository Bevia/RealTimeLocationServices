package com.corebaseit.realtimelocationservices.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.corebaseit.realtimelocationservices.R;
import com.corebaseit.realtimelocationservices.models.DistanceModel;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by vbevia on 09/07/16.
 */
public class LocationAdapter extends ArrayAdapter<DistanceModel> {

    private Context context;
    private List<DistanceModel> dmodel;

    public LocationAdapter(Context context, int resource, List<DistanceModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.dmodel = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //trying to reuse a recycled view
        final ViewHolderItem viewHolder;
        final DistanceModel distance = dmodel.get(position);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.locations_item_layout, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.textViewCity = (TextView) convertView.findViewById(R.id.textView1);
            viewHolder.textViewDistance = (TextView) convertView.findViewById(R.id.textView2);
            viewHolder.textViewAddress = (TextView) convertView.findViewById(R.id.textView3);
            //viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView1);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        viewHolder.textViewCity.setText(distance.getCity());
        viewHolder.textViewAddress.setText(distance.getAddress());

        if(distance.getDistance() < 1 ) //Take constrain out, now it will show even over long distances!
          //  if(distance.getDistance() < 1 || distance.getDistance() > 20000000)
        {viewHolder.textViewDistance.setText("");
        }else {
            if(distance.getDistance() > 1000) {
                viewHolder.textViewDistance.setText(new DecimalFormat("##.##").format(distance.getDistance()/1000) + "  km");
            }else{
                viewHolder.textViewDistance.setText(new DecimalFormat("##.##").format(distance.getDistance()) + "  metros");
            }
        }

     /*   Glide.with(context)
                .load(distance.getPictureUrl())
                .transform(new CircleTransform(context))
                .crossFade()
                .into(viewHolder.image);
*/
        return convertView;
    }

    static class ViewHolderItem {
        private TextView textViewCity;
        private TextView textViewAddress;
        private TextView textViewDistance;
        //private ImageView image;
    }
}

