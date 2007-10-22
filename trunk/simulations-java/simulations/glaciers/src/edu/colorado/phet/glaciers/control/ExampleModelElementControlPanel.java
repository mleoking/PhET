/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.geom.Point2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.glaciers.model.ExampleModelElement;
import edu.colorado.phet.glaciers.model.ExampleModelElement.ExampleModelElementListener;

/**
 * ExampleControlPanel is the control panel for an ExampleModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModelElementControlPanel extends JPanel implements ExampleModelElementListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
	private ExampleModelElement _modelElement;
	
    private LinearValueControl _widthControl, _heightControl;
    private JLabel _positionDisplay;
    private LinearValueControl _orientationControl;
    
    private ControlObserver _controlObserver;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleModelElementControlPanel( Font titleFont, Font controlFont, ExampleModelElement modelElement ) {
        super();
        
        _controlObserver = new ControlObserver();
        
        _modelElement = modelElement;
        _modelElement.addListener( this );
        
        // Title
        JLabel titleLabel = new JLabel( "Example Model Element" );
        titleLabel.setFont( titleFont );
        
        // Width
        double min = 10;
        double max = 400;
        String label = "width:";
        String valuePattern = "##0";
        String units = "";
        _widthControl = new LinearValueControl( min, max, label, valuePattern, units );
        _widthControl.setTextFieldEditable( true );
        _widthControl.setFont( controlFont );
        _widthControl.setUpDownArrowDelta( 1 );
        _widthControl.setTickPattern( "0" );
        _widthControl.setMajorTickSpacing( max - min );
        _widthControl.setMinorTickSpacing( 10 );
        _widthControl.addChangeListener( _controlObserver );
        
        // Height
        min = 10;
        max = 400;
        label = "height:";
        valuePattern = "##0";
        units = "";
        _heightControl = new LinearValueControl( min, max, label, valuePattern, units );
        _heightControl.setTextFieldEditable( true );
        _heightControl.setFont( controlFont );
        _heightControl.setUpDownArrowDelta( 1 );
        _heightControl.setTickPattern( "0" );
        _heightControl.setMajorTickSpacing( max - min );
        _heightControl.setMinorTickSpacing( 10 );
        _heightControl.addChangeListener( _controlObserver );
        
        // Position display
        _positionDisplay = new JLabel();
        _positionDisplay.setFont( controlFont );
        
        // Orientation control
        min = 0;
        max = 360;
        label = "orientation:";
        valuePattern = "##0";
        units = "degrees";
        _orientationControl = new LinearValueControl( min, max, label, valuePattern, units );
        _orientationControl.setTextFieldEditable( true );
        _orientationControl.setFont( controlFont );
        _orientationControl.setUpDownArrowDelta( 1 );
        _orientationControl.setTickPattern( "0" );
        _orientationControl.setMajorTickSpacing( 90 );
        _orientationControl.setMinorTickSpacing( 45 );
        _orientationControl.addChangeListener( _controlObserver );
        
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
        
        sizeChanged();
        positionChanged();
        orientationChanged();
    }
    
    public void cleanup() {
        _modelElement.removeListener( this );
    }
    
    //----------------------------------------------------------------------------
    // ExampleModelElementListener
    //----------------------------------------------------------------------------
    
	public void orientationChanged() {
        final double degrees = Math.toDegrees( _modelElement.getOrientation() );
        _orientationControl.setValue( degrees );
	}

	public void positionChanged() {
        Point2D p = _modelElement.getPositionReference();
        String s = "position: (" + (int) p.getX() + "," + (int) p.getY() + ")";
        _positionDisplay.setText( s );
	}

	public void sizeChanged() {
        Dimension size = _modelElement.getSizeReference();
        _widthControl.setValue( size.getWidth() );
        _heightControl.setValue( size.getHeight() );
	}

    //----------------------------------------------------------------------------
    // Control changes
    //----------------------------------------------------------------------------
    
    private class ControlObserver implements ChangeListener {
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
        _modelElement.setSize( new Dimension( (int) width, (int) height ) );
    }
    
    /**
     * Updates the model when the orientation control changes.
     */
    private void updateModelOrientation() {
        final double radians = Math.toRadians( _orientationControl.getValue() );
        _modelElement.setOrientation( radians );
    }


}
