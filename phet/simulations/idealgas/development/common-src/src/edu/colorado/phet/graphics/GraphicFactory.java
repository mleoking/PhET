/*
 * Class: GraphicFactory
 * Package: edu.colorado.phet.graphics
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 12:12:17 PM
 */
package edu.colorado.phet.graphics;

import edu.colorado.phet.physics.body.Particle;

import java.util.HashMap;

/**
 * This class creates graphic elements for physical elements. It can be subclassed
 * by specific applications to handle the physical and graphical elements that
 * are particular to the application.
 * <p>
 * The factory will create a graphic for a body if there is a graphic class
 * associated with the class of the body, or one of it's superclasses. Any graphic
 * class used by the factory must have a contructor that takes no parameters.
 * <p>
 * This class and any derived subclasses are intended to be singletons. Typically,
 * all that needs to be done in the derived class is to define a constructor that
 * adds the appropriate class pairings to the physicalToGraphicalClassMap, and
 * the standard code for creating a singleton.
 * <p>
 * Application-specific concrete subclasses of PhetApplication must provide
 * implementations of getGraphicFactory() that return an instance of GraphicFactory.
 * These implementations will usually get the singleton instance of an associated
 * application-specific subclass of GraphicFactory, similar to the one shown in the
 * example below.
 * <p>
 * <b>Example:
 * <pre>
 * <code>
 * public class ChargeExampleGraphicFactory extends GraphicFactory {
 *
 *     public ChargeExampleGraphicFactory() {
 *         HashMap classMap = getPhysicalToGraphicalClassMap();
 *         classMap.put( NegativeCharge.class, NegativeChargeGraphic.class );
 *         classMap.put( PositiveCharge.class, PositiveChargeGraphic.class );
 *     }
 *
 *     //
 *     // Static fields and methods
 *     //
 *     private static GraphicFactory instance = new ChargeExampleGraphicFactory();
 *     public static GraphicFactory instance() {
 *         return instance;
 *     }
 * }
 * </code>
 * </pre>
 */
public class GraphicFactory {

    // The map that is used to get the class of graphic associated with
    // a particule phyisical entity. It is intended that this map be
    // added to by subclasses.
    private HashMap physicalToGraphicalClassMap = new HashMap();

    /**
     * Protected contructor to ensure that this must be subclassed
     */
    protected GraphicFactory() {
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
    public PhetGraphic createGraphic( Particle body, ApparatusPanel owningApparatusPanel ) {

        PhetGraphic graphic = null;
        try {
            Class graphicClass = null;

            // Go through the class hierarchy of the body until we either find a corresponding
            // graphic class, or we have tried all superclasses of the body
            for( Class bodyClass = body.getClass(); graphicClass == null && bodyClass != null; bodyClass = bodyClass.getSuperclass() ) {
                graphicClass = (Class)physicalToGraphicalClassMap.get( bodyClass );
            }

            // If we found no entry in the map for the body's class, throw out the anchor
//            if( graphicClass == null ) {
//                throw new RuntimeException( "No graphic class found for physical body.");
//            }

            // Some physical entities have no associated graphics, so we must test for null
            if( graphicClass != null ) {
                graphic = (PhetGraphic)graphicClass.newInstance();
                graphic.setApparatusPanel( owningApparatusPanel );
                graphic.init( body );
            }
        }
        catch( InstantiationException e ) {
            throw new RuntimeException( "Cannot instatiate PhetGraphic subclass" );
        }
        catch( IllegalAccessException e ) {
            throw new RuntimeException( "Cannot instatiate PhetGraphic subclass" );

        }
        return graphic;
    }
}
