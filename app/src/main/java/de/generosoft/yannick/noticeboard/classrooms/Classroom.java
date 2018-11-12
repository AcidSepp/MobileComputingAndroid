package de.generosoft.yannick.noticeboard.classrooms;

public class Classroom implements Comparable<Classroom> {

    private final String classroomName;
    private final String lecturer;

    public Classroom(final String classroomName, final String lecturer) {
        this.classroomName = classroomName;
        this.lecturer = lecturer;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public String getLecturer() {
        return lecturer;
    }

    @Override
    public int compareTo(final Classroom o) {
        return classroomName.compareTo(o.classroomName);
    }
}
