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
    protected PNode listenNode = null;

    /**
     * True when event handlers are set active.
     */
    protected boolean active = false;

    // The previous component - used to generate mouseEntered and
    // mouseExited events
    Component prevComponent = null;

    // The components whose cursor is on the screen
    Component cursorComponent = null;

    // Previous points used in generating mouseEntered and mouseExited events
    Point2D prevPoint = null;
    Point2D prevOff = null;

    static class ButtonData {
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

    ButtonData leftButtonData = new ButtonData();
    ButtonData rightButtonData = new ButtonData();
    ButtonData middleButtonData = new ButtonData();

    PSwingCanvas canvas;

    // Constructor that adds the mouse listeners to a

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
//                listenNode.removeMouseListener( this );
//                listenNode.removeMouseMotionListener( this );
            }
        }
        else if( !this.active && active ) {
            if( listenNode != null ) {
                this.active = true;
                listenNode.addInputEventListener( this );
//                listenNode.addMouseListener( this );
//                listenNode.addMouseMotionListener( this );
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
//
//    // Forwards mousePressed events to Swing components used in Jazz,
//    // if any should receive the event
//    public void mousePressed( ZMouseEvent e1 ) {
//        dispatchEvent( e1 );
//    }
//
//    // Forwards mouseReleased events to Swing components used in Jazz,
//    // if any should receive the event
//    public void mouseReleased( ZMouseEvent e1 ) {
//        dispatchEvent( e1 );
//    }
//
//    // Forwards mouseClicked events to Swing components used in Jazz,
//    // if any should receive the event
//    public void mouseClicked( ZMouseEvent e1 ) {
//        dispatchEvent( e1 );
//    }
//
//    // We'll have to implement this ourselves later
//    // since it only tells us when we exit the
//    // PNode
//    public void mouseExited( ZMouseEvent e1 ) {
//        dispatchEvent( e1 );
//    }
//
//    // We'll have to implement this ourselves later
//    // since it only tells us when we enter the
//    // PNode
//    public void mouseEntered( ZMouseEvent e1 ) {
//        dispatchEvent( e1 );
//    }
//
//    // Forwards mouseMoved events to Swing components used in Jazz,
//    // if any should receive the event
//    public void mouseMoved( ZMouseEvent e1 ) {
//        dispatchEvent( e1 );
//    }
//
//    // Forwards mouseDragged events to Swing components used in Jazz,
//    // if any should receive the event
//    public void mouseDragged( ZMouseEvent e1 ) {
//        dispatchEvent( e1 );
//    }

    // A re-implementation of Container.findComponentAt that ensures
    // that the returned component is *SHOWING* not just visible
    Component findComponentAt( Component c, int x, int y ) {
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
                        comp = findComponentAt( comp, x - (int)p.getX(), y - (int)p.getY() );
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

    // Determines if any Swing components being used in Jazz should
    // receive the given MouseEvent and forwards the event to that
    // component. However, mouseEntered
    // and mouseExited are independent of the buttons
    // Also, notice the notes on mouseEntered and mouseExited
    void dispatchEvent( PSwingMouseEvent e1, PInputEvent aEvent ) {
//        System.out.println( "PSwingEventHandler.dispatchEvent: event=" + e1 );
        PNode grabNode = null;
        Component comp = null;
        Point2D pt = null;
        PNode currentNode = e1.getPath().getPickedNode();

        // The offsets to put the event in the correct context
        int offX = 0;
        int offY = 0;

//        if( currentNode instanceof ZVisualLeaf || currentNode instanceof ZVisualGroup ) {
        if( true ) {
            PNode vc = e1.getCurrentNode();
            PNode visualNode = currentNode;

            if( vc instanceof PSwing ) {

                PSwing swing = (PSwing)vc;
                grabNode = visualNode;

                if( grabNode.isDescendentOf( canvas.getRoot() ) ) {
                    pt = new Point2D.Double( e1.getX(), e1.getY() );
//                    e1.getPath().getTopCamera().cameraToLocal( pt, grabNode );

//                    System.out.println( "ClickPoint: pt = " + pt );
                    cameraToLocal( e1.getPath().getTopCamera(), pt, grabNode );
//                    System.out.println( "CameraToLocal(pt)= " + pt );
//                    prevPoint = (Point2D)pt.clone();
                    prevPoint = new Point2D.Double( pt.getX(), pt.getY() );

                    // This is only partially fixed to find the deepest
                    // component at pt.  It needs to do something like
                    // package private method:
                    // Container.getMouseEventTarget(int,int,boolean)
                    comp = findComponentAt( swing.getComponent(), (int)pt.getX(), (int)pt.getY() );
//                    System.out.println( "Found Component: comp = " + comp );
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
        }

        // This first case we don't want to give events to just
        // any Swing component - but to the one that got the
        // original mousePressed
        if( e1.getID() == MouseEvent.MOUSE_DRAGGED ||
            e1.getID() == MouseEvent.MOUSE_RELEASED ) {

            // LEFT MOUSE BUTTON
            if( SwingUtilities.isLeftMouseButton( e1 ) && leftButtonData.getFocusedComponent() != null ) {

                if( leftButtonData.getPNode().isDescendentOf( canvas.getRoot() ) ) {
                    pt = new Point2D.Double( e1.getX(), e1.getY() );
//                    e1.getPath().getTopCamera().cameraToLocal( pt, focusNodeLeft );
                    cameraToLocal( e1.getPath().getTopCamera(), pt, leftButtonData.getPNode() );
                    MouseEvent e_temp = new MouseEvent( leftButtonData.getFocusedComponent(),
                                                        e1.getID(),
                                                        e1.getWhen(),
                                                        e1.getModifiers(),
                                                        (int)pt.getX() - leftButtonData.getOffsetX(),
                                                        (int)pt.getY() - leftButtonData.getOffsetY(),
                                                        e1.getClickCount(),
                                                        e1.isPopupTrigger() );

                    PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent( e_temp.getID(), e_temp, aEvent );
//                    System.out.println( "focusComponentLeft.getClass() = " + focusComponentLeft.getClass() );
                    dispatchEvent( leftButtonData.getFocusedComponent(), e2 );
//                    focusComponentLeft.dispatchEvent( e2 );
                }
                else {
                    dispatchEvent( leftButtonData.getFocusedComponent(), e1 );
//                    focusComponentLeft.dispatchEvent( e1 );
                }

                leftButtonData.getPSwing().repaint();

                e1.consume();

                if( e1.getID() == MouseEvent.MOUSE_RELEASED ) {
                    leftButtonData.mouseReleased();
                }

            }

            // MIDDLE MOUSE BUTTON
            if( SwingUtilities.isMiddleMouseButton( e1 ) &&
                middleButtonData.getFocusedComponent() != null ) {

                if( middleButtonData.getPNode().isDescendentOf( canvas.getRoot() ) ) {
                    pt = new Point2D.Double( e1.getX(), e1.getY() );
//                    e1.getPath().getTopCamera().cameraToLocal( pt, focusNodeMiddle );
                    cameraToLocal( e1.getPath().getTopCamera(), pt, middleButtonData.getPNode() );

                    MouseEvent e_temp = new MouseEvent( middleButtonData.getFocusedComponent(),
                                                        e1.getID(),
                                                        e1.getWhen(),
                                                        e1.getModifiers(),
                                                        (int)pt.getX() - middleButtonData.getOffsetX(),
                                                        (int)pt.getY() - middleButtonData.getOffsetY(),
                                                        e1.getClickCount(),
                                                        e1.isPopupTrigger() );

                    PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent( e_temp.getID(), e_temp, aEvent );

                    middleButtonData.getFocusedComponent().dispatchEvent( e2 );
                }
                else {
                    middleButtonData.getFocusedComponent().dispatchEvent( e1 );
                }

                middleButtonData.getPSwing().repaint();

                e1.consume();

                if( e1.getID() == MouseEvent.MOUSE_RELEASED ) {
                    middleButtonData.mouseReleased();
                }

            }

            // RIGHT MOUSE BUTTON
            if( SwingUtilities.isRightMouseButton( e1 ) &&
                rightButtonData.getFocusedComponent() != null ) {

                if( rightButtonData.getPNode().isDescendentOf( canvas.getRoot() ) ) {
                    pt = new Point2D.Double( e1.getX(), e1.getY() );
//                    e1.getPath().getTopCamera().cameraToLocal( pt, focusNodeRight );
                    cameraToLocal( e1.getPath().getTopCamera(), pt, rightButtonData.getPNode() );
                    MouseEvent e_temp = new MouseEvent( rightButtonData.getFocusedComponent(),
                                                        e1.getID(),
                                                        e1.getWhen(),
                                                        e1.getModifiers(),
                                                        (int)pt.getX() - rightButtonData.getOffsetX(),
                                                        (int)pt.getY() - rightButtonData.getOffsetY(),
                                                        e1.getClickCount(),
                                                        e1.isPopupTrigger() );

                    PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent( e_temp.getID(), e_temp, aEvent );

                    rightButtonData.getFocusedComponent().dispatchEvent( e2 );
                }
                else {
                    rightButtonData.getFocusedComponent().dispatchEvent( e1 );
                }

                rightButtonData.getPSwing().repaint();

                e1.consume();

                if( e1.getID() == MouseEvent.MOUSE_RELEASED ) {
                    rightButtonData.mouseReleased();
                }

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
//            comp.dispatchEvent( e2 );

            e1.consume();
        }

        // Now we need to check if an exit or enter event needs to
        // be dispatched - this code is independent of the mouseButtons.
        // I tested in normal Swing to see the correct behavior.
        if( prevComponent != null ) {
            // This means mouseExited

            // This shouldn't happen - since we're only getting node events
            if( comp == null || e1.getID() == MouseEvent.MOUSE_EXITED ) {
                MouseEvent e_temp = new MouseEvent( prevComponent,
                                                    MouseEvent.MOUSE_EXITED,
                                                    e1.getWhen(),
                                                    0,
                                                    (int)prevPoint.getX() - (int)prevOff.getX(),
                                                    (int)prevPoint.getY() - (int)prevOff.getY(),
                                                    e1.getClickCount(),
                                                    e1.isPopupTrigger() );

                PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent( e_temp.getID(), e_temp, aEvent );

                dispatchEvent( prevComponent, e2 );
//                prevComponent.dispatchEvent( e2 );
                prevComponent = null;

                if( e1.getID() == MouseEvent.MOUSE_EXITED ) {
                    e1.consume();
                }
            }

            // This means mouseExited prevComponent and mouseEntered comp
            else if( prevComponent != comp ) {
                MouseEvent e_temp = new MouseEvent( prevComponent,
                                                    MouseEvent.MOUSE_EXITED,
                                                    e1.getWhen(),
                                                    0,
                                                    (int)prevPoint.getX() - (int)prevOff.getX(),
                                                    (int)prevPoint.getY() - (int)prevOff.getY(),
                                                    e1.getClickCount(),
                                                    e1.isPopupTrigger() );

                PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent( e_temp.getID(), e_temp, aEvent );

                dispatchEvent( prevComponent, e2 );
//                prevComponent.dispatchEvent( e2 );
                e_temp = new MouseEvent( comp,
                                         MouseEvent.MOUSE_ENTERED,
                                         e1.getWhen(),
                                         0,
                                         (int)prevPoint.getX() - offX,
                                         (int)prevPoint.getY() - offY,
                                         e1.getClickCount(),
                                         e1.isPopupTrigger() );

                e2 = PSwingMouseEvent.createMouseEvent( e_temp.getID(), e_temp, aEvent );

                comp.dispatchEvent( e2 );
            }
        }
        else {
            // This means mouseEntered
            if( comp != null ) {
                MouseEvent e_temp = new MouseEvent( comp,
                                                    MouseEvent.MOUSE_ENTERED,
                                                    e1.getWhen(),
                                                    0,
                                                    (int)prevPoint.getX() - offX,
                                                    (int)prevPoint.getY() - offY,
                                                    e1.getClickCount(),
                                                    e1.isPopupTrigger() );

                PSwingMouseEvent e2 = PSwingMouseEvent.createMouseEvent( e_temp.getID(), e_temp, aEvent );
                dispatchEvent( comp, e2 );
//                comp.dispatchEvent( e2 );
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

    private void dispatchEvent( final Component target, final PSwingMouseEvent event ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                target.dispatchEvent( event );
            }
        } );
    }

    private void cameraToLocal( PCamera topCamera, Point2D pt, PNode node ) {
        boolean newWay = true;
        if( newWay ) {

            if( topCamera.isAncestorOf( node ) ) {
//                System.out.println( "PSwingEventHandler.cameraToLocal, in camera" );
                topCamera.localToGlobal( pt );
//                System.out.println( "cam.toGlobal="+pt );
                node.globalToLocal( pt );
//                System.out.println( "node.globalToLocal: pt = " + pt );
                return;
            }

//            System.out.println( "doing cam-to-local, node.getGlobalFullBounds() = " + node.getGlobalFullBounds() );
//            topCamera.viewToLocal( pt );
//            System.out.println( "topCamera.viewToLocal(pt) = " + pt );
//            topCamera.localToGlobal( pt );
//            System.out.println( "topCamera.localToGlobal(pt)=" + pt );
//            node.globalToLocal( pt );
//            System.out.println( "node.globalToLocal(pt)=" + pt );
////            pt.setLocation( 5, 5 );
//            return;
        }
        AffineTransform inverse = null;
        try {
            inverse = topCamera.getViewTransform().createInverse();
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }
        inverse.transform( pt, pt );
        if( node != null ) {
//            PLayer layer = topCamera.getgetAncestorLayerFor( node );
//
//            if( layer == null ) {
//                // Oops - this node can't be seen through the camera
//                throw new ZNodeNotFoundException( "Node " + node + " is not accessible from camera " + this );
//            }
//            else {
            node.globalToLocal( pt );
//            }
        }
        return;
    }

    private boolean recursing = false;

    public void processEvent( PInputEvent aEvent, int type ) {
//        String note = "PSwingEventHandler.processEvent: ";
//        if( aEvent.getSourceSwingEvent().getID() == MouseEvent.MOUSE_PRESSED ) {
//            System.out.println( note + "MOUSE PRESSED" );
//        }
//        if( aEvent.getSourceSwingEvent().getID() == MouseEvent.MOUSE_RELEASED ) {
//            System.out.println( note + "MOUSE RELEASED" );
//        }
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
}
