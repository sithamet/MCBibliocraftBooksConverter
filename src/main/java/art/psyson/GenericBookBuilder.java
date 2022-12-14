package art.psyson;

import art.psyson.util.FileTool;
import art.psyson.util.Functions;
import art.psyson.util.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static art.psyson.Main.OUTPUT;
import static art.psyson.Main.SEP;
import static art.psyson.util.CODES.*;
import static art.psyson.util.Functions.getLength;

/**
 * Parses Session's object .md files from Notion for content (title, description, content, author, icon code, tags) and
 * builds Book objects for further work.
 * <br><br>
 * Then, calls  implementation (BibliocraftBookBuilder at the moment) to create chest item's NBT file with all the books
 * <br><br>
 * <b>Warning:</b> BibliocraftBookBuilder must be called AFTER Book sample is ready. E.g., if you want to build books only
 * on one topic, you must first create an array of built Book objects, and then iterate them with BibliocraftBookBuilder
 */
public class GenericBookBuilder {

    final static String MC_BOLD = "§l";
    final static String MC_ITAL = "§o";
    final static String MC_RESET = "§r";
    final static String NO_AUTHOR = "Неизвестен";

    Session session;

    Logger l;

    public GenericBookBuilder(Session session) {
        l = new Logger(this);
        this.session = session;
    }

    public void buildBooks() {
        l.log("Starting the building process...");


        Map<Book, String> bookReport = new HashMap<>();

        parseBooksFromScritoriaFiles(bookReport);

        removeInvalidBooksFromBuild(bookReport);

        l.log("AFTER INVALID REMOVAL");

        for (Book b : session.getBooks()) {
            l.log(String.valueOf(b.ID));
        }

        if (session.getBooks().size() == 0) {
            return;
        }

        convertMarkdownToMinecraft(session.getBooks());

        // todo here starts building the chest file


        //todo Fix that deamonic shitcode and replace with a configurator instead
       BookCollection questBooks = new BookCollection("questBooks");
       BookCollection deepLoreBooks = new BookCollection("deepLoreBooks");
       BookCollection morhileBooks = new BookCollection("morhileBooks");
       BookCollection mechanBooks = new BookCollection("mechanBooks");
       BookCollection p5 = new BookCollection("p5");
       BookCollection defaultCollection = new BookCollection("defaultCollection");

        for (Book b : session.getBooks()) {
            if (Arrays.asList(b.getTags()).contains("ивент")) {
                questBooks.addBook(b);
            }
            if (Arrays.asList(b.getTags()).contains("диплор")) {
                deepLoreBooks.addBook(b);
            }
            if (Arrays.asList(b.getTags()).contains("морхиль")) {
                morhileBooks.addBook(b);
            }
            if (Arrays.asList(b.getTags()).contains("механики")) {
                mechanBooks.addBook(b);
            }
            if (Arrays.asList(b.getTags()).contains("ПпПППпП")) {
                p5.addBook(b);
            }
            defaultCollection.addBook(b);
        }

        ArrayList<BookCollection> collections = new ArrayList<>();

        collections.add(questBooks);
        collections.add(deepLoreBooks);
        collections.add(morhileBooks);
        collections.add(mechanBooks);
        collections.add(p5);
        collections.add(defaultCollection);


        for (BookCollection collection : collections) {
            compileBooks(collection);
        }

    }

