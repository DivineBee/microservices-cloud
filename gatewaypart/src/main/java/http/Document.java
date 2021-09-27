package http;

import java.util.ArrayList;
import java.util.Arrays;

public class Document {
    private String title;
    private String text;
    private ArrayList<Integer> user_id;
    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<Integer> getUser_id() {
        return user_id;
    }

    public void setUser_id(ArrayList<Integer> user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Document{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", user_id=" + user_id +
                ", id=" + id +
                '}';
    }
}
