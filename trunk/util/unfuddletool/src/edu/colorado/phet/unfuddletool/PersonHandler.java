package edu.colorado.phet.unfuddletool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.colorado.phet.unfuddletool.data.Person;

public class PersonHandler {

    public Set<Person> people;

    private PersonHandler() {
        people = new HashSet<Person>();

        refreshPeople();
    }

    private static PersonHandler personHandler;

    public static PersonHandler getPersonHandler() {
        if ( personHandler == null ) {
            personHandler = new PersonHandler();
        }

        return personHandler;
    }

    public void refreshPeople() {
        String xmlString = Communication.getXMLResponse( "<request></request>", "people", Authentication.auth );
        try {
            people.clear();

            Element element = (Element) Communication.toDocument( xmlString ).getFirstChild();
            NodeList personList = element.getElementsByTagName( "person" );

            for ( int i = 0; i < personList.getLength(); i++ ) {
                Element personElement = (Element) personList.item( i );

                people.add( new Person( personElement ) );
            }

        }
        catch( TransformerException e ) {
            e.printStackTrace();
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace();
        }
    }

    public Person getPersonById( int id ) {
        Iterator<Person> iter = people.iterator();

        while ( iter.hasNext() ) {
            Person person = iter.next();

            if ( person.getId() == id ) {
                return person;
            }
        }

        return null;
    }
}
