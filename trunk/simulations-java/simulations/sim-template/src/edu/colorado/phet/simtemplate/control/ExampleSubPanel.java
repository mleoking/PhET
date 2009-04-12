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
import edu.colorado.phet.simtemplate.SimTemplateStrings;

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
        JLabel titleLabel = new JLabel( SimTemplateStrings.TITLE_EXAMPLE_CONTROL_PANEL );
        
        // Position display
        _positionDisplay = new JLabel();
        
        // Orientation control
        double min = 0;
        double max = 360;
        String label = SimTemplateStrings.LABEL_ORIENTATION;
        String valuePattern = "##0";
        String units = SimTemplateStrings.UNITS_ORIENTATION;
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
        layout.addComponent( _positionDisplay, row++, column );
        layout.addComponent( _orientationControl, row++, column );
    }
    
    public void cleanup() {}
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getOrientation() {
        return _orientationControl.getValue();
    }
    
    public void setOrientation( double orientation ) {
        if ( orientation != getOrientation() ) {
            _orientationControl.setValue( orientation );
        }
    }
    
    public void setPosition( Point2D p ) {
        String s = SimTemplateStrings.LABEL_POSITION + " (" + (int) p.getX() + "," + (int) p.getY() + ")";
        _positionDisplay.setText( s );
    }
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
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
        public void orientationChanged();
    }
    
    public static class ExampleSubPanelAdapter implements ExampleSubPanelListener {
        public void orientationChanged() {}
    }
    
    public void addExampleSubPanelListener( ExampleSubPanelListener listener ) {
        _listeners.add( listener );
    }
   
    public void removeExampleSubPanelListener( ExampleSubPanelListener listener ) {
        _listeners.remove( listener );
    }
}
