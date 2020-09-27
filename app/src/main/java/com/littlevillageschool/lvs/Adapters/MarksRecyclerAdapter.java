package com.littlevillageschool.lvs.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.littlevillageschool.lvs.Model.Student;
import com.littlevillageschool.lvs.R;

import java.util.List;
import java.util.Random;

/**
 * Created by Alalaa Center on 30/07/2016.
 */
public class MarksRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Student.Material> data;
    private Context ctx;
    private int res;
    private LayoutInflater inflater;
    private Random rand;

    public MarksRecyclerAdapter(Context ctx, int res,List<Student.Material> data){
        this.ctx = ctx;
        this.res = res;
        this.data = data;
        this.inflater = LayoutInflater.from(ctx);
        this.rand = new Random();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(res,parent,false);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view = holder.itemView;

        Student.Material curItem = data.get(position);

        TextView matTitleTxtView = (TextView)view.findViewById(R.id.subjectTitleTxtV);
        RecyclerView examRecyclerView = (RecyclerView) view.findViewById(R.id.examsRecyclerView);

//        int spacingInPixels = ctx.getResources().getDimensionPixelSize(R.dimen.spacing);
//        examRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        GridLayoutManager layoutManager = new GridLayoutManager(ctx,3);
        examRecyclerView.setLayoutManager(layoutManager);
        examRecyclerView.setNestedScrollingEnabled(true);
        examRecyclerView.setAdapter(curItem.getExamAdapter() );

        matTitleTxtView.setText(curItem.getMaterialTitle());
       // matTitleTxtView.setBackgroundColor(randomColor);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
