package com.littlevillageschool.lvs.Activities;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.littlevillageschool.lvs.Adapters.FragmentAdapter;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.Email;
import com.littlevillageschool.lvs.Model.Messaging;
import com.littlevillageschool.lvs.R;

import java.security.PrivateKey;

/**
 * Created by Alalaa Center on 16/08/2016.
 */
public class EmailFragment extends android.support.v4.app.Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FragmentAdapter adapter;
    private Messaging messaging;
    private int currentPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messaging = LvsApplication.messaging;
        adapter = new FragmentAdapter(getChildFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = MainActivity.tabLayout;

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                FragmentLifecycle fragmentToShow = (FragmentLifecycle)adapter.getItem(position);
                fragmentToShow.onResumeFragment();

                FragmentLifecycle fragmentToHide = (FragmentLifecycle)adapter.getItem(currentPosition);
                fragmentToHide.onPauseFragment();

                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setCurrentItem(0);


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.composeEmailFab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toComposeActivity = new Intent(getActivity(), ComposeEmailActivity.class);
                toComposeActivity.putExtra("NEW_EMAIL_EXTRA", true);
                startActivity(toComposeActivity);
            }
        });

    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {

        final int position;
        final Email currentItem;

        if (item.getGroupId() == 1) {//inbox
            position = messaging.getInboxAdapter().getPosition();
            currentItem = messaging.getInbox().get(position);

        }else if(item.getGroupId() == 2) {//outbox
            position = messaging.getOutboxAdapter().getPosition();
            currentItem = messaging.getOutbox().get(position);

        }else{
            currentItem = new Email();
            position = 0;
        }
        final Intent toComposeEmailActivity = new Intent(getActivity(), ComposeEmailActivity.class);
        toComposeEmailActivity.putExtra("emailId", currentItem.getId());
        currentItem.setCtx(getActivity());

        currentItem.setCallBack(new Email.MyCallBack() {
            @Override
            public void onSuccess(Email.EmailService service) {
                switch (service) {
                    case DELETE:
                        if(item.getGroupId() == 1) {//inbox

                            messaging.removeFromInbox(position);
                            if (messaging.getInbox().size() == 0)
                                ((InboxEmailFragment)adapter.getItem(0)).emptyTxtV.setVisibility(View.VISIBLE);
                        }else if (item.getGroupId() == 2) {//outbox
                            messaging.removeFromOutbox(position);
                            if(messaging.getOutbox().size() == 0)
                                ((OutboxEmailFragment)adapter.getItem(1)).emptyTxtV.setVisibility(View.VISIBLE);
                        }
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
                    .setMessage(getString(R.string.confirm_delete))
                    .setMessage(getString(R.string.confirm_delete_2))
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
}
