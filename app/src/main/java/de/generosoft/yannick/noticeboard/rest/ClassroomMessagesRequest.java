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
import java.util.List;

import de.generosoft.yannick.noticeboard.R;
import de.generosoft.yannick.noticeboard.rest.pojo.Message;

public class ClassroomMessagesRequest {

    private final RequestQueue requestQueue;
    private final ClassroomMessagesRequest.Listener listener;
    private final Response.Listener<JSONObject> responseListener;
    private final Response.ErrorListener errorListener;
    private final String url;

    public static ClassroomMessagesRequest getRequest(final ClassroomMessagesRequest.Listener listener, final Context context) {
        return new ClassroomMessagesRequest(listener, context, context.getString(R.string.login_url) + "/messages/classroom");
    }

    private ClassroomMessagesRequest(final ClassroomMessagesRequest.Listener listener, final Context context, final String url) {
        System.out.println(url);
        this.url = url;
        this.listener = listener;
        this.responseListener = response ->  {
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
            error.printStackTrace();
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

        public abstract void onSuccess(final List<Message> messages);

        public abstract void onFailure(final String message);
    }
}
