package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.util.MultiMap;
import edu.colorado.phet.common.view.util.RectangleUtils;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class GraphicLayerSet extends PhetGraphic {

    private MultiMap graphicMap = new MultiMap();
    private PhetGraphic activeUnit;
    private SwingAdapter swingAdapter;

    public GraphicLayerSet( Component component ) {
        super( component );
        this.swingAdapter = new SwingAdapter();
    }

    /**
     * Paints all PhetGraphics in order on the Graphics2D device
     *
     * @param g the Graphics2D on which to paint.
     */
    public void paint( Graphics2D g ) {
        if( isVisible() ) {
            Iterator it = graphicMap.iterator();
            while( it.hasNext() ) {
                PhetGraphic graphic = (PhetGraphic)it.next();
                graphic.paint( g );
            }
        }
    }

    /**
     * This is the MouseListener that should be added to Components for listening.
     *
     * @return the SwingAdapter to listen to Components.
     */
    public SwingAdapter getSwingAdapter() {
        return swingAdapter;
    }

    /**
     * Used to see if the mouse is in component InteractiveGraphic
     *
     * @param x
     * @param y
     * @return true if any child graphics contain the specified point.
     */
    public boolean contains( int x, int y ) {
        if( isVisible() ) {
            Iterator it = this.graphicMap.iterator();
            while( it.hasNext() ) {
                PhetGraphic o = (PhetGraphic)it.next();
                if( o.contains( x, y ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines the union of child graphics bounds.
     *
     * @return the union of child graphics bounds.
     */
    protected Rectangle determineBounds() {
        PhetGraphic[] ch = getGraphics();
        Rectangle[] r = new Rectangle[ch.length];
        for( int i = 0; i < r.length; i++ ) {
            r[i] = ch[i].getBounds();
        }
        return RectangleUtils.union( r );
    }

    /**
     * Remove all graphics from this GraphicLayerSet.
     */
    public void clear() {
        graphicMap.clear();
    }

    /**
     * Remove a particular PhetGraphic from this composite.
     *
     * @param graphic the graphic to remove.
     */
    public void removeGraphic( PhetGraphic graphic ) {
        graphicMap.removeValue( graphic );
    }

    /**
     * Ensure that all children will repaint in their respective rectangles.
     */
    protected void forceRepaint() {
        Iterator it = graphicMap.iterator();
        while( it.hasNext() ) {
            PhetGraphic graphic = (PhetGraphic)it.next();
            graphic.forceRepaint();
        }

    }

    /**
     * Add the specified graphic to the default (0th) layer.
     *
     * @param graphic the graphic to add.
     */
    public void addGraphic( PhetGraphic graphic ) {
        addGraphic( graphic, 0 );
    }

    /**
     * Returns graphics from a forward iterator.
     *
     * @return the array of graphics in painting order.
     */

    public PhetGraphic[] getGraphics() {
        Iterator it = graphicMap.iterator();
        ArrayList graphics = new ArrayList();
        while( it.hasNext() ) {
            PhetGraphic graphic = (PhetGraphic)it.next();
            graphics.add( graphic );
        }
        return (PhetGraphic[])graphics.toArray( new PhetGraphic[0] );
    }

    /**
     * Add the specified graphic to the specified layer.
     *
     * @param graphic
     * @param layer
     */
    public void addGraphic( PhetGraphic graphic, double layer ) {
        this.graphicMap.add( new Double( layer ), graphic );
    }

    public void moveToTop( PhetGraphic target ) {
        this.removeGraphic( target );
        graphicMap.add( graphicMap.lastKey(), target );
    }

    /**
     * A forward iterator over the PhetGraphics.
     *
     * @return the PhetGraphic iterator.
     */
    protected Iterator iterator() {
        return this.graphicMap.iterator();
    }

    /**
     * Methods for MouseInteraction.
     */
    public void startDragging( MouseEvent event, PhetGraphic activeUnit ) {
        if( this.activeUnit != null ) {
            this.activeUnit.mouseExited( event );
        }
        this.activeUnit = activeUnit;
        activeUnit.mouseEntered( event );
        activeUnit.mousePressed( event );
        activeUnit.mouseDragged( event );
    }

    /**
     * Determine the active unit, based on the mouse event, firing enter and exit events if necessary.
     *
     * @param e
     */
    protected void handleEntranceAndExit( MouseEvent e ) {
        // Find the topmost graphic that can handle the event
        PhetGraphic unit = getHandler( e.getPoint() );
//        System.out.println( "unit = " + unit );
        if( unit == null ) {
            // If the mouse isn't over anything contained in the
            // CompositeGraphic...
            if( activeUnit != null ) {
                activeUnit.mouseExited( e );
                activeUnit = null;
            }
        }
        else {//unit was non-null.
            if( activeUnit == unit ) {
                //same guy
            }
            else if( activeUnit == null ) {
                //Fire a mouse entered, set the active unit.
                activeUnit = unit;
                activeUnit.mouseEntered( e );
            }
            else if( activeUnit != unit ) {
                //Switch active units.
                activeUnit.mouseExited( e );
                activeUnit = unit;
                activeUnit.mouseEntered( e );
            }
        }
        //        System.out.println( "activeUnit = " + activeUnit );
    }

    /**
     * Determine the PhetGraphic suited for handling a click at the specified point.
     *
     * @param p the mouse point.
     * @return the handler.
     */
    protected PhetGraphic getHandler( Point p ) {
        PhetGraphic[] graphics = getGraphics();
        PhetGraphic result = null;
        for( int i = graphics.length - 1; result == null && i >= 0; i-- ) {
            PhetGraphic g = graphics[i];
            if( g.isVisible() ) {
                if( g instanceof GraphicLayerSet ) {
                    GraphicLayerSet gx = (GraphicLayerSet)g;
                    result = gx.getHandler( p );
                }
                else if( g.contains( p.x, p.y ) ) {
                    result = g;
                }
            }
        }
        return result;
    }

    /**
     * This class is used on a Swing component or AWT component to forward events to
     * the PhetGraphic subsystem.
     */
    public class SwingAdapter implements MouseInputListener {

        public void mouseClicked( MouseEvent e ) {
            //Make sure we're over the active guy.
            handleEntranceAndExit( e );
            if( activeUnit != null ) {
                activeUnit.mouseClicked( e );
            }
        }

        public void mousePressed( MouseEvent e ) {
            handleEntranceAndExit( e );
            if( activeUnit != null ) {
                activeUnit.mousePressed( e );
            }
        }

        public void mouseReleased( MouseEvent e ) {
            if( activeUnit != null ) {
                activeUnit.mouseReleased( e );
            }
        }

        /**
         * This method is no-op because if the user is dragging a graphic,
         * and handleEntranceAndExit() gets
         * called, the boundary may be dropped.
         *
         * @param e
         */
        public void mouseEntered( MouseEvent e ) {
        }

        /**
         * This method is no-op because if the user is dragging a graphic,
         * and handleEntranceAndExit() gets
         * called, the boundary may be dropped.
         *
         * @param e
         */
        public void mouseExited( MouseEvent e ) {
        }

        public void mouseDragged( MouseEvent e ) {
            if( activeUnit != null ) {
                activeUnit.mouseDragged( e );
            }
        }

        public void mouseMoved( MouseEvent e ) {
            //iterate down over the mouse handlers.
            handleEntranceAndExit( e );
            if( activeUnit != null ) {
                activeUnit.mouseMoved( e );
            }
        }

    }

    public int getX() {
        return getLocation().x;
    }

    public int getY() {
        return getLocation().y;
    }
}
