package com.littlevillageschool.lvs.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.CardView;


import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.Email;
import com.littlevillageschool.lvs.R;

import java.util.List;

/**
 * Created by Alalaa Center on 21/07/2016.
 */
public class EmailInboxRecyclerAdapter extends RecyclerView.Adapter<EmailInboxRecyclerAdapter.MyViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public MyViewHolder(View view) {
            super(view);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            String[] menuItems =
                    LvsApplication.APP_CTX.getResources().getStringArray(R.array.emailListContextMenu);
            for (String st : menuItems) {
                menu.add(1, Menu.NONE,Menu.NONE,st);
            }

        }
    }

    private int position;

    private Context ctx;
    private List<Email> data;
    private int res;
    private LayoutInflater inflater;

    public EmailInboxRecyclerAdapter(Context _ctx, int _res, List<Email> _data) {
        this.ctx = _ctx;
        this.data = _data;
        this.res = _res;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(res, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        CardView view = (CardView) holder.itemView;
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });

        Email curr = data.get(position);

        TextView senderTxtV = (TextView) view.findViewById(R.id.emailItemSenderTxtV);
        TextView subjectTxtV = (TextView) view.findViewById(R.id.emailItemSubjectTxtV);
        TextView dateTxtV = (TextView) view.findViewById(R.id.emailItemDateTxtV);
        ImageView attachmentImgV = (ImageView) view.findViewById(R.id.emailItemAttachImgV);

        senderTxtV.setText(curr.getSender());
        subjectTxtV.setText(curr.getTitle());
        dateTxtV.setText(curr.getDate());
        if(!curr.isHasAttachments())
            attachmentImgV.setVisibility(View.INVISIBLE);
        else
            attachmentImgV.setVisibility(View.VISIBLE);

        if(!curr.isReaded())
            view.setCardBackgroundColor(Color.LTGRAY);
        else
            view.setCardBackgroundColor(Color.WHITE);

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }


}
