package com.littlevillageschool.lvs.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.littlevillageschool.lvs.Model.Email;
import com.littlevillageschool.lvs.R;

import java.util.List;

/**
 * Created by Alalaa Center on 04/09/2016.
 */
public class AttachmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Email.Attachment> data;
    private Context ctx;
    private int res;
    private LayoutInflater inflater;

    public AttachmentAdapter(Context ctx, int res, List<Email.Attachment> data) {

        this.ctx = ctx;
        this.data = data;
        this.res = res;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(res, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        View itemView = holder.itemView;

        ImageView fileIcon = ((ImageView) itemView.findViewById(R.id.fileTypeIcon));
        TextView fileName = ((TextView) itemView.findViewById(R.id.fileNameTxtV));

        int fileIconId = 0;
        try {
            fileIconId = ctx.getResources().getIdentifier(
                    "drawable/" + data.get(position).getFileType(), null, ctx.getPackageName());
        }catch (Exception e){

        }
        fileIcon.setImageResource(fileIconId);
        fileName.setText(data.get(position).getFileName());

    }
}
