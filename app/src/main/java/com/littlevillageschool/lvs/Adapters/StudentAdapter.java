package com.littlevillageschool.lvs.Adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.littlevillageschool.lvs.Model.Student;
import com.littlevillageschool.lvs.R;

import java.util.List;

/**
 * Created by Alalaa Center on 31/07/2016.
 */
public class StudentAdapter extends ArrayAdapter<Student> {

    private List<Student> data;
    private Context ctx;
    private int res;
    private LayoutInflater inflater;

    public StudentAdapter(Context context, int resource, List<Student> objects) {
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
    public Student getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public int getPosition(Student item) {
        return this.data.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(res,parent,false);

        TextView name = (TextView) convertView;
        name.setTextColor(Color.WHITE);
        name.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));
        name.setText(data.get(position).getFullName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(res,parent,false);

        TextView name = (TextView) convertView;
        name.setTextColor(Color.WHITE);
        name.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));
        name.setText(data.get(position).getFullName());

        return convertView;
    }
}
