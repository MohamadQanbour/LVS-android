package com.littlevillageschool.lvs.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.littlevillageschool.lvs.Model.Misc;
import com.littlevillageschool.lvs.R;

import java.util.List;

/**
 * Created by Raafat Alhoumaidy on 10/21/2016.
 */

public class StaffsAdapter extends ArrayAdapter<Misc.Staff> {

    private List<Misc.Staff> data;
    private Context ctx;
    private int res;
    private LayoutInflater inflater;

    public StaffsAdapter(Context context, int resource, List<Misc.Staff> objects) {
        super(context, resource, objects);
        this.data = objects;
        this.ctx = context;
        this.res = resource;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Nullable
    @Override
    public Misc.Staff getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public int getPosition(Misc.Staff item) {
        return this.data.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = this.inflater.inflate(res,parent,false);

        TextView titleTxtV = ((TextView) convertView.findViewById(R.id.titleTxtV));
        TextView descriptionTxtV = (TextView)convertView.findViewById(R.id.descriptionTxtV);

        titleTxtV.setText(data.get(position).getRoleName());
        descriptionTxtV.setText(data.get(position).getStaffsNames());

        return convertView;
    }
}
