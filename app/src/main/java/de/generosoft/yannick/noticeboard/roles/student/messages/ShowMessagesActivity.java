package de.generosoft.yannick.noticeboard.roles.student.messages;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import de.generosoft.yannick.noticeboard.R;
import de.generosoft.yannick.noticeboard.rest.UserMessagesRequest;
import de.generosoft.yannick.noticeboard.rest.pojo.Message;
import de.generosoft.yannick.noticeboard.util.AfterTextChangedListener;
import de.generosoft.yannick.noticeboard.util.NavigationBarListener;
import de.generosoft.yannick.noticeboard.util.StringFilter;

public class ShowMessagesActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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
        setContentView(R.layout.activity_student_messsages);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        final Toolbar toolbar = findViewById(R.id.toolbar_);
        toolbar.setTitle(R.string.show_my_messages);
        setSupportActionBar(toolbar);
        final ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        navigationView.setNavigationItemSelectedListener(new NavigationBarListener(email, password, getApplicationContext(), drawerLayout));

        final View viewById = toolbar.findViewById(R.id.refreshButton);
        viewById.setOnClickListener(this::refresh);


        final EditText editText = findViewById(R.id.editText);
        editText.addTextChangedListener((AfterTextChangedListener) s -> {
            filter = s.toString();
            fillLayout();
        });

        final ListView listView = findViewById(R.id.list);
        strings = new ArrayList<>();
        stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings);
        listView.setAdapter(stringArrayAdapter);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh(navigationView);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
