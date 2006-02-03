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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.*;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PInputEventListener;


/**
 * PGlassPane is a Piccolo-based glass pane that does not intercept any events.
 * The cursor on the glass pane is set by adding a MouseListener (referred to
 * herein as the "cursor listener" on all JComponents in the hierarchy.
 * A Timer is used to periodically ensure that all JComponents have the 
 * cursor listener.
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
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int CURSOR_LISTENER_UPDATE_FREQUENCY = 1000; // milliseconds
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JFrame _parentFrame;  // we'll be serving as this frame's glass pane
    private MouseListener _cursorListener; // handles cursor changes
    
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
        
        // Cursor control
        _cursorListener = new MouseAdapter() {
            public void mouseEntered( MouseEvent event ) {
                setCursor( event.getComponent().getCursor() );
            }
            public void mouseExited( MouseEvent e ) {
                setCursor( Cursor.getDefaultCursor() );
            }
        };
        
        // Periodically make sure that all JComponents have cursorListener
        Timer timer = new Timer( CURSOR_LISTENER_UPDATE_FREQUENCY, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                installCursorListener( _parentFrame.getLayeredPane() );
            }
        } );
        timer.start();
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
    
    //----------------------------------------------------------------------------
    // Overrides 
    //----------------------------------------------------------------------------
    
    /*
     * If we don't have listeners for events, then they are
     * automatically directed to the content pane, menu bar, etc.
     * So we override the corresponding "addListener" methods with stubs.
     */
    
    public synchronized void addFocusListener( FocusListener l ) {}

    public synchronized void addInputEventListener( PInputEventListener l ) {}

    public synchronized void addInputMethodListener( InputMethodListener l ) {}

    public synchronized void addKeyListener( KeyListener l ) {}

    public synchronized void addMouseListener( MouseListener l ) {}

    public synchronized void addMouseMotionListener( MouseMotionListener l ) {}

    public synchronized void addMouseWheelListener( MouseWheelListener l ) {}

    //----------------------------------------------------------------------------
    // MouseListener for cursor control 
    //----------------------------------------------------------------------------
    
    /**
     * Install the cursor listener on component and all of its descendents.
     * 
     * @param component
     */
    private void installCursorListener( JComponent component ) {
        if ( component != null && !( component instanceof PGlassPane ) ) {
            if ( !Arrays.asList( component.getMouseListeners() ).contains( _cursorListener ) ) {
                component.addMouseListener( _cursorListener );
            }
            for ( int i = 0; i < component.getComponentCount(); i++ ) {
                if ( component.getComponent( i ) instanceof JComponent ) {
                    installCursorListener( (JComponent) component.getComponent( i ) );
                }
            }
        }
    }
}