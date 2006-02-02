/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.piccolo.help;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * PGlassPane is a Piccolo-based glass pane that does not intercept any
 * events or cursor requests. All events and calls to getCursor are passed
 * to the content pane and menu bar of the parent frame.
 * <p>
 * Sample usage:
 * <code>
 *    JFrame frame = PhetApplication.instance().getPhetFrame();
 *    PGlassPane glassPane = new PGlassPane( frame );
 *    frame.setGlassPane( glassPane );
 *    glassPane.setVisible( true );
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PGlassPane extends PCanvas {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JFrame _parentFrame;  // we'll be serving as this frame's glass pane
    private MouseHandler _mouseHandler; // tracks mouse position & redispatches mouse events

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a PGlassPane.
     * 
     * @param parentFrame this PGlassPane is intended to be the glass pane for parentFrame
     */
    public PGlassPane( JFrame parentFrame ) {
        super();

        // the parent frame
        _parentFrame = parentFrame;

        // The glass pane is transparent
        setBackground( new Color( 0, 0, 0, 0 ) );
        
        // Disable pan & zoom
        setPanEventHandler( null );
        setZoomEventHandler( null );
        
        // Disable interactivity
        getLayer().setPickable( false );
        getLayer().setChildrenPickable( false );

        // Mouse position tracking and event redispatching...
        _mouseHandler = new MouseHandler( parentFrame );
        setMouseHandlerEnabled( true );
    }
    
    //----------------------------------------------------------------------------
    // Accessors 
    //----------------------------------------------------------------------------
    
    /**
     * Gets the parent frame.
     * This PGlassPane is intended to serves as the glass pane for the parent frame.
     * 
     * @return the parent frame
     */
    protected JFrame getParentFrame() {
        return _parentFrame;
    }
    
    /**
     * Enables and disables handlng of mouse events.
     * When enabled, we track the mouse position and redispatch MouseEvents.
     * When disabled, we aren't tracking the mouse position and all MouseEvents
     * pass through the glass pane.
     * <p>
     * NOTE: Since we're only interested in the mouse position, one would think
     * that we would only need to add a MouseMotionListener.  This is not the case;
     * redispatching does not work if we do not add both a MouseListener and a 
     * MouseMotionListener.
     * 
     * @param enabled
     */
    private void setMouseHandlerEnabled( boolean enabled ) {
        if ( enabled ) {
            super.addMouseMotionListener( _mouseHandler );
            super.addMouseListener( _mouseHandler );
        }
        else {
            super.removeMouseMotionListener( _mouseHandler );
            super.removeMouseListener( _mouseHandler );
        }
    }
    
    //----------------------------------------------------------------------------
    // Overrides 
    //----------------------------------------------------------------------------
    
    /**
     * Gets the cursor of the component in the content pane 
     * at the current mouse location.
     * 
     * @return the cursor
     */
    public Cursor getCursor() {
        Cursor cursor = null;
        Component glassPane = this;
        Point glassPanePoint = _mouseHandler.getMouseLocation();
        Component contentPane = _parentFrame.getContentPane();
        Point contentPanePoint = SwingUtilities.convertPoint( glassPane, glassPanePoint, contentPane );
        Component component = SwingUtilities.getDeepestComponentAt( contentPane, contentPanePoint.x, contentPanePoint.y );
        if ( component != null ) {
            cursor = component.getCursor();
        }
        if ( cursor == null ) {
            cursor = super.getCursor();
        }
        return cursor;
    }
    
    /*
     * If we don't have listeners for events, then they are
     * automatically directed to the content pane, menu bar, etc.
     * So we override the corresponding "addListener" methods with stubs.
     * <p>
     * Note that we do add a listener for MouseEvents by calling super addListener methods.
     * We DO want to override MouseEvent-related addListeners with stubs so that
     * no one except us can add one of these listener.
     */
    
    public synchronized void addFocusListener( FocusListener l ) {}

    public synchronized void addInputEventListener( PInputEventListener l ) {}

    public synchronized void addInputMethodListener( InputMethodListener l ) {}

    public synchronized void addKeyListener( KeyListener l ) {}

    public synchronized void addMouseListener( MouseListener l ) {}

    public synchronized void addMouseMotionListener( MouseMotionListener l ) {}

    public synchronized void addMouseWheelListener( MouseWheelListener l ) {}

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /**
     * MouseManager manages mouse events for the glass pane.
     * It redispatches mouse events to the proper component in the content pane
     * or menu bar, and it keeps track of the current mouse location.
     */
    private class MouseHandler implements MouseMotionListener, MouseListener {

        private JFrame _frame;
        private Point _mouseLocation;
        
        //----------------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------------
        
        /**
         * Constructor.
         * 
         * @param frame the frame for which mouse events are being managed
         */
        public MouseHandler( JFrame frame ) {
            _frame = frame;
            _mouseLocation = new Point();
        }

        //----------------------------------------------------------------------------
        // Accessors
        //----------------------------------------------------------------------------
        
        private void setMouseLocation( Point p ) {
            _mouseLocation.setLocation( p );
        }
        
        public Point getMouseLocation() {
            return _mouseLocation;
        }
        
        public int getMouseX() {
            return _mouseLocation.x;
        }
        
        public int getMouseY() {
            return _mouseLocation.y;
        }

        //----------------------------------------------------------------------------
        // MouseEvent handling -- all events are redispatched
        //----------------------------------------------------------------------------
        
        /**
         * Save the mouse location before remapping.
         * We'll need the mouse location to redirect getCursor calls.
         * 
         * @param event
         */
        public void mouseMoved( MouseEvent event ) {
            setMouseLocation( event.getPoint() );
            redispatch( event );
        }
        
        public void mouseDragged( MouseEvent event ) {
            redispatch( event );
        }
        
        public void mouseClicked( MouseEvent event ) {
            redispatch( event );
        }
        
        public void mousePressed( MouseEvent event ) {
            redispatch( event );
        }
        
        public void mouseReleased( MouseEvent event ) {
            redispatch( event );
        }
        
        public void mouseEntered( MouseEvent event ) {
            redispatch( event );
        }
        
        public void mouseExited( MouseEvent event ) {
            redispatch( event );
        }

        //----------------------------------------------------------------------------
        // MouseEvent redispatching
        //----------------------------------------------------------------------------
        
        /*
         * Redispatches a MouseEvent to the component that is below 
         * the glass pane at the current mouse position.
         * 
         * @param event
         */
        private void redispatch( MouseEvent event  ) {
            redispatch2( event );
        }
        
        /*
         * Redispatches a mouse event to either the content pane or the help pane,
         * based on the mouse's position.
         * 
         * problems:
         * - menu items don't receive mouse events
         * - menus don't hilite when you move the mouse across the menubar
         * - menu items don't hilite when you move the mouse down a menu
         * - pop-ups and palettes probably don't work (not tested)
         */
        private void redispatch1( MouseEvent event ) {
            
            Component glassPane = _frame.getGlassPane();
            Container contentPane = _frame.getContentPane();
            JMenuBar menuBar = _frame.getJMenuBar();
            
            Point glassPanePoint = event.getPoint();
            
            Container container = contentPane;
            Point containerPoint = SwingUtilities.convertPoint( glassPane, glassPanePoint, contentPane );
            if ( containerPoint.y < 0 ) {
                container = menuBar;
                containerPoint = SwingUtilities.convertPoint( glassPane, glassPanePoint, menuBar );
            }

            //TODO: If the event is from a component in a popped-up menu, then the container should
            //TODO: probably be the menu's JPopupMenu, and containerPoint should be adjusted accordingly.
            
            Component component = SwingUtilities.getDeepestComponentAt( container, containerPoint.x, containerPoint.y );
            if ( component != null ) {
                Point componentPoint = SwingUtilities.convertPoint( glassPane, glassPanePoint, component);
                component.dispatchEvent( remapMouseEvent( event, component, componentPoint ) );
            }
        }
        
        /*
         * Redispatches a mouse event to the frame's layered pane, which covers the 
         * entire frame and contains the content pane, help pane, pop-ups, palettes, etc.
         * 
         * problems:
         * - menus don't hilite when you move the mouse across the menubar
         * - menu items don't hilite when you move the mouse down a menu
         */
        private void redispatch2( MouseEvent event ) {
            Component glassPane = _frame.getGlassPane();
            Point glassPanePoint = event.getPoint();
            JLayeredPane layeredPane = _frame.getLayeredPane();
            Point layeredPanePoint = SwingUtilities.convertPoint( glassPane, glassPanePoint, layeredPane );
            Component component = SwingUtilities.getDeepestComponentAt( layeredPane, layeredPanePoint.x, layeredPanePoint.y );
            if ( component != null ) {
                Point componentPoint = SwingUtilities.convertPoint( glassPane, glassPanePoint, component);
                component.dispatchEvent( remapMouseEvent( event, component, componentPoint ) );
            }
        }
        
        /*
         * Redispatches a mouse event to the parent frame.
         * Mouse handling is disabled before the redispatch, and enabled after the redispatch.
         * This effectively sends all MouseEvents through the frame twice, and every other 
         * time they are handled and redispatched so that we can keep track of mouse position.
         * 
         * problems:
         * - other events (eg, ActionEvent) don't get through to components
         * - can't select all menu items from the menubar (eg, Help>About)
         */
        private void redispatch3( MouseEvent event ) {
            setMouseHandlerEnabled( false );
            Component component = event.getComponent();
            Point point = event.getPoint();
            Point framePoint = SwingUtilities.convertPoint( component, point, _frame );
            _frame.dispatchEvent( remapMouseEvent( event, _frame, framePoint ) );
            setMouseHandlerEnabled( true );
        }
        
        /*
         * Remaps an existing MouseEvent to a new component and point.
         * 
         * @param e
         * @param component
         * @param point
         * @return a new MouseEvent
         */
        private MouseEvent remapMouseEvent( MouseEvent e, Component component, Point point ) {
            return new MouseEvent( component, e.getID(), e.getWhen(), e.getModifiers(), 
                    point.x, point.y, e.getClickCount(), e.isPopupTrigger(), e.getButton() );
        }
    }
}