// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.test;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.opticaltweezers.control.SimulationSpeedSlider;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * TestSimulationSpeedSlider is a test application for SimulationSpeedSlider.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestSimulationSpeedSlider extends JFrame {
    
    private static final Dimension FRAME_SIZE = new Dimension( 500, 300 );
    
    private static final DoubleRange SLOW_RANGE = new DoubleRange( 1E-10, 1E-5, 1E-10 );
    private static final DoubleRange FAST_RANGE = new DoubleRange( 1E-3, 1E-1, 1E-3 );
    private static final double DEFAULT_VALUE = 1E-2;
    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( "0.0E00" );

    private SimulationSpeedSlider _slider;
    private PText _valueNode;
    
    public TestSimulationSpeedSlider() {
        super();
        
        _slider = new SimulationSpeedSlider( SLOW_RANGE, FAST_RANGE, DEFAULT_VALUE );
        _slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateValue();
            }
        } );
        System.out.println( _slider.getValue() );//XXX
        
        _valueNode = new PText();
        _valueNode.setFont( new PhetFont( 24 ) );
        _valueNode.setTextPaint( Color.BLACK );
        
        PCanvas canvas = new PhetPCanvas();
        canvas.getLayer().addChild( _slider );
        canvas.getLayer().addChild( _valueNode );
        
        _slider.setOffset( 50, 100 );
        _valueNode.setOffset( 50, 30 );
        
        updateValue();
        
        setContentPane( canvas );
        setSize( FRAME_SIZE );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    private void updateValue() {
        double value = _slider.getValue();
        String text = VALUE_FORMAT.format(  value );
//        System.out.println( "TestSimulationSpeedSlider.stateChanged value=" + text + " adjusting=" + _simulationSpeedControl.isAdjusting() );
        _valueNode.setText( text );
    }
    
    public static void main( String[] args ) {
        JFrame testFrame = new TestSimulationSpeedSlider();
        testFrame.show();
    }
}
