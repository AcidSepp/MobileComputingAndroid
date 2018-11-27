package de.generosoft.yannick.noticeboard.rest;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import de.generosoft.yannick.noticeboard.R;


public class LoginRequest  {

    private final RequestQueue requestQueue;
    private final Listener listener;
    private final Response.Listener<JSONObject> responseListener;
    private final Response.ErrorListener errorListener;
    private final String url;

    public LoginRequest(final Listener listener, final Context context) {
        url = context.getString(R.string.login_url) + "/login";
        this.listener = listener;
        this.responseListener = response -> {
            try {
                final String role = response.getString("role");
                listener.onSuccess(role);
            } catch (JSONException e) {
                e.printStackTrace();
                listener.onFailure(e.getMessage());
            }
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

    public void execute(final String email, final String password) throws JSONException {
        final JSONObject jsonObject = getJsonObject(email, password);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, responseListener, errorListener);
        requestQueue.add(jsonObjectRequest);
    }

    private JSONObject getJsonObject(String email, String password) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        return jsonObject;
    }

    public static abstract class Listener {

        public abstract void onSuccess(final String role);

        public abstract void onFailure(final String message);
    }

}
