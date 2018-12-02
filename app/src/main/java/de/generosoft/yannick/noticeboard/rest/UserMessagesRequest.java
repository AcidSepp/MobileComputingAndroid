package de.generosoft.yannick.noticeboard.rest;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;

import de.generosoft.yannick.noticeboard.R;
import de.generosoft.yannick.noticeboard.rest.pojo.Message;

public class UserMessagesRequest {

    private final RequestQueue requestQueue;
    private final UserMessagesRequest.Listener listener;
    private final Response.Listener<JSONObject> responseListener;
    private final Response.ErrorListener errorListener;
    private final String url;

    public UserMessagesRequest(final UserMessagesRequest.Listener listener, final Context context) {
        url = context.getString(R.string.login_url) + "/messages/student";
        this.listener = listener;
        this.responseListener = response -> {
            try {
                final JSONArray array = response.getJSONArray("messages");
                final LinkedList<Message> messages = new LinkedList<>();
                for (int i = 0; i < array.length(); i++) {
                    final JSONObject jsonObject = array.getJSONObject(i);
                    final String payload = jsonObject.getString("payload");
                    final String classRoom = jsonObject.getString("classRoom");
                    final int id = jsonObject.getInt("id");
                    final Message message = new Message(id, payload, classRoom);
                    messages.add(message);
                }
                Collections.sort(messages);
                listener.onSuccess(messages);
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

        public abstract void onSuccess(final LinkedList<Message> messages);

        public abstract void onFailure(final String errorMessage);
    }

}
