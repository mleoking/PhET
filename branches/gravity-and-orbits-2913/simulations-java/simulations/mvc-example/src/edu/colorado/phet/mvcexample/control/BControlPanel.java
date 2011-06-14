// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.control;

import java.awt.geom.Point2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.mvcexample.MVCExampleApplication;
import edu.colorado.phet.mvcexample.model.BModelElement;
import edu.colorado.phet.mvcexample.model.BModelElement.BModelElementListener;

/**
 * BControlPanel is the control panel for BModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BControlPanel extends PointerControlPanel implements BModelElementListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = MVCExampleApplication.RESOURCE_LOADER.getLocalizedString( "BControlPanel.title" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BModelElement _modelElement;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BControlPanel( BModelElement modelElement ) {
        super( TITLE, modelElement.getColor() );
        
        _modelElement = modelElement;
        
        getPositionControl().setValue( _modelElement.getPosition() );
        
        getOrientationControl().setValue( _modelElement.getOrientation() );
        getOrientationControl().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                final double radians = Math.toRadians( getOrientationControl().getValue() );
                _modelElement.setOrientation( radians );
            }
        });
    }
    
    //----------------------------------------------------------------------------
    // BModelElementListener implementation
    //----------------------------------------------------------------------------

    public void orientationChanged( double oldOrientation, double newOrientation ) {
        final double degrees = Math.toDegrees( newOrientation );
        getOrientationControl().setValue( degrees );
    }

    public void positionChanged( Point2D oldPosition, Point2D newPosition ) {
        getPositionControl().setValue( newPosition );
    }
}
