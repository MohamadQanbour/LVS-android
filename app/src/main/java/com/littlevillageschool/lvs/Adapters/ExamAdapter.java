package com.littlevillageschool.lvs.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.littlevillageschool.lvs.Model.Student;
import com.littlevillageschool.lvs.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Alalaa Center on 27/08/2016.
 */
public class ExamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Student.Material.Score> data;
    private Context ctx;
    private int res;
    private LayoutInflater inflater;

    public ExamAdapter(Context ctx, int res,List<Student.Material.Score> data) {
        this.data = data;
        this.ctx = ctx;
        this.res = res;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(res,parent,false);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        View view = holder.itemView;

        Student.Material.Score curExam = data.get(position);
        TextView examTitleTxtv = (TextView) view.findViewById(R.id.examTitleTxtv);
        TextView examScoreTxtV = (TextView) view.findViewById(R.id.examMarkTxtV);

        examScoreTxtV.setText(curExam.getMyScore()+"");
        examTitleTxtv.setText(curExam.getTestTitle());

        if(curExam.getExamType() == Student.Material.Score.ExamType.AVG){
            examTitleTxtv.setBackgroundColor(Color.parseColor("#ffe861"));
            examTitleTxtv.setTextColor(Color.BLACK);
        }else if(curExam.getExamType() == Student.Material.Score.ExamType.SUM){
            examTitleTxtv.setBackgroundColor(Color.parseColor("#ffe861"));
            examTitleTxtv.setTextColor(Color.BLACK);
        }

    }



}
