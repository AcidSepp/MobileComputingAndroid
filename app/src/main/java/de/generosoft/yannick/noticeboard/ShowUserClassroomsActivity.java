package de.generosoft.yannick.noticeboard;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import de.generosoft.yannick.noticeboard.classrooms.Classroom;
import de.generosoft.yannick.noticeboard.classrooms.UserClassroomsRequest;
import de.generosoft.yannick.noticeboard.messages.Message;
import de.generosoft.yannick.noticeboard.messages.UserMessagesRequest;
import de.generosoft.yannick.noticeboard.util.AfterTextChangedListener;
import de.generosoft.yannick.noticeboard.util.NavigationBarListener;
import de.generosoft.yannick.noticeboard.util.StringFilter;

public class ShowUserClassroomsActivity extends AppCompatActivity {

    private String email;
    private String password;
    private LinkedList<Classroom> classrooms = new LinkedList<>();
    private ArrayList<String> strings;
    private ArrayAdapter<String> stringArrayAdapter;
    private UserClassroomsRequest userClassroomsRequest;
    private volatile boolean requesting = false;
    private volatile String filter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_class_rooms);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);

        final Toolbar toolbar = findViewById(R.id.toolbar_);
        setSupportActionBar(toolbar);
        final ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        navigationView.setNavigationItemSelectedListener(new NavigationBarListener(email, password, getApplicationContext(), drawerLayout));

        final ListView listView = findViewById(R.id.list);
        strings = new ArrayList<>();
        stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings);
        listView.setAdapter(stringArrayAdapter);

        final EditText editText = findViewById(R.id.editText);
        editText.addTextChangedListener((AfterTextChangedListener) s -> {
            filter = s.toString();
            fillLayout();
        });

        final UserClassroomsRequest.Listener listener = new UserClassroomsRequest.Listener() {
            @Override
            public void onSuccess(final LinkedList<Classroom> rooms) {
                classrooms.clear();
                classrooms.addAll(rooms);
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
        userClassroomsRequest = new UserClassroomsRequest(listener, this.getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh(navigationView);
    }

    private synchronized void fillLayout() {
        strings.clear();
        stringArrayAdapter.notifyDataSetChanged();
        // reverse the list so the message with the highest id is displayed as first element
        Collections.reverse(classrooms);
        for (final Classroom classroom : classrooms) {
            final String s = classroom.getClassroomName() + ": " + classroom.getLecturer();
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
                userClassroomsRequest.execute(email, password);
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
