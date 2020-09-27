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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alalaa Center on 26/07/2016.
 */
public class Parent extends User {

    public static class Payment {

        public static class PaymentItem {
            private int id;
            private double value;
            private String date;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public double getValue() {
                return value;
            }

            public void setValue(double value) {
                this.value = value;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    date = sdf2.format(sdf.parse(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                this.date = date;
            }

            @Override
            public boolean equals(Object obj) {
                return this.getId() == ((PaymentItem) obj).getId();
            }
        }

        private String studentName;
        private double total;
        private double netTotal;
        private double paymentSum;
        private List<PaymentItem> paymentItems = new ArrayList<>();

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public double getNetTotal() {
            return netTotal;
        }

        public void setNetTotal(double netTotal) {
            this.netTotal = netTotal;
        }

        public double getPaymentSum() {
            return paymentSum;
        }

        public void setPaymentSum(double paymentSum) {
            this.paymentSum = paymentSum;
        }

        public List<PaymentItem> getPaymentItems() {
            return paymentItems;
        }

        public void setPaymentItems(List<PaymentItem> paymentItems) {
            this.paymentItems.clear();
            this.paymentItems.addAll(paymentItems);
        }
    }

    private List<Student> studentList;
    private List<Payment> payments = new ArrayList<>();

    private StringRequest studentOfFamilyRequest;
    private StringRequest paymentRequest;

    public Parent() {
        this.setType(UserType.PARENT);
    }

    public String getStudentsOfFalmilySecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY;

        rawHashString += this.getAccessToken();
        rawHashString += LvsApplication.QUERY;

        rawHashString = rawHashString.toLowerCase();
        return LvsApplication.hashString(rawHashString);
    }

    public String getPaymentSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY
                + this.getAccessToken()
                + LvsApplication.QUERY;

        rawHashString = rawHashString.toLowerCase();
        return LvsApplication.hashString(rawHashString);
    }

    public String getStudentsOfFamilyURL() {
        String url = LvsApplication.SERVICE_URL
                + "Membership/StudentsOfFamily?"
                + "access_token=" + this.getAccessToken()
                + "&security_key=" + this.getStudentsOfFalmilySecurityKey();

        return url;

    }

    public String getPaymentURL() {
        String url = LvsApplication.SERVICE_URL
                + "Membership/Payments?access_token="
                + this.getAccessToken()
                + "&security_key=" + this.getPaymentSecurityKey();

        return url;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public StringRequest getStudentOfFamilyRequest() {

        studentOfFamilyRequest = new StringRequest(Request.Method.POST
                , getStudentsOfFamilyURL()
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
                        Parent.this.getCallBack().onFail(UserService.STUDENT_OF_FAMILY, msg);
                    } else {

                        JSONArray returnedData = response
                                .getJSONObject(0)
                                .getJSONArray("ReturnData");

                        List<Student> students = new ArrayList<>();

                        for (int i = 0; i < returnedData.length(); ++i) {
                            JSONObject child = returnedData.getJSONObject(i);

                            Student student = new Student();
                            student.setAccessToken(child.getString(User.ACCESS_TOKEN_KEY));
                            student.setFullName(child.getString(User.FULL_NAME_KEY));
                            student.setUserName(child.getString(User.USER_NAME_KEY));

                            students.add(student);
                        }
                        //
                        Parent.this.setStudentList(students);

                        Parent.this.getCallBack().onSucc(UserService.STUDENT_OF_FAMILY);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Parent.this.getCallBack().onFail(UserService.STUDENT_OF_FAMILY
                            , LvsApplication.APP_CTX.getString(R.string.error));

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Parent.this.getCallBack().onFail(UserService.STUDENT_OF_FAMILY
                        , LvsApplication.APP_CTX.getString(R.string.network_error_msg));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return studentOfFamilyRequest;
    }

    public StringRequest getPaymentRequest() {

        paymentRequest = new StringRequest(Request.Method.POST
                , this.getPaymentURL()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                try {
                    JSONArray response = new JSONArray(responseStr);

                    boolean hasError = response
                            .getJSONObject(0)
                            .getBoolean("HasError");
                    if (hasError) {
                        String errMsg = response
                                .getJSONObject(0)
                                .getString("ErrorMessage");
                        Parent.this.getCallBack().onFail(UserService.PAYMENT, errMsg);
                    } else {

                        JSONArray returnedData = response
                                .getJSONObject(0)
                                .getJSONArray("ReturnData");

                        payments.clear();

                        for (int i = 0; i < returnedData.length(); ++i) {

                            String studentName = returnedData
                                    .getJSONObject(i)
                                    .getString("StudentName");
                            double netTotal = returnedData
                                    .getJSONObject(i)
                                    .getDouble("NetTotal");
                            double balance = returnedData
                                    .getJSONObject(i)
                                    .getDouble("Balance");
                            double paymentSum = returnedData
                                    .getJSONObject(i)
                                    .getDouble("PaymentsSum");
                            JSONArray payments = returnedData
                                    .getJSONObject(i)
                                    .getJSONArray("Payments");

                            List<Payment.PaymentItem> paymentItems = new ArrayList<>();
                            for (int j = 0; j < payments.length(); ++j) {
                                int paymentNumber = payments
                                        .getJSONObject(j)
                                        .getInt("PaymentNumber");
                                double paymentAmount = payments
                                        .getJSONObject(j)
                                        .getDouble("PaymentAmount");
                                String paymentDate = payments
                                        .getJSONObject(j)
                                        .getString("PaymentDate");

                                Payment.PaymentItem paymentItem = new Payment.PaymentItem();
                                paymentItem.setDate(paymentDate);
                                paymentItem.setValue(paymentAmount);
                                paymentItem.setId(paymentNumber);

                                paymentItems.add(paymentItem);
                            }

                            Payment payment = new Payment();
                            payment.setStudentName(studentName);
                            payment.setTotal(netTotal);
                            payment.setNetTotal(balance);
                            payment.setPaymentSum(paymentSum);
                            payment.setPaymentItems(paymentItems);

                            Parent.this.getPayments().add(payment);

                        }

                        Parent.this.getCallBack().onSucc(UserService.PAYMENT);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Parent.this.getCallBack().onFail(UserService.PAYMENT
                            , LvsApplication.APP_CTX.getString(R.string.error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Parent.this.getCallBack().onFail(UserService.PAYMENT
                        , LvsApplication.APP_CTX.getString(R.string.network_error_msg));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return paymentRequest;
    }
}
