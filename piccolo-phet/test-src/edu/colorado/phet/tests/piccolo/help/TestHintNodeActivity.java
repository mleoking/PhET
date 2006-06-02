package edu.colorado.phet.tests.piccolo.help;

import edu.colorado.phet.piccolo.help.HintNode;
import edu.umd.cs.piccolox.activities.PPositionPathActivity;

import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

public class TestHintNodeActivity extends TestHintNode {

    public TestHintNodeActivity() {
        testSuperActivity( super.getHintNode() );
    }

    public static void testSuperActivity( final HintNode hintNode ) {
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( 300, 300 );
        path.lineTo( 300, 0 );
        path.append( new Arc2D.Float( 0, 0, 300, 300, 90, -90, Arc2D.OPEN ), true );
        path.closePath();

        // create activity to run animation.
        PPositionPathActivity positionPathActivity = new PPositionPathActivity( 5000, 0, new PPositionPathActivity.Target() {
            public void setPosition( double x, double y ) {
                hintNode.setOffset( x, y );
            }
        } );
        positionPathActivity.setSlowInSlowOut( true );
        positionPathActivity.setPositions( path );
        positionPathActivity.setLoopCount( Integer.MAX_VALUE );
        hintNode.setActivity( positionPathActivity );
    }

    public static void main( String[] args ) {
        new TestHintNodeActivity().start();
    }

}
