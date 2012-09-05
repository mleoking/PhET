// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class the depicts a line that starts at a point in space and then fades
 * out.  This was originally created to represent rays of light emanating from
 * a light source, but it may have other uses.
 *
 * @author John Blanco
 */
public class FadingLineNode extends PNode {

    // TODO: I don't like the fade rate parameter, because it's in units of absolute amount (out of 255) per unit distance, which seems weird.
    public FadingLineNode( Vector2D origin, Vector2D endpoint, Color startColor, double fadeRate, double lineThickness ) {
        assert fadeRate >= 0; // Can't have negative fade.
        double zeroIntensityDistance = (double) startColor.getAlpha() / fadeRate;
        Vector2D zeroIntensityPoint = new Vector2D( zeroIntensityDistance, 0 ).getRotatedInstance( endpoint.minus( origin ).getAngle() );

        Color fullyFadedColor = new Color( startColor.getRed(), startColor.getGreen(), startColor.getBlue(), 0 );
        Paint gradientPaint = new GradientPaint( (float) origin.x, (float) origin.y, startColor, (float) zeroIntensityPoint.x, (float) zeroIntensityPoint.y, fullyFadedColor );
        addChild( new PhetPPath( new Line2D.Double( origin.toPoint2D(), endpoint.toPoint2D() ), new BasicStroke( (float) lineThickness ), gradientPaint ) );
    }

    // Test harness.
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 400, 600 );

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        PNode rootNode = new PNode();
        canvas.getLayer().addChild( rootNode );

        rootNode.addChild( new FadingLineNode( new Vector2D( 10, 10 ), new Vector2D( 100, 100 ), Color.ORANGE, 10, 2 ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }

}
