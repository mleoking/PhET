/*
 * Class: ApparatusPanel
 * Package: edu.colorado.phet.graphics
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.graphics;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.physics.body.PhysicalEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.List;

/**
 * This is a base class for panels that contain graphic representations
 * of elements in the PhysicalSystem.
 * <p>
 * The graphic objects to be displayed are maintained in "layers". Each layer can
 * contain any number of Paintable objects, and each layer has an integer "level"
 * associated with it. Layers are drawn in ascending order of their levels. The order
 * in which objects in a given level are drawn in undefined.
 * <p>
 * Levels less than 0 are reserved for items that are always to be displayed. This
 * could, for example, be used for a fixture or instrument that is always to appear as
 * part of the apparatus, such as a table or meter. When this class' clear() method is
 * executed these objects are not destroyed.
 * <p>
 * Levels 1 and higher are used for objects that can be created and destroyed as the
 * application runs. All objects in these layers are destroyed when the clear() method
 * is executed.
 * <p>
 * Instances of this class are Observers of the application's PhysicalSystem
 *
 * @see Paintable
 * @see PhetGraphic
 */
public class ApparatusPanel extends JPanel implements Observer {

    private String name;

    // The map of graphic objects to be drawn in the panel
    private TreeMap graphicLayers = new TreeMap();

    /**
     *
     * @param application
     * @param name
     */
    public ApparatusPanel( PhetApplication application, String name ) {
        this( name );
    }

    /**
     *
     * @param name
     */
    public ApparatusPanel( String name ) {

        // Call superclass constructor with null so that we
        // don't get the default layout manager. This allows us
        // to lay out components with absolute coordinates
        super( null );
        this.setBackground( Color.white );
        this.name = name;

        // Set the size of the panel
        this.setPreferredSize( new Dimension( 600, 520 ) );
    }

    /**
     *
     */
    public void init() {
        // To be overridden
    }

    /**
     * Returns the sorted map of graphic layers
     * @return The sorted map of graphic layers
     */
    protected TreeMap getGraphicLayers() {
        return graphicLayers;
    }

    /**
     * Returns the name of the apparatus panel
     * @return The name of the apparatus panel
     */
    public String getName() {
        return name;
    }

    /**
     * Clears objects in the graphical context that are experiment-specific
     */
    public void clear() {
        synchronized( graphicLayers ) {
            Collection layerKeys = graphicLayers.keySet();
            ArrayList keysToRemove = new ArrayList();
            for( Iterator keyIt = layerKeys.iterator(); keyIt.hasNext(); ) {
                Integer key = (Integer)keyIt.next();
                if( key.intValue() > 0 ) {
                    keysToRemove.add( key );
                }
            }
            for( Iterator keyIt = keysToRemove.iterator(); keyIt.hasNext(); ) {
                Object key = keyIt.next();
                graphicLayers.remove( key );
            }
        }
    }

    /**
     * Draws all the Paintable objects in the ApparatusPanel
     * @param graphics
     */
    protected void paintComponent( Graphics graphics ) {

        super.paintComponent( graphics );

        // Draw a bounding rectangle
        Rectangle boundingRect = this.getBounds();
        graphics.drawRect( 0, 0,
                           (int)boundingRect.getWidth() - 2,
                           (int)boundingRect.getHeight() - 2 );

        Graphics2D g2 = (Graphics2D)graphics;
        Collection layers = graphicLayers.values();
        synchronized( graphicLayers ) {
            for( Iterator layerIt = layers.iterator(); layerIt.hasNext(); ) {
                Collection layer = (Collection)layerIt.next();
                for( Iterator graphicIt = layer.iterator(); graphicIt.hasNext(); ) {
                    Paintable graphic = (Paintable)graphicIt.next();
                    graphic.paint( g2 );
                }
            }
        }
    }