    private void  compileBooks (BookCollection collection) {

        ArrayList<Book> books = collection.books;

        int partitionSize = session.getPartitionSize();

        String collectionFolder = OUTPUT + SEP + session.getSessionTimeStamp() + SEP + collection.title;
        File collectionFile = new File(collectionFolder);
        collectionFile.mkdirs();

        //StackOverFlow copypaste to divite the list into partitions of 5 books (to ease the upload to the game)
        ArrayList<List<Book>> partitions = new ArrayList<>();
        for (int i = 0; i < books.size(); i += partitionSize) {
            partitions.add(books.subList(i, Math.min(i + partitionSize, books.size())));
        }

        //for each books partition
        for (int i = 0; i < partitions.size(); i++) {
            List<Book> bookList = partitions.get(i);

            StringBuilder code = new StringBuilder();

            code.append(session.CHEST_CODE());
            l.log("CHEST CODE IS \n" + session.CHEST_CODE());

            //for each book
            for (int j = 0; j < bookList.size(); j++) {
                Book book = bookList.get(j);
                //here's where book item String with all the content is built
                BibliocraftBookBuilder builder = new BibliocraftBookBuilder("1.0", book, j);
                String bookItemCode = builder.build();

                //adding item to chest
                code.append(bookItemCode);
                if (j != bookList.size() - 1) {
                    code.append(",");
                }
                code.append("\n");
            }

            code.append("]\n"); //close Ttems array inside the Chest
            code.append("}"); //close the Chest


            //writing the collection's partition to the file
            String path = collectionFolder + SEP + "part" + i;
            FileTool.createNewFile(path);

            PrintWriter writer = null;


            try {
                writer = new PrintWriter(path);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            writer.println(code.toString());
            writer.close();

        }


    }




    private void convertMarkdownToMinecraft(List<Book> books) {
        l.log(YELLOW + "Translating Markdown into Minecraft codes..." + RESET);
        for (Book book : books) {
            l.log(GREEN + "Converting a book " + book.getPrettyTitle() + RESET);
            for (int i = 0; i < book.content().size(); i++) {
                String line = book.content().get(i);

                //replace bold
                Pattern pattern = Pattern.compile("\\*\\*");
                line = Functions.replacePairPatternWith(line, pattern, "§l", "§r");

                //replace italics
                pattern = Pattern.compile("\\*");
                line = Functions.replacePairPatternWith(line, pattern, "§o", "§r");

                //replace strikethrough
                pattern = Pattern.compile("~~");
                line = Functions.replacePairPatternWith(line, pattern, "§m", "§r");

                //replace quotes
                pattern = Pattern.compile("\"");
                line = Functions.replacePairPatternWith(line, pattern, "«", "»");

                //replace quotes in title & description
                pattern = Pattern.compile("\"");
                book.setTitle(Functions.replacePairPatternWith(book.Title(), pattern, "«", "»"));

                List<String> newDescription = new ArrayList<>();
                for (String s : book.description()) {
                    newDescription.add(Functions.replacePairPatternWith(s, pattern, "«", "»"));
                }
                book.setDescription(newDescription);

                book.content().set(i, line);
//                l.log(book.content().get(i));
            }

            for (int i = 0; i < book.description().size(); i++) {
                String s = book.description().get(i).replaceAll("&", "§");
                book.description().set(i, s);
            }

            book.setTitle(book.Title().replaceAll("&", "§"));


            l.log(GREEN + "Book converted" + RESET);
        }
    }

    /**
     * Parses all files from the Session and turns them into raw books
     * It gets title, Description list, and raw content lists.
     * while the Book can be built from different sources
     *
     * @param bookReport a Book-String map to store parse results: failed to not. Pay attention that results are surrounded by ANCII color codes
     */
    private void parseBooksFromScritoriaFiles(Map<Book, String> bookReport) {
        for (File file : session.getFiles()) {

            String[] bookTags = {""};
            boolean tagFound = false;
            boolean notReady = false;

            List<List<String>> buffer = new ArrayList<>();
            scanRawBooksIntoBuffer(file, buffer);

            for (List<String> section : buffer) {
                for (int i = 0; i < section.size(); i++) {
                    String line = section.get(i);
                    //check if the file is ready

                    if (line.startsWith("Статус: ")) {
                        String status = line.substring(8);
                        l.log("Status is: " + status);
                        if (!status.equals("Текст готов")) {
                            notReady = true;
                            break;
                        }
                    }
                }
            }

            if (notReady) {
                l.log(RED + "Error! " + RESET + "File %s is not in \"ready\" status." + file.getName());
                l.log("Proceeding to next file...");
                continue;
            }
//            logBufferContent(buffer);

            for (List<String> section : buffer) {
                Book book = new Book();
                boolean titleFound = false;
                boolean descriptionFound = false;
                boolean contentFound = false;
                boolean authorFound = false;
                boolean iconFound = false;



                for (int i = 0; i < section.size(); i++) {
                    String line = section.get(i);

                    //add tags from the book
                    if (!tagFound) { //todo empty list fail-over
                        if (line.startsWith("Темы и тэги:")) {
                            String tags = line.substring(13);
                            l.log("Tags string is: " + tags);
                            tags = Functions.filterStringFrom(tags, ',');
//                            l.log("Comma-less: " + tags);
                            bookTags = tags.split(" ");
//                            l.log("Final tags are " + Arrays.toString(bookTags));
                            tagFound = true;
                        }
                    }


                    //find author
                    if (!authorFound) {
                        authorFound = isAuthorFound(section, book, i, line);
                    }

                    //find icon
                    if (!iconFound) {
                        iconFound = isIconFound(section, book, i, line);
                    }


                    //search for title
                    if (!titleFound) {
                        titleFound = isTitleFound(section, i, line, file);
                        if (titleFound) {
                            book.setTitle(section.get(i + 5));
                        }
                    }

                    //search for description
                    if (!descriptionFound) {
                        descriptionFound = isDescriptionFound(section, i, line);
                        if (descriptionFound) {
                            parseBookDescription(section, book, i, file);
                        }
                    }

                    //search for content
                    if (Pattern.compile("^### \\[").matcher(line).find() && !contentFound) {
                        l.log("Content section is found: " + line);

                        String contentLine = "";
                        try {
                            contentLine = section.get(i + 2);
                            l.log("Content's 1st line is " + contentLine);
                            for (int k = i + 2; k < section.size(); k++) {
                                book.content().add(section.get(k));
                            }
                            l.log("Book content is loaded; ");
                            contentFound = true;
                        } catch (IndexOutOfBoundsException e) {
                            contentFound = false;
                        }

                    }


                }

                l.log(BLUE + "BOOK SUMMARY" + RESET);
                l.log("Title found is " + titleFound);
                if (titleFound) {
                    l.log(book.getPrettyTitle());
                }
                l.log("Description found is " + descriptionFound);
                if (descriptionFound) {
                    l.log("First line is: " + book.description().get(0));
                }
                l.log("Content found is " + contentFound);
                l.log("Author found is " + authorFound);
                l.log("Name is: " + book.author());
                l.log("Icon found is " + iconFound);
                if (iconFound) {
                    l.log("Icon num is " + book.getIcon());
                }
                l.log("Tags found is " + tagFound);
                if (tagFound) {
                    l.log("Tags: " + Arrays.toString(bookTags));
                }


                if (titleFound && descriptionFound && contentFound) {
                    book.setTags(bookTags);
                    session.getBooks().add(book);
                    System.out.println(YELLOW + "Book + \"" + book.getPrettyTitle() + "\" is done" + RESET);
                    bookReport.put(book, "done");
                } else {
                    System.out.println(RED + "Error: Building of this book failed." + RESET);
                    bookReport.put(book, "failed");
                }
            }


        }
    }

    private boolean isIconFound(List<String> section, Book book, int i, String line) {
        if (Pattern.compile("\\*\\*Номер иконки ").matcher(line).find()) {
            l.log("Author section is found: " + line);

            String luaLine = section.get(i + 2);
            String contentLine = section.get(i + 3);
            l.log("lua is " + luaLine);
            l.log("first author line is: " + contentLine);

            if (Pattern.compile("^```").matcher(luaLine).lookingAt()) {
                l.log(YELLOW + "Valid icon line found: " + RESET + contentLine);
                if (contentLine.equals("")) {
                    return false;
                } else {
                    book.setIcon(Integer.parseInt(contentLine));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAuthorFound(List<String> section, Book book, int i, String line) {
        //todo length (65 checking)
        if (Pattern.compile("\\*\\*Автор \\(").matcher(line).find()) {
            l.log("Author section is found: " + line);

            String luaLine = section.get(i + 2);
            String contentLine = section.get(i + 3);
            l.log("lua is " + luaLine);
            l.log("first author line is: " + contentLine);

            if (Pattern.compile("^```").matcher(luaLine).lookingAt()) {
                l.log(YELLOW + "Valid author line found: " + RESET + contentLine);
                if (contentLine.equals("")) {
                    book.setAuthor(NO_AUTHOR);
                    return false;
                } else {
                    book.setAuthor(contentLine);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Remove books that failed to built (no title/description, invalid title/descr, too loong title/descr
     *
     * @param bookReport a map of books with statuses
     */
    private void removeInvalidBooksFromBuild(Map<Book, String> bookReport) {
        l.log(GREEN + "Logging resulting books..." + RESET);
        List<Book> newBooks = new ArrayList<>();


        for (Book book : session.getBooks()) {
            l.log(book.getPrettyTitle() + " " + bookReport.get(book));
            if (!bookReport.get(book).equals("failed")) {
                newBooks.add(book);
            }
        }
        session.getBooks().clear();
        for (Book book : newBooks) {
            session.getBooks().add(book);
        }
    }


    /**
     * Adds valid Description lines to the book, testing against the format & length limits
     *
     * @param section raw MD file section (list of lines between first-level # headings)
     * @param i       line position of the loop inside the section
     * @param file    current MD file (to refer error to the file, as no title may be available to use
     */
    private void parseBookDescription(List<String> section, Book book, int i, File file) {
        for (int k = i + 5; k < section.size(); k++) {
            String s = section.get(k);
            if (s.startsWith("&")) {

                int length = getLength(s);

                String name = file.getName();
                byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
                String nameDome = new String(bytes);


                if (length > 61) {
                    System.out.println(RED + "Error! in file " + nameDome + RESET + "\nDescription is longer than allowed 61 char, length ="
                            + length + "\n Descr: " + RED + s + RESET);
                } else {
                    book.description().add(s);
                    l.log("Line added to description: " + s);

                }


            } else {
                l.log(YELLOW + "End of description." + RESET);
                break;
            }
        }
    }

    /**
     * Find Scriptoria-style description and verify it matches the standard
     *
     * @param section raw MD file section (list of lines between first-level # headings)
     * @param i       line position of the loop inside the section
     * @param line    Current loop line to be tested against Scriptoria markup
     * @return true if description is valid
     */
    private boolean isDescriptionFound(List<String> section, int i, String line) {
        if (Pattern.compile("Описание \\(").matcher(line).find()) {
            l.log("Description section is found: " + line);

            String luaLine = section.get(i + 4);
            String contentLine = section.get(i + 5);
            l.log("lua is " + luaLine);
            l.log("Description 1st line is " + contentLine);

            if (Pattern.compile("^```").matcher(luaLine).lookingAt()
                    &&
                    Pattern.compile("^&").matcher(contentLine).lookingAt()) {
                l.log("Valid description  found");
                return true;
            }
        }
        return false;
    }

    /**
     * Find Scriptoria-style book title and verify it matches the standard. Also tests Title length
     *
     * @param section raw MD file section (list of lines between first-level # headings)
     * @param i       line position of the loop inside the section
     * @param line    Current loop line to be tested against Scriptoria markup
     * @param file    current MD file (to refer error to the file, as no title may be available to use
     * @return
     */
    private boolean isTitleFound(List<String> section, int i, String line, File file) {
        if (Pattern.compile("Название предмета").matcher(line).find()) {
            l.log("Title section is found: " + line);

            String luaLine = section.get(i + 4);
            String contentLine = section.get(i + 5);

            int length = getLength(contentLine);


            if (length > 35) {
                System.out.println(RED + "Error! in file " + file.getName() + RESET + "\nTitle is longer than allowed 35 char, length ="
                        + length + "\n Title: " + RED + contentLine + RESET);
                return false;
            } else if (length <= 3) {
                System.out.println(RED + "Error! in file " + file.getName() + RESET + "\nTitle is empty!");
                return false;
            }


            l.log("lua is " + luaLine);
            l.log("Content is " + contentLine);

            if (Pattern.compile("^```").matcher(luaLine).lookingAt()
                    &&
                    Pattern.compile("^&").matcher(contentLine).lookingAt()) {
                l.log("Valid book title found");
                return true;
            }
        }
        return false;
    }

    /**
     * Utility to log the content of the sections-divided MD file
     *
     * @param buffer
     */
    private void logBufferContent(List<List<String>> buffer) {
        l.log("Logging buffer parts...");

        for (int i = 0; i < buffer.size(); i++) {
            l.log("BUFFER PART " + i);
            for (String s : buffer.get(i)) {
                System.out.println(s);
            }
            l.log("END OF PART " + i);
        }
    }

    /**
     * Scans .md files, find "# " 1st-level headings, and splits the file into raw parts.
     * Most likely, the first part will always be task description & checklist
     *
     * @param file   .md file from Notion export
     * @param buffer Arraylist of <code>ArrayList String</code> that holds lines
     */
    private void scanRawBooksIntoBuffer(File file, List<List<String>> buffer) {
        Scanner scanner = null;

        try {
            FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
            String encoding = reader.getEncoding();
            scanner = new Scanner(reader);
            l.log(RED + "ENCODING = " + encoding + RESET);

        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> currentPart = new ArrayList<>();
        buffer.add(currentPart);

        int lineIndex = 0;

        while (true) {

            String line;
            try {
                line = scanner.nextLine();
            } catch (NoSuchElementException e) {
                l.log("File is over");
                break;
            }

            Pattern pattern = Pattern.compile("^# ");
            Matcher matcher = pattern.matcher(line);
            if (matcher.lookingAt() && lineIndex != 0) {
                l.log("Found new chapter, creating a new buffer...");
                currentPart = new ArrayList<>();
                buffer.add(currentPart);
            }
            currentPart.add(line);
            lineIndex++;
//            l.log("Added line: \"" + line + "\"");
        }
    }


}
