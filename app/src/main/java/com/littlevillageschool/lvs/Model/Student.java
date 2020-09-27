package com.littlevillageschool.lvs.Model;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.littlevillageschool.lvs.Adapters.AbsenceAdapter;
import com.littlevillageschool.lvs.Adapters.ExamAdapter;
import com.littlevillageschool.lvs.Adapters.MarksRecyclerAdapter;
import com.littlevillageschool.lvs.Adapters.NotesAdapter;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alalaa Center on 26/07/2016.
 */
public class Student extends User {


    public static class Material {

        public static class Score {

            public enum ExamType {NUMBER, SUM, AVG}

            private String testTitle;
            private double myScore;
            private ExamType examType;

            public String getTestTitle() {
                return testTitle;
            }

            public void setTestTitle(String testTitle) {
                this.testTitle = testTitle;
            }


            public double getMyScore() {
                return myScore;
            }

            public void setMyScore(double myScore) {
                this.myScore = myScore;
            }

            public ExamType getExamType() {
                return examType;
            }

            public void setExamType(ExamType examType) {
                this.examType = examType;
            }
        }

        private int materialMaxMark;
        private String materialTitle;
        private List<Score> scores;
        private ExamAdapter examAdapter;

        public Material() {
            scores = new ArrayList<>();
            examAdapter = new ExamAdapter(LvsApplication.APP_CTX
                    , R.layout.exams_item
                    , scores);
        }

        public int getMaterialMaxMark() {
            return materialMaxMark;
        }

        public void setMaterialMaxMark(int materialMaxMark) {
            this.materialMaxMark = materialMaxMark;
        }

        public String getMaterialTitle() {
            return materialTitle;
        }

        public void setMaterialTitle(String materialTitle) {
            this.materialTitle = materialTitle;
        }

        public List<Score> getScores() {
            return scores;
        }

        public ExamAdapter getExamAdapter() {
            return examAdapter;
        }

        public void setExamAdapter(ExamAdapter examAdapter) {
            this.examAdapter = examAdapter;
        }

        public void setScores(List<Score> scores) {
            this.scores = scores;
        }
    }

    public static class Absense {

        private int presentDays;
        private int absenseDays;
        private List<String> absenseDates;
        private com.littlevillageschool.lvs.Adapters.AbsenceAdapter absenseAdapter;

        public Absense() {
            absenseDates = new ArrayList<>();
            absenseAdapter = new AbsenceAdapter(LvsApplication.APP_CTX,
                    android.R.layout.simple_list_item_1,
                    absenseDates);
        }

        public int getPresentDays() {
            return presentDays;
        }

        public void setPresentDays(int presentDays) {
            this.presentDays = presentDays;
        }

        public int getAbsenseDays() {
            return absenseDays;
        }

        public void setAbsenseDays(int absenseDays) {
            this.absenseDays = absenseDays;
        }

        public List<String> getAbsenseDates() {
            return absenseDates;
        }

        public void setAbsenseDates(List<String> absenseDates) {
            this.absenseDates = absenseDates;
        }

        public AbsenceAdapter getAbsenseAdapter() {
            return absenseAdapter;
        }

        public void setAbsenseAdapter(AbsenceAdapter absenseAdapter) {
            this.absenseAdapter = absenseAdapter;
        }
    }

    public class Note {
        private int id;
        private String enderName;
        private int studentId;
        private String studentName;
        private int noteType;
        private String noteDate;
        private String noteText;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getEnderName() {
            return enderName;
        }

        public void setEnderName(String enderName) {
            this.enderName = enderName;
        }

        public int getStudentId() {
            return studentId;
        }

        public void setStudentId(int studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public int getNoteType() {
            return noteType;
        }

        public void setNoteType(int noteType) {
            this.noteType = noteType;
        }

        public String getNoteDate() {
            return noteDate;
        }

        public void setNoteDate(String noteDate) {
            this.noteDate = noteDate;
        }

        public String getNoteText() {
            return noteText;
        }

        public void setNoteText(String noteText) {
            this.noteText = noteText;
        }
    }

    private List<Material> materials;
    private Absense absense;
    private MarksRecyclerAdapter materialAdapter;
    private String schadualURL;
    private List<Note> notes;
    private NotesAdapter notesAdapter;

    StringRequest materialsRequest;
    StringRequest absenseRequest;
    StringRequest schadualRequest;
    StringRequest notesRequest;

