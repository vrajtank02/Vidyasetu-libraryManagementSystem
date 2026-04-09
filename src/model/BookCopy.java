package model;

public class BookCopy {
    private int copyId;
    private int bookId;
    private String status;

    public BookCopy() {
    }

    public BookCopy(int copyId, int bookId, String status) {
        this.copyId = copyId;
        this.bookId = bookId;
        this.status = status;
    }

    public int getCopyId() {
        return copyId;
    }

    public void setCopyId(int copyId) {
        this.copyId = copyId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}