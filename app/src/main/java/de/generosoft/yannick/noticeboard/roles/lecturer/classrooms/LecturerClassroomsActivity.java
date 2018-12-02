package de.generosoft.yannick.noticeboard.roles.lecturer.classrooms;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.LinkedList;

import de.generosoft.yannick.noticeboard.R;
import de.generosoft.yannick.noticeboard.rest.ClassroomRequest;
import de.generosoft.yannick.noticeboard.rest.ClassroomsRequest;
import de.generosoft.yannick.noticeboard.rest.pojo.Classroom;
import de.generosoft.yannick.noticeboard.util.AfterTextChangedListener;
import de.generosoft.yannick.noticeboard.util.NavigationBarListener;
import de.generosoft.yannick.noticeboard.util.StringFilter;

public class LecturerClassroomsActivity extends AppCompatActivity {

    private String email;
    private String password;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String filter;
    private boolean requesting;
    private ClassroomsRequest classroomsRequest;
    private LinkedList<Classroom> classrooms = new LinkedList<>();
    private LinkedList<Classroom> shownClassrooms = new LinkedList<>();
    private LecturerClassroomsAdapter classroomsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_classrooms);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);

        final ListView listView = findViewById(R.id.list);
        classroomsAdapter = new LecturerClassroomsAdapter(this, shownClassrooms, email, password);
        listView.setAdapter(classroomsAdapter);

        final Toolbar toolbar = findViewById(R.id.toolbar_);
        toolbar.setTitle(R.string.show_my_classrooms);
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

        final ClassroomsRequest.Listener listener = new ClassroomsRequest.Listener() {
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
        classroomsRequest = ClassroomsRequest.getLecturerClassroomsRequest(listener, this.getApplicationContext());

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this::createClassroom);
    }

    private void createClassroom(final View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Classroom");
        builder.setMessage("Enter classroom name:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        final ClassroomRequest.Listener listener = new ClassroomRequest.Listener() {

            @Override
            public void onSuccess() {
                final Toast created = Toast.makeText(getApplicationContext(), "created classroom", Toast.LENGTH_SHORT);
                refresh(null);
                created.show();
            }

            @Override
            public void onFailure(final String message) {
                final Toast failed = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                failed.show();
            }
        };
        final ClassroomRequest createRequest = ClassroomRequest.getCreateRequest(listener, getApplicationContext());

        builder.setPositiveButton("Create", (dialog, which) -> {
            try {
                createRequest.execute(email, password, input.getText().toString());
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
        shownClassrooms.clear();
        classroomsAdapter.notifyDataSetChanged();
        for (final Classroom classroom : classrooms) {
            final String s = classroom.getClassroomName() + ": " + classroom.getLecturer();
            if (StringFilter.filter(s, filter)) {
                shownClassrooms.add(classroom);
            }
        }
        classroomsAdapter.notifyDataSetChanged();
    }

    public synchronized void refresh(final View view) {
        if (!requesting) {
            try {
                requesting = true;
                classroomsRequest.execute(email, password);
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
