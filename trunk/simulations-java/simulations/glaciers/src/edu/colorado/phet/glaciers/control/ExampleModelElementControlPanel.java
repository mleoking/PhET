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

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ExampleModelElement _exampleModelElement;
    
    private LinearValueControl _widthControl, _heightControl;
    private JLabel _positionDisplay;
    private LinearValueControl _orientationControl;
    
    private ControlListener _controlListener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleModelElementControlPanel( Font titleFont, Font controlFont, ExampleModelElement exampleModelElement ) {
        super();
        
        _exampleModelElement = exampleModelElement;
        _exampleModelElement.addObserver( this );
        
        // Title
        JLabel titleLabel = new JLabel( "Example Model Element" );
        titleLabel.setFont( titleFont );
        
        _controlListener = new ControlListener();
        
        // Width
        double value = _exampleModelElement.getWidth();
        double min = 10;
        double max = 400;
        String label = "width:";
        String valuePattern = "##0";
        String units = "";
        _widthControl = new LinearValueControl( min, max, label, valuePattern, units );
        _widthControl.setValue( value );
        _widthControl.setTextFieldEditable( true );
        _widthControl.setFont( controlFont );
        _widthControl.setUpDownArrowDelta( 1 );
        _widthControl.setTickPattern( "0" );
        _widthControl.setMajorTickSpacing( 90 );
        _widthControl.setMinorTickSpacing( 45 );
        _widthControl.addChangeListener( _controlListener );
        
        // Height
        value = _exampleModelElement.getHeight();
        min = 10;
        max = 400;
        label = "height:";
        valuePattern = "##0";
        units = "";
        _heightControl = new LinearValueControl( min, max, label, valuePattern, units );
        _heightControl.setValue( value );
        _heightControl.setTextFieldEditable( true );
        _heightControl.setFont( controlFont );
        _heightControl.setUpDownArrowDelta( 1 );
        _heightControl.setTickPattern( "0" );
        _heightControl.setMajorTickSpacing( 90 );
        _heightControl.setMinorTickSpacing( 45 );
        _heightControl.addChangeListener( _controlListener );
        
        // Position display
        _positionDisplay = new JLabel();
        _positionDisplay.setFont( controlFont );
        updatePositionDisplay();
        
        // Orientation control
        value = _exampleModelElement.getOrientation();
        min = 0;
        max = 360;
        label = "orientation:";
        valuePattern = "##0";
        units = "degrees";
        _orientationControl = new LinearValueControl( min, max, label, valuePattern, units );
        _orientationControl.setValue( value );
        _orientationControl.setTextFieldEditable( true );
        _orientationControl.setFont( controlFont );
        _orientationControl.setUpDownArrowDelta( 1 );
        _orientationControl.setTickPattern( "0" );
        _orientationControl.setMajorTickSpacing( 90 );
        _orientationControl.setMinorTickSpacing( 45 );
        _orientationControl.addChangeListener( _controlListener );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        int row = 0;
        int column = 0;
        layout.addComponent( titleLabel, row++, column );
        layout.addComponent( _widthControl, row++, column );
        layout.addComponent( _heightControl, row++, column );
        layout.addComponent( _positionDisplay, row++, column );
        layout.addComponent( _orientationControl, row++, column );
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _exampleModelElement.deleteObserver( this );
    }

    //----------------------------------------------------------------------------
    // Model updaters
    //----------------------------------------------------------------------------
    
    private class ControlListener implements ChangeListener {
        public void stateChanged( ChangeEvent e ) {
            Object o = e.getSource();
            if ( o == _widthControl || o == _heightControl ) {
                updateModelSize();
            }
            else if ( o == _orientationControl ) {
                updateModelOrientation();
            }
        }
    }
    
    private void updateModelSize() {
        final double width = _widthControl.getValue();
        final double height = _heightControl.getValue();
        _exampleModelElement.deleteObserver( this );
        _exampleModelElement.setSize( width, height );
        _exampleModelElement.addObserver( this );
    }
    
    /**
     * Updates the model when the orientation control changes.
     */
    private void updateModelOrientation() {
        final double radians = Math.toRadians( _orientationControl.getValue() );
        _exampleModelElement.deleteObserver( this );
        _exampleModelElement.setOrientation( radians );
        _exampleModelElement.addObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the controls when the model changes.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _exampleModelElement ) {
            if ( arg == ExampleModelElement.PROPERTY_SIZE ) {
                updateSizeControl();
            }
            else if ( arg == ExampleModelElement.PROPERTY_POSITION ) {
                updatePositionDisplay();
            }
            else if ( arg == ExampleModelElement.PROPERTY_ORIENTATION ) {
                updateOrientationControl();
            }
        }
    }
    
    public void updateSizeControl() {
        
        _widthControl.removeChangeListener( _controlListener );
        _widthControl.setValue( _exampleModelElement.getWidth() );
        _widthControl.addChangeListener( _controlListener );
        
        _heightControl.removeChangeListener( _controlListener );
        _heightControl.setValue( _exampleModelElement.getHeight() );
        _heightControl.addChangeListener( _controlListener );
    }
    
    public void updatePositionDisplay() {
        Point2D p = _exampleModelElement.getPositionReference();
        String s = "position: (" + (int)p.getX() + "," + (int)p.getY() + ")";
        _positionDisplay.setText( s );
    }
    
    public void updateOrientationControl() {
        final double degrees = Math.toDegrees( _exampleModelElement.getOrientation() );
        _orientationControl.removeChangeListener( _controlListener );
        _orientationControl.setValue( degrees );
        _orientationControl.addChangeListener( _controlListener );
    }
}
