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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.fourier.FourierConfig;


/**
 * ZoomControl
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ZoomControl extends GraphicLayerSet {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int ACTION_ID_ZOOM_IN = 0;
    public static final int ACTION_ID_ZOOM_OUT = 1;
    
    public static final String ACTION_COMMAND_ZOOM_IN = "ZOOM_IN";
    public static final String ACTION_COMMAND_ZOOM_OUT = "ZOOM_OUT";
    
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    
    private static final Point IN_LOCATION = new Point( 3, 13 );
    private static final Point OUT_LOCATION = new Point( 31, 13 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetImageGraphic _inButton, _inButtonPressed, _inButtonDisabled;
    private PhetImageGraphic _outButton, _outButtonPressed, _outButtonDisabled;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ZoomControl( Component component, int direction ) {
        
        PhetImageGraphic background;
        if ( direction == HORIZONTAL ) {
            background = new PhetImageGraphic( component, FourierConfig.ZOOM_BACKGROUND_HORIZONTAL_IMAGE );
        }
        else {
            background = new PhetImageGraphic( component, FourierConfig.ZOOM_BACKGROUND_VERTICAL_IMAGE );
        }
        addGraphic( background );

        _inButton = new PhetImageGraphic( component, FourierConfig.ZOOM_IN_BUTTON_IMAGE );
        _inButtonPressed = new PhetImageGraphic( component, FourierConfig.ZOOM_IN_BUTTON_PRESSED_IMAGE );
        _inButtonDisabled = new PhetImageGraphic( component, FourierConfig.ZOOM_IN_BUTTON_DISABLED_IMAGE );
        _inButton.setLocation( IN_LOCATION );
        _inButtonPressed.setLocation( IN_LOCATION );
        _inButtonDisabled.setLocation( IN_LOCATION );
        addGraphic( _inButton );
        addGraphic( _inButtonPressed );
        addGraphic( _inButtonDisabled );
        
        _outButton = new PhetImageGraphic( component, FourierConfig.ZOOM_OUT_BUTTON_IMAGE );
        _outButtonPressed = new PhetImageGraphic( component, FourierConfig.ZOOM_OUT_BUTTON_PRESSED_IMAGE );
        _outButtonDisabled = new PhetImageGraphic( component, FourierConfig.ZOOM_OUT_BUTTON_DISABLED_IMAGE );
        _outButton.setLocation( OUT_LOCATION );
        _outButtonPressed.setLocation( OUT_LOCATION );
        _outButtonDisabled.setLocation( OUT_LOCATION );
        addGraphic( _outButton );
        addGraphic( _outButtonPressed );
        addGraphic( _outButtonDisabled );
        
        // Interactivity
        background.setIgnoreMouse( true );
        _inButtonDisabled.setIgnoreMouse( true );
        _outButtonDisabled.setIgnoreMouse( true );
        EventListener listener = new EventListener();
        _inButton.addMouseInputListener( listener );
        _inButtonPressed.addMouseInputListener( listener );
        _outButton.addMouseInputListener( listener );
        _outButtonPressed.addMouseInputListener( listener );
        
        // Initial visibility
        _inButtonPressed.setVisible( false );
        _inButtonDisabled.setVisible( false );
        _outButtonPressed.setVisible( false );
        _outButtonDisabled.setVisible( false );
        
        _listenerList = new EventListenerList();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setZoomInEnabled( boolean enabled ) {
        _inButtonDisabled.setVisible( !enabled );
        _inButton.setVisible( enabled );
    }
    
    public void setZoomOutEnabled( boolean enabled ) {
        _outButtonDisabled.setVisible( !enabled );
        _outButton.setVisible( enabled );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    public void addActionListener( ActionListener listener ) {
        _listenerList.add( ActionListener.class, listener );
    }
  
    public void removeActionListener( ActionListener listener ) {
        _listenerList.remove( ActionListener.class, listener );
    }
    
    private void fireActionEvent( ActionEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == ActionListener.class ) {
                ((ActionListener) listeners[ i + 1 ] ).actionPerformed( event );
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
                if ( ! _inButtonDisabled.isVisible() ) {
                    _inButtonPressed.setVisible( true );
                    _inPressed = true;
                }
            }
            else if ( _outButton.getBounds().contains( event.getPoint() ) ) {
                if ( ! _outButtonDisabled.isVisible() ) {
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
                    fireActionEvent( new ActionEvent( this, ACTION_ID_ZOOM_IN, ACTION_COMMAND_ZOOM_IN ) );
                }
            }
            else if ( _outPressed ) {
                _outButtonPressed.setVisible( false );
                _outPressed = false;
                if ( _outButtonPressed.getBounds().contains( event.getPoint() ) ) {
                    fireActionEvent( new ActionEvent( this, ACTION_ID_ZOOM_OUT, ACTION_COMMAND_ZOOM_OUT ) );
                }    
            }
        }
    }
}
