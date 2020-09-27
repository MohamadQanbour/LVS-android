package com.littlevillageschool.lvs.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.littlevillageschool.lvs.R;

/**
 * Created by Raafat Alhoumaidy on 10/18/2016.
 */

public class AboutUsFragment extends android.support.v4.app.Fragment {

    TextView descriptionTxtV;
    TextView smartGateTxtV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        descriptionTxtV = (TextView) view.findViewById(R.id.descriptionTxtV);
        smartGateTxtV = (TextView) view.findViewById(R.id.smartGateTxtV);
        descriptionTxtV.setText((getActivity().getString(R.string.description)));

        smartGateTxtV.setText(Html.fromHtml("All rights reserved by <a href=\"http://www.littlevillageschool.com\">LittleVillageSchool©</a> 2020"));
        smartGateTxtV.setMovementMethod(LinkMovementMethod.getInstance());
        smartGateTxtV.setClickable(true);
    }
}
