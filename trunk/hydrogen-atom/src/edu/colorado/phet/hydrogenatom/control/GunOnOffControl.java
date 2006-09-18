/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * GunOnOffControl
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GunOnOffControl extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Point2D BUTTON_OFFSET = new Point2D.Double( 73, 82 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PImage _onButton, _offButton;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GunOnOffControl() {
        super();
        
        // Images
        BufferedImage gunImage = null;
        BufferedImage onButtonImage = null;
        BufferedImage offButtonImage = null;
        try {
            gunImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_GUN );
            onButtonImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_GUN_ON_BUTTON );
            offButtonImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_GUN_OFF_BUTTON );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        
        // Gun representation
        PImage gunNode = new PImage( gunImage );
        addChild( gunNode );
        
        // On button
        _onButton = new PImage( onButtonImage );
        addChild( _onButton );
        _onButton.setOffset( BUTTON_OFFSET );
        
        // Off button
        _offButton = new PImage( offButtonImage );
        addChild( _offButton );
        _offButton.setOffset( BUTTON_OFFSET );
       
        // Event handling
        _listenerList = new EventListenerList();
        
        PBasicInputEventHandler buttonHandler = new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent event) {
                setOn( !isOn() );
            }
        };
        _onButton.addInputEventListener( buttonHandler );
        _offButton.addInputEventListener( buttonHandler );
        _onButton.addInputEventListener( new CursorHandler() );
        _offButton.addInputEventListener( new CursorHandler() );
        
        // Default state
        setOn( false );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public void setOn( boolean on ) {
        _onButton.setVisible( on );
        _onButton.setPickable( _onButton.getVisible() );
        _offButton.setVisible( !on );
        _offButton.setPickable( _offButton.getVisible() );
        fireChangeEvent( new ChangeEvent( this ) );
    }
    
    public boolean isOn() {
        return _onButton.getVisible();
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /**
     * Adds a ChangeListener.
     *
     * @param listener the listener
     */
    public void addChangeListener( ChangeListener listener ) {
        _listenerList.add( ChangeListener.class, listener );
    }

    /**
     * Removes a ChangeListener.
     *
     * @param listener the listener
     */
    public void removeChangeListener( ChangeListener listener ) {
        _listenerList.remove( ChangeListener.class, listener );
    }

    /**
     * Fires a ChangeEvent.
     *
     * @param event the event
     */
    private void fireChangeEvent( ChangeEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener)listeners[i + 1] ).stateChanged( event );
            }
        }
    }
}
