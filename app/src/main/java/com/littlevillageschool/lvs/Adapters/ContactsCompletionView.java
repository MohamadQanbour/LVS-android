package com.littlevillageschool.lvs.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.Person;
import com.littlevillageschool.lvs.Model.User;
import com.littlevillageschool.lvs.R;
import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by Alalaa Center on 11/08/2016.
 */
public class ContactsCompletionView extends TokenCompleteTextView<Person> {
    public ContactsCompletionView(Context context) {
        super(context);
    }

    public ContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactsCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(Person person) {
        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TextView view = (TextView) l.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);
        view.setText(person.getName());

        return view;
    }

    @Override
    protected Person defaultObject(String completionText) {
//        return ((Person) getAdapter().getItem(0));
        Toast.makeText(getContext(), R.string.contact_not_allowed_err_msg, Toast.LENGTH_SHORT).show();
        return LvsApplication.currUser.getPerson(completionText);
    }
}
