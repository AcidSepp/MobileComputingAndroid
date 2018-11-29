package de.generosoft.yannick.noticeboard.rest;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;

import de.generosoft.yannick.noticeboard.R;
import de.generosoft.yannick.noticeboard.rest.pojo.Classroom;

public class ClassroomsRequest {

    private final RequestQueue requestQueue;
    private final ClassroomsRequest.Listener listener;
    private final Response.Listener<JSONObject> responseListener;
    private final Response.ErrorListener errorListener;
    private final String url;

    public static ClassroomsRequest getLecturerClassroomsRequest(final ClassroomsRequest.Listener listener, final Context context) {
        return new ClassroomsRequest(listener, context, context.getString(R.string.login_url) + "/classrooms/owned");
    }

    public static ClassroomsRequest getStudentAllClassroomsRequest(final ClassroomsRequest.Listener listener, final Context context) {
        return new ClassroomsRequest(listener, context, context.getString(R.string.login_url) + "/classrooms/all");
    }

    public static ClassroomsRequest getStudentSubscribedClassroomsRequest(final ClassroomsRequest.Listener listener, final Context context) {
        return new ClassroomsRequest(listener, context, context.getString(R.string.login_url) + "/classrooms/subscribed");
    }

    private ClassroomsRequest(final ClassroomsRequest.Listener listener, final Context context, final String url) {
        this.url = url;
        this.listener = listener;
        this.responseListener = response -> {
            try {
                final JSONArray array = response.getJSONArray("classrooms");
                final LinkedList<Classroom> classrooms = new LinkedList<>();
                for (int i = 0; i < array.length(); i++) {
                    final JSONObject jsonObject = array.getJSONObject(i);
                    final String classroomName = jsonObject.getString("classroomName");
                    final String lecturer = jsonObject.getString("lecturer");
                    final boolean subscribed = jsonObject.getBoolean("subscribed");
                    final Classroom classroom = new Classroom(classroomName, lecturer, subscribed);
                    classrooms.add(classroom);
                }
                Collections.sort(classrooms);
                listener.onSuccess(classrooms);
            } catch (JSONException e) {
                e.printStackTrace();
                listener.onFailure(e.getMessage());
            }
        };

        errorListener = error -> {
            try {
                final byte[] data = error.networkResponse.data;
                final JSONObject jsonObject = new JSONObject(new String(data));
                final String errorMessage = jsonObject.getString("message");
                listener.onFailure(errorMessage);
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailure(e.getClass().toString());
            }
        };

        requestQueue = Volley.newRequestQueue(context);
    }

    public void execute(final String email, final String password) throws JSONException {
        final JSONObject jsonObject = getJsonObject(email, password);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, responseListener, errorListener);
        requestQueue.add(jsonObjectRequest);
    }

    private JSONObject getJsonObject(final String email, final String password) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        return jsonObject;
    }

    public static abstract class Listener {

        public abstract void onSuccess(final LinkedList<Classroom> classrooms);

        public abstract void onFailure(final String errorMessage);
    }

}
