package de.generosoft.yannick.noticeboard.rest.pojo;

public class Classroom implements Comparable<Classroom> {

    private final String classroomName;
    private final String lecturer;
    private boolean subscribed;

    public Classroom(final String classroomName, final String lecturer, final boolean subscribed) {
        this.classroomName = classroomName;
        this.lecturer = lecturer;
        this.subscribed = subscribed;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public String getLecturer() {
        return lecturer;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void unsubscribe() {
        subscribed = false;
    }

    public void subscribe() {
        subscribed = true;
    }

    @Override
    public int compareTo(final Classroom o) {
        return classroomName.compareTo(o.classroomName);
    }
}
