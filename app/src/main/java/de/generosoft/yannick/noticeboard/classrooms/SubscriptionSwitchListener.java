package de.generosoft.yannick.noticeboard.classrooms;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.json.JSONException;

import de.generosoft.yannick.noticeboard.R;

public class SubscriptionSwitchListener implements  View.OnClickListener {

    private final Classroom classroom;
    private String email;
    private String password;
    private volatile boolean locked = false;

    public SubscriptionSwitchListener(final Classroom classroom, final String email, final String password) {
        this.classroom = classroom;
        this.email = email;
        this.password = password;
    }

    @Override
    public void onClick(View v) {
        final CompoundButton buttonView = v.findViewById(R.id.subscribeSwitch);
        synchronized (v) {
            if (!locked) {
                //unsubscribe
                if (!buttonView.isChecked()) {
                    locked = true;
                    final UnsubscribeRequest unsubscribeRequest = new UnsubscribeRequest(new UnsubscribeRequest.Listener() {
                        @Override
                        public void onSuccess() {
                            locked = false;
                            classroom.unsubscribe();
                            buttonView.setChecked(false);
                            final Toast wtf = Toast.makeText(v.getContext(), "unsubscribed", Toast.LENGTH_SHORT);
                            wtf.show();
                        }

                        @Override
                        public void onFailure(final String message) {
                            final Toast toast = Toast.makeText(buttonView.getContext(), message, Toast.LENGTH_SHORT);
                            toast.show();
                            locked = false;

                            // check button again because the unsubscribe failed
                            buttonView.setChecked(true);
                        }
                    }, buttonView.getContext());
                    try {
                        unsubscribeRequest.execute(email, password, classroom.getClassroomName());
                    } catch (final JSONException e) {
                        e.printStackTrace();
                        final Toast toast = Toast.makeText(buttonView.getContext(), e.getClass().toString(), Toast.LENGTH_SHORT);
                        toast.show();
                        locked = false;

                        // check button again because the unsubscribe failed
                        buttonView.setChecked(true);
                    }
                //subscribe
                } else {
                    final SubscribeRequest unsubscribeRequest = new SubscribeRequest(new SubscribeRequest.Listener() {
                        @Override
                        public void onSuccess() {
                            locked = false;
                            classroom.subscribe();
                            buttonView.setChecked(true);
                            final Toast wtf = Toast.makeText(v.getContext(), "subscribed", Toast.LENGTH_SHORT);
                            wtf.show();
                        }

                        @Override
                        public void onFailure(final String message) {
                            final Toast toast = Toast.makeText(buttonView.getContext(), message, Toast.LENGTH_SHORT);
                            toast.show();
                            locked = false;

                            // uncheck button again because the unsubscribe failed
                            buttonView.setChecked(false);
                        }
                    }, buttonView.getContext());
                    try {
                        unsubscribeRequest.execute(email, password, classroom.getClassroomName());
                    } catch (final JSONException e) {
                        e.printStackTrace();
                        final Toast toast = Toast.makeText(buttonView.getContext(), e.getClass().toString(), Toast.LENGTH_SHORT);
                        toast.show();
                        locked = false;

                        // uncheck button again because the unsubscribe failed
                        buttonView.setChecked(false);
                    }
                }
            } else {
                buttonView.setChecked(!buttonView.isChecked());
            }
        }
    }
}
