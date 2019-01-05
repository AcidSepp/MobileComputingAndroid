package de.generosoft.yannick.noticeboard.roles.student.classrooms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import de.generosoft.yannick.noticeboard.R;
import de.generosoft.yannick.noticeboard.rest.pojo.Classroom;

public class StudentClassroomsAdapter extends ArrayAdapter<Classroom> {

    private Context context;
    private List<Classroom> classrooms;
    private String email;
    private String password;

    public StudentClassroomsAdapter(final Context context, final List<Classroom> classrooms, final String email, final String password) {
        super(context, R.layout.subscription_toogle_layout, classrooms);
        this.context = context;
        this.classrooms = classrooms;
        this.email = email;
        this.password = password;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        if (classrooms.isEmpty()) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            final View view = layoutInflater.inflate(R.layout.classroom_list_empty_layout, null);
            final TextView textView = view.findViewById(R.id.textView);
            textView.setText("No Classrooms Found!");
            return view;
        } else {
            return getClassroomView(position);
        }
    }

    @NonNull
    private View getClassroomView(int position) {
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view = layoutInflater.inflate(R.layout.subscription_toogle_layout, null);
        final Classroom classroom = classrooms.get(position);
        final Switch subscriptionSwitch = view.findViewById(R.id.subscribeSwitch);
        subscriptionSwitch.setChecked(classroom.isSubscribed());
        subscriptionSwitch.setText(classroom.getLecturer() + ": " + classroom.getClassroomName());
        subscriptionSwitch.setOnClickListener(new SubscriptionSwitchListener(classroom, email, password));
        return view;
    }

    @Override
    public int getCount() {
        // returns 1 if classrooms is empty, because the "No Classroms Found" Notification is shown.
        if (classrooms.isEmpty()) {
            return 1;
        } else {
            return super.getCount();
        }
    }
}
