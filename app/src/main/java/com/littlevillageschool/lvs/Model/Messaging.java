package com.littlevillageschool.lvs.Model;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.littlevillageschool.lvs.Adapters.EmailInboxRecyclerAdapter;
import com.littlevillageschool.lvs.Adapters.EmailOutboxRecyclerAdapter;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Alalaa Center on 06/08/2016.
 */
public class Messaging {




    public enum MessaginService {
        GET_INBOX, GET_NEW_INBOX, GET_UNREAD_INBOX, GET_OUTBOX,
        NOT_VIEWED_COUNT, UNREAD_COUNT, VIEW_MESSAGES
    }

    public interface MyCallBack {
        public abstract void onSuccess(Messaging.MessaginService service);

        public abstract void onFail(Messaging.MessaginService service);
    }

    public static final String MESSAGE_ROLE_KEY = "message_role";
    public static final String PAGE_SIZE_KEY = "page_size";
    public static final String PAGE_IDX_KEY = "page_index";
    public static final String SEARCH_ITEM_KEY = "search_item";
    public static final String NEW_KEY = "new";
    public static final String UNREAD_KEY = "unread";

    private List<Email> inbox;
    private List<Email> newInbox;
    private List<Email> unReadInbox;
    private List<Email> outbox;
    private EmailInboxRecyclerAdapter inboxAdapter;
    private EmailOutboxRecyclerAdapter outboxAdapter;
    private int pageSize = 15;
    private int inboxPgIdx = 0;
    private int outboxPgIdx = 0;
    private int totalInbox = 0;
    private int totalOutbox = 0;
    private int totalNewInbox = 0;
    private int totalUnreadInbox = 0;

    private MyCallBack callBack;

    private String searchItem = null;

    StringRequest inboxRequest;
    StringRequest outboxRequest;
    StringRequest unreadInboxRequest;
    StringRequest newInboxRequest;
    StringRequest notViewedCountRequest;
    StringRequest unReadCountRequest;
    StringRequest viewMessagesRequest;

    private Context ctx;

    public Messaging(Context ctx) {
        inbox = new ArrayList<>();
        outbox = new ArrayList<>();
        newInbox = new ArrayList<>();
        unReadInbox = new ArrayList<>();
        inboxAdapter = new EmailInboxRecyclerAdapter(ctx, R.layout.mail_item_layout, inbox);
        outboxAdapter = new EmailOutboxRecyclerAdapter(ctx, R.layout.mail_item_layout, outbox);

        this.ctx = ctx;
    }

    public String getOutboxSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY;
        SortedMap<String, String> data = new TreeMap<>();

        data.put(User.ACCESS_TOKEN_KEY, LvsApplication.currUser.getAccessToken());
        data.put(MESSAGE_ROLE_KEY, "1");
        data.put(PAGE_SIZE_KEY, this.getPageSize() + "");
        data.put(PAGE_IDX_KEY, this.getOutboxPgIdx() + "");
        if (this.getSearchItem() != null)
            data.put(SEARCH_ITEM_KEY, this.getSearchItem());
        data.put(UNREAD_KEY, "false");
        data.put(NEW_KEY, "false");
        data.put("query", LvsApplication.QUERY);


        Set<String> set = data.keySet();
        for (String st : set) {
            rawHashString += (data.get(st).replace(" ", "+"));
        }
        rawHashString = rawHashString.toLowerCase();

