package de.generosoft.yannick.noticeboard.roles.lecturer.classrooms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.List;

import de.generosoft.yannick.noticeboard.R;
import de.generosoft.yannick.noticeboard.rest.DeleteClassroomRequest;
import de.generosoft.yannick.noticeboard.rest.pojo.Classroom;

public class LecturerClassroomsAdapter extends ArrayAdapter<Classroom> {

    private LecturerClassroomsActivity activity;
    private List<Classroom> classrooms;
    private String email;
    private String password;

    public LecturerClassroomsAdapter(final LecturerClassroomsActivity activity, final List<Classroom> classrooms, final String email, final String password) {
        super(activity, R.layout.subscription_toogle_layout, classrooms);
        this.activity = activity;
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
            final LayoutInflater layoutInflater = LayoutInflater.from(activity);
            view = layoutInflater.inflate(R.layout.classroom_deletion_layout, null);
        } else {
            view = convertView;
        }
        final Classroom classroom = classrooms.get(position);

        final TextView textView = view.findViewById(R.id.textView);
        textView.setText(classroom.getClassroomName());

        final ImageButton deleteButton = view.findViewById(R.id.deleteButton);
        final DeleteClassroomRequest.Listener listener = new DeleteClassroomRequest.Listener() {

            @Override
            public void onSuccess() {
                final Toast deleted = Toast.makeText(getContext(), "deleted", Toast.LENGTH_SHORT);
                activity.refresh(null);
                deleted.show();
            }

            @Override
            public void onFailure(final String message) {
                final Toast failed = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
                failed.show();
            }
        };

        final DeleteClassroomRequest deleteClassroomRequest = new DeleteClassroomRequest(listener, getContext());
        final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    try {
                        deleteClassroomRequest.execute(email, password, classroom.getClassroomName());
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        deleteButton.setOnClickListener(v -> {
            builder.setMessage("Are you sure to delete classroom \"" + classroom.getClassroomName() + "\"?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });

        return view;
    }


}
