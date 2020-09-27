package com.littlevillageschool.lvs.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.littlevillageschool.lvs.Adapters.StudentAdapter;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.Messaging;
import com.littlevillageschool.lvs.Model.Parent;
import com.littlevillageschool.lvs.Model.Student;
import com.littlevillageschool.lvs.Model.User;
import com.littlevillageschool.lvs.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , View.OnClickListener {

    private android.support.v4.app.FragmentManager fragmentManager;

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private Spinner spinner;
    private StudentAdapter studentAdapter;
    public static TabLayout tabLayout;
    private int prevSelectedId = 0;
    private HomeFramgent homeFragment;
    private TeacherListFragment teacherListFragment;
    private StaffListFragment staffListFragment;

    private TextView drawerHeaderChildrenTxtV;

    public TextView inboxNavTxtV;
    private boolean backPressed = false;
    private int prevChildIndx = -1;

    private void updateUIs() {
        if (((Parent) LvsApplication.currUser).getStudentList().size() == 0) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("لا يوجد طلاب!")
                    .setMessage("لا يوجد طلاب لعرض معلوماتهم، يرجى التواصل مع المدرسة لحل المشكلة")
                    .setPositiveButton("موافق",null)
                    .show();
            return;
        }
        LvsApplication.selectedChild = ((Parent) LvsApplication.currUser).getStudentList().get(0);

        String childs = "";
        for (Student st : ((Parent) LvsApplication.currUser).getStudentList()) {
            childs += st.getFullName() + ",";

        }
        childs = childs.substring(0, childs.length() - 1);
        drawerHeaderChildrenTxtV.setText(drawerHeaderChildrenTxtV.getText() + "\n" + childs);
        studentAdapter = new StudentAdapter(this,
                android.R.layout.simple_list_item_1,
                ((Parent) LvsApplication.currUser).getStudentList());
        spinner.setAdapter(studentAdapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == prevChildIndx)
//                    return;
//                Student st = studentAdapter.getItem(i);
//                st.callService(User.UserService.ABSENTS);
//                st.callService(User.UserService.MATERIALS);
//                homeFragment = new HomeFramgent();
//
//                st.setCallBack((User.MyCallBack) homeFragment);
//                LvsApplication.selectedChild = studentAdapter.getItem(i);
//                fragmentManager.beginTransaction()
//                        .replace(R.id.mainFragmentContainer, homeFragment)
//                        .commit();
//                prevSelectedId = R.id.nav_home;
//                prevChildIndx = i;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        spinner.setOnItemSelectedListener((homeFragment));
        studentAdapter.notifyDataSetChanged();
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    private boolean checkOldPackages() {

        if (isPackageInstalled("com.smartgateapps.lvs", getPackageManager())) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.delete_old_version)
                    .setMessage(R.string.delete_old_version_msg)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_DELETE);
                            intent.setData(Uri.parse("package:com.smartgateapps.lvs"));
                            startActivity(intent);
                        }
                    }).setCancelable(false).show();
            return true;

        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.home));
        setSupportActionBar(toolbar);

        homeFragment = new HomeFramgent();
        teacherListFragment = new TeacherListFragment();
        staffListFragment = new StaffListFragment();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        spinner = (Spinner) findViewById(R.id.studentSpinner);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView;
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);


        navigationView.getMenu().getItem(0).setChecked(true);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.mainFragmentContainer, homeFragment)
                .commit();

        prevSelectedId = R.id.nav_home;


        View headerView;
        headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        drawerHeaderChildrenTxtV = (TextView) headerView.findViewById(R.id.userNameTxtV);
        drawerHeaderChildrenTxtV.setText(LvsApplication.currUser.getFullName());

        inboxNavTxtV = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_mail));
        inboxNavTxtV.setTextColor(getResources().getColor(R.color.colorAccent));
        inboxNavTxtV.setTypeface(null, Typeface.BOLD);
        inboxNavTxtV.setGravity(Gravity.CENTER_VERTICAL);

        LvsApplication.currUser.setCallBack(new User.MyCallBack() {
            @Override
            public void onSucc(User.UserService service) {
                ((User.MyCallBack) homeFragment).onSucc(service);
            }

            @Override
            public void onFail(User.UserService service, String errorMsg) {
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
        LvsApplication.messaging.setCallBack(new Messaging.MyCallBack() {
            @Override
            public void onSuccess(Messaging.MessaginService service) {
                switch (service) {
                    case UNREAD_COUNT:
                        inboxNavTxtV.setText(LvsApplication.messaging.getTotalUnreadInbox() + "");
                        break;
                    case GET_UNREAD_INBOX:
                        ((Messaging.MyCallBack) homeFragment).onSuccess(Messaging.MessaginService.GET_UNREAD_INBOX);
                        break;
                }
            }

            @Override
            public void onFail(Messaging.MessaginService service) {

                switch (service) {
                    case UNREAD_COUNT:
                        break;
                }
            }
        });

        LvsApplication.messaging.callService(Messaging.MessaginService.UNREAD_COUNT);
        LvsApplication.messaging.callService(Messaging.MessaginService.GET_INBOX);
        LvsApplication.messaging.callService(Messaging.MessaginService.GET_UNREAD_INBOX);
        LvsApplication.messaging.callService(Messaging.MessaginService.GET_OUTBOX);

        if (LvsApplication.currUser.getType() == User.UserType.STUDENT) {
            LvsApplication.currUser.callService(User.UserService.MATERIALS);
            LvsApplication.currUser.callService(User.UserService.ABSENTS);

            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_schadual).setVisible(true);
            //menu.findItem(R.id.nav_notes).setVisible(true);

            spinner.setVisibility(View.GONE);

        } else if (LvsApplication.currUser.getType() == User.UserType.PARENT) {

            getChilderens();
            drawerHeaderChildrenTxtV.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);

            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_payments).setVisible(true);

        }
        checkOldPackages();
    }

    private void getChilderens() {
        if (((Parent) LvsApplication.currUser).getStudentList() != null
                && ((Parent) LvsApplication.currUser).getStudentList().size() > 0)
            updateUIs();
        else {
            LvsApplication.currUser.setCallBack(new User.MyCallBack() {
                @Override
                public void onSucc(User.UserService service) {
                    switch (service) {
                        case STUDENT_OF_FAMILY:
                                updateUIs();
                            break;
                    }
                }

                @Override
                public void onFail(User.UserService service, String errorMsg) {

                }
            });
            LvsApplication.currUser.callService(User.UserService.STUDENT_OF_FAMILY);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (backPressed)
                super.onBackPressed();
            else {
                Toast.makeText(this, R.string.exit_msg, Toast.LENGTH_LONG).show();
                backPressed = true;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        backPressed = false;
                    }
                }, 3000);
            }

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        toolbar.setTitle(item.getTitle());


        if (id == prevSelectedId) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        spinner.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        android.support.v4.app.Fragment fragment = null;
        switch (id) {
            case R.id.nav_home:
                fragment = homeFragment;
                if (LvsApplication.currUser.getType() == User.UserType.PARENT)
                    spinner.setVisibility(View.VISIBLE);
                break;

            case R.id.nav_mail:
                fragment = new EmailFragment();
                tabLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_payments:
                fragment = new PaymentsFragment();
                break;
            case R.id.nav_marks:
                fragment = new MarksFragment();
                break;
            case R.id.nav_notes:
                fragment = new NotesFragment();
                break;
            case R.id.nav_teachin_staff:
                fragment = teacherListFragment;
                break;
            case R.id.nav_Administrative_staff:
                fragment = staffListFragment;
                break;
            case R.id.nav_schadual:
                fragment = new ScadualFragment();
                break;

            case R.id.nav_profil:
                fragment = new UserProifleFragment();
                break;
            case R.id.nav_call_us:
                fragment = new ContactUsFragment();
                break;
            case R.id.nav_about:
                fragment = new AboutUsFragment();
                break;

            case R.id.nav_logout:
                LvsApplication.currUser.callService(User.UserService.UNREGISTER_DEVICE);

                Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
                LvsApplication.sharedPreferences.edit()
                        .remove(LvsApplication.USER_PREF_KEY)
                        .remove(User.ACCESS_TOKEN_KEY)
                        .apply();

                LvsApplication.currUser = new Student();
                LvsApplication.parUser = new Parent();
                LvsApplication.ACCESS_TOKEN = null;
                LvsApplication.messaging = new Messaging(this);
                startActivity(toLogin);
                this.finish();

        }

        prevSelectedId = id;
        if (fragment != null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.mainFragmentContainer, fragment)
                    .commit();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        try {
            String phone = ((TextView) view).getText().toString();
            if (!phone.startsWith("0"))
                phone = "011" + phone;

            String uri = "tel:" + phone;
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
