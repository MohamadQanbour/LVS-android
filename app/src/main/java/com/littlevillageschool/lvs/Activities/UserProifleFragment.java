package com.littlevillageschool.lvs.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.CacheDispatcher;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.User;
import com.littlevillageschool.lvs.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class UserProifleFragment extends android.support.v4.app.Fragment
        implements View.OnClickListener {


    private ImageView profileImgV;
    private TextView fullNameTxtV;
    private TextView emailTxtV;
    private Button changePasswordBtn;
    private Button submitBtn;
    private TextInputLayout oldPasswordLayout;
    private TextInputLayout newPasswordLayout;
    private TextView newpasswordTxtV;
    private TextView oldPasswordTxtV;
    private ScrollView parentView;

    private Animation animation1;
    private Animation animation2;
    private Animation animation3;

    private Animation rAnimation1;
    private Animation rAnimation2;
    private Animation rAnimation3;
    private DotProgressBar dotProgressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.login_btn_reverse_animation);
        animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.login_btn_reverse_animation);
        animation3 = AnimationUtils.loadAnimation(getActivity(), R.anim.login_btn_reverse_animation);

        rAnimation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.login_btn_animation);
        rAnimation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.login_btn_animation);
        rAnimation3 = AnimationUtils.loadAnimation(getActivity(), R.anim.login_btn_animation);

        animation2.setStartOffset(200);
        animation3.setStartOffset(400);

        rAnimation2.setStartOffset(200);
        rAnimation1.setStartOffset(400);
        rAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                submitBtn.setVisibility(View.GONE);
                newPasswordLayout.setVisibility(View.GONE);
                oldPasswordLayout.setVisibility(View.GONE);

                submitBtn.clearAnimation();
                newPasswordLayout.clearAnimation();
                oldPasswordLayout.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_proifle, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        profileImgV = (ImageView) view.findViewById(R.id.profileImgV);

        changePasswordBtn = (Button) view.findViewById(R.id.changPasswordBtn);
        submitBtn = (Button) view.findViewById(R.id.submitChngPswrdBtn);
        oldPasswordLayout = (TextInputLayout) view.findViewById(R.id.oldPasswordTxtLayout);
        newPasswordLayout = (TextInputLayout) view.findViewById(R.id.newPasswordTxtLayout);
        oldPasswordTxtV = (TextView) view.findViewById(R.id.oldPasswordTxtV);
        newpasswordTxtV = (TextView) view.findViewById(R.id.newPasswordTxtV);
        fullNameTxtV = (TextView) view.findViewById(R.id.fullNameTxtv);
        emailTxtV = (TextView) view.findViewById(R.id.emailTxtV);
        dotProgressBar = (DotProgressBar) view.findViewById(R.id.dot_progress_bar);
        dotProgressBar.setVisibility(View.INVISIBLE);
        parentView = (ScrollView) view;

        fullNameTxtV.setText(LvsApplication.currUser.getFullName());
        emailTxtV.setText(LvsApplication.currUser.getEmail());

        changePasswordBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.changPasswordBtn:
                oldPasswordLayout.setVisibility(View.VISIBLE);
                newPasswordLayout.setVisibility(View.VISIBLE);
                submitBtn.setVisibility(View.VISIBLE);

                oldPasswordLayout.setAnimation(animation1);
                newPasswordLayout.setAnimation(animation2);
                submitBtn.setAnimation(animation3);
                changePasswordBtn.setAnimation(rAnimation1);
                changePasswordBtn.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        changePasswordBtn.setVisibility(View.INVISIBLE);
                        changePasswordBtn.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;

            case R.id.submitChngPswrdBtn:

                final String oldPassword = oldPasswordTxtV.getText().toString();
                String newPassword = newpasswordTxtV.getText().toString();
                if (TextUtils.isEmpty(oldPassword)) {
                    oldPasswordTxtV.setError(getString(R.string.empty_password_error));
                    oldPasswordTxtV.requestFocus();
                    break;
                }
                if (TextUtils.isEmpty(newPassword)) {
                    newpasswordTxtV.setError(getString(R.string.empty_password_error));
                    newpasswordTxtV.requestFocus();
                    break;
                }

                LvsApplication.currUser.setOldPassword(oldPassword);
                LvsApplication.currUser.setNewPassword(newPassword);

                if (!(LvsApplication.currUser.getOldPassword()+LvsApplication.PASSWORD_SECURITY)
                        .equals(LvsApplication.currUser.getPassWord())) {
                    oldPasswordTxtV.setError(getString(R.string.old_passord_error));
                    oldPasswordTxtV.requestFocus();
                    break;
                }


                submitBtn.clearAnimation();
                newPasswordLayout.clearAnimation();
                oldPasswordLayout.clearAnimation();

                submitBtn.setAnimation(rAnimation1);
                newPasswordLayout.setAnimation(rAnimation2);
                oldPasswordLayout.setAnimation(rAnimation3);

                submitBtn.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        dotProgressBar.setVisibility(View.VISIBLE);
                        submitBtn.setVisibility(View.INVISIBLE);
                        newPasswordLayout.setVisibility(View.INVISIBLE);
                        oldPasswordLayout.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                LvsApplication.currUser.callService(User.UserService.CHANGE_PASSWORD);
                LvsApplication.currUser.setCallBack(new User.MyCallBack() {
                    @Override
                    public void onSucc(User.UserService service) {
                        changePasswordBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), R.string.done,Toast.LENGTH_SHORT).show();
                        LvsApplication.currUser.setPassWord(LvsApplication.currUser.getOldPassword());
                        LvsApplication.sharedPreferences
                                .edit()
                                .putString(User.PASSWORD_KEY, LvsApplication.currUser.getPassWord().replace(LvsApplication.PASSWORD_SECURITY, ""))
                                .apply();
                    }

                    @Override
                    public void onFail(User.UserService service, String errorMsg) {
                        dotProgressBar.setVisibility(View.GONE);
                        oldPasswordLayout.clearAnimation();
                        newPasswordLayout.clearAnimation();
                        submitBtn.clearAnimation();
                        oldPasswordLayout.setVisibility(View.VISIBLE);
                        newPasswordLayout.setVisibility(View.VISIBLE);
                        submitBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(),errorMsg,Toast.LENGTH_SHORT).show();
                    }
                });

                break;
        }

    }
}
