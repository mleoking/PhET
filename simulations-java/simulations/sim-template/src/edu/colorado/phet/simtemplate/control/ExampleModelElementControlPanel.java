/* Copyright 2007, University of Colorado */

package edu.colorado.phet.simtemplate.control;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.geom.Point2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.simtemplate.TemplateResources;
import edu.colorado.phet.simtemplate.model.ExampleModelElement;
import edu.colorado.phet.simtemplate.model.ExampleModelElement.ExampleModelElementListener;

/**
 * ExampleControlPanel is the control panel for an ExampleModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModelElementControlPanel extends JPanel implements ExampleModelElementListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = TemplateResources.getString( "title.exampleModelElement");
    private static final String LABEL_WIDTH = TemplateResources.getString( "label.width");
    private static final String LABEL_HEIGHT = TemplateResources.getString( "label.height");
    private static final String LABEL_POSITION = TemplateResources.getString( "label.position");
    private static final String LABEL_ORIENTATION = TemplateResources.getString( "label.orientation");
    private static final String UNITS_DISTANCE = TemplateResources.getString( "units.distance");
    private static final String UNITS_ORIENTATION = TemplateResources.getString( "units.orientation");
    
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
    
    public ExampleModelElementControlPanel( ExampleModelElement modelElement ) {
        super();
        
        _controlObserver = new ControlObserver();
        
        _modelElement = modelElement;
        _modelElement.addListener( this );
        
        // Title
        JLabel titleLabel = new JLabel( TITLE );
        
        // Width
        double min = 10;
        double max = 400;
        String label = LABEL_WIDTH;
        String valuePattern = "##0";
        String units = UNITS_DISTANCE;
        _widthControl = new LinearValueControl( min, max, label, valuePattern, units );
        _widthControl.setTextFieldEditable( true );
        _widthControl.setUpDownArrowDelta( 1 );
        _widthControl.setTickPattern( "0" );
        _widthControl.setMajorTickSpacing( max - min );
        _widthControl.setMinorTickSpacing( 10 );
        _widthControl.addChangeListener( _controlObserver );
        
        // Height
        min = 10;
        max = 400;
        label = LABEL_HEIGHT;
        valuePattern = "##0";
        units = UNITS_DISTANCE;
        _heightControl = new LinearValueControl( min, max, label, valuePattern, units );
        _heightControl.setTextFieldEditable( true );
        _heightControl.setUpDownArrowDelta( 1 );
        _heightControl.setTickPattern( "0" );
        _heightControl.setMajorTickSpacing( max - min );
        _heightControl.setMinorTickSpacing( 10 );
        _heightControl.addChangeListener( _controlObserver );
        
        // Position display
        _positionDisplay = new JLabel();
        
        // Orientation control
        min = 0;
        max = 360;
        label = LABEL_ORIENTATION;
        valuePattern = "##0";
        units = UNITS_ORIENTATION;
        _orientationControl = new LinearValueControl( min, max, label, valuePattern, units );
        _orientationControl.setTextFieldEditable( true );
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
        _orientationControl.removeChangeListener( _controlObserver );
        _orientationControl.setValue( degrees );
        _orientationControl.addChangeListener( _controlObserver );
	}

	public void positionChanged() {
        Point2D p = _modelElement.getPositionReference();
        String s = LABEL_POSITION + " (" + (int) p.getX() + "," + (int) p.getY() + ")";
        _positionDisplay.setText( s );
	}

	public void sizeChanged() {
        Dimension size = _modelElement.getSizeReference();
        
        _widthControl.removeChangeListener( _controlObserver );
        _widthControl.setValue( size.getWidth() );
        _widthControl.addChangeListener( _controlObserver );
        
        _heightControl.removeChangeListener( _controlObserver );
        _heightControl.setValue( size.getHeight() );
        _heightControl.addChangeListener( _controlObserver );
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
