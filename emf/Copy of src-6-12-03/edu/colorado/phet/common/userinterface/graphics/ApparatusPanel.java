/*
 * Class: ApparatusPanel
 * Package: edu.colorado.phet.common.userinterface.graphics
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.common.userinterface.graphics;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * This is a base class for panels that contain graphic representations
 * of elements in the PhysicalSystem.
 * <p>
 * The graphic objects to be displayed are maintained in "layers". Each layer can
 * contain any number of Graphic objects, and each layer has an integer "level"
 * associated with it. Layers are drawn in ascending order of their levels. The order
 * in which objects in a given level are drawn in undefined.
 * <p>
 * Levels less than 0 are reserved for items that are always to be displayed. This
 * could, for example, be used for a fixture or instrument that is always to appear as
 * part of the apparatus, such as a table or meter. When this class' removeAllModelElements() method is
 * executed these objects are not destroyed.
 * <p>
 * Levels 1 and higher are used for objects that can be created and destroyed as the
 * application runs. All objects in these layers are destroyed when the removeAllModelElements() method
 * is executed.
 * <p>
 * Instances of this class are Observers of the application's PhysicalSystem
 *
 * @see edu.colorado.phet.common.userinterface.graphics.Graphic
 * @see edu.colorado.phet.deprecatedorstillunderdevelopment.graphics.PhetGraphic
 */
public class ApparatusPanel extends JPanel implements Observer {

    // The map of graphic objects to be drawn in the panel
    private TreeMap graphicLayers = new TreeMap();
    private MouseHandler mh;

    /**
     *
     * @param observable
     */
    public ApparatusPanel( Observable observable ) {
        this();
        observable.addObserver( this );
    }

    /**
     *
     */
    public ApparatusPanel() {
        // Call superclass constructor with null so that we
        // don't get the default layout manager. This allows us
        // to lay out components with absolute coordinates
        super( null );
        this.setBackground( Color.WHITE );
        // Set the size of the panel
        this.setPreferredSize( new Dimension( 600, 520 ) );
        this.mh = new MouseHandler();
        addMouseListener( mh );
        addMouseMotionListener( mh );
    }

    /**
     * Returns the sorted map of graphic layers
     * @return The sorted map of graphic layers
     */
    protected TreeMap getGraphicLayers() {
        return graphicLayers;
    }

    /**
     * Clears objects in the graphical context that are experiment-specific
     */
    public void clear() {
        graphicLayers.clear();
    }

    /**
     * Draws all the Graphic objects in the ApparatusPanel
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
                    Graphic graphic = (Graphic)graphicIt.next();
                    graphic.paint( g2 );
                }
            }
        }
    }

    /**
     *
     * @param graphic
     * @param level
     */
    public void addGraphic( Graphic graphic, int level ) {

        Integer levelKey = new Integer( level );
        Collection layer = (Collection)graphicLayers.get( levelKey );
        if( layer == null ) {
            layer = new ArrayList( 100 );
            graphicLayers.put( levelKey, layer );
        }

        synchronized( graphicLayers ) {
            layer.add( graphic );
        }
    }

    /**
     * Removes the specified paintable from the specified level.
     */
    public void removeGraphic( Graphic graphic ) {
        synchronized( graphicLayers ) {
            for( Iterator layerIt = graphicLayers.values().iterator(); layerIt.hasNext(); ) {
                Collection layer = (Collection)layerIt.next();
                ArrayList removeList = new ArrayList();
                for( Iterator graphicIt = layer.iterator();
                     graphicIt.hasNext(); ) {
                    Graphic testGraphic = (Graphic)graphicIt.next();
                    if( testGraphic == graphic ) {
                        removeList.add( testGraphic );
                    }
                }
                layer.removeAll( removeList );
            }
        }
    }

    /**
     * Returns the InteractiveGraphic in the ApparatusPanel that should
     * handle a specified mouse event
     * @param e
     * @return
     */
    private InteractiveGraphic determineMouseHandler( MouseEvent e ) {
        InteractiveGraphic ig = null;
        Collection layers = graphicLayers.values();
        synchronized( graphicLayers ) {
            for( Iterator layerIt = layers.iterator(); layerIt.hasNext(); ) {
                Collection layer = (Collection)layerIt.next();
                for( Iterator graphicIt = layer.iterator(); graphicIt.hasNext(); ) {
                    Graphic graphic = (Graphic)graphicIt.next();
                    if( graphic instanceof InteractiveGraphic ) {
                        InteractiveGraphic interactiveGraphic = (InteractiveGraphic)graphic;
                        if( interactiveGraphic.canHandleMousePress( e ) ) {
                            ig = interactiveGraphic;
                        }
                    }
                }
            }
        }
        return ig;
    }

    public void update( Observable o, Object arg ) {
        repaint();
    }


    //
    // Inner classes
    //

    private class MouseHandler extends MouseInputAdapter {

        InteractiveGraphic currentIG;

        // implements java.awt.event.MouseListener
        public void mousePressed( MouseEvent e ) {

            // Determine which InteractiveGraphic should handle
            // the event
            InteractiveGraphic ig = ApparatusPanel.this.determineMouseHandler( e );

            // Set the currentIG to be that IG
            currentIG = ig;

            // Pass the event to the currentIG
            if( currentIG != null )
                currentIG.mousePressed( e );
        }

        // implements java.awt.event.MouseListener
        public void mouseReleased( MouseEvent e ) {
            // pass the event to the currentIG
            if( currentIG != null )
                currentIG.mouseReleased( e );
            ;

            currentIG = null;
        }

        // implements java.awt.event.MouseMotionListener
        public void mouseDragged( MouseEvent e ) {
            // If currentIG != null, give him the event
            if( currentIG != null )
                currentIG.mouseDragged( e );
        }
    }
}
