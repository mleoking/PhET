/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.opticaltweezers.util.Vector2D;
import edu.colorado.phet.opticaltweezers.view.Vector2DNode;
import edu.umd.cs.piccolo.PCanvas;

/**
 * TestVector2DNode is a test application for Vector2DNode.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestVector2DNode extends JFrame {

    public TestVector2DNode() {
        super();
        
        final Vector2D vector = new Vector2D.Polar( 100, 0 );
        final double referenceMagnitude = 100;
        final double referenceLength = 100;
        final Vector2DNode vectorNode = new Vector2DNode( vector.getX(), vector.getY(), referenceMagnitude, referenceLength );
        vectorNode.setValueSpacing( 5 );

        PCanvas canvas = new PCanvas();
        canvas.setPreferredSize( new Dimension( 400, 300 ) );
        canvas.getLayer().addChild( vectorNode );
        vectorNode.setOffset( 200, 150 );
        
        final LinearValueControl magnitudeControl = new LinearValueControl( 0, 100, "magnitude:", "##0", "" );
        magnitudeControl.setUpDownArrowDelta( 1 );
        magnitudeControl.setValue( vector.getMagnitude() );
        magnitudeControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                double magnitude = magnitudeControl.getValue();
                vector.setMagnitude( magnitude );
                vectorNode.setXY( vector.getX(), vector.getY() );
            }
        });
        
        final LinearValueControl angleControl = new LinearValueControl( -720, 720, "angle:", "##0", "degrees" );
        angleControl.setUpDownArrowDelta( 1 );
        angleControl.setValue( vector.getAngle() );
        angleControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                double angle = Math.toRadians( angleControl.getValue() );
                vector.setAngle( angle );
                vectorNode.setXY( vector.getX(), vector.getY() );
            }
        });
        
        Box controlPanel = new Box( BoxLayout.Y_AXIS );
        controlPanel.add( new JSeparator() );
        controlPanel.add( magnitudeControl );
        controlPanel.add( new JSeparator() );
        controlPanel.add( angleControl );
        
        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.SOUTH );
        
        setContentPane( panel );
        pack();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    public static void main( String[] args ) {
        TestVector2DNode test = new TestVector2DNode();
        test.show();
    }
}
