package com.littlevillageschool.lvs.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.littlevillageschool.lvs.Adapters.ContactsCompletionView;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.Email;
import com.littlevillageschool.lvs.Model.Person;
import com.littlevillageschool.lvs.Model.User;
import com.littlevillageschool.lvs.R;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

import io.github.mthli.knife.KnifeText;

public class ComposeEmailActivity extends AppCompatActivity {

    private KnifeText knife;
    private LinearLayout LL1;
    private LinearLayout LL2;
    private TextInputLayout textInputLayout1;

    private TextView fromTxtV;
    private ContactsCompletionView toEdtTxt;
    private EditText titleEdtTxt;

    private ArrayAdapter<Person> adapter;
    private HorizontalScrollView tools;
    private Email currEmail;
    List<Person> persons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_email_layout);

        persons = new ArrayList<>();

        knife = (KnifeText) findViewById(R.id.knife);
        LL1 = (LinearLayout) findViewById(R.id.LL1);
        LL2 = (LinearLayout) findViewById(R.id.LL2);
        tools = (HorizontalScrollView)findViewById(R.id.toolsFromat);

        textInputLayout1 = (TextInputLayout) findViewById(R.id.textInput1);

        toEdtTxt = (ContactsCompletionView) findViewById(R.id.toEdtTxt);
        fromTxtV = (TextView) findViewById(R.id.fromTxtV);
        titleEdtTxt = (EditText) findViewById(R.id.titleEdtTxt);

        if(LvsApplication.currUser.getPersons()==null ||
                LvsApplication.currUser.getPersons().size()==0) {
            LvsApplication.currUser.callService(User.UserService.GET_ADDRESS_BOOK);

            toEdtTxt.setEnabled(false);
            LvsApplication.currUser.setCallBack(new User.MyCallBack() {
                @Override
                public void onSucc(User.UserService service) {
                    switch (service){
                        case GET_ADDRESS_BOOK:
                            toEdtTxt.setEnabled(true);
                            persons.clear();
                            persons.addAll(LvsApplication.currUser.getPersons());
                            adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFail(User.UserService service, String errorMsg) {

                }
            });
        }else{
            persons = LvsApplication.currUser.getPersons();
        }

        adapter = new ArrayAdapter<>(this
                , android.R.layout.simple_list_item_1
                , persons);

        toEdtTxt.setAdapter(adapter);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        currEmail = new Email();

        boolean newEmail = extras.getBoolean("NEW_EMAIL_EXTRA", false);
        if (!newEmail) {
            final String ctxMenuItem = extras.getString("CONTEXT_MENU_ITEM_EXTRA");
            String curEmailId = extras.getString("emailId");
            currEmail.setId(curEmailId);
            currEmail.setCtx(this);
            currEmail.callService(Email.EmailService.GET_MESSAGE);

            currEmail.setCallBack(new Email.MyCallBack() {
                @Override
                public void onSuccess(Email.EmailService service) {
                    switch (service) {
                        case GET_MESSAGE:
                            if (ctxMenuItem.equals(getString(R.string.reply))) {
                                titleEdtTxt.setText("RE: " + currEmail.getTitle() + ",");

                            } else if (ctxMenuItem.equals(getString(R.string.reply_all))) {
                                titleEdtTxt.setText("RE: " + currEmail.getTitle() + ",");

                            } else if (ctxMenuItem.equals(getString(R.string.forward))) {
                                titleEdtTxt.setText("FWD: " + currEmail.getTitle() + ",");

                            }
                            break;
                        case GET_REPLY_RECEIVER:

                            for (int i = 0; i < currEmail.getPersons().size(); i++)
                                toEdtTxt.addObjectAsync(currEmail.getPersons().get(i));
                            break;
                        case GET_REPLY_ALL_RECIEVER:

                            for (int i = 0; i < currEmail.getPersons().size(); i++)
                                toEdtTxt.addObjectAsync(currEmail.getPersons().get(i));
                            break;
                    }
                }

                @Override
                public void onFail(Email.EmailService service) {

                }
            });

            toEdtTxt.setEnabled(false);
            titleEdtTxt.setEnabled(false);

            assert ctxMenuItem != null;
            if (ctxMenuItem.equals(getString(R.string.reply))) {
                currEmail.callService(Email.EmailService.GET_REPLY_RECEIVER);

            } else if (ctxMenuItem.equals(getString(R.string.reply_all))) {
                currEmail.callService(Email.EmailService.GET_REPLY_ALL_RECIEVER);
            } else if (ctxMenuItem.equals(getString(R.string.forward))) {
                toEdtTxt.setEnabled(true);
            }
        }


        fromTxtV.setText(LvsApplication.currUser.getFullName());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        KeyboardVisibilityEvent.setEventListener(this
                , new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            if (knife.hasFocus()) {
                                LL1.setVisibility(View.GONE);
                                LL2.setVisibility(View.GONE);
                                textInputLayout1.setVisibility(View.GONE);
                                tools.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (knife.hasFocus()) {
                                LL1.setVisibility(View.VISIBLE);
                                LL2.setVisibility(View.VISIBLE);
                                textInputLayout1.setVisibility(View.VISIBLE);
                                tools.setVisibility(View.GONE);
                            }
                        }
                    }
                });

        knife.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm.isAcceptingText()) {
                        LL1.setVisibility(View.GONE);
                        LL2.setVisibility(View.GONE);
                        textInputLayout1.setVisibility(View.GONE);
                        tools.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        setupBold();
        setupItalic();
        setupUnderline();
        setupStrikethrough();
        setupBullet();
        setupQuote();
        setupLink();
        setupClear();


    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to cancel this message ?")
                .setTitle("Confirm Cancel")
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ComposeEmailActivity.this.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose_email_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.actionDelete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to cancel this message ?")
                        .setTitle("Confirm Cancel")
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ComposeEmailActivity.this.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                builder.show();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.actionSend:

                String body = Html.toHtml(new SpannableString(knife.getText()));
                String subject = titleEdtTxt.getText().toString();

                if(toEdtTxt.getObjects()==null
                        ||toEdtTxt.getObjects().size()==0) {
                    toEdtTxt.setError(getString(R.string.empty_receiver_error));
                    toEdtTxt.requestFocus();
                    break;
                }
                if (TextUtils.isEmpty(titleEdtTxt.getText())) {
                    titleEdtTxt.setError(getString(R.string.empty_title_eror));
                    titleEdtTxt.requestFocus();
                    break;
                }


                // 13/08/2016 append reply and forward
                String append = "";
                String to = LvsApplication.currUser.getFullName();
                if (currEmail.getBody() != null && currEmail.getBody().length() > 0) {
                    append = String.format("<div><br />\n" +
                                    "  </div><hr />\n" +
                                    "\n" +
                                    "<div style=\"font-weight: bold;\">From: %s</div>\n" +
                                    "<div style=\"font-weight: bold;\">Date: %s</div>\n" +
                                    "<div style=\"font-weight: bold;\">To: %s</div><hr />\n"
                            , currEmail.getSender(), currEmail.getDate(), to);
                }

                currEmail.setBody(body + append + currEmail.getBody());
                currEmail.setTitle(subject);
                currEmail.setPersons(toEdtTxt.getObjects());

                currEmail.callService(Email.EmailService.COMPOSE);
                item.setEnabled(false);

                currEmail.setCallBack(new Email.MyCallBack() {
                    @Override
                    public void onSuccess(Email.EmailService service) {
                        Toast.makeText(ComposeEmailActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                        ComposeEmailActivity.this.overridePendingTransition(android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right);
                        ComposeEmailActivity.this.finish();

                    }

                    @Override
                    public void onFail(Email.EmailService service) {
                        item.setEnabled(true);
                        Toast.makeText(ComposeEmailActivity.this, "Error Try Again..", Toast.LENGTH_SHORT).show();
                    }
                });

                break;
        }

        return false;
    }


    private void setupBold() {
        ImageButton bold = (ImageButton) findViewById(R.id.bold);

        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.bold(!knife.contains(KnifeText.FORMAT_BOLD));
            }
        });

        bold.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ComposeEmailActivity.this, R.string.toast_bold, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupItalic() {
        ImageButton italic = (ImageButton) findViewById(R.id.italic);

        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.italic(!knife.contains(KnifeText.FORMAT_ITALIC));
            }
        });

        italic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ComposeEmailActivity.this, R.string.toast_italic, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupUnderline() {
        ImageButton underline = (ImageButton) findViewById(R.id.underline);

        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.underline(!knife.contains(KnifeText.FORMAT_UNDERLINED));
            }
        });

        underline.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ComposeEmailActivity.this, R.string.toast_underline, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupStrikethrough() {
        ImageButton strikethrough = (ImageButton) findViewById(R.id.strikethrough);

        strikethrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.strikethrough(!knife.contains(KnifeText.FORMAT_STRIKETHROUGH));
            }
        });

        strikethrough.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ComposeEmailActivity.this, R.string.toast_strikethrough, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupBullet() {
        ImageButton bullet = (ImageButton) findViewById(R.id.bullet);

        bullet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.bullet(!knife.contains(KnifeText.FORMAT_BULLET));
            }
        });


        bullet.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ComposeEmailActivity.this, R.string.toast_bullet, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupQuote() {
        ImageButton quote = (ImageButton) findViewById(R.id.quote);

        quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.quote(!knife.contains(KnifeText.FORMAT_QUOTE));
            }
        });

        quote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ComposeEmailActivity.this, R.string.toast_quote, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupLink() {
        ImageButton link = (ImageButton) findViewById(R.id.link);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLinkDialog();
            }
        });

        link.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ComposeEmailActivity.this, R.string.toast_insert_link, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupClear() {
        ImageButton clear = (ImageButton) findViewById(R.id.clear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.clearFormats();
            }
        });

        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ComposeEmailActivity.this, R.string.toast_format_clear, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void showLinkDialog() {
        final int start = knife.getSelectionStart();
        final int end = knife.getSelectionEnd();

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_link, null, false);
        final EditText editText = (EditText) view.findViewById(R.id.edit);
        builder.setView(view);
        builder.setTitle(R.string.dialog_title);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String link = editText.getText().toString().trim();
                if (TextUtils.isEmpty(link)) {
                    return;
                }

                // When KnifeText lose focus, use this method
                knife.link(link, start, end);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // DO NOTHING HERE
            }
        });

        builder.create().show();
    }

}
