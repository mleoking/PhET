/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * TestBarGraph tests the bar graph feature for ph-scale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestBarGraph extends JFrame {

    public TestBarGraph() {
        super( "TestBarGraph" );
        
        JPanel chartPanel = new JPanel();
        
        JLabel pHLabel = new JLabel( "pH:" );
        
        final JSlider pHSlider = new JSlider( -1, 15 );
        pHSlider.setValue( 7 );
        
        final JLabel pHValue = new JLabel( String.valueOf( pHSlider.getValue() ) );
        
        pHSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                pHValue.setText( String.valueOf( pHSlider.getValue() ) );
            }
        });
        
        JPanel controlPanel = new JPanel();
        EasyGridBagLayout controlPanelLayout = new EasyGridBagLayout( controlPanel );
        controlPanel.setLayout( controlPanelLayout );
        int row = 0;
        int column = 0;
        controlPanelLayout.addComponent( pHLabel, row, column++ );
        controlPanelLayout.addComponent( pHSlider, row, column++ );
        controlPanelLayout.addComponent( pHValue, row, column++ );
        
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( chartPanel, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.SOUTH );
        
        getContentPane().add( panel );
    }
    
    public static void main( String args[] ) {
        TestBarGraph frame = new TestBarGraph();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 1024, 768 ) );
        frame.setVisible( true );
    }
}
