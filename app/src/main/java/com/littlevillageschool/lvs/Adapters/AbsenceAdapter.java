package com.littlevillageschool.lvs.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.littlevillageschool.lvs.R;

import java.util.List;

/**
 * Created by Alalaa Center on 15/08/2016.
 */
public class AbsenceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> data;
    private Context ctx;
    private LayoutInflater inflater;
    private int res;

    public AbsenceAdapter(Context _ctx,int _res,List<String> _data){
        this.data = _data;
        this.ctx = _ctx;
        this.res = _res;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(res,parent,false);

        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view =  holder.itemView;
        TextView absenseDateTxtV = (TextView)view.findViewById(R.id.absenseDateTxtV);
        absenseDateTxtV.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
