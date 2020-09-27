package com.littlevillageschool.lvs.Model;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Alalaa Center on 21/07/2016.
 */

public class Email implements Serializable {

    public class Attachment implements Serializable {

        private long downloadRequestId;
        private String fileName;
        private String fileUrl;
        private String filePath;
        private String fileSize;
        private String fileType;


        public String getFilePath() {
            filePath = LvsApplication.DOWNLOAD_PATH + fileName;
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public boolean isExist() {
            File file = new File(getFilePath());
            boolean res = file.exists() && file.canRead();

            return res;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public long getDownloadRequestId() {
            return downloadRequestId;
        }

        public void setDownloadRequestId(long downloadRequestId) {
            this.downloadRequestId = downloadRequestId;
        }

        public void downloadAttachment() {
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            String mimeType = myMime.getMimeTypeFromExtension(getFileType());
            String downloadUrl = this.getFileUrl();
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle("Downloading " + getFileName());
            request.setMimeType(mimeType);
            File file = new File(LvsApplication.DOWNLOAD_PATH);
            if (!file.exists())
                file.mkdir();
            request.setDestinationInExternalPublicDir(LvsApplication.DOWNLOAD_FOLDER, getFileName());

            this.setDownloadRequestId(LvsApplication.downloadManager.enqueue(request));

        }
    }

    public enum EmailService {
        DELETE, REPLY, REPLY_ALL, FORWARD, GET_MESSAGE,
        NEXT_PREV, COMPOSE, SAVE_ATTACHMENTS, GET_REPLY_RECEIVER, GET_REPLY_ALL_RECIEVER
    }

    public interface MyCallBack {
        public abstract void onSuccess(EmailService service);

        public abstract void onFail(EmailService service);
    }

    public static final String ID_KEY = "emailId";
    public static final String TITLE_KEY = "emailTitle";
    public static final String SENDER_KEY = "emailSender";
    public static final String DATE_KEY = "emailDate";
    public static final String ROLE_KEY = "emailRole";

    private String id;
    private String forwardId;
    private String title;
    private String sender;
    private String date;
    private String body;
    private boolean hasAttachments;
    private boolean isReaded;
    private int messageRole;
    private List<Person> persons;

    private Context ctx;
    private List<Attachment> attachments;

    private MyCallBack callBack;

    StringRequest deleteMsgRequest;
    StringRequest getMessageRequest;
    StringRequest getReplyReceiverRequest;
    StringRequest composeMessageRequest;
    StringRequest getAllReplyReceiverRequest;

    public String getReplyReceiverSecurityKey() {

        String rawHashString = LvsApplication.SECURITY_KEY;
        SortedMap<String, String> params = new TreeMap<>();

        params.put("access_token", LvsApplication.currUser.getAccessToken());
        params.put("message_id", this.getId());
        params.put("query", LvsApplication.QUERY);

        Set<String> set = params.keySet();
        for (String st : set)
            rawHashString += (params.get(st).replace(" ", "+"));

        rawHashString = rawHashString.toLowerCase();

        return LvsApplication.hashString(rawHashString);
    }

    public String getReqlyAllREceiverSecurityKey() {

        String rawHashString = LvsApplication.SECURITY_KEY;
        SortedMap<String, String> params = new TreeMap<>();

        params.put("access_token", LvsApplication.currUser.getAccessToken());
        params.put("message_id", this.getId());
        params.put("query", LvsApplication.QUERY);

        Set<String> set = params.keySet();
        for (String st : set)
            rawHashString += (params.get(st).replace(" ", "+"));

        rawHashString = rawHashString.toLowerCase();

        return LvsApplication.hashString(rawHashString);
    }

    public String getComposeMassageSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY;
        SortedMap<String, String> params = new TreeMap<>();

        params.put("access_token", LvsApplication.currUser.getAccessToken());
        params.put("subject", this.getTitle());
        if (this.getForwardId() != null)
            params.put("forward", this.getForwardId());
        params.put("query", LvsApplication.QUERY);


        Set<String> set = params.keySet();
        for (String st : set)
            rawHashString += (params.get(st).replace(" ", "+"));

        rawHashString = rawHashString.toLowerCase();

        return LvsApplication.hashString(rawHashString);

    }

    public String getDeleteMsgSecurityKey() {

        String rawHashString = LvsApplication.SECURITY_KEY;
        SortedMap<String, String> params = new TreeMap<>();

        params.put("access_token", LvsApplication.currUser.getAccessToken());
        params.put("message_id", this.getId());
        params.put("message_role", getMessageRole() + "");
        params.put("query", LvsApplication.QUERY);

        Set<String> set = params.keySet();
        for (String st : set)
            rawHashString += (params.get(st).replace(" ", "+"));

        rawHashString = rawHashString.toLowerCase();

        return LvsApplication.hashString(rawHashString);
    }

    public String getGetMessageSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY;
        SortedMap<String, String> params = new TreeMap<>();

        params.put("access_token", LvsApplication.currUser.getAccessToken());
        params.put("message_id", this.getId());
        params.put("query", LvsApplication.QUERY);

