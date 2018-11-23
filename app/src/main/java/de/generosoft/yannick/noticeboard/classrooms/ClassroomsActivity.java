package de.generosoft.yannick.noticeboard.classrooms;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.Collections;
import java.util.LinkedList;

import de.generosoft.yannick.noticeboard.R;
import de.generosoft.yannick.noticeboard.util.AfterTextChangedListener;
import de.generosoft.yannick.noticeboard.util.NavigationBarListener;
import de.generosoft.yannick.noticeboard.util.StringFilter;

public class ClassroomsActivity extends AppCompatActivity {

    private String email;
    private String password;

    private LinkedList<Classroom> classrooms = new LinkedList<>();
    private LinkedList<Classroom> shownClassrooms = new LinkedList<>();
    private ClassroomAdapter classroomAdapter;
    private volatile String filter;

    private ClassroomsRequest ClassroomsRequest;
    private volatile boolean requesting = false;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private boolean showAllClassrooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_class_rooms);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        showAllClassrooms = getIntent().getBooleanExtra("showAllClassrooms", false);

        final ListView listView = findViewById(R.id.list);
        classroomAdapter = new ClassroomAdapter(this.getApplicationContext(), shownClassrooms, email, password);
        listView.setAdapter(classroomAdapter);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);

        final Toolbar toolbar = findViewById(R.id.toolbar_);
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

        final ClassroomsRequest.Listener listener = new ClassroomsRequest.Listener() {
            @Override
            public void onSuccess(final LinkedList<Classroom> rooms) {
                ClassroomsActivity.this.classrooms.clear();
                ClassroomsActivity.this.classrooms.addAll(rooms);
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
        ClassroomsRequest = new ClassroomsRequest(listener, this.getApplicationContext(), showAllClassrooms);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh(navigationView);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private synchronized void fillLayout() {
        shownClassrooms.clear();
        classroomAdapter.notifyDataSetChanged();
        for (final Classroom classroom : classrooms) {
            final String s = classroom.getClassroomName() + ": " + classroom.getLecturer();
            if (StringFilter.filter(s, filter)) {
                shownClassrooms.add(classroom);
            }
        }
        classroomAdapter.notifyDataSetChanged();
    }

    public synchronized void refresh(final View view) {
        if (!requesting) {
            try {
                requesting = true;
                ClassroomsRequest.execute(email, password);
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
