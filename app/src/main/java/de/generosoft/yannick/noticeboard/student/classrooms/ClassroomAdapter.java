package de.generosoft.yannick.noticeboard.student.classrooms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;

import java.util.List;

import de.generosoft.yannick.noticeboard.R;
import de.generosoft.yannick.noticeboard.rest.pojo.Classroom;

public class ClassroomAdapter extends ArrayAdapter<Classroom> {

    private Context context;
    private List<Classroom> classrooms;
    private String email;
    private String password;

    public ClassroomAdapter(final Context context, final List<Classroom> classrooms, final String email, final String password) {
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
        View view;
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.subscription_toogle_layout, null);
        } else {
            view = convertView;
        }
        final Classroom classroom = classrooms.get(position);
        final Switch subscriptionSwitch = view.findViewById(R.id.subscribeSwitch);
        subscriptionSwitch.setChecked(classroom.isSubscribed());
        subscriptionSwitch.setText(classroom.getLecturer() + ": " + classroom.getClassroomName());
        subscriptionSwitch.setOnClickListener(new SubscriptionSwitchListener(classroom, email, password));
        return view;
    }
}
