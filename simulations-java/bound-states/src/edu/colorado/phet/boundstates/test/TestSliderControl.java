/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.test;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.control.SliderControl;


public class TestSliderControl extends JFrame {

    public TestSliderControl() {
        super();
  
        double value = 0;
        double min = -15;
        double max = 5;
        double tickSpacing = Math.abs( max - min );
        int tickDecimalPlaces = 1;
        int valueDecimalPlaces = 1;
        String valueLabel = "energy:";
        String unitsLabel = "eV";
        int columns = 4;
        final SliderControl slider = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, valueLabel, unitsLabel, columns );
        slider.setTextEditable( true );
        slider.addChangeListener( new ChangeListener() { 
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "value=" + slider.getValue() );
            }
        } );
        JPanel panel = new JPanel();
        panel.add( slider );
        
        getContentPane().add( panel );
        pack();
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }
    
    public static void main( String[] args ) {
        TestSliderControl frame = new TestSliderControl();
        frame.show();
    }
}
