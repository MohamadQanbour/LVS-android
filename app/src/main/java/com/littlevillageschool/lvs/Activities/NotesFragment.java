package com.littlevillageschool.lvs.Activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.littlevillageschool.lvs.Adapters.StudentAdapter;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.Parent;
import com.littlevillageschool.lvs.Model.Student;
import com.littlevillageschool.lvs.Model.User;
import com.littlevillageschool.lvs.R;

import java.util.List;

/**
 * Created by Raafat Alhoumaidy on 10/23/2016.
 */

public class NotesFragment extends android.support.v4.app.Fragment {
    private RecyclerView notesRecyclerView;
    private Spinner childsSpinner;
    private StudentAdapter studentAdapter;
    private ProgressBar loadingPB;
    private int prevChildSelected = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_marks, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        notesRecyclerView = (RecyclerView) view.findViewById(R.id.marksRecyclerView);
        childsSpinner = (Spinner) view.findViewById(R.id.studentSpinner);
        loadingPB = (ProgressBar) view.findViewById(R.id.loadingPB);

        notesRecyclerView.setHasFixedSize(true);
        GridLayoutManager lnLayoutMgr;
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE)
            lnLayoutMgr = new GridLayoutManager(getActivity(), 2);
        else
            lnLayoutMgr = new GridLayoutManager(getActivity(), 1);

        notesRecyclerView.setLayoutManager(lnLayoutMgr);

        if (LvsApplication.currUser.getType() == User.UserType.STUDENT) {
            notesRecyclerView.setAdapter(((Student) LvsApplication.currUser).getNotesAdapter());
            List<Student.Note> notes = ((Student) LvsApplication.currUser).getNotes();
            loadingPB.setVisibility(View.VISIBLE);

            if (notes == null || notes.size() == 0) {
                LvsApplication.currUser.callService(User.UserService.NOTES);
                LvsApplication.currUser.setCallBack(new User.MyCallBack() {
                    @Override
                    public void onSucc(User.UserService service) {
                        loadingPB.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFail(User.UserService service, String errorMsg) {
                        LvsApplication.currUser.callService(User.UserService.NOTES);
                    }
                });
            }

            else
                loadingPB.setVisibility(View.GONE);

        } else {
            childsSpinner.setVisibility(View.VISIBLE);
            studentAdapter = new StudentAdapter(getActivity()
                    , android.R.layout.simple_list_item_1,
                    ((Parent) LvsApplication.currUser).getStudentList());
            childsSpinner.setAdapter(studentAdapter);

            childsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                    if (i == prevChildSelected)
                        return;
                    loadingPB.setVisibility(View.VISIBLE);
                    if (((Parent) LvsApplication.currUser).getStudentList().get(i).getNotes().size() == 0)
                        ((Parent) LvsApplication.currUser).getStudentList().get(i).callService(User.UserService.NOTES);
                    else {
                        loadingPB.setVisibility(View.GONE);
                        notesRecyclerView.setAdapter(
                                ((Parent) LvsApplication.currUser).getStudentList().get(0).getNotesAdapter());
                    }
                    ((Parent) LvsApplication.currUser).getStudentList().get(i).setCallBack(new User.MyCallBack() {
                        @Override
                        public void onSucc(User.UserService service) {
                            loadingPB.setVisibility(View.GONE);
                            notesRecyclerView.setAdapter(
                                    ((Parent) LvsApplication.currUser).getStudentList().get(i).getNotesAdapter());
                        }

                        @Override
                        public void onFail(User.UserService service, String errorMsg) {
                            ((Parent) LvsApplication.currUser).getStudentList().get(i).callService(User.UserService.NOTES);
                        }
                    });

                    prevChildSelected = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            if (studentAdapter.getCount() > 0) {
                childsSpinner.setSelection(0);
                loadingPB.setVisibility(View.VISIBLE);
                if (((Parent) LvsApplication.currUser).getStudentList().get(0).getNotes().size() == 0)
                    ((Parent) LvsApplication.currUser).getStudentList().get(0).callService(User.UserService.NOTES);
                else {
                    loadingPB.setVisibility(View.GONE);
                    notesRecyclerView.setAdapter(
                            ((Parent) LvsApplication.currUser).getStudentList().get(0).getNotesAdapter());
                }
                ((Parent) LvsApplication.currUser).getStudentList().get(0).setCallBack(new User.MyCallBack() {
                    @Override
                    public void onSucc(User.UserService service) {
                        loadingPB.setVisibility(View.GONE);
                        notesRecyclerView.setAdapter(
                                ((Parent) LvsApplication.currUser).getStudentList().get(0).getNotesAdapter());
                    }

                    @Override
                    public void onFail(User.UserService service, String errorMsg) {
                        ((Parent) LvsApplication.currUser).getStudentList().get(0).callService(User.UserService.NOTES);
                    }
                });
            }
        }


    }
}