        Set<String> set = params.keySet();
        for (String st : set)
            rawHashString += (params.get(st).replace(" ", "+"));

        rawHashString = rawHashString.toLowerCase();

        return LvsApplication.hashString(rawHashString);
    }

    public String getComposeMessageURL() throws UnsupportedEncodingException {
        String url = LvsApplication.SERVICE_URL
                + "Messaging/Compose?"
                + "access_token=" + LvsApplication.currUser.getAccessToken()
                + "&subject=" + URLEncoder.encode(this.getTitle(), "UTF-8")
                + ((this.getForwardId() != null) ? "&forward=" + this.getForwardId() : "")
                + "&security_key=" + this.getComposeMassageSecurityKey();

        return url;
    }

    public String getGetReplyReceiverURL() {
        String url = LvsApplication.SERVICE_URL
                + "Messaging/GetReplyReceiver?"
                + "access_token=" + LvsApplication.currUser.getAccessToken() + "&"
                + "message_id=" + this.getId() + "&"
                + "security_key=" + this.getReplyReceiverSecurityKey();

        return url;
    }

    public String getGetReplyAllReceiverURL() {
        String url = LvsApplication.SERVICE_URL
                + "Messaging/GetReplyAllReceivers?"
                + "access_token=" + LvsApplication.currUser.getAccessToken() + "&"
                + "message_id=" + this.getId() + "&"
                + "security_key=" + this.getReqlyAllREceiverSecurityKey();

        return url;
    }

    public String getGetMessageURL() {
        String url = LvsApplication.SERVICE_URL
                + "Messaging/GetMessage?"
                + "access_token=" + LvsApplication.currUser.getAccessToken() + "&"
                + "message_id=" + this.getId() + "&"
                + "security_key=" + this.getGetMessageSecurityKey();

        return url;
    }

    public String getDeleteMsgURL() {
        String url = LvsApplication.SERVICE_URL
                + "Messaging/Delete?"
                + "access_token=" + LvsApplication.currUser.getAccessToken() + "&"
                + "message_id=" + this.getId() + "&"
                + "message_role=" + this.getMessageRole() + "&"
                + "security_key=" + this.getDeleteMsgSecurityKey();

        return url;
    }

    public String getForwardId() {
        return forwardId;
    }

