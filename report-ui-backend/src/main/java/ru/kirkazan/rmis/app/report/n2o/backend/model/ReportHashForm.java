package ru.kirkazan.rmis.app.report.n2o.backend.model;

/**
 * @author rsadikov
 * @since 11.11.2015
 */
public class ReportHashForm {
    private int id;
    private String hash;

    public ReportHashForm() {
    }

    public ReportHashForm(int id, String hash) {
        this.id = id;
        this.hash = hash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
