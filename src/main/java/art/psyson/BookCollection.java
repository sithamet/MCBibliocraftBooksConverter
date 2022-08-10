package art.psyson;

import java.util.ArrayList;
import java.util.List;

public class BookCollection {
    public final String title;
    public final ArrayList<Book> books;

    public BookCollection(String title) {
        this.books = new ArrayList<>();
        this.title = title;
    }

    public void addBook (Book book) {
        books.add(book);
    }
}
