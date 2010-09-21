/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Tests Stroke.createStrokedShape for a complex shape.
 * In this case, our shape is the thermometer from LiquidExpansionThermometerNode.
 * The resulting shape does not look at all correct.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestCreateStrokedShape extends JFrame {

    public TestCreateStrokedShape() {
        super( "TestCreateStrokedShape" );
        
        // shape of a simple thermometer
        final double tubeMinX = 25;
        final double tubeWidth = 25;
        final double tubeHeight = 100;
        final double bulbDiameter = 75;
        Shape tubeShape = new RoundRectangle2D.Double( tubeMinX, 0, tubeWidth, bulbDiameter + tubeHeight, tubeWidth, tubeWidth );
        Shape bulbShape = new Ellipse2D.Double( 0, tubeHeight, bulbDiameter, bulbDiameter );
        Area thermometerArea = new Area();
        thermometerArea.add( new Area( bulbShape ) );
        thermometerArea.add( new Area( tubeShape ) );
        
        // node the fills the thermometer shape
        PPath thermometerAreaNode = new PPath( thermometerArea );
        thermometerAreaNode.setPaint( Color.RED );
        thermometerAreaNode.setOffset( 50, 50 );
        
        // attempt to use a shape created by Stroke.createStrokedShape
        Stroke stroke = new BasicStroke( 10f );
        Shape thermometerStrokeShape = stroke.createStrokedShape( thermometerArea );
        PPath thermometerStrokeNode = new PPath( thermometerStrokeShape );
        thermometerStrokeNode.setPaint( Color.RED );
        thermometerStrokeNode.setOffset( thermometerAreaNode.getFullBoundsReference().getMaxX() + 50, thermometerAreaNode.getFullBoundsReference().getY() );
        
        // a simpler shape that demonstrates the same problem
        PPath rectStrokeNoke = new PPath( stroke.createStrokedShape( new Rectangle2D.Double( 0, 0, 100, 100 ) ) );
        rectStrokeNoke.setOffset( thermometerStrokeNode.getFullBoundsReference().getMaxX() + 50, thermometerStrokeNode.getFullBoundsReference().getY() );
        
        PCanvas canvas = new PhetPCanvas();
        canvas.getLayer().addChild( thermometerAreaNode );
        canvas.getLayer().addChild( thermometerStrokeNode );   
        canvas.getLayer().addChild( rectStrokeNoke );   
        
        getContentPane().add( canvas );
    }
    
    public static void main( String args[] ) {
        TestCreateStrokedShape frame = new TestCreateStrokedShape();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 600, 300 ) );
        frame.setVisible( true );
    }
}
