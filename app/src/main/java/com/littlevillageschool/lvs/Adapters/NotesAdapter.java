package com.littlevillageschool.lvs.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.littlevillageschool.lvs.Model.Student;
import com.littlevillageschool.lvs.R;

import java.util.List;

/**
 * Created by Alalaa Center on 15/08/2016.
 */
public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Student.Note> data;
    private Context ctx;
    private LayoutInflater inflater;
    private int res;

    public NotesAdapter(Context _ctx, int _res, List<Student.Note> _data) {
        this.data = _data;
        this.ctx = _ctx;
        this.res = _res;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(res, parent, false);

        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view = holder.itemView;

        TextView senderTxtV = (TextView) view.findViewById(R.id.senderNameTextV);
        TextView noteTxtV = (TextView) view.findViewById(R.id.noteTxtV);
        TextView noteDateTxtV = (TextView) view.findViewById(R.id.noteDateTxtV);

        Student.Note currItem = data.get(position);

        senderTxtV.setText(currItem.getEnderName());
        noteDateTxtV.setText(currItem.getNoteDate());
        noteTxtV.setText(currItem.getNoteText());

        int noteType = currItem.getNoteType();
        if(noteType == 2)
            ((CardView)view).setCardBackgroundColor(ctx.getResources().getColor(R.color.positive));
        else
            ((CardView)view).setCardBackgroundColor(ctx.getResources().getColor(R.color.negative));


    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
