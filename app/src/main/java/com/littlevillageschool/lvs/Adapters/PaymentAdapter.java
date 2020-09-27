package com.littlevillageschool.lvs.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.littlevillageschool.lvs.Model.Parent;
import com.littlevillageschool.lvs.Model.Student;
import com.littlevillageschool.lvs.R;

import java.util.List;

/**
 * Created by Raafat Alhoumaidy on 11/5/2016.
 */

public class PaymentAdapter extends ArrayAdapter<Parent.Payment> {

    private List<Parent.Payment> data;
    private Context ctx;
    private int res;
    private LayoutInflater inflater;

    public PaymentAdapter(Context context, int resource, List<Parent.Payment> objects) {
        super(context, resource, objects);
        this.data = objects;
        this.res = resource;
        this.ctx = context;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public Parent.Payment getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public int getPosition(Parent.Payment item) {
        return this.data.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(res, parent, false);

        TextView name = (TextView) convertView;
        name.setTextColor(Color.WHITE);
        name.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));
        name.setText(data.get(position).getStudentName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(res, parent, false);

        TextView name = (TextView) convertView;
        name.setTextColor(Color.WHITE);
        name.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));
        name.setText(data.get(position).getStudentName());

        return convertView;
    }

}