        return LvsApplication.hashString(rawHashString);
    }

    public String getInboxSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY;
        SortedMap<String, String> data = new TreeMap<>();

        data.put(User.ACCESS_TOKEN_KEY, LvsApplication.currUser.getAccessToken());
        data.put(MESSAGE_ROLE_KEY, "2");
        data.put(PAGE_SIZE_KEY, this.getPageSize() + "");
        data.put(PAGE_IDX_KEY, this.getInboxPgIdx() + "");
        if (this.getSearchItem() != null)
            data.put(SEARCH_ITEM_KEY, this.getSearchItem());
        data.put(UNREAD_KEY, "false");
        data.put(NEW_KEY, "false");
        data.put("query", LvsApplication.QUERY);

        Set<String> set = data.keySet();
        for (String st : set) {
            rawHashString += (data.get(st).replace(" ", "+"));
        }

        rawHashString = rawHashString.toLowerCase();
        return LvsApplication.hashString(rawHashString);
    }

    public String getUnReadInboxSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY;
        SortedMap<String, String> data = new TreeMap<>();

        data.put(User.ACCESS_TOKEN_KEY, LvsApplication.currUser.getAccessToken());
        data.put(MESSAGE_ROLE_KEY, "2");
        data.put(PAGE_SIZE_KEY, 1000 + "");
        data.put(PAGE_IDX_KEY, 0 + "");
        if (this.getSearchItem() != null)
            data.put(SEARCH_ITEM_KEY, this.getSearchItem());
        data.put(UNREAD_KEY, "true");
        data.put(NEW_KEY, "false");
        data.put("query", LvsApplication.QUERY);

        Set<String> set = data.keySet();
        for (String st : set) {
            rawHashString += (data.get(st).replace(" ", "+"));
        }
        rawHashString = rawHashString.toLowerCase();

        return LvsApplication.hashString(rawHashString);
    }

    public String getNewInboxSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY;
        SortedMap<String, String> data = new TreeMap<>();

        data.put(User.ACCESS_TOKEN_KEY, LvsApplication.currUser.getAccessToken());
        data.put(MESSAGE_ROLE_KEY, "2");
        data.put(PAGE_SIZE_KEY, "1000");
        data.put(PAGE_IDX_KEY, "0");
        if (this.getSearchItem() != null)
            data.put(SEARCH_ITEM_KEY, this.getSearchItem());
        data.put(UNREAD_KEY, "false");
        data.put(NEW_KEY, "true");
        data.put("query", LvsApplication.QUERY);


        Set<String> set = data.keySet();
        for (String st : set) {
            rawHashString += (data.get(st).replace(" ", "+"));
        }
        rawHashString = rawHashString.toLowerCase();

        return LvsApplication.hashString(rawHashString);
    }

    public String getNotViewedCountSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY
                + LvsApplication.currUser.getAccessToken()
                + LvsApplication.QUERY;

        return LvsApplication.hashString(rawHashString);
    }

    public String getUnReadCountSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY
                + LvsApplication.currUser.getAccessToken()
                + LvsApplication.QUERY;

        return LvsApplication.hashString(rawHashString);
    }

    public String getViewMessagesSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY;
        SortedMap<String, String> data = new TreeMap<>();

        data.put("access_token", LvsApplication.currUser.getAccessToken());
        data.put(PAGE_SIZE_KEY, this.getPageSize() + "");
        data.put(PAGE_IDX_KEY, this.getInboxPgIdx() + "");
        data.put("query", LvsApplication.QUERY);

        Set<String> set = data.keySet();
        for (String st : set) {
            rawHashString += (data.get(st).replace(" ", "+"));
        }

        rawHashString = rawHashString.toLowerCase();

        return LvsApplication.hashString(rawHashString);
    }


    public String getInboxURL() {
        String url = LvsApplication.SERVICE_URL
                + "Messaging/List?"
                + "access_token=" + LvsApplication.currUser.getAccessToken() + "&"
                + MESSAGE_ROLE_KEY + "=2&"
                + PAGE_SIZE_KEY + "=" + this.getPageSize() + "&"
                + PAGE_IDX_KEY + "=" + this.getInboxPgIdx() + "&"
                + ((this.getSearchItem() != null) ? SEARCH_ITEM_KEY + "=" + this.getSearchItem() + "&" : "")
                + UNREAD_KEY + "=false&"
                + NEW_KEY + "=false&"
                + "security_key=" + this.getInboxSecurityKey();

        return url;
    }

    public String getOutboxURL() {
        String url = LvsApplication.SERVICE_URL
                + "Messaging/List?"
                + "access_token=" + LvsApplication.currUser.getAccessToken() + "&"
                + MESSAGE_ROLE_KEY + "=1&"
                + PAGE_SIZE_KEY + "=" + this.getPageSize() + "&"
                + PAGE_IDX_KEY + "=" + this.getOutboxPgIdx() + "&"
                + ((this.getSearchItem() != null) ? SEARCH_ITEM_KEY + "=" + this.getSearchItem() + "&" : "")
                + UNREAD_KEY + "=false&"
                + NEW_KEY + "=false&"
                + "security_key=" + this.getOutboxSecurityKey();

        return url;
    }

    public String getUnreadInboxURL() {
        String url = LvsApplication.SERVICE_URL
                + "Messaging/List?"
                + "access_token=" + LvsApplication.currUser.getAccessToken() + "&"
                + MESSAGE_ROLE_KEY + "=2&"
                + PAGE_SIZE_KEY + "=" + 1000 + "&"
                + PAGE_IDX_KEY + "=" + 0 + "&"
                + ((this.getSearchItem() != null) ? SEARCH_ITEM_KEY + "=" + this.getSearchItem() + "&" : "")
                + UNREAD_KEY + "=true&"
                + NEW_KEY + "=false&"
                + "security_key=" + this.getUnReadInboxSecurityKey();

        return url;
    }

    public String getNewInboxURL() {
        String url = LvsApplication.SERVICE_URL
                + "Messaging/List?"
                + "access_token =" + LvsApplication.currUser.getAccessToken() + "&"
                + MESSAGE_ROLE_KEY + "=2&"
                + PAGE_SIZE_KEY + "=1000&"
                + PAGE_IDX_KEY + "=0&"
                + ((this.getSearchItem() != null) ? SEARCH_ITEM_KEY + "=" + this.getSearchItem() + "&" : "")
                + UNREAD_KEY + "=false&"
                + NEW_KEY + "=true&"
                + "security_key=" + this.getNewInboxSecurityKey();

        return url;
    }

    public String getNotViewedCountURL() {
        String url = LvsApplication.SERVICE_URL
                + "Messaging/NotViewedCount?"
                + "access_token=" + LvsApplication.currUser.getAccessToken() + "&"
                + "security_key=" + this.getNotViewedCountSecurityKey();

        return url;
    }

    public String getUnReadCountURL() {
        String url = LvsApplication.SERVICE_URL
                + "Messaging/UnreadCount?"
                + "access_token=" + LvsApplication.currUser.getAccessToken() + "&"
                + "security_key=" + this.getUnReadCountSecurityKey();

        return url;
    }

    public String getViewMessageURL() {
        String url = LvsApplication.SERVICE_URL
                + "Messaging/SetViewMessages?"
                + "access_token=" + LvsApplication.currUser.getAccessToken() + "&"
                + PAGE_SIZE_KEY + "=" + this.getPageSize() + "&"
                + PAGE_IDX_KEY + "=" + this.getInboxPgIdx() + "&"
                + "security_key=" + this.getViewMessagesSecurityKey();

        return url;

    }


    public void removeFromInbox(int position) {
        inbox.remove(position);
        inboxAdapter.notifyDataSetChanged();
    }

    public void removeFromUnreadInbox(int pos) {
        unReadInbox.remove(pos);
    }

    public void removeFromOutbox(int position) {
        outbox.remove(position);
        outboxAdapter.notifyDataSetChanged();
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public List<Email> getInbox() {
        return inbox;
    }

    public void setInbox(List<Email> inbox) {
        this.inbox = inbox;
    }

    public List<Email> getOutbox() {
        return outbox;
    }

    public void setOutbox(List<Email> outbox) {
        this.outbox = outbox;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getInboxPgIdx() {
        return inboxPgIdx;
    }

    public void setInboxPgIdx(int inboxPgIdx) {
        if (inboxPgIdx <= 0)
            inbox.clear();
        this.inboxPgIdx = inboxPgIdx;
    }

    public int getOutboxPgIdx() {
        return outboxPgIdx;
    }

    public void setOutboxPgIdx(int outboxPgIdx) {
        if (outboxPgIdx <= 0)
            outbox.clear();
        this.outboxPgIdx = outboxPgIdx;
    }

    public List<Email> getNewInbox() {
        return newInbox;
    }

    public void setNewInbox(List<Email> newInbox) {
        this.newInbox = newInbox;
    }

    public String getSearchItem() {
        return searchItem;
    }

    public void setSearchItem(String searchItem) {
        this.searchItem = searchItem;
    }

    public EmailInboxRecyclerAdapter getInboxAdapter() {
        return inboxAdapter;
    }

    public EmailOutboxRecyclerAdapter getOutboxAdapter() {
        return outboxAdapter;
    }

    public void setTotalInbox(int totalInbox) {
        this.totalInbox = totalInbox;
    }

    public void setTotalOutbox(int totalOutbox) {
        this.totalOutbox = totalOutbox;
    }

    public void setTotalNewInbox(int totalNewInbox) {
        this.totalNewInbox = totalNewInbox;
    }

    public void setTotalUnreadInbox(int totalUnreadInbox) {
        this.totalUnreadInbox = totalUnreadInbox;
    }

    public List<Email> getUnReadInbox() {
        return unReadInbox;
    }

    public void setUnReadInbox(List<Email> unReadInbox) {
        this.unReadInbox = unReadInbox;
    }

    public int getTotalInbox() {
        return totalInbox;
    }

    public int getTotalOutbox() {
        return totalOutbox;
    }

    public int getTotalNewInbox() {
        return totalNewInbox;
    }

    public int getTotalUnreadInbox() {
        return totalUnreadInbox;
    }

    public Context getCtx() {
        return ctx;
    }


    public StringRequest getOutboxRequest() {

        outboxRequest = new StringRequest(Request.Method.POST
                , getOutboxURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                try {
                    JSONArray response = new JSONArray(responseStr);
                    boolean hasError = response
                            .getJSONObject(0)
                            .getBoolean("HasError");
                    if (hasError) {
                        String msg = response
                                .getJSONObject(0)
                                .getString("ErrorMessage");
                        Toast.makeText(Messaging.this.ctx, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject returnedData = response
                                .getJSONObject(0)
                                .getJSONObject("ReturnData");

                        int total = returnedData.getInt("Total");
                        Messaging.this.setTotalInbox(total);
                        JSONArray messages = returnedData.getJSONArray("Messages");
                        List<Email> emails = new ArrayList<>();

                        for (int i = 0; i < messages.length(); ++i) {
                            JSONObject message = messages.getJSONObject(i);
                            String messageId = message.getString("MessageId");
                            String messageTitle = message.getString("Title");
                            String messageDate = message.getString("MessageDate");
                            String messageSender = message.getString("SenderTitle");
                            boolean hasAttachments = message.getBoolean("HasAttachments");
                            boolean isReaded = message.getBoolean("IsRead");

                            Email email = new Email();
                            email.setDate(messageDate);
                            email.setId(messageId);
                            email.setTitle(messageTitle);
                            email.setSender(messageSender);
                            email.setHasAttachments(hasAttachments);
                            email.setReaded(isReaded);
                            email.setMessageRole(1);

                            emails.add(email);
                        }

                        for (Email email : emails)
                            if (Messaging.this.outbox.indexOf(email) == -1)
                                Messaging.this.outbox.add(email);
                        Messaging.this.outboxAdapter.notifyDataSetChanged();
                        Messaging.this.callBack.onSuccess(MessaginService.GET_OUTBOX);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Messaging.this.callBack.onFail(MessaginService.GET_OUTBOX);
                    Toast.makeText(Messaging.this.ctx,
                            LvsApplication.APP_CTX.getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(Messaging.this.ctx,
//                        LvsApplication.APP_CTX.getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
                Messaging.this.callBack.onFail(MessaginService.GET_OUTBOX);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return outboxRequest;
    }

    public StringRequest getInboxRequest() {

        inboxRequest = new StringRequest(Request.Method.POST
                , getInboxURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                try {
                    JSONArray response = new JSONArray(responseStr);
                    boolean hasError = response
                            .getJSONObject(0)
                            .getBoolean("HasError");
                    if (hasError) {
                        String msg = response
                                .getJSONObject(0)
                                .getString("ErrorMessage");
                        Toast.makeText(Messaging.this.ctx, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject returnedData = response
                                .getJSONObject(0)
                                .getJSONObject("ReturnData");

                        int total = returnedData.getInt("Total");
                        Messaging.this.setTotalInbox(total);
                        JSONArray messages = returnedData.getJSONArray("Messages");
                        List<Email> emails = new ArrayList<>();

                        for (int i = 0; i < messages.length(); ++i) {
                            JSONObject message = messages.getJSONObject(i);
                            String messageId = message.getString("MessageId");
                            String messageTitle = message.getString("Title");
                            String messageDate = message.getString("MessageDate");
                            String messageSender = message.getString("SenderTitle");
                            boolean hasAttachments = message.getBoolean("HasAttachments");
                            boolean isReaded = message.getBoolean("IsRead");

                            Email email = new Email();
                            email.setDate(messageDate);
                            email.setId(messageId);
                            email.setTitle(messageTitle);
                            email.setSender(messageSender);
                            email.setHasAttachments(hasAttachments);
                            email.setReaded(isReaded);
                            email.setMessageRole(2);

                            emails.add(email);
                        }
                        for (Email email : emails)
                            if (Messaging.this.inbox.indexOf(email) == -1)
                                Messaging.this.inbox.add(email);
                        Messaging.this.inboxAdapter.notifyDataSetChanged();

                        Messaging.this.callBack.onSuccess(MessaginService.GET_INBOX);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Messaging.this.ctx,
                            LvsApplication.APP_CTX.getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(Messaging.this.ctx,
//                        LvsApplication.APP_CTX.getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
                Messaging.this.callBack.onFail(MessaginService.GET_INBOX);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return inboxRequest;
    }

    public StringRequest getUnreadInboxRequest() {

        unreadInboxRequest = new StringRequest(Request.Method.POST
                , getUnreadInboxURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                try {
                    JSONArray response = new JSONArray(responseStr);
                    boolean hasError = response
                            .getJSONObject(0)
                            .getBoolean("HasError");
                    if (hasError) {
                        String msg = response
                                .getJSONObject(0)
                                .getString("ErrorMessage");
                        Toast.makeText(Messaging.this.ctx, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject returnedData = response
                                .getJSONObject(0)
                                .getJSONObject("ReturnData");

                        int total = returnedData.getInt("Total");
                        Messaging.this.setTotalUnreadInbox(total);
                        JSONArray messages = returnedData.getJSONArray("Messages");
                        List<Email> emails = new ArrayList<>();

                        for (int i = 0; i < messages.length(); ++i) {
                            JSONObject message = messages.getJSONObject(i);
                            String messageId = message.getString("MessageId");
                            String messageTitle = message.getString("Title");
                            String messageDate = message.getString("MessageDate");
                            String messageSender = message.getString("SenderTitle");
                            boolean hasAttachments = message.getBoolean("HasAttachments");
                            boolean isReaded = message.getBoolean("IsRead");

                            Email email = new Email();
                            email.setDate(messageDate);
                            email.setId(messageId);
                            email.setTitle(messageTitle);
                            email.setSender(messageSender);
                            email.setHasAttachments(hasAttachments);
                            email.setReaded(isReaded);
                            email.setMessageRole(2);

                            emails.add(email);
                        }

                        Messaging.this.unReadInbox.clear();
                        Messaging.this.unReadInbox.addAll(emails);

                        Messaging.this.callBack.onSuccess(MessaginService.GET_UNREAD_INBOX);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Messaging.this.ctx,
                            LvsApplication.APP_CTX.getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(Messaging.this.ctx,
//                        LvsApplication.APP_CTX.getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
                Messaging.this.callBack.onFail(MessaginService.GET_UNREAD_INBOX);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };


        return unreadInboxRequest;
    }

    public StringRequest getNewInboxRequest() {

        newInboxRequest = new StringRequest(Request.Method.POST
                , getNewInboxURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                try {
                    JSONArray response = new JSONArray(responseStr);
                    boolean hasError = response
                            .getJSONObject(0)
                            .getBoolean("HasError");
                    if (hasError) {
                        String msg = response
                                .getJSONObject(0)
                                .getString("ErrorMessage");
                        Toast.makeText(Messaging.this.ctx, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject returnedData = response
                                .getJSONObject(0)
                                .getJSONObject("ReturnData");

                        int total = returnedData.getInt("Total");
                        Messaging.this.setTotalNewInbox(total);
                        JSONArray messages = returnedData.getJSONArray("Messages");
                        List<Email> emails = new ArrayList<>();

                        for (int i = 0; i < messages.length(); ++i) {
                            JSONObject message = messages.getJSONObject(i);
                            String messageId = message.getString("MessageId");
                            String messageTitle = message.getString("Title");
                            String messageDate = message.getString("MessageDate");
                            String messageSender = message.getString("SenderTitle");
                            boolean hasAttachments = message.getBoolean("HasAttachments");
                            boolean isReaded = message.getBoolean("IsRead");

                            Email email = new Email();
                            email.setDate(messageDate);
                            email.setId(messageId);
                            email.setTitle(messageTitle);
                            email.setSender(messageSender);
                            email.setHasAttachments(hasAttachments);
                            email.setReaded(isReaded);
                            email.setMessageRole(2);

                            emails.add(email);
                        }

                        Messaging.this.newInbox.clear();
                        Messaging.this.newInbox.addAll(emails);

                        Messaging.this.callService(MessaginService.VIEW_MESSAGES);
                        Messaging.this.callBack.onSuccess(MessaginService.GET_NEW_INBOX);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Messaging.this.ctx,
                            LvsApplication.APP_CTX.getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Messaging.this.callBack.onFail(MessaginService.GET_NEW_INBOX);
//                Toast.makeText(Messaging.this.ctx,
//                        LvsApplication.APP_CTX.getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };


        return newInboxRequest;
    }

    public StringRequest getNotViewedCountRequest() {

        notViewedCountRequest = new StringRequest(Request.Method.POST
                , getNotViewedCountURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                try {
                    JSONArray response = new JSONArray(responseStr);
                    boolean hasError = response
                            .getJSONObject(0)
                            .getBoolean("HasError");

                    if (hasError) {

                        String msg = response
                                .getJSONObject(0)
                                .getString("ErrorMessage");
                        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();

                    } else {
                        int count = response
                                .getJSONObject(0)
                                .getInt("ReturnObject");
                        Messaging.this.totalNewInbox = count;

                        Messaging.this.callBack.onSuccess(MessaginService.NOT_VIEWED_COUNT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Messaging.this.ctx,
                            LvsApplication.APP_CTX.getString(R.string.error), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Messaging.this.callBack.onFail(MessaginService.NOT_VIEWED_COUNT);
//                Toast.makeText(Messaging.this.ctx,
//                        LvsApplication.APP_CTX.getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return notViewedCountRequest;
    }

    public StringRequest getUnReadCountRequest() {

        unReadCountRequest = new StringRequest(Request.Method.POST
                , getUnReadCountURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                try {
                    JSONArray response = new JSONArray(responseStr);
                    boolean hasError = response
                            .getJSONObject(0)
                            .getBoolean("HasError");

                    if (hasError) {

                        String msg = response
                                .getJSONObject(0)
                                .getString("ErrorMessage");
                        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();

                    } else {
                        int count = response
                                .getJSONObject(0)
                                .getInt("ReturnData");
                        Messaging.this.totalUnreadInbox = count;

                        Messaging.this.callBack.onSuccess(MessaginService.UNREAD_COUNT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Messaging.this.ctx,
                            LvsApplication.APP_CTX.getString(R.string.error), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Messaging.this.callBack.onFail(MessaginService.UNREAD_COUNT);
                Toast.makeText(Messaging.this.ctx,
                        LvsApplication.APP_CTX.getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return unReadCountRequest;
    }

    public StringRequest getViewMessagesRequest() {

        viewMessagesRequest = new StringRequest(Request.Method.POST
                , getViewMessageURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                try {
                    JSONArray response = new JSONArray(responseStr);
                    boolean hasError = response
                            .getJSONObject(0)
                            .getBoolean("HasError");

                    if (hasError) {

                        String msg = response
                                .getJSONObject(0)
                                .getString("ErrorMessage");
                        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();

                    } else {
                        boolean done = response
                                .getJSONObject(0)
                                .getBoolean("ReturnData");

                        if (done)
                            Messaging.this.callBack.onSuccess(MessaginService.VIEW_MESSAGES);
                        else
                            Messaging.this.callBack.onFail(MessaginService.VIEW_MESSAGES);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Messaging.this.ctx,
                            LvsApplication.APP_CTX.getString(R.string.error), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Messaging.this.callBack.onFail(MessaginService.VIEW_MESSAGES);
//                Toast.makeText(Messaging.this.ctx,
//                        LvsApplication.APP_CTX.getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return viewMessagesRequest;
    }


    public void callService(MessaginService service) {

        switch (service) {

            case GET_OUTBOX:
                LvsApplication.volleyRequestQ.add(getOutboxRequest());
                break;
            case GET_INBOX:
                LvsApplication.volleyRequestQ.add(this.getInboxRequest());
                break;

            case GET_NEW_INBOX:
                LvsApplication.volleyRequestQ.add(this.getNewInboxRequest());
                break;

            case GET_UNREAD_INBOX:
                LvsApplication.volleyRequestQ.add(this.getUnreadInboxRequest());
                break;

            case NOT_VIEWED_COUNT:
                LvsApplication.volleyRequestQ.add(this.getNotViewedCountRequest());
                break;

            case UNREAD_COUNT:
                LvsApplication.volleyRequestQ.add(this.getUnReadCountRequest());
                break;

            case VIEW_MESSAGES:
                LvsApplication.volleyRequestQ.add(this.getViewMessagesRequest());
                break;
        }
    }
}
