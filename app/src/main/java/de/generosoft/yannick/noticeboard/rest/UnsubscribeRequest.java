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

public class UnsubscribeRequest {

    private final RequestQueue requestQueue;
    private final UnsubscribeRequest.Listener listener;
    private final Response.Listener<JSONObject> responseListener;
    private final Response.ErrorListener errorListener;
    private final String url;

    public UnsubscribeRequest(final UnsubscribeRequest.Listener listener, final Context context) {
        url = context.getString(R.string.login_url) + "/classrooms/unsubscribe";
        this.listener = listener;
        this.responseListener = response -> listener.onSuccess();

        errorListener = error -> {
            error.printStackTrace();
            try {
                final byte[] data = error.networkResponse.data;
                final JSONObject jsonObject = new JSONObject(new String(data));
                final String message = jsonObject.getString("message");
                listener.onFailure(message);
            } catch (Exception e) {
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
        jsonObject.put("classRoomName", classroomName);
        return jsonObject;
    }

    public static abstract class Listener {

        public abstract void onSuccess();

        public abstract void onFailure(final String message);
    }

}
