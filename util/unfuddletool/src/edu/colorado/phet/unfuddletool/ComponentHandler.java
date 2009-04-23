package edu.colorado.phet.unfuddletool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.colorado.phet.unfuddletool.data.Component;

public class ComponentHandler {

    public Set<Component> components;

    private ComponentHandler() {
        components = new HashSet<Component>();

        refreshComponents();
    }

    private static ComponentHandler componentHandler;

    public static ComponentHandler getComponentHandler() {
        if ( componentHandler == null ) {
            componentHandler = new ComponentHandler();
        }

        return componentHandler;
    }

    public void refreshComponents() {
        String xmlString = Communication.getXMLResponse( "<request></request>", "projects/" + Configuration.getProjectIdString() + "/components", Authentication.auth );
        try {
            components.clear();

            Element element = (Element) Communication.toDocument( xmlString ).getFirstChild();
            NodeList componentList = element.getElementsByTagName( "component" );

            for ( int i = 0; i < componentList.getLength(); i++ ) {
                Element componentElement = (Element) componentList.item( i );

                components.add( new Component( componentElement ) );
            }

        }
        catch( TransformerException e ) {
            e.printStackTrace();
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace();
        }
    }

    public Component getComponentById( int id ) {
        Iterator<Component> iter = components.iterator();

        while ( iter.hasNext() ) {
            Component component = iter.next();

            if ( component.getId() == id ) {
                return component;
            }
        }

        return null;
    }
}