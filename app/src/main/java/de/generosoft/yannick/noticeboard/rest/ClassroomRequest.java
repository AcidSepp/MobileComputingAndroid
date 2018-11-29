package de.generosoft.yannick.noticeboard.rest;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import de.generosoft.yannick.noticeboard.R;

public class ClassroomRequest {

    private final RequestQueue requestQueue;
    private final ClassroomRequest.Listener listener;
    private final Response.Listener<JSONObject> responseListener;
    private final Response.ErrorListener errorListener;
    private final String url;


    public static ClassroomRequest getDeleteRequest(final ClassroomRequest.Listener listener, final Context context) {
        return new ClassroomRequest(listener, context, context.getString(R.string.login_url) + "/classrooms/delete");
    }

    public static ClassroomRequest getCreateRequest(final ClassroomRequest.Listener listener, final Context context) {
        return new ClassroomRequest(listener, context, context.getString(R.string.login_url) + "/classrooms/create");
    }

    private ClassroomRequest(final ClassroomRequest.Listener listener, final Context context, final String url) {
        this.url = url;
        this.listener = listener;
        this.responseListener = response -> listener.onSuccess();

        errorListener = error -> {
            try {
                final byte[] data = error.networkResponse.data;
                final JSONObject jsonObject = new JSONObject(new String(data));
                final String message = jsonObject.getString("message");
                listener.onFailure(message);
            } catch (final Exception e) {
                e.printStackTrace();
                listener.onFailure(e.getMessage());
            }
        };

        requestQueue = Volley.newRequestQueue(context);
    }

    public void execute(final String email, final String password, final String classroomName) throws JSONException {
        final JSONObject jsonObject = getJsonObject(email, password, classroomName);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, responseListener, errorListener);
        requestQueue.add(jsonObjectRequest);
    }

    private JSONObject getJsonObject(final String email, final String password, final String classroomName) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        jsonObject.put("classroomName", classroomName);
        return jsonObject;
    }

    public static abstract class Listener {

        public abstract void onSuccess();

        public abstract void onFailure(final String message);
    }
}
