package edu.colorado.phet.energyskatepark.test;

import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.model.physics.CubicSpline2D;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSplineEnvironment;
import edu.colorado.phet.energyskatepark.view.SplineMatch;
import edu.colorado.phet.energyskatepark.view.SplineNode;
import edu.colorado.phet.piccolo.PhetPCanvas;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Mar 16, 2007
 * Time: 12:41:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSplineNode {
    private JFrame frame;

    public TestSplineNode() {
        PhetPCanvas phetPCanvas = new PhetPCanvas(new Rectangle2D.Double(0,0,10,10));
        EnergySkateParkSpline spline = new EnergySkateParkSpline( new CubicSpline2D( new Point2D[]{
                new Point2D.Double( 0, 0 ),
                new Point2D.Double( 5, 5 )} ) );
        SplineNode splineNode = new SplineNode( phetPCanvas, spline, new EnergySkateParkSplineEnvironment() {
            public void removeSpline( SplineNode splineNode ) {
            }

            public SplineMatch proposeMatch( SplineNode splineNode, Point2D toMatch ) {
                return null;
            }

            public void splineTranslated( EnergySkateParkSpline splineSurface, double dx, double dy ) {
            }

            public void attach( SplineNode splineNode, int index, SplineMatch match ) {
            }
        } );
        phetPCanvas.addWorldChild( splineNode );

        frame = new JFrame();
        frame.setContentPane( phetPCanvas );
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public static void main( String[] args ) {
        new TestSplineNode().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
