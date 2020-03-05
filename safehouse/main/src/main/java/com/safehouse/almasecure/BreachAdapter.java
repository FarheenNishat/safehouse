package com.safehouse.almasecure;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.safehouse.almasecure.model.PawnedModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.blinkt.openvpn.R;

public class BreachAdapter extends RecyclerView.Adapter<BreachAdapter.MyViewHolder> {
    private ArrayList<PawnedModel> mDataset;
    private Context mContext;
    MixpanelAPI mixpanel;
    Typeface face;
    Typeface boldFace;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView date;
        public RelativeLayout rlParentView;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.tv_title);
            this.date = (TextView) itemView.findViewById(R.id.tv_date);
            this.rlParentView = itemView.findViewById(R.id.rlParentView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BreachAdapter(ArrayList<PawnedModel> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
        mixpanel =
                MixpanelAPI.getInstance(mContext.getApplicationContext(), mContext.getResources().getString(R.string.MIXPANAL_TOKEN));
        face =  Typeface.createFromAsset(context.getAssets(),
                "titillium_regular.otf");
        boldFace = Typeface.createFromAsset(context.getAssets(),
                "titillium_bold.otf");
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BreachAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.unsecure_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(listItem);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(mDataset.get(position).getName());
        holder.title.setTypeface(boldFace);
        holder.date.setText(mDataset.get(position).getBreachDate());
        holder.date.setTypeface(boldFace);
        holder.rlParentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,    ReccomendationActivity.class);
                String message = mDataset.get(position).getObject().toString();
                intent.putExtra("jsonObject", message);
                JSONObject props = new JSONObject();
                try {
                    props.put(mContext.getString(R.string.analytics_from), mContext.getString(R.string.am_i_exposed_screen));
                    props.put(mContext.getString(R.string.analytics_to)  , mContext.getString(R.string.am_i_exposed_specific_breach_screen));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track("Am I Exposed View Specific Report Appeared", props);
                mContext.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
