package com.littlevillageschool.lvs.Model;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.onesignal.OneSignal;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Alalaa Center on 26/07/2016.
 */
public class User {


    public enum UserService {
        LOGIN, CHANGE_PASSWORD, PROFILE, GET_ADDRESS_BOOK, STUDENT_OF_FAMILY, MATERIALS, SCHADUAL, ABSENTS,
        REGISTER_DEVICE, UNREGISTER_DEVICE, NOTES, PAYMENT
    }

    public enum UserType {STUDENT, PARENT}

    public static interface MyCallBack {
        public abstract void onSucc(UserService service);

        public abstract void onFail(UserService service, String errorMsg);
    }

    public static final String PASSWORD_KEY = "Password";
    public static final String ACCESS_TOKEN_KEY = "AccessToken";
    public static final String FULL_NAME_KEY = "FullName";
    public static final String USER_NAME_KEY = "UserName";
    public static final String TOKEN_KEY = "Token";
    public static final String TYPE_KEY = "Type";
    public static final String EMAIL_KEY = "Email";

    private String userName;
    private String passWord;
    private String fullName;
    private String accessToken;
    private String email;
    private String oldPassword;
    private String newPassword;
    private String deviceToken;
    private UserType type;
    private List<Person> persons;
    private MyCallBack callBack;

    StringRequest addressBookRequest;
    StringRequest loginRequest;
    StringRequest profileRequest;
    StringRequest changePasswordRequest;
    StringRequest registerDeviceRequest;
    StringRequest unRegisterDeviceRequest;

    public Person getPerson(String name) {
        for (Person p : persons) {
            if (p.getName().equalsIgnoreCase(name))
                return p;
        }

        return null;
    }


    public String getJsonStr() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public User() {

    }


    public String getLogInSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY;
        SortedMap<String, String> data = new TreeMap<>();

        data.put("password", LvsApplication.hashString(this.getPassWord()));
        data.put("username", this.getUserName());
        data.put("query", LvsApplication.QUERY);

        Set<String> set = data.keySet();
        for (String st : set) {
            rawHashString += (data.get(st).replace(" ", "+"));
        }
        rawHashString = rawHashString.toLowerCase();

