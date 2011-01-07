// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.control;

import java.util.Observable;
import java.util.Observer;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.mvcexample.MVCExampleApplication;
import edu.colorado.phet.mvcexample.model.AModelElement;

/**
 * AControlPanel is the control panel for an AModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AControlPanel extends PointerControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = MVCExampleApplication.RESOURCE_LOADER.getLocalizedString( "AControlPanel.title" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AModelElement _modelElement;
    private ModelObserver _modelObserver;
    private ControlObserver _controlObserver;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AControlPanel( AModelElement modelElement ) {
        super( TITLE, modelElement.getColor() );
        
        _modelObserver = new ModelObserver();
        _controlObserver = new ControlObserver();
        
        _modelElement = modelElement;
        _modelElement.addObserver( _modelObserver );
        
        getPositionControl().setValue( _modelElement.getPosition() );
        
        getOrientationControl().setValue( _modelElement.getOrientation() );
        getOrientationControl().addChangeListener( _controlObserver );
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _modelElement.deleteObserver( _modelObserver );
    }
    
    //----------------------------------------------------------------------------
    // Model changes
    //----------------------------------------------------------------------------
    
    private class ModelObserver implements Observer {

        public void update( Observable o, Object arg ) {
            if ( arg == AModelElement.PROPERTY_POSITION ) {
                modelPositionChanged();
            }
            else if ( arg == AModelElement.PROPERTY_ORIENTATION ) {
                modelOrientationChanged();
            }
        }
    }
    
    public void modelPositionChanged() {
        getPositionControl().setValue( _modelElement.getPosition() );
    }
    
    public void modelOrientationChanged() {
        final double degrees = Math.toDegrees( _modelElement.getOrientation() );
        getOrientationControl().removeChangeListener( _controlObserver );
        getOrientationControl().setValue( degrees );
        getOrientationControl().addChangeListener( _controlObserver );
    }

    //----------------------------------------------------------------------------
    // Control changes
    //----------------------------------------------------------------------------
    
    private class ControlObserver implements ChangeListener {
        public void stateChanged( ChangeEvent e ) {
            Object o = e.getSource();
            if ( o == getOrientationControl() ) {
                controlOrientationChanged();
            }
        }
    }
    
    private void controlOrientationChanged() {
        final double radians = Math.toRadians( getOrientationControl().getValue() );
        _modelElement.deleteObserver( _modelObserver );
        _modelElement.setOrientation( radians );
        _modelElement.addObserver( _modelObserver );
    }
}
