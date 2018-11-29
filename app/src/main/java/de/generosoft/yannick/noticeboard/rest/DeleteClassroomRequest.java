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

public class DeleteClassroomRequest {

    private final RequestQueue requestQueue;
    private final DeleteClassroomRequest.Listener listener;
    private final Response.Listener<JSONObject> responseListener;
    private final Response.ErrorListener errorListener;
    private final String url;

    public DeleteClassroomRequest(final DeleteClassroomRequest.Listener listener, final Context context) {
        url = context.getString(R.string.login_url) + "/classrooms/delete";
        this.listener = listener;
        this.responseListener = response -> {
                listener.onSuccess();
        };

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
