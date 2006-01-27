/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.help;

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
    private MouseManager _mouseManager; // manages mouse events & tracks mouse position

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a PGlassPane and makes it the glass pane of the 
     * specified parent frame.
     * 
     * @param parentFrame the parent frame
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

        // Mouse location tracking and event redispatching...
        _mouseManager = new MouseManager( parentFrame );
        super.addMouseMotionListener( _mouseManager );
        super.addMouseListener( _mouseManager );
    }
    
    //----------------------------------------------------------------------------
    // Accessors 
    //----------------------------------------------------------------------------
    
    protected JFrame getParentFrame() {
        return _parentFrame;
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
        Point glassPanePoint = _mouseManager.getMouseLocation();
        Component contentPane = _parentFrame.getContentPane();
        Point contentPanePoint = SwingUtilities.convertPoint( glassPane, glassPanePoint, contentPane );
        Component component = SwingUtilities.getDeepestComponentAt( contentPane, contentPanePoint.x, contentPanePoint.y );
        if ( component != null ) {
            cursor = component.getCursor();
        }
        return cursor;
    }
    
    /*
     * If we don't have listeners for events, then they are
     * automatically directed to the content pane, menu bar, etc.
     * So we override the corresponding "addListener" methods with stubs.
     * <p>
     * Note that our constructor added some mouse listeners
     * by calling super.addMouse*Listener methods. We want 
     * to override the addMouse*Listener methods so that no one
     * except us can add one of these listener.
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
    private class MouseManager implements MouseMotionListener, MouseListener {

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
        public MouseManager( JFrame frame ) {
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
         * Redispatches a MouseEvent to the proper component.
         * The glass pane covers the content pane and the menu bar.
         * So we look for the component at the mouse's absolute location,
         * checking the content pane first and then the menu bar.
         * 
         * @param mouseEvent
         */
        private void redispatch( MouseEvent event ) {
            
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
         * Remaps an existing MouseEvent to a new component and point.
         * 
         * @param e
         * @param component
         * @param point
         * @return a new MouseEvent
         */
        private MouseEvent remapMouseEvent( MouseEvent e, Component component, Point point ) {
            return new MouseEvent( component, e.getID(), e.getWhen(), e.getModifiers(), point.x, point.y, e.getClickCount(), e.isPopupTrigger() );
        }
    }
}