    public Student() {
        this.setType(UserType.STUDENT);
        materials = new ArrayList<>();
        notes = new ArrayList<>();
        materialAdapter =
                new MarksRecyclerAdapter(LvsApplication.APP_CTX, R.layout.mark_item_layout, materials);
        notesAdapter = new NotesAdapter(LvsApplication.APP_CTX, R.layout.note_item_layout, notes);
        absense = new Absense();
    }

    public String getNotesSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY
                + this.getAccessToken()
                + LvsApplication.QUERY;

        return LvsApplication.hashString(rawHashString.toLowerCase());
    }

    public String getMaterialSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY
                + this.getAccessToken()
                + LvsApplication.QUERY;

        rawHashString = rawHashString.toLowerCase();
        return LvsApplication.hashString(rawHashString);
    }

    public String getAbsenseSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY
                + this.getAccessToken()
                + LvsApplication.QUERY;

        rawHashString = rawHashString.toLowerCase();
        return LvsApplication.hashString(rawHashString);
    }

    public String getScadulaSecurityKey() {
        String rawHashString = LvsApplication.SECURITY_KEY
                + this.getAccessToken()
                + LvsApplication.QUERY;

        rawHashString = rawHashString.toLowerCase();
        return LvsApplication.hashString(rawHashString);
    }

    public String getNoteURL() {
        String url = LvsApplication.SERVICE_URL
                + "Misc/GetNotes?"
                + "access_token=" + this.getAccessToken()
                + "&security_key=" + this.getNotesSecurityKey();

        return url;
    }

    public String getMaterialURL() {
        String url = LvsApplication.SERVICE_URL
                + "Scores/GetCurrent?"
                + "access_token=" + this.getAccessToken()
                + "&security_key=" + this.getMaterialSecurityKey();

        return url;
    }

    public String getGetSchadualURL() {
        String url = LvsApplication.SERVICE_URL
                + "Misc/ScheduleFile?"
                + "access_token=" + this.getAccessToken()
                + "&security_key=" + this.getScadulaSecurityKey();

        return url;
    }

    public String getAbsenseURL() {
        String url = LvsApplication.SERVICE_URL
                + "Attendance/GetAttendance?"
                + "access_token=" + this.getAccessToken()
                + "&security_key=" + this.getAbsenseSecurityKey();

        return url;
    }

    public NotesAdapter getNotesAdapter() {
        return notesAdapter;
    }

    public void setNotesAdapter(NotesAdapter notesAdapter) {
        this.notesAdapter = notesAdapter;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public String getSchadualURL() {
        return schadualURL;
    }

    public void setSchadualURL(String schadualURL) {
        this.schadualURL = schadualURL;
    }

    public Absense getAbsense() {
        return absense;
    }

    public void setAbsense(Absense absense) {
        this.absense = absense;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public MarksRecyclerAdapter getMaterialAdapter() {
        return materialAdapter;
    }

    public void setMaterialAdapter(MarksRecyclerAdapter materialAdapter) {
        this.materialAdapter = materialAdapter;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public StringRequest getMaterialsRequest() {

        materialsRequest = new StringRequest(Request.Method.POST
                , getMaterialURL()
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
                        Student.this.getCallBack().onFail(UserService.MATERIALS, msg);
                    } else {
                        JSONArray returnedData = response
                                .getJSONObject(0)
                                .getJSONArray("ReturnData");
                        List<Material> mats = new ArrayList<>();
                        for (int i = 0; i < returnedData.length(); ++i) {
                            JSONObject matObj = returnedData.getJSONObject(i);

                            String matTitle = matObj.getString("MaterialTitle");
                            int materialMaxScore = matObj.getInt("MaterialMaxMark");
                            JSONArray scoreArr = matObj.getJSONArray("Exams");

                            List<Material.Score> scores = new ArrayList<>();
                            for (int j = 0; j < scoreArr.length(); ++j) {

                                JSONObject scoreObj = scoreArr.getJSONObject(j);
                                String testTitle = scoreObj.getString("ExamTitle");
                                double myScore = scoreObj.getDouble("ExamMark");
                                int examType = scoreObj.getInt("ExamType");

                                Material.Score score = new Material.Score();
                                score.setMyScore(myScore);
                                score.setTestTitle(testTitle);
                                score.setExamType(Material.Score.ExamType.values()[examType - 1]);
                                if (score.getExamType() != Material.Score.ExamType.AVG
                                        && score.getExamType() != Material.Score.ExamType.SUM)
                                    scores.add(score);

                            }

                            Material mat = new Material();

                            mat.setMaterialTitle(matTitle);
                            mat.scores.addAll(scores);
                            mat.examAdapter.notifyDataSetChanged();

                            mats.add(mat);
                        }

                        Student.this.materials.clear();
                        Student.this.materials.addAll(mats);
                        Student.this.getCallBack().onSucc(UserService.MATERIALS);
                        materialAdapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Student.this.getCallBack().onFail(UserService.MATERIALS,
                            LvsApplication.APP_CTX.getString(R.string.error));

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Student.this.getCallBack().onFail(UserService.MATERIALS
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

        return materialsRequest;
    }

    public StringRequest getAbsenseRequest() {

        absenseRequest = new StringRequest(Request.Method.POST
                , getAbsenseURL()
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
                        Student.this.getCallBack().onFail(UserService.ABSENTS, msg);
                    } else {

                        JSONObject returnedData = response
                                .getJSONObject(0)
                                .getJSONObject("ReturnData");
                        int presentDays = returnedData.getInt("PresentDays");
                        int absenseDays = returnedData.getInt("AbsentDays");
                        JSONArray absenseDatesArr = returnedData.getJSONArray("AbsentDates");

                        List<String> dates = new ArrayList<>();
                        for (int i = 0; i < absenseDatesArr.length(); ++i) {
                            String date = absenseDatesArr.getString(i);
                            dates.add(date);
                        }
                        Absense absense = new Absense();


                        absense.setAbsenseDates(dates);
                        absense.setAbsenseDays(absenseDays);
                        absense.setPresentDays(presentDays);
                        Student.this.setAbsense(absense);
                        Student.this.absense.absenseAdapter.notifyDataSetChanged();
                        Student.this.getCallBack().onSucc(UserService.ABSENTS);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Student.this.getCallBack().onFail(UserService.ABSENTS,
                            LvsApplication.APP_CTX.getString(R.string.error));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Student.this.getCallBack().onFail(UserService.ABSENTS,
                        LvsApplication.APP_CTX.getString(R.string.error));

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", LvsApplication.QUERY);
                return params;
            }
        };

        return absenseRequest;
    }

    public StringRequest getSchadualRequest() {

        schadualRequest = new StringRequest(Request.Method.POST
                , getGetSchadualURL()
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
                    } else {
                        String returnedData = response
                                .getJSONObject(0)
                                .getString("ReturnData");
                        Student.this.setSchadualURL(returnedData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Student.this.getCallBack().onFail(UserService.SCHADUAL,
                            LvsApplication.APP_CTX.getString(R.string.error));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Student.this.getCallBack().onFail(UserService.SCHADUAL,
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

        return schadualRequest;
    }

    public StringRequest getNotesRequest() {

        notesRequest = new StringRequest(Request.Method.POST
                , this.getNoteURL()
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
                        Student.this.getCallBack().onFail(UserService.NOTES, msg);
                    } else {
                        JSONArray returnedData = response
                                .getJSONObject(0)
                                .getJSONArray("ReturnData");
                        List<Note> studentNotes = new ArrayList<>();
                        for (int i = 0; i < returnedData.length(); ++i) {
                            JSONObject notObj = returnedData.getJSONObject(i);
                            int id = notObj.getInt("Id");
                            String senderName = notObj.getString("SenderName");
                            String studentName = notObj.getString("StudentName");
                            int noteType = notObj.getInt("NoteType");
                            String noteText = notObj.getString("NoteText");
                            String noteDate = notObj.getString("NoteDate");

                            Note note = new Note();
                            note.setId(id);
                            note.setEnderName(senderName);
                            note.setNoteText(noteText);
                            note.setNoteDate(noteDate);
                            note.setNoteType(noteType);
                            note.setStudentName(studentName);

                            studentNotes.add(note);
                        }

                        Student.this.notes.clear();
                        Student.this.notes.addAll(studentNotes);
                        Student.this.getCallBack().onSucc(UserService.NOTES);
                        notesAdapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Student.this.getCallBack().onFail(UserService.NOTES,
                            LvsApplication.APP_CTX.getString(R.string.error));

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Student.this.getCallBack().onFail(UserService.NOTES
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

        return notesRequest;
    }
}
