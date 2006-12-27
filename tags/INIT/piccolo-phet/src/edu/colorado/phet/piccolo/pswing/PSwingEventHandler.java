/**
 * Copyright (C) 1998-2000 by University of Maryland, College Park, MD 20742, USA
 * All rights reserved.
 */
package edu.colorado.phet.piccolo.pswing;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * Event handler to send MousePressed, MouseReleased, MouseMoved,
 * MouseClicked, and MouseDragged events on Swing components within
 * a ZCanvas
 *
 * @author Ben Bederson
 * @author Lance Good
 */
public class PSwingEventHandler implements PInputEventListener {

    /**
     * The node to listen to for events.
     */
    private PNode listenNode = null;

    /**
     * True when event handlers are set active.
     */
    private boolean active = false;

    // The previous component - used to generate mouseEntered and
    // mouseExited events
    private Component prevComponent = null;

    // Previous points used in generating mouseEntered and mouseExited events
    private Point2D prevPoint = null;
    private Point2D prevOff = null;

    private boolean recursing = false;//to avoid accidental recursive handling

    private ButtonData leftButtonData = new ButtonData();
    private ButtonData rightButtonData = new ButtonData();
    private ButtonData middleButtonData = new ButtonData();

    private PSwingCanvas canvas;

    /**
     * Constructs a new ZSwingEventHandler for the given canvas,
     * and a node that will recieve the mouse events.
     *
     * @param canvas the canvas associated with this ZSwingEventHandler.
     * @param node   the node the mouse listeners will be attached to.
     */
    public PSwingEventHandler( PSwingCanvas canvas, PNode node ) {
        this.canvas = canvas;
        listenNode = node;
    }

    /**
     * Constructs a new ZSwingEventHandler for the given canvas.
     */
    public PSwingEventHandler( PSwingCanvas canvas ) {
        this.canvas = canvas;
    }

    public void setActive( boolean active ) {
        if( this.active && !active ) {
            if( listenNode != null ) {
                this.active = false;
                listenNode.removeInputEventListener( this );
            }
        }
        else if( !this.active && active ) {
            if( listenNode != null ) {
                this.active = true;
                listenNode.addInputEventListener( this );
            }
        }
    }

    /**
     * Determines if this event handler is active.
     *
     * @return True if active
     */
    public boolean isActive() {
        return active;
    }

    private Component findShowingComponentAt( Component c, int x, int y ) {
        if( !c.contains( x, y ) ) {
            return null;
        }

        if( c instanceof Container ) {
            Container contain = ( (Container)c );
            int ncomponents = contain.getComponentCount();
            Component component[] = contain.getComponents();

            for( int i = 0; i < ncomponents; i++ ) {
                Component comp = component[i];
                if( comp != null ) {
                    Point p = comp.getLocation();
                    if( comp instanceof Container ) {
                        comp = findShowingComponentAt( comp, x - (int)p.getX(), y - (int)p.getY() );
                    }
                    else {
                        comp = comp.getComponentAt( x - (int)p.getX(), y - (int)p.getY() );
                    }
                    if( comp != null && comp.isShowing() ) {
                        return comp;
                    }
                }
            }
        }
        return c;
    }

