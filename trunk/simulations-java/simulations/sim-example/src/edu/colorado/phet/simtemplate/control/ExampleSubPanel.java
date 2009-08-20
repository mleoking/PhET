/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.simtemplate.control;

import java.awt.GridBagConstraints;
import java.awt.geom.Point2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.simtemplate.SimTemplateStrings;
import edu.colorado.phet.simtemplate.model.ExampleModelElement;
import edu.colorado.phet.simtemplate.model.ExampleModelElement.ExampleModelElementListener;

/**
 * ExampleSubPanel is an example of a control panel that implements a subset
 * of the controls in the main control panel.
 * Notice that the panel has no knowledge of any model elements.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleSubPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JLabel positionDisplay;
    private LinearValueControl orientationControl; // in degrees
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleSubPanel( final ExampleModelElement modelElement ) {
        super();
        
        // Title
        JLabel titleLabel = new JLabel( SimTemplateStrings.TITLE_EXAMPLE_CONTROL_PANEL );
        
        // Position display
        positionDisplay = new JLabel();
        
        // Orientation control
        double min = 0;
        double max = 360;
        String label = SimTemplateStrings.LABEL_ORIENTATION;
        String valuePattern = "##0";
        String units = SimTemplateStrings.UNITS_ORIENTATION;
        orientationControl = new LinearValueControl( min, max, label, valuePattern, units );
        orientationControl.setValue( Math.toDegrees( modelElement.getOrientation() ) );
        orientationControl.setTextFieldEditable( true );
        orientationControl.setUpDownArrowDelta( 1 );
        orientationControl.setTickPattern( "0" );
        orientationControl.setMajorTickSpacing( 90 );
        orientationControl.setMinorTickSpacing( 45 );
        orientationControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                modelElement.setOrientation( Math.toRadians( orientationControl.getValue() ) );
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
        layout.addComponent( positionDisplay, row++, column );
        layout.addComponent( orientationControl, row++, column );
        
        modelElement.addExampleModelElementListener( new ExampleModelElementListener() {

            public void orientationChanged() {
                setOrientation( Math.toDegrees( modelElement.getOrientation() ) );
            }

            public void positionChanged() {
                setPosition( modelElement.getPositionReference() );
            }
        });
    }
    
    public void cleanup() {}
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    private void setOrientation( double degrees ) {
        orientationControl.setValue( degrees );
    }
    
    private void setPosition( Point2D p ) {
        String s = SimTemplateStrings.LABEL_POSITION + " (" + (int) p.getX() + "," + (int) p.getY() + ")";
        positionDisplay.setText( s );
    }
}
