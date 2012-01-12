package edu.colorado.phet.unfuddletool.data;

import org.w3c.dom.Element;

public abstract class Record {
    public static int RECORD_ATTACHMENT = 0;
    public static int RECORD_CHANGESET = 1;
    public static int RECORD_TICKET = 2;
    public static int RECORD_COMMENT = 3;

    public abstract int recordType();

    public abstract String toString();

    public static Record fromElement( Element element ) {
        String tagName = element.getTagName();

        if ( tagName.equals( "changeset" ) ) {
            return new Changeset( element );
        }

        if ( tagName.equals( "ticket" ) ) {
            return new Ticket( element );
        }

        if ( tagName.equals( "comment" ) ) {
            return new Comment( element );
        }

        return null;
    }
}
