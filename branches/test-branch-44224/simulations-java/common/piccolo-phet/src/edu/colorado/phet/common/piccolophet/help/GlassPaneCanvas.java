/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.piccolophet.help;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PInputEventListener;


/**
 * GlassPaneCanvas is a Piccolo-based glass pane that does not intercept any events.
 * The cursor on the glass pane is set by adding a MouseListener (referred to
 * herein as the "cursor listener" on all JComponents in the hierarchy.
 * A Timer is used to periodically ensure that all JComponents have the
 * cursor listener.
 * <p/>
 * Sample usage:
 * <code>
 * JFrame frame = PhetApplication.instance().getPhetFrame();
 * GlassPaneCanvas glassPane = new GlassPaneCanvas( frame );
 * frame.setGlassPane( glassPane );
 * glassPane.setVisible( true );
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GlassPaneCanvas extends PCanvas {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final int CURSOR_LISTENER_UPDATE_FREQUENCY = 1000; // milliseconds

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private JFrame _parentFrame;  // we'll be serving as this frame's glass pane
    private Timer _timer; // periodically adds listeners to components in Swing hierarchy
    private MouseListener _componentCursorListener; // handles cursor changes for JComponents
    private MouseMotionListener _canvasCursorListener; // handles cursor changes for nodes on PCanvas
    private ArrayList _componentList; // list of JComponent that have _componentCursorListener attached
    private ArrayList _canvasList; // list of PCanvas that have _canvasCursorListener attached

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructs a GlassPaneCanvas.
     *
     * @param parentFrame this GlassPaneCanvas is intended to be the glass pane for parentFrame
     */
    public GlassPaneCanvas( JFrame parentFrame ) {
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

        // JComponent cursor synchronization
        _componentCursorListener = new MouseAdapter() {
            public void mouseEntered( MouseEvent event ) {
                setCursor( event.getComponent().getCursor() );
            }

            public void mouseExited( MouseEvent e ) {
                //XXX using the default cursor may be incorrect!
                setCursor( Cursor.getDefaultCursor() );
            }
        };

        // PCanvas cursor synchronization
        _canvasCursorListener = new MouseMotionListener() {
            public void mouseMoved( MouseEvent event ) {
                setCursor( event.getComponent().getCursor() );
            }

            public void mouseDragged( MouseEvent event ) {
                setCursor( event.getComponent().getCursor() );
            }
        };

        // Periodically make sure that all components have cursor-related listeners installed...
        _timer = new Timer( CURSOR_LISTENER_UPDATE_FREQUENCY, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                addCursorListeners( _parentFrame.getLayeredPane() );
            }
        } );
        _timer.setInitialDelay( 0 );

        _componentList = new ArrayList();
        _canvasList = new ArrayList();
        setOpaque( false );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the parent frame.
     * This GlassPaneCanvas is intended to serve as the glass pane for the parent frame.
     *
     * @return the parent frame
     */
    protected JFrame getParentFrame() {
        return _parentFrame;
    }

    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------

    /**
     * When the glass pane is visible, start the timer that installs
     * cursor-related listeners.  When the glass pane is invisible,
     * stop the timer and remove all cursor-related listeners, so that
     * we're not doing any unnecessary work.
     *
     * @param visible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            _timer.start();
        }
        else {
            _timer.stop();
            removeCursorListeners();
        }
    }

    /*
     * If we don't have listeners for events, then they are
     * automatically directed to the content pane, menu bar, etc.
     * So we override the corresponding "addListener" methods with stubs.
     */

    public synchronized void addFocusListener( FocusListener l ) {
    }

    public synchronized void addInputEventListener( PInputEventListener l ) {
    }

    public synchronized void addInputMethodListener( InputMethodListener l ) {
    }

    public synchronized void addKeyListener( KeyListener l ) {
    }

    public synchronized void addMouseListener( MouseListener l ) {
    }

    public synchronized void addMouseMotionListener( MouseMotionListener l ) {
    }

    public synchronized void addMouseWheelListener( MouseWheelListener l ) {
    }

    //----------------------------------------------------------------------------
    // MouseListener for cursor control
    //----------------------------------------------------------------------------

    /*
     * Install the listeners on component and all of its descendents.
     * These listeners handle synchronizing the glass pane's cursor
     * with the components that are beneath it in the layered pane.
     * <p>
     * Note that we keep lists of the components that we've added
     * listeners to.  Since the Swing hierarchy may change, this is the
     * only way that we can reliably remove all the listeners that
     * we've added in removeCursorListeners.
     *
     * @param component
     */

    private void addCursorListeners( JComponent component ) {
        if ( component != null ) {

            // All components get a mouse listener.
            if ( !_componentList.contains( _componentCursorListener ) ) {
                component.addMouseListener( _componentCursorListener );
                _componentList.add( component );
            }

            // PhetPCanvas gets an additional mouse lister.
            if ( component instanceof PCanvas && !_canvasList.contains( component ) ) {
                component.addMouseMotionListener( _canvasCursorListener );
                _canvasList.add( component );
            }

            // Recursively process all child components...
            int numberOfChildren = component.getComponentCount();
            for ( int i = 0; i < numberOfChildren; i++ ) {
                Component child = component.getComponent( i );
                if ( child instanceof JComponent ) {
                    addCursorListeners( (JComponent) child );
                }
                else {
                    // ignore other types of Components
                }
            }
        }
    }

    /*
     * Removes all listeners that were added via addCursorListeners.
     *
     * @param component
     */
    private void removeCursorListeners() {
        Iterator i = _componentList.iterator();
        while ( i.hasNext() ) {
            Component component = (Component) i.next();
            component.removeMouseListener( _componentCursorListener );
        }
        Iterator j = _canvasList.iterator();
        while ( j.hasNext() ) {
            Component component = (Component) j.next();
            component.removeMouseMotionListener( _canvasCursorListener );
        }
        _componentList.clear();
        _canvasList.clear();
    }
}