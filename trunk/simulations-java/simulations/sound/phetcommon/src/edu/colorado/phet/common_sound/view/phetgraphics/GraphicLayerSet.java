/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common_sound.view.phetgraphics;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.MouseInputListener;

import edu.colorado.phet.common.phetcommon.util.MultiMap;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;

/**
 * GraphicLayerSet is a collection of PhetGraphics (referred to as "children").
 * Children are painted in the order that they are added.
 * Mouse events received by a GraphicLayer are forwarded to all children.
 *
 * @author ?
 * @version $Revision$
 */
public class GraphicLayerSet extends PhetGraphic {

    private MultiMap graphicMap = new MultiMap();
    private PhetGraphic activeUnit;//The unit being dragged or moused-over.
    private PhetGraphic keyFocusUnit;//The unit that should accept key events.
    private SwingAdapter swingAdapter = new SwingAdapter();
    private KeyListener keyAdapter = new KeyAdapter();
    private static int mouseEventID = 0;//For creating mouse events.

    /**
     * Provided for JavaBeans conformance
     */
    public GraphicLayerSet() {
        super( null );
    }

    public GraphicLayerSet( Component component ) {
        super( component );
    }

    /**
     * Sets the component for this object and all its children
     *
     * @param component
     */
    public void setComponent( Component component ) {
        super.setComponent( component );
        Iterator gIt = graphicMap.iterator();
        while( gIt.hasNext() ) {
            Object o = gIt.next();
            if( o instanceof PhetGraphic ) {
                PhetGraphic phetGraphic = (PhetGraphic)o;
                phetGraphic.setComponent( component );
            }
        }
    }

    /**
     * Paints all PhetGraphics in order on the Graphics2D device
     *
     * @param g2 the Graphics2D on which to paint.
     */
    public void paint( Graphics2D g2 ) {
        if( isVisible() ) {
            super.saveGraphicsState( g2 );
            super.updateGraphicsState( g2 );
            // Iterate over each child graphic.
            Iterator it = graphicMap.iterator();
            while( it.hasNext() ) {
                PhetGraphic graphic = (PhetGraphic)it.next();

                // The following test is here because as persistence support is being developed, null
                // entries are turning up in the MultiMap
                if( graphic != null ) {
                    graphic.paint( g2 );//The children know about our transform implicitly.  They handle the transform.
                }
                else {
                    System.out.println( "GraphicLayerSet.paint: graphic == null" );
                }
            }
            super.restoreGraphicsState();
        }
    }

    /**
     * This is the MouseListener that should be added to Components for listening.
     *
     * @return the SwingAdapter to listen to Components.
     */
    public SwingAdapter getMouseHandler() {
        return swingAdapter;
    }

    /**
     * Used to see if the mouse is in one of our child graphics
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
     * Used to see if the mouse is in one of our child graphics
     *
     * @param p the point
     * @return true or false
     */
    public boolean contains( Point p ) {
        return contains( p.x, p.y );
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

        // todo: Reduce inefiicieny of RectangleUtils.union, by sending a pre-allocated Rectangle to a
        // new versions of RectangleUtils.union that takes a seoond parameter, which is the result Rectangle
        Rectangle bounds = RectangleUtils.union( r );//children do their own transform.

        return bounds;
    }

    /**
     * Remove all graphics from this GraphicLayerSet.
     */
    public void clear() {
        Iterator i = graphicMap.iterator();
        PhetGraphic graphic;
        while( i.hasNext() ) {
            // Do everything that removeGraphic method does.
            graphic = (PhetGraphic)i.next();
            graphic.setParent( null );
            setBoundsDirty();
            graphic.autorepaint();//Automatically repaint.
        }
        graphicMap.clear();
    }

    /**
     * Remove a particular PhetGraphic from this composite.
     *
     * @param graphic the graphic to remove.
     */
    public void removeGraphic( PhetGraphic graphic ) {
        if( containsGraphic( graphic ) ) {
            graphicMap.removeValue( graphic );
            graphic.setParent( null );
            setBoundsDirty();
            graphic.autorepaint();//Automatically repaint.
        }
    }

    public void setBoundsDirty() {
        super.setBoundsDirty();
        Iterator it = graphicMap.iterator();
        while( it.hasNext() ) {
            PhetGraphic graphic = (PhetGraphic)it.next();
            graphic.setBoundsDirty();
        }
    }

    /**
     * Ensure that all children will repaint in their respective rectangles.
     */
    protected void forceRepaint() {
        syncBounds();//This guarantees a notification, if necessary.
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
        graphic.setParent( this );
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
        this.graphicMap.put( new Double( layer ), graphic );
        graphic.setParent( this );
        super.setBoundsDirty();
        graphic.setBoundsDirty();
        graphic.autorepaint();//Automatically repaint the added graphic.
    }

