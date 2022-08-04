package art.psyson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Book {

    final public UUID ID;
    private String author;
    private final List<String> description;
    private List<String> content;
    private final String bookCode;


    public String Title() {
        return title;
    }

    public String getPrettyTitle() {
        return title.substring(2);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public String author() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> description() {
        return description;
    }


    public List<String> content() {
        return content;
    }


    public String bookCode() {
        return bookCode;
    }

    public Book() {
        this.content = new ArrayList<>();
        this.description = new ArrayList<>();
        bookCode = "";
        ID = UUID.randomUUID();
    }


}
