package de.generosoft.yannick.noticeboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import de.generosoft.yannick.noticeboard.messages.Message;
import de.generosoft.yannick.noticeboard.messages.UserMessagesRequest;
import de.generosoft.yannick.noticeboard.util.AfterTextChangedListener;
import de.generosoft.yannick.noticeboard.util.StringFilter;

public class ShowMessagesActivity extends AppCompatActivity {

    private String email;
    private String password;
    private LinkedList<Message> messages = new LinkedList<>();
    private ArrayList<String> strings;
    private ArrayAdapter<String> stringArrayAdapter;
    private UserMessagesRequest userMessagesRequest;
    private volatile boolean requesting = false;
    private volatile String filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages_actitvity);

        final EditText editText = findViewById(R.id.editText);
        editText.addTextChangedListener((AfterTextChangedListener) s -> {
            filter = s.toString();
            fillLayout();
        });

        final ListView listView = findViewById(R.id.list);
        strings = new ArrayList<>();
        stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings);
        listView.setAdapter(stringArrayAdapter);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        final UserMessagesRequest.Listener listener = new UserMessagesRequest.Listener() {
            @Override
            public void onSuccess(final LinkedList<Message> mes) {
                messages.clear();
                messages.addAll(mes);
                fillLayout();
                requesting = false;
            }

            @Override
            public void onFailure(final String errorMessage) {
                Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                toast.show();
                requesting = false;
            }
        };
        userMessagesRequest = new UserMessagesRequest(listener, this.getApplicationContext());
        refresh(null);
    }

    private synchronized void fillLayout() {
        strings.clear();
        stringArrayAdapter.notifyDataSetChanged();
        // reverse the list so the message with the highest id is displayed as first element
        Collections.reverse(messages);
        for (final Message message : messages) {
            final String s = message.getClassroom() + ": " + message.getPayload();
            if (StringFilter.filter(s, filter)) {
                strings.add(s);
            }
        }
        stringArrayAdapter.notifyDataSetChanged();
    }

    public synchronized void refresh(View view) {
        if (!requesting) {
            try {
                requesting = true;
                userMessagesRequest.execute(email, password);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getApplicationContext(), e.getClass().toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
