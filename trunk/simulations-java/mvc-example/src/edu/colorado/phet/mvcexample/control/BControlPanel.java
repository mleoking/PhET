/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.control;

import java.awt.GridBagConstraints;
import java.awt.geom.Point2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.mvcexample.MVCApplication;
import edu.colorado.phet.mvcexample.model.BModelElement;
import edu.colorado.phet.mvcexample.model.BModelElement.BModelElementListener;

/**
 * BControlPanel is the control panel for BModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BControlPanel extends JPanel implements BModelElementListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = MVCApplication.RESOURCE_LOADER.getLocalizedString( "BControlPanel.title" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BModelElement _modelElement;

    private PositionControl _positionControl;
    private OrientationControl _orientationControl;

    private ControlObserver _controlObserver;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BControlPanel( BModelElement modelElement ) {
        super();

        _controlObserver = new ControlObserver();

        _modelElement = modelElement;

        // Title
        JLabel titleLabel = new JLabel( TITLE );
        titleLabel.setFont( new PhetDefaultFont( 14, true /* bold */ ) );

        // Position control (display only)
        _positionControl = new PositionControl();
        _positionControl.setValue( _modelElement.getPositionReference() );

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
        layout.addComponent( _positionControl, row++, column );
        layout.addComponent( _orientationControl, row++, column );
    }
    
    //----------------------------------------------------------------------------
    // BModelElementListener implementation (model changes)
    //----------------------------------------------------------------------------

    public void orientationChanged( double oldOrientation, double newOrientation ) {
        final double degrees = Math.toDegrees( newOrientation );
        _orientationControl.setValue( degrees );
    }

    public void positionChanged( Point2D oldPosition, Point2D newPosition ) {
        _positionControl.setValue( newPosition );
    }

    //----------------------------------------------------------------------------
    // Control changes
    //----------------------------------------------------------------------------

    private class ControlObserver implements ChangeListener {

        public void stateChanged( ChangeEvent e ) {
            Object o = e.getSource();
            if ( o == _orientationControl ) {
                updateModelOrientation();
            }
        }
    }

    /**
     * Updates the model when the orientation control changes.
     */
    private void updateModelOrientation() {
        final double radians = Math.toRadians( _orientationControl.getValue() );
        _modelElement.setOrientation( radians );
    }

}
