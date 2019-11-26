package com.example.papaya;
import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Memo extends RealmObject{

    @Override
    public String toString() {
        return "Memo{" +
                "title='" + title + "' ,content='"+ content +'\'' +
                '}';
    }

    @Required
    private String title;
    private String content;

    public Memo() {
        this.title = "null";
        this.content = "null";
    }
    public Memo(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setContent(String content) {
        this.content = content;
    }
}