// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.test;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * TestThermometerArea tests the constructive area geometry used to form the shape for a thermometer.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestThermometerArea extends JFrame {
    
    // dimensions of the thermometer, not accounting for stroke width
    private static final PDimension THERMOMETER_SIZE = new PDimension( 50, 200 );
    private static final int DEFAULT_STROKE_WIDTH = 5;
    
    // derived constants
    private static final double BULB_DIAMETER = THERMOMETER_SIZE.getWidth();
    private static final double TUBE_WIDTH = 0.5 * THERMOMETER_SIZE.getWidth();
    private static final double TUBE_HEIGHT = THERMOMETER_SIZE.getHeight() - BULB_DIAMETER;
    private PPath pathNode;
    private JLabel valueDisplay;

    public TestThermometerArea() {
        super( "TestThermometerArea" );
        
        // Take the union of a circle and a rounded rectangle.
        // Origin is at upper-left corner of the area's bounding rectangle.
        final double tubeMinX = ( THERMOMETER_SIZE.getWidth() - TUBE_WIDTH ) / 2;
        Shape tubeShape = new RoundRectangle2D.Double( tubeMinX, 0, TUBE_WIDTH, THERMOMETER_SIZE.getHeight(), TUBE_WIDTH, TUBE_WIDTH );
        Shape bulbShape = new Ellipse2D.Double( 0, TUBE_HEIGHT, BULB_DIAMETER, BULB_DIAMETER );
        Area area = new Area( tubeShape );
        area.add( new Area( bulbShape ) );
        
        // Draw the outline of the area
        pathNode = new PPath( area );

        pathNode.setStrokePaint( Color.BLACK );
        pathNode.setOffset( 150, 50 );
        
        PCanvas canvas = new PCanvas();
        canvas.getLayer().addChild( pathNode );
        
        // slider control for changing stroke width
        JLabel label = new JLabel( "stroke width:" );
        valueDisplay = new JLabel( String.valueOf( DEFAULT_STROKE_WIDTH ) );
        final JSlider slider = new JSlider( 1, 10 );
        slider.setValue( DEFAULT_STROKE_WIDTH );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateStrokeWidth( slider.getValue() );
            }
        } );

        updateStrokeWidth( DEFAULT_STROKE_WIDTH );
        
        JPanel controlPanel = new JPanel();
        controlPanel.add( label );
        controlPanel.add( slider );
        controlPanel.add( valueDisplay );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( controlPanel, BorderLayout.SOUTH );
        getContentPane().add( mainPanel );
    }

    private void updateStrokeWidth( int width ) {
        pathNode.setStroke( new BasicStroke( width , BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND) );
        valueDisplay.setText( String.valueOf( width ) );
    }

    public static void main( String args[] ) {
        TestThermometerArea frame = new TestThermometerArea();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 400, 400 ) );
        frame.setVisible( true );
    }
}
