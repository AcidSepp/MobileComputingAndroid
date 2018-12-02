package de.generosoft.yannick.noticeboard.roles.lecturer.classroom;


import android.app.AlertDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.generosoft.yannick.noticeboard.R;
import de.generosoft.yannick.noticeboard.rest.ClassroomMessagesRequest;
import de.generosoft.yannick.noticeboard.rest.PostMessageRequest;
import de.generosoft.yannick.noticeboard.rest.pojo.Message;
import de.generosoft.yannick.noticeboard.util.AfterTextChangedListener;
import de.generosoft.yannick.noticeboard.util.NavigationBarListener;
import de.generosoft.yannick.noticeboard.util.StringFilter;

public class LecturerClassroomActivity extends AppCompatActivity {

    private String email;
    private String password;
    private String classroomName;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LecturerMessagesAdapter messagesAdapter;
    private LinkedList<Message> shownMessages = new LinkedList<>();
    private LinkedList<Message> allMessages = new LinkedList<>();
    private String filter;
    private boolean requesting = false;
    private ClassroomMessagesRequest messagesRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_classroom);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        classroomName = getIntent().getStringExtra("classroomName");

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);

        final ListView listView = findViewById(R.id.list);
        messagesAdapter = new LecturerMessagesAdapter(this, shownMessages, email, password);
        listView.setAdapter(messagesAdapter);

        final Toolbar toolbar = findViewById(R.id.toolbar_);
        toolbar.setTitle(classroomName);
        setSupportActionBar(toolbar);
        final ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        navigationView.setNavigationItemSelectedListener(new NavigationBarListener(email, password, getApplicationContext(), drawerLayout));

        final View viewById = toolbar.findViewById(R.id.refreshButton);
        viewById.setOnClickListener(this::refresh);

        final EditText filterText = findViewById(R.id.editText);
        filterText.addTextChangedListener((AfterTextChangedListener) s -> {
            filter = s.toString();
            fillLayout();
        });

        final ClassroomMessagesRequest.Listener listener = new ClassroomMessagesRequest.Listener() {

            @Override
            public void onSuccess(List<Message> messages) {
                allMessages.clear();
                allMessages.addAll(messages);
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
        messagesRequest = ClassroomMessagesRequest.getRequest(listener, this.getApplicationContext());

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this::createMessage);
    }

    private void createMessage(final View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Message");
        builder.setMessage("Enter Message:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        final PostMessageRequest.Listener listener = new PostMessageRequest.Listener() {

            @Override
            public void onSuccess() {
                final Toast created = Toast.makeText(getApplicationContext(), "posted message", Toast.LENGTH_SHORT);
                refresh(null);
                created.show();
            }

            @Override
            public void onFailure(final String message) {
                final Toast failed = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                failed.show();
            }
        };
        final PostMessageRequest postMessageRequest = PostMessageRequest.getRequest(listener, getApplicationContext());

        builder.setPositiveButton("Post", (dialog, which) -> {
            try {
                postMessageRequest.execute(email, password, classroomName, input.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh(navigationView);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private synchronized void fillLayout() {
        shownMessages.clear();
        messagesAdapter.notifyDataSetChanged();
        for (final Message message : allMessages) {
            final String s = message.getPayload();
            if (StringFilter.filter(s, filter)) {
                shownMessages.add(message);
            }
        }
        // reverse list so the message with the highest id is displayed on the top
        Collections.reverse(shownMessages);
        messagesAdapter.notifyDataSetChanged();
    }

    public synchronized void refresh(final View view) {
        if (!requesting) {
            try {
                requesting = true;
                messagesRequest.execute(email, password, classroomName);
            } catch (final JSONException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getApplicationContext(), e.getClass().toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
