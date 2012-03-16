// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.control;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.mvcexample.MVCExampleApplication;
import edu.colorado.phet.mvcexample.model.CModelElement;
import edu.colorado.phet.mvcexample.model.CModelElement.CModelElementListener;

/**
 * CControlPanel is the control panel for an CModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CControlPanel extends PointerControlPanel implements CModelElementListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = MVCExampleApplication.RESOURCE_LOADER.getLocalizedString( "CControlPanel.title" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private CModelElement _modelElement;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public CControlPanel( CModelElement modelElement ) {
        super( TITLE, modelElement.getColor() );
        
        _modelElement = modelElement;
        _modelElement.addListener( this );
        
        getPositionControl().setValue( _modelElement.getPosition() );
        
        getOrientationControl().setValue( _modelElement.getOrientation() );
        getOrientationControl().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                final double radians = Math.toRadians( getOrientationControl().getValue() );
                _modelElement.setOrientation( radians );
            }
        } );
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _modelElement.removeListener( this );
    }
    
    //----------------------------------------------------------------------------
    // CModelElementListener implementation
    //----------------------------------------------------------------------------
    
    public void positionChanged() {
        getPositionControl().setValue( _modelElement.getPosition() );
    }
    
    public void orientationChanged() {
        final double degrees = Math.toDegrees( _modelElement.getOrientation() );
        getOrientationControl().setValue( degrees );
    }
}
