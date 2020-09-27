package com.littlevillageschool.lvs.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.littlevillageschool.lvs.Adapters.AttachmentAdapter;
import com.littlevillageschool.lvs.Adapters.SpacesItemDecoration;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.Email;
import com.littlevillageschool.lvs.R;

import java.io.File;

public class EmailViewActivity extends AppCompatActivity {

    private TextView titleTxtV;
    private TextView senderTxtV;
    private TextView dateTxtV;
    private WebView bodyTxtV;
    private LinearLayout forwardLL;
    private LinearLayout replyLL;
    private LinearLayout replyAllLL;
    private LinearLayout tools;
    private Email email;
    private RecyclerView attachmantRV;
    private ProgressBar loadingPB;
    private AttachmentAdapter attachmentAdapter;
    private boolean canContact = true;


    public boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.alert)
                        .setMessage(R.string.permission_alert)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(EmailViewActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                        1);
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String id = extras.getString(Email.ID_KEY);
        String title = extras.getString(Email.TITLE_KEY);
        String date = extras.getString(Email.DATE_KEY);
        String sender = extras.getString(Email.SENDER_KEY);
        int role = extras.getInt(Email.ROLE_KEY);


        email = new Email();
        email.setId(id);
        email.setTitle(title);
        email.setSender(sender);
        email.setDate(date);

        email.setCallBack(new Email.MyCallBack() {
            @Override
            public void onSuccess(Email.EmailService service) {
                switch (service) {
                    case GET_MESSAGE:
                        loadingPB.setVisibility(View.INVISIBLE);

                        String text = "<html><head><head/><body>"
                                + (email.getBody())
                                + "</body></html>";

                        bodyTxtV.setVisibility(View.VISIBLE);
                        bodyTxtV.loadData(text, "text/html; charset=utf-8", "utf-8");

                        attachmentAdapter = new AttachmentAdapter(EmailViewActivity.this
                                , R.layout.attachment_item_laytou, email.getAttachments());
                        attachmantRV.setAdapter(attachmentAdapter);

                        break;
                }
            }

            @Override
            public void onFail(Email.EmailService service) {

            }
        });


        titleTxtV = (TextView) findViewById(R.id.messageTitleTxtV);
        senderTxtV = (TextView) findViewById(R.id.messageSenderTxtV);
        dateTxtV = (TextView) findViewById(R.id.messageDateTxtV);
        bodyTxtV = (WebView) findViewById(R.id.messageBodyTxtV);
        forwardLL = (LinearLayout) findViewById(R.id.forwardLL);
        replyAllLL = (LinearLayout) findViewById(R.id.replyAllLL);
        replyLL = (LinearLayout) findViewById(R.id.replyLL);
        loadingPB = (ProgressBar) findViewById(R.id.loadingPB);
        tools = (LinearLayout) findViewById(R.id.tools);
        attachmantRV = (RecyclerView) findViewById(R.id.attachments);

        GridLayoutManager layoutManager;

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new GridLayoutManager(this, 1);
        }

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        attachmantRV.setLayoutManager(layoutManager);
        attachmantRV.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        final GestureDetector mGestureDetector =
                new GestureDetector(this,
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                return true;
                            }
                        });

        attachmantRV.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mGestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    if (!checkPermissions())
                        return false;
                    if (!email.getAttachments().get(pos).isExist())
                        email.getAttachments().get(pos).downloadAttachment();
                    else {

                        openFile(email.getAttachments().get(pos));
                    }

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

        email.callService(Email.EmailService.GET_MESSAGE);

        canContact = LvsApplication.currUser.getPersons() != null &&
                LvsApplication.currUser.getPerson(email.getSender()) != null;

        if (role == 1) {
            assert tools != null;
            tools.setVisibility(View.INVISIBLE);
        }

        bodyTxtV.setVisibility(View.INVISIBLE);
        bodyTxtV.getSettings().setSupportZoom(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            titleTxtV.setTransitionName(getString(R.string.title_text_view_transition));
            senderTxtV.setTransitionName(getString(R.string.sender_text_view_transition));
            dateTxtV.setTransitionName(getString(R.string.date_text_view_transition));
        }

        titleTxtV.setText(email.getTitle());
        senderTxtV.setText(email.getSender());
        dateTxtV.setText(email.getDate());

        final Intent toComposeEmailActivity = new Intent(EmailViewActivity.this
                , ComposeEmailActivity.class);
        toComposeEmailActivity.putExtra("emailId", email.getId());
        email.setCtx(EmailViewActivity.this);
        replyLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toComposeEmailActivity.putExtra(getString(R.string.cntx_menu_item_extra_key), getString(R.string.reply));
                if (canContact)
                    startActivity(toComposeEmailActivity);
                else
                    Toast.makeText(EmailViewActivity.this, R.string.contact_not_allowed_err_msg, Toast.LENGTH_SHORT).show();
            }
        });

        replyAllLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toComposeEmailActivity.putExtra(getString(R.string.cntx_menu_item_extra_key), getString(R.string.reply_all));
                if (canContact)
                    startActivity(toComposeEmailActivity);
                else
                    Toast.makeText(EmailViewActivity.this, R.string.contact_not_allowed_err_msg, Toast.LENGTH_SHORT).show();


            }
        });

        forwardLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toComposeEmailActivity.putExtra(getString(R.string.cntx_menu_item_extra_key), getString(R.string.forward));
                startActivity(toComposeEmailActivity);

            }
        });
    }

    private void openFile(Email.Attachment attachment) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(attachment.getFileType());
        File file =  new File(attachment.getFilePath());
        Uri fileUrl = FileProvider.getUriForFile(
               this
                , getApplicationContext().getPackageName() + ".provider"
                ,file );

        newIntent.setDataAndType(fileUrl, mimeType);
        newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        newIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            this.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                supportFinishAfterTransition();
                onBackPressed();
                return true;
        }

        return false;
    }
}
