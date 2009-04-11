package edu.colorado.phet.unfuddletool.data;

public class Attachment extends Record {
    public String rawContentType;
    public DateTime rawCreatedAt;
    public String rawFilename;
    public int rawId;
    public int rawParentId;
    public String rawParentType;
    public DateTime rawUpdatedAt;
    public String rawKey;

    public int recordType() {
        return Record.RECORD_ATTACHMENT;
    }

    public String toString() {
        return "Attachment";
    }
}
