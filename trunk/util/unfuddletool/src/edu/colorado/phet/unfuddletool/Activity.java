package edu.colorado.phet.unfuddletool;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

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





    public static String getDateXMLString( String startDateString, String endDateString ) {
        return Communication.getXMLResponse( "<request></request>", "projects/" + Configuration.getProjectIdString() + "/activity?start_date=" + startDateString + "&end_date=" + endDateString, Authentication.auth );
    }

    public static String stringFromCalendar( Calendar cal ) {
        return String.valueOf( cal.get( Calendar.YEAR ) ) + "/" + String.valueOf( cal.get( Calendar.MONTH) + 1 ) + "/" + String.valueOf( cal.get( Calendar.DAY_OF_MONTH ) );
    }

    public static void parseEvents( String str ) {
        try {
            Document doc = Communication.toDocument( str );
            NodeList events = doc.getFirstChild().getChildNodes();

            System.out.println( "Found " + String.valueOf( events.getLength() ) + " events." );

            for ( int i = 0; i < events.getLength(); i++ ) {
                Node node = events.item( i );

                if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                    Element eventElement = (Element) node;
                    if ( eventElement.getTagName().equals( "event" ) ) {
                        Event event = new Event( eventElement );
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

    public static void requestRecentActivity( int days ) {
        Calendar cal = new GregorianCalendar();
        cal.setTime( new Date() );
        cal.roll( Calendar.DATE, true );
        cal.roll( Calendar.DATE, true );

        String oldString = stringFromCalendar( cal );

        while( days-- > 0 ) {
            cal.roll( Calendar.DATE, false );
            String newString = stringFromCalendar( cal );

            parseEvents( getDateXMLString( newString, oldString ) );

            oldString = newString;
        }
    }

    
}
