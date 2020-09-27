package com.littlevillageschool.lvs.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.littlevillageschool.lvs.Adapters.EndlessRecyclerOnScrollListener;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.Email;
import com.littlevillageschool.lvs.Model.Messaging;
import com.littlevillageschool.lvs.R;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * Created by Alalaa Center on 21/07/2016.
 */
public class InboxEmailFragment extends android.support.v4.app.Fragment implements FragmentLifecycle {


    private RecyclerView emailsRecyclerView;
    private WaveSwipeRefreshLayout swipeLayout;
    public TextView emptyTxtV;

    public InboxEmailFragment() {
        LvsApplication.messaging.setCallBack(new Messaging.MyCallBack() {
            @Override
            public void onSuccess(Messaging.MessaginService service) {
                switch (service) {
                    case GET_INBOX:
                        swipeLayout.setRefreshing(false);
                        if (LvsApplication.messaging.getInbox().size() > 0) {
                            emptyTxtV.setVisibility(View.GONE);
                            LvsApplication.messaging.callService(Messaging.MessaginService.VIEW_MESSAGES);
                        }
                        break;

                    case UNREAD_COUNT:
                        ((MainActivity) getActivity()).
                                inboxNavTxtV.setText(LvsApplication.messaging.getTotalUnreadInbox() + "");
                        break;


                }
            }

            @Override
            public void onFail(Messaging.MessaginService service) {
                switch (service) {
                    case GET_INBOX:
                        swipeLayout.setRefreshing(false);
                        break;
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mail_box_layout, container, false);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailsRecyclerView = (RecyclerView) view.findViewById(R.id.emailsRecyclerView);
        swipeLayout = (WaveSwipeRefreshLayout) view.findViewById(R.id.mailSwipeLayout);
        emptyTxtV = (TextView) view.findViewById(R.id.emptyTxtV);

        swipeLayout.setWaveColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        swipeLayout.setColorSchemeResources(R.color.colorAccent);

        emailsRecyclerView.setHasFixedSize(true);
        final GridLayoutManager lnLayoutMgr;
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE)
            lnLayoutMgr = new GridLayoutManager(getActivity(), 2);
        else
            lnLayoutMgr = new GridLayoutManager(getActivity(), 1);

        emailsRecyclerView.setLayoutManager(lnLayoutMgr);

        final GestureDetector mGestureDetector =
                new GestureDetector(getActivity(),
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                return true;
                            }
                        });
        emailsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mGestureDetector.onTouchEvent(e)) {

                    int pos = rv.getChildAdapterPosition(child);
                    Email currEmail = LvsApplication.messaging.getInbox().get(pos);

                    currEmail.setReaded(true);
                    LvsApplication.messaging.getInboxAdapter().notifyDataSetChanged();
                    int total = LvsApplication.messaging.getTotalNewInbox();
                    LvsApplication.messaging.setTotalUnreadInbox(total - 1);
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

        emailsRecyclerView.setAdapter(LvsApplication.messaging.getInboxAdapter());
        //registerForContextMenu(emailsRecyclerView);
        if (LvsApplication.messaging.getInbox().size() == 0) {
            emptyTxtV.setVisibility(View.VISIBLE);
            LvsApplication.messaging.callService(Messaging.MessaginService.GET_INBOX);
            swipeLayout.setRefreshing(true);
        } else {
            emptyTxtV.setVisibility(View.GONE);
        }

        emailsRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(lnLayoutMgr) {
            @Override
            public void onLoadMore(int current_page) {
                swipeLayout.setEnabled(true);
                LvsApplication.messaging.setInboxPgIdx(current_page);
                swipeLayout.setRefreshing(true);
                LvsApplication.messaging.callService(Messaging.MessaginService.GET_INBOX);

            }
        });

        swipeLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LvsApplication.messaging.setInboxPgIdx(0);
                LvsApplication.messaging.callService(Messaging.MessaginService.GET_INBOX);
                LvsApplication.messaging.callService(Messaging.MessaginService.UNREAD_COUNT);
                emailsRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(lnLayoutMgr) {
                    @Override
                    public void onLoadMore(int current_page) {
                        swipeLayout.setEnabled(true);
                        LvsApplication.messaging.setInboxPgIdx(current_page);
                        swipeLayout.setRefreshing(true);
                        LvsApplication.messaging.callService(Messaging.MessaginService.GET_INBOX);

                    }
                });
            }
        });

    }


    @Override
    public void onPauseFragment() {
        unregisterForContextMenu(emailsRecyclerView);
        swipeLayout.setEnabled(false);
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onResumeFragment() {
        swipeLayout.setEnabled(true);
        swipeLayout.setRefreshing(false);
        LvsApplication.messaging.setCallBack(new Messaging.MyCallBack() {
            @Override
            public void onSuccess(Messaging.MessaginService service) {
                switch (service) {
                    case GET_INBOX:
                        swipeLayout.setRefreshing(false);
                        if (LvsApplication.messaging.getInbox().size() > 0) {
                            emptyTxtV.setVisibility(View.GONE);
                            LvsApplication.messaging.callService(Messaging.MessaginService.VIEW_MESSAGES);
                        }
                        break;

                    case UNREAD_COUNT:
                        ((MainActivity) getActivity()).
                                inboxNavTxtV.setText(LvsApplication.messaging.getTotalUnreadInbox() + "");
                        break;


                }
            }

            @Override
            public void onFail(Messaging.MessaginService service) {
                switch (service) {
                    case GET_INBOX:
                        swipeLayout.setRefreshing(false);
                        break;
                }
            }
        });
    }
}
