/*
 * Class: ApparatusPanel
 * Package: edu.colorado.phet.common.view.graphics
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * This is a base class for panels that contain graphic representations
 * of elements in the PhysicalSystem.
 * <p/>
 * The graphic objects to be displayed are maintained in "layers". Each layer can
 * contain any number of Graphic objects, and each layer has an integer "level"
 * associated with it. Layers are drawn in ascending order of their levels. The order
 * in which objects in a given level are drawn in undefined.
 * <p/>
 * Levels less than 0 are reserved for items that are always to be displayed. This
 * could, for example, be used for a fixture or instrument that is always to appear as
 * part of the apparatus, such as a table or meter. When this class' removeAllModelElements() method is
 * executed these objects are not destroyed.
 * <p/>
 * Levels 1 and higher are used for objects that can be created and destroyed as the
 * application runs. All objects in these layers are destroyed when the removeAllModelElements() method
 * is executed.
 * <p/>
 * Instances of this class are Observers of the application's PhysicalSystem
 *
 * @see Graphic
 */
public class CompositeGraphic implements InteractiveGraphic {

    // The map of graphic objects to be drawn in the panel
    private TreeMap graphicLayers = new TreeMap();
    private InteractiveGraphic selectedGraphic;
    private InteractiveGraphic mouseEnteredGraphic;
    private List inorder = new ArrayList();

    /**
     *
     */
    public CompositeGraphic() {
    }

    /**
     * Returns the sorted map of graphic layers
     * @return The sorted map of graphic layers
     */

    /**
     * Clears objects in the graphical context that are experiment-specific
     */
    public void removeAllGraphics() {
        graphicLayers.clear();
        recreateList();
    }

    /**
     * Draws all the Graphic objects in the ApparatusPanel
     */
    public void paint( Graphics2D g2 ) {
        //List elements = this.getElementsInOrder();
//        List elements = inorder;
        for( int i = 0; i < inorder.size(); i++ ) {
            Graphic graphic = (Graphic)inorder.get( i );
            graphic.paint( g2 );
        }
    }

    /**
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
        recreateList();
    }

    private void recreateList() {
        this.inorder = this.getElementsInOrder();
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
        recreateList();
    }


    /**
     * Returns the InteractiveGraphic in the ApparatusPanel that should
     * handle a specified mouse event
     *
     * @param e
     * @return
     */
    public InteractiveGraphic determineMouseHandler( MouseEvent e ) {
        InteractiveGraphic ig = null;
        synchronized( graphicLayers ) {
            List elements = this.getElementsInOrder();
            for( int i = elements.size() - 1; i >= 0; i-- ) {

                Graphic graphic = (Graphic)elements.get( i );

                if( graphic instanceof InteractiveGraphic
                    && ( (InteractiveGraphic)graphic ).canHandleMousePress( e ) ) {
                    ig = (InteractiveGraphic)graphic;
                    if( ig instanceof CompositeGraphic ) {
                        ig = ( (CompositeGraphic)ig ).determineMouseHandler( e );
                    }
                    break;
                }
            }
        }
        return ig;
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return determineMouseHandler( event ) != null;
    }

    public void mousePressed( MouseEvent event ) {
        if( selectedGraphic == null ) {
            selectedGraphic = determineMouseHandler( event );
            if( selectedGraphic != null ) {
                selectedGraphic.mousePressed( event );
            }
        }
    }

    public void mouseDragged( MouseEvent event ) {
        if( selectedGraphic != null ) {
            selectedGraphic.mouseDragged( event );
        }
    }

    public void mouseReleased( MouseEvent event ) {
        if( selectedGraphic != null ) {
            selectedGraphic.mouseReleased( event );
        }
        selectedGraphic = null;
    }

    public void mouseEntered( MouseEvent event ) {
        InteractiveGraphic newIG = determineMouseHandler( event );
        // If the mouse is still over an interactive graphic, tell him
        if( newIG != null && newIG != mouseEnteredGraphic ) {
            mouseEnteredGraphic = newIG;
            mouseEnteredGraphic.mouseEntered( event );
        }
    }

    public void mouseExited( MouseEvent event ) {
        if( mouseEnteredGraphic != null ) {
            mouseEnteredGraphic.mouseExited( event );
            mouseEnteredGraphic = null;
        }
    }


    /**
     * Returns a list of the elements in all the layers, in order of their
     * painting order
     *
     * @return
     */
    private List getElementsInOrder() {
        ArrayList elements = new ArrayList();
        synchronized( graphicLayers ) {
            Collection layers = graphicLayers.values();
            for( Iterator layerIt = layers.iterator(); layerIt.hasNext(); ) {
                Collection layer = (Collection)layerIt.next();
                for( Iterator graphicIt = layer.iterator(); graphicIt.hasNext(); ) {
                    elements.add( graphicIt.next() );
                }
            }
        }
        return elements;
    }

    public void mouseMoved( MouseEvent e ) {
        // If the interactive graphic that the mouse is over is
        // has changed, then note it.
        InteractiveGraphic newIG = determineMouseHandler( e );

        //Moving off of a graphic to no graphic.
        if( mouseEnteredGraphic != null && newIG == null ) {
            mouseEnteredGraphic.mouseExited( e );
            mouseEnteredGraphic = null;
        }
        //Moving from one graphic to another.
        else if( mouseEnteredGraphic != null && newIG != null && mouseEnteredGraphic != newIG ) {
            mouseEnteredGraphic.mouseExited( e );
            mouseEnteredGraphic = newIG;
            mouseEnteredGraphic.mouseEntered( e );
        }
        //Moved to something from nothing.
        else if( newIG != null && mouseEnteredGraphic == null ) {
            mouseEnteredGraphic = newIG;
            mouseEnteredGraphic.mouseEntered( e );
        }
    }
}
