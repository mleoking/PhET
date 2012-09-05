// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
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

    public FadingLineNode( Vector2D origin, Vector2D endpoint, Color startColor, double fadeCoefficient, double lineThickness ) {
        addChild( new PhetPPath( new Line2D.Double( origin.toPoint2D(), endpoint.toPoint2D() ), new BasicStroke( (float) lineThickness ), startColor ) );
    }

    // Test harness.
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 400, 600 );

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( stageSize.getWidth() * 0.5 ), (int) Math.round( stageSize.getHeight() * 0.50 ) ),
                1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        PNode rootNode = new PNode();
        canvas.getLayer().addChild( rootNode );

        rootNode.addChild( new FadingLineNode( new Vector2D( 10, 10 ), new Vector2D( 100, 100 ), Color.ORANGE, 0, 2 ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }

}