    // Determines if any Swing components in Piccolo should
    // receive the given MouseEvent and forwards the event to that
    // component. However, mouseEntered
    // and mouseExited are independent of the buttons
    // Also, notice the notes on mouseEntered and mouseExited
    void dispatchEvent( PSwingMouseEvent e1, PInputEvent aEvent ) {
        PNode grabNode = null;
        Component comp = null;
        Point2D pt = null;
        PNode currentNode = e1.getPath().getPickedNode();

        // The offsets to put the event in the correct context
        int offX = 0;
        int offY = 0;

        PNode vc = e1.getCurrentNode();
        PNode visualNode = currentNode;

        if( vc instanceof PSwing ) {

            PSwing swing = (PSwing)vc;
            grabNode = visualNode;

            if( grabNode.isDescendentOf( canvas.getRoot() ) ) {
                pt = new Point2D.Double( e1.getX(), e1.getY() );
                cameraToLocal( e1.getPath().getTopCamera(), pt, grabNode );
                prevPoint = new Point2D.Double( pt.getX(), pt.getY() );

                // This is only partially fixed to find the deepest
                // component at pt.  It needs to do something like
                // package private method:
                // Container.getMouseEventTarget(int,int,boolean)
                comp = findShowingComponentAt( swing.getComponent(), (int)pt.getX(), (int)pt.getY() );

                // We found the right component - but we need to
                // get the offset to put the event in the component's
                // coordinates
                if( comp != null && comp != swing.getComponent() ) {
                    for( Component c = comp; c != swing.getComponent(); c = c.getParent() ) {
                        offX += c.getLocation().getX();
                        offY += c.getLocation().getY();
                    }
                }

                // Mouse Pressed gives focus - effects Mouse Drags and
                // Mouse Releases
                if( comp != null && e1.getID() == MouseEvent.MOUSE_PRESSED ) {
                    if( SwingUtilities.isLeftMouseButton( e1 ) ) {
                        leftButtonData.setState( swing, visualNode, comp, offX, offY );
                    }
                    else if( SwingUtilities.isMiddleMouseButton( e1 ) ) {
                        middleButtonData.setState( swing, visualNode, comp, offX, offY );
                    }
                    else if( SwingUtilities.isRightMouseButton( e1 ) ) {
                        rightButtonData.setState( swing, visualNode, comp, offX, offY );
                    }
                }
            }
        }

        // This first case we don't want to give events to just
        // any Swing component - but to the one that got the
        // original mousePressed
        if( e1.getID() == MouseEvent.MOUSE_DRAGGED ||
            e1.getID() == MouseEvent.MOUSE_RELEASED ) {

            // LEFT MOUSE BUTTON
            if( SwingUtilities.isLeftMouseButton( e1 ) && leftButtonData.getFocusedComponent() != null ) {
                handleButton( e1, aEvent, leftButtonData );
            }

            // MIDDLE MOUSE BUTTON
            if( SwingUtilities.isMiddleMouseButton( e1 ) && middleButtonData.getFocusedComponent() != null ) {
                handleButton( e1, aEvent, middleButtonData );
            }

            // RIGHT MOUSE BUTTON
            if( SwingUtilities.isRightMouseButton( e1 ) && rightButtonData.getFocusedComponent() != null ) {
                handleButton( e1, aEvent, rightButtonData );
            }
        }
        // This case covers the cases mousePressed, mouseClicked,
        // and mouseMoved events
        else if( ( e1.getID() == MouseEvent.MOUSE_PRESSED ||
                   e1.getID() == MouseEvent.MOUSE_CLICKED ||
                   e1.getID() == MouseEvent.MOUSE_MOVED ) &&
                 ( comp != null ) ) {

            MouseEvent e_temp = new MouseEvent( comp,
                                                e1.getID(),
                                                e1.getWhen(),
                                                e1.getModifiers(),
                                                (int)pt.getX() - offX,
                                                (int)pt.getY() - offY,
                                                e1.getClickCount(),
                                                e1.isPopupTrigger() );

            PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent( e_temp.getID(), e_temp, aEvent );
            dispatchEvent( comp, e2 );
            e1.consume();
        }

        // Now we need to check if an exit or enter event needs to
        // be dispatched - this code is independent of the mouseButtons.
        // I tested in normal Swing to see the correct behavior.
        if( prevComponent != null ) {
            // This means mouseExited

            // This shouldn't happen - since we're only getting node events
            if( comp == null || e1.getID() == MouseEvent.MOUSE_EXITED ) {
                MouseEvent e_temp = createExitEvent( e1 );

                PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent( e_temp.getID(), e_temp, aEvent );

                dispatchEvent( prevComponent, e2 );
                prevComponent = null;

                if( e1.getID() == MouseEvent.MOUSE_EXITED ) {
                    e1.consume();
                }
            }

            // This means mouseExited prevComponent and mouseEntered comp
            else if( prevComponent != comp ) {
                MouseEvent e_temp = createExitEvent( e1 );
                PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent( e_temp.getID(), e_temp, aEvent );
                dispatchEvent( prevComponent, e2 );

                e_temp = createEnterEvent( comp, e1, offX, offY );
                e2 = PSwingMouseEvent.createMouseEvent( e_temp.getID(), e_temp, aEvent );
                comp.dispatchEvent( e2 );
            }
        }
        else {
            // This means mouseEntered
            if( comp != null ) {
                MouseEvent e_temp = createEnterEvent( comp, e1, offX, offY );
                PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent( e_temp.getID(), e_temp, aEvent );
                dispatchEvent( comp, e2 );
            }
        }

        //todo add cursors
//        // We have to manager our own Cursors since this is normally
//        // done on the native side
//        if( comp != cursorComponent &&
//            focusNodeLeft == null &&
//            focusNodeMiddle == null &&
//            focusNodeRight == null ) {
//            if( comp != null ) {
//                cursorComponent = comp;
//                canvas.setCursor( comp.getCursor(), false );
//            }
//            else {
//                cursorComponent = null;
//                canvas.resetCursor();
//            }
//        }

        // Set the previous variables for next time
        prevComponent = comp;

        if( comp != null ) {
            prevOff = new Point2D.Double( offX, offY );
        }
    }

