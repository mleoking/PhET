/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.simtemplate.control;

import java.awt.GridBagConstraints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.simtemplate.TemplateStrings;

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
    
    private LinearValueControl _widthControl, _heightControl;
    private JLabel _positionDisplay;
    private LinearValueControl _orientationControl; // in degrees
    private ArrayList _listeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleSubPanel() {
        super();
        
        _listeners = new ArrayList();
        
        // Title
        JLabel titleLabel = new JLabel( TemplateStrings.TITLE_EXAMPLE_CONTROL_PANEL );
        
        // Width
        double min = 10;
        double max = 400;
        String label = TemplateStrings.LABEL_WIDTH;
        String valuePattern = "##0";
        String units = TemplateStrings.UNITS_DISTANCE;
        _widthControl = new LinearValueControl( min, max, label, valuePattern, units );
        _widthControl.setTextFieldEditable( true );
        _widthControl.setUpDownArrowDelta( 1 );
        _widthControl.setTickPattern( "0" );
        _widthControl.setMajorTickSpacing( max - min );
        _widthControl.setMinorTickSpacing( 10 );
        _widthControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyWidthChanged();
            }
        } );
        
        // Height
        min = 10;
        max = 400;
        label = TemplateStrings.LABEL_HEIGHT;
        valuePattern = "##0";
        units = TemplateStrings.UNITS_DISTANCE;
        _heightControl = new LinearValueControl( min, max, label, valuePattern, units );
        _heightControl.setTextFieldEditable( true );
        _heightControl.setUpDownArrowDelta( 1 );
        _heightControl.setTickPattern( "0" );
        _heightControl.setMajorTickSpacing( max - min );
        _heightControl.setMinorTickSpacing( 10 );
        _heightControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyHeightChanged();
            }
        } );
        
        // Position display
        _positionDisplay = new JLabel();
        
        // Orientation control
        min = 0;
        max = 360;
        label = TemplateStrings.LABEL_ORIENTATION;
        valuePattern = "##0";
        units = TemplateStrings.UNITS_ORIENTATION;
        _orientationControl = new LinearValueControl( min, max, label, valuePattern, units );
        _orientationControl.setTextFieldEditable( true );
        _orientationControl.setUpDownArrowDelta( 1 );
        _orientationControl.setTickPattern( "0" );
        _orientationControl.setMajorTickSpacing( 90 );
        _orientationControl.setMinorTickSpacing( 45 );
        _orientationControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyOrientationChanged();
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
        layout.addComponent( _widthControl, row++, column );
        layout.addComponent( _heightControl, row++, column );
        layout.addComponent( _positionDisplay, row++, column );
        layout.addComponent( _orientationControl, row++, column );
    }
    
    public void cleanup() {}
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getWidthValue() {
        return _widthControl.getValue();
    }
    
    public void setWidthValue( double width ) {
        if ( width != getWidthValue() ) {
            _widthControl.setValue( width );
        }
    }
    
    public double getHeightValue() {
        return _heightControl.getValue();
    }
    
    public void setHeightValue( double height ) {
        if ( height != getHeightValue() ) {
            _heightControl.setValue( height );
        }
    }
    
    public double getOrientationValue() {
        return _orientationControl.getValue();
    }
    
    public void setOrientationValue( double orientation ) {
        if ( orientation != getOrientationValue() ) {
            _orientationControl.setValue( orientation );
        }
    }
    
    public void setPosition( Point2D p ) {
        String s = TemplateStrings.LABEL_POSITION + " (" + (int) p.getX() + "," + (int) p.getY() + ")";
        _positionDisplay.setText( s );
    }
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
    private void notifyWidthChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ExampleSubPanelListener) i.next() ).widthChanged();
        }
    }
    
    private void notifyHeightChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ExampleSubPanelListener) i.next() ).heightChanged();
        }
    }
    
    private void notifyOrientationChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ExampleSubPanelListener) i.next() ).orientationChanged();
        }
    }
    
    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------
    
    public interface ExampleSubPanelListener {
        public void widthChanged();
        public void heightChanged();
        public void orientationChanged();
    }
    
    public static class ExampleSubPanelAdapter implements ExampleSubPanelListener {
        public void widthChanged() {};
        public void heightChanged() {};
        public void orientationChanged() {};
    }
    
    public void addExampleSubPanelListener( ExampleSubPanelListener listener ) {
        _listeners.add( listener );
    }
   
    public void removeExampleSubPanelListener( ExampleSubPanelListener listener ) {
        _listeners.remove( listener );
    }
}
