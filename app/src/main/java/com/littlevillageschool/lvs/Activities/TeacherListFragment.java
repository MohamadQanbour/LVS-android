package com.littlevillageschool.lvs.Activities;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.littlevillageschool.lvs.Adapters.TeachersAdapter;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.Misc;
import com.littlevillageschool.lvs.R;

/**
 * Created by Raafat Alhoumaidy on 10/21/2016.
 */

public class TeacherListFragment extends android.support.v4.app.ListFragment {

    TeachersAdapter adapter;
    public TeacherListFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TeachersAdapter(getActivity(), R.layout.teacher_item_layout, LvsApplication.misc.getTeacherList());
        LvsApplication.misc.setCallBack(new Misc.CallBack() {
            @Override
            public void onSucc(Misc.ServiceType serviceType) {
                adapter.notifyDataSetChanged();
                TeacherListFragment.this.setListShown(true);
                setListAdapter(adapter);

            }

            @Override
            public void onFail(Misc.ServiceType serviceType, String errMsg) {
                Toast.makeText(LvsApplication.APP_CTX,errMsg,Toast.LENGTH_SHORT).show();
            }
        });
        LvsApplication.misc.callService(Misc.ServiceType.TEACHER_LIST);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListShown(true);
    }
}