    private MouseEvent createEnterEvent( Component comp, PSwingMouseEvent e1, int offX, int offY ) {
        return new MouseEvent( comp,
                               MouseEvent.MOUSE_ENTERED,
                               e1.getWhen(),
                               0,
                               (int)prevPoint.getX() - offX,
                               (int)prevPoint.getY() - offY,
                               e1.getClickCount(),
                               e1.isPopupTrigger() );
    }

    private MouseEvent createExitEvent( PSwingMouseEvent e1 ) {
        return new MouseEvent( prevComponent,
                               MouseEvent.MOUSE_EXITED,
                               e1.getWhen(),
                               0,
                               (int)prevPoint.getX() - (int)prevOff.getX(),
                               (int)prevPoint.getY() - (int)prevOff.getY(),
                               e1.getClickCount(),
                               e1.isPopupTrigger() );
    }

    private void handleButton( PSwingMouseEvent e1, PInputEvent aEvent, ButtonData buttonData ) {
        Point2D pt;
        if( buttonData.getPNode().isDescendentOf( canvas.getRoot() ) ) {
            pt = new Point2D.Double( e1.getX(), e1.getY() );
            cameraToLocal( e1.getPath().getTopCamera(), pt, buttonData.getPNode() );
            //todo this probably won't handle viewing through multiple cameras.
            MouseEvent e_temp = new MouseEvent( buttonData.getFocusedComponent(),
                                                e1.getID(),
                                                e1.getWhen(),
                                                e1.getModifiers(),
                                                (int)pt.getX() - buttonData.getOffsetX(),
                                                (int)pt.getY() - buttonData.getOffsetY(),
                                                e1.getClickCount(),
                                                e1.isPopupTrigger() );

            PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent( e_temp.getID(), e_temp, aEvent );
            dispatchEvent( buttonData.getFocusedComponent(), e2 );
        }
        else {
            dispatchEvent( buttonData.getFocusedComponent(), e1 );
        }

        buttonData.getPSwing().repaint();

        e1.consume();

        if( e1.getID() == MouseEvent.MOUSE_RELEASED ) {
            buttonData.mouseReleased();
        }
    }

    private void dispatchEvent( final Component target, final PSwingMouseEvent event ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                target.dispatchEvent( event );
            }
        } );
    }

    private void cameraToLocal( PCamera topCamera, Point2D pt, PNode node ) {
        AffineTransform inverse = null;
        try {
            inverse = topCamera.getViewTransform().createInverse();
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }
        inverse.transform( pt, pt );
        if( node != null ) {
            node.globalToLocal( pt );
        }
        return;
    }

    public void processEvent( PInputEvent aEvent, int type ) {
        if( aEvent.isMouseEvent() ) {
            InputEvent sourceSwingEvent = aEvent.getSourceSwingEvent();
            PSwingMouseEvent pSwingMouseEvent = PSwingMouseEvent.createMouseEvent( sourceSwingEvent.getID(), (MouseEvent)sourceSwingEvent, aEvent );
            if( !recursing ) {
                recursing = true;
                dispatchEvent( pSwingMouseEvent, aEvent );
                recursing = false;
            }
        }

    }

    /**
     * Internal Utility class for handling button interactivity.
     */
    private static class ButtonData {
        private PSwing focusPSwing = null;
        private PNode focusNode = null;
        private Component focusComponent = null;
        private int focusOffX = 0;
        private int focusOffY = 0;

        public void setState( PSwing swing, PNode visualNode, Component comp, int offX, int offY ) {
            focusPSwing = swing;
            focusComponent = comp;
            focusNode = visualNode;
            focusOffX = offX;
            focusOffY = offY;
        }

        public Component getFocusedComponent() {
            return focusComponent;
        }

        public PNode getPNode() {
            return focusNode;
        }

        public int getOffsetX() {
            return focusOffX;
        }

        public int getOffsetY() {
            return focusOffY;
        }

        public PSwing getPSwing() {
            return focusPSwing;
        }

        public void mouseReleased() {
            focusComponent = null;
            focusNode = null;
        }
    }
}
