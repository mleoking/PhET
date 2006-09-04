/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.movingman.common;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;


/**
 * ZoomControl is a control for horizontal or vertical zooming.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 *          <p/>
 *          <p/>
 *          Copied from Fourier simulation on 9-3-2006.
 */
public class ZoomControl extends GraphicLayerSet {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final Point IN_LOCATION = new Point( 31, 13 );
    private static final Point OUT_LOCATION = new Point( 3, 13 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private int _orientation;
    private PhetImageGraphic _inButton, _inButtonPressed;
    private PhetImageGraphic _outButton, _outButtonPressed;
    private EventListenerList _listenerList;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ZoomControl( Component component, int orientation ) {
        super( component );

        _orientation = orientation;

        PhetImageGraphic background;
        if( orientation == HORIZONTAL ) {
            background = new PhetImageGraphic( component, FourierConstants.ZOOM_BACKGROUND_HORIZONTAL_IMAGE );
        }
        else {
            background = new PhetImageGraphic( component, FourierConstants.ZOOM_BACKGROUND_VERTICAL_IMAGE );
        }
        addGraphic( background );

        _inButton = new PhetImageGraphic( component, FourierConstants.ZOOM_IN_BUTTON_IMAGE );
        _inButtonPressed = new PhetImageGraphic( component, FourierConstants.ZOOM_IN_BUTTON_PRESSED_IMAGE );
        _inButton.setLocation( IN_LOCATION );
        _inButtonPressed.setLocation( IN_LOCATION );
        addGraphic( _inButton );
        addGraphic( _inButtonPressed );

        _outButton = new PhetImageGraphic( component, FourierConstants.ZOOM_OUT_BUTTON_IMAGE );
        _outButtonPressed = new PhetImageGraphic( component, FourierConstants.ZOOM_OUT_BUTTON_PRESSED_IMAGE );
        _outButton.setLocation( OUT_LOCATION );
        _outButtonPressed.setLocation( OUT_LOCATION );
        addGraphic( _outButton );
        addGraphic( _outButtonPressed );

        // Interactivity
        {
            background.setIgnoreMouse( true );

            _inButton.setCursorHand();
            _inButtonPressed.setCursorHand();
            _outButton.setCursorHand();
            _outButtonPressed.setCursorHand();

            EventListener listener = new EventListener();
            _inButton.addMouseInputListener( listener );
            _inButtonPressed.addMouseInputListener( listener );
            _outButton.addMouseInputListener( listener );
            _outButtonPressed.addMouseInputListener( listener );
        }

        // Initial visibility
        _inButtonPressed.setVisible( false );
        _outButtonPressed.setVisible( false );

        _listenerList = new EventListenerList();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setZoomInEnabled( boolean enabled ) {
        _inButton.setVisible( enabled );
    }

    public void setZoomOutEnabled( boolean enabled ) {
        _outButton.setVisible( enabled );
    }

    /**
     * ZoomEvent indicates that a zoom action has occurred.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    public class ZoomEvent extends EventObject {

        //----------------------------------------------------------------------------
        // Class data
        //----------------------------------------------------------------------------

        // The zoom types
        public static final int VERTICAL_ZOOM_IN = 0;
        public static final int VERTICAL_ZOOM_OUT = 1;
        public static final int HORIZONTAL_ZOOM_IN = 2;
        public static final int HORIZONTAL_ZOOM_OUT = 3;

        //----------------------------------------------------------------------------
        // Instance data
        //----------------------------------------------------------------------------

        private int _zoomType;

        //----------------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------------

        /**
         * Sole constructor.
         *
         * @param source
         * @param zoomType
         */
        public ZoomEvent( Object source, int zoomType ) {
            super( source );
            assert( isValidZoomType( zoomType ) );
            _zoomType = zoomType;
        }

        //----------------------------------------------------------------------------
        // Accessors
        //----------------------------------------------------------------------------

        /**
         * Gets the zoom type associated with the event.
         *
         * @return the zoom type
         */
        public int getZoomType() {
            return _zoomType;
        }

        /**
         * Validates a zoom type.
         *
         * @param zoomType
         * @return true or false
         */
        public boolean isValidZoomType( int zoomType ) {
            return ( zoomType == VERTICAL_ZOOM_IN ||
                     zoomType == VERTICAL_ZOOM_OUT ||
                     zoomType == HORIZONTAL_ZOOM_IN ||
                     zoomType == HORIZONTAL_ZOOM_OUT );
        }
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /**
     * ZoomListener is the listener interface for zoom events.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    public interface ZoomListener extends java.util.EventListener {

        /**
         * Invoked when a zoom occurs.
         *
         * @param event
         */
        public void zoomPerformed( ZoomEvent event );
    }

    public void addZoomListener( ZoomListener listener ) {
        _listenerList.add( ZoomListener.class, listener );
    }

    public void removeZoomListener( ZoomListener listener ) {
        _listenerList.remove( ZoomListener.class, listener );
    }

    public void removeAllZoomListeners() {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ZoomListener.class ) {
                _listenerList.remove( ZoomListener.class, (ZoomListener)listeners[i + 1] );
            }
        }
    }

    private void fireZoomEvent( int zoomType ) {
        ZoomEvent event = new ZoomEvent( this, zoomType );
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ZoomListener.class ) {
                ( (ZoomListener)listeners[i + 1] ).zoomPerformed( event );
            }
        }
    }

    private class EventListener extends MouseInputAdapter {

        private boolean _inPressed, _outPressed;

        public EventListener() {
            super();
        }

        public void mousePressed( MouseEvent event ) {
            if( _inButton.getBounds().contains( event.getPoint() ) ) {
                if( ! _inButtonPressed.isVisible() ) {
                    _inButtonPressed.setVisible( true );
                    _inPressed = true;
                }
            }
            else if( _outButton.getBounds().contains( event.getPoint() ) ) {
                if( ! _outButtonPressed.isVisible() ) {
                    _outButtonPressed.setVisible( true );
                    _outPressed = true;
                }
            }
        }

        public void mouseReleased( MouseEvent event ) {
            if( _inPressed ) {
                _inButtonPressed.setVisible( false );
                _inPressed = false;
                if( _inButtonPressed.getBounds().contains( event.getPoint() ) ) {
                    // Set the wait cursor
                    getComponent().setCursor( FourierConstants.WAIT_CURSOR );
                    // Handle the event
                    int zoomType;
                    if( _orientation == HORIZONTAL ) {
                        fireZoomEvent( ZoomEvent.HORIZONTAL_ZOOM_IN );
                    }
                    else {
                        fireZoomEvent( ZoomEvent.VERTICAL_ZOOM_IN );
                    }
                    // Restore the cursor
                    getComponent().setCursor( FourierConstants.DEFAULT_CURSOR );
                }
            }
            else if( _outPressed ) {
                _outButtonPressed.setVisible( false );
                _outPressed = false;
                if( _outButtonPressed.getBounds().contains( event.getPoint() ) ) {
                    // Set the wait cursor
                    getComponent().setCursor( FourierConstants.WAIT_CURSOR );
                    // Handle the event
                    if( _orientation == HORIZONTAL ) {
                        fireZoomEvent( ZoomEvent.HORIZONTAL_ZOOM_OUT );
                    }
                    else {
                        fireZoomEvent( ZoomEvent.VERTICAL_ZOOM_OUT );
                    }
                    // Restore the cursor
                    getComponent().setCursor( FourierConstants.DEFAULT_CURSOR );
                }
            }
        }
    }

    /**
     * FourierConstants constains various constants.
     * Modify these at your peril ;-)
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    public static class FourierConstants {

        /* Not intended for instantiation. */
        private FourierConstants() {
        }

        //----------------------------------------------------------------------------
        // Debugging switches
        //----------------------------------------------------------------------------

        public static final boolean ANIMATION_ENABLED = true;

        //----------------------------------------------------------------------------
        // Application
        //----------------------------------------------------------------------------

        public static final int APP_FRAME_WIDTH = 1024;
        public static final int APP_FRAME_HEIGHT = 768;

        //----------------------------------------------------------------------------
        // Localization
        //----------------------------------------------------------------------------

        public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/FourierStrings";

        //----------------------------------------------------------------------------
        // Clock
        //----------------------------------------------------------------------------

        public static final double CLOCK_STEP = 1;
        private static final int CLOCK_FRAME_RATE = 25;  // frames per second
        public static final int CLOCK_DELAY = ( 1000 / FourierConstants.CLOCK_FRAME_RATE ); // milliseconds
        public static final boolean CLOCK_TIME_STEP_IS_CONSTANT = true;
        public static final boolean CLOCK_ENABLE_CONTROLS = true;

        //----------------------------------------------------------------------------
        // Fonts
        //----------------------------------------------------------------------------

        public static final String FONT_NAME = "Lucida Sans";

        //----------------------------------------------------------------------------
        // Harmonics
        //----------------------------------------------------------------------------

        public static final int MIN_HARMONICS = 1;
        public static final int MAX_HARMONICS = 11;
        public static final double MAX_HARMONIC_AMPLITUDE = ( 4 / Math.PI );

        /**
         * Arbitrary value for the length (L) of the fundamental harmonic
         */
        public static final double L = 1.0;

        //----------------------------------------------------------------------------
        // Animation
        //----------------------------------------------------------------------------

        public static final double ANIMATION_STEPS_PER_CYCLE = 50;

        //----------------------------------------------------------------------------
        // Charts
        //----------------------------------------------------------------------------

        public static final double AUTOSCALE_PERCENTAGE = 1.05;

        //----------------------------------------------------------------------------
        // Cursors
        //----------------------------------------------------------------------------

        public static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
        public static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );

        //----------------------------------------------------------------------------
        // Images
        //----------------------------------------------------------------------------

        private static final String IMAGES_DIRECTORY = "images/zoom/";
        public static final String ZOOM_BACKGROUND_HORIZONTAL_IMAGE = IMAGES_DIRECTORY + "zoomBackgroundHorizontal.png";
        public static final String ZOOM_BACKGROUND_VERTICAL_IMAGE = IMAGES_DIRECTORY + "zoomBackgroundVertical.png";
        public static final String ZOOM_IN_BUTTON_IMAGE = IMAGES_DIRECTORY + "zoomInButton.png";
        public static final String ZOOM_IN_BUTTON_PRESSED_IMAGE = IMAGES_DIRECTORY + "zoomInButtonPressed.png";
        public static final String ZOOM_OUT_BUTTON_IMAGE = IMAGES_DIRECTORY + "zoomOutButton.png";
        public static final String ZOOM_OUT_BUTTON_PRESSED_IMAGE = IMAGES_DIRECTORY + "zoomOutButtonPressed.png";
    }

    public static void main( String[] args ) {
        ApparatusPanel ap = new ApparatusPanel();
        ZoomControl zc = new ZoomControl( ap, ZoomControl.HORIZONTAL );
        ZoomControl zc2 = new ZoomControl( ap, ZoomControl.VERTICAL );
        zc2.setLocation( 300, 300 );
        ap.addGraphic( zc );
        ap.addGraphic( zc2 );
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( ap );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }

}