    public void setForwardId(String forwardId) {
        this.forwardId = forwardId;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public boolean isReaded() {
        return isReaded;
    }

    public void setReaded(boolean readed) {
        isReaded = readed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMessageRole() {
        return messageRole;
    }

    public void setMessageRole(int messageRole) {
        this.messageRole = messageRole;
    }

    public MyCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public String getJsonPerson() throws JSONException {

        String json = "";
        List<JSONObject> objs = new ArrayList<>();
        for (Person p : this.getPersons()) {
            objs.add(new JSONObject(p.getId()));
        }
        JSONArray array = new JSONArray(objs);
        return array.toString();
    }

    public String getJsonAttachments() {
        String json = "";
        // TODO: 12/08/2016 getAttachmentsJson
        return json;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    @Override
    public boolean equals(Object o) {
        return this.getId().equals(((Email) o).getId());
    }

    public StringRequest getDeleteMsgRequest() {

        deleteMsgRequest = new StringRequest(Request.Method.POST
                , getDeleteMsgURL()
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

                        if (done) {
                            Email.this.callBack.onSuccess(EmailService.DELETE);

                        } else
                            Email.this.callBack.onFail(EmailService.DELETE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Email.this.callBack.onFail(EmailService.DELETE);
//                Toast.makeText(Email.this.ctx,
//                        LvsApplication.APP_CTX.getString(R.string.network_error_msg), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };


        return deleteMsgRequest;
    }

    public StringRequest getGetMessageRequest() {

        getMessageRequest = new StringRequest(Request.Method.POST
                , getGetMessageURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                try {
                    JSONArray response = new JSONArray(responseStr);
                    boolean hassError = response
                            .getJSONObject(0)
                            .getBoolean("HasError");

                    if (hassError) {
                        String errMsg = response
                                .getJSONObject(0)
                                .getString("ErrorMessage");
                        Toast.makeText(ctx, errMsg, Toast.LENGTH_SHORT).show();
                    } else {

                        JSONObject returnedData = response
                                .getJSONObject(0)
                                .getJSONObject("ReturnData");

                        String messageId = returnedData.getString("MessageId");
                        String title = returnedData.getString("Title");
                        String messageDate = returnedData.getString("MessageDate");
                        String senderTitle = returnedData.getString("SenderTitle");
                        String body = returnedData.getString("Body");
                        JSONArray attachments = returnedData.getJSONArray("Attachments");

                        List<Attachment> attaches = new ArrayList<>();
                        for (int i = 0; i < attachments.length(); i++) {
                            JSONObject attachment = attachments.getJSONObject(i);
                            String fileName = attachment.getString("FileName");
                            String filePath = attachment.getString("FilePath");
                            String fileSize = attachment.getString("FileSize");
                            String fileType = attachment.getString("FileType");

                            Attachment attachmentObj = new Attachment();
                            attachmentObj.setFileName(fileName);
                            attachmentObj.setFileUrl(filePath);
                            attachmentObj.setFileSize(fileSize);
                            attachmentObj.setFileType(attachmentObj.getFileName().split("\\.")[1]);

                            attaches.add(attachmentObj);
                        }

                        Email.this.setId(messageId);
                        Email.this.setTitle(title);
                        Email.this.setDate(messageDate);
                        Email.this.setSender(senderTitle);
                        Email.this.setBody(body);
                        Email.this.setAttachments(attaches);

                        Email.this.callBack.onSuccess(EmailService.GET_MESSAGE);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Email.this.ctx,
                            LvsApplication.APP_CTX.getString(R.string.error), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Email.this.callBack.onFail(EmailService.GET_MESSAGE);
//                Toast.makeText(Email.this.ctx,
//                        LvsApplication.APP_CTX.getString(R.string.network_error_msg), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return getMessageRequest;
    }

    public StringRequest getGetReplyReceiverRequest() {

        getReplyReceiverRequest = new StringRequest(Request.Method.POST
                , getGetReplyReceiverURL()
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

                        JSONObject returnedData = response
                                .getJSONObject(0)
                                .getJSONObject("ReturnData");

                        Person person = new Person();
                        String id = returnedData.getString("id");
                        String name = returnedData.getString("text");
                        person.setId(id);
                        person.setName(name);

                        List<Person> persons = new ArrayList<>();
                        persons.add(person);
                        Email.this.setPersons(persons);

                        Email.this.callBack.onSuccess(EmailService.GET_REPLY_RECEIVER);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Email.this.ctx,
                            LvsApplication.APP_CTX.getString(R.string.error), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(Email.this.ctx,
//                        LvsApplication.APP_CTX.getString(R.string.network_error_msg), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return getReplyReceiverRequest;
    }

    public StringRequest getGetAllReplyReceiverRequest() {

        getAllReplyReceiverRequest = new StringRequest(Request.Method.POST
                , getGetReplyAllReceiverURL()
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

                        JSONArray returnedData = response
                                .getJSONObject(0)
                                .getJSONArray("ReturnData");

                        List<Person> persons = new ArrayList<>();
                        for (int i = 0; i < returnedData.length(); ++i) {

                            JSONObject personObj = returnedData.getJSONObject(i);
                            Person person = new Person();
                            String id = personObj.getString("id");
                            String name = personObj.getString("text");
                            person.setId(id);
                            person.setName(name);
                            persons.add(person);
                        }


                        Email.this.setPersons(persons);

                        Email.this.callBack.onSuccess(EmailService.GET_REPLY_ALL_RECIEVER);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Email.this.ctx,
                            LvsApplication.APP_CTX.getString(R.string.error), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(Email.this.ctx,
//                        LvsApplication.APP_CTX.getString(R.string.network_error_msg), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return getAllReplyReceiverRequest;
    }

    public StringRequest getComposeMessageRequest() throws UnsupportedEncodingException {

        composeMessageRequest = new StringRequest(Request.Method.POST
                , getComposeMessageURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                try {
                    JSONArray response = new JSONArray(responseStr);
                    boolean hasError = response
                            .getJSONObject(0)
                            .getBoolean("HasError");
                    if (hasError) {
                        Email.this.callBack.onFail(EmailService.COMPOSE);
                    } else {
                        boolean returnedData = response
                                .getJSONObject(0)
                                .getBoolean("ReturnData");
                        if (returnedData)
                            Email.this.callBack.onSuccess(EmailService.COMPOSE);
                        else
                            Email.this.callBack.onFail(EmailService.COMPOSE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Email.this.callBack.onFail(EmailService.COMPOSE);
                    Toast.makeText(Email.this.ctx,
                            LvsApplication.APP_CTX.getString(R.string.error), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Email.this.callBack.onFail(EmailService.COMPOSE);
//                Toast.makeText(Email.this.ctx,
//                        LvsApplication.APP_CTX.getString(R.string.network_error_msg), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                try {
                    params.put("users", Email.this.getJsonPerson());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (attachments != null && attachments.size() > 0)
                    params.put("attachments", Email.this.getJsonAttachments());
                try {
                    params.put("html", URLEncoder.encode(Email.this.getBody(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                params.put("query", LvsApplication.QUERY);

                return params;
            }

        };

        return composeMessageRequest;
    }

    public void callService(EmailService service) {

        switch (service) {
            case DELETE:
                LvsApplication.volleyRequestQ.add(getDeleteMsgRequest());
                break;
            case GET_MESSAGE:
                LvsApplication.volleyRequestQ.add(getGetMessageRequest());
                break;
            case GET_REPLY_RECEIVER:
                LvsApplication.volleyRequestQ.add(getGetReplyReceiverRequest());
                break;
            case GET_REPLY_ALL_RECIEVER:
                LvsApplication.volleyRequestQ.add(getGetAllReplyReceiverRequest());
                break;
            case COMPOSE:
                try {
                    LvsApplication.volleyRequestQ.add(getComposeMessageRequest());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
