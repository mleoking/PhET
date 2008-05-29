/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * TestThermometerArea tests the constructive area geometry used to form the shape for a thermometer.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestThermometerArea extends JFrame {
    
    private static final PDimension THERMOMETER_SIZE = new PDimension( 50, 200 );
    
    // derived constants
    private static final double BULB_DIAMETER = THERMOMETER_SIZE.getWidth();
    private static final double TUBE_WIDTH = 0.5 * THERMOMETER_SIZE.getWidth();
    private static final double TUBE_HEIGHT = THERMOMETER_SIZE.getHeight() - BULB_DIAMETER;
    
    public TestThermometerArea() {
        super( "TestThermometerArea" );
        
        PCanvas canvas = new PCanvas();
        getContentPane().add( canvas );
        
        // take the union of a circle and a rounded rectangle
        final double tubeMinX = ( THERMOMETER_SIZE.getWidth() - TUBE_WIDTH ) / 2;
        Shape tubeShape = new RoundRectangle2D.Double( tubeMinX, 0, TUBE_WIDTH, THERMOMETER_SIZE.getHeight(), TUBE_WIDTH, TUBE_WIDTH );
        Shape bulbShape = new Ellipse2D.Double( 0, TUBE_HEIGHT, BULB_DIAMETER, BULB_DIAMETER );
        Area area = new Area();
        area.add( new Area( bulbShape ) );
        area.add( new Area( tubeShape ) );
        
        PPath pathNode = new PPath( area );
        pathNode.setStroke( new BasicStroke( 2f ) );
        pathNode.setStrokePaint( Color.BLACK );
        pathNode.setOffset( 100, 100 );
        canvas.getLayer().addChild( pathNode );
    }
    
    public static void main( String args[] ) {
        TestThermometerArea frame = new TestThermometerArea();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 400, 400 ) );
        frame.setVisible( true );
    }
}
