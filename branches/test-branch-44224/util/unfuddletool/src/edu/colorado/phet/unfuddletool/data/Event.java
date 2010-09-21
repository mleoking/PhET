package edu.colorado.phet.unfuddletool.data;

import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.colorado.phet.unfuddletool.handlers.PersonHandler;
import edu.colorado.phet.unfuddletool.util.Communication;

public class Event {

    public enum Type {
        MESSAGE,
        MILESTONE,
        TICKET,
        TIME_ENTRY,
        CHANGESET,
        COMMENT,
        PAGE
    }

    public int rawProjectId;
    public DateTime rawCreatedAt;
    public String rawRecordType;
    public int rawRecordId;
    public String rawEvent;
    public String rawDescription;
    public int rawPersonId;
    public String rawSummary;

    public Record record;

    public Event( Element element ) {
        rawProjectId = Communication.getIntField( element, "project-id" );
        rawCreatedAt = Communication.getDateTimeField( element, "created-at" );
        rawRecordType = Communication.getStringField( element, "record-type" );
        rawRecordId = Communication.getIntField( element, "record-id" );
        rawEvent = Communication.getStringField( element, "event" );
        rawDescription = Communication.getStringField( element, "description" );
        rawPersonId = Communication.getIntField( element, "person-id" );
        rawSummary = Communication.getStringField( element, "summary" );

        Element recordElement = (Element) Communication.getFirstNodeByName( element, "record" );

        NodeList list = recordElement.getChildNodes();

        for ( int i = 0; i < list.getLength(); i++ ) {
            Node node = list.item( i );
            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                record = Record.fromElement( (Element) node );
            }
        }
    }

    public Type getType() {
        if ( rawRecordType.equals( "Message" ) ) {
            return Type.MESSAGE;
        }
        else if ( rawRecordType.equals( "Milestone" ) ) {
            return Type.MILESTONE;
        }
        else if ( rawRecordType.equals( "Ticket" ) ) {
            return Type.TICKET;
        }
        else if ( rawRecordType.equals( "TimeEntry" ) ) {
            return Type.TIME_ENTRY;
        }
        else if ( rawRecordType.equals( "Changeset" ) ) {
            return Type.CHANGESET;
        }
        else if ( rawRecordType.equals( "Comment" ) ) {
            return Type.COMMENT;
        }
        else if ( rawRecordType.equals( "Page" ) ) {
            return Type.PAGE;
        }

        throw new RuntimeException( "record-type of '" + rawRecordType + "' is unknown" );
    }

    public Record getRecord() {
        return record;
    }

    public Comment getCommentRecord() {
        if ( getType() == Type.COMMENT ) {
            return (Comment) record;
        }
        else {
            throw new RuntimeException( "Record was not a comment" );
        }
    }

    public Ticket getTicketRecord() {
        if ( getType() == Type.TICKET ) {
            return (Ticket) record;
        }
        else {
            throw new RuntimeException( "Record was not a ticket" );
        }
    }

    public String toString() {
        String ret = "";

        ret += "Event\n";
        ret += "\tevent: " + rawEvent + "\n";
        ret += "\tcreated-at: " + rawCreatedAt.toString() + " ( raw: " + rawCreatedAt.rawString + " )\n";
        ret += "\tdescription: " + rawDescription + "\n";
        ret += "\trecord-type: " + rawRecordType + "\n";
        ret += "\tsummary: " + rawSummary + "\n";

        if ( record != null ) {
            ret += "\t" + record.toString() + "\n";
        }

        return ret;
    }

    public long getId() {
        return ( rawRecordId << 32 ) + rawCreatedAt.getDate().getTime();
    }

    public Date getCreatedAt() {
        return rawCreatedAt.getDate();
    }

    public String getEventString() {
        return rawEvent;
    }

    public String getSummary() {
        return rawSummary;
    }

    public String getDescription() {
        return rawDescription;
    }

    public Person getPerson() {
        return PersonHandler.getPersonHandler().getPersonById( rawPersonId );
    }

    public String getPersonName() {
        return getPerson().getName();
    }
}
