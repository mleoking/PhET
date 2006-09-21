/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

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
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * GunNode
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GunNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Point2D BUTTON_OFFSET = new Point2D.Double( 32, 82 );
    private static final Point2D CABLE_OFFSET = new Point2D.Double( 40, 140 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PImage _onButton, _offButton;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GunNode() {
        super();
        
        // Nodes
        PImage gunNode = PImageFactory.create( HAConstants.IMAGE_GUN );
        _onButton = PImageFactory.create(HAConstants.IMAGE_GUN_ON_BUTTON );
        _offButton = PImageFactory.create( HAConstants.IMAGE_GUN_OFF_BUTTON );
        PImage cableNode = PImageFactory.create( HAConstants.IMAGE_GUN_CONTROL_CABLE );
        
        // Layering
        addChild( cableNode );
        addChild( gunNode );
        addChild( _onButton );
        addChild( _offButton );
        
        // Positioning
        gunNode.setOffset( 0, 0 );
        _onButton.setOffset( BUTTON_OFFSET );
        _offButton.setOffset( BUTTON_OFFSET );
        cableNode.setOffset( CABLE_OFFSET );
        
        // Event handling
        {
            _listenerList = new EventListenerList();

            gunNode.setPickable( false );
            cableNode.setPickable( false );
            
            PBasicInputEventHandler buttonHandler = new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setOn( !isOn() );
                }
            };
            _onButton.addInputEventListener( buttonHandler );
            _offButton.addInputEventListener( buttonHandler );
            _onButton.addInputEventListener( new CursorHandler() );
            _offButton.addInputEventListener( new CursorHandler() );
        }
        
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
