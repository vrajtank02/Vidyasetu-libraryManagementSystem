package model;

import java.sql.Date;

public class Transaction {
    private int transactionId;
    private int copyId;
    private int memberId;
    private int issueAdminId;
    private int returnAdminId;
    private Date issueDate;
    private Date returnDate;
    private double fineAmount;

    public Transaction() {
    }

    public Transaction(int transactionId, int copyId, int memberId, int issueAdminId, int returnAdminId, Date issueDate,
            Date returnDate, double fineAmount) {
        this.transactionId = transactionId;
        this.copyId = copyId;
        this.memberId = memberId;
        this.issueAdminId = issueAdminId;
        this.returnAdminId = returnAdminId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getCopyId() {
        return copyId;
    }

    public void setCopyId(int copyId) {
        this.copyId = copyId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getIssueAdminId() {
        return issueAdminId;
    }

    public void setIssueAdminId(int issueAdminId) {
        this.issueAdminId = issueAdminId;
    }

    public int getReturnAdminId() {
        return returnAdminId;
    }

    public void setReturnAdminId(int returnAdminId) {
        this.returnAdminId = returnAdminId;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }
}