        return LvsApplication.hashString(rawHashString);
    }

    public String getProfileSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY;

        rawHashString += this.getAccessToken();
        rawHashString += LvsApplication.QUERY;

        rawHashString = rawHashString.toLowerCase();
        return LvsApplication.hashString(rawHashString);

    }

    public String getChangePasswordSecuritykey() {

        String rawHashString = LvsApplication.SECURITY_KEY;
        SortedMap<String, String> data = new TreeMap<>();

        data.put("access_token", this.getAccessToken());
        data.put("new_password", this.getNewPassword());
        data.put("old_password", this.getOldPassword());
        data.put("query", LvsApplication.QUERY);

        Set<String> set = data.keySet();
        for (String st : set) {
            rawHashString += (data.get(st).replace(" ", "+"));
        }
        rawHashString = rawHashString.toLowerCase();
        return LvsApplication.hashString(rawHashString);
    }

    public String getAddressBookSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY +
                this.getAccessToken() +
                LvsApplication.QUERY;
        rawHashString = rawHashString.toLowerCase();

        return LvsApplication.hashString(rawHashString);
    }

    public String getRegisterSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY
                + this.getAccessToken()
                + this.getDeviceToken()
                + "false";
        String lang = Locale.getDefault().getLanguage().equalsIgnoreCase("ar") ? "2" : "1";
        rawHashString += lang;
        rawHashString += LvsApplication.QUERY;

        rawHashString = rawHashString.toLowerCase();
        return LvsApplication.hashString(rawHashString);
    }

    public String getUnRegisterSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY
                + this.getAccessToken()
                + this.getDeviceToken()
                + LvsApplication.QUERY;
        return LvsApplication.hashString(rawHashString.toLowerCase());
    }

    public String getLoginURL() {
        String url = LvsApplication.SERVICE_URL
                + "Membership/Login?"
                + "username=" + this.getUserName()
                + "&password=" + LvsApplication.hashString(this.getPassWord())
                + "&security_key=" + this.getLogInSecurityKey();

        return url;
    }

    public String getProfileURL() {
        String url = LvsApplication.SERVICE_URL
                + "Membership/Profile?"
                + "&access_token=" + this.getAccessToken()
                + "&security_key=" + this.getProfileSecurityKey();

        return url;
    }

    public String getChangePasswordURL() {
        String url = LvsApplication.SERVICE_URL
                + "Membership/ChangePassword?"
                + "access_token=" + this.getAccessToken()
                + "&old_password=" + this.getOldPassword()
                + "&new_password=" + this.getNewPassword()
                + "&security_key=" + this.getChangePasswordSecuritykey();

        return url;
    }

    public String getAddressBookUrl() {
        String url = LvsApplication.SERVICE_URL
                + "Messaging/GetAddressBook?"
                + "access_token=" + this.getAccessToken()
                + "&security_key=" + this.getAddressBookSecurityKey();

        return url;
    }

    public String getRegisterDeviceURL() {
        String url = LvsApplication.SERVICE_URL
                + "Membership/RegisterDevice?"
                + "access_token=" + this.getAccessToken()
                + "&device_token=" + this.getDeviceToken()
                + "&disable=false"
                + "&lang=" + (Locale.getDefault().getLanguage().equalsIgnoreCase("ar") ? "2" : "1")
                + "&security_key=" + this.getRegisterSecurityKey();

        return url;
    }

    public String getUnRegsiterURL() {
        String url = LvsApplication.SERVICE_URL
                + "Membership/UnregisterDevice?"
                + "access_token=" + this.getAccessToken()
                + "&device_token=" + this.getDeviceToken()
                + "&security_key=" + this.getUnRegisterSecurityKey();

        return url;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public MyCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord + LvsApplication.PASSWORD_SECURITY;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        User other = (User) o;
        return this.getAccessToken().equals(((User) o).getAccessToken());
    }

    public StringRequest getChangePasswordRequest() {

        changePasswordRequest = new StringRequest(Request.Method.POST
                , this.getChangePasswordURL()
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

                        User.this.callBack.onFail(UserService.CHANGE_PASSWORD, msg);
                    } else {
                        boolean returnedData = response
                                .getJSONObject(0)
                                .getBoolean("ReturnData");
                        if (returnedData)
                            User.this.callBack.onSucc(UserService.CHANGE_PASSWORD);
                        else
                            User.this.callBack.onFail(UserService.CHANGE_PASSWORD,
                                    LvsApplication.APP_CTX.getString(R.string.error));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    User.this.callBack.onFail(UserService.CHANGE_PASSWORD,
                            LvsApplication.APP_CTX.getString(R.string.error));

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                User.this.callBack.onFail(UserService.CHANGE_PASSWORD,
                        LvsApplication.APP_CTX.getString(R.string.network_error_msg));

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return changePasswordRequest;
    }

    public StringRequest getLoginRequest() {

        loginRequest = new StringRequest(Request.Method.POST
                , this.getLoginURL()
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
                        User.this.callBack.onFail(UserService.LOGIN, msg);
                    } else {

                        JSONObject returnedData = response
                                .getJSONObject(0)
                                .getJSONObject("ReturnData");

                        String accessToken = returnedData.getString("Token");
                        int type = returnedData.getInt("Type");
                        String userName = User.this.getUserName();
                        String password = User.this.passWord;

                        if (type == 1)
                            LvsApplication.currUser = new Student();
                        else
                            LvsApplication.currUser = new Parent();

                        LvsApplication.currUser.setCallBack(callBack);
                        LvsApplication.currUser.setPassWord(password);
                        LvsApplication.currUser.setUserName(userName);
                        LvsApplication.currUser.setAccessToken(accessToken);
                        if (type == 1)
                            LvsApplication.currUser.setType(UserType.STUDENT);
                        else
                            LvsApplication.currUser.setType(UserType.PARENT);
                        LvsApplication.ACCESS_TOKEN = accessToken;
                        User.this.callBack.onSucc(UserService.LOGIN);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    User.this.callBack.onFail(UserService.LOGIN,
                            LvsApplication.APP_CTX.getString(R.string.error));

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                User.this.callBack.onFail(UserService.LOGIN,
                        LvsApplication.APP_CTX.getString(R.string.network_error_msg));

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return this.loginRequest;
    }

    public StringRequest getProfileRequest() {

        profileRequest = new StringRequest(Request.Method.POST
                , this.getProfileURL()
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
                        User.this.callBack.onFail(UserService.PROFILE, msg);
                    } else {

                        JSONObject returnedData = response
                                .getJSONObject(0)
                                .getJSONObject("ReturnData");

                        String fullName = returnedData.getString("FullName");
                        String email = returnedData.getString("Email");

                        User.this.setEmail(email);
                        User.this.setFullName(fullName);

                        User.this.callBack.onSucc(UserService.PROFILE);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    User.this.callBack.onFail(UserService.PROFILE,
                            LvsApplication.APP_CTX.getString(R.string.error));

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                User.this.callBack.onFail(UserService.PROFILE,
                        LvsApplication.APP_CTX.getString(R.string.network_error_msg));

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }

        };

        return this.profileRequest;
    }

    public StringRequest getAddressBookRequest() {

        addressBookRequest = new StringRequest(Request.Method.POST
                , this.getAddressBookUrl()
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
                        User.this.callBack.onFail(UserService.GET_ADDRESS_BOOK, msg);
                    } else {
                        JSONArray returnedData = response
                                .getJSONObject(0)
                                .getJSONArray("ReturnData");

                        List<Person> persons = new ArrayList<Person>();
                        for (int i = 0; i < returnedData.length(); ++i) {
                            String id = returnedData
                                    .getJSONObject(i)
                                    .getString("id");

                            String name = returnedData
                                    .getJSONObject(i)
                                    .getString("text");
                            Person person = new Person();
                            person.setId(id);
                            person.setName(name);
                            persons.add(person);

                        }

                        User.this.setPersons(persons);
                        try {
                            User.this.callBack.onSucc(UserService.GET_ADDRESS_BOOK);
                        } catch (NullPointerException e) {

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    User.this.callBack.onFail(UserService.GET_ADDRESS_BOOK,
                            LvsApplication.APP_CTX.getString(R.string.error));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                User.this.callBack.onFail(UserService.GET_ADDRESS_BOOK,
                        LvsApplication.APP_CTX.getString(R.string.network_error_msg));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return this.addressBookRequest;
    }

    public StringRequest getRegisterDeviceRequest() {

        registerDeviceRequest = new StringRequest(Request.Method.POST
                , this.getRegisterDeviceURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray responseJsonArr = new JSONArray(response);
                    boolean hasError = responseJsonArr
                            .getJSONObject(0)
                            .getBoolean("HasError");
                    if (hasError) {
                        String errMsg = responseJsonArr
                                .getJSONObject(0)
                                .getString("ErrorMessage");
                        User.this.callBack.onFail(UserService.REGISTER_DEVICE,
                                errMsg);
                    } else {

                        boolean returnedData = responseJsonArr
                                .getJSONObject(0)
                                .getBoolean("ReturnData");

                        User.this.callBack.onSucc(UserService.REGISTER_DEVICE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    User.this.callBack.onFail(UserService.REGISTER_DEVICE,
                            LvsApplication.APP_CTX.getString(R.string.error));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                User.this.callBack.onFail(UserService.REGISTER_DEVICE,
                        LvsApplication.APP_CTX.getString(R.string.network_error_msg));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return registerDeviceRequest;
    }

    public StringRequest getUnRegisterDeviceRequest() {

        unRegisterDeviceRequest = new StringRequest(Request.Method.POST
                , this.getUnRegsiterURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);

                return params;
            }
        };

        return unRegisterDeviceRequest;
    }

    public void callService(UserService service) {

        switch (service) {

            case GET_ADDRESS_BOOK:
                LvsApplication.volleyRequestQ.add(this.getAddressBookRequest());
                break;
            case LOGIN:
                LvsApplication.volleyRequestQ.add(this.getLoginRequest());
                break;
            case CHANGE_PASSWORD:
                LvsApplication.volleyRequestQ.add(this.getChangePasswordRequest());
                break;
            case STUDENT_OF_FAMILY:
                LvsApplication.volleyRequestQ.add(((Parent) this).getStudentOfFamilyRequest());
                break;
            case PAYMENT:
                LvsApplication.volleyRequestQ.add(((Parent) this).getPaymentRequest());
                break;
            case PROFILE:
                LvsApplication.volleyRequestQ.add(this.getProfileRequest());
                break;
            case MATERIALS:
                LvsApplication.volleyRequestQ.add(((Student) this).getMaterialsRequest());
                break;
            case NOTES:
                LvsApplication.volleyRequestQ.add(((Student) this).getNotesRequest());
                break;
            case ABSENTS:
                LvsApplication.volleyRequestQ.add(((Student) this).getAbsenseRequest());
                break;
            case SCHADUAL:
                LvsApplication.volleyRequestQ.add(((Student) this).getSchadualRequest());
                break;
            case REGISTER_DEVICE:
                OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                    @Override
                    public void idsAvailable(String userId, String registrationId) {
                        User.this.setDeviceToken(userId);
                        LvsApplication.volleyRequestQ.add(User.this.getRegisterDeviceRequest());
                    }
                });
                break;

            case UNREGISTER_DEVICE:

                LvsApplication.volleyRequestQ.add(User.this.getUnRegisterDeviceRequest());
        }
    }
}
