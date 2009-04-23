package edu.colorado.phet.unfuddletool.data;

import org.w3c.dom.Element;

import edu.colorado.phet.unfuddletool.Communication;

public class Changeset extends Record {

    public int rawAuthorId;
    public String rawAuthorName;
    public String rawAuthorEmail;
    public DateTime rawAuthorDate;
    public int rawCommitterId;
    public String rawCommitterName;
    public String rawCommitterEmail;
    public DateTime rawCommitterDate;
    public DateTime rawCreatedAt;
    public int rawId;
    public String rawMessage;
    public int rawRepositoryId;
    public String rawRevision;

    public Changeset( Element element ) {
        rawAuthorId = Communication.getIntField( element, "author-id" );
        rawAuthorName = Communication.getStringField( element, "author-name" );
        rawAuthorEmail = Communication.getStringField( element, "author-email" );
        rawAuthorDate = Communication.getDateTimeField( element, "author-date" );
        rawCommitterId = Communication.getIntField( element, "committer-id" );
        rawCommitterName = Communication.getStringField( element, "committer-name" );
        rawCommitterEmail = Communication.getStringField( element, "committer-email" );
        rawCommitterDate = Communication.getDateTimeField( element, "committer-date" );
        rawCreatedAt = Communication.getDateTimeField( element, "created-at" );
        rawId = Communication.getIntField( element, "id" );
        rawMessage = Communication.getStringField( element, "message" );
        rawRepositoryId = Communication.getIntField( element, "repository-id" );
        rawRevision = Communication.getStringField( element, "revision" );

    }

    public int recordType() {
        return Record.RECORD_CHANGESET;
    }

    public String toString() {
        return "Changeset (" + rawRevision + ")";
    }

    public int getRevision() {
        return Integer.parseInt( rawRevision );
    }
}
