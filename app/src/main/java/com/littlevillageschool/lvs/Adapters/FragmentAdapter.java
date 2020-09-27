package com.littlevillageschool.lvs.Adapters;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.littlevillageschool.lvs.Activities.InboxEmailFragment;
import com.littlevillageschool.lvs.Activities.OutboxEmailFragment;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.R;

/**
 * Created by Alalaa Center on 16/08/2016.
 */
public class FragmentAdapter extends FragmentPagerAdapter {

    int icons[] = {R.drawable.ic_insert_photo_black_24dp, R.drawable.ic_check_black_24dp};
    String tabText[] = {LvsApplication.APP_CTX.getString(R.string.inbox)
            , LvsApplication.APP_CTX.getString(R.string.outbox)};
    Fragment fragments[] = {new InboxEmailFragment(),new OutboxEmailFragment()};

    public FragmentAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabText[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}
