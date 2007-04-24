package edu.colorado.phet.energyskatepark.test;

import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.model.SPoint2D;
import edu.colorado.phet.energyskatepark.model.physics.CubicSpline2D;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSplineEnvironment;
import edu.colorado.phet.energyskatepark.view.SplineMatch;
import edu.colorado.phet.energyskatepark.view.SplineNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Author: Sam Reid
 * Mar 16, 2007, 12:41:42 PM
 */
public class TestSplineNode {
    private JFrame frame;

    public TestSplineNode() {
        PhetPCanvas phetPCanvas = new PhetPCanvas( new Rectangle2D.Double( 0, 0, 10, 10 ) );
        EnergySkateParkSpline spline = new EnergySkateParkSpline( new SPoint2D[]{
                new SPoint2D( 0, 0 ),
                new SPoint2D( 5, 5 )}  );
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
