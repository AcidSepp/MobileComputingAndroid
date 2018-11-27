package de.generosoft.yannick.noticeboard.rest.pojo;

public class Message implements Comparable<Message> {

    final int id;
    final String payload;
    final String classroom;

    public Message(final int id, final String payload, final String classroom) {
        this.id = id;
        this.payload = payload;
        this.classroom = classroom;
    }

    public int getId() {
        return id;
    }

    public String getPayload() {
        return payload;
    }

    public String getClassroom() {
        return classroom;
    }

    @Override
    public int compareTo(final Message o) {
        if (o.id > this.id) {
            return -1;
        } else if (o.id < this.id){
            return 1;
        }
        return 0;
    }
}
