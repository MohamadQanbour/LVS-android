package com.littlevillageschool.lvs.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.littlevillageschool.lvs.Adapters.AbsenceAdapter;
import com.littlevillageschool.lvs.Adapters.EmailInboxRecyclerAdapter;
import com.littlevillageschool.lvs.Adapters.MarksRecyclerAdapter;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.Email;
import com.littlevillageschool.lvs.Model.Messaging;
import com.littlevillageschool.lvs.Model.Student;
import com.littlevillageschool.lvs.Model.User;
import com.littlevillageschool.lvs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alalaa Center on 23/07/2016.
 */
public class HomeFramgent extends android.support.v4.app.Fragment
        implements Messaging.MyCallBack, User.MyCallBack, AdapterView.OnItemSelectedListener {

    private EmailInboxRecyclerAdapter inboxAdapter;
    private List<Email> lastEmails;
    private RecyclerView lastEmailsRecyclerView;
    private TextView empetyInboxTxtV;
    private ProgressBar loadingInboxPB;

    private MarksRecyclerAdapter marksAdapter;
    private List<Student.Material> mats;
    private RecyclerView marksRecyclerView;
    private TextView empetyMarksTxtV;
    private ProgressBar loadingMarksPB;

    private AbsenceAdapter absenceAdapter;
    private List<String> absenseDates;
    private RecyclerView absenceRecyclerView;
    private TextView empetyAbsenceTxtV;
    private ProgressBar loadingAbsencePB;
    private TextView absenceDaysCountTxtV;
    private TextView presentDaysCountTxtV;

    private boolean marskLoaded = false;
    private boolean inboxLoaded = false;
    private boolean abseceLoaded = false;

    private int lastChildIdx = -1;

    public HomeFramgent() {
        lastEmails = new ArrayList<>();
        inboxAdapter = new EmailInboxRecyclerAdapter(LvsApplication.APP_CTX
                , R.layout.mail_item_layout
                , lastEmails);
        mats = new ArrayList<>();
        marksAdapter = new MarksRecyclerAdapter(LvsApplication.APP_CTX
                , R.layout.mark_item_layout
                , mats);

        absenseDates = new ArrayList<>();
        absenceAdapter = new AbsenceAdapter(LvsApplication.APP_CTX
                , R.layout.absense_day_layout
                , absenseDates);


    }

    @Override
    public void onResume() {
        super.onResume();
        if (LvsApplication.messaging.getUnReadInbox() != null
                && LvsApplication.messaging.getUnReadInbox().size() > 0) {
            inboxLoaded = true;
            lastEmails.clear();
            for (int i = 0; i < 3 && i < LvsApplication.messaging.getUnReadInbox().size(); ++i) {
                lastEmails.add(LvsApplication.messaging.getUnReadInbox().get(i));
            }
            inboxAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lastEmailsRecyclerView = (RecyclerView) view.findViewById(R.id.emailsInRecyclerView);
        absenceRecyclerView = (RecyclerView) view.findViewById(R.id.absenceDatesRecyclerView);
        marksRecyclerView = (RecyclerView) view.findViewById(R.id.marksRecyclerView);
        loadingAbsencePB = (ProgressBar) view.findViewById(R.id.loadingAbsencePB);
        loadingInboxPB = (ProgressBar) view.findViewById(R.id.loadingNewInboxPB);
        loadingMarksPB = (ProgressBar) view.findViewById(R.id.loadingMarksPB);
        empetyAbsenceTxtV = (TextView) view.findViewById(R.id.absenceEmptyTxtV);
        empetyInboxTxtV = (TextView) view.findViewById(R.id.newInboxEmptyTxtV);
        empetyMarksTxtV = (TextView) view.findViewById(R.id.marksEmptyTxtV);
        absenceDaysCountTxtV = (TextView) view.findViewById(R.id.absenceDaysCountTxtV);
        presentDaysCountTxtV = (TextView) view.findViewById(R.id.presentDaysCountTxtV);

        if (abseceLoaded) {
            loadingAbsencePB.setVisibility(View.GONE);
            Student student;
            if (LvsApplication.currUser.getType() == User.UserType.STUDENT)
                student = (Student) LvsApplication.currUser;
            else
                student = LvsApplication.selectedChild;
            absenceDaysCountTxtV.setText(student.getAbsense().getAbsenseDays() +
                    " " + getString(R.string.days));
            presentDaysCountTxtV.setText(student.getAbsense().getPresentDays() +
                    " " + getString(R.string.days));

            if (absenseDates.size() > 0)
                absenceRecyclerView.setVisibility(View.VISIBLE);
            else
                empetyAbsenceTxtV.setVisibility(View.VISIBLE);
        }
        if (marskLoaded) {
            loadingMarksPB.setVisibility(View.GONE);
            if (mats.size() > 0)
                marksRecyclerView.setVisibility(View.VISIBLE);
            else
                empetyMarksTxtV.setVisibility(View.VISIBLE);
        }
        if (inboxLoaded) {
            loadingInboxPB.setVisibility(View.GONE);
            if (lastEmails.size() > 0)
                lastEmailsRecyclerView.setVisibility(View.VISIBLE);
            else
                empetyInboxTxtV.setVisibility(View.VISIBLE);
        }

        lastEmailsRecyclerView.setHasFixedSize(true);
        absenceRecyclerView.setHasFixedSize(true);
        marksRecyclerView.setHasFixedSize(true);
        marksRecyclerView.setNestedScrollingEnabled(true);

        GridLayoutManager lnLayoutMgr1;
        GridLayoutManager lnLayoutMgr2;
        GridLayoutManager lnLayoutMgr3;
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            lnLayoutMgr1 = new GridLayoutManager(getActivity(), 2);
            lnLayoutMgr2 = new GridLayoutManager(getActivity(), 2);
            lnLayoutMgr3 = new GridLayoutManager(getActivity(), 2);
        } else {
            lnLayoutMgr1 = new GridLayoutManager(getActivity(), 1);
            lnLayoutMgr2 = new GridLayoutManager(getActivity(), 1);
            lnLayoutMgr3 = new GridLayoutManager(getActivity(), 1);
        }

        lastEmailsRecyclerView.setLayoutManager(lnLayoutMgr1);
        absenceRecyclerView.setLayoutManager(lnLayoutMgr2);
        marksRecyclerView.setLayoutManager(lnLayoutMgr3);

        final GestureDetector mGestureDetector =
                new GestureDetector(getActivity(),
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                return true;
                            }
                        });
        lastEmailsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mGestureDetector.onTouchEvent(e)) {

                    int pos = rv.getChildAdapterPosition(child);
                    Email currEmail = lastEmails.get(pos);

                    currEmail.setReaded(true);
                    inboxAdapter.notifyDataSetChanged();
                    int total = LvsApplication.messaging.getTotalNewInbox();
                    LvsApplication.messaging.setTotalUnreadInbox(total - 1);
                    LvsApplication.messaging.removeFromUnreadInbox(pos);
                    lastEmails.remove(pos);
                    inboxAdapter.notifyDataSetChanged();

                    View senderTxtV = child.findViewById(R.id.emailItemSenderTxtV);
                    View titleTxtV = child.findViewById(R.id.emailItemSubjectTxtV);
                    View dateTxtV = child.findViewById(R.id.emailItemDateTxtV);

                    Intent toViewActivity = new Intent(getActivity(), EmailViewActivity.class);
                    toViewActivity.putExtra(Email.ID_KEY, currEmail.getId());
                    toViewActivity.putExtra(Email.TITLE_KEY, currEmail.getTitle());
                    toViewActivity.putExtra(Email.SENDER_KEY, currEmail.getSender());
                    toViewActivity.putExtra(Email.DATE_KEY, currEmail.getDate());


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        senderTxtV.setTransitionName(getActivity().getString(R.string.sender_text_view_transition));
                        titleTxtV.setTransitionName(getActivity().getString(R.string.title_text_view_transition));
                        dateTxtV.setTransitionName(getActivity().getString(R.string.date_text_view_transition));

                        Pair<View, String> pair1 = Pair.create(senderTxtV, senderTxtV.getTransitionName());
                        Pair<View, String> pair2 = Pair.create(titleTxtV, titleTxtV.getTransitionName());
                        Pair<View, String> pair3 = Pair.create(dateTxtV, dateTxtV.getTransitionName());

                        ActivityOptionsCompat optionsCompat;
                        optionsCompat = ActivityOptionsCompat
                                .makeSceneTransitionAnimation(getActivity(), pair1, pair2, pair3);

                        startActivity(toViewActivity, optionsCompat.toBundle());
                    } else {
                        startActivity(toViewActivity);
                    }

                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        lastEmailsRecyclerView.setAdapter(inboxAdapter);
        inboxAdapter.notifyDataSetChanged();
        absenceRecyclerView.setAdapter(absenceAdapter);
        marksRecyclerView.setAdapter(marksAdapter);
        //registerForContextMenu(lastEmailsRecyclerView);

    }

    @Override
    public void onSuccess(Messaging.MessaginService service) {
        switch (service) {
            case GET_UNREAD_INBOX:
                inboxLoaded = true;
                if (!isAdded())
                    break;
                loadingInboxPB.setVisibility(View.GONE);
                lastEmails.clear();
                for (int i = 0; i < 3 && i < LvsApplication.messaging.getUnReadInbox().size(); ++i) {
                    lastEmails.add(LvsApplication.messaging.getUnReadInbox().get(i));
                }
                inboxAdapter.notifyDataSetChanged();
                if (lastEmails.size() > 0)
                    lastEmailsRecyclerView.setVisibility(View.VISIBLE);
                else
                    empetyInboxTxtV.setVisibility(View.VISIBLE);

                break;
        }

    }

    @Override
    public void onFail(Messaging.MessaginService service) {

    }

    @Override
    public void onSucc(User.UserService service) {
        switch (service) {

            case MATERIALS:
                marskLoaded = true;
                mats.clear();

                Student student;
                if (LvsApplication.currUser.getType() == User.UserType.STUDENT)
                    student = (Student) LvsApplication.currUser;
                else
                    student = LvsApplication.selectedChild;
                if (student == null || !isAdded())
                    break;
                for (int i = 0;
                     i < 3 && i < student.getMaterials().size(); ++i)
                    mats.add(student.getMaterials().get(i));
                marksAdapter.notifyDataSetChanged();
                if (mats.size() > 0)
                    marksRecyclerView.setVisibility(View.VISIBLE);
                else
                    empetyMarksTxtV.setVisibility(View.VISIBLE);

                loadingMarksPB.setVisibility(View.GONE);

                break;

            case ABSENTS:
                abseceLoaded = true;
                Student student1;
                if (LvsApplication.currUser.getType() == User.UserType.STUDENT)
                    student1 = (Student) LvsApplication.currUser;
                else
                    student1 = LvsApplication.selectedChild;
                if (student1 == null)
                    break;
                if (!isAdded())
                    break;
                loadingAbsencePB.setVisibility(View.GONE);

                absenceDaysCountTxtV.setText(student1.getAbsense().getAbsenseDays() +
                        " " + getString(R.string.days));
                presentDaysCountTxtV.setText(student1.getAbsense().getPresentDays() +
                        " " + getString(R.string.days));
                absenseDates.clear();
                absenseDates.addAll(student1.getAbsense().getAbsenseDates());

                if (absenseDates.size() > 0) {
                    absenceRecyclerView.setVisibility(View.VISIBLE);
                    absenceAdapter.notifyDataSetChanged();
                } else
                    empetyAbsenceTxtV.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onFail(User.UserService service, String errorMsg) {

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        final int position = LvsApplication.messaging.getInboxAdapter().getPosition();
        final Email currentItem = LvsApplication.messaging.getInbox().get(position);

        final Intent toComposeEmailActivity = new Intent(getActivity(), ComposeEmailActivity.class);
        toComposeEmailActivity.putExtra("emailId", currentItem.getId());
        currentItem.setCtx(getActivity());

        currentItem.setCallBack(new Email.MyCallBack() {
            @Override
            public void onSuccess(Email.EmailService service) {
                switch (service) {
                    case DELETE:

                        LvsApplication.messaging.removeFromInbox(position);
                        lastEmails.clear();
                        for (int i = 0; i < 3 && i < LvsApplication.messaging.getInbox().size(); ++i) {
                            lastEmails.add(LvsApplication.messaging.getInbox().get(i));
                        }
                        inboxAdapter.notifyDataSetChanged();
                        if (lastEmails.size() > 0)
                            lastEmailsRecyclerView.setVisibility(View.VISIBLE);
                        else
                            empetyInboxTxtV.setVisibility(View.VISIBLE);

                        Toast.makeText(getActivity(), "Email has been deleted ...", Toast.LENGTH_SHORT).show();
                        break;

                }
            }

            @Override
            public void onFail(Email.EmailService service) {

            }
        });

        if (item.getTitle().equals(getString(R.string.reply))) {

            toComposeEmailActivity.putExtra(getActivity().getString(R.string.cntx_menu_item_extra_key), getString(R.string.reply));
            startActivity(toComposeEmailActivity);
            return true;
        } else if (item.getTitle().equals(getString(R.string.reply_all))) {

            toComposeEmailActivity.putExtra(getActivity().getString(R.string.cntx_menu_item_extra_key), getString(R.string.reply_all));
            startActivity(toComposeEmailActivity);
            return true;
        } else if (item.getTitle().equals(getString(R.string.forward))) {

            toComposeEmailActivity.putExtra(getActivity().getString(R.string.cntx_menu_item_extra_key), getString(R.string.forward));
            startActivity(toComposeEmailActivity);
            return true;
        } else if (item.getTitle().equals(getString(R.string.delete))) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setMessage("Confirm Deletion...")
                    .setMessage("Are you sure you want to delete this email ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            currentItem.callService(Email.EmailService.DELETE);
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();

            return true;
        }

        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterForContextMenu(lastEmailsRecyclerView);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == lastChildIdx)
            return;

        Student student = ((Student) adapterView.getAdapter().getItem(i));
        LvsApplication.selectedChild = student;
        LvsApplication.selectedChild.setCallBack(this);

        if (LvsApplication.selectedChild.getMaterials() == null
                || LvsApplication.selectedChild.getMaterials().size() == 0) {
            LvsApplication.selectedChild.callService(User.UserService.MATERIALS);
            mats.clear();
            loadingMarksPB.setVisibility(View.VISIBLE);
            marksRecyclerView.setVisibility(View.GONE);
        } else {
            marskLoaded = true;
            loadingMarksPB.setVisibility(View.GONE);
            mats.clear();

            for (int ii = 0;
                 ii < 3 && ii < student.getMaterials().size(); ++ii)
                mats.add(student.getMaterials().get(i));
            marksAdapter.notifyDataSetChanged();
            if (mats.size() > 0)
                marksRecyclerView.setVisibility(View.VISIBLE);
            else
                empetyMarksTxtV.setVisibility(View.VISIBLE);

        }
        if (LvsApplication.selectedChild.getAbsense() == null) {
            LvsApplication.selectedChild.callService(User.UserService.ABSENTS);
            absenceRecyclerView.setVisibility(View.GONE);
            loadingAbsencePB.setVisibility(View.VISIBLE);
        } else {
            abseceLoaded = true;
            loadingAbsencePB.setVisibility(View.GONE);
            if (!isAdded())
                return;
            absenceDaysCountTxtV.setText(student.getAbsense().getAbsenseDays() +
                    " " + getString(R.string.days));
            presentDaysCountTxtV.setText(student.getAbsense().getPresentDays() +
                    " " + getString(R.string.days));
            absenseDates.clear();
            absenseDates.addAll(student.getAbsense().getAbsenseDates());

            if (absenseDates.size() > 0) {
                absenceRecyclerView.setVisibility(View.VISIBLE);
                absenceAdapter.notifyDataSetChanged();
            } else
                empetyAbsenceTxtV.setVisibility(View.VISIBLE);
        }

        lastChildIdx = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
