package com.littlevillageschool.lvs.Model;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Raafat Alhoumaidy on 10/21/2016.
 */

public class Misc {

    public enum ServiceType {TEACHER_LIST, STAFF_LIST}

    public static interface CallBack {
        public void onSucc(ServiceType serviceType);

        public void onFail(ServiceType serviceType, String errMsg);
    }

    public static class Teacher {

        private String teacherName;
        private List<String> materials;

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getMaterials() {
            String res = "";
            for (String st : materials) {
                res += (st + ",");
            }
            if (res.length() > 0)
                res = res.substring(0, res.length() - 1);
            return res;
        }

        public void setMaterials(List<String> materials) {
            this.materials = materials;
        }
    }

    public static class Staff {

        private String roleName;
        private List<String> staffsNames;

        public String getStaffsNames() {

            String res = "";

            for (String st : staffsNames) {
                res += st + ", ";
            }
            if (res.length() > 0)
                res = res.substring(0, res.length() - 1);
            return res;
        }

        public void setStaffsNames(List<String> staffsNames) {
            this.staffsNames = staffsNames;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }
    }

    private List<Teacher> teacherList;
    private List<Staff> staffList;
    private CallBack callBack;

    private StringRequest teacherListRequest;
    private StringRequest staffListRequest;

    public Misc() {
        this.teacherList = new ArrayList<>();
        this.staffList = new ArrayList<>();
        callBack = new CallBack() {
            @Override
            public void onSucc(ServiceType serviceType) {

            }

            @Override
            public void onFail(ServiceType serviceType, String errMsg) {

            }
        };
    }

    public String getTeacherListSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY;
        String lang = Locale.getDefault().getLanguage().equalsIgnoreCase("ar") ? "2" : "1";

        rawHashString += lang + LvsApplication.QUERY;

        rawHashString = rawHashString.toLowerCase();
        return LvsApplication.hashString(rawHashString);
    }

    public String getStaffListSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY + LvsApplication.QUERY;

        return LvsApplication.hashString(rawHashString.toLowerCase());
    }

    public String getTeacherListURL() {
        String lang = Locale.getDefault().getLanguage().equalsIgnoreCase("ar") ? "2" : "1";
        String url = LvsApplication.SERVICE_URL
                + "Misc/TeachersList?"
                + "lang=" + lang
                + "&security_key=" + this.getTeacherListSecurityKey();

        return url;
    }

    public String getStaffListURL() {
        String url = LvsApplication.SERVICE_URL
                + "Misc/StaffList?"
                + "security_key=" + this.getStaffListSecurityKey();

        return url;
    }

    public CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public List<Staff> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<Staff> staffList) {
        this.staffList = staffList;
    }

    public List<Teacher> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<Teacher> teacherList) {
        this.teacherList = teacherList;
    }

    public StringRequest getTeacherListRequest() {

        teacherListRequest = new StringRequest(Request.Method.POST
                , this.getTeacherListURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray responseJson = new JSONArray(response);
                    boolean hasError = responseJson
                            .getJSONObject(0)
                            .getBoolean("HasError");
                    if (hasError) {
                        String errMsg = responseJson
                                .getJSONObject(0)
                                .getString("ErrorMessage");
                        Misc.this.callBack.onFail(ServiceType.TEACHER_LIST, errMsg);
                    } else {
                        JSONArray returnData = responseJson
                                .getJSONObject(0)
                                .getJSONArray("ReturnData");
                        Misc.this.teacherList.clear();
                        for (int i = 0; i < returnData.length(); ++i) {
                            Teacher teacher = new Teacher();
                            String name = returnData
                                    .getJSONObject(i)
                                    .getString("TeacherName");
                            JSONArray mats = returnData
                                    .getJSONObject(i)
                                    .getJSONArray("Materials");
                            List<String> teacherMats = new ArrayList<>();
                            for (int j = 0; j < mats.length(); ++j) {
                                String matName = mats.getString(j);
                                teacherMats.add(matName);
                            }

                            teacher.setTeacherName(name);
                            teacher.setMaterials(teacherMats);
                            Misc.this.teacherList.add(teacher);

                            Misc.this.callBack.onSucc(ServiceType.TEACHER_LIST);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Misc.this.callBack.onFail(ServiceType.TEACHER_LIST,
                            LvsApplication.APP_CTX.getString(R.string.error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Misc.this.callBack.onFail(ServiceType.TEACHER_LIST,
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

        return teacherListRequest;
    }

    public StringRequest getStaffListRequest() {

        staffListRequest = new StringRequest(Request.Method.POST
                , this.getStaffListURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    int lang = Locale.getDefault().getLanguage().equalsIgnoreCase("ar") ? 1 : 0;
                    JSONArray responseJson = new JSONArray(response);
                    boolean hasError = responseJson
                            .getJSONObject(0)
                            .getBoolean("HasError");
                    if (hasError) {
                        String errMsg = responseJson
                                .getJSONObject(0)
                                .getString("ErrorMessage");
                        Misc.this.callBack.onFail(ServiceType.STAFF_LIST, errMsg);
                    } else {
                        JSONArray returnedData = responseJson
                                .getJSONObject(0)
                                .getJSONArray("ReturnData");
                        Misc.this.staffList.clear();
                        for (int i = 0; i < returnedData.length(); ++i) {
                            JSONObject stafObj = returnedData.getJSONObject(i);
                            String roleName = stafObj.getString("RoleName");
                            JSONArray staffList = stafObj.getJSONArray("Staff");
                            List<String> staffNames = new ArrayList<>();
                            for (int j = 0; j < staffList.length(); ++j) {
                                staffNames.add(staffList.getString(j));
                            }

                            Staff staff = new Staff();
                            staff.setStaffsNames(staffNames);
                            staff.setRoleName(roleName.split("\\|")[lang]);

                            Misc.this.staffList.add(staff);
                        }

                        Misc.this.callBack.onSucc(ServiceType.STAFF_LIST);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Misc.this.callBack.onFail(ServiceType.STAFF_LIST
                            , LvsApplication.APP_CTX.getString(R.string.error));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Misc.this.callBack.onFail(ServiceType.STAFF_LIST,
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

        return staffListRequest;
    }

    public void callService(ServiceType serviceType) {

        switch (serviceType) {
            case STAFF_LIST:
                LvsApplication.volleyRequestQ.add(this.getStaffListRequest());
                break;
            case TEACHER_LIST:
                LvsApplication.volleyRequestQ.add(this.getTeacherListRequest());
                break;
        }
    }
}
