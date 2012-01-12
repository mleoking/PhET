// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.test;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;


public class TestArrow extends JFrame {

    private static final double HEAD_WIDTH = 20;
    private static final double HEAD_HEIGHT = 20;
    private static final double TAIL_WIDTH = 5;
    private static final double FRACTIONAL_HEAD_HEIGHT = 0.5;
    
    private PPath _arrowNode;
    private LinearValueControl _magnitudeControl;

    public TestArrow() {
        super();
        
        PCanvas canvas = new PCanvas();
        canvas.setPreferredSize( new Dimension( 400, 300 ) );
        
        _arrowNode = new PPath();
        _arrowNode.setPaint( Color.RED );
        _arrowNode.setStroke( new BasicStroke( 1f ) );
        _arrowNode.setStrokePaint( Color.BLACK );
        canvas.getLayer().addChild( _arrowNode );
        _arrowNode.setOffset( 50, 150 );
        
        _magnitudeControl = new LinearValueControl( 0, 100, "magnitude:", "##0.0", "" );
        _magnitudeControl.setUpDownArrowDelta( 1 );
        _magnitudeControl.setValue( 50 );
        _magnitudeControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                updateArrow();
            }
        });
        
        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );
        panel.add( _magnitudeControl, BorderLayout.SOUTH );
        
        setContentPane( panel );
        pack();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        updateArrow();
    }
    
    private void updateArrow() {
        double magnitude = _magnitudeControl.getValue();
        Point2D tailPosition = new Point2D.Double( 0, 0 );
        Point2D tipPosition = new Point2D.Double( magnitude, 0 );
        Arrow arrow = new Arrow( tailPosition, tipPosition, HEAD_HEIGHT, HEAD_WIDTH, TAIL_WIDTH, FRACTIONAL_HEAD_HEIGHT, true /* scaleTailToo */ );
        _arrowNode.setPathTo( arrow.getShape() );
    }

    public static void main( String[] args ) {
        TestArrow test = new TestArrow();
        test.show();
    }

}
