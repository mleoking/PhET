/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.glaciers.model.ExampleModelElement;

/**
 * ExampleControlPanel is the control panel for an ExampleModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModelElementControlPanel extends JPanel implements Observer {

    private ExampleModelElement _exampleModelElement;
    
    private JLabel _positionDisplay;
    private LinearValueControl _orientationControl;
    
    public ExampleModelElementControlPanel( Font titleFont, Font controlFont, ExampleModelElement exampleModelElement ) {
        super();
        
        _exampleModelElement = exampleModelElement;
        _exampleModelElement.addObserver( this );
        
        // Title
        JLabel titleLabel = new JLabel( "Example Model Element" );
        titleLabel.setFont( titleFont );
        
        // Position display
        _positionDisplay = new JLabel();
        _positionDisplay.setFont( controlFont );
        updatePositionDisplay();
        
        // Orientation control
        double value = _exampleModelElement.getOrientation();
        double min = 0;
        double max = 360;
        String label = "orientation:";
        String valuePattern = "##0";
        String units = "degrees";
        _orientationControl = new LinearValueControl( min, max, label, valuePattern, units );
        _orientationControl.setValue( value );
        _orientationControl.setTextFieldEditable( true );
        _orientationControl.setFont( controlFont );
        _orientationControl.setUpDownArrowDelta( 1 );
        _orientationControl.setTickPattern( "0" );
        _orientationControl.setMajorTickSpacing( 90 );
        _orientationControl.setMinorTickSpacing( 45 );
        _orientationControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleOrientationControlChange();
            }
        } );
        
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
     * Updates the model when the orientation control changes.
     */
    private void handleOrientationControlChange() {
        final double radians = Math.toRadians( _orientationControl.getValue() );
        _exampleModelElement.deleteObserver( this );
        _exampleModelElement.setOrientation( radians );
        _exampleModelElement.addObserver( this );
    }
    
    /**
     * Updates the controls when the model changes.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _exampleModelElement ) {
            if ( arg == ExampleModelElement.PROPERTY_POSITION ) {
                updatePositionDisplay();
            }
            else if ( arg == ExampleModelElement.PROPERTY_ORIENTATION ) {
                updateOrientationControl();
            }
        }
    }
    
    public void updatePositionDisplay() {
        Point2D p = _exampleModelElement.getPositionReference();
        String s = "position: (" + (int)p.getX() + "," + (int)p.getY() + ")";
        _positionDisplay.setText( s );
    }
    
    public void updateOrientationControl() {
        final double degrees = Math.toDegrees( _exampleModelElement.getOrientation() );
        _orientationControl.setValue( degrees );
    }
}