    /**
     * Moves a graphic to the top layer of the set
     *
     * @param target
     */
    public void moveToTop( PhetGraphic target ) {
        this.removeGraphic( target );
        graphicMap.put( graphicMap.lastKey(), target );
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
     * Returns the number of graphics in the GraphicLayerSet
     *
     * @return
     */
    public int getNumGraphics() {
        return graphicMap.size();
    }

    ///////////////////////////////////////////////////////////////////////////////
    // Setters and getters for persistence
    //
    public void setGraphicMap( MultiMap graphicMap ) {
        this.graphicMap = graphicMap;
    }


    ///////////////////////////////////////////////////////////////////////////////
    // Methods for MouseInteraction.
    //
    public void startDragging( MouseEvent event, PhetGraphic activeUnit ) {
        if( this.activeUnit != null ) {
            this.activeUnit.fireMouseExited( event );
        }
        this.activeUnit = activeUnit;
        activeUnit.fireMouseEntered( event );
        activeUnit.fireMousePressed( event );
        activeUnit.fireMouseDragged( event );
    }

    //experimental
    public void clearActiveUnit() {
        if( this.activeUnit != null ) {
            activeUnit.fireMouseReleased( new MouseEvent( getComponent(), mouseEventID++, System.currentTimeMillis(), 0, 0, 0, 0, false ) );
            activeUnit.fireMouseExited( new MouseEvent( getComponent(), mouseEventID++, System.currentTimeMillis(), 0, 0, 0, 0, false ) );
        }
    }

    /**
     * Determine the active unit, based on the mouse event, firing enter and exit events if necessary.
     *
     * @param e
     */
    protected void handleEntranceAndExit( MouseEvent e ) {
        // Find the topmost graphic that can handle the event
        PhetGraphic newUnit = getHandler( e.getPoint() );
//        System.out.println( "newUnit = " + newUnit );
        if( newUnit == null ) {
            // If the mouse isn't over anything contained in the
            // CompositeGraphic...
            if( activeUnit != null ) {
                activeUnit.fireMouseExited( e );
                activeUnit = null;
            }
        }
        else {//newUnit was non-null.
            if( activeUnit == newUnit ) {
                //same guy
            }
            else if( activeUnit == null ) {
                //Fire a mouse entered, set the active newUnit.
                activeUnit = newUnit;
                activeUnit.fireMouseEntered( e );
            }
            else if( activeUnit != newUnit ) {
                //Switch active units.
                activeUnit.fireMouseExited( e );
                activeUnit = newUnit;
                activeUnit.fireMouseEntered( e );
            }
        }
        //        System.out.println( "activeUnit = " + activeUnit );
    }

    /**
     * Determine the PhetGraphic suited for handling a click at the specified point.
     * If no such graphic is identified and this GraphicLayerSet is interested in
     * mouse events, then this GraphicLayerSet is returned.
     *
     * @param p the mouse point.
     * @return the handler.
     */
    protected PhetGraphic getHandler( Point p ) {

        // If the GraphicLayerSet is ignoring the mouse, then don't check any children.
        if( getIgnoreMouse() == true ) {
            return null;
        }

        PhetGraphic[] graphics = getGraphics();
        PhetGraphic result = null;

        // For each graphic, working from foreground to background layer...
        for( int i = graphics.length - 1; result == null && i >= 0; i-- ) {
            PhetGraphic g = graphics[i];

            // XMLEncoder/Decoder serialization puts nulls in the map, for some reason.
            if( g != null ) {
                if( g.isVisible() && !g.getIgnoreMouse() ) {
                    if( g instanceof GraphicLayerSet ) {
                        // Ask the GraphicLayerSet for the graphic.
                        result = ( (GraphicLayerSet)g ).getHandler( p );
                    }
                    else if( g.contains( p.x, p.y ) ) {
                        // We picked this graphic.
                        result = g;
                    }
                }
            }
        }

        // We picked a graphic with no mouse listener, 
        // and this GraphicLayerSet does have a mouse listener.
        // So let the GraphicLayerSet handle the event.
        if( result != null && result.numMouseInputListeners() == 0 &&
            isVisible() && numMouseInputListeners() != 0 && this.contains( p ) ) {
            result = this;
        }

        return result;
    }

    public KeyListener getKeyAdapter() {
        return keyAdapter;
    }

    public void setKeyFocus( PhetGraphic focus ) {
        if( keyFocusUnit != focus ) {
            if( keyFocusUnit != null ) {
                keyFocusUnit.lostKeyFocus();
            }

            this.keyFocusUnit = focus;
            //Fire a focus change.
            if( keyFocusUnit != null ) {
                keyFocusUnit.gainedKeyFocus();
            }
        }

    }

    public void childBecameInvisible( PhetGraphic phetGraphic ) {
        if( keyFocusUnit == phetGraphic ) {
            setKeyFocus( null );
        }
        if( activeUnit == phetGraphic ) {
            MouseEvent mouseEvent = new MouseEvent( getComponent(), mouseEventID++, System.currentTimeMillis(), 0, 0, 0, 0, false );
            activeUnit.fireMouseExitedBecauseInvisible( mouseEvent );
            activeUnit = null;
        }
    }

    public MultiMap getGraphicMap() {
        return graphicMap;
    }

    //////////////////////////////////////////////////////////////
    // Inner classes
    //
    public class KeyAdapter implements KeyListener {
        //TODO this should probably include code to have a separate key-focused handler.
        public void keyTyped( KeyEvent e ) {
            if( keyFocusUnit != null ) {
                keyFocusUnit.fireKeyTyped( e );
            }
        }

        public void keyPressed( KeyEvent e ) {
            if( keyFocusUnit != null ) {
                keyFocusUnit.fireKeyPressed( e );
            }
        }

        public void keyReleased( KeyEvent e ) {
            if( keyFocusUnit != null ) {
                keyFocusUnit.fireKeyReleased( e );
            }
        }
    }


    /**
     * This class is used on a Swing component or AWT component to forward events to
     * the PhetGraphic subsystem.
     */
    public class SwingAdapter implements MouseInputListener {
        private boolean pressed = false;

        public void mouseClicked( MouseEvent e ) {
            //Make sure we're over the active guy.
            handleEntranceAndExit( e );
            if( activeUnit != null ) {
                activeUnit.fireMouseClicked( e );
            }
            setKeyFocus( activeUnit );
            pressed = false;
        }


        public void mousePressed( MouseEvent e ) {
            handleEntranceAndExit( e );
            if( activeUnit != null ) {
                activeUnit.fireMousePressed( e );
            }
            setKeyFocus( activeUnit );
            pressed = true;
        }

        /**
         * When the user releases the mouse, the following happens:
         * 1. The active unit gets a mouse release event.
         * 2. If the mouse has left the active unit:
         * a. A mouse exited event is fired on the active unit.
         * b. We check to see if the mouse is over a different interactive graphic.  If so:
         * ii. Fire a mouse Entered on the new unit.
         * 3.
         *
         * @param e
         */
        public void mouseReleased( MouseEvent e ) {
            if( activeUnit != null ) {
                activeUnit.fireMouseReleased( e );
                handleEntranceAndExit( e );
            }
            pressed = false;
        }

        /**
         * This method is no-op because if the user is dragging a graphic,
         * and handleEntranceAndExit() gets
         * called, the boundary may be dropped.
         *
         * @param e
         */
        public void mouseEntered( MouseEvent e ) {
            if( !pressed && activeUnit == null ) {
                handleEntranceAndExit( e );
            }
        }

        /**
         * This method is no-op because if the user is dragging a graphic,
         * and handleEntranceAndExit() gets
         * called, the boundary may be dropped.
         *
         * @param e
         */
        public void mouseExited( MouseEvent e ) {
            if( !pressed && activeUnit != null ) {
                activeUnit.fireMouseExited( e );
                activeUnit = null;
//                handleEntranceAndExit( e );
            }
        }

        public void mouseDragged( MouseEvent e ) {
            if( activeUnit != null ) {
                activeUnit.fireMouseDragged( e );
            }
            pressed = true;
        }

        public void mouseMoved( MouseEvent e ) {
            //iterate down over the mouse handlers.
            handleEntranceAndExit( e );
            if( activeUnit != null ) {
                activeUnit.fireMouseMoved( e );
            }
            pressed = false;
        }
    }

    public boolean containsGraphic( PhetGraphic graphic ) {
        return graphicMap.containsValue( graphic );
    }

    public PhetGraphic getActiveUnit() {
        return activeUnit;
    }

    public Rectangle getVisibleBounds() {
        PhetGraphic[] ch = getGraphics();
        Rectangle[] r = new Rectangle[ch.length];
        for( int i = 0; i < r.length; i++ ) {
            r[i] = ch[i].getVisibleBounds();
        }

        // todo: Reduce inefiicieny of RectangleUtils.union, by sending a pre-allocated Rectangle to a
        // new versions of RectangleUtils.union that takes a seoond parameter, which is the result Rectangle
        Rectangle bounds = RectangleUtils.union( r );//children do their own transform.

        return bounds;
    }

    /*
    * When a GraphicLayerSet's visibility changes, we need to also
    * notify all of its children's listeners.
    */
    protected void fireVisibilityChanged() {
        super.fireVisibilityChanged();
        Iterator it = graphicMap.iterator();
        while( it.hasNext() ) {
            PhetGraphic graphic = (PhetGraphic)it.next();
            graphic.fireVisibilityChanged();
        }
    }
}
