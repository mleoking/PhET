/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.control;

import java.awt.GridBagConstraints;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.mvcexample.MVCApplication;
import edu.colorado.phet.mvcexample.model.AModelElement;

/**
 * AControlPanel is the control panel for an AModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = MVCApplication.RESOURCE_LOADER.getLocalizedString( "AControlPanel.title" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AModelElement _modelElement;
    
    private PositionControl _positionDisplay;
    private OrientationControl _orientationControl;
    
    private ModelObserver _modelObserver;
    private ControlObserver _controlObserver;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AControlPanel( AModelElement modelElement ) {
        super();
        
        _modelObserver = new ModelObserver();
        _controlObserver = new ControlObserver();
        
        _modelElement = modelElement;
        _modelElement.addObserver( _modelObserver );
        
        // Title
        JLabel titleLabel = new JLabel( TITLE );
        titleLabel.setFont( new PhetDefaultFont( 14, true /* bold */ ) );
        
        // Position control (display only)
        _positionDisplay = new PositionControl();
        _positionDisplay.setValue( _modelElement.getPositionReference() );
        
        // Orientation control
        _orientationControl = new OrientationControl();
        _orientationControl.setValue( _modelElement.getOrientation() );
        _orientationControl.addChangeListener( _controlObserver );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        int row = 0;
        int column = 0;
        layout.addComponent( titleLabel, row++, column );
        layout.addComponent( _positionDisplay, row++, column );
        layout.addComponent( _orientationControl, row++, column );
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
            if ( o == _modelElement ) {
                if ( arg == AModelElement.PROPERTY_POSITION ) {
                    modelPositionChanged();
                }
                else if ( arg == AModelElement.PROPERTY_ORIENTATION ) {
                    modelOrientationChanged();
                }
            }
        }
    }
    
    public void modelPositionChanged() {
        _positionDisplay.setValue( _modelElement.getPositionReference() );
    }
    
    public void modelOrientationChanged() {
        final double degrees = Math.toDegrees( _modelElement.getOrientation() );
        _orientationControl.removeChangeListener( _controlObserver );
        _orientationControl.setValue( degrees );
        _orientationControl.addChangeListener( _controlObserver );
    }

    //----------------------------------------------------------------------------
    // Control changes
    //----------------------------------------------------------------------------
    
    private class ControlObserver implements ChangeListener {
        public void stateChanged( ChangeEvent e ) {
            Object o = e.getSource();
            if ( o == _orientationControl ) {
                controlOrientationChanged();
            }
        }
    }
    
    private void controlOrientationChanged() {
        final double radians = Math.toRadians( _orientationControl.getValue() );
        _modelElement.deleteObserver( _modelObserver );
        _modelElement.setOrientation( radians );
        _modelElement.addObserver( _modelObserver );
    }
}
