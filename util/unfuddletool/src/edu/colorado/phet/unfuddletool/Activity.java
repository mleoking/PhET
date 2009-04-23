package edu.colorado.phet.unfuddletool;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.colorado.phet.unfuddletool.data.Event;

public class Activity {

    public Activity() {
        getEvents();
    }

    public String getActivityXMLString() {
        return Communication.getXMLResponse( "<request><start-date>2009/1/1</start-date><end-date>2020/1/1</end-date><limit>2</limit></request>", "projects/" + Configuration.getProjectIdString() + "/activity", Authentication.auth );
    }

    public void getEvents() {
        try {
            String ret = getActivityXMLString();
            Document doc = Communication.toDocument( ret );
            NodeList events = doc.getFirstChild().getChildNodes();

            System.out.println( "Found " + String.valueOf( events.getLength() ) + " events." );

            for ( int i = 0; i < events.getLength(); i++ ) {
                Node node = events.item( i );

                if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                    Element event = (Element) node;
                    if ( event.getTagName().equals( "event" ) ) {
                        processEvent( event );
                    }
                }

            }
        }
        catch( TransformerException e ) {
            e.printStackTrace();
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace();
        }
    }

    private void processEvent( Element element ) {
        Event event = new Event( element );
        //System.out.println( event.toString() );
    }
}
