/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.event.ZoomEvent;
import edu.colorado.phet.fourier.event.ZoomListener;


/**
 * ZoomControl is a control for horizontal or vertical zooming.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
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
        if ( orientation == HORIZONTAL ) {
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
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    public void addZoomListener( ZoomListener listener ) {
        _listenerList.add( ZoomListener.class, listener );
    }
  
    public void removeZoomListener( ZoomListener listener ) {
        _listenerList.remove( ZoomListener.class, listener );
    }
    
    public void removeAllZoomListeners() {
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == ZoomListener.class ) {
                _listenerList.remove( ZoomListener.class, (ZoomListener) listeners[ i + 1 ] );
            }
        }
    }
    
    private void fireZoomEvent( int zoomType ) {
        ZoomEvent event = new ZoomEvent( this, zoomType );
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == ZoomListener.class ) {
                ((ZoomListener) listeners[ i + 1 ] ).zoomPerformed( event );
            }
        }
    }
    
    private class EventListener extends MouseInputAdapter {
        
        private boolean _inPressed, _outPressed;
        
        public EventListener() {
            super();
        }
    
        public void mousePressed( MouseEvent event ) {
            if ( _inButton.getBounds().contains( event.getPoint() ) ) {
                if ( ! _inButtonPressed.isVisible() ) {
                    _inButtonPressed.setVisible( true );
                    _inPressed = true;
                }
            }
            else if ( _outButton.getBounds().contains( event.getPoint() ) ) {
                if ( ! _outButtonPressed.isVisible() ) {
                    _outButtonPressed.setVisible( true );
                    _outPressed = true;
                }    
            }
        }
        
        public void mouseReleased( MouseEvent event ) {
            if ( _inPressed ) {
                _inButtonPressed.setVisible( false );
                _inPressed = false;
                if ( _inButtonPressed.getBounds().contains( event.getPoint() ) ) {
                    // Set the wait cursor
                    getComponent().setCursor( FourierConfig.WAIT_CURSOR );
                    // Handle the event
                    int zoomType;
                    if ( _orientation == HORIZONTAL ) {
                        fireZoomEvent( ZoomEvent.HORIZONTAL_ZOOM_IN );
                    }
                    else {
                        fireZoomEvent( ZoomEvent.VERTICAL_ZOOM_IN );
                    }
                    // Restore the cursor
                    getComponent().setCursor( FourierConfig.DEFAULT_CURSOR );
                }
            }
            else if ( _outPressed ) {
                _outButtonPressed.setVisible( false );
                _outPressed = false;
                if ( _outButtonPressed.getBounds().contains( event.getPoint() ) ) {
                    // Set the wait cursor
                    getComponent().setCursor( FourierConfig.WAIT_CURSOR );
                    // Handle the event
                    if ( _orientation == HORIZONTAL ) {
                        fireZoomEvent( ZoomEvent.HORIZONTAL_ZOOM_OUT );
                    }
                    else {
                        fireZoomEvent( ZoomEvent.VERTICAL_ZOOM_OUT );
                    }
                    // Restore the cursor
                    getComponent().setCursor( FourierConfig.DEFAULT_CURSOR );
                }    
            }
        }
    }
}
