/**
 * Class: GraphicFactoryX
 * Package: edu.colorado.phet.common.userinterface.view
 * Author: Another Guy
 * Date: May 29, 2003
 */
package edu.colorado.phet.common.userinterface.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.userinterface.graphics.ApparatusPanel;
import edu.colorado.phet.common.userinterface.graphics.Graphic;

import java.util.HashMap;

public class GraphicFactoryX {

    // The map that is used to get the class of graphic associated with
    // a particule phyisical entity. It is intended that this map be
    // added to by subclasses.
    private HashMap physicalToGraphicalClassMap = new HashMap();

    /**
     * Protected contructor to ensure that this must be subclassed
     */
    protected GraphicFactoryX() {
    }

    /**
     * Returns the HashMap used to find the appropriate PhetGraphic subclass
     * for a Particle subclass. This is called by concrete subclasses of GraphicFactory
     * so they can added application-specific class-to-class pairings
     * @return The class-to-class HashMap
     */
    protected HashMap getPhysicalToGraphicalClassMap() {
        return physicalToGraphicalClassMap;
    }

    /**
     * Creates an instance of the graphic class associated with the class of a specified
     * body.
     * @param body
     * @param owningApparatusPanel
     * @return An instance of the PhetGraphic associated with the body's class
     * @throws RuntimeException if no instance of a PhetGraphic subclass can be
     * instantiated for the body
     */
    public Graphic createGraphic( ModelElement body, ApparatusPanel owningApparatusPanel ) {

        Graphic graphic = null;
        try {
            Class graphicClass = null;

            // Go through the class hierarchy of the body until we either find a corresponding
            // graphic class, or we have tried all superclasses of the body
            for( Class bodyClass = body.getClass(); graphicClass == null && bodyClass != null; bodyClass = bodyClass.getSuperclass() ) {
                graphicClass = (Class)physicalToGraphicalClassMap.get( bodyClass );
            }

            // Some physical entities have no associated graphics, so we must test for null
            if( graphicClass != null ) {
                graphic = (Graphic)graphicClass.newInstance();
            }
        }
        catch( InstantiationException e ) {
            throw new RuntimeException( "Cannot instatiate Graphic subclass" );
        }
        catch( IllegalAccessException e ) {
            throw new RuntimeException( "Cannot instatiate Graphic subclass" );

        }
        return graphic;
    }
}
