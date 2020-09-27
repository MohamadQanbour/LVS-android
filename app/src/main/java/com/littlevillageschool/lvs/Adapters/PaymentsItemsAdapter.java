package com.littlevillageschool.lvs.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.littlevillageschool.lvs.Model.Parent;
import com.littlevillageschool.lvs.R;

import java.util.List;

/**
 * Created by Raafat Alhoumaidy on 11/5/2016.
 */

public class PaymentsItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<Parent.Payment.PaymentItem> data;
    private Context ctx;
    private int res;
    private LayoutInflater inflater;

    public PaymentsItemsAdapter(Context context, int resource, List<Parent.Payment.PaymentItem> objects) {
        this.data = objects;
        this.ctx = context;
        this.res = resource;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(res, parent, false);

        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        View convertView = holder.itemView;

        TextView idTxtV = (TextView)convertView.findViewById(R.id.paymentNumberTxtV);
        TextView paymentValueTxtV = (TextView) convertView.findViewById(R.id.paymentValueTxtV);
        TextView paymentDateTxtV  = (TextView) convertView.findViewById(R.id.paymentDateTxtV);

        Parent.Payment.PaymentItem currItem =data.get(position);

        idTxtV.setText(String.valueOf(currItem.getId()));
        paymentDateTxtV.setText(currItem.getDate());
        paymentValueTxtV.setText(String.valueOf(currItem.getValue()));

    }
}
