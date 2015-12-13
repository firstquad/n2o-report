package ru.kirkazan.rmis.app.report.n2o.api.model;

/**
 * @author rsadikov
 * @since 03.11.2015
 */
public class ReportHash {
    private int id;
    private byte[] hash;

    public ReportHash() {
    }

    public ReportHash(int id, byte[] hash) {
        this.id = id;
        this.hash = hash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }
}
