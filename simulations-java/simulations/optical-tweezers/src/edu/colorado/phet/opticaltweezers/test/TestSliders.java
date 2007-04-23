/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.test;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.opticaltweezers.control.slider.LinearSlider;
import edu.colorado.phet.opticaltweezers.control.slider.LogarithmicSlider;


public class TestSliders extends JFrame {

    private static final Dimension FRAME_SIZE = new Dimension( 500, 500 );
    
    public TestSliders() {
        
        final LinearSlider linearSlider = new LinearSlider( -1, 1 );
        linearSlider.addChangeListener( new ChangeListener() {
           public void stateChanged( ChangeEvent event ) {
               System.out.println( "TestSliders.stateChanged: " + linearSlider.getModelValue() );
           }
        });
        
        final LogarithmicSlider logSlider = new LogarithmicSlider( 1E-6, 1E-2 );
        logSlider.addChangeListener( new ChangeListener() {
           public void stateChanged( ChangeEvent event ) {
               System.out.println( "TestSliders.stateChanged: " + logSlider.getModelValue() );
           }
        });
        
        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout( panel, BoxLayout.Y_AXIS );
        panel.setLayout( layout );
        panel.add( linearSlider );
        panel.add( logSlider );
        
        setContentPane( panel );
        setSize( FRAME_SIZE );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    public static void main( String[] args ) {
        TestSliders test = new TestSliders();
        test.show();
    }
}