    /**
     * Tells if the apparatus panel has a specified Paintable in one of its layers
     * @param paintable
     * @return
     */
    public boolean hasPaintable( Paintable paintable ) {
        boolean result = false;
        Collection layers = graphicLayers.values();
        synchronized( graphicLayers ) {
            for( Iterator layerIt = layers.iterator();
                 result == false && layerIt.hasNext(); ) {
                Collection layer = (Collection)layerIt.next();
                for( Iterator graphicIt = layer.iterator();
                     result == false && graphicIt.hasNext(); ) {
                    if( paintable == graphicIt.next() ) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Adds a PhetGraphic to the ApparatusPanel
     * @param graphic
     * @param level
     */
    public void addGraphic( PhetGraphic graphic, int level ) {

        // Bind the graphic to this container. This is needed so the graphic can
        // change the curor, etc.
        graphic.setApparatusPanel( this );
        addPaintable( graphic, level );
    }

    /**
     *
     * @param graphic
     * @param level
     */
    public void addPaintable( Paintable graphic, int level ) {

        Integer levelKey = new Integer( level );
        Collection layer = (Collection)graphicLayers.get( levelKey );
        if( layer == null ) {
            layer = new ArrayList( 100 );
            graphicLayers.put( levelKey, layer );
        }

        synchronized( graphicLayers ) {
            layer.add( graphic );
        }

        if( graphic instanceof MouseListener ) {
            this.addMouseListener( (MouseListener)graphic );
        }

        if( graphic instanceof MouseMotionListener ) {
            this.addMouseMotionListener( (MouseMotionListener)graphic );
        }
    }

    /**
     *
     * @param observable
     * @param o
     */
    public void update( Observable observable, Object o ) {

        // Have we been told that a body has been removed from the system?
        if( observable instanceof PhysicalEntity
                && o instanceof Integer
                && o == Particle.S_REMOVE_BODY ) {
            this.removeBody( (PhysicalEntity)observable );
        }
        this.invalidate();
        this.repaint();
    }

    /**
     * Remove the graphic associated with a body in the physical system
     * @param body
     */
    public void removeBody( PhysicalEntity body ) {
        for( Iterator layerIt = graphicLayers.values().iterator(); layerIt.hasNext(); ) {
            List layer = (List)layerIt.next();
            for( int i = 0; i < layer.size(); i++ ) {
                PhetGraphic graphic = (PhetGraphic)layer.get( i );
                if( graphic.getBody() == body ) {
                    synchronized( graphicLayers ) {
                        layer.remove( i );
                    }
                }
            }
        }
    }

    /**
     * Removes the specified paintable from the specified level.
     */
    public void removePaintable( Paintable graphic, int level ) {
        Collection layer = (Collection)graphicLayers.get( new
                Integer( level ) );
        layer.remove( graphic );
    }


    /**
     * Locates the first instance of a specified type of PhetGraphic in the
     * apparatus panel. This method can be used to get a reference to a particular
     * graphic.
     * @param graphicType
     * @return
     */
    public PhetGraphic getGraphicOfType( Class graphicType ) {
        PhetGraphic result = null;
        Collection layers = getGraphicLayers().values();
        for( Iterator layerIt = layers.iterator(); layerIt.hasNext(); ) {
            List layer = (List)layerIt.next();

            for( int i = 0; i < layer.size(); i++ ) {
                PhetGraphic graphic = (PhetGraphic)layer.get( i );
                if( graphicType.isInstance( graphic ) ) {
                    result = graphic;
                    break;
                }
            }
        }
        return result;
    }

    /**
     *
     * @param graphicType
     * @return
     */
    public List getAllGraphicsOfType( Class graphicType ) {
        ArrayList resultList = new ArrayList();
        Collection layers = getGraphicLayers().values();
        for( Iterator layerIt = layers.iterator(); layerIt.hasNext(); ) {
            List layer = (List)layerIt.next();

            for( int i = 0; i < layer.size(); i++ ) {
                PhetGraphic graphic = (PhetGraphic)layer.get( i );
                if( graphicType.isInstance( graphic ) ) {
                    resultList.add( graphic );
                }
            }
        }
        return resultList;
    }

    /**
     * Makes the ApparatusPanel an Observer of the PhysicalSystem.
     * <p>
     * This method should be overridden, with a call to super.activate() by
     * application-specific subclasses to do any initialization that is needed
     * when the panel becomes active
     */
    public void activate() {
        PhetApplication.instance().getPhysicalSystem().addObserver( this );
    }

    /**
     * Removes the ApparatusPanel as an Observer of the PhysicalSystem.
     * <p>
     * This method should be overridden, with a call to super.deactivate() by
     * application-specific subclasses to do any initialization that is needed
     * when the panel becomes inactive
     */
    public void deactivate() {
        PhetApplication.instance().getPhysicalSystem().deleteObserver( this );
    }


    //
    // Static fields and methods
    //

    /**
     *
     */
    private static float  worldToScreenScaleX = 1;
    private static float  worldToScreenScaleY = -1;
    private static float  screenOffsetX = 0;
    private static float  screenOffsetY = 649;

    public static float  worldToScreenX( float  worldX ) {
        float  screenX = worldX * worldToScreenScaleX + screenOffsetX;
//        return screenX;
        return worldX;
    }

    public static float  worldToScreenY( float  worldY ) {
        float  screenY = worldY * worldToScreenScaleY + screenOffsetY;
//        return screenY;
        return worldY;
    }

    public static float  screenToWorldX( float  screenX ) {
        float  worldX = ( screenX - screenOffsetX ) / worldToScreenScaleX;
//        return worldX;
        return screenX;
    }

    public static float  screenToWorldY( float  screenY ) {
        float  worldY = ( screenY - screenOffsetY ) / worldToScreenScaleY;
//        return worldY;
        return screenY;
    }

}
