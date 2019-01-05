package de.generosoft.yannick.noticeboard.roles.lecturer.classroom;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;

import de.generosoft.yannick.noticeboard.R;
import de.generosoft.yannick.noticeboard.rest.ClassroomRequest;
import de.generosoft.yannick.noticeboard.rest.DeleteMessageRequest;
import de.generosoft.yannick.noticeboard.rest.pojo.Classroom;
import de.generosoft.yannick.noticeboard.rest.pojo.Message;

public class LecturerMessagesAdapter extends ArrayAdapter<Message> {

    private final String email;
    private final String password;
    private final List<Message> messages;
    private final LecturerClassroomActivity activity;

    public LecturerMessagesAdapter(final LecturerClassroomActivity activity, final List<Message> messages, final String email, final String password) {
        super(activity.getApplicationContext(), 0, messages);
        this.email = email;
        this.password = password;
        this.messages = messages;
        this.activity = activity;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        if (messages.isEmpty()) {
            final LayoutInflater layoutInflater = LayoutInflater.from(activity);
            final View view = layoutInflater.inflate(R.layout.classroom_list_empty_layout, null);
            final TextView textView = view.findViewById(R.id.textView);
            textView.setText("No Messages Found!");
            return view;
        } else {
            return getMessageView(position);
        }
    }

    @NonNull
    private View getMessageView(int position) {
        final LayoutInflater layoutInflater = LayoutInflater.from(activity);
        final View view = layoutInflater.inflate(R.layout.classroom_deletion_layout, null);
        final Message message = messages.get(position);

        final TextView textView = view.findViewById(R.id.textView);
        textView.setText(message.getPayload());

        final ImageButton deleteButton = view.findViewById(R.id.deleteButton);
        final DeleteMessageRequest.Listener listener = new DeleteMessageRequest.Listener() {

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

        final DeleteMessageRequest deleteRequest = DeleteMessageRequest.getRequest(listener, getContext());
        final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    try {
                        deleteRequest.execute(email, password, message.getId());
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        deleteButton.setOnClickListener(v -> {
            builder.setMessage("Are you sure to delete message?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener);
            builder.show();
        });

        return view;
    }

    @Override
    public int getCount() {
        // returns 1 if the messages list is empty, because the empty notification is shown
        if (messages.isEmpty()) {
            return 1;
        } else {
            return super.getCount();
        }
    }
}
