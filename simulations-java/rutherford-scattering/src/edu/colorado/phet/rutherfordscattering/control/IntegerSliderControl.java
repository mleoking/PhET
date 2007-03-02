/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.control;

import java.awt.Font;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.rutherfordscattering.RSConstants;


public class IntegerSliderControl extends JPanel {

    private TitledBorder _titledBorder;
    private JSlider _slider;
    
    public IntegerSliderControl( String label, int min, int max, int value ) {
        super();
        
        _titledBorder = new TitledBorder( label );
        _titledBorder.setTitleFont( RSConstants.CONTROL_FONT  );
        setBorder( _titledBorder );
        
        _slider = new JSlider();
        add( _slider );
        _slider.setFont( RSConstants.CONTROL_FONT );
        _slider.setMinimum( min );
        _slider.setMaximum( max );
        _slider.setValue( value );
        _slider.setMajorTickSpacing( max - min );
        _slider.setPaintTicks( true );
        _slider.setPaintLabels( true );
//        Hashtable labelTable = new Hashtable();
//        labelTable.put( new Integer( min ), new JLabel( String.valueOf( min ) ) );
//        labelTable.put( new Integer( max ), new JLabel( String.valueOf( max ) ) );
//        _slider.setLabelTable( labelTable );
    }
    
    public int getValue() {
        return _slider.getValue();
    }
    
    public void setTitleFont( Font font ) {
        _titledBorder.setTitleFont( font );
    }
    
    public void setSliderFont( Font font ) {
        _slider.setFont( font );
    }
    
    protected JSlider getSlider() {
        return _slider;
    }
    
    public void addChangeListener( ChangeListener listener ) {
        _slider.addChangeListener( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        _slider.removeChangeListener( listener );
    }
}
