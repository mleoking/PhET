/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.test;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
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

    private SimulationSpeedSlider _simulationSpeedControl;
    private PText _valueDisplayNode;
    
    public TestSimulationSpeedSlider() {
        super();
        
        _simulationSpeedControl = new SimulationSpeedSlider( SLOW_RANGE, FAST_RANGE, DEFAULT_VALUE );
        _simulationSpeedControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                String text = _simulationSpeedControl.getFormattedValue();
//                System.out.println( "TestSimulationSpeedSlider.stateChanged value=" + text + " adjusting=" + _simulationSpeedControl.isAdjusting() );
                _valueDisplayNode.setText( text );
            }
        } );
        
        _valueDisplayNode = new PText();
        _valueDisplayNode.setFont( new PhetDefaultFont( 24 ) );
        _valueDisplayNode.setTextPaint( Color.BLACK );
        _valueDisplayNode.setText( _simulationSpeedControl.getFormattedValue() );
        
        PCanvas canvas = new PhetPCanvas();
        canvas.getLayer().addChild( _simulationSpeedControl );
        canvas.getLayer().addChild( _valueDisplayNode );
        
        _simulationSpeedControl.setOffset( 50, 100 );
        _valueDisplayNode.setOffset( 50, 30 );
        
        setContentPane( canvas );
        setSize( FRAME_SIZE );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    public static void main( String[] args ) {
        JFrame testFrame = new TestSimulationSpeedSlider();
        testFrame.show();
    }
}
