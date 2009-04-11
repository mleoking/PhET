package edu.colorado.phet.unfuddletool.data;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.colorado.phet.unfuddletool.Communication;

public class Event {
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

        //record = Record.fromElement( (Element) ((Element) Communication.getFirstNodeByName( element, "record" )).getFirstChild() );

        Element recordElement = (Element) Communication.getFirstNodeByName( element, "record" );
        //System.out.println( "***Record: " + Communication.toString( record ) );

        NodeList list = recordElement.getChildNodes();

        for ( int i = 0; i < list.getLength(); i++ ) {
            Node node = list.item( i );
            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                //System.out.println( "***Node: " + Communication.toString( node ) );
                record = Record.fromElement( (Element) node );
            }
        }
    }

    public String toString() {
        String ret = "";

        ret += "Event\n";
        ret += "\tevent: " + rawEvent + "\n";
        ret += "\tcreated-at: " + rawCreatedAt.toString() + "\n";
        ret += "\tdescription: " + rawDescription + "\n";
        ret += "\trecord-type: " + rawRecordType + "\n";
        ret += "\tsummary: " + rawSummary + "\n";

        if ( record != null ) {
            ret += "\t" + record.toString() + "\n";
        }

        return ret;
    }
}
