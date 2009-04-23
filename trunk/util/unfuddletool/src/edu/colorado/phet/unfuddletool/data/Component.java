package edu.colorado.phet.unfuddletool.data;

import org.w3c.dom.Element;

import edu.colorado.phet.unfuddletool.Communication;
import edu.colorado.phet.unfuddletool.PersonHandler;
import edu.colorado.phet.unfuddletool.ComponentHandler;

public class Component {

    public DateTime rawCreatedAt;
    public int rawId;
    public String rawName;
    public int rawProjectId;
    public DateTime rawUpdatedAt;

    public Component( Element element ) {
        rawCreatedAt = Communication.getDateTimeField( element, "created-at" );
        rawId = Communication.getIntField( element, "id" );
        rawName = Communication.getStringField( element, "name" );
        rawProjectId = Communication.getIntField( element, "project-id" );
        rawUpdatedAt = Communication.getDateTimeField( element, "updated-at" );
    }

    public String toString() {
        return getName();
    }

    public int getId() {
        return rawId;
    }

    public String getName() {
        return rawName;
    }

    public static String getNameFromId( int id ) {
        if ( id == -1 ) {
            return "none";
        }

        Component component = ComponentHandler.getComponentHandler().getComponentById( id );
        if ( component == null ) {
            return "none";
        }

        return component.getName();
    }

}
