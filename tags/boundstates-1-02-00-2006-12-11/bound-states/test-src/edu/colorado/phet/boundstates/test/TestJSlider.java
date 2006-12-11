/* Copyright 2006, University of Colorado */

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
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class TestJSlider extends JFrame {

    private JSlider _slider;
    
    public TestJSlider() {

        _slider = new JSlider();
        _slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleSliderChange();
            }
        } );

        JPanel panel = new JPanel();
        panel.add( _slider );

        getContentPane().add( panel );
        pack();
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }

    public void handleSliderChange() {
        if ( !_slider.getValueIsAdjusting() ) {
            System.out.println( _slider.getValue() );
        }
    }
    
    public static void main( String[] args ) {
        TestJSlider frame = new TestJSlider();
        frame.show();
    }

}
